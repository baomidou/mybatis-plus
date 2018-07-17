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
package com.baomidou.mybatisplus.core.conditions.query;

import static java.util.stream.Collectors.joining;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
import com.baomidou.mybatisplus.core.toolkit.support.Property;

/**
 * <p>
 * Lambda 语法使用 Wrapper
 * </p>
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
@SuppressWarnings("serial")
public class LambdaQueryWrapper<T> extends AbstractLambdaWrapper<T, LambdaQueryWrapper<T>> implements Serializable {

    /**
     * SQL 查询字段内容，例如：id,name,age
     */
    private List<String> sqlSelect = new ArrayList<>();

    LambdaQueryWrapper(T entity, AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs,
                       MergeSegments mergeSegments) {
        this.entity = entity;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
    }

    @Override
    public String getSqlSelect() {
        if (CollectionUtils.isEmpty(sqlSelect)) {
            return null;
        }
        return SqlUtils.stripSqlInjection(sqlSelect.stream().collect(joining(",")));
    }

    /**
     * <p>
     * SELECT 部分 SQL 设置
     * </p>
     *
     * @param columns 查询字段
     */
    @SafeVarargs
    public final LambdaQueryWrapper<T> select(Property<T, ?>... columns) {
        for (Property<T, ?> column : columns) {
            sqlSelect.add(this.columnToString(column));
        }
        return typedThis;
    }


    @Override
    protected LambdaQueryWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new LambdaQueryWrapper<>(entity, paramNameSeq, paramNameValuePairs, new MergeSegments());
    }
}
