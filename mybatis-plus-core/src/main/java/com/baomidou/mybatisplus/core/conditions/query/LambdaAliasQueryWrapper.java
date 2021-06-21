/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.conditions.query;

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

/**
 * 支持联表查询的{@link LambdaQueryWrapper} 且支持自定义主表别名
 *
 * @author huguirong
 * @since 2021-06-21
 */
public class LambdaAliasQueryWrapper<T> extends LambdaQueryWrapper<T> {

    private String alias;

    public LambdaAliasQueryWrapper(String alias) {
        super();
        this.setAlias(alias);
    }

    public LambdaAliasQueryWrapper(T entity, String alias) {
        super(entity);
        this.setAlias(alias);
    }

    public LambdaAliasQueryWrapper(Class<T> entityClass, String alias) {
        super(entityClass);
        this.setAlias(alias);
    }

    LambdaAliasQueryWrapper(T entity, Class<T> entityClass, SharedString sqlSelect, AtomicInteger paramNameSeq,
                            Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments, SharedString paramAlias,
                            SharedString lastSql, SharedString sqlComment, SharedString sqlFirst, String alias) {
        super(entity, entityClass, sqlSelect, paramNameSeq, paramNameValuePairs,
            mergeSegments, paramAlias, lastSql, sqlComment, sqlFirst);
        this.setAlias(alias);
    }

    private void setAlias(String alias) {
        Assert.notEmpty(alias, "This alias is required; it must not be null");
        this.alias = alias;
    }

    @Override
    protected Map<String, ColumnCache> getColumnMap(Class<?> entityClass) {
        Map<String, ColumnCache> columnMap = new HashMap<>();
        Map<String, ColumnCache> cacheColumnMap = LambdaUtils.getColumnMap(entityClass);
        if (cacheColumnMap == null) {
            columnMap = new HashMap<>();
        } else if (!cacheColumnMap.isEmpty()) {
            final String columnPrefix = alias.concat(StringPool.DOT);
            columnMap = cacheColumnMap.entrySet().stream().collect(toMap(Map.Entry::getKey, entry -> {
                ColumnCache cache = entry.getValue();
                /* 将表字段拼上表别名 */
                String aliasColumn = columnPrefix.concat(cache.getColumn());
                return new ColumnCache(aliasColumn, cache.getColumnSelect());
            }));
        }
        /* 给columnMap补充别名扩展字段 */
        Map<String, ColumnCache> aliasColumnMap = LambdaUtils.getAliasColumnMap(entityClass);
        if (ObjectUtils.isNotEmpty(aliasColumnMap)) {
            columnMap.putAll(aliasColumnMap);
        }
        Assert.isFalse(columnMap.isEmpty(), String.format("Can not find lambda cache for this entity [%s]", entityClass.getName()));
        return columnMap;
    }

    @Override
    protected LambdaAliasQueryWrapper<T> instance() {
        return new LambdaAliasQueryWrapper<>(getEntity(), getEntityClass(), null, paramNameSeq, paramNameValuePairs,
            new MergeSegments(), paramAlias, SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString(), alias);
    }
}
