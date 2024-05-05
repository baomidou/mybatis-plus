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

import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.ChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;

/**
 * 具有查询方法的定义
 *
 * @author miemie
 * @since 2018-12-19
 */
public interface ChainQuery<T> extends ChainWrapper<T> {

    /**
     * 获取集合
     *
     * @return 集合
     */
    default List<T> list() {
        return execute(mapper -> mapper.selectList(getWrapper()));
    }

    /**
     * 获取集合
     *
     * @param page 分页条件
     * @return 集合记录
     * @since 3.5.3.2
     */
    default List<T> list(IPage<T> page) {
        return execute(mapper -> mapper.selectList(page, getWrapper()));
    }

    /**
     * 获取单个
     *
     * @return 单个
     */
    default T one() {
        return execute(mapper -> mapper.selectOne(getWrapper()));
    }

    /**
     * 获取单个
     *
     * @return 单个
     * @since 3.3.0
     */
    default Optional<T> oneOpt() {
        return Optional.ofNullable(one());
    }

    /**
     * 获取 count
     *
     * @return count
     */
    default Long count() {
        return execute(mapper -> SqlHelper.retCount(mapper.selectCount(getWrapper())));
    }

    /**
     * 判断数据是否存在
     *
     * @return true 存在 false 不存在
     */
    default boolean exists() {
        return this.count() > 0;
    }

    /**
     * 获取分页数据
     *
     * @param page 分页条件
     * @return 分页数据
     */
    default <E extends IPage<T>> E page(E page) {
        return execute(mapper -> mapper.selectPage(page, getWrapper()));
    }
}
