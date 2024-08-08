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
package com.baomidou.mybatisplus.extension.conditions.update;

import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;

/**
 * Update Chain Wrapper
 *
 * @author miemie
 * @since 2018-12-19
 */
@SuppressWarnings({"serial"})
public class UpdateChainWrapper<T> extends AbstractChainWrapper<T, String, UpdateChainWrapper<T>, UpdateWrapper<T>>
    implements ChainUpdate<T>, Update<UpdateChainWrapper<T>, String> {

    private final BaseMapper<T> baseMapper;
    private final Class<T> entityClass;

    public UpdateChainWrapper(BaseMapper<T> baseMapper) {
        super();
        this.baseMapper = baseMapper;
        this.entityClass = null;
        super.wrapperChildren = new UpdateWrapper<>();
    }

    public UpdateChainWrapper(Class<T> entityClass) {
        super();
        this.baseMapper = null;
        this.entityClass = entityClass;
        super.wrapperChildren = new UpdateWrapper<>();
    }

    @Override
    public UpdateChainWrapper<T> set(boolean condition, String column, Object val, String mapping) {
        wrapperChildren.set(condition, column, val, mapping);
        return typedThis;
    }

    @Override
    public UpdateChainWrapper<T> setSql(boolean condition, String setSql, Object... params) {
        wrapperChildren.setSql(condition, setSql, params);
        return typedThis;
    }

    @Override
    public UpdateChainWrapper<T> setIncrBy(boolean condition, String column, Number val) {
        wrapperChildren.setIncrBy(condition, column, val);
        return typedThis;
    }

    @Override
    public UpdateChainWrapper<T> setDecrBy(boolean condition, String column, Number val) {
        wrapperChildren.setDecrBy(condition, column, val);
        return typedThis;
    }

    @Override
    public String getSqlSet() {
        throw ExceptionUtils.mpe("can not use this method for \"%s\"", "getSqlSet");
    }

    @Override
    public BaseMapper<T> getBaseMapper() {
        return baseMapper;
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    public LambdaUpdateChainWrapper<T> lambda(){
        return new LambdaUpdateChainWrapper<>(
            baseMapper,
            wrapperChildren.lambda()
        );
    }
}
