/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 * Lambda 语法使用 Wrapper
 * <p>统一处理解析 lambda 获取 column</p>
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
public abstract class AbstractLambdaWrapper<T, Children extends AbstractLambdaWrapper<T, Children>>
    extends AbstractWrapper<T, SFunction<T, ?>, Children> {

    private Map<String, ColumnCache> columnMap = null;
    private boolean initColumnMap = false;

    @Override
    @SafeVarargs
    protected final String columnsToString(SFunction<T, ?>... columns) {
        return columnsToString(true, columns);
    }

    @SafeVarargs
    protected final String columnsToString(boolean onlyColumn, SFunction<T, ?>... columns) {
        return columnsToString(onlyColumn, CollectionUtils.toList(columns));
    }

    protected final String columnsToString(boolean onlyColumn, List<SFunction<T, ?>> columns) {
        return columns.stream().map(i -> columnToString(i, onlyColumn)).collect(joining(StringPool.COMMA));
    }

    @Override
    protected String columnToString(SFunction<T, ?> column) {
        return columnToString(column, true);
    }

    protected String columnToString(SFunction<T, ?> column, boolean onlyColumn) {
        ColumnCache cache = getColumnCache(column);
        return onlyColumn ? cache.getColumn() : cache.getColumnSelect();
    }

    @Override
    @SafeVarargs
    public final Children groupBy(boolean condition, SFunction<T, ?> column, SFunction<T, ?>... columns) {
        return super.groupBy(condition, column, columns);
    }

    @Override
    @SafeVarargs
    public final Children orderBy(boolean condition, boolean isAsc, SFunction<T, ?> column, SFunction<T, ?>... columns) {
        return orderBy(condition, isAsc, column, CollectionUtils.toList(columns));
    }

    @Override
    @SafeVarargs
    public final Children groupBy(SFunction<T, ?> column, SFunction<T, ?>... columns) {
        return doGroupBy(true, column, CollectionUtils.toList(columns));
    }


    @Override
    public Children groupBy(boolean condition, SFunction<T, ?> column, List<SFunction<T, ?>> columns) {
        return doGroupBy(condition,column,columns);
    }

    @Override
    @SafeVarargs
    public final Children orderByAsc(SFunction<T, ?> column, SFunction<T, ?>... columns) {
        return super.orderByAsc(column, columns);
    }

    @Override
    @SafeVarargs
    public final Children orderByAsc(boolean condition, SFunction<T, ?> column, SFunction<T, ?>... columns) {
        return super.orderByAsc(condition, column, columns);
    }

    @Override
    @SafeVarargs
    public final Children orderByDesc(SFunction<T, ?> column, SFunction<T, ?>... columns) {
        return super.orderByDesc(column, columns);
    }

    @Override
    @SafeVarargs
    public final Children orderByDesc(boolean condition, SFunction<T, ?> column, SFunction<T, ?>... columns) {
        return super.orderByDesc(condition, column, columns);
    }


    /**
     * 获取 SerializedLambda 对应的列信息，从 lambda 表达式中推测实体类
     * <p>
     * 如果获取不到列信息，那么本次条件组装将会失败
     *
     * @return 列
     * @throws com.baomidou.mybatisplus.core.exceptions.MybatisPlusException 获取不到列信息时抛出异常
     */
    protected ColumnCache getColumnCache(SFunction<T, ?> column) {
        LambdaMeta meta = LambdaUtils.extract(column);
        String fieldName = PropertyNamer.methodToProperty(meta.getImplMethodName());
        Class<?> instantiatedClass = meta.getInstantiatedClass();
        tryInitCache(instantiatedClass);
        return getColumnCache(fieldName, instantiatedClass);
    }

    private void tryInitCache(Class<?> lambdaClass) {
        if (!initColumnMap) {
            final Class<T> entityClass = getEntityClass();
            if (entityClass != null) {
                lambdaClass = entityClass;
            }
            columnMap = LambdaUtils.getColumnMap(lambdaClass);
            Assert.notNull(columnMap, "can not find lambda cache for this entity [%s]", lambdaClass.getName());
            initColumnMap = true;
        }
    }

    private ColumnCache getColumnCache(String fieldName, Class<?> lambdaClass) {
        ColumnCache columnCache = columnMap.get(LambdaUtils.formatKey(fieldName));
        Assert.notNull(columnCache, "can not find lambda cache for this property [%s] of entity [%s]",
            fieldName, lambdaClass.getName());
        return columnCache;
    }
}
