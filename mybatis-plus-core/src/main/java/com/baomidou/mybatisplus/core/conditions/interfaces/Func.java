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

import com.baomidou.mybatisplus.core.toolkit.Assert;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * <p>
 * 查询条件封装
 * 比较值
 * </p>
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
@SuppressWarnings("unchecked")
public interface Func<Children, R> extends Serializable {

    /**
     * ignore
     */
    default Children isNull(R column) {
        return isNull(true, column);
    }

    /**
     * 字段 IS NULL
     * 例: isNull("name")
     *
     * @param condition 执行条件
     * @param column    字段
     * @return children
     */
    Children isNull(boolean condition, R column);

    /**
     * ignore
     */
    default Children isNotNull(R column) {
        return isNotNull(true, column);
    }

    /**
     * 字段 IS NOT NULL
     * 例: isNotNull("name")
     *
     * @param condition 执行条件
     * @param column    字段
     * @return children
     */
    Children isNotNull(boolean condition, R column);

    /**
     * ignore
     */
    default Children in(R column, Collection<?> coll) {
        return in(true, column, coll);
    }

    /**
     * ignore
     */
    default Children inOrThrow(R column, Collection<?> value) {
        return inOrThrow(true, column, value);
    }

    /**
     * 字段 IN (value.get(0), value.get(1), ...)
     * 例: in("id", Arrays.asList(1,2,3,4,5))
     *
     * <li> 如果集合为 empty 则直接抛出异常 </li>
     *
     * @param condition 执行条件
     * @param column    字段
     * @param coll      数据集合
     * @return children
     */
    default Children inOrThrow(boolean condition, R column, Collection<?> coll) {
        Assert.notEmpty(coll, "coll could not be empty!");
        return in(condition, column, coll);
    }

    /**
     * 字段 IN (value.get(0), value.get(1), ...)
     * 例: in("id", Arrays.asList(1,2,3,4,5))
     *
     * <li> 如果集合为 empty 则不会进行 sql 拼接 </li>
     *
     * @param condition 执行条件
     * @param column    字段
     * @param coll      数据集合
     * @return children
     */
    Children in(boolean condition, R column, Collection<?> coll);

    /**
     * ignore
     */
    default Children inOrThrow(R column, Object... values) {
        return inOrThrow(true, column, values);
    }

    /**
     * 字段 IN (value.get(0), value.get(1), ...)
     * 例: in("id", Arrays.asList(1,2,3,4,5))
     *
     * <li> 如果数组为 empty 则直接抛出异常 </li>
     *
     * @param condition 执行条件
     * @param column    字段
     * @param values    数据数组
     * @return children
     */
    default Children inOrThrow(boolean condition, R column, Object... values) {
        Assert.notEmpty(values, "values could not be empty!");
        return in(condition, column, Arrays.stream(values).collect(toList()));
    }

    /**
     * ignore
     */
    default Children in(R column, Object... values) {
        return in(true, column, values);
    }

    /**
     * 字段 IN (v0, v1, ...)
     * 例: in("id", 1, 2, 3, 4, 5)
     *
     * <li> 如果动态数组为 empty 则不会进行 sql 拼接 </li>
     *
     * @param condition 执行条件
     * @param column    字段
     * @param values    数据数组
     * @return children
     */
    default Children in(boolean condition, R column, Object... values) {
        return in(condition, column, Arrays.stream(Optional.ofNullable(values).orElseGet(() -> new Object[]{}))
            .collect(toList()));
    }

    /**
     * ignore
     */
    default Children notIn(R column, Collection<?> coll) {
        return notIn(true, column, coll);
    }

    /**
     * ignore
     */
    default Children notInOrThrow(R column, Collection<?> value) {
        return notInOrThrow(true, column, value);
    }

    /**
     * 字段 IN (value.get(0), value.get(1), ...)
     * 例: in("id", Arrays.asList(1,2,3,4,5))
     *
     * <li> 如果集合为 empty 则直接抛出异常 </li>
     *
     * @param condition 执行条件
     * @param column    字段
     * @param coll      数据集合
     * @return children
     */
    default Children notInOrThrow(boolean condition, R column, Collection<?> coll) {
        Assert.notEmpty(coll, "coll could not be empty!");
        return notIn(condition, column, coll);
    }

    /**
     * 字段 NOT IN (value.get(0), value.get(1), ...)
     * 例: notIn("id", Arrays.asList(1,2,3,4,5))
     *
     * @param condition 执行条件
     * @param column    字段
     * @param coll      数据集合
     * @return children
     */
    Children notIn(boolean condition, R column, Collection<?> coll);

