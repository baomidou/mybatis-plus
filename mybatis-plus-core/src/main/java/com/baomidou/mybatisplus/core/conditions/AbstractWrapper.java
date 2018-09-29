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
import static com.baomidou.mybatisplus.core.enums.WrapperKeyword.APPLY;
import static com.baomidou.mybatisplus.core.enums.WrapperKeyword.LEFT_BRACKET;
import static com.baomidou.mybatisplus.core.enums.WrapperKeyword.RIGHT_BRACKET;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.baomidou.mybatisplus.core.conditions.interfaces.Compare;
import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.interfaces.Join;
import com.baomidou.mybatisplus.core.conditions.interfaces.Nested;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.SerializationUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;


/**
 * <p>
 * 查询条件封装
 * </p>
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
@SuppressWarnings("unchecked,serial")
public abstract class AbstractWrapper<T, R, This extends AbstractWrapper<T, R, This>> extends Wrapper<T>
    implements Compare<This, R>, Nested<This>, Join<This>, Func<This, R> {

    /**
     * 前缀
     */
    private static final String MP_GENERAL_PARAMNAME = "MPGENVAL";
    private static final String DEFAULT_PARAM_ALIAS = Constants.WRAPPER;
    /**
     * 占位符
     */
    private static final String PLACE_HOLDER = "{%s}";
    private static final String MYBATIS_PLUS_TOKEN = "#{%s.paramNameValuePairs.%s}";
    protected final This typedThis = (This) this;
    /**
     * 必要度量
     */
    protected AtomicInteger paramNameSeq;
    protected Map<String, Object> paramNameValuePairs;
    protected String paramAlias = null;
    protected String lastSql = StringPool.EMPTY;
    /**
     * 数据库表映射实体类
     */
    protected T entity;
    protected MergeSegments expression;
    /**
     * 实体类型
     */
    protected Class<T> entityClass;

    @Override
    public T getEntity() {
        return entity;
    }

    public This setEntity(T entity) {
        this.entity = entity;
        this.initEntityClass();
        return typedThis;
    }

    protected void initEntityClass() {
        if (this.entity != null) {
            this.entityClass = (Class<T>) entity.getClass();
        }
    }

    protected Class<T> getCheckEntityClass() {
        Assert.notNull(entityClass, "entityClass must not null,please set entity before use this method!");
        return entityClass;
    }

    @Override
    public <V> This allEq(boolean condition, Map<R, V> params, boolean null2IsNull) {
        if (condition && CollectionUtils.isNotEmpty(params)) {
            params.forEach((k, v) -> {
                if (StringUtils.checkValNotNull(v)) {
                    eq(k, v);
                } else {
                    if (null2IsNull) {
                        isNull(k);
                    }
                }
            });
        }
        return typedThis;
    }

    @Override
    public <V> This allEq(boolean condition, BiPredicate<R, V> filter, Map<R, V> params, boolean null2IsNull) {
        if (condition && CollectionUtils.isNotEmpty(params)) {
            params.forEach((k, v) -> {
                if (filter.test(k, v)) {
                    if (StringUtils.checkValNotNull(v)) {
                        eq(k, v);
                    } else {
                        if (null2IsNull) {
                            isNull(k);
                        }
                    }
                }
            });
        }
        return typedThis;
    }

    @Override
    public This eq(boolean condition, R column, Object val) {
        return addCondition(condition, column, EQ, val);
    }

    @Override
    public This ne(boolean condition, R column, Object val) {
        return addCondition(condition, column, NE, val);
    }

    @Override
    public This gt(boolean condition, R column, Object val) {
        return addCondition(condition, column, GT, val);
    }

    @Override
    public This ge(boolean condition, R column, Object val) {
        return addCondition(condition, column, GE, val);
    }

    @Override
    public This lt(boolean condition, R column, Object val) {
        return addCondition(condition, column, LT, val);
    }

    @Override
    public This le(boolean condition, R column, Object val) {
        return addCondition(condition, column, LE, val);
    }

    @Override
    public This like(boolean condition, R column, Object val) {
        return doIt(condition, () -> columnToString(column), LIKE, () -> formatSql("{0}", StringPool.PERCENT + val + StringPool.PERCENT));
    }

    @Override
    public This notLike(boolean condition, R column, Object val) {
        return not(condition).like(condition, column, val);
    }

    @Override
    public This likeLeft(boolean condition, R column, Object val) {
        return doIt(condition, () -> columnToString(column), LIKE, () -> formatSql("{0}", StringPool.PERCENT + val));
    }

    @Override
    public This likeRight(boolean condition, R column, Object val) {
        return doIt(condition, () -> columnToString(column), LIKE, () -> formatSql("{0}", val + StringPool.PERCENT));
    }

    @Override
    public This between(boolean condition, R column, Object val1, Object val2) {
        return doIt(condition, () -> columnToString(column), BETWEEN, () -> formatSql("{0}", val1), AND,
            () -> formatSql("{0}", val2));
    }

    @Override
    public This notBetween(boolean condition, R column, Object val1, Object val2) {
        return not(condition).between(condition, column, val1, val2);
    }

    @Override
    public This and(boolean condition, Function<This, This> func) {
        return and(condition).addNestedCondition(condition, func);
    }

    @Override
    public This or(boolean condition, Function<This, This> func) {
        return or(condition).addNestedCondition(condition, func);
    }

    @Override
    public This nested(boolean condition, Function<This, This> func) {
        return addNestedCondition(condition, func);
    }

    @Override
    public This or(boolean condition) {
        return doIt(condition, OR);
    }

    @Override
    public This apply(boolean condition, String applySql, Object... value) {
        return doIt(condition, APPLY, () -> formatSql(applySql, value));
    }

    @Override
    public This last(boolean condition, String lastSql) {
        if (condition) {
            this.lastSql = StringPool.SPACE + lastSql;
        }
        return typedThis;
    }

    @Override
    public This exists(boolean condition, String existsSql) {
        return doIt(condition, EXISTS, () -> String.format("(%s)", existsSql));
    }

    @Override
    public This notExists(boolean condition, String notExistsSql) {
        return not(condition).exists(condition, notExistsSql);
    }

    @Override
    public This isNull(boolean condition, R column) {
        return doIt(condition, () -> columnToString(column), IS_NULL);
    }

    @Override
    public This isNotNull(boolean condition, R column) {
        return doIt(condition, () -> columnToString(column), IS_NOT_NULL);
    }

    @Override
    public This in(boolean condition, R column, Collection<?> value) {
        if (CollectionUtils.isEmpty(value)) {
            return typedThis;
        }
        return doIt(condition, () -> columnToString(column), IN, inExpression(value));
    }

    @Override
    public This notIn(boolean condition, R column, Collection<?> value) {
        if (CollectionUtils.isEmpty(value)) {
            return typedThis;
        }
        return not(condition).in(condition, column, value);
    }

    @Override
    public This inSql(boolean condition, R column, String inValue) {
        return doIt(condition, () -> columnToString(column), IN, () -> String.format("(%s)", inValue));
    }

    @Override
    public This notInSql(boolean condition, R column, String inValue) {
        return not(condition).inSql(condition, column, inValue);
    }

    @Override
    public This groupBy(boolean condition, R... columns) {
        if (ArrayUtils.isEmpty(columns)) {
            return typedThis;
        }
        return doIt(condition, GROUP_BY, () -> columnsToString(columns));
    }

    @Override
    public This orderBy(boolean condition, boolean isAsc, R... columns) {
        if (ArrayUtils.isEmpty(columns)) {
            return typedThis;
        }
        SqlKeyword mode = isAsc ? ASC : DESC;
        for (R column : columns) {
            doIt(condition, ORDER_BY, () -> columnToString(column), mode);
        }
        return typedThis;
    }

    @Override
    public This having(boolean condition, String sqlHaving, Object... params) {
        return doIt(condition, HAVING, () -> formatSqlIfNeed(condition, sqlHaving, params));
    }

    /**
     * <p>
     * 内部自用
     * </p>
     * NOT 关键词
     */
    protected This not(boolean condition) {
        return doIt(condition, NOT);
    }

    /**
     * <p>
     * 内部自用
     * </p>
     * 拼接 AND
     */
    protected This and(boolean condition) {
        return doIt(condition, AND);
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
     */
    protected This addCondition(boolean condition, R column, SqlKeyword sqlKeyword, Object val) {
        return doIt(condition, () -> columnToString(column), sqlKeyword, () -> formatSql("{0}", val));
    }

    /**
     * <p>
     * 多重嵌套查询条件
     * </p>
     *
     * @param condition 查询条件值
     */
    protected This addNestedCondition(boolean condition, Function<This, This> func) {
        return doIt(condition, LEFT_BRACKET, func.apply(instance(paramNameSeq, paramNameValuePairs)), RIGHT_BRACKET);
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
    protected final String formatSql(String sqlStr, Object... params) {
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
    protected final String formatSqlIfNeed(boolean need, String sqlStr, Object... params) {
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
            .collect(joining(StringPool.COMMA, StringPool.LEFT_BRACKET, StringPool.RIGHT_BRACKET));
    }

    /**
     * 必要的初始化
     */
    protected final void initNeed() {
        paramNameSeq = new AtomicInteger(0);
        paramNameValuePairs = new HashMap<>(16);
        expression = new MergeSegments();
    }

    /**
     * <p>
     * 对sql片段进行组装
     * </p>
     *
     * @param condition   是否执行
     * @param sqlSegments sql片段数组
     */
    protected This doIt(boolean condition, ISqlSegment... sqlSegments) {
        if (condition) {
            expression.add(sqlSegments);
        }
        return typedThis;
    }

    public String getParamAlias() {
        return StringUtils.isEmpty(paramAlias) ? DEFAULT_PARAM_ALIAS : paramAlias;
    }

    @Override
    public String getSqlSegment() {
        String sqlSegment = expression.getSqlSegment();
        if (StringUtils.isNotEmpty(sqlSegment)) {
            return sqlSegment + lastSql;
        }
        if (StringUtils.isNotEmpty(lastSql)) {
            return lastSql;
        }
        return null;
    }

    @Override
    public MergeSegments getExpression() {
        return expression;
    }

    public Map<String, Object> getParamNameValuePairs() {
        return paramNameValuePairs;
    }

    /**
     * <p>
     * 多字段转换为逗号 "," 分割字符串
     * </p>
     *
     * @param columns 多字段
     */
    protected String columnsToString(R... columns) {
        return Arrays.stream(columns).map(this::columnToString).collect(joining(StringPool.COMMA));
    }

    /**
     * 获取 columnName
     */
    protected abstract String columnToString(R column);

    @Override
    public This clone() {
        return SerializationUtils.clone(typedThis);
    }
}
