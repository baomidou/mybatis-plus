package com.baomidou.mybatisplus.generator.samples;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
/**
 * 达梦 代码生成
 * <p>
 * 手工示例：需要连真实库执行 main()，不参与自动化测试，所以生成器对达梦的适配目前没有机器验证的覆盖。
 *
 * @author lanjerry
 * @since 3.5.3
 */
public class DMGeneratorSample extends BaseGeneratorTest {

    /**
     * 数据源配置
     */
    private static final DataSourceConfig DATA_SOURCE_CONFIG = new DataSourceConfig.Builder(
            "jdbc:dm://xxxx:5236/DMSERVER?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf-8",
            "SYSDBA", "SYSDBA").schema("SYSDBA").build();

    public static void main(String[] args) {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.global(globalConfig().build());
        generator.execute();
    }

}
