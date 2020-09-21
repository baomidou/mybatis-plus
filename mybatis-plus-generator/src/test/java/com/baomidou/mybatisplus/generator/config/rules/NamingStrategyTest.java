package com.baomidou.mybatisplus.generator.config.rules;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

public class NamingStrategyTest {

    @Test
    void removePrefixTest() {
        Assertions.assertEquals(NamingStrategy.removePrefix("test_ab", "t_", "test"), "_ab");
        Assertions.assertEquals(NamingStrategy.removePrefix("test_ab", new HashSet<>(Arrays.asList("t_", "test"))), "_ab");
    }

    @Test
    void underlineToCamelTest(){
        Assertions.assertEquals(NamingStrategy.underlineToCamel("Aid"), "aid");
        Assertions.assertEquals(NamingStrategy.underlineToCamel("test_id"), "testId");
        Assertions.assertEquals(NamingStrategy.underlineToCamel("TEST_ID"), "testId");
        Assertions.assertEquals(NamingStrategy.underlineToCamel("Test_id"), "testId");
        Assertions.assertEquals(NamingStrategy.underlineToCamel("Test_ID"), "testId");
        Assertions.assertEquals(NamingStrategy.underlineToCamel("TeSt_id"), "testId");
    }

}
