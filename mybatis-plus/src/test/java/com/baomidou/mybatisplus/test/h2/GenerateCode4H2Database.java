package com.baomidou.mybatisplus.test.h2;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.querys.H2Query;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * <p>
 * </p>
 *
 * @author yuxiaobin
 * @date 2019/1/8
 */
@FixMethodOrder(MethodSorters.JVM)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
public class GenerateCode4H2Database extends BaseTest{

    @Test
    public void test() {
        String dbUrl = "jdbc:h2:mem:test;MODE=mysql";
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.H2)
            .setDbQuery(new H2Query())
            .setUrl(dbUrl)
            .setUsername("sa")
            .setPassword("")
            .setDriverName("org.h2.Driver");
        generate("com.baomidou.mp.h2", dataSourceConfig, "h2user");
    }

    public void generate(String packageName, DataSourceConfig dataSourceConfig, String tableNames) {
        boolean serviceClassNameStartWithI = false;
        boolean enableTableFieldAnnotation = true;
        GlobalConfig config = new GlobalConfig();
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig
            .setCapitalMode(true)
            .setEntityLombokModel(false)
            // .setDbColumnUnderline(true) 改为如下 2 个配置
            .setNaming(NamingStrategy.underline_to_camel)
            .setColumnNaming(NamingStrategy.underline_to_camel)
            .entityTableFieldAnnotationEnable(enableTableFieldAnnotation)
            .setInclude(tableNames);//修改替换成你需要的表名，多个表名传数组
        config.setActiveRecord(false)
            .setAuthor("K神带你飞")
            .setOutputDir("d:\\codeGen")
            .setFileOverride(true);
        if (!serviceClassNameStartWithI) {
            config.setServiceName("%sService");
        }
        new AutoGenerator().setGlobalConfig(config)
            .setDataSource(dataSourceConfig)
            .setStrategy(strategyConfig)
            .setPackageInfo(
                new PackageConfig()
                    .setParent(packageName)
                    .setController("controller")
                    .setEntity("entity")
            ).execute();
    }

}
