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
package com.baomidou.mybatisplus.plugins.pagination;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.RowBounds;

import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.plugins.pagination.dialects.DB2Dialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.H2Dialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.HSQLDialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.MariaDBDialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.MySqlDialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.OracleDialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.PostgreDialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.SQLServer2005Dialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.SQLServerDialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.SQLiteDialect;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 分页方言工厂类
 * </p>
 *
 * @author hubin
 * @Date 2016-01-23
 */
public class DialectFactory {

    /**
     * 方言缓存
     */
    private static final Map<String, IDialect> dialectCache = new ConcurrentHashMap<>();


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
    public static String buildPaginationSql(Pagination page, String buildSql, DBType dbType, String dialectClazz)
        throws Exception {
        // fix #172, 196
        return getDialect(dbType, dialectClazz).buildPaginationSql(buildSql, PageHelper.offsetCurrent(page), page.getSize());
    }

    /**
     * Physical Pagination Interceptor for all the queries with parameter
     * {@link org.apache.ibatis.session.RowBounds}
     *
     * @param rowBounds
     * @param buildSql
     * @param dbType
     * @param dialectClazz
     * @return
     * @throws Exception
     */
    public static String buildPaginationSql(RowBounds rowBounds, String buildSql, DBType dbType, String dialectClazz)
        throws Exception {
        // fix #196
        return getDialect(dbType, dialectClazz).buildPaginationSql(buildSql, rowBounds.getOffset(), rowBounds.getLimit());
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
    private static IDialect getDialect(DBType dbType, String dialectClazz) throws Exception {
        IDialect dialect = dialectCache.get(dbType.getDb());
        if (null == dialect) {
            // 缓存方言
            if (StringUtils.isNotEmpty(dialectClazz)) {
                // 自定义方言
                dialect = dialectCache.get(dialectClazz);
                if (null != dialect) {
                    return dialect;
                }
                try {
                    Class<?> clazz = Class.forName(dialectClazz);
                    if (IDialect.class.isAssignableFrom(clazz)) {
                        dialect = (IDialect) clazz.newInstance();
                        dialectCache.put(dialectClazz, dialect);
                    }
                } catch (ClassNotFoundException e) {
                    throw new MybatisPlusException("Class :" + dialectClazz + " is not found");
                }
            } else {
                dialect = getDialectByDbType(dbType);
                dialectCache.put(dbType.getDb(), dialect);
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
    private static IDialect getDialectByDbType(DBType dbType) {
        if (dbType == DBType.MYSQL) {
            return new MySqlDialect();
        }
        if (dbType == DBType.MARIADB) {
            return new MariaDBDialect();
        }
        if (dbType == DBType.ORACLE) {
            return new OracleDialect();
        }
        if (dbType == DBType.DB2) {
            return new DB2Dialect();
        }
        if (dbType == DBType.H2) {
            return new H2Dialect();
        }
        if (dbType == DBType.SQLSERVER) {
            return new SQLServerDialect();
        }
        if (dbType == DBType.SQLSERVER2005) {
            return new SQLServer2005Dialect();
        }
        if (dbType == DBType.POSTGRE) {
            return new PostgreDialect();
        }
        if (dbType == DBType.HSQL) {
            return new HSQLDialect();
        }
        if (dbType == DBType.SQLITE) {
            return new SQLiteDialect();
        }
        throw new MybatisPlusException("The Database's Not Supported! DBType:" + dbType);
    }

}
