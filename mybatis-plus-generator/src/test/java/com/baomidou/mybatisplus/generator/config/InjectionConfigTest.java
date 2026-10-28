package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author lanjerry
 * @since 2021-09-06
 */
public class InjectionConfigTest {

    @Test
    void builderTest() {
        Map<String, Object> customMap = new HashMap<>();
        customMap.put("test", "baomidou");
        Map<String, String> customFile = new HashMap<>();
        customFile.put("test.txt", "/templates/test.vm");
        InjectionConfig injectionConfig = GeneratorBuilder.injectionConfigBuilder().customMap(customMap)
                .customFile(customFile).build();
        assertThat(injectionConfig.getCustomMap()).hasSize(1);
        assertThat(injectionConfig.getCustomMap().get("test")).isEqualTo("baomidou");
        assertThat(injectionConfig.getCustomFiles()).hasSize(1);
        assertThat(injectionConfig.getCustomFiles().get(0).getTemplatePath()).isEqualTo("/templates/test.vm");
    }
}
