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
package com.baomidou.mybatisplus.autoconfigure;

/**
 * Callback interface that can be customized a {@link MybatisPlusProperties} object generated on auto-configuration.
 *
 * <p> 慎用 </p>
 *
 * @author miemie
 * @since 3.1.2
 */
@FunctionalInterface
public interface MybatisPlusPropertiesCustomizer {

    /**
     * Customize the given a {@link MybatisPlusProperties} object.
     *
     * @param properties the MybatisPlusProperties object to customize
     */
    void customize(MybatisPlusProperties properties);
}
