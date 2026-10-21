package com.baomidou.mybatisplus.generator.samples;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
/**
 * Oracle 代码生成
 * <p>
 * 手工示例：需要连真实库执行 main()，不参与自动化测试，所以生成器对 Oracle 的适配目前没有机器验证的覆盖。
 *
 * @author lanjerry
 * @since 3.5.3
 */
public class OracleGeneratorSample extends BaseGeneratorSample {

    /**
     * 数据源配置
     */
    private static final DataSourceConfig DATA_SOURCE_CONFIG = new DataSourceConfig.Builder(
            "jdbc:oracle:thin:@xxxx:1521:helowin", "system", "system").schema("ANONYMOUS").build();

    public static void main(String[] args) {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.global(globalConfig().build());
        generator.execute();
    }

}
