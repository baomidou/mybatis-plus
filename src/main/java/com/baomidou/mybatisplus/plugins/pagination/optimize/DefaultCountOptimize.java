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
package com.baomidou.mybatisplus.plugins.pagination.optimize;

import com.baomidou.mybatisplus.parser.AbstractSqlParser;
import com.baomidou.mybatisplus.parser.SqlInfo;

/**
 * <p>
 * Default Count Optimize
 * </p>
 *
 * @author hubin
 * @Date 2017-06-20
 */
public class DefaultCountOptimize extends AbstractSqlParser {

    public DefaultCountOptimize(String sql, String dbType) {
        super(sql, dbType);
    }

    @Override
    public SqlInfo optimizeSql() {
        String sql = this.getSql();
        String dbType = this.getDbType();
        if (logger.isDebugEnabled()) {
            logger.debug(" DefaultCountOptimize sql=" + sql + ", dbType=" + dbType);
        }
        SqlInfo sqlInfo = SqlInfo.newInstance();
        // 调整SQL便于解析
        String tempSql = sql.replaceAll("(?i)ORDER[\\s]+BY", "ORDER BY").replaceAll("(?i)GROUP[\\s]+BY", "GROUP BY");
        String indexOfSql = tempSql.toUpperCase();
        // 有排序情况
        int orderByIndex = indexOfSql.lastIndexOf("ORDER BY");
        // 只针对 ALI_DRUID DEFAULT 这2种情况
        if (orderByIndex > -1) {
            sqlInfo.setOrderBy(false);
        }
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
            countSql.append("FROM ( ").append(sql).append(" ) TOTAL");
        }
        sqlInfo.setSql(countSql.toString());
        return sqlInfo;
    }
}
