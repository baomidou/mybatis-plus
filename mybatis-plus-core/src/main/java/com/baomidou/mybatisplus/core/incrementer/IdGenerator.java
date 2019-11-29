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
package com.baomidou.mybatisplus.core.incrementer;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;

/**
 * Id生成器接口
 *
 * @author sd-wangtaicheng@sdcncsi.com.cn
 * @date 2019/10/15
 * @since 3.2.1
 */
public interface IdGenerator {

    /**
     * 生成Id {@link IdType#ID_WORKER}
     *
     * @param entity 实体
     * @return id
     */
    Number nextId(Object entity);

    /**
     * 获取uuid {@link IdType#UUID}
     *
     * @param entity 实体
     * @return uuid
     */
    default String nextUUID(Object entity) {
        return IdWorker.get32UUID();
    }
}
