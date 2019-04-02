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
package com.baomidou.mybatisplus.extension.service.additional;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.interfaces.Compare;
import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.interfaces.Join;
import com.baomidou.mybatisplus.core.conditions.interfaces.Nested;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;

/**
 * 所有包装类都继承此抽象类,此抽象类代理了大部分生成 where 条件的方法
 * <li> 泛型: Children ,表示子类 </li>
 * <li> 泛型: Param ,表示子类所包装的具体 Wrapper 类型 </li>
 *
 * @author miemie
 * @since 2018-12-19
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class AbstractChainWrapper<T, R, Children extends AbstractChainWrapper<T, R, Children, Param>, Param>
    extends Wrapper<T> implements Compare<Children, R>, Func<Children, R>, Join<Children>, Nested<Param, Children> {

    protected final Children typedThis = (Children) this;
    /**
     * 子类所包装的具体 Wrapper 类型
     */
    protected Param wrapperChildren;

    /**
     * 必须的构造函数
     */
    public AbstractChainWrapper() {
    }

    @SuppressWarnings("rawtypes")
	public AbstractWrapper getWrapper() {
        return (AbstractWrapper) wrapperChildren;
    }

    @Override
    public T getEntity() {
        throw ExceptionUtils.mpe("can not use this method for \"%s\"", "getEntity");
    }

    @Override
    public MergeSegments getExpression() {
        throw ExceptionUtils.mpe("can not use this method for \"%s\"", "getExpression");
    }

    @Override
    public String getCustomSqlSegment() {
        throw ExceptionUtils.mpe("can not use this method for \"%s\"", "getCustomSqlSegment");
    }

    @Override
    public <V> Children allEq(boolean condition, Map<R, V> params, boolean null2IsNull) {
        getWrapper().allEq(condition, params, null2IsNull);
        return typedThis;
    }

    @Override
    public <V> Children allEq(boolean condition, BiPredicate<R, V> filter, Map<R, V> params, boolean null2IsNull) {
        getWrapper().allEq(condition, filter, params, null2IsNull);
        return typedThis;
    }

    @Override
    public Children eq(boolean condition, R column, Object val) {
        getWrapper().eq(condition, column, val);
        return typedThis;
    }

    @Override
    public Children ne(boolean condition, R column, Object val) {
        getWrapper().ne(condition, column, val);
        return typedThis;
    }

    @Override
    public Children gt(boolean condition, R column, Object val) {
        getWrapper().gt(condition, column, val);
        return typedThis;
    }

    @Override
    public Children ge(boolean condition, R column, Object val) {
        getWrapper().ge(condition, column, val);
        return typedThis;
    }

    @Override
    public Children lt(boolean condition, R column, Object val) {
        getWrapper().lt(condition, column, val);
        return typedThis;
    }

    @Override
    public Children le(boolean condition, R column, Object val) {
        getWrapper().le(condition, column, val);
        return typedThis;
    }

    @Override
    public Children between(boolean condition, R column, Object val1, Object val2) {
        getWrapper().between(condition, column, val1, val2);
        return typedThis;
    }

    @Override
    public Children notBetween(boolean condition, R column, Object val1, Object val2) {
        getWrapper().notBetween(condition, column, val1, val2);
        return typedThis;
    }

    @Override
    public Children like(boolean condition, R column, Object val) {
        getWrapper().like(condition, column, val);
        return typedThis;
    }

    @Override
    public Children notLike(boolean condition, R column, Object val) {
        getWrapper().notLike(condition, column, val);
        return typedThis;
    }

    @Override
    public Children likeLeft(boolean condition, R column, Object val) {
        getWrapper().likeLeft(condition, column, val);
        return typedThis;
    }

    @Override
    public Children likeRight(boolean condition, R column, Object val) {
        getWrapper().likeRight(condition, column, val);
        return typedThis;
    }

    @Override
    public Children isNull(boolean condition, R column) {
        getWrapper().isNull(condition, column);
        return typedThis;
    }

    @Override
    public Children isNotNull(boolean condition, R column) {
        getWrapper().isNotNull(condition, column);
        return typedThis;
    }

    @Override
    public Children in(boolean condition, R column, Collection<?> coll) {
        getWrapper().in(condition, column, coll);
        return typedThis;
    }

    @Override
    public Children notIn(boolean condition, R column, Collection<?> coll) {
        getWrapper().notIn(condition, column, coll);
        return typedThis;
    }

    @Override
    public Children inSql(boolean condition, R column, String inValue) {
        getWrapper().inSql(condition, column, inValue);
        return typedThis;
    }

    @Override
    public Children notInSql(boolean condition, R column, String inValue) {
        getWrapper().notInSql(condition, column, inValue);
        return typedThis;
    }

    @Override
    public Children groupBy(boolean condition, R... columns) {
        getWrapper().groupBy(condition, columns);
        return typedThis;
    }

    @Override
    public Children orderBy(boolean condition, boolean isAsc, R... columns) {
        getWrapper().orderBy(condition, isAsc, columns);
        return typedThis;
    }

    @Override
    public Children having(boolean condition, String sqlHaving, Object... params) {
        getWrapper().having(condition, sqlHaving, params);
        return typedThis;
    }

    @Override
    public Children or(boolean condition) {
        getWrapper().or(condition);
        return typedThis;
    }

    @Override
    public Children apply(boolean condition, String applySql, Object... value) {
        getWrapper().apply(condition, applySql, value);
        return typedThis;
    }

    @Override
    public Children last(boolean condition, String lastSql) {
        getWrapper().last(condition, lastSql);
        return typedThis;
    }

    @Override
    public Children exists(boolean condition, String existsSql) {
        getWrapper().exists(condition, existsSql);
        return typedThis;
    }

    @Override
    public Children notExists(boolean condition, String notExistsSql) {
        getWrapper().notExists(condition, notExistsSql);
        return typedThis;
    }

    @Override
    public Children and(boolean condition, Function<Param, Param> func) {
        getWrapper().and(condition, func);
        return typedThis;
    }

    @Override
    public Children or(boolean condition, Function<Param, Param> func) {
        getWrapper().or(condition, func);
        return typedThis;
    }

    @Override
    public Children nested(boolean condition, Function<Param, Param> func) {
        getWrapper().nested(condition, func);
        return typedThis;
    }

    @Override
    public String getSqlSegment() {
        throw ExceptionUtils.mpe("can not use this method for \"%s\"", "getSqlSegment");
    }

}
