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
package com.baomidou.mybatisplus.extension.conditions;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;

/**
 * 此接口没特殊意义,只是为了减少实现类的代码量,主要在 AbstractChainWrapper 抽象类上实现
 * <p>以及 继承该接口的子接口能直接获取到 BaseMapper 和相应的 Wrapper</p>
 *
 * @author miemie
 * @since 2018-12-19
 */
public interface ChainWrapper<T> {

    /**
     * 获取 BaseMapper
     *
     * @return BaseMapper
     */
    BaseMapper<T> getBaseMapper();

    /**
     * 获取最终拿去执行的 Wrapper
     *
     * @return Wrapper
     */
    Wrapper<T> getWrapper();

    /**
     * 获取当前实体Class
     *
     * @return Class
     */
    Class<T> getEntityClass();

    /**
     * 执行baseMapper操作，有baseMapper操作时使用baseMapper，没有时通过entityClass获取baseMapper，再使用
     *
     * @param function 操作
     * @param <R>      返回值
     * @return 结果
     */
    default <R> R execute(SFunction<BaseMapper<T>, R> function) {
        BaseMapper<T> baseMapper = getBaseMapper();
        if (baseMapper != null) {
            return function.apply(baseMapper);
        }
        return SqlHelper.execute(getEntityClass(), function);
    }
}
