package com.baomidou.mybatisplus.toolkit;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.mapper.AutoSqlInjector;
import com.baomidou.mybatisplus.mapper.ISqlInjector;
import com.baomidou.mybatisplus.mapper.MetaObjectHandler;

/**
 * <p>
 * Mybatis全局缓存工具类
 * </p>
 *
 * @author Caratacus
 * @since 2017-06-15
 */
public class GlobalConfigUtils {

    // 日志
    private static final Log logger = LogFactory.getLog(GlobalConfigUtils.class);
    /**
     * 默认参数
     */
    public static final GlobalConfiguration DEFAULT = defaults();
    /**
     * 缓存全局信息
     */
    private static final Map<String, GlobalConfiguration> GLOBAL_CONFIG = new ConcurrentHashMap<>();

    public GlobalConfigUtils() {
        // 构造方法
    }

    /**
     * <p>
     * 获取当前的SqlSessionFactory
     * </p>
     *
     * @param clazz 实体类
     * @return
     */
    public static SqlSessionFactory currentSessionFactory(Class<?> clazz) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        if(tableInfo != null){
            String configMark = tableInfo.getConfigMark();
            GlobalConfiguration mybatisGlobalConfig = GlobalConfigUtils.getGlobalConfig(configMark);
            return mybatisGlobalConfig.getSqlSessionFactory();
        }
       throw new MybatisPlusException(ClassUtils.getUserClass(clazz).getName() + " Not Found TableInfoCache.");
    }

    /**
     * <p>
     * 获取默认MybatisGlobalConfig
     * </p>
     *
     * @return
     */
    public static GlobalConfiguration defaults() {
        return new GlobalConfiguration();
    }

    /**
     * <p>
     * 设置全局设置(以configuration地址值作为Key)
     * <p/>
     *
     * @param configuration       Mybatis 容器配置对象
     * @param mybatisGlobalConfig 全局配置
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
     * <p>
     * 获取MybatisGlobalConfig (统一所有入口)
     * </p>
     *
     * @param configuration Mybatis 容器配置对象
     * @return
     */
    public static GlobalConfiguration getGlobalConfig(Configuration configuration) {
        if (configuration == null) {
            throw new MybatisPlusException("Error: You need Initialize MybatisConfiguration !");
        }
        return getGlobalConfig(configuration.toString());
    }

    /**
     * <p>
     * 获取MybatisGlobalConfig (统一所有入口)
     * </p>
     *
     * @param configMark 配置标记
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

    public static MetaObjectHandler getMetaObjectHandler(Configuration configuration) {
        return getGlobalConfig(configuration).getMetaObjectHandler();
    }

    public static FieldStrategy getFieldStrategy(Configuration configuration) {
        return getGlobalConfig(configuration).getFieldStrategy();
    }

    public static boolean isRefresh(Configuration configuration) {
        return getGlobalConfig(configuration).isRefresh();
    }

    public static Set<String> getMapperRegistryCache(Configuration configuration) {
        return getGlobalConfig(configuration).getMapperRegistryCache();
    }

    public static String getIdentifierQuote(Configuration configuration) {
        return getGlobalConfig(configuration).getIdentifierQuote();
    }

    /**
     * <p>
     * 设置元数据相关属性
     * </p>
     *
     * @param dataSource   数据源
     * @param globalConfig 全局配置
     */
    public static void setMetaData(DataSource dataSource, GlobalConfiguration globalConfig) {
        dataSource = AopUtils.getTargetObject(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            String jdbcUrl = connection.getMetaData().getURL();
            // 设置全局关键字
            globalConfig.setSqlKeywords(connection.getMetaData().getSQLKeywords());
            // 自动设置数据库类型
            if (globalConfig.getDbType() == null) {
                globalConfig.setDbTypeOfJdbcUrl(jdbcUrl);
            }
        } catch (Exception e) {
            throw new MybatisPlusException("Error: GlobalConfigUtils setMetaData Fail !  Cause:" + e);
        }
    }

}
