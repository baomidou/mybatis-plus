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
package com.baomidou.mybatisplus.core.conditions.interfaces;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * 查询条件封装
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
@SuppressWarnings("unchecked")
public interface Func<Children, R> extends Serializable {

    /**
     * 字段 IS NULL
     * <p>例: isNull("name")</p>
     *
     * @param column    字段
     * @return children
     */
    default Children isNull(R column) {
        return isNull(true, column);
    }

    /**
     * 字段 IS NULL
     * <p>例: isNull(true, "name")</p>
     *
     * @param condition 执行条件
     * @param column    字段
     * @return children
     */
    Children isNull(boolean condition, R column);

    /**
     * 字段 IS NOT NULL
     * <p>例: isNotNull("name")</p>
     *
     * @param column    字段
     * @return children
     */
    default Children isNotNull(R column) {
        return isNotNull(true, column);
    }

    /**
     * 字段 IS NOT NULL
     * <p>例: isNotNull(true, "name")</p>
     *
     * @param condition 执行条件
     * @param column    字段
     * @return children
     */
    Children isNotNull(boolean condition, R column);

    /**
     * 字段 IN (value.get(0), value.get(1), ...)
     * <p>例: in("id", Arrays.asList(1, 2, 3, 4, 5))</p>
     *
     * <li> 注意！当集合为 空或null 时, sql会拼接为：WHERE (字段名 IN ()), 执行时报错</li>
     * <li> 若要在特定条件下不拼接, 可在 condition 条件中判断 </li>
     *
     * @param column    字段
     * @param coll      数据集合
     * @return children
     */
    default Children in(R column, Collection<?> coll) {
        return in(true, column, coll);
    }

    /**
     * 字段 IN (value.get(0), value.get(1), ...)
     * <p>例: in(true, "id", Arrays.asList(1, 2, 3, 4, 5))</p>
     *
     * <li> 注意！当集合为 空或null 时, sql会拼接为：WHERE (字段名 IN ()), 执行时报错</li>
     * <li> 若要在特定条件下不拼接, 可在 condition 条件中判断 </li>
     *
     * @param condition 执行条件
     * @param column    字段
     * @param coll      数据集合
     * @return children
     */
    Children in(boolean condition, R column, Collection<?> coll);

    /**
     * 字段 IN (v0, v1, ...)
     * <p>例: in("id", 1, 2, 3, 4, 5)</p>
     *
     * <li> 注意！当数组为 空或null 时, sql会拼接为：WHERE (字段名 IN ()), 执行时报错</li>
     * <li> 若要在特定条件下不拼接, 可在 condition 条件中判断 </li>
     *
     * @param column    字段
     * @param values    数据数组
     * @return children
     */
    default Children in(R column, Object... values) {
        return in(true, column, values);
    }

    /**
     * 字段 IN (v0, v1, ...)
     * <p>例: in(true, "id", 1, 2, 3, 4, 5)</p>
     *
     * <li> 注意！当数组为 空或null 时, sql会拼接为：WHERE (字段名 IN ()), 执行时报错</li>
     * <li> 若要在特定条件下不拼接, 可在 condition 条件中判断 </li>
     *
     * @param condition 执行条件
     * @param column    字段
     * @param values    数据数组
     * @return children
     */
    Children in(boolean condition, R column, Object... values);

    /**
     * 字段 NOT IN (value.get(0), value.get(1), ...)
     * <p>例: notIn("id", Arrays.asList(1, 2, 3, 4, 5))</p>
     *
     * <li> 注意！当集合为 空或null 时, sql会拼接为：WHERE (字段名 NOT IN ()), 执行时报错</li>
     * <li> 若要在特定条件下不拼接, 可在 condition 条件中判断 </li>
     *
     * @param column    字段
     * @param coll      数据集合
     * @return children
     */
    default Children notIn(R column, Collection<?> coll) {
        return notIn(true, column, coll);
    }

