package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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
        InjectionConfig injectionConfig = GeneratorBuilder.injectionConfigBuilder().customMap(customMap).customFile(customFile).build();
        Assertions.assertEquals(1, injectionConfig.getCustomMap().size());
        Assertions.assertEquals("baomidou",injectionConfig.getCustomMap().get("test"));
        Assertions.assertEquals(1, injectionConfig.getCustomFiles().size());
        Assertions.assertEquals("/templates/test.vm",injectionConfig.getCustomFiles().get(0).getTemplatePath());
    }
}