    /**
     * ignore
     */
    default Children notIn(R column, Object... value) {
        return notIn(true, column, value);
    }

    /**
     * ignore
     */
    default Children notInOrThrow(R column, Object... values) {
        return notInOrThrow(true, column, values);
    }

    /**
     * 字段 IN (value.get(0), value.get(1), ...)
     * 例: in("id", Arrays.asList(1,2,3,4,5))
     *
     * <li> 如果数组为 empty 则直接抛出异常 </li>
     *
     * @param condition 执行条件
     * @param column    字段
     * @param values    数据数组
     * @return children
     */
    default Children notInOrThrow(boolean condition, R column, Object... values) {
        Assert.notEmpty(values, "values could not be empty!");
        return notIn(condition, column, Arrays.stream(values).collect(toList()));
    }

    /**
     * 字段 NOT IN (v0, v1, ...)
     * 例: notIn("id", 1, 2, 3, 4, 5)
     *
     * @param condition 执行条件
     * @param column    字段
     * @param values    数据数组
     * @return children
     */
    default Children notIn(boolean condition, R column, Object... values) {
        return notIn(condition, column, Arrays.stream(Optional.ofNullable(values).orElseGet(() -> new Object[]{}))
            .collect(toList()));
    }

    /**
     * ignore
     */
    default Children inSql(R column, String inValue) {
        return inSql(true, column, inValue);
    }

    /**
     * 字段 IN ( sql语句 )
     * !! sql 注入方式的 in 方法 !!
     * 例1: inSql("id", "1,2,3,4,5,6")
     * 例2: inSql("id", "select id from table where id < 3")
     *
     * @param condition 执行条件
     * @param column    字段
     * @param inValue   sql语句
     * @return children
     */
    Children inSql(boolean condition, R column, String inValue);

    /**
     * ignore
     */
    default Children notInSql(R column, String inValue) {
        return notInSql(true, column, inValue);
    }

    /**
     * 字段 NOT IN ( sql语句 )
     * !! sql 注入方式的 not in 方法 !!
     * 例1: notInSql("id", "1,2,3,4,5,6")
     * 例2: notInSql("id", "select id from table where id < 3")
     *
     * @param condition 执行条件
     * @param column    字段
     * @param inValue   sql语句 ---> 1,2,3,4,5,6 或者 select id from table where id < 3
     * @return children
     */
    Children notInSql(boolean condition, R column, String inValue);

    /**
     * ignore
     */
    default Children groupBy(R... columns) {
        return groupBy(true, columns);
    }

    /**
     * 分组：GROUP BY 字段, ...
     * 例: groupBy("id", "name")
     *
     * @param condition 执行条件
     * @param columns   字段数组
     * @return children
     */
    Children groupBy(boolean condition, R... columns);

    /**
     * ignore
     */
    default Children orderByAsc(R... columns) {
        return orderByAsc(true, columns);
    }

    /**
     * 排序：ORDER BY 字段, ... ASC
     * 例: orderByAsc("id", "name")
     *
     * @param condition 执行条件
     * @param columns   字段数组
     * @return children
     */
    default Children orderByAsc(boolean condition, R... columns) {
        return orderBy(condition, true, columns);
    }

    /**
     * ignore
     */
    default Children orderByDesc(R... columns) {
        return orderByDesc(true, columns);
    }

    /**
     * 排序：ORDER BY 字段, ... DESC
     * 例: orderByDesc("id", "name")
     *
     * @param condition 执行条件
     * @param columns   字段数组
     * @return children
     */
    default Children orderByDesc(boolean condition, R... columns) {
        return orderBy(condition, false, columns);
    }

    /**
     * 排序：ORDER BY 字段, ...
     * 例: orderBy(true, "id", "name")
     *
     * @param condition 执行条件
     * @param isAsc     是否是 ASC 排序
     * @param columns   字段数组
     * @return children
     */
    Children orderBy(boolean condition, boolean isAsc, R... columns);

    /**
     * ignore
     */
    default Children having(String sqlHaving, Object... params) {
        return having(true, sqlHaving, params);
    }

    /**
     * HAVING ( sql语句 )
     * 例1: having("sum(age) > 10")
     * 例2: having("sum(age) > {0}", 10)
     *
     * @param condition 执行条件
     * @param sqlHaving sql 语句
     * @param params    参数数组
     * @return children
     */
    Children having(boolean condition, String sqlHaving, Object... params);
}