    /**
     * 字段 NOT IN (value.get(0), value.get(1), ...)
     * <p>例: notIn(true, "id", Arrays.asList(1, 2, 3, 4, 5))</p>
     *
     * <li> 注意！当集合为 空或null 时, sql会拼接为：WHERE (字段名 NOT IN ()), 执行时报错</li>
     * <li> 若要在特定条件下不拼接, 可在 condition 条件中判断 </li>
     *
     * @param condition 执行条件
     * @param column    字段
     * @param coll      数据集合
     * @return children
     */
    Children notIn(boolean condition, R column, Collection<?> coll);

    /**
     * 字段 NOT IN (v0, v1, ...)
     * <p>例: notIn("id", 1, 2, 3, 4, 5)</p>
     *
     * <li> 注意！当数组为 空或null 时, sql会拼接为：WHERE (字段名 NOT IN ()), 执行时报错</li>
     * <li> 若要在特定条件下不拼接, 可在 condition 条件中判断 </li>
     *
     * @param column    字段
     * @param values    数据数组
     * @return children
     */
    default Children notIn(R column, Object... values) {
        return notIn(true, column, values);
    }

    /**
     * 字段 NOT IN (v0, v1, ...)
     * <p>例: notIn(true, "id", 1, 2, 3, 4, 5)</p>
     *
     * <li> 注意！当数组为 空或null 时, sql会拼接为：WHERE (字段名 NOT IN ()), 执行时报错</li>
     * <li> 若要在特定条件下不拼接, 可在 condition 条件中判断 </li>
     *
     * @param condition 执行条件
     * @param column    字段
     * @param values    数据数组
     * @return children
     */
    Children notIn(boolean condition, R column, Object... values);

    /**
     * 字段 EQ ( sql语句 )
     * <p>!! sql 注入方式的 eq 方法 !!</p>
     * <p>例1: eqSql("id", "1")</p>
     * <p>例2: eqSql("id", "select MAX(id) from table")</p>
     *
     * @param column 字段
     * @param sql    sql语句
     * @return children
     * @since 3.5.6
     */
    default Children eqSql(R column, String sql) {
        return eqSql(true, column, sql);
    }

    /**
     * 字段 EQ ( sql语句 )
     * <p>!! sql 注入方式的 eq 方法 !!</p>
     * <p>例1: eqSql("id", "1")</p>
     * <p>例2: eqSql("id", "select MAX(id) from table")</p>
     *
     * @param condition 执行条件
     * @param column    字段
     * @param sql       sql语句
     * @return children
     * @since 3.5.6
     */
    Children eqSql(boolean condition, R column, String sql);

    /**
     * 字段 IN ( sql语句 )
     * <p>!! sql 注入方式的 in 方法 !!</p>
     * <p>例1: inSql("id", "1")</p>
     * <p>例2: inSql("id", "select id from table where id &lt; 3")</p>
     *
     * @param column    字段
     * @param sql   sql语句
     * @return children
     */
    default Children inSql(R column, String sql) {
        return inSql(true, column, sql);
    }

    /**
     * 字段 IN ( sql语句 )
     * <p>!! sql 注入方式的 in 方法 !!</p>
     * <p>例1: inSql(true, "id", "1")</p>
     * <p>例2: inSql(true, "id", "select id from table where id &lt; 3")</p>
     *
     * @param condition 执行条件
     * @param column    字段
     * @param sql   sql语句
     * @return children
     */
    Children inSql(boolean condition, R column, String sql);

    /**
     * 字段 &gt; ( sql语句 )
     * <p>例1: gtSql(true, "id", "1")</p>
     * <p>例1: gtSql(true, "id", "select id from table where name = 'JunJun'")</p>
     *
     * @param condition 执行条件
     * @param column    字段
     * @param sql       sql语句
     * @return children
     */
    Children gtSql(boolean condition, R column, String sql);

    /**
     * 字段 &gt; ( sql语句 )
     * <p>例1: gtSql("id", "1")</p>
     * <p>例1: gtSql("id", "select id from table where name = 'JunJun'")</p>
     *
     * @param column 字段
     * @param sql    sql语句
     * @return children
     */
    default Children gtSql(R column, String sql) {
        return gtSql(true, column, sql);
    }

