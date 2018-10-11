/*
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.spring;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLConfigBuilder;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlHelper;
import com.baomidou.mybatisplus.extension.handlers.EnumAnnotationTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.EnumTypeHandler;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.baomidou.mybatisplus.extension.toolkit.PackageHelper;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static org.springframework.util.Assert.notNull;
import static org.springframework.util.Assert.state;
import static org.springframework.util.ObjectUtils.isEmpty;
import static org.springframework.util.StringUtils.hasLength;
import static org.springframework.util.StringUtils.tokenizeToStringArray;

/**
 * <p>
 * 拷贝类 org.mybatis.spring.SqlSessionFactoryBean 修改方法 buildSqlSessionFactory()
 * 加载自定义 MybatisXmlConfigBuilder
 * </p>
 *
 * @author hubin
 * @since 2017-01-04
 */
public class MybatisSqlSessionFactoryBean implements FactoryBean<SqlSessionFactory>, InitializingBean, ApplicationListener<ApplicationEvent> {

    private static final Log LOGGER = LogFactory.getLog(SqlSessionFactoryBean.class);

    private Resource configLocation;

    private MybatisConfiguration configuration;

    private Resource[] mapperLocations;

    private DataSource dataSource;

    private TransactionFactory transactionFactory;

    private Properties configurationProperties;

    private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();

    private SqlSessionFactory sqlSessionFactory;

    //EnvironmentAware requires spring 3.1
    private String environment = MybatisSqlSessionFactoryBean.class.getSimpleName();

    private boolean failFast;

    private Interceptor[] plugins;

    private TypeHandler<?>[] typeHandlers;

    private String typeHandlersPackage;

    private Class<?>[] typeAliases;

    private String typeAliasesPackage;

    // TODO 自定义枚举包
    private String typeEnumsPackage;

    private Class<?> typeAliasesSuperType;

    //issue #19. No default provider.
    private DatabaseIdProvider databaseIdProvider;

    private Class<? extends VFS> vfs;

    private Cache cache;

    private ObjectFactory objectFactory;

    private ObjectWrapperFactory objectWrapperFactory;

    private GlobalConfig globalConfig;

    // TODO 注入全局配置
    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    /**
     * Sets the ObjectFactory.
     *
     * @param objectFactory
     * @since 1.1.2
     */
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    /**
     * Sets the ObjectWrapperFactory.
     *
     * @param objectWrapperFactory
     * @since 1.1.2
     */
    public void setObjectWrapperFactory(ObjectWrapperFactory objectWrapperFactory) {
        this.objectWrapperFactory = objectWrapperFactory;
    }

    /**
     * Gets the DatabaseIdProvider
     *
     * @return
     * @since 1.1.0
     */
    public DatabaseIdProvider getDatabaseIdProvider() {
        return databaseIdProvider;
    }

    /**
     * Sets the DatabaseIdProvider.
     * As of version 1.2.2 this variable is not initialized by default.
     *
     * @param databaseIdProvider
     * @since 1.1.0
     */
    public void setDatabaseIdProvider(DatabaseIdProvider databaseIdProvider) {
        this.databaseIdProvider = databaseIdProvider;
    }

    public Class<? extends VFS> getVfs() {
        return this.vfs;
    }

    public void setVfs(Class<? extends VFS> vfs) {
        this.vfs = vfs;
    }

