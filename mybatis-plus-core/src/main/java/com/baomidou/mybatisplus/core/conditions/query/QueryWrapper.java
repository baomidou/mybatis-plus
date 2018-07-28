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

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;

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
    private String[] sqlSelect;

    /**
     * 排除字段
     */
    private String[] excludeColumns = new String[]{};

    /**
     * 实体类型
     */
    private Class<?> entityClass;

    public QueryWrapper() {
        this(null,  null);
    }

    public QueryWrapper(T entity) {
        this(entity, null);
    }

    public QueryWrapper(T entity, String... column) {
        this.sqlSelect = column;
        this.entity = entity;
        this.initNeed();
    }

    private QueryWrapper(T entity, String sqlSelect[], AtomicInteger paramNameSeq,
                         Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments) {
        this.entity = entity;
        this.sqlSelect = sqlSelect;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
    }

    @Override
    public String getSqlSelect() {
        //TODO 这里看要不要兼容下原来的sqlSelect，进行切割
        if(ArrayUtils.isNotEmpty(sqlSelect)){
            sqlSelect = Arrays.stream(sqlSelect).filter($this->!Arrays.asList(excludeColumns).contains($this)).toArray(String[]::new);
        }else{
            if(entityClass!=null){
                sqlSelect = TableInfoHelper.getTableColumns(entityClass, excludeColumns);
            }
        }
        return ArrayUtils.isNotEmpty(sqlSelect) ? SqlUtils.stripSqlInjection(Arrays.stream(sqlSelect).collect(Collectors.joining(","))) : null;
    }

    public QueryWrapper<T> select(String... sqlSelect) {
        if (ArrayUtils.isNotEmpty(sqlSelect)) {
            this.sqlSelect = sqlSelect;
        }
        return typedThis;
    }

    public QueryWrapper<T> excludeColumns(Class<?> clazz,String... excludeColumns) {
        this.excludeColumns = excludeColumns;
        this.entityClass = clazz;
        return typedThis;
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
