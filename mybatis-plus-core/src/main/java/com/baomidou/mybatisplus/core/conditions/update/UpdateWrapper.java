package com.baomidou.mybatisplus.core.conditions.update;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;

/**
 * <p>
 * Update 条件封装
 * </p>
 *
 * @author hubin miemie HCL
 * @since 2018-05-30
 */
public class UpdateWrapper<T> extends AbstractWrapper<T, String, UpdateWrapper<T>> {

    /**
     * SQL 更新字段内容，例如：name='1',age=2
     */
    protected String sqlSet;

    public UpdateWrapper() {
        this(null, null);
    }

    public UpdateWrapper(T entity) {
        this(entity, null);
    }

    public UpdateWrapper(T entity, String sqlSet) {
        this.entity = entity;
        this.sqlSet = sqlSet;
        this.initNeed();
    }

    private UpdateWrapper(T entity, String sqlSet, AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        this.entity = entity;
        this.sqlSet = sqlSet;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
    }

    public LambdaUpdateWrapper<T> stream() {
        return new LambdaUpdateWrapper<>(this.entity, this.sqlSet);
    }

    @Override
    public String getSqlSet() {
        return sqlSet;
    }

    public void setSqlSet(String sqlSet) {
        this.sqlSet = sqlSet;
    }

    @Override
    public String columnToString(String column) {
        return column;
    }

    @Override
    protected UpdateWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new UpdateWrapper<>(entity, sqlSet, paramNameSeq, paramNameValuePairs);
    }
}
