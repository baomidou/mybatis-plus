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
package com.baomidou.mybatisplus.core.toolkit.reflect;

/**
 * 泛型类工具（用于隔离Spring的代码）
 *
 * @author noear
 * @author hubin
 * @since 2021-09-03
 */
public class GenericTypeUtils {
    private static IGenericTypeResolver GENERIC_TYPE_RESOLVER;

    /**
     * 获取泛型工具助手
     */
    public static Class<?>[] resolveTypeArguments(final Class<?> clazz, final Class<?> genericIfc) {
        if (null == GENERIC_TYPE_RESOLVER) {
            // 直接使用 spring 静态方法，减少对象创建
            return SpringReflectionHelper.resolveTypeArguments(clazz, genericIfc);
        }
        return GENERIC_TYPE_RESOLVER.resolveTypeArguments(clazz, genericIfc);
    }

    /**
     * 设置泛型工具助手。如果不想使用Spring封装，可以使用前替换掉
     */
    public static void setGenericTypeResolver(IGenericTypeResolver genericTypeResolver) {
        GENERIC_TYPE_RESOLVER = genericTypeResolver;
    }
}
