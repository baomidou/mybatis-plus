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
package com.baomidou.mybatisplus.mapper;

import com.baomidou.mybatisplus.MybatisAbstractSQL;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 实现AbstractSQL ，实现WHERE条件自定义
 * </p>
 *
 * @author yanghu , Caratacus
 * @Date 2016-08-22
 */
@SuppressWarnings("serial")
public class SqlPlus extends MybatisAbstractSQL<SqlPlus> {

    private final String IS_NOT_NULL = " IS NOT NULL";
    private final String IS_NULL = " IS NULL";

    @Override
    public SqlPlus getSelf() {
        return this;
    }

    /**
     * IS NOT NULL查询
     *
     * @param columns 以逗号分隔的字段名称
     * @return this
     */
    public SqlPlus IS_NOT_NULL(String columns) {
        handerNull(columns, IS_NOT_NULL);
        return this;
    }

    /**
     * IS NULL查询
     *
     * @param columns 以逗号分隔的字段名称
     * @return
     */
    public SqlPlus IS_NULL(String columns) {
        handerNull(columns, IS_NULL);
        return this;
    }

    /**
     * 将EXISTS语句添加到WHERE条件中
     *
     * @param value
     * @return
     */
    public SqlPlus EXISTS(String value) {
        handerExists(value, false);
        return this;
    }

    /**
     * 处理EXISTS操作
     *
     * @param value
     * @param isNot 是否为NOT EXISTS操作
     */
    private void handerExists(String value, boolean isNot) {
        if (StringUtils.isNotEmpty(value)) {
            StringBuilder inSql = new StringBuilder();
            if (isNot) {
                inSql.append(" NOT");
            }
            inSql.append(" EXISTS (").append(value).append(")");
            WHERE(inSql.toString());
        }
    }

    /**
     * 将NOT_EXISTS语句添加到WHERE条件中
     *
     * @param value
     * @return
     */
    public SqlPlus NOT_EXISTS(String value) {
        handerExists(value, true);
        return this;
    }

    /**
     * 以相同的方式处理null和notnull
     *
     * @param columns 以逗号分隔的字段名称
     * @param sqlPart SQL部分
     */
    private void handerNull(String columns, String sqlPart) {
        if (StringUtils.isNotEmpty(columns)) {
            String[] cols = columns.split(",");
            for (String col : cols) {
                if (StringUtils.isNotEmpty(col.trim())) {
                    WHERE(col + sqlPart);
                }
            }
        }
    }
}
