/*
 * Copyright (c) 2011-2016, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.service.additional;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.interfaces.Compare;
import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.interfaces.Join;
import com.baomidou.mybatisplus.core.conditions.interfaces.Nested;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

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
    extends Wrapper<T> implements Compare<Children, R>, Func<Children, R>, Join<Children>, Nested<Param, Children>,
    ChainWrapper<T> {
    protected final Children typedThis = (Children) this;
    /**
     * 子类所包装的具体 Wrapper 类型
     */
    protected Param wrapperChildren;
    private BaseMapper<T> baseMapper;

    /**
     * 必须的构造函数
     * 子类必须给 wrapperChildren 赋值
     *
     * @param baseMapper BaseMapper
     */
    public AbstractChainWrapper(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public BaseMapper<T> getBaseMapper() {
        return baseMapper;
    }

    @Override
    public Wrapper<T> getWrapper() {
        return (Wrapper<T>) wrapperChildren;
    }

    private AbstractWrapper getAbstractWrapper() {
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
        getAbstractWrapper().allEq(condition, params, null2IsNull);
        return typedThis;
    }

    @Override
    public <V> Children allEq(boolean condition, BiPredicate<R, V> filter, Map<R, V> params, boolean null2IsNull) {
        getAbstractWrapper().allEq(condition, filter, params, null2IsNull);
        return typedThis;
    }

    @Override
    public Children eq(boolean condition, R column, Object val) {
        getAbstractWrapper().eq(condition, column, val);
        return typedThis;
    }

    @Override
    public Children ne(boolean condition, R column, Object val) {
        getAbstractWrapper().ne(condition, column, val);
        return typedThis;
    }

    @Override
    public Children gt(boolean condition, R column, Object val) {
        getAbstractWrapper().gt(condition, column, val);
        return typedThis;
    }

    @Override
    public Children ge(boolean condition, R column, Object val) {
        getAbstractWrapper().ge(condition, column, val);
        return typedThis;
    }

    @Override
    public Children lt(boolean condition, R column, Object val) {
        getAbstractWrapper().lt(condition, column, val);
        return typedThis;
    }

    @Override
    public Children le(boolean condition, R column, Object val) {
        getAbstractWrapper().le(condition, column, val);
        return typedThis;
    }

    @Override
    public Children between(boolean condition, R column, Object val1, Object val2) {
        getAbstractWrapper().between(condition, column, val1, val2);
        return typedThis;
    }

    @Override
    public Children notBetween(boolean condition, R column, Object val1, Object val2) {
        getAbstractWrapper().notBetween(condition, column, val1, val2);
        return typedThis;
    }

    @Override
    public Children like(boolean condition, R column, Object val) {
        getAbstractWrapper().like(condition, column, val);
        return typedThis;
    }

    @Override
    public Children notLike(boolean condition, R column, Object val) {
        getAbstractWrapper().notLike(condition, column, val);
        return typedThis;
    }

    @Override
    public Children likeLeft(boolean condition, R column, Object val) {
        getAbstractWrapper().likeLeft(condition, column, val);
        return typedThis;
    }

    @Override
    public Children likeRight(boolean condition, R column, Object val) {
        getAbstractWrapper().likeRight(condition, column, val);
        return typedThis;
    }

    @Override
    public Children isNull(boolean condition, R column) {
        getAbstractWrapper().isNull(condition, column);
        return typedThis;
    }

    @Override
    public Children isNotNull(boolean condition, R column) {
        getAbstractWrapper().isNotNull(condition, column);
        return typedThis;
    }

    @Override
    public Children in(boolean condition, R column, Collection<?> coll) {
        getAbstractWrapper().in(condition, column, coll);
        return typedThis;
    }

    @Override
    public Children notIn(boolean condition, R column, Collection<?> coll) {
        getAbstractWrapper().notIn(condition, column, coll);
        return typedThis;
    }

    @Override
    public Children inSql(boolean condition, R column, String inValue) {
        getAbstractWrapper().inSql(condition, column, inValue);
        return typedThis;
    }

    @Override
    public Children notInSql(boolean condition, R column, String inValue) {
        getAbstractWrapper().notInSql(condition, column, inValue);
        return typedThis;
    }

    @Override
    public Children groupBy(boolean condition, R... columns) {
        getAbstractWrapper().groupBy(condition, columns);
        return typedThis;
    }

    @Override
    public Children orderBy(boolean condition, boolean isAsc, R... columns) {
        getAbstractWrapper().orderBy(condition, isAsc, columns);
        return typedThis;
    }

    @Override
    public Children having(boolean condition, String sqlHaving, Object... params) {
        getAbstractWrapper().having(condition, sqlHaving, params);
        return typedThis;
    }

    @Override
    public Children or(boolean condition) {
        getAbstractWrapper().or(condition);
        return typedThis;
    }

    @Override
    public Children apply(boolean condition, String applySql, Object... value) {
        getAbstractWrapper().apply(condition, applySql, value);
        return typedThis;
    }

    @Override
    public Children last(boolean condition, String lastSql) {
        getAbstractWrapper().last(condition, lastSql);
        return typedThis;
    }

    @Override
    public Children exists(boolean condition, String existsSql) {
        getAbstractWrapper().exists(condition, existsSql);
        return typedThis;
    }

    @Override
    public Children notExists(boolean condition, String notExistsSql) {
        getAbstractWrapper().notExists(condition, notExistsSql);
        return typedThis;
    }

    @Override
    public Children and(boolean condition, Function<Param, Param> func) {
        getAbstractWrapper().and(condition, func);
        return typedThis;
    }

    @Override
    public Children or(boolean condition, Function<Param, Param> func) {
        getAbstractWrapper().or(condition, func);
        return typedThis;
    }

    @Override
    public Children nested(boolean condition, Function<Param, Param> func) {
        getAbstractWrapper().nested(condition, func);
        return typedThis;
    }

    @Override
    public String getSqlSegment() {
        throw ExceptionUtils.mpe("can not use this method for \"%s\"", "getSqlSegment");
    }
}
