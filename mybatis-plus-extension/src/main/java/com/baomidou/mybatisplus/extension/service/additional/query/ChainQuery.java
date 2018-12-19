/*
 * Copyright (c) 2011-2016, hubin (jobob@qq.com).
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

    default List<T> list() {
        return getBaseMapper().selectList(getWrapper());
    }

    default T one() {
        return getBaseMapper().selectOne(getWrapper());
    }

    default Integer count() {
        return getBaseMapper().selectCount(getWrapper());
    }

    default IPage<T> page(IPage<T> page) {
        return getBaseMapper().selectPage(page, getWrapper());
    }
}
