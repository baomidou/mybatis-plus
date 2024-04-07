package com.baomidou.mybatisplus.extension.handlers;


import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.locationtech.jts.geom.Geometry;

import com.baomidou.mybatisplus.extension.toolkit.GeomUtils;

import lombok.extern.slf4j.Slf4j;


/**
 * Geometry 类型处理器
 * <p>仅Mysql支持</p>
 */
@Slf4j
@MappedTypes(value = {Geometry.class})
@SuppressWarnings({"squid:S112", "unused"})
public class GeometryTypeHandler extends BaseTypeHandler<Geometry> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Geometry parameter, JdbcType jdbcType) {
        try {
            ps.setBytes(i, GeomUtils.toMysqlWkb(parameter));
        } catch (SQLException e) {
            throw new RuntimeException("Geometry -> WKB ERR" + e.getMessage());
        }
    }

    @Override
    public Geometry getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return GeomUtils.fromMysqlWkb(rs.getBytes(columnName));
    }

    @Override
    public Geometry getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return GeomUtils.fromMysqlWkb(rs.getBytes(columnIndex));
    }

    @Override
    public Geometry getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return GeomUtils.fromMysqlWkb(cs.getBytes(columnIndex));
    }

}
