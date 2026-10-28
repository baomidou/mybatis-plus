package com.baomidou.mybatisplus.generator.engine;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import com.baomidou.mybatisplus.generator.config.po.LikeTable;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.po.TableInfoTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2020/10/11.
 */
@Tag("unit")
public class TemplateEngineTest {

    private void compatibleAssert(ConfigBuilder configBuilder) {
        VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();
        velocityTemplateEngine.setConfigBuilder(configBuilder);
        TableInfo tableInfo = new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(),
                TableInfoTest.dataSourceConfig, GeneratorBuilder.strategyConfig(), null, null, null), "user");
        tableInfo.processTable();
        Map<String, Object> objectMap = velocityTemplateEngine.getObjectMap(configBuilder, tableInfo);
        assertThat(objectMap.get("enableCache")).isEqualTo(Boolean.FALSE);
        assertThat(objectMap.get("baseResultMap")).isEqualTo(Boolean.TRUE);
        assertThat(objectMap.get("baseColumnList")).isEqualTo(Boolean.TRUE);
        assertThat(objectMap.get("activeRecord")).isEqualTo(Boolean.TRUE);
        assertThat(objectMap.get("idType")).isEqualTo(IdType.INPUT.toString());
    }

    @Test
    void compatibleTest() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(
                "jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "").build();
        compatibleAssert(
                new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig,
                        new StrategyConfig.Builder().likeTable(new LikeTable("USER")).entityBuilder()
                                .enableActiveRecord().idType(IdType.INPUT).mapperBuilder().enableBaseResultMap()
                                .enableBaseColumnList().build(),
                        null, GeneratorBuilder.globalConfig(), null));
    }
}
