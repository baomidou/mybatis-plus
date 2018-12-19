package com.baomidou.mybatisplus.extension.service.additional;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.interfaces.Compare;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;

import java.util.Map;
import java.util.function.BiPredicate;

/**
 * , Func<This, R>, Join<This>, Nested<This>
 *
 * @author miemie
 * @since 2018-12-19
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class ChainWrapper<T, R, This extends ChainWrapper<T, R, This>> extends Wrapper<T>
    implements Compare<This, R> {
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
    public String getSqlSegment() {
        return getWrapper().getSqlSegment();
    }
}
