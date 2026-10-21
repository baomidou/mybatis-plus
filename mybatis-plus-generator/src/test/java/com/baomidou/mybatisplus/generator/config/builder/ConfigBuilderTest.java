package com.baomidou.mybatisplus.generator.config.builder;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2020/10/6.
 */
public class ConfigBuilderTest {

    private static final DataSourceConfig DATA_SOURCE_CONFIG = new DataSourceConfig.Builder(
            "jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "").build();

    @Test
    void matcherRegTableTest() {
        assertThat(ConfigBuilder.matcherRegTable("user")).isFalse();
        assertThat(ConfigBuilder.matcherRegTable("USER")).isFalse();
        assertThat(ConfigBuilder.matcherRegTable("t_user")).isFalse();
        assertThat(ConfigBuilder.matcherRegTable("T_USER")).isFalse();
        assertThat(ConfigBuilder.matcherRegTable("t_user_1")).isFalse();
        assertThat(ConfigBuilder.matcherRegTable("t_user_12")).isFalse();
        assertThat(ConfigBuilder.matcherRegTable("t-user-12")).isFalse();
        assertThat(ConfigBuilder.matcherRegTable("t_user_[0-9]")).isTrue();
        assertThat(ConfigBuilder.matcherRegTable("t_user_\\d")).isTrue();
        assertThat(ConfigBuilder.matcherRegTable("t_user_\\d{3,4}")).isTrue();
        assertThat(ConfigBuilder.matcherRegTable("^t_.*")).isTrue();
    }

    @Test
    void pathInfoTest() {
        ConfigBuilder configBuilder;
        Map<OutputFile, String> pathInfo;
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), DATA_SOURCE_CONFIG,
                GeneratorBuilder.strategyConfig(), null, null, null);
        pathInfo = configBuilder.getPathInfo();
        assertThat(pathInfo).isNotEmpty();
        assertThat(pathInfo).hasSize(7);
        assertThat(pathInfo).containsKey(OutputFile.entity);
        assertThat(pathInfo).containsKey(OutputFile.controller);
        assertThat(pathInfo).containsKey(OutputFile.service);
        assertThat(pathInfo).containsKey(OutputFile.serviceImpl);
        assertThat(pathInfo).containsKey(OutputFile.xml);
        assertThat(pathInfo).containsKey(OutputFile.mapper);
        assertThat(pathInfo).containsKey(OutputFile.parent);

        configBuilder = new ConfigBuilder(
                GeneratorBuilder.packageConfigBuilder()
                        .pathInfo(Collections.singletonMap(OutputFile.entity, "/tmp/code/entity")).build(),
                DATA_SOURCE_CONFIG, GeneratorBuilder.strategyConfig(), null, null, null);
        pathInfo = configBuilder.getPathInfo();
        assertThat(pathInfo).isNotEmpty();
        assertThat(pathInfo).hasSize(7);
        assertThat(pathInfo).containsKey(OutputFile.entity);
    }
}
