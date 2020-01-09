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
package com.baomidou.mybatisplus.extension.plugins.pagination;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.DialectRegistry;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import org.apache.ibatis.session.RowBounds;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分页方言工厂类
 *
 * @author hubin
 * @since 2016-01-23
 */
public class DialectFactory {

    private static final DialectRegistry DIALECT_REGISTRY = new DialectRegistry();

    /**
     * 自定义方言缓存
     */
    private static final Map<String, IDialect> DIALECT_CACHE = new ConcurrentHashMap<>();

    /**
     * Physical Page Interceptor for all the queries with parameter
     * {@link RowBounds}
     *
     * @param page         翻页对象
     * @param buildSql     编译 SQL
     * @param dbType       数据类型
     * @param dialectClazz 数据库方言
     * @return 分页模型
     * @deprecated 3.3.1
     */
    @Deprecated
    public static DialectModel buildPaginationSql(IPage<?> page, String buildSql, DbType dbType, String dialectClazz) {
        // fix #196
        return getDialect(dbType, dialectClazz).buildPaginationSql(buildSql, page.offset(), page.getSize());
    }

    /**
     * 获取数据库方言
     *
     * @param dbType       数据库类型
     * @param dialectClazz 自定义方言实现类
     * @return ignore
     * @deprecated 3.3.1 {@link #getDialect(String)}
     */
    @Deprecated
    private static IDialect getDialect(DbType dbType, String dialectClazz) {
        //这里需要注意一下，就的版本是把dbType和dialectClazz同时传进来的，所以会存在dbType是一定会有值，dialectClazz可能为空的情况，兼容需要先判断dialectClazz
        return StringUtils.isBlank(dialectClazz) ? DIALECT_REGISTRY.getDialect(dbType) : DIALECT_CACHE.computeIfAbsent(dialectClazz, DialectFactory::classToDialect);
    }

    /**
     * 获取实现方言
     *
     * @param dialectClazz 方言全类名
     * @return 方言实现对象
     * @since 3.3.1
     */
    public static IDialect getDialect(String dialectClazz) {
        return DIALECT_CACHE.computeIfAbsent(dialectClazz, DialectFactory::classToDialect);
    }

    public static IDialect getDialect(DbType dbType) {
        return DIALECT_REGISTRY.getDialect(dbType);
    }

    private static IDialect newInstance(Class<? extends IDialect> dialectClazz) {
        IDialect dialect = ClassUtils.newInstance(dialectClazz);
        Assert.notNull(dialect, "The value of the dialect property in mybatis configuration.xml is not defined.");
        return dialect;
    }

    @SuppressWarnings("unchecked")
    private static IDialect classToDialect(String dialectClazz){
        IDialect dialect = null;
        try {
            Class<?> clazz = Class.forName(dialectClazz);
            if (IDialect.class.isAssignableFrom(clazz)) {
                dialect = newInstance((Class<? extends IDialect>) clazz);
            }
        } catch (ClassNotFoundException e) {
            throw ExceptionUtils.mpe("Class : %s is not found", dialectClazz);
        }
        return dialect;
    }
}
