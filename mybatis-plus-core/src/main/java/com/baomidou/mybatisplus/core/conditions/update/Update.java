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
package com.baomidou.mybatisplus.core.conditions.update;

import java.io.Serializable;

/**
 * @author miemie
 * @since 2018-12-12
 */
public interface Update<Children, R> extends Serializable {

    /**
     * 设置 更新 SQL 的 SET 片段
     *
     * @param column 字段
     * @param val    值
     * @return children
     */
    default Children set(R column, Object val) {
        return set(true, column, val);
    }

    /**
     * 设置 更新 SQL 的 SET 片段
     *
     * @param condition 是否加入 set
     * @param column    字段
     * @param val       值
     * @return children
     */
    default Children set(boolean condition, R column, Object val) {
        return set(condition, column, val, null);
    }

    /**
     * 设置 更新 SQL 的 SET 片段
     *
     * @param column  字段
     * @param val     值
     * @param mapping 例: javaType=int,jdbcType=NUMERIC,typeHandler=xxx.xxx.MyTypeHandler
     * @return children
     */
    default Children set(R column, Object val, String mapping) {
        return set(true, column, val, mapping);
    }

    /**
     * 设置 更新 SQL 的 SET 片段
     *
     * @param condition 是否加入 set
     * @param column    字段
     * @param val       值
     * @param mapping   例: javaType=int,jdbcType=NUMERIC,typeHandler=xxx.xxx.MyTypeHandler
     * @return children
     */
    Children set(boolean condition, R column, Object val, String mapping);

    /**
     * 设置 更新 SQL 的 SET 片段
     *
     * @param setSql set sql
     *               例1: setSql("id=1")
     *               例2: setSql("dateColumn={0}", LocalDate.now())
     *               例4: setSql("type={0,javaType=int,jdbcType=NUMERIC,typeHandler=xxx.xxx.MyTypeHandler}", "待处理字符串")
     * @return children
     */
    default Children setSql(String setSql, Object... params) {
        return setSql(true, setSql, params);
    }

    /**
     * 设置 更新 SQL 的 SET 片段
     *
     * @param condition 执行条件
     * @param setSql    set sql
     *                  例1: setSql("id=1")
     *                  例2: setSql("dateColumn={0}", LocalDate.now())
     *                  例4: setSql("type={0,javaType=int,jdbcType=NUMERIC,typeHandler=xxx.xxx.MyTypeHandler}", "待处理字符串")
     * @return children
     */
    Children setSql(boolean condition, String setSql, Object... params);

    /**
     * 字段自增变量 val 值
     *
     * @param column 字段
     * @param val    变量值 1 字段自增 + 1
     */
    default Children setIncrBy(R column, Number val) {
        return setIncrBy(true, column, val);
    }

    /**
     * 字段自增变量 val 值
     *
     * @param condition 是否加入 set
     * @param column    字段
     * @param val       变量值 1 字段自增 + 1
     */
    Children setIncrBy(boolean condition, R column, Number val);

    /**
     * 字段自减变量 val 值
     *
     * @param column 字段
     * @param val    变量值 1 字段自减 - 1
     */
    default Children setDecrBy(R column, Number val) {
        return setDecrBy(true, column, val);
    }

    /**
     * 字段自减变量 val 值
     *
     * @param condition 是否加入 set
     * @param column    字段
     * @param val       变量值 1 字段自减 - 1
     */
    Children setDecrBy(boolean condition, R column, Number val);

    /**
     * 获取 更新 SQL 的 SET 片段
     */
    String getSqlSet();
}
