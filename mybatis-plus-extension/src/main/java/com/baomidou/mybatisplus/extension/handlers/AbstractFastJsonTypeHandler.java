package com.baomidou.mybatisplus.extension.handlers;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Fastjson 处理器抽象类 适配 List/Object
 * 原本的FastjsonTypeHandler在反序列化为List的时候List成员类为JsonObject
 * 继承本类并且注解@MappedTypes()为自己想要的类型即可适配 List
 * @author Moose
 * @since 2024/09/06
 */
public abstract class AbstractFastJsonTypeHandler<T> extends BaseTypeHandler<T> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        if (Objects.nonNull(parameter)) {
            ps.setString(i, JSON.toJSONString(parameter));
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return deserialize(rs.getString(columnName));
    }


    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return deserialize(rs.getString(columnIndex));
    }


    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return deserialize(cs.getString(columnIndex));
    }

    @SuppressWarnings("unchecked")
    private T deserialize(String data) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        Class<T> clazz = (Class<T>) getRawType();
        if (data.startsWith("[")) {
            return (T) JSON.parseArray(data, clazz);
        } else {
            return JSON.parseObject(data, clazz);
        }
    }
}
