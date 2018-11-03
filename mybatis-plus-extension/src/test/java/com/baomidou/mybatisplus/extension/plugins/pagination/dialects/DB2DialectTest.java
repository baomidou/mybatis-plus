package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
        Page page = new Page(2, 10);
        System.out.println(page.offset() + 1);
        System.out.println(page.getSize() + page.offset());
//        System.out.println(db2Dialect.buildPaginationSql(sql, page.offset(), page.getSize()).getDialectSql());
    }
}
