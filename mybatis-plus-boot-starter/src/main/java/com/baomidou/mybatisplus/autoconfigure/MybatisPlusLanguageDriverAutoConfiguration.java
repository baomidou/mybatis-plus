/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.scripting.MybatisFreeMarkerLanguageDriver;
import com.baomidou.mybatisplus.extension.scripting.MybatisThymeleafLanguageDriver;
import com.baomidou.mybatisplus.extension.scripting.MybatisVelocityLanguageDriver;
import org.apache.ibatis.scripting.LanguageDriver;
import org.mybatis.scripting.freemarker.FreeMarkerLanguageDriver;
import org.mybatis.scripting.freemarker.FreeMarkerLanguageDriverConfig;
import org.mybatis.scripting.thymeleaf.ThymeleafLanguageDriver;
import org.mybatis.scripting.thymeleaf.ThymeleafLanguageDriverConfig;
import org.mybatis.scripting.velocity.VelocityLanguageDriver;
import org.mybatis.scripting.velocity.VelocityLanguageDriverConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-Configuration} for MyBatis's scripting language drivers.
 * <p> copy from {@link org.mybatis.spring.boot.autoconfigure.MybatisLanguageDriverAutoConfiguration}</p>
 *
 * @author miemie
 * @since 2019-10-22
 */
@Configuration
@ConditionalOnClass(LanguageDriver.class)
public class MybatisPlusLanguageDriverAutoConfiguration {

    private static final String CONFIGURATION_PROPERTY_PREFIX = Constants.MYBATIS_PLUS + ".scripting-language-driver";

    /**
     * Configuration class for mybatis-freemarker 1.1.x or under.
     */
    @Configuration
    @ConditionalOnClass(FreeMarkerLanguageDriver.class)
    @ConditionalOnMissingClass("org.mybatis.scripting.freemarker.FreeMarkerLanguageDriverConfig")
    public static class LegacyFreeMarkerConfiguration {
        @Bean
        @ConditionalOnMissingBean
        FreeMarkerLanguageDriver freeMarkerLanguageDriver() {
            return new MybatisFreeMarkerLanguageDriver();
        }
    }

    /**
     * Configuration class for mybatis-freemarker 1.2.x or above.
     */
    @Configuration
    @ConditionalOnClass({FreeMarkerLanguageDriver.class, FreeMarkerLanguageDriverConfig.class})
    public static class FreeMarkerConfiguration {
        @Bean
        @ConditionalOnMissingBean
        FreeMarkerLanguageDriver freeMarkerLanguageDriver(FreeMarkerLanguageDriverConfig config) {
            return new MybatisFreeMarkerLanguageDriver(config);
        }

        @Bean
        @ConditionalOnMissingBean
        @ConfigurationProperties(CONFIGURATION_PROPERTY_PREFIX + ".freemarker")
        public FreeMarkerLanguageDriverConfig freeMarkerLanguageDriverConfig() {
            return FreeMarkerLanguageDriverConfig.newInstance();
        }
    }

    /**
     * Configuration class for mybatis-velocity 2.1.x or above.
     */
    @Configuration
    @ConditionalOnClass({VelocityLanguageDriver.class, VelocityLanguageDriverConfig.class})
    public static class VelocityConfiguration {
        @Bean
        @ConditionalOnMissingBean
        VelocityLanguageDriver velocityLanguageDriver(VelocityLanguageDriverConfig config) {
            return new MybatisVelocityLanguageDriver(config);
        }

        @Bean
        @ConditionalOnMissingBean
        @ConfigurationProperties(CONFIGURATION_PROPERTY_PREFIX + ".velocity")
        public VelocityLanguageDriverConfig velocityLanguageDriverConfig() {
            return VelocityLanguageDriverConfig.newInstance();
        }
    }

    @Configuration
    @ConditionalOnClass(ThymeleafLanguageDriver.class)
    public static class ThymeleafConfiguration {
        @Bean
        @ConditionalOnMissingBean
        ThymeleafLanguageDriver thymeleafLanguageDriver(ThymeleafLanguageDriverConfig config) {
            return new MybatisThymeleafLanguageDriver(config);
        }

        @Bean
        @ConditionalOnMissingBean
        @ConfigurationProperties(CONFIGURATION_PROPERTY_PREFIX + ".thymeleaf")
        public ThymeleafLanguageDriverConfig thymeleafLanguageDriverConfig() {
            return ThymeleafLanguageDriverConfig.newInstance();
        }

        // This class provides to avoid the https://github.com/spring-projects/spring-boot/issues/21626 as workaround.
        @SuppressWarnings("unused")
        private static class MetadataThymeleafLanguageDriverConfig extends ThymeleafLanguageDriverConfig {

            @ConfigurationProperties(CONFIGURATION_PROPERTY_PREFIX + ".thymeleaf.dialect")
            @Override
            public DialectConfig getDialect() {
                return super.getDialect();
            }

            @ConfigurationProperties(CONFIGURATION_PROPERTY_PREFIX + ".thymeleaf.template-file")
            @Override
            public TemplateFileConfig getTemplateFile() {
                return super.getTemplateFile();
            }
        }
    }
}
