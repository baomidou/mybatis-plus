/*
 * Copyright (c) 2011-2022, baomidou (jobob@qq.com).
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
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 根据 ID 集合删除
 *
 * @author hubin
 * @since 2018-04-06
 */
public class DeleteBatchByIds extends AbstractMethod {

    public DeleteBatchByIds() {
        this(SqlMethod.DELETE_BATCH_BY_IDS.getMethod());
    }

    /**
     * @param name 方法名
     * @since 3.5.0
     */
    public DeleteBatchByIds(String name) {
        super(name);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql;
        SqlMethod sqlMethod = SqlMethod.LOGIC_DELETE_BATCH_BY_IDS;
        if (tableInfo.isWithLogicDelete()) {
            sql = logicDeleteScript(tableInfo, sqlMethod);
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Object.class);
            return addUpdateMappedStatement(mapperClass, modelClass, methodName, sqlSource);
        } else {
            sqlMethod = SqlMethod.DELETE_BATCH_BY_IDS;
            sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), tableInfo.getKeyColumn(),
                SqlScriptUtils.convertForeach(
                    SqlScriptUtils.convertChoose("@org.apache.ibatis.type.SimpleTypeRegistry@isSimpleType(item.getClass())",
                        "#{item}", "#{item." + tableInfo.getKeyProperty() + "}"),
                    COLL, null, "item", COMMA));
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Object.class);
            return this.addDeleteMappedStatement(mapperClass, methodName, sqlSource);
        }
    }

    /**
     * @param tableInfo 表信息
     * @return 逻辑删除脚本
     * @since 3.5.0
     */
    public String logicDeleteScript(TableInfo tableInfo, SqlMethod sqlMethod) {
        return String.format(sqlMethod.getSql(), tableInfo.getTableName(),
            sqlLogicSet(tableInfo), tableInfo.getKeyColumn(), SqlScriptUtils.convertForeach(
                SqlScriptUtils.convertChoose("@org.apache.ibatis.type.SimpleTypeRegistry@isSimpleType(item.getClass())",
                    "#{item}", "#{item." + tableInfo.getKeyProperty() + "}"),
                COLL, null, "item", COMMA),
            tableInfo.getLogicDeleteSql(true, true));
    }
}
