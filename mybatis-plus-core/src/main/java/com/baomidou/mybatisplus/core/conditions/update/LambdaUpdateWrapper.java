package com.baomidou.mybatisplus.core.conditions.update;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.support.Property;

/**
 * @author hubin miemie HCL
 * @since 2018-05-30
 */
public class LambdaUpdateWrapper<T> extends AbstractWrapper<T, Property<T, ?>, LambdaUpdateWrapper<T>> {

    /**
     * SQL 更新字段内容，例如：name='1',age=2
     */
    protected String sqlSet;

    public LambdaUpdateWrapper() {
        this(null, null);
    }

    public LambdaUpdateWrapper(T entity) {
        this(entity, null);
    }

    public LambdaUpdateWrapper(T entity, String sqlSet) {
        this.entity = entity;
        this.sqlSet = sqlSet;
        this.initNeed();
    }

    private LambdaUpdateWrapper(T entity, String sqlSet, AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        this.entity = entity;
        this.sqlSet = sqlSet;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
    }

    @Override
    public String getSqlSet() {
        return sqlSet;
    }

    public void setSqlSet(String sqlSet) {
        this.sqlSet = sqlSet;
    }

    @Override
    public String columnToString(Property<T, ?> column) {
        return TableInfoHelper.toColumn(column);
    }

    @Override
    protected LambdaUpdateWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new LambdaUpdateWrapper<>(entity, sqlSet, paramNameSeq, paramNameValuePairs);
    }
}
