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

import lombok.Getter;

import java.util.Map;
import java.util.Objects;

/**
 * Immutable metadata for a MyBatis-Plus enum.
 *
 * @author hubin
 * @since 2026-07-01
 */
public final class EnumMetadata {

    @Getter
    private final Class<?> propertyType;
    private final Enum<?>[] enumConstants;
    private final Map<Object, Enum<?>> valueToEnum;
    private final Map<Enum<?>, Object> enumToValue;
    @Getter
    private final String getterName;

    EnumMetadata(Class<?> propertyType, Enum<?>[] enumConstants, Map<Object, Enum<?>> valueToEnum,
            Map<Enum<?>, Object> enumToValue, String getterName) {
        this.propertyType = propertyType;
        this.enumConstants = enumConstants.clone();
        this.valueToEnum = valueToEnum;
        this.enumToValue = enumToValue;
        this.getterName = getterName;
    }

    /**
     * Gets enum constants.
     *
     * @return cloned enum constants
     */
    public Enum<?>[] getEnumConstants() {
        return enumConstants.clone();
    }

    /**
     * Gets the configured persistent value of an enum constant.
     *
     * @param parameter enum constant
     * @return persistent value
     */
    public Object getValue(Enum<?> parameter) {
        return enumToValue.get(parameter);
    }

    /**
     * Gets the enum constant by persistent value.
     *
     * @param value persistent value
     * @return enum constant, or null when value does not match
     */
    public Enum<?> getEnum(Object value) {
        Enum<?> enumValue = valueToEnum.get(value);
        if (enumValue != null || value == null) {
            return enumValue;
        }
        Object normalizedValue = EnumUtils.normalizeValue(value);
        if (Objects.equals(value, normalizedValue)) {
            return null;
        }
        return valueToEnum.get(normalizedValue);
    }

    /**
     * Gets the enum constant by persistent value.
     *
     * @param value persistent value
     * @param <E> enum type
     * @return enum constant, or null when value does not match
     */
    @SuppressWarnings("unchecked")
    public <E extends Enum<E>> E valueOf(Object value) {
        return (E) getEnum(value);
    }
}
