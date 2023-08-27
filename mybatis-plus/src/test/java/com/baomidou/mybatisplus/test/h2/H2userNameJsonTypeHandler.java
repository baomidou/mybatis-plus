package com.baomidou.mybatisplus.test.h2;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.test.h2.entity.H2User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 只做演示其它方法未实现
 */
@Slf4j
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class H2userNameJsonTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String json, JdbcType jdbcType) throws SQLException {
        H2User h2User = JSON.parseObject(json, H2User.class);
        ps.setString(i, h2User.getName());
    }

    @Override
    public String getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return null;
    }

    @Override
    public String getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return null;
    }

    @Override
    public String getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return null;
    }
}

