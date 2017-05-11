/**
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
package com.baomidou.mybatisplus.entity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.sql.DataSource;

import com.baomidou.mybatisplus.mapper.IKeyGenerator;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.MybatisSqlSessionTemplate;
import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.mapper.AutoSqlInjector;
import com.baomidou.mybatisplus.mapper.ISqlInjector;
import com.baomidou.mybatisplus.mapper.MetaObjectHandler;
import com.baomidou.mybatisplus.toolkit.IOUtils;
import com.baomidou.mybatisplus.toolkit.JdbcUtils;
import com.baomidou.mybatisplus.toolkit.SqlReservedWords;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;

/**
 * <p>
 * Mybatis全局缓存
 * </p>
 *
 * @author Caratacus
 * @since 2016-12-06
 */
public class GlobalConfiguration implements Cloneable {

    /**
     * 默认参数
     */
    public static final GlobalConfiguration DEFAULT = new GlobalConfiguration();
    // 日志
    private static final Log logger = LogFactory.getLog(GlobalConfiguration.class);
    /**
     * 缓存全局信息
     */
    private static final Map<String, GlobalConfiguration> GLOBAL_CONFIG = new ConcurrentHashMap<>();
    // 逻辑删除全局值
    private String logicDeleteValue = null;
    // 逻辑未删除全局值
    private String logicNotDeleteValue = null;
    // 数据库类型（默认 MySql）
    private DBType dbType = DBType.MYSQL;
    // 主键类型（默认 ID_WORKER）
    private IdType idType = IdType.ID_WORKER;
    // 表名、字段名、是否使用下划线命名（默认 false）
    private boolean dbColumnUnderline = false;
    // SQL注入器
    private ISqlInjector sqlInjector;
    // 表关键词 key 生成器
    private IKeyGenerator keyGenerator;
    // 元对象字段填充控制器
    private MetaObjectHandler metaObjectHandler = new DefaultMetaObjectHandler();
    // 字段验证策略
    private FieldStrategy fieldStrategy = FieldStrategy.NOT_NULL;
    // 是否刷新mapper
    private boolean isRefresh = false;
    // 是否自动获取DBType
    private boolean isAutoSetDbType = true;
    // 是否大写命名
    private boolean isCapitalMode = false;
    // 标识符
    private String identifierQuote;
    // 缓存当前Configuration的SqlSessionFactory
    private SqlSessionFactory sqlSessionFactory;
    // 缓存已注入CRUD的Mapper信息
    private Set<String> mapperRegistryCache = new ConcurrentSkipListSet<>();
    // 单例重用SqlSession
    private SqlSession sqlSession;

    public GlobalConfiguration() {
        // 构造方法
    }

    public GlobalConfiguration(ISqlInjector sqlInjector) {
        this.sqlInjector = sqlInjector;
    }

    /**
     * 获取当前的SqlSessionFactory
     *
     * @param clazz
     * @return
     */
    public static SqlSessionFactory currentSessionFactory(Class<?> clazz) {
        String configMark = TableInfoHelper.getTableInfo(clazz).getConfigMark();
        GlobalConfiguration mybatisGlobalConfig = GlobalConfiguration.getGlobalConfig(configMark);
        return mybatisGlobalConfig.getSqlSessionFactory();
    }

    /**
     * 获取默认MybatisGlobalConfig
     *
     * @return
     */
    public static GlobalConfiguration defaults() {
        try {
            GlobalConfiguration clone = DEFAULT.clone();
            clone.setSqlInjector(new AutoSqlInjector());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new MybatisPlusException("ERROR: CLONE MybatisGlobalConfig DEFAULT FAIL !  Cause:" + e);
        }
    }

    /**
     * <p>
     * 设置全局设置(以configuration地址值作为Key)
     * <p/>
     *
     * @param configuration
     * @param mybatisGlobalConfig
     * @return
     */
    public static void setGlobalConfig(Configuration configuration, GlobalConfiguration mybatisGlobalConfig) {
        if (configuration == null || mybatisGlobalConfig == null) {
            throw new MybatisPlusException("Error: Could not setGlobalConfig");
        }
        // 设置全局设置
        GLOBAL_CONFIG.put(configuration.toString(), mybatisGlobalConfig);
    }

    /**
     * 获取MybatisGlobalConfig (统一所有入口)
     *
     * @param configuration
     * @return
     */
    public static GlobalConfiguration getGlobalConfig(Configuration configuration) {
        if (configuration == null) {
            throw new MybatisPlusException("Error: You need Initialize MybatisConfiguration !");
        }
        return getGlobalConfig(configuration.toString());
    }

    /**
     * 获取MybatisGlobalConfig (统一所有入口)
     *
     * @param configMark
     * @return
     */
    public static GlobalConfiguration getGlobalConfig(String configMark) {
        GlobalConfiguration cache = GLOBAL_CONFIG.get(configMark);
        if (cache == null) {
            // 没有获取全局配置初始全局配置
            logger.debug("DeBug: MyBatis Plus Global configuration Initializing !");
            GLOBAL_CONFIG.put(configMark, DEFAULT);
            return DEFAULT;
        }
        return cache;
    }

    public static DBType getDbType(Configuration configuration) {
        return getGlobalConfig(configuration).getDbType();
    }

    public static IKeyGenerator getKeyGenerator(Configuration configuration) {
        return getGlobalConfig(configuration).getKeyGenerator();
    }

    public static IdType getIdType(Configuration configuration) {
        return getGlobalConfig(configuration).getIdType();
    }

    public static boolean isDbColumnUnderline(Configuration configuration) {
        return getGlobalConfig(configuration).isDbColumnUnderline();
    }

