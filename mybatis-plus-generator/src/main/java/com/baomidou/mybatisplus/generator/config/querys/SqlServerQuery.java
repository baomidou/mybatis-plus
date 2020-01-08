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

import java.sql.ResultSet;
import java.sql.SQLException;

import com.baomidou.mybatisplus.annotation.DbType;

/**
 * SqlServer 表数据查询
 *
 * @author hubin
 * @since 2018-01-16
 */
public class SqlServerQuery extends AbstractDbQuery {

    @Override
    public String tablesSql() {
        return "select cast(so.name as varchar(500)) as TABLE_NAME, " +
            "cast(sep.value as varchar(500)) as COMMENTS from sysobjects so " +
            "left JOIN sys.extended_properties sep on sep.major_id=so.id and sep.minor_id=0 " +
            "where (xtype='U' or xtype='v')";
    }


    @Override
    public String tableFieldsSql() {
        return "SELECT  cast(a.name AS VARCHAR(500)) AS TABLE_NAME,cast(b.name AS VARCHAR(500)) AS COLUMN_NAME, "
            + "cast(c.VALUE AS NVARCHAR(500)) AS COMMENTS,cast(sys.types.name AS VARCHAR (500)) AS DATA_TYPE,"
            + "(" + " SELECT CASE count(1) WHEN 1 then 'PRI' ELSE '' END"
            + " FROM syscolumns,sysobjects,sysindexes,sysindexkeys,systypes "
            + " WHERE syscolumns.xusertype = systypes.xusertype AND syscolumns.id = object_id (a.name) AND sysobjects.xtype = 'PK'"
            + " AND sysobjects.parent_obj = syscolumns.id " + " AND sysindexes.id = syscolumns.id "
            + " AND sysobjects.name = sysindexes.name AND sysindexkeys.id = syscolumns.id "
            + " AND sysindexkeys.indid = sysindexes.indid "
            + " AND syscolumns.colid = sysindexkeys.colid AND syscolumns.name = b.name) as 'KEY',"
            + "  b.is_identity isIdentity "
            + " FROM ( select name,object_id from sys.tables UNION all select name,object_id from sys.views ) a "
            + " INNER JOIN sys.columns b ON b.object_id = a.object_id "
            + " LEFT JOIN sys.types ON b.user_type_id = sys.types.user_type_id   "
            + " LEFT JOIN sys.extended_properties c ON c.major_id = b.object_id AND c.minor_id = b.column_id "
            + " WHERE a.name = '%s' and sys.types.name !='sysname' ";
    }

    @Override
    public String tableName() {
        return "TABLE_NAME";
    }


    @Override
    public String tableComment() {
        return "COMMENTS";
    }


    @Override
    public String fieldName() {
        return "COLUMN_NAME";
    }


    @Override
    public String fieldType() {
        return "DATA_TYPE";
    }


    @Override
    public String fieldComment() {
        return "COMMENTS";
    }


    @Override
    public String fieldKey() {
        return "KEY";
    }


    @Override
    public boolean isKeyIdentity(ResultSet results) throws SQLException {
        return 1 == results.getInt("isIdentity");
    }
}
