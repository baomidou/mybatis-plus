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
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.*;
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

    /**
     * 方言缓存
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
     */
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
     */
    private static IDialect getDialect(DbType dbType, String dialectClazz) {
        IDialect dialect = DIALECT_CACHE.get(dbType.getDb());
        if (null == dialect) {
            // 自定义方言
            if (StringUtils.isNotEmpty(dialectClazz)) {
                dialect = DIALECT_CACHE.get(dialectClazz);
                if (null != dialect) {
                    return dialect;
                }
                try {
                    Class<?> clazz = Class.forName(dialectClazz);
                    if (IDialect.class.isAssignableFrom(clazz)) {
                        dialect = (IDialect) clazz.newInstance();
                        DIALECT_CACHE.put(dialectClazz, dialect);
                    }
                } catch (ClassNotFoundException e) {
                    throw ExceptionUtils.mpe("Class : %s is not found", dialectClazz);
                } catch (IllegalAccessException | InstantiationException e) {
                    throw ExceptionUtils.mpe("Class : %s can not be instance", dialectClazz);
                }
            } else {
                // 缓存方言
                dialect = getDialectByDbType(dbType);
                DIALECT_CACHE.put(dbType.getDb(), dialect);
            }
            /* 未配置方言则抛出异常 */
            Assert.notNull(dialect, "The value of the dialect property in mybatis configuration.xml is not defined.");
        }
        return dialect;
    }

    /**
     * 根据数据库类型选择不同分页方言
     *
     * @param dbType 数据库类型
     * @return 分页语句组装类
     */
    private static IDialect getDialectByDbType(DbType dbType) {
        switch (dbType) {
            case MYSQL:
                return new MySqlDialect();
            case MARIADB:
                return new MariaDBDialect();
            case ORACLE:
                return new OracleDialect();
            case DB2:
                return new DB2Dialect();
            case H2:
                return new H2Dialect();
            case SQL_SERVER:
                return new SQLServerDialect();
            case SQL_SERVER2005:
                return new SQLServer2005Dialect();
            case POSTGRE_SQL:
                return new PostgreDialect();
            case HSQL:
                return new HSQLDialect();
            case SQLITE:
                return new SQLiteDialect();
            case DM:
                return new DmDialect();
            default:
                throw ExceptionUtils.mpe("The Database's IDialect Not Supported!");
        }
    }
}
