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
package com.baomidou.mybatisplus.core.handlers;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * 自定义枚举属性转换器
 *
 * @author hubin
 * @since 2017-10-11
 */
public class MybatisEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    interface FuncWithException<T, U, R> {
        R apply(T t, U u) throws SQLException;
    }

    private static final Map<Class<?>, FuncWithException<ResultSet, String, Object>> QUERY_RESULT_BY_NAME = new HashMap<>();
    private static final Map<Class<?>, FuncWithException<ResultSet, Integer, Object>> QUERY_RESULT_BY_INDEX = new HashMap<>();
    private static final Map<Class<?>, FuncWithException<CallableStatement, Integer, Object>> QUERY_RESULT_USE_CALLABLE = new HashMap<>();

    static {
        QUERY_RESULT_BY_NAME.put(Boolean.class, ResultSet::getBoolean);
        QUERY_RESULT_BY_NAME.put(Byte.class, ResultSet::getByte);
        QUERY_RESULT_BY_NAME.put(Character.class, ResultSet::getInt);
        QUERY_RESULT_BY_NAME.put(Double.class, ResultSet::getDouble);
        QUERY_RESULT_BY_NAME.put(Float.class, ResultSet::getFloat);
        QUERY_RESULT_BY_NAME.put(Integer.class, ResultSet::getInt);
        QUERY_RESULT_BY_NAME.put(Long.class, ResultSet::getLong);
        QUERY_RESULT_BY_NAME.put(Short.class, ResultSet::getShort);
        QUERY_RESULT_BY_NAME.put(String.class, ResultSet::getString);

        QUERY_RESULT_BY_INDEX.put(Boolean.class, ResultSet::getBoolean);
        QUERY_RESULT_BY_INDEX.put(Byte.class, ResultSet::getByte);
        QUERY_RESULT_BY_INDEX.put(Character.class, ResultSet::getInt);
        QUERY_RESULT_BY_INDEX.put(Double.class, ResultSet::getDouble);
        QUERY_RESULT_BY_INDEX.put(Float.class, ResultSet::getFloat);
        QUERY_RESULT_BY_INDEX.put(Integer.class, ResultSet::getInt);
        QUERY_RESULT_BY_INDEX.put(Long.class, ResultSet::getLong);
        QUERY_RESULT_BY_INDEX.put(Short.class, ResultSet::getShort);
        QUERY_RESULT_BY_INDEX.put(String.class, ResultSet::getString);

        QUERY_RESULT_USE_CALLABLE.put(Boolean.class, CallableStatement::getBoolean);
        QUERY_RESULT_USE_CALLABLE.put(Byte.class, CallableStatement::getByte);
        QUERY_RESULT_USE_CALLABLE.put(Character.class, CallableStatement::getInt);
        QUERY_RESULT_USE_CALLABLE.put(Double.class, CallableStatement::getDouble);
        QUERY_RESULT_USE_CALLABLE.put(Float.class, CallableStatement::getFloat);
        QUERY_RESULT_USE_CALLABLE.put(Integer.class, CallableStatement::getInt);
        QUERY_RESULT_USE_CALLABLE.put(Long.class, CallableStatement::getLong);
        QUERY_RESULT_USE_CALLABLE.put(Short.class, CallableStatement::getShort);
        QUERY_RESULT_USE_CALLABLE.put(String.class, CallableStatement::getString);
    }

    private static final Map<String, String> TABLE_METHOD_OF_ENUM_TYPES = new ConcurrentHashMap<>();
    private static final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();
    private final Class<E> enumClassType;
    private final Class<?> propertyType;
    private final Invoker getInvoker;

    public MybatisEnumTypeHandler(Class<E> enumClassType) {
        if (enumClassType == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.enumClassType = enumClassType;
        MetaClass metaClass = MetaClass.forClass(enumClassType, REFLECTOR_FACTORY);
        String name = "value";
        if (!IEnum.class.isAssignableFrom(enumClassType)) {
            name = findEnumValueFieldName(this.enumClassType).orElseThrow(() -> new IllegalArgumentException(String.format("Could not find @EnumValue in Class: %s.", this.enumClassType.getName())));
        }
        this.propertyType = ReflectionKit.resolvePrimitiveIfNecessary(metaClass.getGetterType(name));
        this.getInvoker = metaClass.getGetInvoker(name);
    }

    /**
     * 查找标记标记EnumValue字段
     *
     * @param clazz class
     * @return EnumValue字段
     * @since 3.3.1
     */
    public static Optional<String> findEnumValueFieldName(Class<?> clazz) {
        if (clazz != null && clazz.isEnum()) {
            String className = clazz.getName();
            return Optional.ofNullable(CollectionUtils.computeIfAbsent(TABLE_METHOD_OF_ENUM_TYPES, className, key -> {
                Optional<Field> fieldOptional = findEnumValueAnnotationField(clazz);
                return fieldOptional.map(Field::getName).orElse(null);
            }));
        }
        return Optional.empty();
    }

    private static Optional<Field> findEnumValueAnnotationField(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(EnumValue.class)).findFirst();
    }

    /**
     * 判断是否为MP枚举处理
     *
     * @param clazz class
     * @return 是否为MP枚举处理
     * @since 3.3.1
     */
    public static boolean isMpEnums(Class<?> clazz) {
        return clazz != null && clazz.isEnum() && (IEnum.class.isAssignableFrom(clazz) || findEnumValueFieldName(clazz).isPresent());
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType)
        throws SQLException {
        if (jdbcType == null) {
            ps.setObject(i, this.getValue(parameter));
        } else {
            // see r3589
            ps.setObject(i, this.getValue(parameter), jdbcType.TYPE_CODE);
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object value = getObject(rs, columnName);
        if (null == value && rs.wasNull()) {
            return null;
        }
        return this.valueOf(value);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = getObject(rs, columnIndex);
        if (null == value && rs.wasNull()) {
            return null;
        }
        return this.valueOf(value);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = getObject(cs, columnIndex);
        if (null == value && cs.wasNull()) {
            return null;
        }
        return this.valueOf(value);
    }

    private E valueOf(Object value) {
        E[] es = this.enumClassType.getEnumConstants();
        return Arrays.stream(es).filter((e) -> equalsValue(value, getValue(e))).findAny().orElse(null);
    }

    /**
     * 值比较
     *
     * @param sourceValue 数据库字段值
     * @param targetValue 当前枚举属性值
     * @return 是否匹配
     * @since 3.3.0
     */
    protected boolean equalsValue(Object sourceValue, Object targetValue) {
        String sValue = StringUtils.toStringTrim(sourceValue);
        String tValue = StringUtils.toStringTrim(targetValue);
        if (sourceValue instanceof Number && targetValue instanceof Number
            && new BigDecimal(sValue).compareTo(new BigDecimal(tValue)) == 0) {
            return true;
        }
        return Objects.equals(sValue, tValue);
    }

    private Object getValue(Object object) {
        try {
            return this.getInvoker.invoke(object, new Object[0]);
        } catch (ReflectiveOperationException e) {
            throw ExceptionUtils.mpe(e);
        }
    }

    private Object getObject(ResultSet rs, String columnName) throws SQLException {
        try {
            return rs.getObject(columnName, this.propertyType);
        } catch (SQLFeatureNotSupportedException e) {
            if (QUERY_RESULT_BY_NAME.containsKey(this.propertyType)) {
                return rs.getString(columnName) == null ? null : QUERY_RESULT_BY_NAME.get(
                    this.propertyType).apply(rs, columnName);
            }
            throw e;
        }
    }

    private Object getObject(ResultSet rs, int columnIdx) throws SQLException {
        try {
            return rs.getObject(columnIdx, this.propertyType);
        } catch (SQLFeatureNotSupportedException e) {
            if (QUERY_RESULT_BY_INDEX.containsKey(this.propertyType)) {
                return rs.getString(columnIdx) == null ? null : QUERY_RESULT_BY_INDEX.get(
                    this.propertyType).apply(rs, columnIdx);
            }
            throw e;
        }
    }

    private Object getObject(CallableStatement cs, int columnIdx) throws SQLException {
        try {
            return cs.getObject(columnIdx, this.propertyType);
        } catch (SQLFeatureNotSupportedException e) {
            if (QUERY_RESULT_USE_CALLABLE.containsKey(this.propertyType)) {
                return cs.getString(columnIdx) == null ? null : QUERY_RESULT_USE_CALLABLE.get(
                    this.propertyType).apply(cs, columnIdx);
            }
            throw e;
        }
    }
}
