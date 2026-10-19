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
package com.baomidou.mybatisplus.core.handlers;

import com.baomidou.mybatisplus.core.toolkit.EnumMetadata;
import com.baomidou.mybatisplus.core.toolkit.EnumUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 自定义枚举属性转换器
 *
 * @author hubin
 * @since 2017-10-11
 */
public final class MybatisEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private final EnumMetadata metadata;

    /**
     * 创建枚举类型处理器。
     *
     * @param enumClassType 枚举类型
     */
    public MybatisEnumTypeHandler(Class<E> enumClassType) {
        if (enumClassType == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.metadata = EnumUtils.metadata(enumClassType);
    }

    /**
     * 查找标记标记EnumValue字段。
     *
     * @param clazz class
     * @return EnumValue字段
     * @since 3.3.1
     */
    public static Optional<String> findEnumValueFieldName(Class<?> clazz) {
        return EnumUtils.findEnumValueFieldName(clazz);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        Object value = metadata.getValue(parameter);
        if (jdbcType == null) {
            ps.setObject(i, value);
        } else {
            // see r3589
            ps.setObject(i, value, jdbcType.TYPE_CODE);
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName, metadata.getPropertyType());
        if (null == value || rs.wasNull()) {
            return null;
        }
        return metadata.valueOf(value);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex, metadata.getPropertyType());
        if (null == value || rs.wasNull()) {
            return null;
        }
        return metadata.valueOf(value);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = cs.getObject(columnIndex, metadata.getPropertyType());
        if (null == value || cs.wasNull()) {
            return null;
        }
        return metadata.valueOf(value);
    }
}
