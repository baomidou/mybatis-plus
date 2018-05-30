/*
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.conditions.where;


import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;

/**
 * <p>
 * Entity 对象封装操作类
 * </p>
 *
 * @author hubin miemie HCL
 * @Date 2018-05-25
 */
public class EntityWrapper<T> extends AbstractWrapper<T, String, EntityWrapper<T>> {

    /**
     * SQL 查询字段内容，例如：id,name,age
     */
    private String sqlSelect;


    @Override
    public String getSqlSelect() {
        return StringUtils.isEmpty(sqlSelect) ? null : SqlUtils.stripSqlInjection(sqlSelect);
    }

    public EntityWrapper<T> setSqlSelect(String sqlSelect) {
        if (StringUtils.isNotEmpty(sqlSelect)) {
            this.sqlSelect = sqlSelect;
        }
        return this;
    }

    public EntityWrapper() {
        this(null, null);
    }

    public EntityWrapper(T entity) {
        this(entity, null);
    }

    public EntityWrapper(T entity, String sqlSelect) {
        this.sqlSelect = sqlSelect;
        this.entity = entity;
        this.initNeed();
    }

    private EntityWrapper(T entity, String sqlSelect, AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        this.entity = entity;
        this.sqlSelect = sqlSelect;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
    }

    public LambdaEntityWrapper<T> stream() {
        return new LambdaEntityWrapper<>(this.entity, this.sqlSelect);
    }

    @Override
    public String columnToString(String column) {
        return column;
    }

    @Override
    protected EntityWrapper<T> instance(AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs) {
        return new EntityWrapper<>(entity, sqlSelect, paramNameSeq, paramNameValuePairs);
    }
}
