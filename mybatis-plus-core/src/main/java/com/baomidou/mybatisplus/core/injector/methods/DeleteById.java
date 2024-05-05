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
package com.baomidou.mybatisplus.core.injector.methods;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * 根据 ID 删除
 *
 * @author hubin
 * @since 2018-04-06
 */
public class DeleteById extends AbstractMethod {

    public DeleteById() {
        this(SqlMethod.DELETE_BY_ID.getMethod());
    }

    /**
     * @param name 方法名
     * @since 3.5.0
     */
    public DeleteById(String name) {
        super(name);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql;
        SqlMethod sqlMethod;
        if (tableInfo.isWithLogicDelete()) {
            sqlMethod = SqlMethod.LOGIC_DELETE_BY_ID;
            List<TableFieldInfo> fieldInfos = tableInfo.getFieldList().stream()
                .filter(TableFieldInfo::isWithUpdateFill)
                .filter(f -> !f.isLogicDelete())
                .collect(toList());
            if (CollectionUtils.isNotEmpty(fieldInfos)) {
                String sqlSet = "SET " + SqlScriptUtils.convertIf(fieldInfos.stream()
                    .map(i -> i.getSqlSet(EMPTY)).collect(joining(EMPTY)), "!@org.apache.ibatis.type.SimpleTypeRegistry@isSimpleType(_parameter.getClass())", true)
                    + tableInfo.getLogicDeleteSql(false, false);
                sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), sqlSet, tableInfo.getKeyColumn(),
                    tableInfo.getKeyProperty(), tableInfo.getLogicDeleteSql(true, true));
            } else {
                sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), sqlLogicSet(tableInfo),
                    tableInfo.getKeyColumn(), tableInfo.getKeyProperty(),
                    tableInfo.getLogicDeleteSql(true, true));
            }
            SqlSource sqlSource = super.createSqlSource(configuration, sql, Object.class);
            return addUpdateMappedStatement(mapperClass, modelClass, methodName, sqlSource);
        } else {
            sqlMethod = SqlMethod.DELETE_BY_ID;
            sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), tableInfo.getKeyColumn(),
                tableInfo.getKeyProperty());
            return this.addDeleteMappedStatement(mapperClass, methodName, super.createSqlSource(configuration, sql, Object.class));
        }
    }
}
