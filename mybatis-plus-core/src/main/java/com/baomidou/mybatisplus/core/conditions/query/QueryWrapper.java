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
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;

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
     * 查询字段
     */
    private String sqlSelect;

    public QueryWrapper() {
        this(null);
    }

    public QueryWrapper(T entity) {
        this.entity = entity;
        this.initEntityClass();
        this.initNeed();
    }

    public QueryWrapper(T entity, String... columns) {
        this.entity = entity;
        this.select(columns);
        this.initEntityClass();
        this.initNeed();
    }

    /**
     * 非对外公开的构造方法,只用于生产嵌套 sql
     *
     * @param entityClass 本不应该需要的
     */
    private QueryWrapper(T entity, Class<T> entityClass, AtomicInteger paramNameSeq,
                         Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments) {
        this.entity = entity;
        this.entityClass = entityClass;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
    }

    public QueryWrapper<T> select(String... columns) {
        if (ArrayUtils.isNotEmpty(columns)) {
            this.sqlSelect = String.join(StringPool.COMMA, columns);
        }
        return typedThis;
    }

    public QueryWrapper<T> select(Predicate<TableFieldInfo> predicate) {
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
    public QueryWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        this.entityClass = entityClass;
        this.sqlSelect = TableInfoHelper.getTableInfo(getCheckEntityClass()).chooseSelect(predicate);
        return typedThis;
    }

    /**
     * <p>
     * 返回一个支持 lambda 函数写法的 wrapper
     * </p>
     */
    public LambdaQueryWrapper<T> lambda() {
        return new LambdaQueryWrapper<>(entity, entityClass, sqlSelect, paramNameSeq, paramNameValuePairs, expression);
    }

    @Override
    public String getSqlSelect() {
        return sqlSelect;
    }

    @Override
    protected String columnToString(String column) {
        return column;
    }

    /**
     * <p>
     * 用于生成嵌套 sql
     * 故 sqlSelect 不向下传递
     * </p>
     */
    @Override
    protected QueryWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new QueryWrapper<>(entity, entityClass, paramNameSeq, paramNameValuePairs, new MergeSegments());
    }
}
