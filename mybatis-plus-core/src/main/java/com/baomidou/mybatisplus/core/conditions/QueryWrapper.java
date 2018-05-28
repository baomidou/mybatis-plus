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

import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.enums.SqlKeyword.*;


/**
 * <p>
 * 查询条件封装
 * </p>
 *
 * @author hubin
 * @since 2017-05-26
 */
public class QueryWrapper<T> extends Wrapper<T> {

    /**
     * 占位符
     */
    private static final String PLACE_HOLDER = "{%s}";

    private static final String MYBATIS_PLUS_TOKEN = "#{%s.paramNameValuePairs.%s}";

    private static final String MP_GENERAL_PARAMNAME = "MPGENVAL";

    private static final String DEFAULT_PARAM_ALIAS = "ew";
    private final Map<String, Object> paramNameValuePairs = new HashMap<>();
    private final AtomicInteger paramNameSeq = new AtomicInteger(0);
    protected String paramAlias = null;
    private List<ISqlSegment> expression = new ArrayList<>();

    public QueryWrapper apply(String condition) {
        expression.add(() -> condition);
        return this;
    }

    public QueryWrapper and(Function<QueryWrapper, QueryWrapper> func) {
        return addNestedCondition(func, AND);
    }

    public QueryWrapper or(String column, Object val) {
        return addCondition(true, column, OR, val);//todo 待动
    }

    public QueryWrapper or(Function<QueryWrapper, QueryWrapper> func) {
        return addNestedCondition(func, OR);
    }

    public QueryWrapper in(String condition) {//todo 待动
        return addNestedCondition(condition, IN);
    }

    public QueryWrapper notIn(String condition) {
        return not().in(condition);
    }

    /**
     * LIKE '%值%'
     */
    public QueryWrapper like(String column, Object val) {
        return like(true, column, val);
    }

    /**
     * LIKE '%值%'
     */
    public QueryWrapper like(boolean condition, String column, Object val) {
        return doIt(condition, () -> column, LIKE, () -> "'%", () -> formatSql("{0}", val), () -> "%'");
    }

    /**
     * LIKE '%值'
     */
    public QueryWrapper likeLeft(String column, Object val) {
        return likeLeft(true, column, val);
    }

    /**
     * LIKE '%值'
     */
    public QueryWrapper likeLeft(boolean condition, String column, Object val) {
        return doIt(condition, () -> column, LIKE, () -> "'%", () -> formatSql("{0}", val), () -> "'");
    }

    /**
     * LIKE '值%'
     */
    public QueryWrapper likeRight(String column, Object val) {
        return likeRight(true, column, val);
    }

    /**
     * LIKE '值%'
     */
    public QueryWrapper likeRight(boolean condition, String column, Object val) {
        return doIt(condition, () -> column, LIKE, () -> "'", () -> formatSql("{0}", val), () -> "%'");
    }

    /**
     * 等于 =
     */
    public QueryWrapper eq(String column, Object val) {
        return eq(true, column, val);
    }

    /**
     * 等于 =
     */
    public QueryWrapper eq(boolean condition, String column, Object val) {
        return addCondition(condition, column, EQ, val);
    }

    /**
     * 不等于 <>
     */
    public QueryWrapper ne(String column, Object val) {
        return ne(true, column, val);
    }

    /**
     * 不等于 <>
     */
    public QueryWrapper ne(boolean condition, String column, Object val) {
        return addCondition(condition, column, NE, val);
    }

    /**
     * 大于 >
     */
    public QueryWrapper gt(String column, Object val) {
        return gt(true, column, val);
    }

    /**
     * 大于 >
     */
    public QueryWrapper gt(boolean condition, String column, Object val) {
        return addCondition(condition, column, GT, val);
    }

    /**
     * 大于等于 >=
     */
    public QueryWrapper ge(String column, Object val) {
        return ge(true, column, val);
    }

    /**
     * 大于等于 >=
     */
    public QueryWrapper ge(boolean condition, String column, Object val) {
        return addCondition(condition, column, GE, val);
    }

    /**
     * 小于 <
     */
    public QueryWrapper lt(String column, Object val) {
        return lt(true, column, val);
    }

    /**
     * 小于 <
     */
    public QueryWrapper lt(boolean condition, String column, Object val) {
        return addCondition(condition, column, LT, val);
    }

