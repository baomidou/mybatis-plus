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

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
import com.baomidou.mybatisplus.core.toolkit.support.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static java.util.stream.Collectors.joining;

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
     * 过滤的字段
     */
    private String predicateSelect;

    /**
     * 查询字段
     */
    private List<String> queryColumn = new ArrayList<>();

    /**
     * 排除字段
     */
    @Deprecated
    private List<String> excludeColumn = new ArrayList<>();

    public LambdaQueryWrapper() {
        this(null);
    }

    public LambdaQueryWrapper(T entity) {
        this.entity = entity;
        this.initEntityClass();
        this.initNeed();
    }

    @SuppressWarnings(value = "unchecked")
    LambdaQueryWrapper(T entity, AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs,
                       MergeSegments mergeSegments) {
        this.entity = entity;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.initEntityClass();
    }

    @Override
    public String getSqlSelect() {
        if (StringUtils.isNotEmpty(predicateSelect)) {
            return predicateSelect;
        }
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

    public LambdaQueryWrapper<T> select(Predicate<TableFieldInfo> predicate) {
        return select(entityClass, predicate);
    }

    /**
     * <p>
     * 过滤查询的字段信息(主键除外!)
     * 目前该方法优先级最高,一旦使用其他的设置select的方法将失效!!!
     * </p>
     * <p>
     * 例1: 只要 java 字段名以 "test" 开头的              -> select(i -> i.getProperty().startsWith("test"))
     * 例2: 只要 java 字段属性是 CharSequence 类型的       -> select(TableFieldInfo::isCharSequence)
     * 例3: 只要 java 字段没有填充策略的                   -> select(i -> i.getFieldFill == FieldFill.DEFAULT)
     * 例4: 要全部字段                                   -> select(i -> true)
     * 例5: 只要主键字段                                 -> select(i -> false)
     * </p>
     *
     * @param predicate 过滤方式
     * @return this
     */
    public LambdaQueryWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        this.entityClass = entityClass;
        this.checkEntityClass();
        this.predicateSelect = TableInfoHelper.getTableInfo(entityClass).chooseSelect(predicate);
        return typedThis;
    }

    /**
     * <p>
     * SELECT 部分 SQL 设置
     * </p>
     *
     * @param excludeColumns 排除的查询字段
     * @deprecated begin 3.0.3,建议使用 {@link #select(Predicate)},请尽快使用新方法,预计 3.0.5 开始移除此方法
     */
    @Deprecated
    @SafeVarargs
    public final LambdaQueryWrapper<T> excludeColumns(Class<T> entityClass, Property<T, ?>... excludeColumns) {
        Assert.notEmpty(excludeColumns, "excludeColumns must not empty");
        this.entityClass = entityClass;
        this.checkEntityClass();
        //todo
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
     * @deprecated begin 3.0.3,建议使用 {@link #select(Predicate)},请尽快使用新方法,预计 3.0.5 开始移除此方法
     */
    @SafeVarargs
    @Deprecated
    @SuppressWarnings(value = "unchecked")
    public final LambdaQueryWrapper<T> excludeColumns(Property<T, ?>... excludeColumns) {
        this.checkEntityClass();
        return excludeColumns(entityClass, excludeColumns);
    }

    @Override
    protected LambdaQueryWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new LambdaQueryWrapper<>(entity, paramNameSeq, paramNameValuePairs, new MergeSegments());
    }
}
