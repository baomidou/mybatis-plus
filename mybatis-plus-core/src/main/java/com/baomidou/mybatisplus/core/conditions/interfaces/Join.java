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
     * ignore
     */
    default This or() {
        return or(true);
    }

    /**
     * 拼接 OR
     *
     * @param condition 执行条件
     */
    This or(boolean condition);

    /**
     * ignore
     */
    default This apply(String applySql, Object... value) {
        return apply(true, applySql, value);
    }

    /**
     * !! 会有 sql 注入风险 !!
     * 拼接 sql
     * 例1: apply("id = 1")
     * 例2: apply("date_format(dateColumn,'%Y-%m-%d') = '2008-08-08'")
     * 例3: apply("date_format(dateColumn,'%Y-%m-%d') = {0}", LocalDate.now())
     *
     * @param condition 执行条件
     */
    This apply(boolean condition, String applySql, Object... value);

    /**
     * ignore
     */
    default This last(String lastSql) {
        return last(true, lastSql);
    }

    /**
     * 无视优化规则直接拼接到 sql 的最后(有sql注入的风险,请谨慎使用)
     * 例: last("limit 1")
     * 注意只能调用一次,多次调用以最后一次为准
     *
     * @param condition 执行条件
     * @param lastSql   sql语句
     */
    This last(boolean condition, String lastSql);

    /**
     * ignore
     */
    default This exists(String existsSql) {
        return exists(true, existsSql);
    }

    /**
     * !! sql 注入方法 !!
     * 拼接 EXISTS ( sql语句 )
     * 例: exists("select id from table where age = 1")
     *
     * @param condition 执行条件
     * @param existsSql sql语句
     */
    This exists(boolean condition, String existsSql);

    /**
     * ignore
     */
    default This notExists(String notExistsSql) {
        return notExists(true, notExistsSql);
    }

    /**
     * !! sql 注入方法 !!
     * 拼接 NOT EXISTS ( sql语句 )
     * 例: notExists("select id from table where age = 1")
     *
     * @param condition    执行条件
     * @param notExistsSql sql语句
     */
    This notExists(boolean condition, String notExistsSql);
}
