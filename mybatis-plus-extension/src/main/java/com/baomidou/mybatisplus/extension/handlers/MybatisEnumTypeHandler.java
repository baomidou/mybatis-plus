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
package com.baomidou.mybatisplus.extension.handlers;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * 自定义枚举属性转换器
 *
 * @author hubin
 * @since 2017-10-11
 * @deprecated 3.4.0 @2020-06-23 use {@link com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler}
 */
@Deprecated
public class MybatisEnumTypeHandler<E extends Enum<E>> extends com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler<E> {

    public MybatisEnumTypeHandler(Class<E> type) {
        super(type);
    }

    /**
     * 查找标记EnumValue字段
     *
     * @param clazz class
     * @return EnumValue字段
     * @deprecated 3.3.1 {@link #findEnumValueFieldName(Class)}
     */
    @Deprecated
    public static Optional<Field> dealEnumType(Class<?> clazz) {
        return com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler.dealEnumType(clazz);
    }

    /**
     * 查找标记标记EnumValue字段
     *
     * @param clazz class
     * @return EnumValue字段
     * @since 3.3.1
     */
    public static Optional<String> findEnumValueFieldName(Class<?> clazz) {
        return com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler.findEnumValueFieldName(clazz);
    }

    /**
     * 判断是否为MP枚举处理
     *
     * @param clazz class
     * @return 是否为MP枚举处理
     * @since 3.3.1
     */
    public static boolean isMpEnums(Class<?> clazz) {
        return com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler.isMpEnums(clazz);
    }
    
}
