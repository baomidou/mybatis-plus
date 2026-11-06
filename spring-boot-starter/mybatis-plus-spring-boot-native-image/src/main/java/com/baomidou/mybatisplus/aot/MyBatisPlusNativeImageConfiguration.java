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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * mybatis-plus 的 native-image 支持：静态元数据（META-INF/native-image 下的 *-config.json）负责 MyBatis/MyBatis-Plus
 * 基础设施，本配置类只负责随项目变化的用户 Mapper 接口与 Mapper XML。 XML 由 {@code mybatis-plus.mapper-locations} 及默认 classpath 位置发现；使用 MyBatis
 * 配置文件中的 {@code <mappers>} 或自定义 {@code setMapperLocations} 时，可启用 {@link #REGISTER_ALL_XML_PROPERTY} 注册 classpath 下的
 * XML。
 *
 * @author xiaochen
 * @since 2025-08-20
 */
@ConditionalOnProperty(value = AbstractAotProcessor.AOT_PROCESSING, havingValue = "true")
@ConditionalOnClass(MapperFactoryBean.class)
@Configuration(proxyBeanMethods = false)
public final class MyBatisPlusNativeImageConfiguration {

    /**
     * 开启后注册 classpath 下全部 XML，默认只注册 mybatis-plus.mapper-locations 命中的 XML
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
                            registerSamePathMapperXml(mapperInterfaceType, hints);
                        }
                    }
                }
            };
        }

        /**
         * Resolve configured classpath locations during AOT and register only the XML resources they actually match.
         */
        static void registerMapperXmlResources(RuntimeHints hints, Environment environment) {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            List<String> locations;
            if (isRegisterAllXml(environment)) {
                locations = List.of("classpath*:**/*.xml", "classpath*:*.xml");
            } else {
                locations = resolveMapperLocations(environment);
            }
            for (String location : locations) {
                if (!isClasspathLocation(location)) {
                    continue;
                }
                try {
                    for (Resource resource : resolver.getResources(location)) {
                        if (resource.getFilename() == null || !resource.getFilename().endsWith(".xml")) {
                            continue;
                        }
                        hints.resources().registerPattern(toClasspathResourcePath(resource, resolver.getClassLoader()));
                    }
                } catch (IOException e) {
                    throw new IllegalStateException(
                            "Failed to resolve MyBatis mapper resources for location '" + location + "' during AOT", e);
                }
            }
        }

        /**
         * MybatisMapperAnnotationBuilder#loadXmlResource 会自动加载与 Mapper 接口同路径的 xml，与 mapper-locations 无关，需精确登记。
         */
        static void registerSamePathMapperXml(Class<?> mapperInterfaceType, RuntimeHints hints) {
            String resourcePath = mapperInterfaceType.getName().replace('.', '/') + ".xml";
            ClassPathResource resource = new ClassPathResource(resourcePath, mapperInterfaceType.getClassLoader());
            if (resource.exists()) {
                hints.resources().registerResource(resource);
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

        private static boolean isClasspathLocation(String location) {
            String value = location.trim();
            return value.startsWith("classpath*:") || value.startsWith("classpath:") || !value.contains(":");
        }

        private static String toClasspathResourcePath(Resource resource, ClassLoader classLoader) throws IOException {
            if (resource instanceof ClassPathResource classPathResource) {
                return classPathResource.getPath();
            }
            URL url = resource.getURL();
            if ("jar".equals(url.getProtocol())) {
                if (url.openConnection() instanceof JarURLConnection connection) {
                    return connection.getEntryName();
                }
            } else if ("file".equals(url.getProtocol())) {
                try {
                    java.nio.file.Path resourcePath = java.nio.file.Paths.get(url.toURI());
                    Enumeration<URL> roots = classLoader.getResources("");
                    while (roots.hasMoreElements()) {
                        URL root = roots.nextElement();
                        if ("file".equals(root.getProtocol())) {
                            java.nio.file.Path rootPath = java.nio.file.Paths.get(root.toURI());
                            if (resourcePath.startsWith(rootPath)) {
                                return rootPath.relativize(resourcePath).toString().replace('\\', '/');
                            }
                        }
                    }
                } catch (URISyntaxException e) {
                    throw new IOException("Invalid classpath resource URL: " + url, e);
                }
            }
            throw new IOException("Cannot determine classpath path for MyBatis mapper resource: " + resource);
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
