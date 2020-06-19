/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.parser;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Sql Info
 *
 * @author hubin
 * @since 2017-06-20
 */
@Data
@Accessors(chain = true)
public class SqlInfo {

    /**
     * SQL 内容
     */
    private String sql;
    /**
     * 是否排序
     */
    private boolean orderBy;

    /**
     * 使用工厂快速构建 SQLInfo，默认支持排序
     *
     * @param sql SQL 语句
     * @return 返回新的 SQL 信息
     * @see #of(String, boolean)
     */
    public static SqlInfo of(String sql) {
        return of(sql, true);
    }

    /**
     * 使用工厂快速构建 SQLInfo
     *
     * @param sql  sql 语句
     * @param sort 是否排序
     * @return 返回新的 SQLInfo
     */
    public static SqlInfo of(String sql, boolean sort) {
        SqlInfo info = new SqlInfo();
        info.setSql(sql);
        info.setOrderBy(sort);
        return info;
    }

    public static SqlInfo newInstance() {
        return new SqlInfo().setOrderBy(true);
    }

}
