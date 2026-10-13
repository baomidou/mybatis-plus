/*
 *    Copyright 2015-2025 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.spring.boot.autoconfigure;

import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.AutoMappingUnknownColumnBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Configuration properties for MyBatis.
 *
 * @author Eddú Meléndez
 * @author Kazuki Shimizu
 */
@ConfigurationProperties(prefix = MybatisProperties.MYBATIS_PREFIX)
public class MybatisProperties {

    public static final String MYBATIS_PREFIX = "mybatis";

    private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    /**
     * Location of MyBatis xml config file.
     */
    private String configLocation;

    /**
     * Locations of MyBatis mapper files.
     */
    private String[] mapperLocations;

    /**
     * Packages to search type aliases. (Package delimiters are ",; \t\n")
     */
    private String typeAliasesPackage;

    /**
     * The super class for filtering type alias. If this not specifies, the MyBatis deal as type alias all classes that
     * searched from typeAliasesPackage.
     */
    private Class<?> typeAliasesSuperType;

    /**
     * Packages to search for type handlers. (Package delimiters are ",; \t\n")
     */
    private String typeHandlersPackage;

    /**
     * Indicates whether perform presence check of the MyBatis xml config file.
     */
    private boolean checkConfigLocation = false;

    /**
     * Execution mode for {@link org.mybatis.spring.SqlSessionTemplate}.
     */
    private ExecutorType executorType;

    /**
     * The default scripting language driver class. (Available when use together with mybatis-spring 2.0.2+)
     */
    private Class<? extends LanguageDriver> defaultScriptingLanguageDriver;

    /**
     * Externalized properties for MyBatis configuration.
     */
    private Properties configurationProperties;

    /**
     * A Configuration object for customize default settings. If {@link #configLocation} is specified, this property is
     * not used.
     */
    private CoreConfiguration configuration;

    /**
     * @since 1.1.0
     */
    public String getConfigLocation() {
        return this.configLocation;
    }

    /**
     * @since 1.1.0
     */
    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public String[] getMapperLocations() {
        return this.mapperLocations;
    }

