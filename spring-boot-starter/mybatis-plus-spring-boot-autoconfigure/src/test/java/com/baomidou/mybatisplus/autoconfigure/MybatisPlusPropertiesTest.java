package com.baomidou.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong
 */
public class MybatisPlusPropertiesTest {

    @Test
    void testValue() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        MybatisPlusProperties.CoreConfiguration coreConfiguration = new MybatisPlusProperties.CoreConfiguration();
        coreConfiguration.setCacheEnabled(true);
        coreConfiguration.setDatabaseId("test");
        coreConfiguration.setDefaultFetchSize(10);
        coreConfiguration.applyTo(configuration);
        assertThat(configuration.isCacheEnabled()).isTrue();
        assertThat(configuration.getDatabaseId()).isEqualTo("test");
        assertThat(configuration.getDefaultFetchSize()).isEqualTo(10);
    }

    @Test
    void testNull() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setCacheEnabled(true);
        configuration.setDatabaseId("test");
        configuration.setDefaultFetchSize(10);
        MybatisPlusProperties.CoreConfiguration coreConfiguration = new MybatisPlusProperties.CoreConfiguration();
        coreConfiguration.applyTo(configuration);
        assertThat(configuration.isCacheEnabled()).isTrue();
        assertThat(configuration.getDatabaseId()).isEqualTo("test");
        assertThat(configuration.getDefaultFetchSize()).isEqualTo(10);
    }

}
