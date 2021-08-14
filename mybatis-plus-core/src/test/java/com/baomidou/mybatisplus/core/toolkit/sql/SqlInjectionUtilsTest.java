package com.baomidou.mybatisplus.core.toolkit.sql;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SQL 注入验证工具类测试
 *
 * @author hubin
 * @since 2021-08-15
 */
public class SqlInjectionUtilsTest {

    @Test
    public void sqlTest() {
        assertSql(false, "insert abc");
        assertSql(true, "insert user (id,name) value (1, 'qm')");
        assertSql(true, "select * from user");
        assertSql(true, "delete from user");
        assertSql(true, "drop table user");
        assertSql(true, ";truncate from user");
        assertSql(false, "update");
        assertSql(false, "trigger");
        assertSql(true, "and name like '%s123%s'");
        assertSql(false, "convert(name using GBK)");
    }

    private void assertSql(boolean injection, String sql) {
        Assertions.assertEquals(injection, SqlInjectionUtils.check(sql));
    }
}
