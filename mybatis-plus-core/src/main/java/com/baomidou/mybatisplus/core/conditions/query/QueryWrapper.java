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

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * <p>
 * Entity 对象封装操作类
 * </p>
 *
 * @author hubin miemie HCL
 * @since 2018-05-25
 */
@SuppressWarnings("serial")
public class QueryWrapper<T> extends AbstractWrapper<T, String, QueryWrapper<T>> {

    /**
     * 过滤的字段
     */
    private String predicateSelect;

    /**
     * 查询字段
     */
    private String[] sqlSelect;

    /**
     * 排除字段
     */
    @Deprecated
    private String[] excludeColumns = new String[]{};


    public QueryWrapper() {
        this(null);
    }

    public QueryWrapper(T entity) {
        this(entity, null);
    }

    public QueryWrapper(T entity, String... column) {
        this.sqlSelect = column;
        this.entity = entity;
        this.initEntityClass();
        this.initNeed();
    }

    private QueryWrapper(T entity, String sqlSelect[], AtomicInteger paramNameSeq,
                         Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments) {
        this.entity = entity;
        this.sqlSelect = sqlSelect;
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
        //TODO 这里看要不要兼容下原来的sqlSelect，进行切割
        if (ArrayUtils.isNotEmpty(sqlSelect)) {
            List<String> excludeColumnList = Arrays.asList(excludeColumns);
            sqlSelect = Arrays.stream(sqlSelect).filter(i -> !excludeColumnList.contains(i)).toArray(String[]::new);
        } else {
            if (entityClass != null) {
                sqlSelect = TableInfoHelper.getTableColumns(entityClass, excludeColumns);
            }
        }
        return ArrayUtils.isNotEmpty(sqlSelect) ?
            SqlUtils.stripSqlInjection(String.join(StringPool.COMMA, sqlSelect)) : null;
    }

    public QueryWrapper<T> select(String... sqlSelect) {
        if (ArrayUtils.isNotEmpty(sqlSelect)) {
            this.sqlSelect = sqlSelect;
        }
        return typedThis;
    }

    public QueryWrapper<T> select(Predicate<TableFieldInfo> predicate) {
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
    public QueryWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        this.entityClass = entityClass;
        this.checkEntityClass();
        this.predicateSelect = TableInfoHelper.getTableInfo(entityClass).chooseSelect(predicate);
        return typedThis;
    }

    /**
     * 排除字段
     *
     * @param entityClass    实体类
     * @param excludeColumns 排除字段列表
     * @deprecated begin 3.0.3,建议使用 {@link #select(Predicate)},请尽快使用新方法,预计 3.0.5 开始移除此方法
     */
    @Deprecated
    public QueryWrapper<T> excludeColumns(Class<T> entityClass, String... excludeColumns) {
        Assert.notEmpty(excludeColumns, "excludeColumns must not empty");
        this.excludeColumns = excludeColumns;
        this.entityClass = entityClass;
        this.checkEntityClass();
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
    @Deprecated
    @SuppressWarnings("unchecked")
    public QueryWrapper<T> excludeColumns(String... excludeColumns) {
        this.checkEntityClass();
        return excludeColumns(entityClass, excludeColumns);
    }

    /**
     * <p>
     * 返回一个支持 lambda 函数写法的 wrapper
     * </p>
     */
    public LambdaQueryWrapper<T> lambda() {
        return new LambdaQueryWrapper<>(entity, paramNameSeq, paramNameValuePairs, expression);
    }

    @Override
    protected String columnToString(String column) {
        return column;
    }

    @Override
    protected QueryWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new QueryWrapper<>(entity, sqlSelect, paramNameSeq, paramNameValuePairs, new MergeSegments());
    }
}
