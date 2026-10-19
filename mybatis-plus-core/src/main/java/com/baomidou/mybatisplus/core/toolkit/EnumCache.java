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
package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.invoker.Invoker;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global cache for MyBatis-Plus enum metadata.
 *
 * @author hubin
 * @since 2026-07-01
 */
final class EnumCache {

    private static final String ENUM_VALUE_PROPERTY = "value";
    private static final String NO_ENUM_VALUE_FIELD = "";
    private static final Object[] EMPTY_ARGS = new Object[0];
    private static final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();
    private static final ConcurrentHashMap<Class<?>, EnumMetadata> CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, String> ENUM_VALUE_FIELD_CACHE = new ConcurrentHashMap<>();

    private EnumCache() {
        // utility class
    }

    static EnumMetadata metadata(Class<?> enumClassType) {
        if (enumClassType == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        return CACHE.computeIfAbsent(enumClassType, EnumCache::createMetadata);
    }

    static Optional<String> findEnumValueFieldName(Class<?> clazz) {
        if (clazz == null || !clazz.isEnum()) {
            return Optional.empty();
        }
        String fieldName = ENUM_VALUE_FIELD_CACHE.computeIfAbsent(clazz, EnumCache::findEnumValueFieldNameOrEmpty);
        return NO_ENUM_VALUE_FIELD.equals(fieldName) ? Optional.empty() : Optional.of(fieldName);
    }

    static boolean isMpEnums(Class<?> clazz) {
        return clazz != null && clazz.isEnum()
                && (IEnum.class.isAssignableFrom(clazz) || findEnumValueFieldName(clazz).isPresent());
    }

    private static EnumMetadata createMetadata(Class<?> enumClassType) {
        if (!enumClassType.isEnum()) {
            throw new IllegalArgumentException("Type argument must be an enum: " + enumClassType.getName());
        }
        MetaClass metaClass = MetaClass.forClass(enumClassType, REFLECTOR_FACTORY);
        String getterName = resolveGetterName(enumClassType);
        Class<?> propertyType = ReflectionKit.resolvePrimitiveIfNecessary(metaClass.getGetterType(getterName));
        Invoker getInvoker = metaClass.getGetInvoker(getterName);
        Enum<?>[] enumConstants = (Enum<?>[]) enumClassType.getEnumConstants();
        Map<Object, Enum<?>> valueToEnum = new HashMap<>(enumConstants.length * 4 + 1);
        Map<Enum<?>, Object> enumToValue = new IdentityHashMap<>(enumConstants.length);
        for (Enum<?> enumConstant : enumConstants) {
            Object value = invokeValue(getInvoker, enumConstant);
            enumToValue.put(enumConstant, value);
            EnumUtils.putEnumValue(valueToEnum, value, enumConstant);
        }
        return new EnumMetadata(propertyType, enumConstants, Collections.unmodifiableMap(valueToEnum),
                Collections.unmodifiableMap(enumToValue), getterName);
    }

    private static String resolveGetterName(Class<?> enumClassType) {
        if (IEnum.class.isAssignableFrom(enumClassType)) {
            return ENUM_VALUE_PROPERTY;
        }
        return findEnumValueFieldName(enumClassType).orElseThrow(
                () -> new IllegalArgumentException("Could not find @EnumValue in Class: " + enumClassType.getName()));
    }

    private static Object invokeValue(Invoker getInvoker, Enum<?> enumConstant) {
        try {
            return getInvoker.invoke(enumConstant, EMPTY_ARGS);
        } catch (ReflectiveOperationException e) {
            throw ExceptionUtils.mpe(e);
        }
    }

    private static String findEnumValueFieldNameOrEmpty(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(EnumValue.class)) {
                return field.getName();
            }
        }
        return NO_ENUM_VALUE_FIELD;
    }
}
