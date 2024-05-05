package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.annotation.DbType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * @author nieqiuqiu
 */
class DbTypeTest {

    @Test
    void testGetDbType() {
        Assertions.assertEquals(DbType.MYSQL, DbType.getDbType("mysql"));
        Assertions.assertEquals(DbType.MYSQL, DbType.getDbType("Mysql"));
        Assertions.assertEquals(DbType.OTHER, DbType.getDbType("other"));
        Assertions.assertEquals(DbType.OTHER, DbType.getDbType("unknown"));
    }
}
