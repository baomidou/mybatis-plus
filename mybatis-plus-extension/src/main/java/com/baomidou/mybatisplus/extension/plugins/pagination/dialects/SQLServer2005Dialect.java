/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQLServer 2005 数据库分页方言
 *
 * @author hubin
 * @since 2016-11-10
 */
public class SQLServer2005Dialect implements IDialect {

    private static final Pattern ORDER_BY_PATTERN = Pattern.compile("\\((.)*order by(.)*\\)");

    private static final Pattern SELECT_PATTERN = Pattern.compile("(?i)select\\s+(distinct\\s+)?");

    public String getOrderByPart(String sql) {
        String order_by = "order by";
        int lastIndex = sql.toLowerCase().lastIndexOf(order_by);
        if (lastIndex == -1) {
            return StringPool.EMPTY;
        }
        Matcher matcher = ORDER_BY_PATTERN.matcher(sql);
        if (!matcher.find()) {
            return sql.substring(lastIndex);
        }
        int end = matcher.end();
        return lastIndex < end ? StringPool.EMPTY : sql.substring(lastIndex);
    }

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        StringBuilder pagingBuilder = new StringBuilder();
        String orderby = getOrderByPart(originalSql);
        String distinctStr = StringPool.EMPTY;
        String sqlPartString = originalSql;
        Matcher matcher = SELECT_PATTERN.matcher(originalSql);
        if (matcher.find()) {
            int index = matcher.end() - 1;
            if (matcher.group().toLowerCase().contains("distinct")) {
                distinctStr = "DISTINCT ";
            }
            sqlPartString = sqlPartString.substring(index);
        }
        pagingBuilder.append(sqlPartString);
        // if no ORDER BY is specified use fake ORDER BY field to avoid errors
        if (StringUtils.isBlank(orderby)) {
            orderby = "ORDER BY CURRENT_TIMESTAMP";
        }
        long firstParam = offset + 1;
        long secondParam = offset + limit;
        String sql = "WITH selectTemp AS (SELECT " + distinctStr + "TOP 100 PERCENT " +
            " ROW_NUMBER() OVER (" + orderby + ") as __row_number__, " + pagingBuilder +
            ") SELECT * FROM selectTemp WHERE __row_number__ BETWEEN " +
            //FIX#299：原因：mysql中limit 10(offset,size) 是从第10开始（不包含10）,；而这里用的BETWEEN是两边都包含，所以改为offset+1
            firstParam + " AND " + secondParam + " ORDER BY __row_number__";
        return new DialectModel(sql);
    }
}
