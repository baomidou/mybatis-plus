/**
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
package com.baomidou.mybatisplus.toolkit;

import com.baomidou.mybatisplus.enums.Optimize;
import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.baomidou.mybatisplus.plugins.pagination.optimize.AliDruidCountOptimize;
import com.baomidou.mybatisplus.plugins.pagination.optimize.DefaultCountOptimize;
import com.baomidou.mybatisplus.plugins.pagination.optimize.JsqlParserCountOptimize;
import com.baomidou.mybatisplus.parser.AbstractSqlParser;
import com.baomidou.mybatisplus.parser.SqlInfo;

/**
 * <p>
 * SqlUtils工具类
 * </p>
 *
 * @author Caratacus
 * @Date 2016-11-13
 */
public class SqlUtils {

    private final static SqlFormatter sqlFormatter = new SqlFormatter();
    public static final String SQL_BASE_COUNT = "SELECT COUNT(1) FROM ( %s ) TOTAL";

    /**
     * <p>
     * 获取CountOptimize
     * </p>
     *
     * @param sqlParser       Count SQL 解析类
     * @param originalSql     需要计算Count SQL
     * @param optimizeType    count优化方式
     * @param isOptimizeCount 是否需要优化Count
     * @return SqlInfo
     */
    public static SqlInfo getCountOptimize(AbstractSqlParser sqlParser, String originalSql,
                                           String optimizeType, String dialectType,
                                           boolean isOptimizeCount) {
        Optimize opType = Optimize.getOptimizeType(optimizeType);

        // COUNT SQL 不优化
        if (!isOptimizeCount && Optimize.DEFAULT == opType) {
            SqlInfo sqlInfo = SqlInfo.newInstance();
            String tempSql = originalSql.replaceAll("(?i)ORDER[\\s]+BY", "ORDER BY");
            int orderByIndex = tempSql.toUpperCase().lastIndexOf("ORDER BY");
            sqlInfo.setOrderBy(orderByIndex > -1);
            sqlInfo.setSql(String.format(SQL_BASE_COUNT, originalSql));
            return sqlInfo;
        }

        // 用户自定义 COUNT SQL 解析
        if (null != sqlParser) {
            return sqlParser.optimizeSql();
        }

        // 默认存在的优化类型
        switch (opType) {
            case ALI_DRUID:
                sqlParser = new AliDruidCountOptimize(originalSql, dialectType);
                break;
            case JSQLPARSER:
                sqlParser = new JsqlParserCountOptimize(originalSql, dialectType);
                break;
            default:
                sqlParser = new DefaultCountOptimize(originalSql, dialectType);
                break;
        }

        return sqlParser.optimizeSql();
    }

    /**
     * 查询SQL拼接Order By
     *
     * @param originalSql 需要拼接的SQL
     * @param page        page对象
     * @param orderBy     是否需要拼接Order By
     * @return
     */
    public static String concatOrderBy(String originalSql, Pagination page, boolean orderBy) {
        if (orderBy && StringUtils.isNotEmpty(page.getOrderByField()) && page.isOpenSort()) {
            StringBuilder buildSql = new StringBuilder(originalSql);
            buildSql.append(" ORDER BY ").append(page.getOrderByField());
            buildSql.append(page.isAsc() ? " ASC " : " DESC ");
            return buildSql.toString();
        }
        return originalSql;
    }

    /**
     * 格式sql
     *
     * @param boundSql
     * @param format
     * @return
     */
    public static String sqlFormat(String boundSql, boolean format) {
        if (format) {
            return sqlFormatter.format(boundSql);
        } else {
            return boundSql.replaceAll("[\\s]+", " ");
        }
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

}
