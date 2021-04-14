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
package com.baomidou.mybatisplus.extension.conditions.update;

import com.baomidou.mybatisplus.extension.conditions.ChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;

/**
 * 具有更新方法的定义
 *
 * @author miemie
 * @since 2018-12-19
 */
public interface ChainUpdate<T> extends ChainWrapper<T> {

    /**
     * 更新数据
     *
     * @return 是否成功
     */
    default boolean update() {
        return update(null);
    }

    /**
     * 更新数据
     *
     * @param entity 实体类
     * @return 是否成功
     */
    default boolean update(T entity) {
        return SqlHelper.retBool(getBaseMapper().update(entity, getWrapper()));
    }

    /**
     * 删除数据
     *
     * @return 是否成功
     */
    default boolean remove() {
        return SqlHelper.retBool(getBaseMapper().delete(getWrapper()));
    }
}