    public void setMapperLocations(String[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public String getTypeHandlersPackage() {
        return this.typeHandlersPackage;
    }

    public void setTypeHandlersPackage(String typeHandlersPackage) {
        this.typeHandlersPackage = typeHandlersPackage;
    }

    public String getTypeAliasesPackage() {
        return this.typeAliasesPackage;
    }

    public void setTypeAliasesPackage(String typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }

    /**
     * @since 1.3.3
     */
    public Class<?> getTypeAliasesSuperType() {
        return typeAliasesSuperType;
    }

    /**
     * @since 1.3.3
     */
    public void setTypeAliasesSuperType(Class<?> typeAliasesSuperType) {
        this.typeAliasesSuperType = typeAliasesSuperType;
    }

    public boolean isCheckConfigLocation() {
        return this.checkConfigLocation;
    }

    public void setCheckConfigLocation(boolean checkConfigLocation) {
        this.checkConfigLocation = checkConfigLocation;
    }

    public ExecutorType getExecutorType() {
        return this.executorType;
    }

    public void setExecutorType(ExecutorType executorType) {
        this.executorType = executorType;
    }

    /**
     * @since 2.1.0
     */
    public Class<? extends LanguageDriver> getDefaultScriptingLanguageDriver() {
        return defaultScriptingLanguageDriver;
    }

    /**
     * @since 2.1.0
     */
    public void setDefaultScriptingLanguageDriver(Class<? extends LanguageDriver> defaultScriptingLanguageDriver) {
        this.defaultScriptingLanguageDriver = defaultScriptingLanguageDriver;
    }

    /**
     * @since 1.2.0
     */
    public Properties getConfigurationProperties() {
        return configurationProperties;
    }

    /**
     * @since 1.2.0
     */
    public void setConfigurationProperties(Properties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    public CoreConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(CoreConfiguration configuration) {
        this.configuration = configuration;
    }

    public Resource[] resolveMapperLocations() {
        return Stream.of(Optional.ofNullable(this.mapperLocations).orElse(new String[0]))
                .flatMap(location -> Stream.of(getResources(location))).toArray(Resource[]::new);
    }

    private Resource[] getResources(String location) {
        try {
            return resourceResolver.getResources(location);
        } catch (IOException e) {
            return new Resource[0];
        }
    }

    /**
     * The configuration properties for mybatis core module.
     *
     * @since 3.0.0
     */
    public static class CoreConfiguration {

        /**
         * Allows using RowBounds on nested statements. If allow, set the false. Default is false.
         */
        private Boolean safeRowBoundsEnabled;

        /**
         * Allows using ResultHandler on nested statements. If allow, set the false. Default is true.
         */
        private Boolean safeResultHandlerEnabled;

        /**
         * Enables automatic mapping from classic database column names A_COLUMN to camel case classic Java property names
         * aColumn. Default is false.
         */
        private Boolean mapUnderscoreToCamelCase;

        /**
         * When enabled, any method call will load all the lazy properties of the object. Otherwise, each property is loaded
         * on demand (see also lazyLoadTriggerMethods). Default is false.
         */
        private Boolean aggressiveLazyLoading;

        /**
         * Allows or disallows multiple ResultSets to be returned from a single statement (compatible driver required).
         * Default is true.
         */
        private Boolean multipleResultSetsEnabled;

        /**
         * Allows JDBC support for generated keys. A compatible driver is required. This setting forces generated keys to be
         * used if set to true, as some drivers deny compatibility but still work (e.g. Derby). Default is false.
         */
        private Boolean useGeneratedKeys;

        /**
         * Uses the column label instead of the column name. Different drivers behave differently in this respect. Refer to
         * the driver documentation, or test out both modes to determine how your driver behaves. Default is true.
         */
        private Boolean useColumnLabel;

        /**
         * Globally enables or disables any caches configured in any mapper under this configuration. Default is true.
         */
        private Boolean cacheEnabled;

        /**
         * Specifies if setters or map's put method will be called when a retrieved value is null. It is useful when you
         * rely on Map.keySet() or null value initialization. Note primitives such as (int,boolean,etc.) will not be set to
         * null. Default is false.
         */
        private Boolean callSettersOnNulls;

        /**
         * Allow referencing statement parameters by their actual names declared in the method signature. To use this
         * feature, your project must be compiled in Java 8 with -parameters option. Default is true.
         */
        private Boolean useActualParamName;

        /**
         * MyBatis, by default, returns null when all the columns of a returned row are NULL. When this setting is enabled,
         * MyBatis returns an empty instance instead. Note that it is also applied to nested results (i.e. collection and
         * association). Default is false.
         */
        private Boolean returnInstanceForEmptyRow;

        /**
         * Removes extra whitespace characters from the SQL. Note that this also affects literal strings in SQL. Default is
         * false.
         */
        private Boolean shrinkWhitespacesInSql;

        /**
         * Specifies the default value of 'nullable' attribute on 'foreach' tag. Default is false.
         */
        private Boolean nullableOnForEach;

        /**
         * When applying constructor auto-mapping, argument name is used to search the column to map instead of relying on
         * the column order. Default is false.
         */
        private Boolean argNameBasedConstructorAutoMapping;

        /**
         * Globally enables or disables lazy loading. When enabled, all relations will be lazily loaded. This value can be
         * superseded for a specific relation by using the fetchType attribute on it. Default is False.
         */
        private Boolean lazyLoadingEnabled;

        /**
         * Sets the number of seconds the driver will wait for a response from the database.
         */
        private Integer defaultStatementTimeout;

        /**
         * Sets the driver a hint as to control fetching size for return results. This parameter value can be override by a
         * query setting.
         */
        private Integer defaultFetchSize;

        /**
         * MyBatis uses local cache to prevent circular references and speed up repeated nested queries. By default
         * (SESSION) all queries executed during a session are cached. If localCacheScope=STATEMENT local session will be
         * used just for statement execution, no data will be shared between two different calls to the same SqlSession.
         * Default is SESSION.
         */
        private LocalCacheScope localCacheScope;

        /**
         * Specifies the JDBC type for null values when no specific JDBC type was provided for the parameter. Some drivers
         * require specifying the column JDBC type but others work with generic values like NULL, VARCHAR or OTHER. Default
         * is OTHER.
         */
        private JdbcType jdbcTypeForNull;

        /**
         * Specifies a scroll strategy when omit it per statement settings.
         */
        private ResultSetType defaultResultSetType;

        /**
         * Configures the default executor. SIMPLE executor does nothing special. REUSE executor reuses prepared statements.
         * BATCH executor reuses statements and batches updates. Default is SIMPLE.
         */
        private ExecutorType defaultExecutorType;

        /**
         * Specifies if and how MyBatis should automatically map columns to fields/properties. NONE disables auto-mapping.
         * PARTIAL will only auto-map results with no nested result mappings defined inside. FULL will auto-map result
         * mappings of any complexity (containing nested or otherwise). Default is PARTIAL.
         */
        private AutoMappingBehavior autoMappingBehavior;

        /**
         * Specify the behavior when detects an unknown column (or unknown property type) of automatic mapping target.
         * Default is NONE.
         */
        private AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior;

        /**
         * Specifies the prefix string that MyBatis will add to the logger names.
         */
        private String logPrefix;

        /**
         * Specifies which Object's methods trigger a lazy load. Default is [equals,clone,hashCode,toString].
         */
        private Set<String> lazyLoadTriggerMethods;

        /**
         * Specifies which logging implementation MyBatis should use. If this setting is not present logging implementation
         * will be autodiscovered.
         */
        private Class<? extends Log> logImpl;

        /**
         * Specifies VFS implementations.
         */
        private Class<? extends VFS> vfsImpl;

        /**
         * Specifies an sql provider class that holds provider method. This class apply to the type(or value) attribute on
         * sql provider annotation(e.g. @SelectProvider), when these attribute was omitted.
         */
        private Class<?> defaultSqlProviderType;

        /**
         * Specifies the TypeHandler used by default for Enum.
         */
        Class<? extends TypeHandler> defaultEnumTypeHandler;

        /**
         * Specifies the class that provides an instance of Configuration. The returned Configuration instance is used to
         * load lazy properties of deserialized objects. This class must have a method with a signature static Configuration
         * getConfiguration().
         */
        private Class<?> configurationFactory;

        /**
         * Specify any configuration variables.
         */
        private Properties variables;

        /**
         * Specifies the database identify value for switching query to use.
         */
        private String databaseId;

        public Boolean getSafeRowBoundsEnabled() {
            return safeRowBoundsEnabled;
        }

        public void setSafeRowBoundsEnabled(Boolean safeRowBoundsEnabled) {
            this.safeRowBoundsEnabled = safeRowBoundsEnabled;
        }

        public Boolean getSafeResultHandlerEnabled() {
            return safeResultHandlerEnabled;
        }

        public void setSafeResultHandlerEnabled(Boolean safeResultHandlerEnabled) {
            this.safeResultHandlerEnabled = safeResultHandlerEnabled;
        }

        public Boolean getMapUnderscoreToCamelCase() {
            return mapUnderscoreToCamelCase;
        }

        public void setMapUnderscoreToCamelCase(Boolean mapUnderscoreToCamelCase) {
            this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
        }

        public Boolean getAggressiveLazyLoading() {
            return aggressiveLazyLoading;
        }

        public void setAggressiveLazyLoading(Boolean aggressiveLazyLoading) {
            this.aggressiveLazyLoading = aggressiveLazyLoading;
        }

        @DeprecatedConfigurationProperty(since = "3.0.4", reason = "The option is not used at MyBatis core module. It will be removed in the future. See https://github.com/mybatis/mybatis-3/pull/3238")
        public Boolean getMultipleResultSetsEnabled() {
            return multipleResultSetsEnabled;
        }

        public void setMultipleResultSetsEnabled(Boolean multipleResultSetsEnabled) {
            this.multipleResultSetsEnabled = multipleResultSetsEnabled;
        }

        public Boolean getUseGeneratedKeys() {
            return useGeneratedKeys;
        }

        public void setUseGeneratedKeys(Boolean useGeneratedKeys) {
            this.useGeneratedKeys = useGeneratedKeys;
        }

        public Boolean getUseColumnLabel() {
            return useColumnLabel;
        }

        public void setUseColumnLabel(Boolean useColumnLabel) {
            this.useColumnLabel = useColumnLabel;
        }

        public Boolean getCacheEnabled() {
            return cacheEnabled;
        }

        public void setCacheEnabled(Boolean cacheEnabled) {
            this.cacheEnabled = cacheEnabled;
        }

        public Boolean getCallSettersOnNulls() {
            return callSettersOnNulls;
        }

        public void setCallSettersOnNulls(Boolean callSettersOnNulls) {
            this.callSettersOnNulls = callSettersOnNulls;
        }

        public Boolean getUseActualParamName() {
            return useActualParamName;
        }

        public void setUseActualParamName(Boolean useActualParamName) {
            this.useActualParamName = useActualParamName;
        }

        public Boolean getReturnInstanceForEmptyRow() {
            return returnInstanceForEmptyRow;
        }

        public void setReturnInstanceForEmptyRow(Boolean returnInstanceForEmptyRow) {
            this.returnInstanceForEmptyRow = returnInstanceForEmptyRow;
        }

        public Boolean getShrinkWhitespacesInSql() {
            return shrinkWhitespacesInSql;
        }

        public void setShrinkWhitespacesInSql(Boolean shrinkWhitespacesInSql) {
            this.shrinkWhitespacesInSql = shrinkWhitespacesInSql;
        }

        public Boolean getNullableOnForEach() {
            return nullableOnForEach;
        }

        public void setNullableOnForEach(Boolean nullableOnForEach) {
            this.nullableOnForEach = nullableOnForEach;
        }

        public Boolean getArgNameBasedConstructorAutoMapping() {
            return argNameBasedConstructorAutoMapping;
        }

        public void setArgNameBasedConstructorAutoMapping(Boolean argNameBasedConstructorAutoMapping) {
            this.argNameBasedConstructorAutoMapping = argNameBasedConstructorAutoMapping;
        }

        public String getLogPrefix() {
            return logPrefix;
        }

        public void setLogPrefix(String logPrefix) {
            this.logPrefix = logPrefix;
        }

        public Class<? extends Log> getLogImpl() {
            return logImpl;
        }

        public void setLogImpl(Class<? extends Log> logImpl) {
            this.logImpl = logImpl;
        }

        public Class<? extends VFS> getVfsImpl() {
            return vfsImpl;
        }

        public void setVfsImpl(Class<? extends VFS> vfsImpl) {
            this.vfsImpl = vfsImpl;
        }

        public Class<?> getDefaultSqlProviderType() {
            return defaultSqlProviderType;
        }

        public void setDefaultSqlProviderType(Class<?> defaultSqlProviderType) {
            this.defaultSqlProviderType = defaultSqlProviderType;
        }

        public LocalCacheScope getLocalCacheScope() {
            return localCacheScope;
        }

        public void setLocalCacheScope(LocalCacheScope localCacheScope) {
            this.localCacheScope = localCacheScope;
        }

        public JdbcType getJdbcTypeForNull() {
            return jdbcTypeForNull;
        }

        public void setJdbcTypeForNull(JdbcType jdbcTypeForNull) {
            this.jdbcTypeForNull = jdbcTypeForNull;
        }

        public Set<String> getLazyLoadTriggerMethods() {
            return lazyLoadTriggerMethods;
        }

        public void setLazyLoadTriggerMethods(Set<String> lazyLoadTriggerMethods) {
            this.lazyLoadTriggerMethods = lazyLoadTriggerMethods;
        }

        public Integer getDefaultStatementTimeout() {
            return defaultStatementTimeout;
        }

        public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
            this.defaultStatementTimeout = defaultStatementTimeout;
        }

        public Integer getDefaultFetchSize() {
            return defaultFetchSize;
        }

        public void setDefaultFetchSize(Integer defaultFetchSize) {
            this.defaultFetchSize = defaultFetchSize;
        }

        public ResultSetType getDefaultResultSetType() {
            return defaultResultSetType;
        }

        public void setDefaultResultSetType(ResultSetType defaultResultSetType) {
            this.defaultResultSetType = defaultResultSetType;
        }

        public ExecutorType getDefaultExecutorType() {
            return defaultExecutorType;
        }

        public void setDefaultExecutorType(ExecutorType defaultExecutorType) {
            this.defaultExecutorType = defaultExecutorType;
        }

        public AutoMappingBehavior getAutoMappingBehavior() {
            return autoMappingBehavior;
        }

        public void setAutoMappingBehavior(AutoMappingBehavior autoMappingBehavior) {
            this.autoMappingBehavior = autoMappingBehavior;
        }

        public AutoMappingUnknownColumnBehavior getAutoMappingUnknownColumnBehavior() {
            return autoMappingUnknownColumnBehavior;
        }

        public void setAutoMappingUnknownColumnBehavior(
                AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior) {
            this.autoMappingUnknownColumnBehavior = autoMappingUnknownColumnBehavior;
        }

        public Properties getVariables() {
            return variables;
        }

        public void setVariables(Properties variables) {
            this.variables = variables;
        }

        public Boolean getLazyLoadingEnabled() {
            return lazyLoadingEnabled;
        }

        public void setLazyLoadingEnabled(Boolean lazyLoadingEnabled) {
            this.lazyLoadingEnabled = lazyLoadingEnabled;
        }

        public Class<?> getConfigurationFactory() {
            return configurationFactory;
        }

        public void setConfigurationFactory(Class<?> configurationFactory) {
            this.configurationFactory = configurationFactory;
        }

        public Class<? extends TypeHandler> getDefaultEnumTypeHandler() {
            return defaultEnumTypeHandler;
        }

        public void setDefaultEnumTypeHandler(Class<? extends TypeHandler> defaultEnumTypeHandler) {
            this.defaultEnumTypeHandler = defaultEnumTypeHandler;
        }

        public String getDatabaseId() {
            return databaseId;
        }

        public void setDatabaseId(String databaseId) {
            this.databaseId = databaseId;
        }

        public void applyTo(Configuration target) {
            PropertyMapper mapper = PropertyMapper.get();
            mapper.from(getSafeRowBoundsEnabled()).to(target::setSafeRowBoundsEnabled);
            mapper.from(getSafeResultHandlerEnabled()).to(target::setSafeResultHandlerEnabled);
            mapper.from(getMapUnderscoreToCamelCase()).to(target::setMapUnderscoreToCamelCase);
            mapper.from(getAggressiveLazyLoading()).to(target::setAggressiveLazyLoading);
            mapper.from(getMultipleResultSetsEnabled()).to(target::setMultipleResultSetsEnabled);
            mapper.from(getUseGeneratedKeys()).to(target::setUseGeneratedKeys);
            mapper.from(getUseColumnLabel()).to(target::setUseColumnLabel);
            mapper.from(getCacheEnabled()).to(target::setCacheEnabled);
            mapper.from(getCallSettersOnNulls()).to(target::setCallSettersOnNulls);
            mapper.from(getUseActualParamName()).to(target::setUseActualParamName);
            mapper.from(getReturnInstanceForEmptyRow()).to(target::setReturnInstanceForEmptyRow);
            mapper.from(getShrinkWhitespacesInSql()).to(target::setShrinkWhitespacesInSql);
            mapper.from(getNullableOnForEach()).to(target::setNullableOnForEach);
            mapper.from(getArgNameBasedConstructorAutoMapping()).to(target::setArgNameBasedConstructorAutoMapping);
            mapper.from(getLazyLoadingEnabled()).to(target::setLazyLoadingEnabled);
            mapper.from(getLogPrefix()).to(target::setLogPrefix);
            mapper.from(getLazyLoadTriggerMethods()).to(target::setLazyLoadTriggerMethods);
            mapper.from(getDefaultStatementTimeout()).to(target::setDefaultStatementTimeout);
            mapper.from(getDefaultFetchSize()).to(target::setDefaultFetchSize);
            mapper.from(getLocalCacheScope()).to(target::setLocalCacheScope);
            mapper.from(getJdbcTypeForNull()).to(target::setJdbcTypeForNull);
            mapper.from(getDefaultResultSetType()).to(target::setDefaultResultSetType);
            mapper.from(getDefaultExecutorType()).to(target::setDefaultExecutorType);
            mapper.from(getAutoMappingBehavior()).to(target::setAutoMappingBehavior);
            mapper.from(getAutoMappingUnknownColumnBehavior()).to(target::setAutoMappingUnknownColumnBehavior);
            mapper.from(getVariables()).to(target::setVariables);
            mapper.from(getLogImpl()).to(target::setLogImpl);
            mapper.from(getVfsImpl()).to(target::setVfsImpl);
            mapper.from(getDefaultSqlProviderType()).to(target::setDefaultSqlProviderType);
            mapper.from(getConfigurationFactory()).to(target::setConfigurationFactory);
            mapper.from(getDefaultEnumTypeHandler()).to(target::setDefaultEnumTypeHandler);
            mapper.from(getDatabaseId()).to(target::setDatabaseId);
        }
    }
}
