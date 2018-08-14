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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
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
public class LambdaQueryWrapper<T> extends AbstractLambdaWrapper<T, LambdaQueryWrapper<T>> {

    /**
     * 查询字段
     */
    private List<String> queryColumn = new ArrayList<>();

    /**
     * 排除字段
     */
    private List<String> excludeColumn = new ArrayList<>();

    @SuppressWarnings(value = "unchecked")
    LambdaQueryWrapper(T entity, AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs,
                       MergeSegments mergeSegments) {
        this.entity = entity;
        if (entity != null) {
            this.entityClass = (Class<T>) entity.getClass();
        }
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
    }

    @Override
    public String getSqlSelect() {
        if (CollectionUtils.isEmpty(queryColumn)) {
            if (entityClass != null) {
                queryColumn = Arrays.asList(TableInfoHelper.getTableColumns(entityClass, excludeColumn.toArray(new String[0])));
            }
        } else {
            return SqlUtils.stripSqlInjection(queryColumn.stream()
                .filter(i -> !excludeColumn.contains(i)).collect(joining(StringPool.COMMA)));
        }
        return CollectionUtils.isEmpty(queryColumn) ? null : String.join(StringPool.COMMA, queryColumn);
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
            queryColumn.add(this.columnToString(column));
        }
        return typedThis;
    }

    /**
     * <p>
     * SELECT 部分 SQL 设置
     * </p>
     *
     * @param excludeColumns 排除的查询字段
     */
    @SafeVarargs
    public final LambdaQueryWrapper<T> excludeColumns(Class<T> entityClass, Property<T, ?>... excludeColumns) {
        Assert.notNull(entityClass, "entityClass is not null");
        Assert.notEmpty(excludeColumns, "excludeColumns is not empty");
        this.entityClass = entityClass;
        for (Property<T, ?> column : excludeColumns) {
            excludeColumn.add(this.columnToString(column));
        }
        return typedThis;
    }

    /**
     * <p>
     * 排除字段，该方法请在  setEntity 之后使用，否则无法获知表实体类型
     * </p>
     *
     * @param excludeColumns 排除字段列表
     */
    @SafeVarargs
    @SuppressWarnings(value = "unchecked")
    public final LambdaQueryWrapper<T> excludeColumns(Property<T, ?>... excludeColumns) {
        Assert.notNull(entity, "Unable to find entity type, please use method `excludeColumns(Class<T> entityClass, String... excludeColumns)`");
        return excludeColumns((Class<T>) entity.getClass(), excludeColumns);
    }

    @Override
    protected LambdaQueryWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new LambdaQueryWrapper<>(entity, paramNameSeq, paramNameValuePairs, new MergeSegments());
    }
}
