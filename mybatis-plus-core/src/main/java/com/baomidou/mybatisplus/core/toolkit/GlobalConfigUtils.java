package com.baomidou.mybatisplus.core.toolkit;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.DbConfig;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;

/**
 * <p>
 * Mybatis全局缓存工具类
 * </p>
 *
 * @author Caratacus
 * @since 2017-06-15
 */
public class GlobalConfigUtils {

    /**
     * 日志
     */
    private static final Log logger = LogFactory.getLog(GlobalConfigUtils.class);
    /**
     * 默认参数
     */
    public static final GlobalConfig DEFAULT = defaults();
    /**
     * 缓存全局信息
     */
    private static final Map<String, GlobalConfig> GLOBAL_CONFIG = new ConcurrentHashMap<>();

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
        String configMark = TableInfoHelper.getTableInfo(clazz).getConfigMark();
        GlobalConfig mybatisGlobalConfig = GlobalConfigUtils.getGlobalConfig(configMark);
        return mybatisGlobalConfig.getSqlSessionFactory();
    }

    /**
     * <p>
     * 获取默认MybatisGlobalConfig
     * </p>
     *
     * @return
     */
    public static GlobalConfig defaults() {
        GlobalConfig config = new GlobalConfig();
        config.setDbConfig(new DbConfig());
        return config;
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
    public static void setGlobalConfig(Configuration configuration, GlobalConfig mybatisGlobalConfig) {
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
    public static GlobalConfig getGlobalConfig(Configuration configuration) {
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
    public static GlobalConfig getGlobalConfig(String configMark) {
        GlobalConfig cache = GLOBAL_CONFIG.get(configMark);
        if (cache == null) {
            // 没有获取全局配置初始全局配置
            logger.debug("DeBug: MyBatis Plus Global configuration Initializing !");
            GLOBAL_CONFIG.put(configMark, DEFAULT);
            return DEFAULT;
        }
        return cache;
    }

    public static DbType getDbType(Configuration configuration) {
        return getGlobalConfig(configuration).getDbConfig().getDbType();
    }

    public static IKeyGenerator getKeyGenerator(Configuration configuration) {
        return getGlobalConfig(configuration).getDbConfig().getKeyGenerator();
    }

    public static IdType getIdType(Configuration configuration) {
        return getGlobalConfig(configuration).getDbConfig().getIdType();
    }

    public static boolean isDbColumnUnderline(Configuration configuration) {
        return getGlobalConfig(configuration).getDbConfig().isColumnUnderline();
    }

    public static ISqlInjector getSqlInjector(Configuration configuration) {
        // fix #140
        GlobalConfig globalConfiguration = getGlobalConfig(configuration);
        ISqlInjector sqlInjector = globalConfiguration.getSqlInjector();
        if (sqlInjector == null) {
            sqlInjector = new DefaultSqlInjector();
            globalConfiguration.setSqlInjector(sqlInjector);
        }
        return sqlInjector;
    }

    public static MetaObjectHandler getMetaObjectHandler(Configuration configuration) {
        return getGlobalConfig(configuration).getMetaObjectHandler();
    }

    public static boolean isRefresh(Configuration configuration) {
        return getGlobalConfig(configuration).isRefresh();
    }

    public static Set<String> getMapperRegistryCache(Configuration configuration) {
        return getGlobalConfig(configuration).getMapperRegistryCache();
    }

    public static SqlSession getSqlSession(Configuration configuration) {
        return getGlobalConfig(configuration).getSqlSession();
    }
}
