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
package com.baomidou.mybatisplus.core.conditions.select;

import static java.util.stream.Collectors.joining;

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
import com.baomidou.mybatisplus.core.toolkit.support.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * Lambda 语法使用 Wrapper
 * </p>
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
public class LambdaEntityWrapper<T> extends AbstractLambdaWrapper<T, LambdaEntityWrapper<T>> {

    /**
     * SQL 查询字段内容，例如：id,name,age
     */
    private List<String> sqlSelect = new ArrayList<>();

    LambdaEntityWrapper(T entity, AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        this.entity = entity;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
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
     * @return
     */
    public LambdaEntityWrapper<T> select(Property<T, ?>... columns) {
        for (Property<T, ?> column : columns) {
            sqlSelect.add(this.columnToString(column));
        }
        return typedThis();
    }


    @Override
    protected LambdaEntityWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new LambdaEntityWrapper<>(entity, paramNameSeq, paramNameValuePairs);
    }
}
