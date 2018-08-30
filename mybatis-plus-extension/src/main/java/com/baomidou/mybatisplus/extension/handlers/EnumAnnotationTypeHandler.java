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
 * 支持
 * </p>
 *
 * @author yuxiaobin
 * @date 2018/8/30
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
                ps.setObject(i, tableEnumField == null ? parameter.name() : tableEnumField.get(parameter), jdbcType.TYPE_CODE); // see r3589
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
