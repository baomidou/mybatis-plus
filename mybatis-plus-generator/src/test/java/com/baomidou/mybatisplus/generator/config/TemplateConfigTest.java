package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2020/10/10.
 */
@Tag("unit")
public class TemplateConfigTest {

    @Test
    void disableTest() {
        TemplateConfig templateConfig;
        templateConfig = GeneratorBuilder.templateConfig().disable();
        assertThat(templateConfig.getController()).isNull();
        assertThat(templateConfig.getService()).isNull();
        assertThat(templateConfig.getServiceImpl()).isNull();
        assertThat(templateConfig.getMapper()).isNull();
        assertThat(templateConfig.getXml()).isNull();
        assertThat(templateConfig.getEntity(true)).isNull();
        assertThat(templateConfig.getEntity(false)).isNull();

        templateConfig = GeneratorBuilder.templateConfig().disable(TemplateType.SERVICE);
        assertThat(templateConfig.getService()).isNull();
        assertThat(templateConfig.getEntity(true)).isNotNull();
        assertThat(templateConfig.getEntity(false)).isNotNull();

        templateConfig = GeneratorBuilder.templateConfig().disable(TemplateType.SERVICE_IMPL);
        assertThat(templateConfig.getServiceImpl()).isNull();
        assertThat(templateConfig.getEntity(true)).isNotNull();
        assertThat(templateConfig.getEntity(false)).isNotNull();

        templateConfig = GeneratorBuilder.templateConfig().disable(TemplateType.ENTITY);
        assertThat(templateConfig.getServiceImpl()).isNotNull();
        assertThat(templateConfig.getService()).isNotNull();
        assertThat(templateConfig.getEntity(true)).isNull();
        assertThat(templateConfig.getEntity(false)).isNull();

        templateConfig = GeneratorBuilder.templateConfig().disable(TemplateType.ENTITY);
        assertThat(templateConfig.getServiceImpl()).isNotNull();
        assertThat(templateConfig.getService()).isNotNull();
        assertThat(templateConfig.getEntity(true)).isNull();
        assertThat(templateConfig.getEntity(false)).isNull();
    }

    @Test
    void entityTest() {
        assertThat(GeneratorBuilder.templateConfig().getEntity(true)).isEqualTo("/templates/entity.kt");
        assertThat(GeneratorBuilder.templateConfig().getEntity(false)).isEqualTo("/templates/entity.java");
        assertThat(GeneratorBuilder.templateConfigBuilder().entityKt("/tm/entity.kt").build().getEntity(true))
                .isEqualTo("/tm/entity.kt");
        assertThat(GeneratorBuilder.templateConfigBuilder().entity("/tm/entity.java").build().getEntity(false))
                .isEqualTo("/tm/entity.java");
        assertThat(new TemplateConfig.Builder().entityKt("/tm/entity.kt").build().getEntity(true))
                .isEqualTo("/tm/entity.kt");
        assertThat(new TemplateConfig.Builder().entity("/tm/entity.java").build().getEntity(false))
                .isEqualTo("/tm/entity.java");
        assertThat(new TemplateConfig.Builder().entity("myEntity.java.vm").build().getEntity(false))
                .isEqualTo("myEntity.java.vm");
        assertThat(new TemplateConfig.Builder().entityKt("myEntity.kt.vm").build().getEntity(true))
                .isEqualTo("myEntity.kt.vm");
    }

    @Test
    void builderTest() {
        TemplateConfig templateConfig;
        templateConfig = GeneratorBuilder.templateConfig();
        assertThat(templateConfig.getEntity(true)).isNotNull();
        assertThat(templateConfig.getEntity(false)).isNotNull();
        assertThat(templateConfig.getService()).isNotNull();
        assertThat(templateConfig.getServiceImpl()).isNotNull();

        templateConfig = new TemplateConfig.Builder().entity("/tmp/entity.java").entityKt("/tmp/entity.kt")
                .service("/tmp/service.java").serviceImpl("/tmp/serviceImpl.java").build();
        assertThat(templateConfig.getEntity(true)).isNotNull();
        assertThat(templateConfig.getEntity(false)).isNotNull();
        assertThat(templateConfig.getService()).isNotNull();
        assertThat(templateConfig.getServiceImpl()).isNotNull();
        assertThat(templateConfig.getEntity(true)).isEqualTo("/tmp/entity.kt");
        assertThat(templateConfig.getEntity(false)).isEqualTo("/tmp/entity.java");
        assertThat(templateConfig.getService()).isEqualTo("/tmp/service.java");
        assertThat(templateConfig.getServiceImpl()).isEqualTo("/tmp/serviceImpl.java");
    }
}
