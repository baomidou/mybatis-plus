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

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;

/**
 * Lambda Update Chain Wrapper
 *
 * @author miemie
 * @since 2018-12-19
 */
public class LambdaUpdateChainWrapper<T> extends AbstractChainWrapper<T, SFunction<T, ?>, LambdaUpdateChainWrapper<T>, LambdaUpdateWrapper<T>>
    implements ChainUpdate<T>, Update<LambdaUpdateChainWrapper<T>, SFunction<T, ?>> {

    private final BaseMapper<T> baseMapper;

    public LambdaUpdateChainWrapper(BaseMapper<T> baseMapper) {
        super();
        this.baseMapper = baseMapper;
        super.wrapperChildren = new LambdaUpdateWrapper<>();
    }

    public LambdaUpdateChainWrapper(Class<T> entityClass) {
        super();
        this.baseMapper = null;
        super.wrapperChildren = new LambdaUpdateWrapper<>(entityClass);
    }

    public LambdaUpdateChainWrapper(BaseMapper<T> baseMapper, LambdaUpdateWrapper<T> wrapperChildren) {
        super();
        this.baseMapper = baseMapper;
        super.wrapperChildren = wrapperChildren;
    }

    @Override
    public LambdaUpdateChainWrapper<T> set(boolean condition, SFunction<T, ?> column, Object val, String mapping) {
        wrapperChildren.set(condition, column, val, mapping);
        return typedThis;
    }

    @Override
    public LambdaUpdateChainWrapper<T> setSql(boolean condition, String setSql, Object... params) {
        wrapperChildren.setSql(condition, setSql, params);
        return typedThis;
    }


    /**
     * 字段自增变量 val 值
     *
     * @param condition 条件
     * @param column    字段
     * @param val       值
     * @return this
     * @since 3.5.6
     */
    @Override
    public LambdaUpdateChainWrapper<T> setIncrBy(boolean condition, SFunction<T, ?> column, Number val) {
        wrapperChildren.setIncrBy(condition, column, val);
        return typedThis;
    }

    /**
     * 字段自减变量 val 值
     *
     * @param condition 条件
     * @param column    字段
     * @param val       值
     * @return this
     * @since 3.5.6
     */
    @Override
    public LambdaUpdateChainWrapper<T> setDecrBy(boolean condition, SFunction<T, ?> column, Number val) {
        wrapperChildren.setDecrBy(condition, column, val);
        return typedThis;
    }

    @Override
    public BaseMapper<T> getBaseMapper() {
        return baseMapper;
    }

    @Override
    public Class<T> getEntityClass() {
        return super.wrapperChildren.getEntityClass();
    }
}
