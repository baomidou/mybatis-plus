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
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;


/**
 * 根据 ID 集合删除
 *
 * @author nieqiurong
 * @since 3.5.7
 */
public class DeleteByIds extends AbstractMethod {

    public DeleteByIds() {
        this(SqlMethod.DELETE_BY_IDS.getMethod());
    }

    /**
     * @param name 方法名
     * @since 3.5.0
     */
    public DeleteByIds(String name) {
        super(name);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql;
        SqlMethod sqlMethod = SqlMethod.LOGIC_DELETE_BY_IDS;
        if (tableInfo.isWithLogicDelete()) {
            sql = logicDeleteScript(tableInfo, sqlMethod);
            SqlSource sqlSource = super.createSqlSource(configuration, sql, Object.class);
            return addUpdateMappedStatement(mapperClass, modelClass, methodName, sqlSource);
        } else {
            sqlMethod = SqlMethod.DELETE_BY_IDS;
            sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), tableInfo.getKeyColumn(),
                SqlScriptUtils.convertForeach(
                    SqlScriptUtils.convertChoose("@org.apache.ibatis.type.SimpleTypeRegistry@isSimpleType(item.getClass())",
                        "#{item}", "#{item." + tableInfo.getKeyProperty() + "}"),
                    COLL, null, "item", COMMA));
            SqlSource sqlSource = super.createSqlSource(configuration, sql, Object.class);
            return this.addDeleteMappedStatement(mapperClass, methodName, sqlSource);
        }
    }

    /**
     * @param tableInfo 表信息
     * @return 逻辑删除脚本
     * @since 3.5.0
     */
    public String logicDeleteScript(TableInfo tableInfo, SqlMethod sqlMethod) {
        List<TableFieldInfo> fieldInfos = tableInfo.getFieldList().stream()
            .filter(TableFieldInfo::isWithUpdateFill)
            .filter(f -> !f.isLogicDelete())
            .collect(toList());
        String sqlSet = "SET ";
        if (CollectionUtils.isNotEmpty(fieldInfos)) {
            sqlSet += SqlScriptUtils.convertIf(fieldInfos.stream()
                .map(i -> i.getSqlSet(Constants.MP_FILL_ET + StringPool.DOT)).collect(joining(EMPTY)), String.format("%s != null", Constants.MP_FILL_ET), true);
        }
        sqlSet += StringPool.EMPTY + tableInfo.getLogicDeleteSql(false, false);
        return String.format(sqlMethod.getSql(), tableInfo.getTableName(),
            sqlSet, tableInfo.getKeyColumn(), SqlScriptUtils.convertForeach(
                SqlScriptUtils.convertChoose("@org.apache.ibatis.type.SimpleTypeRegistry@isSimpleType(item.getClass())",
                    "#{item}", "#{item." + tableInfo.getKeyProperty() + "}"),
                COLL, null, "item", COMMA),
            tableInfo.getLogicDeleteSql(true, true));
    }

}
