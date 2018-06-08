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
package com.baomidou.mybatisplus.core.parser;

/**
 * <p>
 * Sql Info
 * </p>
 *
 * @author hubin
 * @since 2017-06-20
 */
public class SqlInfo {

    /**
     * SQL 内容
     */
    private String sql;
    /**
     * 是否排序
     */
    private boolean orderBy = true;

    public static SqlInfo newInstance() {
        return new SqlInfo();
    }

    public String getSql() {
        return sql;
    }

    public SqlInfo setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public boolean isOrderBy() {
        return orderBy;
    }

    public SqlInfo setOrderBy(boolean orderBy) {
        this.orderBy = orderBy;
        return this;
    }
}
