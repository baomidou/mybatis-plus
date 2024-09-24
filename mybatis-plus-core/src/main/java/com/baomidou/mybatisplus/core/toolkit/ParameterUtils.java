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
package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * 参数工具类
 *
 * @author nieqiuqiu
 * @since 2020-01-10
 * @since 3.3.1
 */
public class ParameterUtils {

    private ParameterUtils() {

    }

    /**
     * 查找分页参数
     *
     * @param args 参数对象（可以为 null)
     * @return 分页参数
     */
    public static <E> Optional<IPage<E>> findPage(Object... args) {
        if (args == null) {
            return Optional.empty();
        }

        if (args.length == 1 && args[0] instanceof Map) {
            Collection<?> argValues = ((Map<?, ?>) args[0]).values();
            for (Object arg : argValues) {
                if (arg instanceof IPage) {
                    return Optional.of((IPage<E>) arg);
                }
            }
            return Optional.empty();
        }

        for (Object arg : args) {
            if (arg instanceof IPage) {
                return Optional.of((IPage<E>) arg);
            }
        }
        return Optional.empty();
    }

}
