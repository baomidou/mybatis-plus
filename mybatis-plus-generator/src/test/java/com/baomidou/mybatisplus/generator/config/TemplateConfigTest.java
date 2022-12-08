package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author nieqiurong 2020/10/10.
 */
public class TemplateConfigTest {

    @Test
    void disableTest() {
        TemplateConfig templateConfig;
        templateConfig = GeneratorBuilder.templateConfig().disable();
        Assertions.assertNull(templateConfig.getController());
        Assertions.assertNull(templateConfig.getService());
        Assertions.assertNull(templateConfig.getServiceImpl());
        Assertions.assertNull(templateConfig.getMapper());
        Assertions.assertNull(templateConfig.getXml());
        Assertions.assertNull(templateConfig.getEntity(true));
        Assertions.assertNull(templateConfig.getEntity(false));


        templateConfig = GeneratorBuilder.templateConfig().disable(TemplateType.SERVICE);
        Assertions.assertNull(templateConfig.getService());
        Assertions.assertNotNull(templateConfig.getEntity(true));
        Assertions.assertNotNull(templateConfig.getEntity(false));

        templateConfig = GeneratorBuilder.templateConfig().disable(TemplateType.SERVICE_IMPL);
        Assertions.assertNull(templateConfig.getServiceImpl());
        Assertions.assertNotNull(templateConfig.getEntity(true));
        Assertions.assertNotNull(templateConfig.getEntity(false));

        templateConfig = GeneratorBuilder.templateConfig().disable(TemplateType.ENTITY);
        Assertions.assertNotNull(templateConfig.getServiceImpl());
        Assertions.assertNotNull(templateConfig.getService());
        Assertions.assertNull(templateConfig.getEntity(true));
        Assertions.assertNull(templateConfig.getEntity(false));

        templateConfig = GeneratorBuilder.templateConfig().disable(TemplateType.ENTITY);
        Assertions.assertNotNull(templateConfig.getServiceImpl());
        Assertions.assertNotNull(templateConfig.getService());
        Assertions.assertNull(templateConfig.getEntity(true));
        Assertions.assertNull(templateConfig.getEntity(false));
    }

    @Test
    void entityTest() {
        Assertions.assertEquals("/templates/entity.kt", GeneratorBuilder.templateConfig().getEntity(true));
        Assertions.assertEquals("/templates/entity.java", GeneratorBuilder.templateConfig().getEntity(false));
        Assertions.assertEquals("/tm/entity.kt", GeneratorBuilder.templateConfigBuilder().entityKt("/tm/entity.kt").build().getEntity(true));
        Assertions.assertEquals("/tm/entity.java", GeneratorBuilder.templateConfigBuilder().entity("/tm/entity.java").build().getEntity(false));
        Assertions.assertEquals("/tm/entity.kt", new TemplateConfig.Builder().entityKt("/tm/entity.kt").build().getEntity(true));
        Assertions.assertEquals("/tm/entity.java", new TemplateConfig.Builder().entity("/tm/entity.java").build().getEntity(false));
        Assertions.assertEquals("myEntity.java.vm", new TemplateConfig.Builder().entity("myEntity.java.vm").build().getEntity(false));
        Assertions.assertEquals("myEntity.kt.vm", new TemplateConfig.Builder().entityKt("myEntity.kt.vm").build().getEntity(true));
    }

    @Test
    void builderTest() {
        TemplateConfig templateConfig;
        templateConfig = GeneratorBuilder.templateConfig();
        Assertions.assertNotNull(templateConfig.getEntity(true));
        Assertions.assertNotNull(templateConfig.getEntity(false));
        Assertions.assertNotNull(templateConfig.getService());
        Assertions.assertNotNull(templateConfig.getServiceImpl());

        templateConfig = new TemplateConfig.Builder().entity("/tmp/entity.java").entityKt("/tmp/entity.kt").service("/tmp/service.java").serviceImpl("/tmp/serviceImpl.java").build();
        Assertions.assertNotNull(templateConfig.getEntity(true));
        Assertions.assertNotNull(templateConfig.getEntity(false));
        Assertions.assertNotNull(templateConfig.getService());
        Assertions.assertNotNull(templateConfig.getServiceImpl());
        Assertions.assertEquals("/tmp/entity.kt", templateConfig.getEntity(true));
        Assertions.assertEquals("/tmp/entity.java", templateConfig.getEntity(false));
        Assertions.assertEquals("/tmp/service.java", templateConfig.getService());
        Assertions.assertEquals("/tmp/serviceImpl.java", templateConfig.getServiceImpl());
    }
}
