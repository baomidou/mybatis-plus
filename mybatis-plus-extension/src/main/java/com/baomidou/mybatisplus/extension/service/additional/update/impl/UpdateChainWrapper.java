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
package com.baomidou.mybatisplus.extension.service.additional.update.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * Update Chain Wrapper
 *
 * @author miemie
 * @since 2018-12-19
 * @deprecated 3.3.0
 */
@Deprecated
public class UpdateChainWrapper<T> extends com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper<T> {

    public UpdateChainWrapper(BaseMapper<T> baseMapper) {
        super(baseMapper);
    }
}
