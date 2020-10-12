package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.generator.config.rules.DateType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author nieqiurong 2020/10/12.
 */
public class GlobalConfigTest {

    private void buildAssert(GlobalConfig globalConfig) {
        Assertions.assertTrue(globalConfig.isKotlin());
        Assertions.assertTrue(globalConfig.isSwagger2());
        Assertions.assertTrue(globalConfig.isOpen());
        Assertions.assertEquals(globalConfig.getAuthor(), "mp");
        Assertions.assertEquals(globalConfig.getOutputDir(), "/temp/code");
        Assertions.assertEquals(globalConfig.getDateType(), DateType.SQL_PACK);
    }

    @Test
    void builderTest() {
        GlobalConfig globalConfig;
        globalConfig = new GlobalConfig().setAuthor("mp")
            .setDateType(DateType.SQL_PACK).setOpen(true).setOutputDir("/temp/code")
            .setActiveRecord(true).setBaseColumnList(true).setBaseResultMap(true).setKotlin(true).setSwagger2(true);
        buildAssert(globalConfig);
        globalConfig = new GlobalConfig.Builder().author("mp")
            .dateType(DateType.SQL_PACK).openDir(true).outputDir("/temp/code").activeRecord(true).baseColumnList(true)
            .baseColumnList(true).kotlin(true).swagger2(true).build();
        buildAssert(globalConfig);
    }
}
