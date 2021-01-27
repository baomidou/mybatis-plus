/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.conditions.update;

import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.Serializable;

/**
 * @author miemie
 * @since 2018-12-12
 */
public interface Update<Children, R> extends Serializable {

    /**
     * ignore
     */
    default Children set(R column, Object val) {
        return set(true, column, val);
    }

    /**
     * 设置 更新 SQL 的 SET 片段
     *
     * @param condition 是否加入 set
     * @param column    字段
     * @param val       值
     * @return children
     */
    default Children set(boolean condition, R column, Object val) {
        return set(condition, column, val, null, null, null);
    }

    /**
     * ignore
     */
    default Children set(R column, Object val, Class<? extends TypeHandler<?>> typeHandler) {
        return set(true, column, val, typeHandler, null, null);
    }

    /**
     * 设置 更新 SQL 的 SET 片段
     *
     * @param condition 是否加入 set
     * @param column    字段
     * @param val       值
     * @return children
     */
    default Children set(boolean condition, R column, Object val, Class<? extends TypeHandler<?>> typeHandler) {
        return set(condition, column, val, typeHandler, null, null);
    }

    /**
     * ignore
     */
    default Children set(R column, Object val, Class<? extends TypeHandler<?>> typeHandler, JdbcType jdbcType) {
        return set(true, column, val, typeHandler, jdbcType, null);
    }

    /**
     * ignore
     */
    default Children set(boolean condition, R column, Object val, Class<? extends TypeHandler<?>> typeHandler, JdbcType jdbcType) {
        return set(condition, column, val, typeHandler, jdbcType, null);
    }

    /**
     * ignore
     */
    default Children set(R column, Object val, Class<? extends TypeHandler<?>> typeHandler, JdbcType jdbcType, Integer numericScale) {
        return set(true, column, val, typeHandler, jdbcType, numericScale);
    }

    /**
     * 设置 更新 SQL 的 SET 片段
     *
     * @param condition 是否加入 set
     * @param column    字段
     * @param val       值
     * @return children
     */
    default Children set(boolean condition, R column, Object val, Class<? extends TypeHandler<?>> typeHandler, JdbcType jdbcType, Integer numericScale) {
        String mapping = null;
        if (condition) {
            mapping = SqlScriptUtils.convertParamMapping(typeHandler, jdbcType, numericScale);
        }
        return set(condition, column, val, mapping);
    }

    /**
     * ignore
     */
    default Children set(R column, Object val, String mapping) {
        return set(true, column, val, mapping);
    }

    /**
     * 设置 更新 SQL 的 SET 片段
     *
     * @param condition 是否加入 set
     * @param column    字段
     * @param val       值
     * @param mapping   例: javaType=int,jdbcType=NUMERIC,typeHandler=xxx.xxx.MyTypeHandler
     * @return children
     */
    Children set(boolean condition, R column, Object val, String mapping);

    /**
     * ignore
     */
    default Children setSql(String sql) {
        return setSql(true, sql);
    }

    /**
     * 设置 更新 SQL 的 SET 片段
     *
     * @param sql set sql
     * @return children
     */
    Children setSql(boolean condition, String sql);

    /**
     * 获取 更新 SQL 的 SET 片段
     */
    String getSqlSet();
}
