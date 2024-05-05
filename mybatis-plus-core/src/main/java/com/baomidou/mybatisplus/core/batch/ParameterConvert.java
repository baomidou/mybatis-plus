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
package com.baomidou.mybatisplus.core.batch;

/**
 * @author nieqiurong
 * @since 3.5.4
 */
@FunctionalInterface
public interface ParameterConvert<T> {

    /**
     * 转换当前实体参数为mapper方法参数
     *
     * @param parameter 参数对象
     * @return mapper方法参数.
     */
    Object convert(T parameter);

}
