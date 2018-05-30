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
package com.baomidou.mybatisplus.core.conditions.where;

import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.support.Property;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 查询条件封装
 * </p>
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
public class LamEntityWrapper<T> extends AbstractEntityWrapper<T, Property<T, ?>, LamEntityWrapper<T>> {

    public LamEntityWrapper() {
        initNeed();
    }

    public LamEntityWrapper(T entity) {
        this.entity = entity;
        initNeed();
    }

    public LamEntityWrapper(T entity, String sqlSelect) {
        this.entity = entity;
        this.sqlSelect = sqlSelect;
        initNeed();
    }

    private LamEntityWrapper(T entity, String sqlSelect, AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        this.entity = entity;
        this.sqlSelect = sqlSelect;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
    }

    @Override
    public String columnToString(Property<T, ?> column) {
        return TableInfoHelper.toColumn(column);
    }

    @Override
    protected LamEntityWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new LamEntityWrapper<>(entity, sqlSelect, paramNameSeq, paramNameValuePairs);
    }
}
