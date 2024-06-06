package com.baomidou.mybatisplus.generator.config.builder;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

/**
 * @author nieqiurong 2020/10/6.
 */
public class ConfigBuilderTest {

    private static final DataSourceConfig DATA_SOURCE_CONFIG = new DataSourceConfig.Builder("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "sa", "").build();

    @Test
    void matcherRegTableTest() {
        Assertions.assertFalse(ConfigBuilder.matcherRegTable("user"));
        Assertions.assertFalse(ConfigBuilder.matcherRegTable("USER"));
        Assertions.assertFalse(ConfigBuilder.matcherRegTable("t_user"));
        Assertions.assertFalse(ConfigBuilder.matcherRegTable("T_USER"));
        Assertions.assertFalse(ConfigBuilder.matcherRegTable("t_user_1"));
        Assertions.assertFalse(ConfigBuilder.matcherRegTable("t_user_12"));
        Assertions.assertFalse(ConfigBuilder.matcherRegTable("t-user-12"));
        Assertions.assertTrue(ConfigBuilder.matcherRegTable("t_user_[0-9]"));
        Assertions.assertTrue(ConfigBuilder.matcherRegTable("t_user_\\d"));
        Assertions.assertTrue(ConfigBuilder.matcherRegTable("t_user_\\d{3,4}"));
        Assertions.assertTrue(ConfigBuilder.matcherRegTable("^t_.*"));
    }

    @Test
    void pathInfoTest() {
        ConfigBuilder configBuilder;
        Map<OutputFile, String> pathInfo;
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), DATA_SOURCE_CONFIG, GeneratorBuilder.strategyConfig(),
            null, null, null);
        pathInfo = configBuilder.getPathInfo();
        Assertions.assertFalse(pathInfo.isEmpty());
        Assertions.assertEquals(7, pathInfo.size());
        Assertions.assertTrue(pathInfo.containsKey(OutputFile.entity));
        Assertions.assertTrue(pathInfo.containsKey(OutputFile.controller));
        Assertions.assertTrue(pathInfo.containsKey(OutputFile.service));
        Assertions.assertTrue(pathInfo.containsKey(OutputFile.serviceImpl));
        Assertions.assertTrue(pathInfo.containsKey(OutputFile.xml));
        Assertions.assertTrue(pathInfo.containsKey(OutputFile.mapper));
        Assertions.assertTrue(pathInfo.containsKey(OutputFile.parent));

        configBuilder = new ConfigBuilder(
            GeneratorBuilder.packageConfigBuilder().pathInfo(Collections.singletonMap(OutputFile.entity,
                "/tmp/code/entity")).build(), DATA_SOURCE_CONFIG, GeneratorBuilder.strategyConfig(),
            null, null, null);
        pathInfo = configBuilder.getPathInfo();
        Assertions.assertFalse(pathInfo.isEmpty());
        Assertions.assertEquals(7, pathInfo.size());
        Assertions.assertTrue(pathInfo.containsKey(OutputFile.entity));
    }
}
