/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.conditions.update;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * Update 条件封装
 * </p>
 *
 * @author yangyuhan
 * @since 2018-11-02
 */
@SuppressWarnings("serial")
public class UpdateWrapper<T> extends AbstractWrapper<T, String, UpdateWrapper<T>> {

    /**
     * SQL 更新字段内容，例如：name='1',age=2
     */
    private List<String> sqlSet;

    public UpdateWrapper() {
        // 如果无参构造函数，请注意实体 NULL 情况 SET 必须有否则 SQL 异常
        this(null);
    }

    public UpdateWrapper(T entity) {
        super.setEntity(entity);
        this.sqlSet = new ArrayList<>();
        this.initNeed();
    }

    private UpdateWrapper(T entity, List<String> sqlSet, AtomicInteger paramNameSeq,
                          Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments) {
        super.setEntity(entity);
        this.sqlSet = sqlSet;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
    }

    /**
     * <p>
     * 返回一个支持 lambda 函数写法的 wrapper
     * </p>
     */
    public LambdaUpdateWrapper<T> lambda() {
        return new LambdaUpdateWrapper<>(entity, sqlSet, paramNameSeq, paramNameValuePairs, expression);
    }



    /**
     * <p>
     * 返回一个支持 kotlin  lambda 函数写法的 wrapper
     * </p>
     */
    public LambdaUpdateWrapperKt<T> lambdaKt() {
        return new LambdaUpdateWrapperKt<>(entity, paramNameSeq, paramNameValuePairs, expression);
    }

    @Override
    public String getSqlSet() {
        if (CollectionUtils.isEmpty(sqlSet)) {
            return null;
        }
        return String.join(StringPool.COMMA, sqlSet);
    }

    /**
     * <p>
     * SQL SET 字段
     * </p>
     *
     * @param column 字段
     * @param val    值
     */
    public UpdateWrapper<T> set(String column, Object val) {
        return this.set(true, column, val);
    }

    /**
     * <p>
     * SQL SET 字段
     * </p>
     *
     * @param condition 操作条件
     * @param column    字段
     * @param val       值
     */
    public UpdateWrapper<T> set(boolean condition, String column, Object val) {
        if (condition) {
            sqlSet.add(String.format("%s=%s", column, formatSql("{0}", val)));
        }
        return typedThis;
    }

    /**
     * <p>
     * SET 部分 SQL
     * </p>
     *
     * @param sql SET 部分内容
     */
    public UpdateWrapper<T> setSql(String sql) {
        sqlSet.add(sql);
        return typedThis;
    }

    @Override
    protected String columnToString(String column) {
        return column;
    }

    @Override
    protected UpdateWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new UpdateWrapper<>(entity, sqlSet, paramNameSeq, paramNameValuePairs, new MergeSegments());
    }
}