    /**
     * 小于等于 <=
     */
    public QueryWrapper le(String column, Object val) {
        return le(true, column, val);
    }

    /**
     * 小于等于 <=
     */
    public QueryWrapper le(boolean condition, String column, Object val) {
        return addCondition(condition, column, LE, val);
    }

    /**
     * BETWEEN 值1 AND 值2
     */
    public QueryWrapper between(String column, Object val1, Object val2) {
        return between(true, column, "val1", "val2");
    }

    /**
     * BETWEEN 值1 AND 值2
     */
    public QueryWrapper between(boolean condition, String column, Object val1, Object val2) {
        return doIt(condition, () -> column, BETWEEN, () -> "val1", AND, () -> "val2");
    }

    /**
     * 字段 IS NULL
     */
    public QueryWrapper isNull(String column) {
        return isNull(true, column);
    }

    /**
     * 字段 IS NULL
     */
    public QueryWrapper isNull(boolean condition, String column) {
        return doIt(condition, () -> column, IS_NULL);
    }

    /**
     * 字段 IS NOT NULL
     */
    public QueryWrapper isNotNull(String column) {
        return isNotNull(true, column);
    }

    /**
     * 字段 IS NOT NULL
     */
    public QueryWrapper isNotNull(boolean condition, String column) {
        return doIt(condition, () -> column, IS_NOT_NULL);
    }

    /**
     * 分组：GROUP BY 字段, ...
     */
    public QueryWrapper groupBy(String column) {
        return doIt(true, GROUP_BY, () -> column);
    }

    /**
     * 排序：ORDER BY 字段, ...
     */
    public QueryWrapper orderBy(String column) {//todo 产生的sql有bug
        return doIt(true, ORDER_BY, () -> column);
    }

    /**
     * HAVING 关键词
     */
    public QueryWrapper having() {
        return doIt(true, HAVING);
    }

    /**
     * exists ( sql 语句 )
     */
    public QueryWrapper exists(String condition) {
        return this.addNestedCondition(condition, EXISTS);
    }

    /**
     * LAST 拼接在 SQL 末尾
     */
    public QueryWrapper last(String condition) {
        return doIt(true, () -> condition);
    }

    /**
     * NOT 关键词
     */
    protected QueryWrapper not() {//todo 待考虑
        return doIt(true, NOT);
    }

    /**
     * <p>
     * 普通查询条件
     * </p>
     *
     * @param condition  是否执行
     * @param column     字段
     * @param sqlKeyword SQL 关键词
     * @param val        条件值
     * @return this
     */
    protected QueryWrapper addCondition(boolean condition, String column, SqlKeyword sqlKeyword, Object val) {
        return doIt(condition, () -> column, sqlKeyword, () -> this.formatSql("{0}", val));
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
    protected QueryWrapper addNestedCondition(Object val, SqlKeyword sqlKeyword) {
        return doIt(true, sqlKeyword, () -> this.formatSql("({0})", val));
    }

    /**
     * <p>
     * 多重嵌套查询条件
     * </p>
     *
     * @param condition  查询条件值
     * @param sqlKeyword SQL 关键词
     * @return this
     */
    protected QueryWrapper addNestedCondition(Function<QueryWrapper, QueryWrapper> condition, SqlKeyword sqlKeyword) {
        return doIt(true, sqlKeyword, () -> "(", condition.apply(new QueryWrapper()), () -> ")");
    }

    /**
     * <p>
     * 对sql片段进行组装
     * </p>
     *
     * @param condition    是否执行
     * @param iSqlSegments sql片段数组
     * @return this
     */
    protected QueryWrapper doIt(boolean condition, ISqlSegment... iSqlSegments) {
        if (condition) {
            expression.addAll(Arrays.asList(iSqlSegments));
        }
        return this;
    }

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
     * Format SQL for methods: EntityWrapper<T>.where/and/or...("name={0}", value);
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

    public String getParamAlias() {
        return StringUtils.isEmpty(paramAlias) ? DEFAULT_PARAM_ALIAS : paramAlias;
    }

    @Override
    public String getSqlSegment() {
        return String.join(" ", expression.stream()
            .map(ISqlSegment::getSqlSegment)
            .collect(Collectors.toList()));
    }
}
