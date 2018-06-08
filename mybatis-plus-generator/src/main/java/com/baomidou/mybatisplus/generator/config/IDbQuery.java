/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.generator.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.baomidou.mybatisplus.annotation.DbType;

/**
 * <p>
 * 表数据查询接口
 * </p>
 *
 * @author hubin
 * @since 2018-01-16
 */
public interface IDbQuery {


    /**
     * 数据库类型
     */
    DbType dbType();


    /**
     * 表信息查询 SQL
     */
    String tablesSql();


    /**
     * 表字段信息查询 SQL
     */
    String tableFieldsSql();


    /**
     * 表名称
     */
    String tableName();


    /**
     * 表注释
     */
    String tableComment();


    /**
     * 字段名称
     */
    String fieldName();


    /**
     * 字段类型
     */
    String fieldType();


    /**
     * 字段注释
     */
    String fieldComment();


    /**
     * 主键字段
     */
    String fieldKey();


    /**
     * <p>
     * 判断主键是否为identity，目前仅对mysql进行检查
     * </p>
     *
     * @param results ResultSet
     * @return 主键是否为identity
     * @throws SQLException
     */
    boolean isKeyIdentity(ResultSet results) throws SQLException;


    /**
     * 自定义字段名称
     */
    String[] fieldCustom();
}
