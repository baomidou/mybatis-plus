package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Jdbc 工具类测试
 *
 * @author hubin
 * @since 2020-09-15
 */
public class JdbcUtilsTest {

    @Test
    public void testPattern(){
        String regex = ":dm\\d*:";
        Assertions.assertTrue(JdbcUtils.regexFind(regex, ":dm:"));
        Assertions.assertTrue(JdbcUtils.regexFind(regex, ":dm8:"));
        Assertions.assertTrue(JdbcUtils.regexFind(regex, "123:dm6:abc"));
        Assertions.assertTrue(JdbcUtils.regexFind(regex, ":dm7:abc"));
        Assertions.assertTrue(JdbcUtils.regexFind(regex, "a12ds:dm71:"));
        Assertions.assertFalse(JdbcUtils.regexFind(regex, "a12ds:dmc1:abc"));
    }

    @Test
    void testGetDbType(){
        Assertions.assertEquals(DbType.GAUSS_DB, JdbcUtils.getDbType("jdbc:gaussdb://127.0.0.1:8000/baomidou"));
        // zenith 为第三方驱动，非官方标准驱动
        Assertions.assertEquals(DbType.GAUSS, JdbcUtils.getDbType("jdbc:zenith://127.0.0.1:8000/baomidou"));
    }

}
