/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.override;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * SelectReturnTypeHandler 静态注册中心。
 * <p>以 {@code Map<Class, Handler>} 存储类型绑定关系，类型由 {@link SelectReturnTypeHandler#getType()} 提供，
 * 查找时通过 {@link Class#isAssignableFrom(Class)} 匹配。</p>
 *
 * <p>注册方式：</p>
 * <pre>{@code
 * SelectReturnTypeHandlerRegistry.register(new MyHandler());
 * }</pre>
 *
 * @author shanhy
 * @since 3.5.18
 */
public final class SelectReturnTypeHandlerRegistry {

    private static final Map<Class<?>, SelectReturnTypeHandler> HANDLERS = new LinkedHashMap<>();

    private SelectReturnTypeHandlerRegistry() {
    }

    /**
     * 注册一个处理器，目标类型由 {@link SelectReturnTypeHandler#getType()} 提供。
     * <p>后注册的同类型处理器会覆盖先注册的。</p>
     *
     * @param handler 处理器实例
     */
    public static synchronized void register(SelectReturnTypeHandler handler) {
        if (handler != null) {
            HANDLERS.put(handler.getType(), handler);
        }
    }

    /**
     * 查找匹配的处理器。
     * <p>遍历所有已注册的类型，通过 {@link Class#isAssignableFrom(Class)} 匹配，
     * 命中第一个即返回。后注册的优先检查。</p>
     *
     * @param returnType Mapper 方法声明的返回类型
     * @return 匹配的处理器，未找到则返回 null
     */
    public static SelectReturnTypeHandler findHandler(Class<?> returnType) {
        if (returnType == null) {
            return null;
        }
        // 反向遍历：后注册的优先
        Class<?>[] keys = HANDLERS.keySet().toArray(new Class<?>[0]);
        for (int i = keys.length - 1; i >= 0; i--) {
            if (keys[i].isAssignableFrom(returnType)) {
                return HANDLERS.get(keys[i]);
            }
        }
        return null;
    }

    /**
     * 获取所有已注册的类型集合（只读）。
     *
     * @return 已注册的类型集合
     */
    public static Set<Class<?>> getRegisteredTypes() {
        return Set.copyOf(HANDLERS.keySet());
    }
}
