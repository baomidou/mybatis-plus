/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.service.additional.query;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.additional.ChainWrapper;

import java.util.List;

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
        return getBaseMapper().selectList(getWrapper());
    }

    /**
     * 获取单个
     *
     * @return 单个
     */
    default T one() {
        return getBaseMapper().selectOne(getWrapper());
    }

    /**
     * 获取 count
     *
     * @return count
     */
    default Integer count() {
        return getBaseMapper().selectCount(getWrapper());
    }

    /**
     * 获取分页数据
     *
     * @param page 分页条件
     * @return 分页数据
     */
    default IPage<T> page(IPage<T> page) {
        return getBaseMapper().selectPage(page, getWrapper());
    }
}
