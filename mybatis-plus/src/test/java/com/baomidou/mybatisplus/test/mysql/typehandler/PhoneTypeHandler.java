package com.baomidou.mybatisplus.test.mysql.typehandler;

import com.baomidou.mybatisplus.test.mysql.entity.PhoneNumber;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author junyu
 * @Date 2016-09-09
 */

@MappedTypes(PhoneNumber.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class PhoneTypeHandler extends BaseTypeHandler<PhoneNumber> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PhoneNumber parameter, JdbcType jdbcType) throws SQLException {
        System.err.println("--------- Executing PhoneTypeHandler --------- ");
        ps.setString(i, parameter.getAsString());
    }

    @Override
    public PhoneNumber getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return new PhoneNumber(rs.getString(columnName));
    }

    @Override
    public PhoneNumber getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return new PhoneNumber(rs.getString(columnIndex));
    }

    @Override
    public PhoneNumber getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return new PhoneNumber(cs.getString(columnIndex));
    }
}
