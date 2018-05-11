package com.baomidou.mybatisplus.test.generator;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * 自定义模块生成,临时解决方案.
 *
 * @author nieqiurong 2018/5/1
 */
public class CustomModuleGenerator {

    public static void main(String[] args) {
        AutoGenerator mpg = new AutoGenerator().setGlobalConfig(
            new GlobalConfig().setOutputDir("/nieqiurong/baomidou/").setActiveRecord(true).setAuthor("苞米地")
        ).setDataSource(
            new DataSourceConfig().setDbType(DbType.MYSQL).setDriverName("com.mysql.jdbc.Driver").setUsername("root").setPassword("123456").setUrl("jdbc:mysql://127.0.0.1:3306/auth?characterEncoding=utf8")
        ).setStrategy(
            new StrategyConfig().setNaming(NamingStrategy.underline_to_camel)
        ).setPackageInfo(
            new PackageConfig().setParent("com.baomidou").setModuleName("papapa"));
        ConfigBuilder config = new ConfigBuilder(mpg.getPackageInfo(), mpg.getDataSource(), mpg.getStrategy(), mpg.getTemplate(), mpg.getGlobalConfig());
        config.getPathInfo().remove(ConstVal.ENTITY_PATH);//不想生成的模块.
        config.getPathInfo().remove(ConstVal.SERVICE_PATH);//不想生成的模块.
        config.getPathInfo().remove(ConstVal.SERVICEIMPL_PATH);//不想生成的模块.
        config.getPathInfo().remove(ConstVal.MAPPER_PATH);//不想生成的模块.
        config.getPathInfo().remove(ConstVal.XML_PATH);//不想生成的模块.
        config.getPathInfo().remove(ConstVal.CONTROLLER_PATH);//不想生成的模块.
        mpg.setConfig(config);
        mpg.execute();
    }

}
