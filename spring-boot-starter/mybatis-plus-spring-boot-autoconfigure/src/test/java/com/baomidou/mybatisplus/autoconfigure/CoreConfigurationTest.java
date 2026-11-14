package com.baomidou.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@code MybatisPlusProperties.CoreConfiguration} 往 {@code MybatisConfiguration} 上落值的两条线：设了就覆盖， 没设就保持原样。test-shared
 * 里另有一份三份 starter 共用的 {@code MybatisPlusPropertiesTest}， 测的是 {@code resolveMapperLocations}，对象不同，所以本类改名让开同名。
 *
 * @author nieqiurong
 */
@Tag("unit")
public class CoreConfigurationTest {

    @Test
    void shouldApplyCoreConfigurationValues() {
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
    void shouldKeepExistingValuesWhenCoreConfigIsNull() {
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
