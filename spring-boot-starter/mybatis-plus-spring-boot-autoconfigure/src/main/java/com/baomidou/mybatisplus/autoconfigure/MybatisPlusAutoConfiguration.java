/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.autoconfigure;


import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.AnnotationHandler;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.handlers.PostInitTableInfoHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link EnableAutoConfiguration Auto-Configuration} for Mybatis. Contributes a
 * {@link SqlSessionFactory} and a {@link SqlSessionTemplate}.
 * <p>
 * If {@link org.mybatis.spring.annotation.MapperScan} is used, or a
 * configuration file is specified as a property, those will be considered,
 * otherwise this auto-configuration will attempt to register mappers based on
 * the interface definitions in or under the root auto-configuration package.
 * </p>
 * <p> copy from {@link org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration}</p>
 *
 * @author Eddú Meléndez
 * @author Josh Long
 * @author Kazuki Shimizu
 * @author Eduardo Macarrón
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties(MybatisPlusProperties.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, MybatisPlusLanguageDriverAutoConfiguration.class})
public class MybatisPlusAutoConfiguration implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MybatisPlusAutoConfiguration.class);

    private final MybatisPlusProperties properties;

    private final Interceptor[] interceptors;

    private final TypeHandler[] typeHandlers;

    private final LanguageDriver[] languageDrivers;

    private final ResourceLoader resourceLoader;

    private final DatabaseIdProvider databaseIdProvider;

    private final List<ConfigurationCustomizer> configurationCustomizers;

    private final List<SqlSessionFactoryBeanCustomizer> sqlSessionFactoryBeanCustomizers;

    private final List<MybatisPlusPropertiesCustomizer> mybatisPlusPropertiesCustomizers;

    private final ApplicationContext applicationContext;

    public MybatisPlusAutoConfiguration(MybatisPlusProperties properties,
                                        ObjectProvider<Interceptor[]> interceptorsProvider,
                                        ObjectProvider<TypeHandler[]> typeHandlersProvider,
                                        ObjectProvider<LanguageDriver[]> languageDriversProvider,
                                        ResourceLoader resourceLoader,
                                        ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                        ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
                                        ObjectProvider<List<SqlSessionFactoryBeanCustomizer>> sqlSessionFactoryBeanCustomizers,
                                        ObjectProvider<List<MybatisPlusPropertiesCustomizer>> mybatisPlusPropertiesCustomizerProvider,
                                        ApplicationContext applicationContext) {
        this.properties = properties;
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.typeHandlers = typeHandlersProvider.getIfAvailable();
        this.languageDrivers = languageDriversProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
        this.sqlSessionFactoryBeanCustomizers = sqlSessionFactoryBeanCustomizers.getIfAvailable();
        this.mybatisPlusPropertiesCustomizers = mybatisPlusPropertiesCustomizerProvider.getIfAvailable();
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        if (!CollectionUtils.isEmpty(mybatisPlusPropertiesCustomizers)) {
            mybatisPlusPropertiesCustomizers.forEach(i -> i.customize(properties));
        }
        checkConfigFileExists();
    }

    private void checkConfigFileExists() {
        if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
            Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
            Assert.state(resource.exists(),
                "Cannot find config location: " + resource + " (please add config file or check your Mybatis configuration)");
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        applyConfiguration(factory);
        if (this.properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(this.properties.getConfigurationProperties());
        }
        if (!ObjectUtils.isEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }
        if (this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (this.properties.getTypeAliasesSuperType() != null) {
            factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(this.typeHandlers)) {
            factory.setTypeHandlers(this.typeHandlers);
        }
        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
            factory.setMapperLocations(this.properties.resolveMapperLocations());
        }
        this.getBeanThen(TransactionFactory.class, factory::setTransactionFactory);

        Class<? extends LanguageDriver> defaultLanguageDriver = this.properties.getDefaultScriptingLanguageDriver();
        if (!ObjectUtils.isEmpty(this.languageDrivers)) {
            factory.setScriptingLanguageDrivers(this.languageDrivers);
        }
        Optional.ofNullable(defaultLanguageDriver).ifPresent(factory::setDefaultScriptingLanguageDriver);

        applySqlSessionFactoryBeanCustomizers(factory);

        GlobalConfig globalConfig = this.properties.getGlobalConfig();
        this.getBeanThen(MetaObjectHandler.class, globalConfig::setMetaObjectHandler);
        this.getBeanThen(AnnotationHandler.class, globalConfig::setAnnotationHandler);
        this.getBeanThen(PostInitTableInfoHandler.class, globalConfig::setPostInitTableInfoHandler);
        this.getBeansThen(IKeyGenerator.class, i -> globalConfig.getDbConfig().setKeyGenerators(i));
        this.getBeanThen(ISqlInjector.class, globalConfig::setSqlInjector);
        this.getBeanThen(IdentifierGenerator.class, globalConfig::setIdentifierGenerator);
        factory.setGlobalConfig(globalConfig);
        return factory.getObject();
    }

    /**
     * 检查spring容器里是否有对应的bean,有则进行消费
     *
     * @param clazz    class
     * @param consumer 消费
     * @param <T>      泛型
     */
    private <T> void getBeanThen(Class<T> clazz, Consumer<T> consumer) {
        if (this.applicationContext.getBeanNamesForType(clazz, false, false).length > 0) {
            consumer.accept(this.applicationContext.getBean(clazz));
        }
    }

    /**
     * 检查spring容器里是否有对应的bean,有则进行消费
     *
     * @param clazz    class
     * @param consumer 消费
     * @param <T>      泛型
     */
    private <T> void getBeansThen(Class<T> clazz, Consumer<List<T>> consumer) {
        if (this.applicationContext.getBeanNamesForType(clazz, false, false).length > 0) {
            final Map<String, T> beansOfType = this.applicationContext.getBeansOfType(clazz);
            List<T> clazzList = new ArrayList<>();
            beansOfType.forEach((k, v) -> clazzList.add(v));
            consumer.accept(clazzList);
        }
    }

    private void applyConfiguration(MybatisSqlSessionFactoryBean factory) {
        MybatisPlusProperties.CoreConfiguration coreConfiguration = this.properties.getConfiguration();
        MybatisConfiguration configuration = null;
        if (coreConfiguration != null || !StringUtils.hasText(this.properties.getConfigLocation())) {
            configuration = new MybatisConfiguration();
        }
        if (configuration != null && coreConfiguration != null) {
            coreConfiguration.applyTo(configuration);
        }
        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                customizer.customize(configuration);
            }
        }
        factory.setConfiguration(configuration);
    }

    private void applySqlSessionFactoryBeanCustomizers(MybatisSqlSessionFactoryBean factory) {
        if (!CollectionUtils.isEmpty(this.sqlSessionFactoryBeanCustomizers)) {
            for (SqlSessionFactoryBeanCustomizer customizer : this.sqlSessionFactoryBeanCustomizers) {
                customizer.customize(factory);
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        ExecutorType executorType = this.properties.getExecutorType();
        if (executorType != null) {
            return new SqlSessionTemplate(sqlSessionFactory, executorType);
        } else {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
    }

    /**
     * This will just scan the same base package as Spring Boot does. If you want more power, you can explicitly use
     * {@link org.mybatis.spring.annotation.MapperScan} but this will get typed mappers working correctly, out-of-the-box,
     * similar to using Spring Data JPA repositories.
     */
    public static class AutoConfiguredMapperScannerRegistrar
        implements BeanFactoryAware, EnvironmentAware, ImportBeanDefinitionRegistrar {

        private BeanFactory beanFactory;
        private Environment environment;

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

            if (!AutoConfigurationPackages.has(this.beanFactory)) {
                logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.");
                return;
            }

            logger.debug("Searching for mappers annotated with @Mapper");

            List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
            if (logger.isDebugEnabled()) {
                packages.forEach(pkg -> logger.debug("Using auto-configuration base package '{}'", pkg));
            }

            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
            builder.addPropertyValue("processPropertyPlaceHolders", true);
            builder.addPropertyValue("annotationClass", Mapper.class);
            builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(packages));
            BeanWrapper beanWrapper = new BeanWrapperImpl(MapperScannerConfigurer.class);
            Set<String> propertyNames = Stream.of(beanWrapper.getPropertyDescriptors()).map(PropertyDescriptor::getName)
                .collect(Collectors.toSet());
            if (propertyNames.contains("lazyInitialization")) {
                // Need to mybatis-spring 2.0.2+
                builder.addPropertyValue("lazyInitialization", "${mybatis-plus.lazy-initialization:${mybatis.lazy-initialization:false}}");
            }
            if (propertyNames.contains("defaultScope")) {
                // Need to mybatis-spring 2.0.6+
                builder.addPropertyValue("defaultScope", "${mybatis-plus.mapper-default-scope:}");
            }

            // for spring-native
            Boolean injectSqlSession = environment.getProperty("mybatis-plus.inject-sql-session-on-mapper-scan", Boolean.class);
            if (injectSqlSession == null) {
                injectSqlSession = environment.getProperty("mybatis.inject-sql-session-on-mapper-scan", Boolean.class, Boolean.TRUE);
            }
            if (injectSqlSession && this.beanFactory instanceof ListableBeanFactory) {
                ListableBeanFactory listableBeanFactory = (ListableBeanFactory) this.beanFactory;
                Optional<String> sqlSessionTemplateBeanName = Optional
                    .ofNullable(getBeanNameForType(SqlSessionTemplate.class, listableBeanFactory));
                Optional<String> sqlSessionFactoryBeanName = Optional
                    .ofNullable(getBeanNameForType(SqlSessionFactory.class, listableBeanFactory));
                if (sqlSessionTemplateBeanName.isPresent() || !sqlSessionFactoryBeanName.isPresent()) {
                    builder.addPropertyValue("sqlSessionTemplateBeanName",
                        sqlSessionTemplateBeanName.orElse("sqlSessionTemplate"));
                } else {
                    builder.addPropertyValue("sqlSessionFactoryBeanName", sqlSessionFactoryBeanName.get());
                }
            }
            builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

            registry.registerBeanDefinition(MapperScannerConfigurer.class.getName(), builder.getBeanDefinition());
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        private String getBeanNameForType(Class<?> type, ListableBeanFactory factory) {
            String[] beanNames = factory.getBeanNamesForType(type);
            return beanNames.length > 0 ? beanNames[0] : null;
        }
    }

    /**
     * If mapper registering configuration or mapper scanning configuration not present, this configuration allow to scan
     * mappers based on the same component-scanning path as Spring Boot itself.
     */
    @org.springframework.context.annotation.Configuration(proxyBeanMethods = false)
    @Import(AutoConfiguredMapperScannerRegistrar.class)
    @ConditionalOnMissingBean({MapperFactoryBean.class, MapperScannerConfigurer.class})
    public static class MapperScannerRegistrarNotFoundConfiguration implements InitializingBean {

        @Override
        public void afterPropertiesSet() {
            logger.debug(
                "Not found configuration for registering mapper bean using @MapperScan, MapperFactoryBean and MapperScannerConfigurer.");
        }
    }

}
