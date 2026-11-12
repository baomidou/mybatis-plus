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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Utility methods for MyBatis-Plus enum metadata and value conversion.
 *
 * @author hubin
 * @since 2026-07-01
 */
public final class EnumUtils {

    private EnumUtils() {
        // utility class
    }

    /**
     * Gets cached metadata for an MP enum.
     *
     * @param enumClassType enum class
     * @return enum metadata
     */
    public static EnumMetadata metadata(Class<?> enumClassType) {
        return EnumCache.metadata(enumClassType);
    }

    /**
     * Finds the field annotated with {@code @EnumValue}.
     *
     * @param clazz class
     * @return enum value field name
     */
    public static Optional<String> findEnumValueFieldName(Class<?> clazz) {
        return EnumCache.findEnumValueFieldName(clazz);
    }

    /**
     * Whether the class is an MP enum declared by {@code IEnum} or {@code @EnumValue}.
     *
     * @param clazz class
     * @return true if the class is an MP enum
     */
    public static boolean isMpEnums(Class<?> clazz) {
        return EnumCache.isMpEnums(clazz);
    }

    /**
     * Gets the configured persistent value of an enum constant.
     *
     * @param parameter enum constant
     * @return persistent value
     */
    public static Object getValue(Enum<?> parameter) {
        if (parameter == null) {
            return null;
        }
        return metadata(parameter.getDeclaringClass()).getValue(parameter);
    }

    /**
     * Resolves an enum constant by persistent value.
     *
     * @param enumClassType enum class
     * @param value persistent value
     * @param <E> enum type
     * @return enum constant, or null when value does not match
     */
    public static <E extends Enum<E>> E valueOf(Class<E> enumClassType, Object value) {
        return metadata(enumClassType).valueOf(value);
    }

    /**
     * Normalizes enum values and JDBC values into stable comparison keys.
     *
     * @param value raw value
     * @return normalized value
     */
    public static Object normalizeValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return ((String) value).trim();
        }
        if (value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte) {
            return ((Number) value).longValue();
        }
        if (value instanceof BigInteger) {
            return normalizeBigInteger((BigInteger) value);
        }
        if (value instanceof BigDecimal) {
            return normalizeBigDecimal((BigDecimal) value);
        }
        if (value instanceof Timestamp) {
            return ((Timestamp) value).getTime();
        }
        if (value instanceof Date) {
            return ((Date) value).getTime();
        }
        return value;
    }

    static void putEnumValue(Map<Object, Enum<?>> valueToEnum, Object value, Enum<?> enumConstant) {
        putIfAbsent(valueToEnum, value, enumConstant);
        Object normalizedValue = normalizeValue(value);
        if (!Objects.equals(value, normalizedValue)) {
            putIfAbsent(valueToEnum, normalizedValue, enumConstant);
        }
        putCompatibilityAliases(valueToEnum, value, enumConstant);
    }

    private static Object normalizeBigInteger(BigInteger value) {
        if (value.bitLength() < Long.SIZE) {
            return value.longValue();
        }
        return value;
    }

    private static Object normalizeBigDecimal(BigDecimal value) {
        try {
            return value.longValueExact();
        } catch (ArithmeticException ignored) {
            return value.stripTrailingZeros();
        }
    }

    private static void putCompatibilityAliases(Map<Object, Enum<?>> valueToEnum, Object value, Enum<?> enumConstant) {
        if (value == null) {
            return;
        }
        if (value instanceof Number) {
            putNumberAliases(valueToEnum, (Number) value, enumConstant);
            return;
        }
        if (value instanceof String) {
            putStringAliases(valueToEnum, ((String) value).trim(), enumConstant);
            return;
        }
        if (value instanceof Character) {
            putCharacterAliases(valueToEnum, (Character) value, enumConstant);
            return;
        }
        if (value instanceof Boolean || value instanceof Enum<?>) {
            putIfAbsent(valueToEnum, String.valueOf(value), enumConstant);
        }
    }

    private static void putNumberAliases(Map<Object, Enum<?>> valueToEnum, Number value, Enum<?> enumConstant) {
        String source = String.valueOf(value);
        putIfAbsent(valueToEnum, source, enumConstant);
        Object normalized = normalizeValue(value);
        if (normalized instanceof Long) {
            putIfAbsent(valueToEnum, String.valueOf(normalized), enumConstant);
        } else if (normalized instanceof BigDecimal) {
            putIfAbsent(valueToEnum, ((BigDecimal) normalized).toPlainString(), enumConstant);
        }
    }

    private static void putStringAliases(Map<Object, Enum<?>> valueToEnum, String value, Enum<?> enumConstant) {
        if (value.length() == 1) {
            putIfAbsent(valueToEnum, value.charAt(0), enumConstant);
            putDigitAlias(valueToEnum, value.charAt(0), enumConstant);
        }
        Long longValue = parseCanonicalLong(value);
        if (longValue != null) {
            putIfAbsent(valueToEnum, longValue, enumConstant);
        }
        if ("true".equals(value)) {
            putIfAbsent(valueToEnum, Boolean.TRUE, enumConstant);
        } else if ("false".equals(value)) {
            putIfAbsent(valueToEnum, Boolean.FALSE, enumConstant);
        }
    }

    private static void putCharacterAliases(Map<Object, Enum<?>> valueToEnum, Character value, Enum<?> enumConstant) {
        putIfAbsent(valueToEnum, String.valueOf(value), enumConstant);
        putDigitAlias(valueToEnum, value, enumConstant);
    }

    private static void putDigitAlias(Map<Object, Enum<?>> valueToEnum, char value, Enum<?> enumConstant) {
        if (value >= '0' && value <= '9') {
            putIfAbsent(valueToEnum, (long) (value - '0'), enumConstant);
        }
    }

    private static Long parseCanonicalLong(String value) {
        if (value.isEmpty()) {
            return null;
        }
        try {
            Long longValue = Long.valueOf(value);
            return value.equals(String.valueOf(longValue)) ? longValue : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static void putIfAbsent(Map<Object, Enum<?>> valueToEnum, Object key, Enum<?> enumConstant) {
        valueToEnum.putIfAbsent(key, enumConstant);
    }
}