    /**
     * 字段 >= ( sql语句 )
     * <p>例1: geSql(true, "id", "1")</p>
     * <p>例1: geSql(true, "id", "select id from table where name = 'JunJun'")</p>
     *
     * @param condition 执行条件
     * @param column    字段
     * @param sql       sql语句
     * @return children
     */
    Children geSql(boolean condition, R column, String sql);

    /**
     * 字段 >= ( sql语句 )
     * <p>例1: geSql("id", "1")</p>
     * <p>例1: geSql("id", "select id from table where name = 'JunJun'")</p>
     *
     * @param column 字段
     * @param sql    sql语句
     * @return children
     */
    default Children geSql(R column, String sql) {
        return geSql(true, column, sql);
    }

    /**
     * 字段 &lt; ( sql语句 )
     * <p>例1: ltSql(true, "id", "1")</p>
     * <p>例1: ltSql(true , "id", "select id from table where name = 'JunJun'")</p>
     *
     * @param condition 执行条件
     * @param column 字段
     * @param sql sql语句
     * @return children
     */
    Children ltSql(boolean condition, R column, String sql);

    /**
     * 字段 &lt; ( sql语句 )
     * <p>例1: ltSql("id", "1")</p>
     * <p>例1: ltSql("id", "select id from table where name = 'JunJun'")</p>
     *
     * @param column 字段
     * @param sql    sql语句
     * @return children
     */
    default Children ltSql(R column, String sql) {
        return ltSql(true, column, sql);
    }

    /**
     * 字段 <= ( sql语句 )
     * <p>例1: leSql(true, "id", "1")</p>
     * <p>例1: leSql(true ,"id", "select id from table where name = 'JunJun'")</p>
     *
     * @param condition 执行条件
     * @param column    字段
     * @param sql       sql语句
     * @return children
     */
    Children leSql(boolean condition, R column, String sql);

    /**
     * 字段 <= ( sql语句 )
     * <p>例1: leSql("id", "1")</p>
     * <p>例1: leSql("id", "select id from table where name = 'JunJun'")</p>
     *
     * @param column 字段
     * @param inValue  sql语句
     * @return children
     */
    default Children leSql(R column, String inValue) {
        return leSql(true, column, inValue);
    }

    /**
     * 字段 NOT IN ( sql语句 )
     * <p>!! sql 注入方式的 not in 方法 !!</p>
     * <p>例1: notInSql("id", "1, 2, 3, 4, 5, 6")</p>
     * <p>例2: notInSql("id", "select id from table where id &lt; 3")</p>
     *
     * @param column  字段
     * @param inValue sql语句 ---&gt; 1,2,3,4,5,6 或者 select id from table where id &lt; 3
     * @return children
     */
    default Children notInSql(R column, String inValue) {
        return notInSql(true, column, inValue);
    }

    /**
     * 字段 NOT IN ( sql语句 )
     * <p>!! sql 注入方式的 not in 方法 !!</p>
     * <p>例1: notInSql(true, "id", "1, 2, 3, 4, 5, 6")</p>
     * <p>例2: notInSql(true, "id", "select id from table where id &lt; 3")</p>
     *
     * @param condition 执行条件
     * @param column    字段
     * @param inValue   sql语句 ---&gt; 1,2,3,4,5,6 或者 select id from table where id &lt; 3
     * @return children
     */
    Children notInSql(boolean condition, R column, String inValue);

    /**
     * 分组：GROUP BY 字段, ...
     * <p>例: groupBy(true, "id")</p>
     *
     * @param condition 执行条件
     * @param column    单个字段
     * @return children
     */
    Children groupBy(boolean condition, R column);

    /**
     * 分组：GROUP BY 字段, ...
     * <p>例: groupBy("id")</p>
     *
     * @param column 单个字段
     * @return children
     */
    default Children groupBy(R column) {
        return groupBy(true, column);
    }

