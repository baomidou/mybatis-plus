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
package com.baomidou.mybatisplus.extension.plugins.pagination;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.RowBounds;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.DB2Dialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.H2Dialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.HSQLDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.MariaDBDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.MySqlDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.OracleDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.PostgreDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.SQLServer2005Dialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.SQLServerDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.SQLiteDialect;


/**
 * <p>
 * 分页方言工厂类
 * </p>
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
     * <p>
     * 生成翻页执行 SQL
     * </p>
     *
     * @param page         翻页对象
     * @param buildSql     执行 SQL
     * @param dbType       数据库类型
     * @param dialectClazz 自定义方言实现类
     * @return
     * @throws Exception
     */
    public static String buildPaginationSql(Page page, String buildSql, DbType dbType, String dialectClazz)
        throws Exception {
        // fix #172, 196
        return getDialect(dbType, dialectClazz).buildPaginationSql(buildSql, PageHelper.offsetCurrent(page), page.getSize());
    }

    /**
     * Physical Page Interceptor for all the queries with parameter
     * {@link RowBounds}
     *
     * @param page         翻页对象
     * @param buildSql     编译 SQL
     * @param dbType       数据类型
     * @param dialectClazz 数据库方言
     * @return
     * @throws Exception
     */
    public static String buildPaginationSql(IPage page, String buildSql, DbType dbType, String dialectClazz)
        throws Exception {
        // fix #196
        return getDialect(dbType, dialectClazz).buildPaginationSql(buildSql, page.offset(), page.getSize());
    }

    /**
     * <p>
     * 获取数据库方言
     * </p>
     *
     * @param dbType       数据库类型
     * @param dialectClazz 自定义方言实现类
     * @return
     * @throws Exception
     */
    private static IDialect getDialect(DbType dbType, String dialectClazz) throws Exception {
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
                    throw new MybatisPlusException("Class :" + dialectClazz + " is not found");
                }
            } else {
                // 缓存方言
                dialect = getDialectByDbType(dbType);
                DIALECT_CACHE.put(dbType.getDb(), dialect);
            }
            /* 未配置方言则抛出异常 */
            if (dialect == null) {
                throw new MybatisPlusException("The value of the dialect property in mybatis configuration.xml is not defined.");
            }
        }
        return dialect;
    }

    /**
     * <p>
     * 根据数据库类型选择不同分页方言
     * </p>
     *
     * @param dbType 数据库类型
     * @return
     * @throws Exception
     */
    private static IDialect getDialectByDbType(DbType dbType) {
        if (dbType == DbType.MYSQL) {
            return new MySqlDialect();
        }
        if (dbType == DbType.MARIADB) {
            return new MariaDBDialect();
        }
        if (dbType == DbType.ORACLE) {
            return new OracleDialect();
        }
        if (dbType == DbType.DB2) {
            return new DB2Dialect();
        }
        if (dbType == DbType.H2) {
            return new H2Dialect();
        }
        if (dbType == DbType.SQL_SERVER) {
            return new SQLServerDialect();
        }
        if (dbType == DbType.SQL_SERVER2005) {
            return new SQLServer2005Dialect();
        }
        if (dbType == DbType.POSTGRE_SQL) {
            return new PostgreDialect();
        }
        if (dbType == DbType.HSQL) {
            return new HSQLDialect();
        }
        if (dbType == DbType.SQLITE) {
            return new SQLiteDialect();
        }
        throw new MybatisPlusException("The Database's Not Supported! DBType:" + dbType);
    }

}
