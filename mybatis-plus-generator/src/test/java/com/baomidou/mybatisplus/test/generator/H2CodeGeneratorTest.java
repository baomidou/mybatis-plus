package com.baomidou.mybatisplus.test.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.LikeTable;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.h2.Driver;
import org.junit.jupiter.api.Test;

/**
 * H2代码生成
 *
 * @author nieqiuqiu
 */
class H2CodeGeneratorTest {

    private static String outPutDir = System.getProperty("os.name").toLowerCase().contains("windows") ? "D://tmp" : "tmp";

    private DataSourceConfig dataSourceConfig() {
        String dbUrl = "jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.H2)
            .setUrl(dbUrl)
            .setUsername("sa")
            .setPassword("")
            .setDriverName(Driver.class.getName());
        return dataSourceConfig;
    }

    private StrategyConfig strategyConfig() {
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig
            .setCapitalMode(true)
            .setEntityLombokModel(false)
            .setEnableSqlFilter(true)
            .setNaming(NamingStrategy.underline_to_camel);
        return strategyConfig;
    }

    private GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setActiveRecord(false)
            .setIdType(IdType.ASSIGN_ID)
            .setAuthor("test")
            .setOutputDir(outPutDir)
            .setOpen(true)
            .setFileOverride(true);
        return globalConfig;
    }

    @Test
    void testLike() {
        new AutoGenerator()
            .setGlobalConfig(globalConfig())
            .setDataSource(dataSourceConfig())
            .setStrategy(strategyConfig().setLikeTable(new LikeTable("USERS")))
            .execute();
    }

    @Test
    void testNotLike() {
        new AutoGenerator()
            .setGlobalConfig(globalConfig())
            .setDataSource(dataSourceConfig())
            .setStrategy(strategyConfig().setNotLikeTable(new LikeTable("USERS")))
            .execute();
    }

    @Test
    void testInclude() {
        new AutoGenerator()
            .setGlobalConfig(globalConfig())
            .setDataSource(dataSourceConfig())
            .setStrategy(strategyConfig().setInclude("USERS"))
            .execute();
    }

    @Test
    void testExclude() {
        new AutoGenerator()
            .setGlobalConfig(globalConfig())
            .setDataSource(dataSourceConfig())
            .setStrategy(strategyConfig().setExclude("USERS"))
            .execute();
    }

    @Test
    void testLikeAndInclude(){
        new AutoGenerator()
            .setGlobalConfig(globalConfig())
            .setDataSource(dataSourceConfig())
            .setStrategy(strategyConfig().setLikeTable(new LikeTable("TABLE")).setInclude("TABLE_PRIVILEGES","TABLE_TYPES"))
            .execute();
    }

    @Test
    void testLikeAndExclude(){
        new AutoGenerator()
            .setGlobalConfig(globalConfig())
            .setDataSource(dataSourceConfig())
            .setStrategy(strategyConfig().setLikeTable(new LikeTable("TABLE")).setExclude("TABLE_PRIVILEGES","TABLE_TYPES"))
            .execute();
    }

    @Test
    void testNotLikeAndInclude(){
        new AutoGenerator()
            .setGlobalConfig(globalConfig())
            .setDataSource(dataSourceConfig())
            .setStrategy(strategyConfig().setNotLikeTable(new LikeTable("TABLE")).setInclude("USERS"))
            .execute();
    }

    @Test
    void testNotLikeAndExclude(){
        new AutoGenerator()
            .setGlobalConfig(globalConfig())
            .setDataSource(dataSourceConfig())
            .setStrategy(strategyConfig().setNotLikeTable(new LikeTable("TABLE")).setExclude("USERS"))
            .execute();
    }
}
