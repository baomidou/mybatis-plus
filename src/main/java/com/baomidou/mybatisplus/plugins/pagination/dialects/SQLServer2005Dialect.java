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
package com.baomidou.mybatisplus.plugins.pagination.dialects;

import com.baomidou.mybatisplus.plugins.pagination.IDialect;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * SQLServer 2005 数据库分页方言
 * </p>
 *
 * @author hubin
 * @Date 2016-11-10
 */
public class SQLServer2005Dialect implements IDialect {

    public static final SQLServer2005Dialect INSTANCE = new SQLServer2005Dialect();

    private static String getOrderByPart(String sql) {
        String loweredString = sql.toLowerCase();
        int orderByIndex = loweredString.indexOf("order by");
        if (orderByIndex != -1) {
            return sql.substring(orderByIndex);
        } else {
            return "";
        }
    }

    public String buildPaginationSql(String originalSql, int offset, int limit) {
        StringBuilder pagingBuilder = new StringBuilder();
        String orderby = getOrderByPart(originalSql);
        String distinctStr = "";

        String loweredString = originalSql.toLowerCase();
        String sqlPartString = originalSql;
        if (loweredString.trim().startsWith("select")) {
            int index = 6;
            if (loweredString.startsWith("select distinct")) {
                distinctStr = "DISTINCT ";
                index = 15;
            }
            sqlPartString = sqlPartString.substring(index);
        }
        pagingBuilder.append(sqlPartString);

        // if no ORDER BY is specified use fake ORDER BY field to avoid errors
        if (StringUtils.isEmpty(orderby)) {
            orderby = "ORDER BY CURRENT_TIMESTAMP";
        }

        StringBuilder sql = new StringBuilder();
        sql.append("WITH query AS (SELECT ").append(distinctStr).append("TOP 100 PERCENT ")
                .append(" ROW_NUMBER() OVER (").append(orderby).append(") as __row_number__, ").append(pagingBuilder)
                .append(") SELECT * FROM query WHERE __row_number__ BETWEEN ").append(offset).append(" AND ")
                .append(offset + limit).append(" ORDER BY __row_number__");
        return sql.toString();
    }
}