    public Cache getCache() {
        return this.cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    /**
     * Mybatis plugin list.
     *
     * @param plugins list of plugins
     * @since 1.0.1
     */
    public void setPlugins(Interceptor[] plugins) {
        this.plugins = plugins;
    }

    /**
     * 支持 typeAliasesPackage 多项每项都有通配符 com.a.b.*.po, com.c.*.po
     * ISSUE https://gitee.com/baomidou/mybatis-plus/issues/IKJ48
     */
    public void setTypeAliasesPackage(String typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }

    public void setTypeEnumsPackage(String typeEnumsPackage) {
        this.typeEnumsPackage = typeEnumsPackage;
    }

    /**
     * Super class which domain objects have to extend to have a type alias created.
     * No effect if there is no package to scan configured.
     *
     * @param typeAliasesSuperType super class for domain objects
     * @since 1.1.2
     */
    public void setTypeAliasesSuperType(Class<?> typeAliasesSuperType) {
        this.typeAliasesSuperType = typeAliasesSuperType;
    }

    /**
     * Packages to search for type handlers.
     *
     * @param typeHandlersPackage package to scan for type handlers
     * @since 1.0.1
     */
    public void setTypeHandlersPackage(String typeHandlersPackage) {
        this.typeHandlersPackage = typeHandlersPackage;
    }

    /**
     * Set type handlers. They must be annotated with {@code MappedTypes} and optionally with {@code MappedJdbcTypes}
     *
     * @param typeHandlers Type handler list
     * @since 1.0.1
     */
    public void setTypeHandlers(TypeHandler<?>[] typeHandlers) {
        this.typeHandlers = typeHandlers;
    }

    /**
     * List of type aliases to register. They can be annotated with {@code Alias}
     *
     * @param typeAliases Type aliases list
     * @since 1.0.1
     */
    public void setTypeAliases(Class<?>[] typeAliases) {
        this.typeAliases = typeAliases;
    }

    /**
     * If true, a final check is done on Configuration to assure that all mapped
     * statements are fully loaded and there is no one still pending to resolve
     * includes. Defaults to false.
     *
     * @param failFast enable failFast
     * @since 1.0.1
     */
    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }

    /**
     * Set the location of the MyBatis {@code SqlSessionFactory} config file. A typical value is
     * "WEB-INF/mybatis-configuration.xml".
     */
    public void setConfigLocation(Resource configLocation) {
        this.configLocation = configLocation;
    }

