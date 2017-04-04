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

/**
 * <p>
 * ORACLE 数据库分页语句组装实现
 * </p>
 *
 * @author hubin
 * @Date 2016-01-23
 */
public class OracleDialect implements IDialect {

    public static final OracleDialect INSTANCE = new OracleDialect();

    public String buildPaginationSql(String originalSql, int offset, int limit) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ( SELECT TMP.*, ROWNUM ROW_ID FROM ( ");
        sql.append(originalSql).append(" ) TMP WHERE ROWNUM <=").append((offset >= 1) ? (offset + limit) : limit);
        sql.append(") WHERE ROW_ID > ").append(offset);
        return sql.toString();
    }
}
