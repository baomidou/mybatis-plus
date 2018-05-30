package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.support.Property;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ming
 * @since 2018/5/30
 */
public class LamUpdWrapper<T> extends AbstractUpdWrapper<T, Property<T, ?>, LamUpdWrapper<T>> {

    public LamUpdWrapper() {
        /* 注意，传入查询参数 */
    }

    public LamUpdWrapper(T entity) {
        this.entity = entity;
    }

    public LamUpdWrapper(T entity, String sqlSet) {
        this.entity = entity;
        this.sqlSet = sqlSet;
    }

    private LamUpdWrapper(T entity, String sqlSet, AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        this.entity = entity;
        this.sqlSet = sqlSet;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
    }

    @Override
    public String columnToString(Property<T, ?> column) {
        return TableInfoHelper.toColumn(column);
    }

    @Override
    protected LamUpdWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new LamUpdWrapper<>(entity, sqlSet, paramNameSeq, paramNameValuePairs);
    }
}
