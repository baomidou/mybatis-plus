/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.handlers;

import com.baomidou.mybatisplus.core.handlers.IJsonTypeHandler;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author miemie
 * @since 2019-11-28
 */
public abstract class AbstractJsonTypeHandler<T> extends BaseTypeHandler<T> implements IJsonTypeHandler<T> {

    protected final Log log = LogFactory.getLog(this.getClass());

    protected final Class<?> type;

    /**
     * @since 3.5.6
     */
    protected Type genericType;

    /**
     * 默认初始化
     *
     * @param type 类型
     */
    public AbstractJsonTypeHandler(Class<?> type) {
        this.type = type;
        if (log.isTraceEnabled()) {
            log.trace(this.getClass().getSimpleName() + "(" + type + ")");
        }
        Assert.notNull(type, "Type argument cannot be null");
    }

    /**
     * 通过字段初始化
     *
     * @param type  类型
     * @param field 字段
     * @since 3.5.6
     */
    public AbstractJsonTypeHandler(Class<?> type, Field field) {
        this(type);
        this.genericType = field.getGenericType();
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        final String json = rs.getString(columnName);
        return StringUtils.isBlank(json) ? null : parse(json);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        final String json = rs.getString(columnIndex);
        return StringUtils.isBlank(json) ? null : parse(json);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        final String json = cs.getString(columnIndex);
        return StringUtils.isBlank(json) ? null : parse(json);
    }

    public Type getFieldType() {
        return this.genericType != null ? this.genericType : this.type;
    }

}
