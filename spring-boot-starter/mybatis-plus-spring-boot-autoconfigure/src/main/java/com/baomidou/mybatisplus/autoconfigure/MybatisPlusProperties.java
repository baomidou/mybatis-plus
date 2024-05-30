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
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.AutoMappingUnknownColumnBehavior;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
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
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = Constants.MYBATIS_PLUS)
public class MybatisPlusProperties {

    private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    /**
     * Location of MyBatis xml config file.
     */
    private String configLocation;

    /**
     * Locations of MyBatis mapper files.
     *
     * @since 3.1.2 add default value
     */
    private String[] mapperLocations = new String[]{"classpath*:/mapper/**/*.xml"};

    /**
     * Packages to search type aliases. (Package delimiters are ",; \t\n")
     */
    private String typeAliasesPackage;

    /**
     * The super class for filtering type alias.
     * If this not specifies, the MyBatis deal as type alias all classes that searched from typeAliasesPackage.
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
     * <p>
     * 如果设置了这个,你会至少失去几乎所有 mp 提供的功能
     */
    private Class<? extends LanguageDriver> defaultScriptingLanguageDriver;

    /**
     * Externalized properties for MyBatis configuration.
     */
    private Properties configurationProperties;

    /**
     * A Configuration object for customize default settings. If {@link #configLocation}
     * is specified, this property is not used.
     */
    private CoreConfiguration configuration;

    /**
     * 全局配置
     */
    @NestedConfigurationProperty
    private GlobalConfig globalConfig = GlobalConfigUtils.defaults();


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
     * 虽然为高版本新增开始,但为了美化配置提示,这里也在SpringBoot2上使用.
     *
     * @since 3.0.0
     */
    @Getter
    @Setter
    public static class CoreConfiguration {

        /**
         * Allows using RowBounds on nested statements. If allowed, set the false. Default is false.
         */
        private Boolean safeRowBoundsEnabled;

        /**
         * Allows using ResultHandler on nested statements. If allowed, set the false. Default is true.
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
         * Sets the driver a hint as to control fetching size for return results. This parameter value can be overridden by a
         * query setting.
         */
        private Integer defaultFetchSize;

        /**
         * MyBatis uses local cache to prevent circular references and speed up repeated nested queries. By default,
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
         * will be discovered.
         */
        private Class<? extends Log> logImpl;

        /**
         * Specifies VFS implementations.
         */
        private Class<? extends VFS> vfsImpl;

        /**
         * Specifies a sql provider class that holds provider method. This class apply to the type(or value) attribute on
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

        // 新增兼容开始... mybatis 3.x 的有做属性删减  部分属性在2.x可用 3.x 已经被剔除了.
        /**
         * Specifies the language used by default for dynamic SQL generation.
         */
        private Class<? extends LanguageDriver> defaultScriptingLanguageDriver;

        private Boolean useGeneratedShortKey;

        public void applyTo(MybatisConfiguration target) {
            PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
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
            mapper.from(getDefaultScriptingLanguageDriver()).to(target::setDefaultScriptingLanguage);
            mapper.from(getDatabaseId()).to(target::setDatabaseId);
            mapper.from(getUseGeneratedShortKey()).to(target::setUseGeneratedShortKey);
        }
    }

}
