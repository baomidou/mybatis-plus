/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.conditions.interfaces.Compare;
import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.interfaces.Join;
import com.baomidou.mybatisplus.core.conditions.interfaces.Nested;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.segments.NormalSegmentList;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static com.baomidou.mybatisplus.core.enums.SqlKeyword.*;
import static com.baomidou.mybatisplus.core.enums.WrapperKeyword.*;
import static java.util.stream.Collectors.joining;

/**
 * 查询条件封装
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class AbstractWrapper<T, R, Children extends AbstractWrapper<T, R, Children>> extends Wrapper<T>
    implements Compare<Children, R>, Nested<Children, Children>, Join<Children>, Func<Children, R> {

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
    protected final Children typedThis = (Children) this;
    protected final String paramAlias = null;
    /**
     * 必要度量
     */
    protected AtomicInteger paramNameSeq;
    protected Map<String, Object> paramNameValuePairs;
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

    public Children setEntity(T entity) {
        this.entity = entity;
        this.initEntityClass();
        return typedThis;
    }

    protected void initEntityClass() {
        if (this.entityClass == null && this.entity != null) {
            this.entityClass = (Class<T>) entity.getClass();
        }
    }

    protected Class<T> getCheckEntityClass() {
        Assert.notNull(entityClass, "entityClass must not null,please set entity before use this method!");
        return entityClass;
    }

    @Override
    public <V> Children allEq(boolean condition, Map<R, V> params, boolean null2IsNull) {
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
    public <V> Children allEq(boolean condition, BiPredicate<R, V> filter, Map<R, V> params, boolean null2IsNull) {
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
    public Children eq(boolean condition, R column, Object val) {
        return addCondition(condition, column, EQ, val);
    }

    @Override
    public Children ne(boolean condition, R column, Object val) {
        return addCondition(condition, column, NE, val);
    }

    @Override
    public Children gt(boolean condition, R column, Object val) {
        return addCondition(condition, column, GT, val);
    }

    @Override
    public Children ge(boolean condition, R column, Object val) {
        return addCondition(condition, column, GE, val);
    }

    @Override
    public Children lt(boolean condition, R column, Object val) {
        return addCondition(condition, column, LT, val);
    }

    @Override
    public Children le(boolean condition, R column, Object val) {
        return addCondition(condition, column, LE, val);
    }

    @Override
    public Children like(boolean condition, R column, Object val) {
        return doIt(condition, () -> columnToString(column), LIKE, () -> formatSql("{0}", StringPool.PERCENT + val + StringPool.PERCENT));
    }

    @Override
    public Children notLike(boolean condition, R column, Object val) {
        return not(condition).like(condition, column, val);
    }

    @Override
    public Children likeLeft(boolean condition, R column, Object val) {
        return doIt(condition, () -> columnToString(column), LIKE, () -> formatSql("{0}", StringPool.PERCENT + val));
    }

    @Override
    public Children likeRight(boolean condition, R column, Object val) {
        return doIt(condition, () -> columnToString(column), LIKE, () -> formatSql("{0}", val + StringPool.PERCENT));
    }

    @Override
    public Children between(boolean condition, R column, Object val1, Object val2) {
        return doIt(condition, () -> columnToString(column), BETWEEN, () -> formatSql("{0}", val1), AND,
            () -> formatSql("{0}", val2));
    }

    @Override
    public Children notBetween(boolean condition, R column, Object val1, Object val2) {
        return not(condition).between(condition, column, val1, val2);
    }

    @Override
    public Children and(boolean condition, Function<Children, Children> func) {
        return and(condition).addNestedCondition(condition, func);
    }

    @Override
    public Children or(boolean condition, Function<Children, Children> func) {
        return or(condition).addNestedCondition(condition, func);
    }

    @Override
    public Children nested(boolean condition, Function<Children, Children> func) {
        return addNestedCondition(condition, func);
    }

    @Override
    public Children or(boolean condition) {
        return doIt(condition, OR);
    }

    @Override
    public Children apply(boolean condition, String applySql, Object... value) {
        return doIt(condition, APPLY, () -> formatSql(applySql, value));
    }

    @Override
    public Children last(boolean condition, String lastSql) {
        if (condition) {
            this.lastSql = StringPool.SPACE + lastSql;
        }
        return typedThis;
    }

    @Override
    public Children exists(boolean condition, String existsSql) {
        return doIt(condition, EXISTS, () -> String.format("(%s)", existsSql));
    }

    @Override
    public Children notExists(boolean condition, String notExistsSql) {
        return not(condition).exists(condition, notExistsSql);
    }

    @Override
    public Children isNull(boolean condition, R column) {
        return doIt(condition, () -> columnToString(column), IS_NULL);
    }

    @Override
    public Children isNotNull(boolean condition, R column) {
        return doIt(condition, () -> columnToString(column), IS_NOT_NULL);
    }

    @Override
    public Children in(boolean condition, R column, Collection<?> coll) {
        return doIt(condition, () -> columnToString(column), IN, inExpression(coll));
    }

    @Override
    public Children notIn(boolean condition, R column, Collection<?> coll) {
        return not(condition).in(condition, column, coll);
    }

    @Override
    public Children inSql(boolean condition, R column, String inValue) {
        return doIt(condition, () -> columnToString(column), IN, () -> String.format("(%s)", inValue));
    }

    @Override
    public Children notInSql(boolean condition, R column, String inValue) {
        return not(condition).inSql(condition, column, inValue);
    }

    @Override
    public Children groupBy(boolean condition, R... columns) {
        if (ArrayUtils.isEmpty(columns)) {
            return typedThis;
        }
        return doIt(condition, GROUP_BY, () -> columnsToString(columns));
    }

    @Override
    public Children orderBy(boolean condition, boolean isAsc, R... columns) {
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
    public Children having(boolean condition, String sqlHaving, Object... params) {
        return doIt(condition, HAVING, () -> formatSqlIfNeed(condition, sqlHaving, params));
    }

    /**
     * 内部自用
     * <p>NOT 关键词</p>
     */
    protected Children not(boolean condition) {
        return doIt(condition, NOT);
    }

    /**
     * 内部自用
     * <p>拼接 AND</p>
     */
    protected Children and(boolean condition) {
        return doIt(condition, AND);
    }

    /**
     * 普通查询条件
     *
     * @param condition  是否执行
     * @param column     属性
     * @param sqlKeyword SQL 关键词
     * @param val        条件值
     */
    protected Children addCondition(boolean condition, R column, SqlKeyword sqlKeyword, Object val) {
        return doIt(condition, () -> columnToString(column), sqlKeyword, () -> formatSql("{0}", val));
    }

    /**
     * 多重嵌套查询条件
     *
     * @param condition 查询条件值
     */
    protected Children addNestedCondition(boolean condition, Function<Children, Children> func) {
        return doIt(condition, LEFT_BRACKET, func.apply(instance()), RIGHT_BRACKET);
    }

    /**
     * 子类返回一个自己的新对象
     */
    protected abstract Children instance();

    /**
     * 格式化SQL
     *
     * @param sqlStr SQL语句部分
     * @param params 参数集
     * @return sql
     */
    protected final String formatSql(String sqlStr, Object... params) {
        return formatSqlIfNeed(true, sqlStr, params);
    }

    /**
     * <p>
     * 根据需要格式化SQL<br>
     * <br>
     * Format SQL for methods: EntityQ<T>.where/and/or...("name={0}", value);
     * ALL the {<b>i</b>} will be replaced with #{MPGENVAL<b>i</b>}<br>
     * <br>
     * ew.where("sample_name=<b>{0}</b>", "haha").and("sample_age &gt;<b>{0}</b>
     * and sample_age&lt;<b>{1}</b>", 18, 30) <b>TO</b>
     * sample_name=<b>#{MPGENVAL1}</b> and sample_age&gt;#<b>{MPGENVAL2}</b> and
     * sample_age&lt;<b>#{MPGENVAL3}</b><br>
     * </p>
     *
     * @param need   是否需要格式化
     * @param sqlStr SQL语句部分
     * @param params 参数集
     * @return sql
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
     * 获取in表达式 包含括号
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
     * 对sql片段进行组装
     *
     * @param condition   是否执行
     * @param sqlSegments sql片段数组
     * @return children
     */
    protected Children doIt(boolean condition, ISqlSegment... sqlSegments) {
        if (condition) {
            expression.add(sqlSegments);
        }
        return typedThis;
    }

    @SuppressWarnings("all")
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
    public String getCustomSqlSegment() {
        MergeSegments expression = getExpression();
        if (Objects.nonNull(expression)) {
            NormalSegmentList normal = expression.getNormal();
            String sqlSegment = getSqlSegment();
            if (StringUtils.isNotEmpty(sqlSegment)) {
                if (normal.isEmpty()) {
                    return sqlSegment;
                } else {
                    return concatWhere(sqlSegment);
                }
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 拼接`WHERE`至SQL前
     *
     * @param sql sql
     * @return 带 where 的 sql
     */
    private String concatWhere(String sql) {
        return Constants.WHERE + " " + sql;
    }


    @Override
    public MergeSegments getExpression() {
        return expression;
    }

    public Map<String, Object> getParamNameValuePairs() {
        return paramNameValuePairs;
    }

    /**
     * 多字段转换为逗号 "," 分割字符串
     *
     * @param columns 多字段
     */
    protected String columnsToString(R... columns) {
        return Arrays.stream(columns).map(this::columnToString).collect(joining(StringPool.COMMA));
    }

    /**
     * 获取 columnName
     */
    protected String columnToString(R column) {
        if (column instanceof String) {
            return (String) column;
        }
        throw ExceptionUtils.mpe("not support this column !");
    }

    @Override
    @SuppressWarnings("all")
    public Children clone() {
        return SerializationUtils.clone(typedThis);
    }
}
