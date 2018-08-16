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
package com.baomidou.mybatisplus.extension.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;

import java.text.MessageFormat;
import java.util.List;

/**
 * <p>
 * 抽象的注入方法类
 * </p>
 *
 * @author hubin
 * @since 2018-04-06
 */
public abstract class LogicAbstractMethod extends AbstractMethod {

    public LogicAbstractMethod() {
        // TO DO NOTHING
    }

    /**
     * <p>
     * SQL 更新 set 语句
     * </p>
     *
     * @param table 表信息
     * @return sql and 片段
     */
    public String getLogicDeleteSql(boolean startWithAnd, TableInfo table) {
        StringBuilder sql = new StringBuilder();
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            if (fieldInfo.isLogicDelete()) {
                if (startWithAnd) {
                    sql.append(" AND ");
                }
                sql.append(fieldInfo.getColumn());
                if (StringUtils.isCharSequence(fieldInfo.getPropertyType())) {
                    sql.append("='").append(fieldInfo.getLogicNotDeleteValue()).append(StringPool.SINGLE_QUOTE);
                } else {
                    sql.append(StringPool.EQUALS).append(fieldInfo.getLogicNotDeleteValue());
                }
            }
        }
        return sql.toString();
    }

    /**
     * <p>
     * SQL 更新 set 语句
     * </p>
     *
     * @param table 表信息
     * @return sql set 片段
     */
    protected String sqlLogicSet(TableInfo table) {
        List<TableFieldInfo> fieldList = table.getFieldList();
        StringBuilder set = new StringBuilder("SET ");
        int i = 0;
        for (TableFieldInfo fieldInfo : fieldList) {
            if (fieldInfo.isLogicDelete()) {
                if (++i > 1) {
                    set.append(StringPool.COMMA);
                }
                set.append(fieldInfo.getColumn()).append(StringPool.EQUALS);
                if (StringUtils.isCharSequence(fieldInfo.getPropertyType())) {
                    set.append(StringPool.SINGLE_QUOTE).append(fieldInfo.getLogicDeleteValue()).append(StringPool.SINGLE_QUOTE);
                } else {
                    set.append(fieldInfo.getLogicDeleteValue());
                }
            }
        }
        return set.toString();
    }

    // ------------ 处理逻辑删除条件过滤 ------------

    @Override
    protected String sqlWhereEntityWrapper(TableInfo table) {
        if (table.isLogicDelete()) {
            String sqlScript = table.getAllSqlWhere(true, true, Constants.WRAPPER_ENTITY_SPOT);
            sqlScript = StringPool.NEWLINE + sqlScript + StringPool.NEWLINE;
            sqlScript = SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", Constants.WRAPPER_ENTITY));
            sqlScript += (StringPool.NEWLINE + table.getLogicDeleteSql(true, true));
            sqlScript += (StringPool.NEWLINE + SqlScriptUtils.convertIf(String.format(" AND ${%s}",
                Constants.WRAPPER_SQLSEGMENT),
                MessageFormat.format("{0} != null and {0} != ''", Constants.WRAPPER_SQLSEGMENT)));
            sqlScript = SqlScriptUtils.convertTrim(sqlScript, "WHERE", null, "AND|OR", null);
            sqlScript = SqlScriptUtils.convertChoose("ew!=null and !ew.emptyOfWhere", sqlScript,
                "WHERE " + table.getLogicDeleteSql(false, false));
            return sqlScript;
        }
        // 正常逻辑
        return super.sqlWhereEntityWrapper(table);
    }

    @Override
    protected String sqlWhereByMap(TableInfo table) {
        String sqlScript = super.sqlWhereByMap(table);
        if (table.isLogicDelete()) {
            // 逻辑删除
            sqlScript += (StringPool.NEWLINE + table.getLogicDeleteSql(true, false));
            sqlScript = SqlScriptUtils.convertTrim(sqlScript, "WHERE", null, "AND", null);
        }
        return sqlScript;
    }
}
