/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.handlers;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.baomidou.mybatisplus.core.toolkit.EnumUtils;

/**
 * <p>
 * EnumValue 注解自定义枚举属性转换器
 * </p>
 *
 * @author yuxiaobin
 * @date 2018-08-30
 */
public class EnumAnnotationTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private final Class<E> type;

    public EnumAnnotationTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    private static final Log LOGGER = LogFactory.getLog(EnumAnnotationTypeHandler.class);

    private static final Map<Class<?>, Field> TABLE_FIELD_OF_ENUM_TYPES = new ConcurrentHashMap<>();

    public static void addEnumType(Class<?> clazz, Field tableFieldOfEnumType) {
        TABLE_FIELD_OF_ENUM_TYPES.put(clazz, tableFieldOfEnumType);
    }


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Enum parameter, JdbcType jdbcType) throws SQLException {
        Field tableEnumField = TABLE_FIELD_OF_ENUM_TYPES.get(type);
        try {
            if (jdbcType == null) {
                ps.setObject(i, tableEnumField == null ? parameter.name() : tableEnumField.get(parameter));
            } else {
                // see r3589
                ps.setObject(i, tableEnumField == null ? parameter.name() : tableEnumField.get(parameter), jdbcType.TYPE_CODE);
            }
        } catch (IllegalAccessException e) {
            LOGGER.error("unrecognized jdbcType, failed to set StringValue for type=" + parameter);
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getEnumResult(rs.getObject(columnName));
    }

    private E getEnumResult(Object s) {
        if (s == null) {
            return null;
        }
        Field tableEnumField = TABLE_FIELD_OF_ENUM_TYPES.get(type);
        if (tableEnumField != null) {
            return EnumUtils.valueOf(type, s, tableEnumField);
        }
        return Enum.valueOf(type, s.toString());
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getEnumResult(rs.getObject(columnIndex));
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getEnumResult(cs.getString(columnIndex));
    }

}
