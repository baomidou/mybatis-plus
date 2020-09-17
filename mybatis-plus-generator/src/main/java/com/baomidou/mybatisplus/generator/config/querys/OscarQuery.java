package com.baomidou.mybatisplus.generator.config.querys;

/**
 * <p></p>
 *
 * @author whli
 * @version 1.0.0
 * @since 2020/7/28 18:54
 */
public class OscarQuery extends AbstractDbQuery {

    @Override
    public String tablesSql() {
        return "SELECT " +
            "a.TABLE_NAME, " +
            "b.COMMENTS " +
            "FROM USER_TABLES a " +
            "INNER JOIN USER_TAB_COMMENTS b ON (b.TABLE_TYPE = 'TABLE' AND a.TABLE_NAME = b.TABLE_NAME) ";
    }

    @Override
    public String tableFieldsSql() {
        return "SELECT " +
            "T1.COLUMN_NAME, " +
            "T1.DATA_TYPE, " +
            "T2.COMMENTS, " +
            "CASE WHEN T3.CONSTRAINT_TYPE = 'P' THEN 'PRI' " +
            "ELSE '' END KEY " +
            "FROM USER_TAB_COLUMNS T1 " +
            "INNER JOIN USER_COL_COMMENTS T2 ON (T1.COLUMN_NAME = T2.COLUMN_NAME) " +
            "LEFT JOIN(SELECT a.TABLE_NAME,b.COLUMN_NAME,a.CONSTRAINT_TYPE FROM USER_CONSTRAINTS a, USER_IND_COLUMNS b " +
            "WHERE a.CONSTRAINT_TYPE = 'P' AND a.INDEX_NAME = b.INDEX_NAME) T3 ON (T1.TABLE_NAME = T3.TABLE_NAME AND T1.COLUMN_NAME = T3.COLUMN_NAME) " +
            "WHERE T1.TABLE_NAME = '%s' " +
            "GROUP BY T1.COLUMN_NAME,T1.DATA_TYPE,T2.COMMENTS,T3.CONSTRAINT_TYPE ";
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
}
