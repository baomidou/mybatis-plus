package com.baomidou.mybatisplus.core.toolkit.sql;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertSql(true, "case when(hex(DATABASE()) like 'ffff%' then (id) ELSE(dict_type) end");
        assertSql(true, "CASE WHEN (hex(database ( )) like 'ffff%') THEN id ELSE dict_type END");
        assertSql(true, "database()");
        assertSql(false, "mydatabase()");
        assertSql(false, "database_name");

        // 高风险 SQL 片段
        assertSql(true, "id; select 1");
        assertSql(true, "id -- comment");
        assertSql(true, "id # comment");
        assertSql(true, "id/**/desc");
        assertSql(true, "id /*!50000 UNION SELECT 1 */");
        assertSql(true, "id OR(1=1)");
        assertSql(true, "id AND(1=1)");
        assertSql(true, "pg_sleep(5)");
        assertSql(true, "extractvalue(1, 1)");
        assertSql(true, "updatexml(1, 1, 1)");
        assertSql(true, "load_file('/etc/passwd')");
        assertSql(true, "current_setting('server_version')");

        assertSql(true, "field(status,'a;b','c')");
        assertSql(true, "field(status,'a/*b','c')");
        assertSql(false, "`order#` DESC");
        assertSql(false, "my_extractvalue(5)");
        assertSql(false, "order_id");
        assertSql(false, "");
        assertSql(false, "user_name_123");
        assertSql(false, "中文字段");
        assertSql(true, "user_name;select 1");
        assertSql(true, "user_name OR(1=1)");
    }
    private void assertSql(boolean injection, String sql) {
        assertThat(SqlInjectionUtils.check(sql)).isEqualTo(injection);
    }
}
