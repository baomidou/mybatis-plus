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
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;

/**
 * <p>
 * 抽象的注入方法类
 * </p>
 *
 * @author hubin
 * @since 2018-04-06
 */
public abstract class AbstractLogicMethod extends AbstractMethod {

    public AbstractLogicMethod() {
        // TO DO NOTHING
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
        return "SET " + table.getLogicDeleteSql(false, true);
    }

    // ------------ 处理逻辑删除条件过滤 ------------

    @Override
    protected String sqlWhereEntityWrapper(TableInfo table) {
        if (table.isLogicDelete()) {
            String sqlScript = table.getAllSqlWhere(true, true, Constants.WRAPPER_ENTITY_SPOT);
            sqlScript = SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", Constants.WRAPPER_ENTITY),
                true);
            sqlScript += (StringPool.NEWLINE + table.getLogicDeleteSql(true, false) +
                StringPool.NEWLINE);
            sqlScript += SqlScriptUtils.convertIf(String.format(" ${%s}", Constants.WRAPPER_SQLSEGMENT),
                String.format("%s != null and %s != '' and ew.notEmptyOfWhere", Constants.WRAPPER_SQLSEGMENT,
                    Constants.WRAPPER_SQLSEGMENT), true);
            sqlScript += StringPool.NEWLINE;
            sqlScript += SqlScriptUtils.convertIf(String.format(" ${%s}", Constants.WRAPPER_SQLSEGMENT),
                String.format("%s != null and %s != '' and ew.emptyOfWhere", Constants.WRAPPER_SQLSEGMENT,
                    Constants.WRAPPER_SQLSEGMENT), true);
            sqlScript = SqlScriptUtils.convertChoose("ew!=null", sqlScript,
                table.getLogicDeleteSql(true, false));
            sqlScript = SqlScriptUtils.convertTrim(sqlScript, "WHERE", null, "AND|OR",
                null);
            return sqlScript;
        }
        // 正常逻辑
        return super.sqlWhereEntityWrapper(table);
    }

    @Override
    protected String sqlWhereByMap(TableInfo table) {
        if (table.isLogicDelete()) {
            // 逻辑删除
            String sqlScript = SqlScriptUtils.convertChoose("v == null", " ${k} IS NULL ",
                " ${k} = #{v} ");
            sqlScript = SqlScriptUtils.convertForeach(sqlScript, "cm", "k", "v", "AND");
            sqlScript = SqlScriptUtils.convertIf(sqlScript, "cm != null and !cm.isEmpty", true);
            sqlScript += (StringPool.NEWLINE + table.getLogicDeleteSql(true, false));
            sqlScript = SqlScriptUtils.convertTrim(sqlScript, "WHERE", null, "AND", null);
            return sqlScript;
        }
        return super.sqlWhereByMap(table);
    }
}
