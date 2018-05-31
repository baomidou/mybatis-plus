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

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

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
    private List<String> expression = new ArrayList<>();

    public UpdateWrapper() {
        this(null);
    }

    public UpdateWrapper(T entity) {
        this.entity = entity;
        this.initNeed();
    }

    private UpdateWrapper(T entity, AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        this.entity = entity;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
    }

    public LambdaUpdateWrapper<T> stream() {
        return new LambdaUpdateWrapper<>(entity, paramNameSeq, paramNameValuePairs);
    }

    @Override
    public String getSqlSet() {
        if (CollectionUtils.isEmpty(expression)) {
            return null;
        }
        return expression.stream().collect(joining(","));
    }

    public UpdateWrapper<T> set(String column, Object val) {
        // todo 待优化
        expression.add(String.format("%s=%s", column, val));
        return typedThis();
    }

    @Override
    public String columnToString(String column) {
        return column;
    }

    @Override
    protected UpdateWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new UpdateWrapper<>(entity, paramNameSeq, paramNameValuePairs);
    }
}
