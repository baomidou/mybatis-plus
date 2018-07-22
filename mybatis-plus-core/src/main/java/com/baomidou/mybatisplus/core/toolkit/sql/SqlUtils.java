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

import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;

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
                builder.append("%").append(str);
                break;
            case RIGHT:
                builder.append(str).append("%");
                break;
            case CUSTOM:
                builder.append(str);
                break;
            default:
                builder.append("%").append(str).append("%");
        }
        return builder.toString();
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
        if (null == sql) {
            throw new MybatisPlusException("strip sql is null.");
        }
        return sql.replaceAll("('.+--)|(--)|(\\|)|(%7C)", "");
    }
}
