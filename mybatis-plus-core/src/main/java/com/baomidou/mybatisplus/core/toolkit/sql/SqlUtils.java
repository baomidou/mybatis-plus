/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.toolkit.sql;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * <p>
 * SqlUtils工具类
 * </p>
 *
 * @author Caratacus
 * @since 2016-11-13
 */
public class SqlUtils {

    private final static SqlFormatter SQL_FORMATTER = new SqlFormatter();


    /**
     * 格式sql
     *
     * @param boundSql
     * @param format
     * @return
     */
    public static String sqlFormat(String boundSql, boolean format) {
        if (format) {
            try {
                return SQL_FORMATTER.format(boundSql);
            } catch (Exception ignored) {
            }
        }
        return boundSql;
    }

    /**
     * <p>
     * 用%连接like
     * </p>
     *
     * @param str 原字符串
     * @return
     */
    public static String concatLike(String str, SqlLike type) {
        StringBuilder builder = new StringBuilder(str.length() + 3);
        switch (type) {
            case LEFT:
                builder.append(StringPool.PERCENT).append(str);
                break;
            case RIGHT:
                builder.append(str).append(StringPool.PERCENT);
                break;
            case CUSTOM:
                builder.append(str);
                break;
            default:
                builder.append(StringPool.PERCENT).append(str).append(StringPool.PERCENT);
        }
        return builder.toString();
    }

    /**
     * <p>
     * 获取需要转义的SQL字段
     * </p>
     *
     * @param dbType   数据库类型
     * @param val      值
     * @param isColumn val 是否是数据库字段
     */
    public static String sqlWordConvert(DbType dbType, String val, boolean isColumn) {
        if (dbType == DbType.POSTGRE_SQL) {
            if (isColumn && (StringUtils.isNotColumnName(val) || val.toLowerCase().equals(val))) {
                // 都是数据库字段的情况下
                // 1.手动加了转义符
                // 2.全小写之后和原值一样
                // 都直接返回
                return val;
            }
            return String.format("\"%s\"", val);
        }
        return val;
    }

    /**
     * <p>
     * SQL注入内容剥离
     * </p>
     *
     * @param sql 待处理 SQL 内容
     * @return this
     */
    public static String stripSqlInjection(String sql) {
        Assert.notNull(sql, "strip sql is null.");
        return sql.replaceAll("('.+--)|(--)|(\\|)|(%7C)", StringPool.EMPTY);
    }
}
