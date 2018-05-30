package com.baomidou.mybatisplus.core.conditions;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ming
 * @since 2018/5/30
 */
public class StrUpdWrapper<T> extends AbstractUpdWrapper<T, String, StrUpdWrapper<T>> {

    public StrUpdWrapper() {
        /* 注意，传入查询参数 */
    }

    public StrUpdWrapper(T entity) {
        this.entity = entity;
    }

    public StrUpdWrapper(T entity, String sqlSet) {
        this.entity = entity;
        this.sqlSet = sqlSet;
    }

    private StrUpdWrapper(T entity, String sqlSet, AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        this.entity = entity;
        this.sqlSet = sqlSet;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
    }

    @Override
    public String columnToString(String column) {
        return column;
    }

    @Override
    protected StrUpdWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new StrUpdWrapper<>(entity, sqlSet, paramNameSeq, paramNameValuePairs);
    }
}