    public static ISqlInjector getSqlInjector(Configuration configuration) {
        // fix #140
        GlobalConfiguration globalConfiguration = getGlobalConfig(configuration);
        ISqlInjector sqlInjector = globalConfiguration.getSqlInjector();
        if (sqlInjector == null) {
            sqlInjector = new AutoSqlInjector();
            globalConfiguration.setSqlInjector(sqlInjector);
        }
        return sqlInjector;
    }

    public IKeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public void setKeyGenerator(IKeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    public static MetaObjectHandler getMetaObjectHandler(Configuration configuration) {
        return getGlobalConfig(configuration).getMetaObjectHandler();
    }

    public static FieldStrategy getFieldStrategy(Configuration configuration) {
        return getGlobalConfig(configuration).getFieldStrategy();
    }

    public static boolean isRefresh(Configuration configuration) {
        return getGlobalConfig(configuration).isRefresh();
    }

    public static boolean isAutoSetDbType(Configuration configuration) {
        return getGlobalConfig(configuration).isAutoSetDbType();
    }

    public static Set<String> getMapperRegistryCache(Configuration configuration) {
        return getGlobalConfig(configuration).getMapperRegistryCache();
    }

    public static String getIdentifierQuote(Configuration configuration) {
        return getGlobalConfig(configuration).getIdentifierQuote();
    }

    public static SqlSession getSqlSession(Configuration configuration) {
        return getGlobalConfig(configuration).getSqlSession();
    }

    /**
     * 设置元数据相关属性
     *
     * @param dataSource
     * @param globalConfig
     */
    public static void setMetaData(DataSource dataSource, GlobalConfiguration globalConfig) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            String jdbcUrl = connection.getMetaData().getURL();
            // 设置全局关键字
            globalConfig.setSqlKeywords(connection.getMetaData().getSQLKeywords());
            // 自动设置数据库类型
            if (globalConfig.isAutoSetDbType()) {
                globalConfig.setDbTypeByJdbcUrl(jdbcUrl);
            }
        } catch (SQLException e) {
            logger.warn("Warn: GlobalConfiguration setMetaData Fail !  Cause:" + e);
        } finally {
            IOUtils.closeQuietly(connection);
        }
    }

    public String getLogicDeleteValue() {
        return logicDeleteValue;
    }

    public void setLogicDeleteValue(String logicDeleteValue) {
        this.logicDeleteValue = logicDeleteValue;
    }

    public String getLogicNotDeleteValue() {
        return logicNotDeleteValue;
    }

    public void setLogicNotDeleteValue(String logicNotDeleteValue) {
        this.logicNotDeleteValue = logicNotDeleteValue;
    }

    public DBType getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = DBType.getDBType(dbType);
        this.isAutoSetDbType = false;
    }

    public void setDbTypeByJdbcUrl(String jdbcUrl) {
        this.dbType = JdbcUtils.getDbType(jdbcUrl);
    }

    public IdType getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = IdType.getIdType(idType);
    }

    public boolean isDbColumnUnderline() {
        return dbColumnUnderline;
    }

    public void setDbColumnUnderline(boolean dbColumnUnderline) {
        this.dbColumnUnderline = dbColumnUnderline;
    }

    public ISqlInjector getSqlInjector() {
        return sqlInjector;
    }

    public void setSqlInjector(ISqlInjector sqlInjector) {
        this.sqlInjector = sqlInjector;
    }

    public MetaObjectHandler getMetaObjectHandler() {
        return metaObjectHandler;
    }

    public void setMetaObjectHandler(MetaObjectHandler metaObjectHandler) {
        this.metaObjectHandler = metaObjectHandler;
    }

    public FieldStrategy getFieldStrategy() {
        return fieldStrategy;
    }

    public void setFieldStrategy(int fieldStrategy) {
        this.fieldStrategy = FieldStrategy.getFieldStrategy(fieldStrategy);
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        this.isRefresh = refresh;
    }

    public boolean isAutoSetDbType() {
        return isAutoSetDbType;
    }

    public void setAutoSetDbType(boolean autoSetDbType) {
        this.isAutoSetDbType = autoSetDbType;
    }

    public Set<String> getMapperRegistryCache() {
        return mapperRegistryCache;
    }

    public void setMapperRegistryCache(Set<String> mapperRegistryCache) {
        this.mapperRegistryCache = mapperRegistryCache;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.sqlSession = new MybatisSqlSessionTemplate(sqlSessionFactory);
    }

    public boolean isCapitalMode() {
        return isCapitalMode;
    }

    public void setCapitalMode(boolean isCapitalMode) {
        this.isCapitalMode = isCapitalMode;
    }

    public String getIdentifierQuote() {
        if (null == identifierQuote) {
            return dbType.getQuote();
        }
        return identifierQuote;
    }

    public void setIdentifierQuote(String identifierQuote) {
        this.identifierQuote = identifierQuote;
    }

    public void setSqlKeywords(String sqlKeywords) {
        if (StringUtils.isNotEmpty(sqlKeywords)) {
            SqlReservedWords.RESERVED_WORDS.addAll(StringUtils.splitWorker(sqlKeywords.toUpperCase(), ",", -1, false));
        }
    }

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    @Override
    protected GlobalConfiguration clone() throws CloneNotSupportedException {
        return (GlobalConfiguration) super.clone();
    }

    /**
     * <p>
     * 标记全局设置 (统一所有入口)
     * </p>
     *
     * @param sqlSessionFactory
     * @return
     */
    public SqlSessionFactory signGlobalConfig(SqlSessionFactory sqlSessionFactory) {
        if (null != sqlSessionFactory) {
            setGlobalConfig(sqlSessionFactory.getConfiguration(), this);
        }
        return sqlSessionFactory;
    }
}
