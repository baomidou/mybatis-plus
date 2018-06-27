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
package com.baomidou.mybatisplus.core.conditions.interfaces;

import java.io.Serializable;

/**
 * <p>
 * 查询条件封装
 * 拼接
 * </p>
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
public interface Join<This> extends Serializable {

    /**
     * 拼接 OR
     */
    default This or() {
        return or(true);
    }

    /**
     * 拼接 OR
     */
    This or(boolean condition);

    /**
     * 拼接 IN ( sql 语句 )
     * 例: in("1,2,3,4,5,6")
     */
    default This in(String sql) {
        return in(true, sql);
    }

    /**
     * 拼接 IN ( sql 语句 )
     * 例: in("1,2,3,4,5,6")
     */
    This in(boolean condition, String sql);

    /**
     * 拼接 NOT IN ( sql 语句 )
     * 例: notIn("1,2,3,4,5,6")
     */
    default This notIn(String sql) {
        return notIn(true, sql);
    }

    /**
     * 拼接 NOT IN ( sql 语句 )
     * 例: notIn("1,2,3,4,5,6")
     */
    This notIn(boolean condition, String sql);

    /**
     * 拼接 sql
     * 例: apply("date_format(column,'%Y-%m-%d') = '2008-08-08'")
     */
    default This apply(String applySql) {
        return apply(true, applySql);
    }

    /**
     * 拼接 sql
     * 例: apply("date_format(column,'%Y-%m-%d') = '2008-08-08'")
     */
    This apply(boolean condition, String applySql);

    /**
     * 拼接 sql
     * 例: apply("date_format(column,'%Y-%m-%d') = {0}", LocalDate.now())
     */
    default This apply(String applySql, Object... value) {
        return apply(true, applySql, value);
    }

    /**
     * 拼接 sql
     * 例: apply("date_format(column,'%Y-%m-%d') = {0}", LocalDate.now())
     */
    This apply(boolean condition, String applySql, Object... value);

    /**
     * 无视优化规则直接拼接到 sql 的最后
     * 例: last("limit 1")
     */
    default This last(String lastSql) {
        return last(true, lastSql);
    }

    /**
     * 无视优化规则直接拼接到 sql 的最后
     * 例: last("limit 1")
     */
    This last(boolean condition, String lastSql);

    /**
     * EXISTS ( sql 语句 )
     * 例: exists("select id from table where age = 1")
     */
    default This exists(String existsSql) {
        return exists(true, existsSql);
    }

    /**
     * EXISTS ( sql 语句 )
     */
    This exists(boolean condition, String existsSql);

    /**
     * NOT EXISTS ( sql 语句 )
     * 例: notExists("select id from table where age = 1")
     */
    default This notExists(String notExistsSql) {
        return notExists(true, notExistsSql);
    }

    /**
     * NOT EXISTS ( sql 语句 )
     */
    This notExists(boolean condition, String notExistsSql);
}
