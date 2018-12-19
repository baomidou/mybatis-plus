package com.baomidou.mybatisplus.extension.service.additional;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.interfaces.Compare;
import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.interfaces.Join;
import com.baomidou.mybatisplus.core.conditions.interfaces.Nested;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * , Func<This, R>, Join<This>, Nested<This>
 *
 * @author miemie
 * @since 2018-12-19
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class AbstractChainWrapper<T, R, This extends AbstractChainWrapper<T, R, This>> extends Wrapper<T>
    implements Compare<This, R>, Func<This, R>, Join<This>, Nested<This> {
    protected final This typedThis = (This) this;

    protected abstract AbstractWrapper getWrapper();

    @Override
    public T getEntity() {
        return (T) getWrapper().getEntity();
    }

    @Override
    public MergeSegments getExpression() {
        return getWrapper().getExpression();
    }

    @Override
    public String getCustomSqlSegment() {
        return getWrapper().getCustomSqlSegment();
    }

    @Override
    public <V> This allEq(boolean condition, Map<R, V> params, boolean null2IsNull) {
        getWrapper().allEq(condition, params, null2IsNull);
        return typedThis;
    }

    @Override
    public <V> This allEq(boolean condition, BiPredicate<R, V> filter, Map<R, V> params, boolean null2IsNull) {
        getWrapper().allEq(condition, filter, params, null2IsNull);
        return typedThis;
    }

    @Override
    public This eq(boolean condition, R column, Object val) {
        getWrapper().eq(condition, column, val);
        return typedThis;
    }

    @Override
    public This ne(boolean condition, R column, Object val) {
        getWrapper().ne(condition, column, val);
        return typedThis;
    }

    @Override
    public This gt(boolean condition, R column, Object val) {
        getWrapper().gt(condition, column, val);
        return typedThis;
    }

    @Override
    public This ge(boolean condition, R column, Object val) {
        getWrapper().ge(condition, column, val);
        return typedThis;
    }

    @Override
    public This lt(boolean condition, R column, Object val) {
        getWrapper().lt(condition, column, val);
        return typedThis;
    }

    @Override
    public This le(boolean condition, R column, Object val) {
        getWrapper().le(condition, column, val);
        return typedThis;
    }

    @Override
    public This between(boolean condition, R column, Object val1, Object val2) {
        getWrapper().between(condition, column, val1, val2);
        return typedThis;
    }

    @Override
    public This notBetween(boolean condition, R column, Object val1, Object val2) {
        getWrapper().notBetween(condition, column, val1, val2);
        return typedThis;
    }

    @Override
    public This like(boolean condition, R column, Object val) {
        getWrapper().like(condition, column, val);
        return typedThis;
    }

    @Override
    public This notLike(boolean condition, R column, Object val) {
        getWrapper().notLike(condition, column, val);
        return typedThis;
    }

    @Override
    public This likeLeft(boolean condition, R column, Object val) {
        getWrapper().likeLeft(condition, column, val);
        return typedThis;
    }

    @Override
    public This likeRight(boolean condition, R column, Object val) {
        getWrapper().likeRight(condition, column, val);
        return typedThis;
    }

    @Override
    public This isNull(boolean condition, R column) {
        getWrapper().isNull(condition, column);
        return typedThis;
    }

    @Override
    public This isNotNull(boolean condition, R column) {
        getWrapper().isNotNull(condition, column);
        return typedThis;
    }

    @Override
    public This in(boolean condition, R column, Collection<?> coll) {
        getWrapper().in(condition, column, coll);
        return typedThis;
    }

    @Override
    public This notIn(boolean condition, R column, Collection<?> coll) {
        getWrapper().notIn(condition, column, coll);
        return typedThis;
    }

    @Override
    public This inSql(boolean condition, R column, String inValue) {
        getWrapper().inSql(condition, column, inValue);
        return typedThis;
    }

    @Override
    public This notInSql(boolean condition, R column, String inValue) {
        getWrapper().notInSql(condition, column, inValue);
        return typedThis;
    }

    @Override
    public This groupBy(boolean condition, R... columns) {
        getWrapper().groupBy(condition, columns);
        return typedThis;
    }

    @Override
    public This orderBy(boolean condition, boolean isAsc, R... columns) {
        getWrapper().orderBy(condition, isAsc, columns);
        return typedThis;
    }

    @Override
    public This having(boolean condition, String sqlHaving, Object... params) {
        getWrapper().having(condition, sqlHaving, params);
        return typedThis;
    }

    @Override
    public This or(boolean condition) {
        getWrapper().or(condition);
        return typedThis;
    }

    @Override
    public This apply(boolean condition, String applySql, Object... value) {
        getWrapper().apply(condition, applySql, value);
        return typedThis;
    }

    @Override
    public This last(boolean condition, String lastSql) {
        getWrapper().last(condition, lastSql);
        return typedThis;
    }

    @Override
    public This exists(boolean condition, String existsSql) {
        getWrapper().exists(condition, existsSql);
        return typedThis;
    }

    @Override
    public This notExists(boolean condition, String notExistsSql) {
        getWrapper().notExists(condition, notExistsSql);
        return typedThis;
    }

    @Override
    public This and(boolean condition, Function<This, This> func) {
        getWrapper().and(condition, func);
        return typedThis;
    }

    @Override
    public This or(boolean condition, Function<This, This> func) {
        getWrapper().or(condition, func);
        return typedThis;
    }

    @Override
    public This nested(boolean condition, Function<This, This> func) {
        getWrapper().nested(condition, func);
        return typedThis;
    }

    @Override
    public String getSqlSegment() {
        return getWrapper().getSqlSegment();
    }
}