    /**
     * Set a customized MyBatis configuration.
     *
     * @param configuration MyBatis configuration
     * @since 1.3.0
     */
    public void setConfiguration(MybatisConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Set locations of MyBatis mapper files that are going to be merged into the {@code SqlSessionFactory}
     * configuration at runtime.
     * <p>
     * This is an alternative to specifying "&lt;sqlmapper&gt;" entries in an MyBatis config file.
     * This property being based on Spring's resource abstraction also allows for specifying
     * resource patterns here: e.g. "classpath*:sqlmap/*-mapper.xml".
     */
    public void setMapperLocations(Resource[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    /**
     * Set optional properties to be passed into the SqlSession configuration, as alternative to a
     * {@code &lt;properties&gt;} tag in the configuration xml file. This will be used to
     * resolve placeholders in the config file.
     */
    public void setConfigurationProperties(Properties sqlSessionFactoryProperties) {
        this.configurationProperties = sqlSessionFactoryProperties;
    }

    /**
     * Set the JDBC {@code DataSource} that this instance should manage transactions for. The {@code DataSource}
     * should match the one used by the {@code SqlSessionFactory}: for example, you could specify the same
     * JNDI DataSource for both.
     * <p>
     * A transactional JDBC {@code Connection} for this {@code DataSource} will be provided to application code
     * accessing this {@code DataSource} directly via {@code DataSourceUtils} or {@code DataSourceTransactionManager}.
     * <p>
     * The {@code DataSource} specified here should be the target {@code DataSource} to manage transactions for, not
     * a {@code TransactionAwareDataSourceProxy}. Only data access code may work with
     * {@code TransactionAwareDataSourceProxy}, while the transaction manager needs to work on the
     * underlying target {@code DataSource}. If there's nevertheless a {@code TransactionAwareDataSourceProxy}
     * passed in, it will be unwrapped to extract its target {@code DataSource}.
     */
    public void setDataSource(DataSource dataSource) {
        if (dataSource instanceof TransactionAwareDataSourceProxy) {
            // If we got a TransactionAwareDataSourceProxy, we need to perform
            // transactions for its underlying target DataSource, else data
            // access code won't see properly exposed transactions (i.e.
            // transactions for the target DataSource).
            this.dataSource = ((TransactionAwareDataSourceProxy) dataSource).getTargetDataSource();
        } else {
            this.dataSource = dataSource;
        }
    }

    /**
     * Sets the {@code SqlSessionFactoryBuilder} to use when creating the {@code SqlSessionFactory}.
     * <p>
     * This is mainly meant for testing so that mock SqlSessionFactory classes can be injected. By
     * default, {@code SqlSessionFactoryBuilder} creates {@code DefaultSqlSessionFactory} instances.
     */
    public void setSqlSessionFactoryBuilder(SqlSessionFactoryBuilder sqlSessionFactoryBuilder) {
        this.sqlSessionFactoryBuilder = sqlSessionFactoryBuilder;
    }

    /**
     * Set the MyBatis TransactionFactory to use. Default is {@code SpringManagedTransactionFactory}
     * <p>
     * The default {@code SpringManagedTransactionFactory} should be appropriate for all cases:
     * be it Spring transaction management, EJB CMT or plain JTA. If there is no active transaction,
     * SqlSession operations will execute SQL statements non-transactionally.
     * <p>
     * <b>It is strongly recommended to use the default {@code TransactionFactory}.</b> If not used, any
     * attempt at getting an SqlSession through Spring's MyBatis framework will throw an exception if
     * a transaction is active.
     *
     * @param transactionFactory the MyBatis TransactionFactory
     * @see SpringManagedTransactionFactory
     */
    public void setTransactionFactory(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    /**
     * <b>NOTE:</b> This class <em>overrides</em> any {@code Environment} you have set in the MyBatis
     * config file. This is used only as a placeholder name. The default value is
     * {@code SqlSessionFactoryBean.class.getSimpleName()}.
     *
     * @param environment the environment name
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        notNull(dataSource, "Property 'dataSource' is required");
        notNull(sqlSessionFactoryBuilder, "Property 'sqlSessionFactoryBuilder' is required");
        state((configuration == null && configLocation == null) || !(configuration != null && configLocation != null),
            "Property 'configuration' and 'configLocation' can not specified with together");

        this.sqlSessionFactory = buildSqlSessionFactory();
        //TODO: 3.0 注入到globalConfig
    }

    /**
     * Build a {@code SqlSessionFactory} instance.
     * <p>
     * The default implementation uses the standard MyBatis {@code XMLConfigBuilder} API to build a
     * {@code SqlSessionFactory} instance based on an Reader.
     * Since 1.3.0, it can be specified a {@link Configuration} instance directly(without config file).
     *
     * @return SqlSessionFactory
     * @throws IOException if loading the config file failed
     */
    protected SqlSessionFactory buildSqlSessionFactory() throws Exception {

        MybatisConfiguration configuration;

        // TODO 加载自定义 MybatisXmlConfigBuilder
        MybatisXMLConfigBuilder xmlConfigBuilder = null;
        if (this.configuration != null) {
            configuration = this.configuration;
            if (configuration.getVariables() == null) {
                configuration.setVariables(this.configurationProperties);
            } else if (this.configurationProperties != null) {
                configuration.getVariables().putAll(this.configurationProperties);
            }
        } else if (this.configLocation != null) {
            xmlConfigBuilder = new MybatisXMLConfigBuilder(this.configLocation.getInputStream(), null, this.configurationProperties);
            configuration = xmlConfigBuilder.getConfiguration();
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Property 'configuration' or 'configLocation' not specified, using default MyBatis Configuration");
            }
            // TODO 使用自定义配置
            configuration = new MybatisConfiguration();
            if (this.configurationProperties != null) {
                configuration.setVariables(this.configurationProperties);
            }
        }

        if (this.globalConfig == null) {
            this.globalConfig = GlobalConfigUtils.defaults();
        }
        if (this.globalConfig.getDbConfig() == null) {
            this.globalConfig.setDbConfig(new GlobalConfig.DbConfig());
        }

        // TODO 初始化 id-work 以及 打印骚东西
        configuration.init(this.globalConfig);

        if (this.objectFactory != null) {
            configuration.setObjectFactory(this.objectFactory);
        }

        if (this.objectWrapperFactory != null) {
            configuration.setObjectWrapperFactory(this.objectWrapperFactory);
        }

        if (this.vfs != null) {
            configuration.setVfsImpl(this.vfs);
        }

        if (hasLength(this.typeAliasesPackage)) {
            // TODO 支持自定义通配符
            List<String> typeAliasPackageList = new ArrayList<>();
            if (typeAliasesPackage.contains(StringPool.ASTERISK) && !typeAliasesPackage.contains(StringPool.COMMA) && !typeAliasesPackage.contains(StringPool.SEMICOLON)) {
                String[] convertTypeAliasesPackages = PackageHelper.convertTypeAliasesPackage(this.typeAliasesPackage);
                if (ArrayUtils.isEmpty(convertTypeAliasesPackages)) {
                    LOGGER.warn("Can't find class in '[" + this.typeAliasesPackage + "]' package. Please check your configuration.");
                } else {
                    typeAliasPackageList.addAll(Arrays.asList(convertTypeAliasesPackages));
                }
            } else {
                String[] typeAliasPackageArray = tokenizeToStringArray(this.typeAliasesPackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
                for (String one : typeAliasPackageArray) {
                    if (one.contains(StringPool.ASTERISK)) {
                        String[] convertTypeAliasesPackages = PackageHelper.convertTypeAliasesPackage(one);
                        if (ArrayUtils.isEmpty(convertTypeAliasesPackages)) {
                            LOGGER.warn("Can't find class in '[" + one + "]' package. Please check your configuration.");
                        } else {
                            typeAliasPackageList.addAll(Arrays.asList(convertTypeAliasesPackages));
                        }
                    } else {
                        typeAliasPackageList.add(one);
                    }
                }
            }
            for (String packageToScan : typeAliasPackageList) {
                configuration.getTypeAliasRegistry().registerAliases(packageToScan,
                    typeAliasesSuperType == null ? Object.class : typeAliasesSuperType);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Scanned package: '" + packageToScan + "' for aliases");
                }
            }
        }

        // TODO 自定义枚举类扫描处理
        if (hasLength(this.typeEnumsPackage)) {
            Set<Class> classes;
            if (typeEnumsPackage.contains(StringPool.STAR) && !typeEnumsPackage.contains(StringPool.COMMA)
                && !typeEnumsPackage.contains(StringPool.SEMICOLON)) {
                classes = PackageHelper.scanTypePackage(typeEnumsPackage);
                if (classes.isEmpty()) {
                    LOGGER.warn("Can't find class in '[" + typeEnumsPackage + "]' package. Please check your configuration.");
                }
            } else {
                String[] typeEnumsPackageArray = tokenizeToStringArray(this.typeEnumsPackage,
                    ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
                Assert.notNull(typeEnumsPackageArray, "not find typeEnumsPackage:" + typeEnumsPackage);
                classes = new HashSet<>();
                for (String typePackage : typeEnumsPackageArray) {
                    Set<Class> scanTypePackage = PackageHelper.scanTypePackage(typePackage);
                    if (scanTypePackage.isEmpty()) {
                        LOGGER.warn("Can't find class in '[" + typePackage + "]' package. Please check your configuration.");
                    } else {
                        classes.addAll(PackageHelper.scanTypePackage(typePackage));
                    }
                }
            }
            // 取得类型转换注册器
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            for (Class cls : classes) {
                if (cls.isEnum()) {
                    if (IEnum.class.isAssignableFrom(cls)) {
                        // 接口方式
                        typeHandlerRegistry.register(cls, EnumTypeHandler.class);
                    } else {
                        // 注解方式
                        Class<?> clazz = dealEnumType(cls);
                        if (null != clazz) {
                            typeHandlerRegistry.register(cls, EnumAnnotationTypeHandler.class);
                        } else {
                            // 原生方式
                            registerOriginalEnumTypeHandler(typeHandlerRegistry, cls);
                        }
                    }
                }
            }
        }

        if (!isEmpty(this.typeAliases)) {
            for (Class<?> typeAlias : this.typeAliases) {
                configuration.getTypeAliasRegistry().registerAlias(typeAlias);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Registered type alias: '" + typeAlias + "'");
                }
            }
        }

        if (!isEmpty(this.plugins)) {
            for (Interceptor plugin : this.plugins) {
                configuration.addInterceptor(plugin);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Registered plugin: '" + plugin + "'");
                }
            }
        }

        if (hasLength(this.typeHandlersPackage)) {
            String[] typeHandlersPackageArray = tokenizeToStringArray(this.typeHandlersPackage,
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            for (String packageToScan : typeHandlersPackageArray) {
                configuration.getTypeHandlerRegistry().register(packageToScan);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Scanned package: '" + packageToScan + "' for type handlers");
                }
            }
        }

        if (!isEmpty(this.typeHandlers)) {
            for (TypeHandler<?> typeHandler : this.typeHandlers) {
                configuration.getTypeHandlerRegistry().register(typeHandler);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Registered type handler: '" + typeHandler + "'");
                }
            }
        }

        if (this.databaseIdProvider != null) {//fix #64 set databaseId before parse mapper xmls
            try {
                configuration.setDatabaseId(this.databaseIdProvider.getDatabaseId(this.dataSource));
            } catch (SQLException e) {
                throw new NestedIOException("Failed getting a databaseId", e);
            }
        }

        if (this.cache != null) {
            configuration.addCache(this.cache);
        }

        if (xmlConfigBuilder != null) {
            try {
                xmlConfigBuilder.parse();

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Parsed configuration file: '" + this.configLocation + "'");
                }
            } catch (Exception ex) {
                throw new NestedIOException("Failed to parse config resource: " + this.configLocation, ex);
            } finally {
                ErrorContext.instance().reset();
            }
        }

        if (this.transactionFactory == null) {
            this.transactionFactory = new SpringManagedTransactionFactory();
        }

        configuration.setEnvironment(new Environment(this.environment, this.transactionFactory, this.dataSource));

        // TODO 设置元数据相关 如果用户没有配置 dbType 则自动获取
        if (globalConfig.getDbConfig().getDbType() == DbType.OTHER) {
            try (Connection connection = AopUtils.getTargetObject(this.dataSource).getConnection()) {
                globalConfig.getDbConfig().setDbType(JdbcUtils.getDbType(connection.getMetaData().getURL()));
            } catch (Exception e) {
                throw ExceptionUtils.mpe("Error: GlobalConfigUtils setMetaData Fail !  Cause:" + e);
            }
        }
        SqlSessionFactory sqlSessionFactory = this.sqlSessionFactoryBuilder.build(configuration);

        // TODO SqlRunner
        SqlHelper.FACTORY = sqlSessionFactory;

        // TODO 设置全局参数属性 以及 缓存 sqlSessionFactory
        globalConfig.signGlobalConfig(sqlSessionFactory);

        if (!isEmpty(this.mapperLocations)) {
            if (globalConfig.isRefresh()) {
                //TODO 设置自动刷新配置 减少配置
                new MybatisMapperRefresh(this.mapperLocations, sqlSessionFactory, 2,
                    2, true);
            }
            for (Resource mapperLocation : this.mapperLocations) {
                if (mapperLocation == null) {
                    continue;
                }

                try {
                    // TODO  这里也换了噢噢噢噢
                    XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(mapperLocation.getInputStream(),
                        configuration, mapperLocation.toString(), configuration.getSqlFragments());
                    xmlMapperBuilder.parse();
                } catch (Exception e) {
                    throw new NestedIOException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
                } finally {
                    ErrorContext.instance().reset();
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Parsed mapper file: '" + mapperLocation + "'");
                }
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Property 'mapperLocations' was not specified or no matching resources found");
            }
        }
        return sqlSessionFactory;
    }

