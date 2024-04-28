/*
 * Copyright (c) 2011-2023, baomidou (jobob@qq.com).
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
        return
                "SELECT T2.COLUMN_NAME,T1.COMMENTS," +
                        "CASE WHEN T2.DATA_TYPE='NUMBER' THEN (CASE WHEN T2.DATA_PRECISION IS NULL THEN T2.DATA_TYPE WHEN NVL(T2.DATA_SCALE, 0) > 0 THEN T2.DATA_TYPE||'('||T2.DATA_PRECISION||','||T2.DATA_SCALE||')' ELSE T2.DATA_TYPE||'('||T2.DATA_PRECISION||')' END) ELSE T2.DATA_TYPE END DATA_TYPE ," +
                        "CASE WHEN CONSTRAINT_TYPE='P' THEN 'PRI' END AS KEY " +
                        "FROM USER_COL_COMMENTS T1, USER_TAB_COLUMNS T2, " +
                        "(SELECT T4.TABLE_NAME, T4.COLUMN_NAME ,T5.CONSTRAINT_TYPE " +
                        "FROM USER_CONS_COLUMNS T4, USER_CONSTRAINTS T5 " +
                        "WHERE T4.CONSTRAINT_NAME = T5.CONSTRAINT_NAME " +
                        "AND T5.CONSTRAINT_TYPE = 'P')T3 " +
                        "WHERE T1.TABLE_NAME = T2.TABLE_NAME AND " +
                        "T1.COLUMN_NAME=T2.COLUMN_NAME AND " +
                        "T1.TABLE_NAME = T3.TABLE_NAME(+) AND " +
                        "T1.COLUMN_NAME=T3.COLUMN_NAME(+)   AND " +
                        "T1.TABLE_NAME = '%s' and owner='%s' " +
                        "ORDER BY T2.TABLE_NAME,T2.COLUMN_ID";
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
        return "COMMENTS";
    }

    @Override
    public String fieldKey() {
        return "KEY";
    }
}
