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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
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


    public QueryWrapper() {
        this(null, null);
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

    /**
     * 排除字段
     *
     * @param entityClass    实体类
     * @param excludeColumns 排除字段列表
     */
    public QueryWrapper<T> excludeColumns(Class<T> entityClass, String... excludeColumns) {
        Assert.notNull(entityClass, "entityClass is not null");
        Assert.notEmpty(excludeColumns, "excludeColumns is not empty");
        this.excludeColumns = excludeColumns;
        this.entityClass = entityClass;
        return typedThis;
    }

    /**
     * <p>
     * 排除字段，该方法请在  setEntity 之后使用，否则无法获知表实体类型
     * </p>
     *
     * @param excludeColumns 排除字段列表
     */
    @SuppressWarnings(value = "unchecked")
    public QueryWrapper<T> excludeColumns(String... excludeColumns) {
        Assert.notNull(entity, "Unable to find entity type, please use method `excludeColumns(Class<T> entityClass, String... excludeColumns)`");
        return excludeColumns((Class<T>) entity.getClass(), excludeColumns);
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
