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
package com.baomidou.mybatisplus.core.conditions;

import static com.baomidou.mybatisplus.core.enums.SqlKeyword.AND;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.ASC;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.BETWEEN;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.DESC;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.EQ;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.EXISTS;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.GE;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.GROUP_BY;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.GT;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.HAVING;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.IN;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.IS_NOT_NULL;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.IS_NULL;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.LE;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.LIKE;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.LT;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.NE;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.NOT;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.OR;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.ORDER_BY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;


/**
 * <p>
 * 查询条件封装
 * </p>
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
public abstract class AbstractWrapper<T, R, This extends AbstractWrapper<T, R, This>> extends Wrapper<T> {

    private static final String MP_GENERAL_PARAMNAME = "MPGENVAL";
    private static final String DEFAULT_PARAM_ALIAS = "ew";
    /**
     * 占位符
     */
    private static final String PLACE_HOLDER = "{%s}";
    private static final String MYBATIS_PLUS_TOKEN = "#{%s.paramNameValuePairs.%s}";
    protected AtomicInteger paramNameSeq;
    protected Map<String, Object> paramNameValuePairs;
    protected String paramAlias = null;
    private List<ISqlSegment> expression = new ArrayList<>();
    private boolean didOrderBy = false;

    /**
     * 数据库表映射实体类
     */
    protected T entity;

    @Override
    public T getEntity() {
        return this.entity;
    }

    public This setEntity(T entity) {
        this.entity = entity;
        return typedThis();
    }

    protected abstract String columnToString(R column);

    public This apply(String condition) {
        return doIt(true, () -> condition);
    }

    /**
     * LIKE '%值%'
     */
    public This like(R column, Object val) {
        return like(true, column, val);
    }

    /**
     * LIKE '%值%'
     */
    public This like(boolean condition, R column, Object val) {
        return doIt(condition, () -> columnToString(column), LIKE, () -> "'%", () -> formatSql("{0}", val),
            () -> "%'");
    }

    /**
     * NOT LIKE '%值%'
     */
    public This notLike(R column, Object val) {
        return notLike(true, column, val);
    }

    /**
     * NOT LIKE '%值%'
     */
    public This notLike(boolean condition, R column, Object val) {
        return not(condition).like(condition, column, val);
    }

    /**
     * LIKE '%值'
     */
    public This likeLeft(R column, Object val) {
        return likeLeft(true, column, val);
    }

    /**
     * LIKE '%值'
     */
    public This likeLeft(boolean condition, R column, Object val) {
        return doIt(condition, () -> columnToString(column), LIKE, () -> "'%", () -> formatSql("{0}", val),
            () -> "'");
    }

    /**
     * LIKE '值%'
     */
    public This likeRight(R column, Object val) {
        return likeRight(true, column, val);
    }

    /**
     * LIKE '值%'
     */
    public This likeRight(boolean condition, R column, Object val) {
        return doIt(condition, () -> columnToString(column), LIKE, () -> "'", () -> formatSql("{0}", val),
            () -> "%'");
    }

    /**
     * 等于 =
     */
    public This eq(R column, Object val) {
        return eq(true, column, val);
    }

    /**
     * 等于 =
     */
    public This eq(boolean condition, R column, Object val) {
        return addCondition(condition, column, EQ, val);
    }

    /**
     * 不等于 <>
     */
    public This ne(R column, Object val) {
        return ne(true, column, val);
    }

    /**
     * 不等于 <>
     */
    public This ne(boolean condition, R column, Object val) {
        return addCondition(condition, column, NE, val);
    }

    /**
     * 大于 >
     */
    public This gt(R column, Object val) {
        return gt(true, column, val);
    }

    /**
     * 大于 >
     */
    public This gt(boolean condition, R column, Object val) {
        return addCondition(condition, column, GT, val);
    }

    /**
     * 大于等于 >=
     */
    public This ge(R column, Object val) {
        return ge(true, column, val);
    }

    /**
     * 大于等于 >=
     */
    public This ge(boolean condition, R column, Object val) {
        return addCondition(condition, column, GE, val);
    }

    /**
     * 小于 <
     */
    public This lt(R column, Object val) {
        return lt(true, column, val);
    }

    /**
     * 小于 <
     */
    public This lt(boolean condition, R column, Object val) {
        return addCondition(condition, column, LT, val);
    }

    /**
     * 小于等于 <=
     */
    public This le(R column, Object val) {
        return le(true, column, val);
    }

    /**
     * 小于等于 <=
     */
    public This le(boolean condition, R column, Object val) {
        return addCondition(condition, column, LE, val);
    }

    /**
     * BETWEEN 值1 AND 值2
     */
    public This between(R column, Object val1, Object val2) {
        return between(true, column, val1, val2);
    }

    /**
     * BETWEEN 值1 AND 值2
     */
    public This between(boolean condition, R column, Object val1, Object val2) {
        return doIt(condition, () -> columnToString(column), BETWEEN, () -> formatSql("{0}", val1), AND,
            () -> formatSql("{0}", val2));
    }

    /**
     * NOT BETWEEN 值1 AND 值2
     */
    public This notBetween(R column, Object val1, Object val2) {
        return notBetween(true, column, val1, val1);
    }

    /**
     * NOT BETWEEN 值1 AND 值2
     */
    public This notBetween(boolean condition, R column, Object val1, Object val2) {
        return not(condition).between(condition, column, val1, val2);
    }

    /**
     * 字段 IS NULL
     */
    public This isNull(R column) {
        return isNull(true, column);
    }

    /**
     * 字段 IS NULL
     */
    public This isNull(boolean condition, R column) {
        return doIt(condition, () -> columnToString(column), IS_NULL);
    }

    /**
     * 字段 IS NOT NULL
     */
    public This isNotNull(R column) {
        return isNotNull(true, column);
    }

    /**
     * 字段 IS NOT NULL
     */
    public This isNotNull(boolean condition, R column) {
        return doIt(condition, () -> columnToString(column), IS_NOT_NULL);
    }

    public This and() {
        return and(true);
    }

    public This and(boolean condition) {
        return doIt(condition, AND);
    }

    public This and(Function<This, This> func) {
        return and(true, func);
    }

    public This and(boolean condition, Function<This, This> func) {
        return addNestedCondition(condition, func, AND);
    }

    public This or() {
        return or(true);
    }

    public This or(boolean condition) {
        return doIt(condition, OR);
    }

    public This or(Function<This, This> func) {
        return or(true, func);
    }

    public This or(boolean condition, Function<This, This> func) {
        return addNestedCondition(condition, func, OR);
    }

    /**
     * 字段 IN (value.get(0), value.get(1), ...)
     */
    public This in(R column, Collection<?> value) {
        return in(true, column, value);
    }

    /**
     * 字段 IN (value.get(0), value.get(1), ...)
     */
    public This in(boolean condition, R column, Collection<?> value) {
        if (CollectionUtils.isEmpty(value)) {
            return typedThis();
        }
        return doIt(condition, () -> columnToString(column), IN, inExpression(value));
    }

    /**
     * 字段 NOT IN (value.get(0), value.get(1), ...)
     */
    public This notIn(R column, Collection<?> value) {
        return notIn(true, column, value);
    }

    /**
     * 字段 NOT IN (value.get(0), value.get(1), ...)
     */
    public This notIn(boolean condition, R column, Collection<?> value) {
        if (CollectionUtils.isEmpty(value)) {
            return typedThis();
        }
        return not(condition).in(condition, column, value);
    }

    /**
     * 分组：GROUP BY 字段, ...
     */
    public This groupBy(R column) {
        return doIt(true, GROUP_BY, () -> columnToString(column));
    }

    /**
     * 排序：ORDER BY 字段, ...
     */
    public This orderBy(R column) {
        return orderBy(column, true);
    }

    /**
     * 排序：ORDER BY 字段, ...
     */
    public This orderBy(R column, boolean isAsc) {//todo 多次调用如下解决?
        if (!didOrderBy) {
            didOrderBy = true;
            return doIt(true, ORDER_BY, () -> columnToString(column), isAsc ? ASC : DESC);
        }
        return doIt(true, () -> ",", () -> columnToString(column), isAsc ? ASC : DESC);
    }

    /**
     * 字段 IN ( sql 语句 )
     * 例: in("select id from table where age < 20")
     */
    public This in(String sql) {
        return in(true, sql);
    }

    /**
     * 字段 IN ( sql 语句 )
     */
    public This in(boolean condition, String sql) {
        return addNestedCondition(condition, sql, IN);
    }

    /**
     * 字段 NOT IN ( sql 语句 )
     * 例: notIn("select id from table where age < 20")
     */
    public This notIn(String sql) {
        return notIn(true, sql);
    }

    /**
     * 字段 NOT IN ( sql 语句 )
     */
    public This notIn(boolean condition, String sql) {
        return not(condition).in(condition, sql);
    }

    /**
     * HAVING ( sql 语句 )
     * 例: having("sum(age) > {0}", 1)
     */
    public This having(String sqlHaving, Object... params) {
        return having(true, sqlHaving, params);
    }

    /**
     * HAVING ( sql 语句 )
     */
    public This having(boolean condition, String sqlHaving, Object... params) {
        return doIt(condition, HAVING, () -> formatSqlIfNeed(condition, sqlHaving, params));
    }

    /**
     * EXISTS ( sql 语句 )
     * 例: exists("select id from table where age = 1")
     */
    public This exists(String sql) {
        return exists(true, sql);
    }

    /**
     * EXISTS ( sql 语句 )
     */
    public This exists(boolean condition, String sql) {
        return addNestedCondition(condition, sql, EXISTS);
    }

    /**
     * NOT EXISTS ( sql 语句 )
     * 例: notExists("select id from table where age = 1")
     */
    public This notExists(String sql) {
        return notExists(true, sql);
    }

    /**
     * NOT EXISTS ( sql 语句 )
     */
    public This notExists(boolean condition, String sql) {
        return not(condition).exists(condition, sql);
    }

    /**
     * LAST 拼接在 SQL 末尾
     * 例: last("limit 10")
     */
    public This last(String sql) {
        return last(true, sql);
    }

    /**
     * LAST 拼接在 SQL 末尾
     */
    public This last(boolean condition, String sql) {
        return doIt(condition, () -> sql);
    }

    /**
     * NOT 关键词
     */
    protected This not(boolean condition) {
        return doIt(condition, NOT);
    }

    /**
     * <p>
     * 普通查询条件
     * </p>
     *
     * @param condition  是否执行
     * @param column     属性
     * @param sqlKeyword SQL 关键词
     * @param val        条件值
     * @return this
     */
    protected This addCondition(boolean condition, R column, SqlKeyword sqlKeyword, Object val) {
        return doIt(condition, () -> columnToString(column),
            sqlKeyword, () -> this.formatSql("{0}", val));
    }

    /**
     * <p>
     * 嵌套查询条件
     * </p>
     *
     * @param val        查询条件值
     * @param sqlKeyword SQL 关键词
     * @return this
     */
    protected This addNestedCondition(boolean condition, String val, SqlKeyword sqlKeyword) {
        return doIt(condition, sqlKeyword, () -> this.formatSql("({0})", val));
    }

    /**
     * <p>
     * 多重嵌套查询条件
     * </p>
     *
     * @param condition  查询条件值
     * @param sqlKeyword SQL 关键词
     * @return
     */
    protected This addNestedCondition(boolean condition, Function<This, This> func, SqlKeyword sqlKeyword) {
        return doIt(condition, sqlKeyword, () -> "(",
            func.apply(instance(paramNameSeq, paramNameValuePairs)), () -> ")");
    }

    /**
     * 子类返回一个自己的新对象
     */
    protected abstract This instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs);

    /**
     * <p>
     * 格式化SQL
     * </p>
     *
     * @param sqlStr SQL语句部分
     * @param params 参数集
     * @return this
     */
    protected String formatSql(String sqlStr, Object... params) {
        return formatSqlIfNeed(true, sqlStr, params);
    }

    /**
     * <p>
     * 根据需要格式化SQL<BR>
     * <BR>
     * Format SQL for methods: EntityQ<T>.where/and/or...("name={0}", value);
     * ALL the {<b>i</b>} will be replaced with #{MPGENVAL<b>i</b>}<BR>
     * <BR>
     * ew.where("sample_name=<b>{0}</b>", "haha").and("sample_age &gt;<b>{0}</b>
     * and sample_age&lt;<b>{1}</b>", 18, 30) <b>TO</b>
     * sample_name=<b>#{MPGENVAL1}</b> and sample_age&gt;#<b>{MPGENVAL2}</b> and
     * sample_age&lt;<b>#{MPGENVAL3}</b><BR>
     * </p>
     *
     * @param need   是否需要格式化
     * @param sqlStr SQL语句部分
     * @param params 参数集
     * @return this
     */
    protected String formatSqlIfNeed(boolean need, String sqlStr, Object... params) {
        if (!need || StringUtils.isEmpty(sqlStr)) {
            return null;
        }
        if (ArrayUtils.isNotEmpty(params)) {
            for (int i = 0; i < params.length; ++i) {
                String genParamName = MP_GENERAL_PARAMNAME + paramNameSeq.incrementAndGet();
                sqlStr = sqlStr.replace(String.format(PLACE_HOLDER, i),
                    String.format(MYBATIS_PLUS_TOKEN, getParamAlias(), genParamName));
                paramNameValuePairs.put(genParamName, params[i]);
            }
        }
        return sqlStr;
    }

    /**
     * <p>
     * 获取in表达式 包含括号
     * </p>
     *
     * @param value 集合
     */
    private ISqlSegment inExpression(Collection<?> value) {
        return () -> value.stream().map(i -> formatSql("{0}", i))
            .collect(Collectors.joining(",", "(", ")"));
    }

    /**
     * 必要的初始化
     */
    protected void initNeed() {
        this.paramNameSeq = new AtomicInteger(0);
        this.paramNameValuePairs = new HashMap<>();
    }

    /**
     * <p>
     * 对sql片段进行组装
     * </p>
     *
     * @param condition   是否执行
     * @param sqlSegments sql片段数组
     * @return this
     */
    protected This doIt(boolean condition, ISqlSegment... sqlSegments) {
        if (condition) {
            expression.addAll(Arrays.asList(sqlSegments));
        }
        return typedThis();
    }

    public String getParamAlias() {
        return StringUtils.isEmpty(paramAlias) ? DEFAULT_PARAM_ALIAS : paramAlias;
    }

    @SuppressWarnings("unchecked")
    protected This typedThis() {
        return (This) this;
    }

    @Override
    public String getSqlSegment() {
        return String.join(" ", expression.stream()
            .map(ISqlSegment::getSqlSegment)
            .collect(Collectors.toList()));
    }

    public Map<String, Object> getParamNameValuePairs() {
        return paramNameValuePairs;
    }
}
