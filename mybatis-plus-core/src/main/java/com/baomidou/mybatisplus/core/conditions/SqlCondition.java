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
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.BETWEEN;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;


/**
 * <p>
 * 查询条件封装
 * </p>
 *
 * @author hubin
 * @since 2017-05-26
 */
public class SqlCondition<T> extends Wrapper<T> {

    /**
     * 占位符
     */
    private static final String PLACE_HOLDER = "{%s}";

    private static final String MYBATIS_PLUS_TOKEN = "#{%s.paramNameValuePairs.%s}";

    private static final String MP_GENERAL_PARAMNAME = "MPGENVAL";

    private static final String DEFAULT_PARAM_ALIAS = "ew";
    private List<ISqlSegment> expression = new ArrayList<>();
    private final Map<String, Object> paramNameValuePairs = new HashMap<>();
    private final AtomicInteger paramNameSeq = new AtomicInteger(0);
    protected String paramAlias = null;

    public SqlCondition apply(String condition) {
        expression.add(() -> condition);
        return this;
    }

    public SqlCondition and(Function<SqlCondition, SqlCondition> func) {
        return addNestedCondition(func, AND);
    }

    public SqlCondition or(String column, Object val) {
        return this.addCondition(column, OR, val);
    }

    public SqlCondition or(Function<SqlCondition, SqlCondition> func) {
        return addNestedCondition(func, OR);
    }

    public SqlCondition in(String condition) {
        return this.addNestedCondition(condition, IN);
    }

    public SqlCondition notIn(String condition) {
        return this.not().in(condition);
    }

    /**
     * LIKE '%值%'
     */
    public SqlCondition like(String condition) {
        expression.add(LIKE);
        expression.add(() -> "'%");
        expression.add(() -> condition);
        expression.add(() -> "%'");
        return this;
    }

    /**
     * LIKE '%值'
     */
    public SqlCondition likeLeft(String condition) {
        expression.add(LIKE);
        expression.add(() -> "'%");
        expression.add(() -> condition);
        expression.add(() -> "'");
        return this;
    }

    /**
     * LIKE '值%'
     */
    public SqlCondition likeRight(String condition) {
        expression.add(LIKE);
        expression.add(() -> "'");
        expression.add(() -> condition);
        expression.add(() -> "%'");
        return this;
    }

    /**
     * 等于 =
     */
    public SqlCondition eq(String column, Object val) {
        return this.addCondition(column, EQ, val);
    }

    /**
     * 不等于 <>
     */
    public SqlCondition ne(String column, Object val) {
        return this.addCondition(column, NE, val);
    }

    /**
     * 大于 >
     */
    public SqlCondition gt(String column, Object val) {
        return this.addCondition(column, GT, val);
    }

    /**
     * 大于等于 >=
     */
    public SqlCondition ge(String column, Object val) {
        return this.addCondition(column, GE, val);
    }

    /**
     * 小于 <
     */
    public SqlCondition lt(String column, Object val) {
        return this.addCondition(column, LT, val);
    }

    /**
     * 小于等于 <=
     */
    public SqlCondition le(String column, Object val) {
        return this.addCondition(column, LE, val);
    }

    /**
     * 字段 IS NULL
     */
    public SqlCondition isNull(String column) {
        expression.add(() -> column);
        expression.add(IS_NULL);
        return this;
    }

    /**
     * 字段 IS NOT NULL
     */
    public SqlCondition isNotNull(String column) {
        expression.add(() -> column);
        expression.add(IS_NOT_NULL);
        return this;
    }

    /**
     * 分组：GROUP BY 字段, ...
     */
    public SqlCondition groupBy(String column) {
        expression.add(GROUP_BY);
        expression.add(() -> column);
        return this;
    }

    /**
     * 排序：ORDER BY 字段, ...
     */
    public SqlCondition orderBy(String column) {
        expression.add(ORDER_BY);
        expression.add(() -> column);
        return this;
    }

    /**
     * HAVING 关键词
     */
    public SqlCondition having() {
        expression.add(HAVING);
        return this;
    }

    /**
     * exists ( sql 语句 )
     */
    public SqlCondition exists(String condition) {
        return this.addNestedCondition(condition, EXISTS);
    }

    /**
     * BETWEEN 值1 AND 值2
     */
    public SqlCondition between(String condition, Object val1, Object val2) {
        expression.add(() -> condition);
        expression.add(BETWEEN);
        expression.add(() -> "val1");
        expression.add(AND);
        expression.add(() -> "val2");
        return this;
    }

    /**
     * LAST 拼接在 SQL 末尾
     */
    public SqlCondition last(String condition) {
        expression.add(() -> condition);
        return this;
    }

    /**
     * NOT 关键词
     */
    protected SqlCondition not() {
        expression.add(NOT);
        return this;
    }

    /**
     * <p>
     * 普通查询条件
     * </p>
     *
     * @param column     字段
     * @param sqlKeyword SQL 关键词
     * @param val        条件值
     * @return
     */
    protected SqlCondition addCondition(String column, SqlKeyword sqlKeyword, Object val) {
        expression.add(() -> column);
        expression.add(sqlKeyword);
        expression.add(() -> this.formatSql("{0}", val));
        return this;
    }

    /**
     * <p>
     * 嵌套查询条件
     * </p>
     *
     * @param val        查询条件值
     * @param sqlKeyword SQL 关键词
     * @return
     */
    protected SqlCondition addNestedCondition(Object val, SqlKeyword sqlKeyword) {
        expression.add(sqlKeyword);
        expression.add(() -> this.formatSql("({0})", val));
        return this;
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
    protected SqlCondition addNestedCondition(Function<SqlCondition, SqlCondition> condition, SqlKeyword sqlKeyword) {
        expression.add(sqlKeyword);
        expression.add(() -> "(");
        expression.add(condition.apply(new SqlCondition()));
        expression.add(() -> ")");
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
