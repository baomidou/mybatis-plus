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
package com.baomidou.mybatisplus.generator.config.querys;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;

/**
 * DM 表数据查询
 *
 * @author halower,daiby
 * @since 2019-06-27
 */
public class DMQuery extends AbstractDbQuery {

    @Override
    public String tablesSql() {
        return "SELECT * FROM ( SELECT /*+ MAX_OPT_N_TABLES(5) */ NULL AS TABLE_CAT, SCHEMAS.NAME AS TABLE_SCHEM, \n" +
            "TABS.NAME AS TABLE_NAME, CASE TABS.SUBTYPE$ WHEN 'UTAB' THEN 'TABLE' WHEN 'VIEW' THEN 'VIEW' WHEN 'STAB' THEN 'SYSTEM TABLE' WHEN 'SYNOM'\n" +
            " THEN 'SYNONYM' END AS TABLE_TYPE, (SELECT COMMENT$ FROM SYSTABLECOMMENTS WHERE SCHNAME = SCHEMAS.NAME AND TVNAME = TABS.NAME) AS REMARKS, \n" +
            " NULL AS TYPE_CAT, NULL AS TYPE_SCHEM, NULL AS TYPE_NAME, NULL AS SELF_REFERENCING_COL_NAME, NULL AS REF_GENERATION \n" +
            "  FROM (SELECT ID, NAME, PID FROM SYS.SYSOBJECTS WHERE TYPE$ = 'SCH' AND NAME LIKE '%s'  ESCAPE '!'  )  SCHEMAS, \n" +
            "  (SELECT * FROM SYS.SYSOBJECTS  WHERE ((SUBTYPE$='UTAB' AND CAST((INFO3 & 0x00FF & 0x003F) AS INT) != 9 AND CAST((INFO3 & 0x00FF & 0x003F) AS INT) \n" +
            "  != 27 AND CAST((INFO3 & 0x00FF & 0x003F) AS INT) != 29 AND CAST((INFO3 & 0x00FF & 0x003F) AS INT) != 25 AND CAST((INFO3 & 0x00FF & 0x003F) AS INT)\n" +
            "   != 12 AND CAST((INFO3 & 0x00FF & 0x003F) AS INT) != 7 AND CAST((INFO3 & 0x00FF & 0x003F) AS INT) != 21 AND CAST((INFO3 & 0x00FF & 0x003F) AS INT)\n" +
            "    != 23 AND CAST((INFO3 & 0x00FF & 0x003F) AS INT) != 18 AND CAST((INFO3 & 0x00FF & 0x003F) AS INT) != 5)OR SUBTYPE$='VIEW' )) \n" +
            "TABS WHERE TABS.SCHID = SCHEMAS.ID ) TEMP WHERE 1=1";
    }

