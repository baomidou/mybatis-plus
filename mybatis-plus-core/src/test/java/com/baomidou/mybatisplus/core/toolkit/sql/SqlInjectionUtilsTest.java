package com.baomidou.mybatisplus.core.toolkit.sql;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SQL 注入验证工具类测试
 *
 * @author hubin
 * @since 2021-08-15
 */
class SqlInjectionUtilsTest {

    @Test
    void sqlTest() {
        assertSql(false, "insert abc");
        assertSql(true, "insert into user (id,name) value (1, 'qm')");
        assertSql(true, "SELECT * FROM user");
        assertSql(true, "delete from user");
        assertSql(true, "drop TABLE user");
        assertSql(true, ";TRUNCATE from user");
        assertSql(false, "update");
        assertSql(false, "trigger");
        assertSql(false, "convert(name using GBK)");

        // 无空格
        assertSql(false, "insert_into");
        assertSql(true, "SELECT aa FROM user");
        // 无空格
        assertSql(true, "SELECT*FROM user");
        // 左空格
        assertSql(true, "SELECT *FROM user");
        // 右空格
        assertSql(true, "SELECT* FROM user");
        // 左tab
        assertSql(true, "SELECT                 *FROM user");
        // 右tab
        assertSql(true, "SELECT*        FROM user");
        assertSql(false, "SELECT*FROMuser");
        // 该字符串里包含 setT or
        assertSql(false, "databaseType desc,orderNum desc)");
        // 双引号情况
        assertSql(true, "\\\" or 1=1 and \\\"123\\\"=\\\"123\\\"");
        //Wrapper的apply情况
        assertSql(true, "1 = 1) OR 1 = 1 --");

        // https://github.com/baomidou/mybatis-plus/pull/5438/files
        assertSql(false, "insert");
        assertSql(false, "union");
        assertSql(false, "or");
        assertSql(false, "delete");
        assertSql(false, "drop");
        assertSql(true, "AND age not in (1,2,3)");
        assertSql(true, "and age <> 1");
        assertSql(false, "ORDER BY field(status,'SUCCESS','FAILED','CLOSED')");
        assertSql(true, "ORDER BY id,'SUCCESS',''-- FAILED','CLOSED'");
        assertSql(true, "or 1 = 1");
        assertSql(true, "and 1 = 1");
        assertSql(true, "hi = 1 or abc");
        assertSql(true, "(hi = 1) and abc");
        assertSql(false, "orAnd");
        assertSql(false, "andOr");
        assertSql(false, "andOr");

        // 函数验证
        assertSql(true, "if(2=2)");
        assertSql(false, "if");
        assertSql(true, "SUBSTR(name)");
        assertSql(true, "substr(name)");
        assertSql(true, "suBStr(name)");
        assertSql(false, "suBStr");
        assertSql(true, "SUBSTRING(name)");
        assertSql(true, "CHAR(name)");
        assertSql(true, "char(name)");
        assertSql(true, "concat(name, '0')");
        assertSql(false, "concat");
        assertSql(true, "select(table_name) from info");
        assertSql(true, ",sleep(0.01)");
        assertSql(false, ",sleep");
        assertSql(true, "DBMS_LOCK.sleep(0.01)");
        assertSql(true, "1=1&&(if(substr((select(table_name) from information_schema.TABLES WHERE table_schema=database() limit 0,1),1,1)!='a',sleep(0.01),2))");
    }

    private void assertSql(boolean injection, String sql) {
        Assertions.assertEquals(injection, SqlInjectionUtils.check(sql));
    }
}
