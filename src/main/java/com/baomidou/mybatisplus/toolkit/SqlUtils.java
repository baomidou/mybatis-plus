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

import com.baomidou.mybatisplus.entity.CountOptimize;
import com.baomidou.mybatisplus.enums.Optimize;
import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

/**
 * <p>
 * SqlUtils工具类
 * </p>
 *
 * @author Caratacus
 * @Date 2016-11-13
 */
public class SqlUtils {

    public static final String SQL_BASE_COUNT = "SELECT COUNT(1) FROM ( %s ) TOTAL";
    private final static SqlFormatter sqlFormatter = new SqlFormatter();

    /**
     * 获取CountOptimize
     *
     * @param originalSql
     *            需要计算Count SQL
     * @param optimizeType
     *            count优化方式
     * @param isOptimizeCount
     *            是否需要优化Count
     * @return CountOptimize
     */
    public static CountOptimize getCountOptimize(String originalSql, String optimizeType, String dialectType,
                                                 boolean isOptimizeCount) {
        CountOptimize countOptimize = CountOptimize.newInstance();
        // 获取优化类型
        Optimize opType = Optimize.getOptimizeType(optimizeType);
        // 调整SQL便于解析
        String tempSql = originalSql.replaceAll("(?i)ORDER[\\s]+BY", "ORDER BY").replaceAll("(?i)GROUP[\\s]+BY", "GROUP BY");
        String indexOfSql = tempSql.toUpperCase();
        // 有排序情况
        int orderByIndex = indexOfSql.lastIndexOf("ORDER BY");
        // 只针对 ALI_DRUID DEFAULT 这2种情况
        if (orderByIndex > -1) {
            countOptimize.setOrderBy(false);
        }
        if (!isOptimizeCount && opType.equals(Optimize.DEFAULT)) {
            countOptimize.setCountSQL(String.format(SQL_BASE_COUNT, originalSql));
            return countOptimize;
        }

        switch (opType) {
            case ALI_DRUID:
                /**
                 * 调用ali druid方式 插件dbType一定要设置为小写与JdbcConstants保持一致
                 *
                 * @see com.alibaba.druid.util.JdbcConstants
                 */
                String aliCountSql = DruidUtils.count(originalSql, dialectType);
                countOptimize.setCountSQL(aliCountSql);
                break;
            case JSQLPARSER:
                /**
                 * 调用JsqlParser方式
                 */
                JsqlParserUtils.jsqlparserCount(countOptimize, originalSql);
                break;
            default:
                StringBuilder countSql = new StringBuilder("SELECT COUNT(1) ");
                boolean optimize = false;
                if (!indexOfSql.contains("DISTINCT") && !indexOfSql.contains("GROUP BY")) {
                    int formIndex = indexOfSql.indexOf("FROM");
                    if (formIndex > -1) {
                        if (orderByIndex > -1) {
                            tempSql = tempSql.substring(0, orderByIndex);
                            countSql.append(tempSql.substring(formIndex));
                            // 无排序情况
                        } else {
                            countSql.append(tempSql.substring(formIndex));
                        }
                        // 执行优化
                        optimize = true;
                    }
                }
                if (!optimize) {
                    // 无优化SQL
                    countSql.append("FROM ( ").append(originalSql).append(" ) TOTAL");
                }
                countOptimize.setCountSQL(countSql.toString());
        }

        return countOptimize;
    }

    /**
     * 查询SQL拼接Order By
     *
     * @param originalSql
     *            需要拼接的SQL
     * @param page
     *            page对象
     * @param orderBy
     *            是否需要拼接Order By
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
     * @param str
     *            原字符串
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
