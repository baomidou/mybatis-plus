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
package com.baomidou.mybatisplus.extension.conditions.query;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;

import java.util.List;
import java.util.function.Predicate;

/**
 * Query Chain Wrapper
 *
 * @author miemie
 * @since 2018-12-19
 */
@SuppressWarnings({"serial"})
public class QueryChainWrapper<T> extends AbstractChainWrapper<T, String, QueryChainWrapper<T>, QueryWrapper<T>>
    implements ChainQuery<T>, Query<QueryChainWrapper<T>, T, String> {

    private final BaseMapper<T> baseMapper;
    private final Class<T> entityClass;

    public QueryChainWrapper(BaseMapper<T> baseMapper) {
        super();
        this.baseMapper = baseMapper;
        this.entityClass = null;
        super.wrapperChildren = new QueryWrapper<>();
    }

    public QueryChainWrapper(Class<T> entityClass) {
        super();
        this.baseMapper = null;
        this.entityClass = entityClass;
        super.wrapperChildren = new QueryWrapper<>();
    }

    @Override
    public QueryChainWrapper<T> select(boolean condition, List<String> columns) {
        wrapperChildren.select(condition, columns);
        return typedThis;
    }

    @Override
    public QueryChainWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        wrapperChildren.select(entityClass, predicate);
        return typedThis;
    }

    @Override
    public String getSqlSelect() {
        throw ExceptionUtils.mpe("can not use this method for \"%s\"", "getSqlSelect");
    }

    @Override
    public BaseMapper<T> getBaseMapper() {
        return baseMapper;
    }

    /**
     * 获取当前实体Class
     *
     * @return Class
     */
    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }


    public LambdaQueryChainWrapper<T> lambda(){
        return new LambdaQueryChainWrapper<>(
            baseMapper,
            wrapperChildren.lambda()
        );
    }

}
