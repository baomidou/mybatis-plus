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
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.support.Property;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

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
    private String sqlSelect;

    public LambdaQueryWrapper() {
        this(null);
    }

    public LambdaQueryWrapper(T entity) {
        this.entity = entity;
        this.initEntityClass();
        this.initNeed();
    }

    LambdaQueryWrapper(T entity, Class<T> entityClass, String sqlSelect, AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs,
                       MergeSegments mergeSegments) {
        this.entity = entity;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.sqlSelect = sqlSelect;
        this.entityClass = entityClass;
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
        if (ArrayUtils.isNotEmpty(columns)) {
            this.sqlSelect = this.columnsToString(columns);
        }
        return typedThis;
    }

    public LambdaQueryWrapper<T> select(Predicate<TableFieldInfo> predicate) {
        return select(entityClass, predicate);
    }

    /**
     * <p>
     * 过滤查询的字段信息(主键除外!)
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
        this.sqlSelect = TableInfoHelper.getTableInfo(getCheckEntityClass()).chooseSelect(predicate);
        return typedThis;
    }

    @Override
    public String getSqlSelect() {
        return sqlSelect;
    }

    /**
     * <p>
     * 用于生成嵌套 sql
     * 故 sqlSelect 不向下传递
     * </p>
     */
    @Override
    protected LambdaQueryWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new LambdaQueryWrapper<>(entity, entityClass, null, paramNameSeq, paramNameValuePairs, new MergeSegments());
    }
}
