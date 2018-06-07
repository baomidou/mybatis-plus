package com.baomidou.mybatisplus.autoconfigure;

import org.apache.ibatis.session.Configuration;

/**
 * Callback interface that can be customized a {@link Configuration} object generated on auto-configuration.
 *
 * @author Kazuki Shimizu
 * @since 1.2.1
 */
@FunctionalInterface
public interface ConfigurationCustomizer {

    /**
     * Customize the given a {@link Configuration} object.
     * @param configuration the configuration object to customize
     */
    void customize(Configuration configuration);

}
