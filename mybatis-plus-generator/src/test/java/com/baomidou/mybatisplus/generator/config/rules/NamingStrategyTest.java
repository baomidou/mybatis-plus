package com.baomidou.mybatisplus.generator.config.rules;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class NamingStrategyTest {

    @Test
    void removePrefixTest() {
        assertThat(NamingStrategy.removePrefix("test_ab", new HashSet<>() {

            {
                add("t_");
                add("test");
            }
        })).isEqualTo("_ab");
    }

    @Test
    void underlineToCamelTest() {
        assertThat(NamingStrategy.underlineToCamel("Aid")).isEqualTo("aid");
        assertThat(NamingStrategy.underlineToCamel("AId")).isEqualTo("aId");
        assertThat(NamingStrategy.underlineToCamel("test_id")).isEqualTo("testId");
        assertThat(NamingStrategy.underlineToCamel("TEST_ID")).isEqualTo("testId");
        assertThat(NamingStrategy.underlineToCamel("Test_id")).isEqualTo("testId");
        assertThat(NamingStrategy.underlineToCamel("Test_ID")).isEqualTo("testId");
        assertThat(NamingStrategy.underlineToCamel("TeSt_id")).isEqualTo("testId");
        assertThat(NamingStrategy.underlineToCamel("createTime")).isEqualTo("createTime");
        assertThat(NamingStrategy.underlineToCamel("create_time")).isEqualTo("createTime");
        assertThat(NamingStrategy.underlineToCamel("create_Time")).isEqualTo("createTime");
        assertThat(NamingStrategy.underlineToCamel("Create_Time")).isEqualTo("createTime");
        assertThat(NamingStrategy.underlineToCamel("CREATETIME")).isEqualTo("createtime");
    }

}
