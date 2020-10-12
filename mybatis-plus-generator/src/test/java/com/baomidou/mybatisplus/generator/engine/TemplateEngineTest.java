package com.baomidou.mybatisplus.generator.engine;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.LikeTable;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author nieqiurong 2020/10/11.
 */
public class TemplateEngineTest {


    private void compatibleAssert(ConfigBuilder configBuilder){
        VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();
        velocityTemplateEngine.setConfigBuilder(configBuilder);
        Map<String, Object> objectMap = velocityTemplateEngine.getObjectMap(new TableInfo());
        Assertions.assertEquals(Boolean.TRUE, objectMap.get("enableCache"));
        Assertions.assertEquals(Boolean.TRUE, objectMap.get("baseResultMap"));
        Assertions.assertEquals(Boolean.TRUE, objectMap.get("baseColumnList"));
        Assertions.assertEquals(Boolean.TRUE, objectMap.get("activeRecord"));
        Assertions.assertEquals(IdType.INPUT.toString(), objectMap.get("idType"));
    }

    @Test
    void compatibleTest(){
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "").build();
        compatibleAssert(new ConfigBuilder(new PackageConfig(), dataSourceConfig,
            new StrategyConfig.Builder().likeTable(new LikeTable("USER")).build(),
            new TemplateConfig.Builder().build(),
            new GlobalConfig().setActiveRecord(true).setBaseColumnList(true).setEnableCache(true).setBaseResultMap(true).setIdType(IdType.INPUT)));

        compatibleAssert(new ConfigBuilder(new PackageConfig(), dataSourceConfig,
            new StrategyConfig.Builder().likeTable(new LikeTable("USER")).build(),
            new TemplateConfig.Builder().build(),
            new GlobalConfig.Builder().activeRecord(true).baseColumnList(true).enableCache(true).baseResultMap(true).idType(IdType.INPUT).build()));

        compatibleAssert(new ConfigBuilder(new PackageConfig(), dataSourceConfig,
            new StrategyConfig.Builder().likeTable(new LikeTable("USER"))
                .entityBuilder().activeRecord(true).idType(IdType.INPUT)
                .mapperBuilder().baseResultMap(true).baseColumnList(true).enableXmlCache(true)
                .build(),
            new TemplateConfig.Builder().build(),
            new GlobalConfig.Builder().build()));

        compatibleAssert(new ConfigBuilder(new PackageConfig(), dataSourceConfig,
            new StrategyConfig.Builder().likeTable(new LikeTable("USER"))
                .entityBuilder().activeRecord(true).idType(IdType.INPUT)
                .mapperBuilder().baseResultMap(true).baseColumnList(true).enableXmlCache(true)
                .build(),
            new TemplateConfig.Builder().build(),
            new GlobalConfig.Builder().activeRecord(false).baseColumnList(false).enableCache(false).baseResultMap(false).idType(IdType.ASSIGN_ID).build()));


    }

}
