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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
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
 * Fastjson 实现 JSON 字段类型处理器
 *
 * @author hubin
 * @since 2019-08-25
 */
@Slf4j
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class FastjsonTypeHandler extends BaseTypeHandler<Object> {
    private Class<Object> type;

    public FastjsonTypeHandler(Class<Object> type) {
        if (log.isTraceEnabled()) {
            log.trace("FastjsonTypeHandler(" + type + ")");
        }
        if (null == type) {
            throw new MybatisPlusException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Object o, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, JSON.toJSONString(o, SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullStringAsEmpty));
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return JSON.parseObject(resultSet.getString(s), type);
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return JSON.parseObject(resultSet.getString(i), type);
    }

    @Override
    public Object getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return JSON.parseObject(callableStatement.getString(i), type);
    }
}
