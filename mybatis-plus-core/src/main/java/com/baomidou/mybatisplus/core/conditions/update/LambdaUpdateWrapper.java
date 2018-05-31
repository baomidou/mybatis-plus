package com.baomidou.mybatisplus.core.conditions.update;

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hubin miemie HCL
 * @since 2018-05-30
 */
public class LambdaUpdateWrapper<T> extends AbstractLambdaWrapper<T, LambdaUpdateWrapper<T>> {
    /**
     * SQL 更新字段内容，例如：name='1',age=2
     */
    protected String sqlSet;

    LambdaUpdateWrapper(T entity, String sqlSet, AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        this.entity = entity;
        this.sqlSet = sqlSet;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
    }

    @Override
    public String getSqlSet() {
        return sqlSet;
    }

    public LambdaUpdateWrapper<T> setSqlSet(String sqlSet) {
        this.sqlSet = sqlSet;
        return typedThis();
    }

    @Override
    protected LambdaUpdateWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new LambdaUpdateWrapper<>(entity, sqlSet, paramNameSeq, paramNameValuePairs);
    }
}