    /**
     * 分组：GROUP BY 字段, ...
     * <p>例: groupBy(true, Arrays.asList("id", "name"))</p>
     *
     * @param condition 执行条件
     * @param columns   字段数组
     * @return children
     */
    Children groupBy(boolean condition, List<R> columns);

    /**
     * 分组：GROUP BY 字段, ...
     * <p>例: groupBy(Arrays.asList("id", "name"))</p>
     *
     * @param columns 字段数组
     * @return children
     */
    default Children groupBy(List<R> columns) {
        return groupBy(true, columns);
    }

    /**
     * 分组：GROUP BY 字段, ...
     * <p>例: groupBy("id", "name")</p>
     *
     * @param column  单个字段
     * @param columns 字段数组
     * @return children
     */
    default Children groupBy(R column, R... columns) {
        return groupBy(true, column, columns);
    }

    /**
     * 分组：GROUP BY 字段, ...
     * <p>例: groupBy(true, "id", "name")</p>
     *
     * @param condition 执行条件
     * @param column    单个字段
     * @param columns   字段数组
     * @return children
     */
    Children groupBy(boolean condition, R column, R... columns);

    /**
     * 分组：GROUP BY 字段, ...
     * <p>例: groupBy(true, "id", Arrays.asList("name"))</p>
     *
     * @param condition 执行条件
     * @param column    单个字段
     * @param columns   字段数组
     * @return children
     * @since 3.5.4
     */
    Children groupBy(boolean condition, R column, List<R> columns);

    /**
     * 排序：ORDER BY 字段, ... ASC
     * <p>例: orderByAsc(true, "id")</p>
     *
     * @param condition 执行条件
     * @param column    单个字段
     * @return children
     */
    default Children orderByAsc(boolean condition, R column) {
        return orderBy(condition, true, column);
    }

    /**
     * 排序：ORDER BY 字段, ... ASC
     * <p>例: orderByAsc("id")</p>
     *
     * @param column 单个字段
     * @return children
     */
    default Children orderByAsc(R column) {
        return orderByAsc(true, column);
    }

    /**
     * 排序：ORDER BY 字段, ... ASC
     * <p>例: orderByAsc(true, Arrays.asList("id", "name"))</p>
     *
     * @param condition 执行条件
     * @param columns   字段数组
     * @return children
     */
    default Children orderByAsc(boolean condition, List<R> columns) {
        return orderBy(condition, true, columns);
    }

    /**
     * 排序：ORDER BY 字段, ... ASC
     * <p>例: orderByAsc(Arrays.asList("id", "name"))</p>
     *
     * @param columns 字段数组
     * @return children
     */
    default Children orderByAsc(List<R> columns) {
        return orderByAsc(true, columns);
    }

    /**
     * 排序：ORDER BY 字段, ... ASC
     *
     * @param column  字段
     * @param columns 字段数组
     * @return children
     */
    default Children orderByAsc(R column, R... columns) {
        return orderByAsc(true, column, columns);
    }

    /**
     * 排序：ORDER BY 字段, ... ASC
     *
     * @param condition 执行条件
     * @param column    字段
     * @param columns   字段数组
     */
    default Children orderByAsc(boolean condition, R column, R... columns) {
        return orderBy(condition, true, column, columns);
    }

    /**
     * 排序：ORDER BY 字段, ... ASC
     * <p>例: orderByAsc(true, Arrays.asList("id", "name"))</p>
     *
     * @param condition 执行条件
     * @param columns   字段数组
     * @return children
     * @since 3.5.4
     */
    default Children orderByAsc(boolean condition, R column, List<R> columns) {
        return orderBy(condition, true, column, columns);
    }

    /**
     * 排序：ORDER BY 字段, ... DESC
     * <p>例: orderByDesc(true, "id")</p>
     *
     * @param condition 执行条件
     * @param column    字段
     * @return children
     */
    default Children orderByDesc(boolean condition, R column) {
        return orderBy(condition, false, column);
    }

