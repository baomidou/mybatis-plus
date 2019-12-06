/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.handlers;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义枚举属性转换器
 *
 * @author hubin
 * @since 2017-10-11
 */
public class MybatisEnumTypeHandler<E extends Enum<?>> extends BaseTypeHandler<Enum<?>> {

    private static ReflectorFactory reflectorFactory = new DefaultReflectorFactory();

    private static final Map<String, String> TABLE_METHOD_OF_ENUM_TYPES = new ConcurrentHashMap<>();

    private final Class<E> type;

    private Invoker invoker;

    public MybatisEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        MetaClass metaClass = MetaClass.forClass(type, reflectorFactory);
        String name = "value";
        if (!IEnum.class.isAssignableFrom(type)) {
            name = TABLE_METHOD_OF_ENUM_TYPES.computeIfAbsent(type.getName(), k -> {
                Field field = dealEnumType(this.type).orElseThrow(() -> new IllegalArgumentException(String.format("Could not find @EnumValue in Class: %s.", type.getName())));
                return field.getName();
            });
        }
        this.invoker = metaClass.getGetInvoker(name);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Enum<?> parameter, JdbcType jdbcType)
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
        if (null == rs.getObject(columnName) && rs.wasNull()) {
            return null;
        }
        return this.valueOf(this.type, rs.getObject(columnName));
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        if (null == rs.getObject(columnIndex) && rs.wasNull()) {
            return null;
        }
        return this.valueOf(this.type, rs.getObject(columnIndex));
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        if (null == cs.getObject(columnIndex) && cs.wasNull()) {
            return null;
        }
        return this.valueOf(this.type, cs.getObject(columnIndex));
    }

    public static Optional<Field> dealEnumType(Class<?> clazz) {
        return clazz.isEnum() ? Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(EnumValue.class)).findFirst() : Optional.empty();
    }

    private E valueOf(Class<E> enumClass, Object value) {
        E[] es = enumClass.getEnumConstants();
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
        if (sourceValue instanceof Number && targetValue instanceof Number
            && new BigDecimal(String.valueOf(sourceValue)).compareTo(new BigDecimal(String.valueOf(targetValue))) == 0) {
            return true;
        }
        return Objects.equals(sourceValue, targetValue);
    }

    private Object getValue(Object object) {
        try {
            return invoker.invoke(object, new Object[0]);
        } catch (ReflectiveOperationException e) {
            throw ExceptionUtils.mpe(e);
        }
    }
}
