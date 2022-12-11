package com.baomidou.mybatisplus.generator.samples;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import org.junit.jupiter.api.Test;

/**
 * 达梦 代码生成
 *
 * @author lanjerry
 * @since 3.5.3
 */
public class DMGeneratorTest extends BaseGeneratorTest {

    /**
     * 数据源配置
     */
    private static final DataSourceConfig DATA_SOURCE_CONFIG = new DataSourceConfig
        .Builder("jdbc:dm://xxxx:5236/DMSERVER?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf-8", "SYSDBA", "SYSDBA")
        .schema("SYSDBA")
        .build();

    @Test
    public void testSimple() {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.global(globalConfig().build());
        generator.execute();
    }
}
