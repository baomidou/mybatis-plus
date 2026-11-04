/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.aot;

import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationExcludeFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.aot.AbstractAotProcessor;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * mybatis-plus 的 native-image 支持：静态元数据（META-INF/native-image 下的 *-config.json）负责 MyBatis/MyBatis-Plus
 * 基础设施，本配置类只负责随项目变化的用户 Mapper 接口与 Mapper XML。
 *
 * @author xiaochen
 * @since 2025-08-20
 */
@ConditionalOnProperty(value = AbstractAotProcessor.AOT_PROCESSING, havingValue = "true")
@ConditionalOnClass(MapperFactoryBean.class)
@Configuration(proxyBeanMethods = false)
public final class MyBatisPlusNativeImageConfiguration {

    /**
     * 开启后注册 classpath 下全部 xml，默认只注册 mybatis-plus.mapper-locations 命中的 xml
     */
    public static final String REGISTER_ALL_XML_PROPERTY = "mybatis-plus.native.register-all-xml";

    /**
     * Mapper XML 位置，前缀与 MybatisPlusProperties 一致（Constants.MYBATIS_PLUS）
     */
    private static final String MAPPER_LOCATIONS_PROPERTY = "mybatis-plus.mapper-locations";

    /**
     * 未配置 mapper-locations 时的默认值，与 MybatisPlusProperties#mapperLocations 的默认值保持一致
     */
    private static final String DEFAULT_MAPPER_LOCATION = "classpath*:/mapper/**/*.xml";

    @Bean
    MyBatisBeanFactoryInitializationAotProcessor myBatisBeanFactoryInitializationAotProcessor() {
        return new MyBatisBeanFactoryInitializationAotProcessor();
    }

    @Bean
    static MyBatisMapperFactoryBeanPostProcessor myBatisMapperFactoryBeanPostProcessor() {
        return new MyBatisMapperFactoryBeanPostProcessor();
    }

