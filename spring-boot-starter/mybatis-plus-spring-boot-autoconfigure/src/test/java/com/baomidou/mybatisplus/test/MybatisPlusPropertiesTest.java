package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertTrue(configuration.isCacheEnabled());
        Assertions.assertEquals("test", configuration.getDatabaseId());
        Assertions.assertEquals(10, configuration.getDefaultFetchSize());
    }

    @Test
    void testNull() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setCacheEnabled(true);
        configuration.setDatabaseId("test");
        configuration.setDefaultFetchSize(10);
        MybatisPlusProperties.CoreConfiguration coreConfiguration = new MybatisPlusProperties.CoreConfiguration();
        coreConfiguration.applyTo(configuration);
        Assertions.assertTrue(configuration.isCacheEnabled());
        Assertions.assertEquals("test", configuration.getDatabaseId());
        Assertions.assertEquals(10, configuration.getDefaultFetchSize());
    }

}
