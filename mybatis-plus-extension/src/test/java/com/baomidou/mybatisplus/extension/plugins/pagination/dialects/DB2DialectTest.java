package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import org.junit.Test;

/**
 * @author miemie
 * @since 2018-11-01
 */
public class DB2DialectTest {

    @Test
    public void m1() {
        String sql = "select * from xxx where id = ? order by id";
        DB2Dialect db2Dialect = new DB2Dialect();
        System.out.println(db2Dialect.buildPaginationSql(sql, 1, 10).getDialectSql());
    }
}
