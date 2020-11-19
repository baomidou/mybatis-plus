/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mybatis全局缓存工具类
 *
 * @author Caratacus
 * @since 2017-06-15
 */
public class GlobalConfigUtils {

    /**
     * 缓存全局信息
     */
    private static final Map<String, GlobalConfig> GLOBAL_CONFIG = new ConcurrentHashMap<>();

    /**
     * 获取当前的SqlSessionFactory
     *
     * @param clazz 实体类
     */
    public static SqlSessionFactory currentSessionFactory(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        Assert.notNull(tableInfo, ClassUtils.getUserClass(clazz).getName() + " Not Found TableInfoCache.");
        return getGlobalConfig(tableInfo.getConfiguration()).getSqlSessionFactory();
    }

    /**
     * 获取默认 MybatisGlobalConfig
     */
    public static GlobalConfig defaults() {
        return new GlobalConfig().setDbConfig(new GlobalConfig.DbConfig());
    }

    /**
     * <p>
     * 设置全局设置(以configuration地址值作为Key)
     * <p/>
     *
     * @param configuration Mybatis 容器配置对象
     * @param globalConfig  全局配置
     */
    public static void setGlobalConfig(Configuration configuration, GlobalConfig globalConfig) {
        Assert.isTrue(configuration != null && globalConfig != null, "Error: Could not setGlobalConfig");
        // 设置全局设置
        GLOBAL_CONFIG.putIfAbsent(Integer.toHexString(configuration.hashCode()), globalConfig);
    }

    /**
     * 获取MybatisGlobalConfig (统一所有入口)
     *
     * @param configuration Mybatis 容器配置对象
     */
    public static GlobalConfig getGlobalConfig(Configuration configuration) {
        Assert.notNull(configuration, "Error: You need Initialize MybatisConfiguration !");
        final String key = Integer.toHexString(configuration.hashCode());
        return CollectionUtils.computeIfAbsent(GLOBAL_CONFIG, key, k -> defaults());
    }

    public static IKeyGenerator getKeyGenerator(Configuration configuration) {
        return getGlobalConfig(configuration).getDbConfig().getKeyGenerator();
    }

    public static IdType getIdType(Configuration configuration) {
        return getGlobalConfig(configuration).getDbConfig().getIdType();
    }

    public static GlobalConfig.DbConfig getDbConfig(Configuration configuration) {
        return getGlobalConfig(configuration).getDbConfig();
    }

    public static ISqlInjector getSqlInjector(Configuration configuration) {
        return getGlobalConfig(configuration).getSqlInjector();
    }

    public static Optional<MetaObjectHandler> getMetaObjectHandler(Configuration configuration) {
        return Optional.ofNullable(getGlobalConfig(configuration).getMetaObjectHandler());
    }

    public static Class<?> getSuperMapperClass(Configuration configuration) {
        return getGlobalConfig(configuration).getSuperMapperClass();
    }

    public static boolean isSupperMapperChildren(Configuration configuration, Class<?> mapperClass) {
        return getSuperMapperClass(configuration).isAssignableFrom(mapperClass);
    }

    public static Set<String> getMapperRegistryCache(Configuration configuration) {
        return getGlobalConfig(configuration).getMapperRegistryCache();
    }
}
