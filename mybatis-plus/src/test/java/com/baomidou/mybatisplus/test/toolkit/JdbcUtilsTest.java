package com.baomidou.mybatisplus.test.toolkit;

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
}