    static class MyBatisBeanFactoryInitializationAotProcessor
            implements BeanFactoryInitializationAotProcessor, BeanRegistrationExcludeFilter, EnvironmentAware {

        private final Set<Class<?>> excludeClasses = new HashSet<>();

        private Environment environment;

        MyBatisBeanFactoryInitializationAotProcessor() {
            excludeClasses.add(MapperScannerConfigurer.class);
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        @Override
        public boolean isExcludedFromAotProcessing(RegisteredBean registeredBean) {
            return excludeClasses.contains(registeredBean.getBeanClass());
        }

        @Override
        public BeanFactoryInitializationAotContribution processAheadOfTime(
                ConfigurableListableBeanFactory beanFactory) {
            String[] beanNames = beanFactory.getBeanNamesForType(MapperFactoryBean.class);
            return (context, code) -> {
                RuntimeHints hints = context.getRuntimeHints();
                registerMapperXmlResources(hints, environment);
                for (String beanName : beanNames) {
                    String beanDefinitionName = beanName.startsWith("&") ? beanName.substring(1) : beanName;
                    BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
                    PropertyValue mapperInterface = beanDefinition.getPropertyValues()
                            .getPropertyValue("mapperInterface");
                    if (mapperInterface != null && mapperInterface.getValue() != null) {
                        Class<?> mapperInterfaceType = (Class<?>) mapperInterface.getValue();
                        if (mapperInterfaceType != null) {
                            registerReflectionTypeIfNecessary(mapperInterfaceType, hints);
                            hints.proxies().registerJdkProxy(mapperInterfaceType);
                            hints.proxies()
                                    .registerJdkProxy(AopProxyUtils.completeJdkProxyInterfaces(mapperInterfaceType));
                            registerMapperRelationships(mapperInterfaceType, hints);
                        }
                    }
                }
            };
        }

        /**
         * Mapper XML 的资源提示跟随 mybatis-plus.mapper-locations：用户把 XML 放在哪，注册就覆盖哪， 不再靠固定目录名猜。
         */
        static void registerMapperXmlResources(RuntimeHints hints, Environment environment) {
            if (isRegisterAllXml(environment)) {
                hints.resources().registerPattern("**/*.xml");
                hints.resources().registerPattern("*.xml");
                return;
            }
            for (String location : resolveMapperLocations(environment)) {
                for (String pattern : toResourcePatterns(location)) {
                    hints.resources().registerPattern(pattern);
                }
            }
        }

        static List<String> resolveMapperLocations(Environment environment) {
            if (environment == null) {
                return List.of(DEFAULT_MAPPER_LOCATION);
            }
            List<String> locations = Binder.get(environment).bind(MAPPER_LOCATIONS_PROPERTY, String[].class)
                    .map(Arrays::asList).orElse(null);
            return locations == null ? List.of(DEFAULT_MAPPER_LOCATION) : locations;
        }

        static boolean isRegisterAllXml(Environment environment) {
            if (Boolean.getBoolean(REGISTER_ALL_XML_PROPERTY)) {
                return true;
            }
            if (environment == null) {
                return false;
            }
            String value = environment.getProperty(REGISTER_ALL_XML_PROPERTY);
            return value != null && Boolean.parseBoolean(value.trim());
        }

        /**
         * 把 Spring 的 mapper-locations 转成 classpath 相对的资源 pattern。 Boot3 的 Spring 6.2 写 resource-config.json 时把 单星双星都转成
         * .* 且保留字面斜杠， 双星接斜杠的写法会漏掉 mapper/UserMapper.xml 这种单层布局；Boot4 的 Spring 7 原样透传 glob， 单星反而只匹配单层。两版语义相反，
         * 只能成对注册单星与双星，保证任意深度在两个版本下都被命中。
         */
        static List<String> toResourcePatterns(String location) {
            String pattern = toClasspathPattern(location);
            if (pattern == null) {
                return List.of();
            }
            Set<String> patterns = new LinkedHashSet<>();
            patterns.add(pattern);
            String flat = flattenWildcards(pattern);
            if (!flat.equals(pattern)) {
                patterns.add(flat);
            }
            return new ArrayList<>(patterns);
        }

        /**
         * 去掉双星段： Boot3 把单星转成跨目录的 .* 而双星后面的斜杠是字面量， 会漏掉单层布局； Boot4 的 glob 里单星只匹配单层。 两者各缺一半， 成对注册才覆盖所有深度。
         */
        static String flattenWildcards(String pattern) {
            List<String> segments = new ArrayList<>();
            for (String segment : pattern.split("/")) {
                if (!"**".equals(segment)) {
                    segments.add(segment);
                }
            }
            return String.join("/", segments);
        }

        /**
         * 只认 classpath 前缀， 其它协议（file:、http: 等）不是 classpath 资源， 无法转成 native-image 的资源 pattern。
         */
        static String toClasspathPattern(String location) {
            if (location == null) {
                return null;
            }
            String value = location.trim();
            if (value.startsWith("classpath*:")) {
                value = value.substring("classpath*:".length());
            } else if (value.startsWith("classpath:")) {
                value = value.substring("classpath:".length());
            } else if (value.contains(":")) {
                return null;
            }
            while (value.startsWith("/")) {
                value = value.substring(1);
            }
            return value.isEmpty() ? null : value;
        }

        private void registerMapperRelationships(Class<?> mapperInterfaceType, RuntimeHints hints) {
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(mapperInterfaceType);
            for (Method method : methods) {
                if (method.getDeclaringClass() != Object.class) {
                    ReflectionUtils.makeAccessible(method);
                    registerSqlProviderTypes(
                            method, hints, SelectProvider.class, SelectProvider::value, SelectProvider::type);
                    registerSqlProviderTypes(
                            method, hints, InsertProvider.class, InsertProvider::value, InsertProvider::type);
                    registerSqlProviderTypes(
                            method, hints, UpdateProvider.class, UpdateProvider::value, UpdateProvider::type);
                    registerSqlProviderTypes(
                            method, hints, DeleteProvider.class, DeleteProvider::value, DeleteProvider::type);
                    Class<?> returnType = MyBatisMapperTypeUtils.resolveReturnClass(mapperInterfaceType, method);
                    registerReflectionTypeIfNecessary(returnType, hints);
                    MyBatisMapperTypeUtils.resolveParameterClasses(mapperInterfaceType, method)
                            .forEach(x -> registerReflectionTypeIfNecessary(x, hints));
                }
            }
        }

        @SafeVarargs
        private <T extends Annotation> void registerSqlProviderTypes(Method method, RuntimeHints hints,
                Class<T> annotationType, Function<T, Class<?>>... providerTypeResolvers) {
            for (T annotation : method.getAnnotationsByType(annotationType)) {
                for (Function<T, Class<?>> providerTypeResolver : providerTypeResolvers) {
                    registerReflectionTypeIfNecessary(providerTypeResolver.apply(annotation), hints);
                }
            }
        }

        private void registerReflectionTypeIfNecessary(Class<?> type, RuntimeHints hints) {
            if (!type.isPrimitive() && !type.getName().startsWith("java")) {
                hints.reflection().registerType(type, MemberCategory.values());
            }
        }

    }

    static class MyBatisMapperTypeUtils {

        private MyBatisMapperTypeUtils() {
        }

        static Class<?> resolveReturnClass(Class<?> mapperInterface, Method method) {
            Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
            return typeToClass(resolvedReturnType, method.getReturnType());
        }

        static Set<Class<?>> resolveParameterClasses(Class<?> mapperInterface, Method method) {
            Set<Class<?>> set = new HashSet<>();
            for (Type x : TypeParameterResolver.resolveParamTypes(method, mapperInterface)) {
                Class<?> aClass = typeToClass(x, x instanceof Class ? (Class<?>) x : Object.class);
                set.add(aClass);
            }
            return set;
        }

        private static Class<?> typeToClass(Type src, Class<?> fallback) {
            Class<?> result = null;
            if (src instanceof Class<?> cls) {
                if (cls.isArray()) {
                    result = cls.getComponentType();
                } else {
                    result = cls;
                }
            } else if (src instanceof ParameterizedType parameterizedType) {
                int index = (parameterizedType.getRawType() instanceof Class
                        && Map.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())
                        && parameterizedType.getActualTypeArguments().length > 1) ? 1 : 0;
                Type actualType = parameterizedType.getActualTypeArguments()[index];
                result = typeToClass(actualType, fallback);
            }
            if (result == null) {
                result = fallback;
            }
            return result;
        }

    }

