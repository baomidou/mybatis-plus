package com.baomidou.mybatisplus.generator.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author nieqiurong 2020/10/10.
 */
public class TemplateConfigTest {

    @Test
    void disableTest() {
        TemplateConfig templateConfig;
        templateConfig = new TemplateConfig().disable();
        Assertions.assertNull(templateConfig.getController());
        Assertions.assertNull(templateConfig.getService());
        Assertions.assertNull(templateConfig.getServiceImpl());
        Assertions.assertNull(templateConfig.getMapper());
        Assertions.assertNull(templateConfig.getXml());
        Assertions.assertNull(templateConfig.getEntity(true));
        Assertions.assertNull(templateConfig.getEntity(false));

        templateConfig = new TemplateConfig.Builder().build();
        Assertions.assertNull(templateConfig.getController());
        Assertions.assertNull(templateConfig.getService());
        Assertions.assertNull(templateConfig.getServiceImpl());
        Assertions.assertNull(templateConfig.getMapper());
        Assertions.assertNull(templateConfig.getXml());
        Assertions.assertNull(templateConfig.getEntity(true));
        Assertions.assertNull(templateConfig.getEntity(false));


        templateConfig = new TemplateConfig().disable(TemplateType.SERVICE);
        Assertions.assertNull(templateConfig.getServiceImpl());
        Assertions.assertNull(templateConfig.getService());
        Assertions.assertNotNull(templateConfig.getEntity(true));
        Assertions.assertNotNull(templateConfig.getEntity(false));

        templateConfig = new TemplateConfig.Builder().all().build().disable(TemplateType.SERVICE);
        Assertions.assertNull(templateConfig.getServiceImpl());
        Assertions.assertNull(templateConfig.getService());
        Assertions.assertNotNull(templateConfig.getEntity(true));
        Assertions.assertNotNull(templateConfig.getEntity(false));

        templateConfig = new TemplateConfig().disable(TemplateType.ENTITY);
        Assertions.assertNotNull(templateConfig.getServiceImpl());
        Assertions.assertNotNull(templateConfig.getService());
        Assertions.assertNull(templateConfig.getEntity(true));
        Assertions.assertNull(templateConfig.getEntity(false));

        templateConfig = new TemplateConfig.Builder().all().build().disable(TemplateType.ENTITY);
        Assertions.assertNotNull(templateConfig.getServiceImpl());
        Assertions.assertNotNull(templateConfig.getService());
        Assertions.assertNull(templateConfig.getEntity(true));
        Assertions.assertNull(templateConfig.getEntity(false));

    }

    @Test
    void entityTest() {
        Assertions.assertEquals("/templates/entity.kt", new TemplateConfig().getEntity(true));
        Assertions.assertEquals("/templates/entity.kt", new TemplateConfig.Builder().all().build().getEntity(true));
        Assertions.assertEquals("/templates/entity.kt", new TemplateConfig.Builder().entity().build().getEntity(true));
        Assertions.assertEquals("/templates/entity.java", new TemplateConfig().getEntity(false));
        Assertions.assertEquals("/templates/entity.java", new TemplateConfig.Builder().all().build().getEntity(false));
        Assertions.assertEquals("/templates/entity.java", new TemplateConfig.Builder().entity().build().getEntity(false));
        Assertions.assertEquals("/tm/entity.kt", new TemplateConfig().setEntity("/tm/entity.java").getEntity(true));
        Assertions.assertEquals("/tm/entity.java", new TemplateConfig().setEntity("/tm/entity.java").getEntity(false));
        Assertions.assertEquals("/tm/entity.kt", new TemplateConfig.Builder().entity("/tm/entity.java").build().getEntity(true));
        Assertions.assertEquals("/tm/entity.java", new TemplateConfig.Builder().entity("/tm/entity.java").build().getEntity(false));
        Assertions.assertEquals("/tm/entity.kt", new TemplateConfig().setEntity("/tm/entity").getEntity(true));
        Assertions.assertEquals("/tm/entity.kt", new TemplateConfig.Builder().entity("/tm/entity").build().getEntity(true));
        Assertions.assertEquals("/tm/entity.java", new TemplateConfig().setEntity("/tm/entity").getEntity(false));
        Assertions.assertEquals("/tm/entity.java", new TemplateConfig.Builder().entity("/tm/entity").build().getEntity(false));
        Assertions.assertEquals("/tm/entity.kt", new TemplateConfig().setEntity("/tm/entity%s").getEntity(true));
        Assertions.assertEquals("/tm/entity.kt", new TemplateConfig.Builder().entity("/tm/entity%s").build().getEntity(true));
        Assertions.assertEquals("/tm/entity.java", new TemplateConfig().setEntity("/tm/entity%s").getEntity(false));
        Assertions.assertEquals("/tm/entity.java", new TemplateConfig.Builder().entity("/tm/entity%s").build().getEntity(false));
    }

    @Test
    void builderTest() {
        TemplateConfig templateConfig;
        templateConfig = new TemplateConfig.Builder().entity().service().build();
        Assertions.assertNotNull(templateConfig.getEntity(true));
        Assertions.assertNotNull(templateConfig.getEntity(false));
        Assertions.assertNotNull(templateConfig.getService());
        Assertions.assertNotNull(templateConfig.getServiceImpl());
        Assertions.assertNull(templateConfig.getController());
        Assertions.assertNull(templateConfig.getMapper());
        Assertions.assertNull(templateConfig.getXml());

        templateConfig = new TemplateConfig.Builder().entity("/tmp/entity").service("/tmp/service.java", "/tmp/serviceImpl.java").build();
        Assertions.assertNotNull(templateConfig.getEntity(true));
        Assertions.assertNotNull(templateConfig.getEntity(false));
        Assertions.assertNotNull(templateConfig.getService());
        Assertions.assertNotNull(templateConfig.getServiceImpl());
        Assertions.assertNull(templateConfig.getController());
        Assertions.assertNull(templateConfig.getMapper());
        Assertions.assertNull(templateConfig.getXml());
        Assertions.assertEquals("/tmp/entity.kt", templateConfig.getEntity(true));
        Assertions.assertEquals("/tmp/entity.java", templateConfig.getEntity(false));
        Assertions.assertEquals("/tmp/service.java", templateConfig.getService());
        Assertions.assertEquals("/tmp/serviceImpl.java", templateConfig.getServiceImpl());
    }
}
