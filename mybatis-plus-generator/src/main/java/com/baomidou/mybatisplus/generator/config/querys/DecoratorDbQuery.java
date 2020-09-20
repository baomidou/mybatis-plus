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
package com.baomidou.mybatisplus.generator.config.querys;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.IDbQuery;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 装饰DbQuery
 *
 * @author nieqiurong 2020/9/17.
 */
public class DecoratorDbQuery extends AbstractDbQuery {

    private final IDbQuery dbQuery;
    private final DataSourceConfig dataSourceConfig;
    private final DbType dbType;

    public DecoratorDbQuery(IDbQuery dbQuery, DataSourceConfig dataSourceConfig) {
        this.dbQuery = dbQuery;
        this.dataSourceConfig = dataSourceConfig;
        this.dbType = dataSourceConfig.getDbType();
    }

    @Override
    public String tablesSql() {
        String tablesSql = dbQuery.tablesSql();
        String schema = dataSourceConfig.getSchemaName();
        if (DbType.POSTGRE_SQL == dbType) {
            if (schema == null) {
                //pg 默认 schema=public
                schema = "public";
                dataSourceConfig.setSchemaName(schema);
            }
            tablesSql = String.format(tablesSql, schema);
        } else if (DbType.KINGBASE_ES == dbType) {
            if (schema == null) {
                //kingbase 默认 schema=PUBLIC
                schema = "PUBLIC";
                dataSourceConfig.setSchemaName(schema);
            }
            tablesSql = String.format(tablesSql, schema);
        } else if (DbType.DB2 == dbType) {
            if (schema == null) {
                //db2 默认 schema=current schema
                schema = "current schema";
                dataSourceConfig.setSchemaName(schema);
            }
            tablesSql = String.format(tablesSql, schema);
        }
        //oracle数据库表太多，出现最大游标错误
        else if (DbType.ORACLE == dbType) {
            //oracle 默认 schema=username
            if (schema == null) {
                schema = dataSourceConfig.getUsername().toUpperCase();
                dataSourceConfig.setSchemaName(schema);
            }
            tablesSql = String.format(tablesSql, schema);
        }
        return tablesSql;
    }

    @Override
    public String tableFieldsSql() {
        return dbQuery.tableFieldsSql();
    }

    /**
     * 扩展{@link #tableFieldsSql()}方法
     *
     * @param tableName 表名
     * @return 查询表字段语句
     */
    public String tableFieldsSql(String tableName) {
        String tableFieldsSql = this.tableFieldsSql();
        if (DbType.POSTGRE_SQL == dbType) {
            tableFieldsSql = String.format(tableFieldsSql, dataSourceConfig.getSchemaName(), tableName);
        } else if (DbType.KINGBASE_ES == dbType) {
            tableFieldsSql = String.format(tableFieldsSql, dataSourceConfig.getSchemaName(), tableName);
        } else if (DbType.DB2 == dbType) {
            tableFieldsSql = String.format(tableFieldsSql, dataSourceConfig.getSchemaName(), tableName);
        } else if (DbType.ORACLE == dbType) {
            tableName = tableName.toUpperCase();
            tableFieldsSql = String.format(tableFieldsSql.replace("#schema", dataSourceConfig.getSchemaName()), tableName);
        } else if (DbType.DM == dbType) {
            tableName = tableName.toUpperCase();
            tableFieldsSql = String.format(tableFieldsSql, tableName);
        } else if (DbType.H2 == dbType) {
            tableFieldsSql = String.format(tableFieldsSql, tableName);
        } else {
            tableFieldsSql = String.format(tableFieldsSql, tableName);
        }
        return tableFieldsSql;
    }

    @Override
    public String tableName() {
        return dbQuery.tableName();
    }

    @Override
    public String tableComment() {
        return dbQuery.tableComment();
    }

    public String getTableComment(ResultSet resultSet) throws SQLException {
        return getResultStringValue(resultSet, this.tableComment());
    }

    @Override
    public String fieldName() {
        return dbQuery.fieldName();
    }

    @Override
    public String fieldType() {
        return dbQuery.fieldType();
    }

    @Override
    public String fieldComment() {
        return dbQuery.fieldComment();
    }

    public String getFiledComment(ResultSet resultSet) throws SQLException {
        return getResultStringValue(resultSet, this.fieldComment());
    }

    private String getResultStringValue(ResultSet resultSet, String columnLabel) throws SQLException {
        return StringUtils.isNotBlank(columnLabel) ? StringPool.EMPTY : formatComment(resultSet.getString(columnLabel));
    }

    public String formatComment(String comment) {
        return StringUtils.isBlank(comment) ? StringPool.EMPTY : comment.replaceAll("\r\n", "\t");
    }

    @Override
    public String fieldKey() {
        return dbQuery.fieldKey();
    }

    @Override
    public boolean isKeyIdentity(ResultSet results) throws SQLException {
        return dbQuery.isKeyIdentity(results);
    }

    @Override
    public String[] fieldCustom() {
        return dbQuery.fieldCustom();
    }
}
