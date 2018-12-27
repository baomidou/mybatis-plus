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
package com.baomidou.mybatisplus.extension.service.additional.update;

import com.baomidou.mybatisplus.extension.service.additional.ChainWrapper;

/**
 * 具有更新方法的定义
 *
 * @author miemie
 * @since 2018-12-19
 */
public interface ChainUpdate<T> extends ChainWrapper<T> {

    default int update(T entity) {
        return getBaseMapper().update(entity, getWrapper());
    }
}