    @Override
    public String tableFieldsSql() {
        return "SELECT /*+ MAX_OPT_N_TABLES(5) */ DISTINCT NULL AS TABLE_CAT, SCHS.NAME AS TABLE_SCHEM, TABS.NAME AS TABLE_NAME, COLS.NAME AS COLUMN_NAME, \n" +
            "CASE COLS.TYPE$ WHEN 'NUMBER' THEN 2 WHEN 'NUMERIC' THEN 2 WHEN 'TIMESTAMP' THEN 93 WHEN 'CHARACTER' THEN 1 WHEN 'VARCHAR' THEN 12 WHEN 'VARCHAR2' THEN 12 WHEN 'DEC' THEN 3 WHEN 'DECIMAL' THEN 3 WHEN 'BIT' THEN -7 \n" +
            "WHEN 'INT' THEN 4 WHEN 'INTEGER' THEN 4 WHEN 'BIGINT' THEN -5 WHEN 'BYTE' THEN -6 WHEN 'TINYINT' THEN -6 WHEN 'SMALLINT' THEN 5 WHEN 'BINARY' THEN -2 WHEN 'VARBINARY' THEN -3 WHEN 'FLOAT' THEN 6 WHEN 'DOUBLE' THEN 8\n" +
            " WHEN 'REAL' THEN 7 WHEN 'DOUBLE PRECISION' THEN 8 WHEN 'DATE' THEN 91 WHEN 'TIME' THEN 92 WHEN 'DATETIME' THEN 93 WHEN 'TEXT' THEN -1 WHEN 'LONGVARCHAR' THEN -1 WHEN 'IMAGE' THEN -4 WHEN 'LONGVARBINARY' THEN -4 \n" +
            " WHEN 'BLOB' THEN 2004 WHEN 'CLOB' THEN 2005 WHEN 'CURSOR' THEN -10 WHEN 'BOOL' THEN 16 WHEN 'BOOLEAN' THEN 16 ELSE SF_GET_DATA_TYPE(COLS.TYPE$, CAST(COLS.SCALE AS INT), 3) END AS DATA_TYPE,CASE INSTR(COLS.TYPE$,'CLASS',1,1)\n" +
            "  WHEN 0 THEN COLS.TYPE$ ELSE SF_GET_CLASS_NAME(COLS.TYPE$) END AS TYPE_NAME,CASE SF_GET_COLUMN_SIZE(COLS.TYPE$, CAST (COLS.LENGTH$ AS INT), CAST (COLS.SCALE AS INT)) \n" +
            "  WHEN -2 THEN NULL ELSE SF_GET_COLUMN_SIZE(COLS.TYPE$, CAST (COLS.LENGTH$ AS INT), CAST (COLS.SCALE AS INT)) END AS COLUMN_SIZE,CASE SF_GET_BUFFER_LEN(COLS.TYPE$, CAST (COLS.LENGTH$ AS INT), CAST (COLS.SCALE AS INT)) \n" +
            "  WHEN -2 THEN NULL ELSE SF_GET_BUFFER_LEN(COLS.TYPE$, CAST (COLS.LENGTH$ AS INT), CAST (COLS.SCALE AS INT)) END AS BUFFER_LENGTH,CASE SF_GET_DECIMAL_DIGITS(COLS.TYPE$, CAST (COLS.SCALE AS INT)) \n" +
            "  WHEN -2 THEN NULL ELSE SF_GET_DECIMAL_DIGITS(COLS.TYPE$, CAST (COLS.SCALE AS INT)) END AS DECIMAL_DIGITS,10 AS NUM_PREC_RADIX,CASE COLS.NULLABLE$ WHEN 'Y' THEN 1 ELSE 0 END AS NULLABLE,\n" +
            "  (SELECT COMMENT$ FROM SYSCOLUMNCOMMENTS WHERE SCHNAME=SCHS.NAME AND TVNAME=TABS.NAME AND COLNAME=COLS.NAME) AS REMARKS,COLS.DEFVAL AS COLUMN_DEF,0 AS SQL_DATA_TYPE,0 AS SQL_DATETIME_SUB,\n" +
            "  CASE SF_GET_OCT_LENGTH(COLS.TYPE$, CAST (COLS.LENGTH$ AS INT)) WHEN -2 THEN NULL ELSE SF_GET_OCT_LENGTH(COLS.TYPE$, CAST (COLS.LENGTH$ AS INT)) END AS CHAR_OCTET_LENGTH,COLS.COLID + 1 AS ORDINAL_POSITION,CASE COLS.NULLABLE$ \n" +
            "  WHEN 'Y' THEN 'YES' ELSE 'NO' END AS IS_NULLABLE,NULL AS SCOPE_CATLOG,NULL AS SCOPE_SCHEMA,NULL AS SCOPE_TABLE,0 AS SOURCE_DATA_TYPE FROM\n" +
            "   (\n" +
            "   SELECT ID, NAME, PID FROM SYS.SYSOBJECTS WHERE TYPE$ = 'SCH' \n" +
            "   AND NAME = '%s' \n" +
            "   )  AS SCHS, (SELECT ID, SCHID, NAME FROM SYS.SYSOBJECTS \n" +
            "   WHERE TYPE$ = 'SCHOBJ' AND SUBTYPE$ IN('UTAB', 'STAB', 'VIEW')\n" +
            "     AND NAME LIKE '%s' ESCAPE '!' \n" +
            "     )  AS TABS, \n" +
            "SYS.SYSCOLUMNS AS COLS WHERE TABS.ID = COLS.ID AND SCHS.ID = TABS.SCHID  ORDER BY TABLE_SCHEM ASC,TABLE_NAME ASC,ORDINAL_POSITION ASC";
    }

    @Override
    public String tableName() {
        return "TABLE_NAME";
    }
    @Override
    public String tableComment() {
        return "REMARKS";
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
        return "REMARKS";
    }

    @Override
    public String fieldKey() {
        return "KEY";
    }

    @Override
    public String primaryKeySql(DataSourceConfig dataSourceConfig, String tableName) {
        return "  SELECT /*+ MAX_OPT_N_TABLES(5) */ NULL AS TABLE_CAT, 'SYSDBA' AS TABLE_SCHEM,'" + tableName + "' AS TABLE_NAME,'true' AS key,\n" +
            "  COLS.NAME AS COLUMN_NAME,SF_GET_INDEX_KEY_SEQ(INDS.KEYNUM, INDS.KEYINFO, COLS.COLID) AS KEY_SEQ,CONS.NAME AS PK_NAME FROM \n" +
            "  SYS.SYSINDEXES INDS, (SELECT OBJ.NAME, CON.ID, CON.TYPE$, CON.TABLEID, CON.COLID, CON.INDEXID FROM SYS.SYSCONS AS CON, \n" +
            "  SYS.SYSOBJECTS AS OBJ WHERE OBJ.SUBTYPE$='CONS' AND OBJ.ID=CON.ID) CONS, SYS.SYSCOLUMNS COLS, (SELECT NAME ,ID FROM SYS.SYSOBJECTS \n" +
            "  WHERE SUBTYPE$='UTAB' \n" +
            "  AND NAME = '" + tableName + "' \n" +
            "  AND SCHID=(SELECT ID FROM SYS.SYSOBJECTS WHERE NAME = '" + dataSourceConfig.getSchemaName() + "' AND TYPE$='SCH')) TAB, (SELECT ID, NAME FROM SYS.SYSOBJECTS WHERE SUBTYPE$='INDEX')OBJ_INDS \n" +
            "  WHERE CONS.TYPE$='P' AND CONS.INDEXID=INDS.ID AND INDS.ID=OBJ_INDS.ID AND TAB.ID=COLS.ID AND CONS.TABLEID=TAB.ID AND \n" +
            "  SF_COL_IS_IDX_KEY(INDS.KEYNUM, INDS.KEYINFO,COLS.COLID)=1 ORDER BY COLUMN_NAME ASC";
    }

}
