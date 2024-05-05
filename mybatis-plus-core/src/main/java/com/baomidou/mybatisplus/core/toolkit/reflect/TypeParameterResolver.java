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
package com.baomidou.mybatisplus.core.toolkit.reflect;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 类型参数实现收集器，采集类型实现中各个类型参数的实际值
 * <p>
 * Create by hcl at 2023/9/25
 */
public class TypeParameterResolver {
    private final Map<TypeVariable<?>, Type> map;
    private final Set<Type> distinct;

    protected TypeParameterResolver(Map<TypeVariable<?>, Type> map) {
        this.map = map;
        this.distinct = new HashSet<>();
    }

    /**
     * 获取类型上指定索引位置参数的实现信息
     *
     * @param source 类型
     * @param index  索引
     * @param type   实现类型
     * @return 返回类型实现或者 null
     */
    public static Type resolveClassIndexedParameter(Type type, Class<?> source, int index) {
        return calculateParameterValue(resolveParameterValues(type), source.getTypeParameters()[index]);
    }

    /**
     * 计算参数值
     *
     * @param map       变量 Map
     * @param parameter 参数
     * @return 返回参数值
     */
    public static Type calculateParameterValue(Map<TypeVariable<?>, Type> map, TypeVariable<?> parameter) {
        Type res = map.get(parameter);
        while (res instanceof TypeVariable<?>) {
            res = map.get(res);
        }
        return res;
    }

    /**
     * 解析指定类型下的泛型参数实现信息
     *
     * @param from 起始类型
     * @return 返回全部的泛型参数及其映射类型值
     */
    public static Map<TypeVariable<?>, Type> resolveParameterValues(Type from) {
        Map<TypeVariable<?>, Type> map = new HashMap<>();
        new TypeParameterResolver(map).visitType(from);
        return map;
    }

    /**
     * 访问类型，类型中需要关注两个：{@link Class} 和 {@link ParameterizedType}
     *
     * @param type 类型
     */
    public void visitType(Type type) {
        if (!distinct.add(type)) {
            return;
        }

        if (type instanceof Class<?>) {
            visitClass((Class<?>) type);
            return;
        }

        if (type instanceof ParameterizedType) {
            visitParameterizedType((ParameterizedType) type);
        }

    }

    /**
     * 访问类型，类型的树可以分解为父类和接口，这两个地方都要解析。
     *
     * @param c 类
     */
    private void visitClass(Class<?> c) {
        visitType(c.getGenericSuperclass());
        for (Type i : c.getGenericInterfaces()) {
            visitType(i);
        }
    }

    /**
     * 访问参数化类型，类型参数映射的主要逻辑就在这里
     *
     * @param parameterized 参数化类型
     */
    private void visitParameterizedType(ParameterizedType parameterized) {
        Type raw = parameterized.getRawType();
        visitType(raw);

        if (raw instanceof GenericDeclaration) {
            GenericDeclaration declaration = (GenericDeclaration) raw;
            TypeVariable<?>[] parameters = declaration.getTypeParameters();
            Type[] arguments = parameterized.getActualTypeArguments();
            for (int i = 0; i < parameters.length; i++) {
                TypeVariable<?> parameter = parameters[i];
                Type argument = arguments[i];
                map.put(parameter, argument);
                visitType(argument);
            }
        }
    }

}
