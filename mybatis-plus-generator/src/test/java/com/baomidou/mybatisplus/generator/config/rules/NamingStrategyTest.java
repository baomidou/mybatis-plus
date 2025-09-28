package com.baomidou.mybatisplus.generator.config.rules;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

public class NamingStrategyTest {

    @Test
    void removePrefixTest() {
        Assertions.assertEquals("_ab", NamingStrategy.removePrefix("test_ab", new HashSet<>() {{
            add("t_");
            add("test");
        }}));
    }

    @Test
    void underlineToCamelTest() {
        Assertions.assertEquals("aid", NamingStrategy.underlineToCamel("Aid"));
        Assertions.assertEquals("aId", NamingStrategy.underlineToCamel("AId"));
        Assertions.assertEquals("testId", NamingStrategy.underlineToCamel("test_id"));
        Assertions.assertEquals("testId", NamingStrategy.underlineToCamel("TEST_ID"));
        Assertions.assertEquals("testId", NamingStrategy.underlineToCamel("Test_id"));
        Assertions.assertEquals("testId", NamingStrategy.underlineToCamel("Test_ID"));
        Assertions.assertEquals("testId", NamingStrategy.underlineToCamel("TeSt_id"));
        Assertions.assertEquals("createTime", NamingStrategy.underlineToCamel("createTime"));
        Assertions.assertEquals("createTime", NamingStrategy.underlineToCamel("create_time"));
        Assertions.assertEquals("createTime", NamingStrategy.underlineToCamel("create_Time"));
        Assertions.assertEquals("createTime", NamingStrategy.underlineToCamel("Create_Time"));
        Assertions.assertEquals("createtime", NamingStrategy.underlineToCamel("CREATETIME"));
    }

}
