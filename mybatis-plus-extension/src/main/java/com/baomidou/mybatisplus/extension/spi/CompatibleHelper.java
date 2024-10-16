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
package com.baomidou.mybatisplus.extension.spi;

import java.util.ServiceLoader;

/**
 * 兼容处理辅助类
 */
public class CompatibleHelper {

    private static CompatibleSet COMPATIBLE_SET;

    public static CompatibleSet getCompatibleSet() {
        if (null == COMPATIBLE_SET) {
            ServiceLoader<CompatibleSet> loader = ServiceLoader.load(CompatibleSet.class);
            COMPATIBLE_SET = loader.iterator().next();
        }
        return COMPATIBLE_SET;
    }
}