    /**
     * 处理普通枚举
     * 把带{@link EnumValue}的field注册到处理器中
     *
     * @param clazz
     */
    protected Class<?> dealEnumType(Class<?> clazz) {
        if (clazz.isEnum()) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(EnumValue.class)) {
                    f.setAccessible(true);
                    EnumAnnotationTypeHandler.addEnumType(clazz, f);
                    return clazz;
                }
            }
        }
        return null;
    }

    /**
     * 对原生枚举的处理类，默认{@link EnumOrdinalTypeHandler}
     *
     * @param typeHandlerRegistry
     * @param enumClazz
     */
    protected void registerOriginalEnumTypeHandler(TypeHandlerRegistry typeHandlerRegistry, Class<?> enumClazz) {
        typeHandlerRegistry.register(enumClazz, EnumOrdinalTypeHandler.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SqlSessionFactory getObject() throws Exception {
        if (this.sqlSessionFactory == null) {
            afterPropertiesSet();
        }

        return this.sqlSessionFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends SqlSessionFactory> getObjectType() {
        return this.sqlSessionFactory == null ? SqlSessionFactory.class : this.sqlSessionFactory.getClass();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (failFast && event instanceof ContextRefreshedEvent) {
            // fail-fast -> check all statements are completed
            this.sqlSessionFactory.getConfiguration().getMappedStatementNames();
        }
    }
}