    static class MyBatisMapperFactoryBeanPostProcessor implements MergedBeanDefinitionPostProcessor, BeanFactoryAware {

        private static final org.apache.commons.logging.Log log = LogFactory
                .getLog(MyBatisMapperFactoryBeanPostProcessor.class);

        private static final String MAPPER_FACTORY_BEAN = "org.mybatis.spring.mapper.MapperFactoryBean";

        private ConfigurableBeanFactory beanFactory;

        @Override
        public void setBeanFactory(BeanFactory beanFactory) {
            this.beanFactory = (ConfigurableBeanFactory) beanFactory;
        }

        @Override
        public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType,
                String beanName) {
            if (ClassUtils.isPresent(MAPPER_FACTORY_BEAN, this.beanFactory.getBeanClassLoader())) {
                resolveMapperFactoryBeanTypeIfNecessary(beanDefinition);
            }
        }

        private void resolveMapperFactoryBeanTypeIfNecessary(RootBeanDefinition beanDefinition) {
            if (!beanDefinition.hasBeanClass()
                    || !MapperFactoryBean.class.isAssignableFrom(beanDefinition.getBeanClass())) {
                return;
            }
            if (beanDefinition.getResolvableType().hasUnresolvableGenerics()) {
                Class<?> mapperInterface = getMapperInterface(beanDefinition);
                if (mapperInterface != null) {
                    // 将通用类型信息暴露给上下文，以防止早期初始化
                    beanDefinition.setTargetType(
                            ResolvableType.forClassWithGenerics(beanDefinition.getBeanClass(), mapperInterface));
                    ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
                    constructorArgumentValues.addGenericArgumentValue(mapperInterface);
                    beanDefinition.setConstructorArgumentValues(constructorArgumentValues);
                }
            }
        }

        private Class<?> getMapperInterface(RootBeanDefinition beanDefinition) {
            try {
                return (Class<?>) beanDefinition.getPropertyValues().get("mapperInterface");
            } catch (Exception e) {
                log.warn("获取映射器接口类型失败。", e);
                return null;
            }
        }

    }
}
