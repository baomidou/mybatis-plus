package com.baomidou.mybatisplus.test.base.type;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author miemie
 * @since 2019-01-22
 */
public class ListTypeHandler extends BaseTypeHandler<List<?>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<?> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            ps.setString(i, null);
        }
    }

    @Override
    public List<?> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String result = rs.getString(columnName);
        if (StringUtils.isNotEmpty(result)) {
            try {
                return objectMapper.readValue(result, new TypeReference<List<String>>() {
                });
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public List<?> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = rs.getString(columnIndex);
        if (StringUtils.isNotEmpty(result)) {
            try {
                return objectMapper.readValue(result, new TypeReference<List<String>>() {
                });
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public List<?> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String result = cs.getString(columnIndex);
        if (StringUtils.isNotEmpty(result)) {
            try {
                return objectMapper.readValue(result, new TypeReference<List<String>>() {
                });
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }
}