    /**
     * 排序：ORDER BY 字段, ... DESC
     * <p>例: orderByDesc("id")</p>
     *
     * @param column    字段
     * @return children
     */
    default Children orderByDesc(R column) {
        return orderByDesc(true, column);
    }

    /**
     * 排序：ORDER BY 字段, ... DESC
     * <p>例: orderByDesc(true, Arrays.asList("id", "name"))</p>
     *
     * @param condition 执行条件
     * @param columns   字段列表
     * @return children
     */
    default Children orderByDesc(boolean condition, List<R> columns) {
        return orderBy(condition, false, columns);
    }

    /**
     * 排序：ORDER BY 字段, ... DESC
     *
     * @param columns 字段列表
     */
    default Children orderByDesc(List<R> columns) {
        return orderByDesc(true, columns);
    }

    /**
     * 排序：ORDER BY 字段, ... DESC
     *
     * @param column  单个字段
     * @param columns 字段列表
     */
    default Children orderByDesc(R column, R... columns) {
        return orderByDesc(true, column, columns);
    }

    /**
     * 排序：ORDER BY 字段, ... DESC
     *
     * @param condition 执行条件
     * @param column    单个字段
     * @param columns   字段列表
     */
    default Children orderByDesc(boolean condition, R column, R... columns) {
        return orderBy(condition, false, column, CollectionUtils.toList(columns));
    }

    /**
     * 排序：ORDER BY 字段, ... DESC
     *
     * @param condition 执行条件
     * @param column    单个字段
     * @param columns   字段列表
     * @since 3.5.4
     */
    default Children orderByDesc(boolean condition, R column, List<R> columns) {
        return orderBy(condition, false, column, columns);
    }


    /**
     * 排序：ORDER BY 字段, ...
     * <p>例: orderBy(true, "id")</p>
     *
     * @param condition 执行条件
     * @param isAsc     是否是 ASC 排序
     * @param column    单个字段
     * @return children
     */
    Children orderBy(boolean condition, boolean isAsc, R column);

    /**
     * 排序：ORDER BY 字段, ...
     * <p>例: orderBy(true, Arrays.asList("id", "name"))</p>
     *
     * @param condition 执行条件
     * @param isAsc     是否是 ASC 排序
     * @param columns   字段列表
     * @return children
     */
    Children orderBy(boolean condition, boolean isAsc, List<R> columns);

    /**
     * 排序：ORDER BY 字段, ...
     *
     * @param condition 执行条件
     * @param isAsc     是否是 ASC 排序
     * @param columns   字段列表
     * @return children
     */
    Children orderBy(boolean condition, boolean isAsc, R column, R... columns);

    /**
     * 排序：ORDER BY 字段, ...
     *
     * @param condition 执行条件
     * @param isAsc     是否是 ASC 排序
     * @param columns   字段列表
     * @return children
     * @since 3.5.4
     */
    Children orderBy(boolean condition, boolean isAsc, R column, List<R> columns);


    /**
     * HAVING ( sql语句 )
     * <p>例1: having("sum(age) &gt; 10")</p>
     * <p>例2: having("sum(age) &gt; {0}", 10)</p>
     *
     * @param sqlHaving sql 语句
     * @param params    参数数组
     * @return children
     */
    default Children having(String sqlHaving, Object... params) {
        return having(true, sqlHaving, params);
    }

    /**
     * HAVING ( sql语句 )
     * <p>例1: having(true, "sum(age) &gt; 10")</p>
     * <p>例2: having(true, "sum(age) &gt; {0}", 10)</p>
     *
     * @param condition 执行条件
     * @param sqlHaving sql 语句
     * @param params    参数数组
     * @return children
     */
    Children having(boolean condition, String sqlHaving, Object... params);

    /**
     * 消费函数
     *
     * @param consumer 消费函数
     * @return children
     */
    default Children func(Consumer<Children> consumer) {
        return func(true, consumer);
    }

    /**
     * 消费函数
     *
     * @param condition 执行条件
     * @param consumer  消费函数
     * @return children
     * @since 3.3.1
     */
    Children func(boolean condition, Consumer<Children> consumer);
}
