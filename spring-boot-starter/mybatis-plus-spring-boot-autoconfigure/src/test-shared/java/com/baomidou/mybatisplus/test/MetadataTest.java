package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 检查自动配置元数据生成
 *
 * @author nieqiurong 2019/2/9.
 */
@Tag("integration")
class MetadataTest {

    @Test
    void checkSpringAutoconfigureMetadataProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileSystemResource(
                "build/classes/java/main/META-INF/spring-autoconfigure-metadata.properties").getInputStream()) {
            properties.load(inputStream);
        }

        Class<MybatisPlusAutoConfiguration> autoConfigurationClass = MybatisPlusAutoConfiguration.class;
        AutoConfigureAfter autoConfigureAfter = autoConfigurationClass.getAnnotation(AutoConfigureAfter.class);
        assertThat(properties.getProperty(autoConfigurationClass.getName() + ".AutoConfigureAfter"))
                .isEqualTo(classNames(autoConfigureAfter.value()));

        ConditionalOnSingleCandidate conditionalOnSingleCandidate = autoConfigurationClass
                .getAnnotation(ConditionalOnSingleCandidate.class);
        assertThat(properties.getProperty(autoConfigurationClass.getName() + ".ConditionalOnSingleCandidate"))
                .isEqualTo(conditionalOnSingleCandidate.value().getName());

        ConditionalOnClass conditionalOnClass = autoConfigurationClass.getAnnotation(ConditionalOnClass.class);
        assertThat(properties.getProperty(autoConfigurationClass.getName() + ".ConditionalOnClass"))
                .isEqualTo(classNames(conditionalOnClass.value()));
    }

    private static String classNames(Class<?>[] classes) {
        return Arrays.stream(classes).map(Class::getName).collect(Collectors.joining(","));
    }
}
