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
package com.baomidou.mybatisplus.core.enums;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import lombok.AllArgsConstructor;

/**
 * wrapper 内部使用枚举
 *
 * @author miemie
 * @since 2018-07-30
 */
@AllArgsConstructor
public enum WrapperKeyword implements ISqlSegment {
    /**
     * 只用作于辨识,不用于其他
     */
    APPLY(null);

    private final String keyword;

    @Override
    public String getSqlSegment() {
        return keyword;
    }
}
