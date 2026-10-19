/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
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
        if (tableInfo.isWithLogicDelete()) {
            List<TableFieldInfo> fieldInfos = tableInfo.getFieldList().stream().filter(TableFieldInfo::isWithUpdateFill)
                    .filter(f -> !f.isLogicDelete()).collect(toList());
            TableFieldInfo logicDeleteField = tableInfo.getLogicDeleteFieldInfo();
            boolean logicDeleteWithFill = logicDeleteField != null && logicDeleteField.isWithUpdateFill();
            if (CollectionUtils.isNotEmpty(fieldInfos) || logicDeleteWithFill) {
                String entityCondition = "@org.apache.ibatis.reflection.SystemMetaObject@forObject(_parameter).findProperty('"
                        + tableInfo.getKeyProperty() + "', false) != null";
                String fillSetSql = fieldInfos.stream().map(i -> i.getSqlSet(EMPTY)).collect(joining(EMPTY));
                String sqlSet;
                if (logicDeleteWithFill) {
                    String fillSql = logicDeleteField.getSqlSet(true, EMPTY);
                    fillSql = fillSql.substring(0, fillSql.length() - COMMA.length());
                    String logicDeleteChoose = SqlScriptUtils
                            .convertChoose(entityCondition, fillSql, tableInfo.getLogicDeleteSql(false, false));
                    if (CollectionUtils.isNotEmpty(fieldInfos)) {
                        sqlSet = "SET " + SqlScriptUtils.convertIf(fillSetSql, entityCondition, true)
                                + logicDeleteChoose;
                    } else {
                        sqlSet = "SET " + logicDeleteChoose;
                    }
                } else {
                    sqlSet = "SET " + SqlScriptUtils.convertIf(fillSetSql, entityCondition, true)
                            + tableInfo.getLogicDeleteSql(false, false);
                }
                sql = SqlMethod.LOGIC_DELETE_BY_ID.format(
                        tableInfo.getTableName(), sqlSet, tableInfo.getKeyColumn(), tableInfo.getKeyProperty(),
                        tableInfo.getLogicDeleteSql(true, true));
            } else {
                sql = SqlMethod.LOGIC_DELETE_BY_ID.format(
                        tableInfo.getTableName(), sqlLogicSet(tableInfo), tableInfo.getKeyColumn(),
                        tableInfo.getKeyProperty(), tableInfo.getLogicDeleteSql(true, true));
            }
            SqlSource sqlSource = super.createSqlSource(configuration, sql, Object.class);
            return addUpdateMappedStatement(mapperClass, modelClass, methodName, sqlSource);
        } else {
            sql = SqlMethod.DELETE_BY_ID
                    .format(tableInfo.getTableName(), tableInfo.getKeyColumn(), tableInfo.getKeyProperty());
            return this.addDeleteMappedStatement(
                    mapperClass, methodName, super.createSqlSource(configuration, sql, Object.class));
        }
    }
}
