package com.baomidou.mybatisplus.test.generator;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * <p>
 * 自定义模板，生成代码
 * 仅示例了mapper.xml文件
 * </p>
 *
 * @author yuxiaobin
 * @date 2018/5/23
 */
public class CodeGeneratorCustomTemplate {

    /**
     * 是否强制带上注解
     */
    boolean enableTableFieldAnnotation = false;
    /**
     * 生成的注解带上IdType类型
     */
    IdType tableIdType = null;
    /**
     * 是否去掉生成实体的属性名前缀
     */
    String[] fieldPrefix = null;
    /**
     * 生成的Service 接口类名是否以I开头
     * 默认是以I开头
     * user表 -> IUserService, UserServiceImpl
     */
    boolean serviceClassNameStartWithI = true;

    private void generateByTablesWithInjectConfig(String packageName, String... tableNames) {
        GlobalConfig config = new GlobalConfig();
        String dbUrl = "jdbc:mysql://localhost:3306/mybatis-plus";
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL)
            .setUrl(dbUrl)
            .setUsername("root")
            .setPassword("")
            .setDriverName("com.mysql.jdbc.Driver");
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig
            .setVersionFieldName("version")
            .setCapitalMode(true)
            .setEntityLombokModel(false)
            .setDbColumnUnderline(true)
            .setNaming(NamingStrategy.underline_to_camel)
            .entityTableFieldAnnotationEnable(enableTableFieldAnnotation)
            .fieldPrefix(fieldPrefix)//test_id -> id, test_type -> type
            .setInclude(tableNames);//修改替换成你需要的表名，多个表名传数组
        config.setActiveRecord(false)
            .setIdType(tableIdType)
            .setAuthor("K神带你飞")
            .setBaseResultMap(true)
            .setOutputDir("d:\\codeGen")
            .setFileOverride(true);
        if (!serviceClassNameStartWithI) {
            config.setServiceName("%sService");
        }
        TemplateConfig templateConfig = new TemplateConfig()
            .setXml("/templates/mapper2.xml");//注意：不要带上.vm
        InjectionConfig injectionConfig = new InjectionConfig() {
            @Override
            public void initMap() {//自定义参数
                Map<String, Object> map = new HashMap<>();
                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
                this.setMap(map);
            }
        };
        new AutoGenerator().setGlobalConfig(config)
            .setTemplate(templateConfig)//自定义模板路径
            .setCfg(injectionConfig)
            .setDataSource(dataSourceConfig)
            .setStrategy(strategyConfig)
            .setPackageInfo(
                new PackageConfig()
                    .setParent(packageName)
                    .setController("controller")
                    .setEntity("entity")
            ).execute();
    }


    @Test
    public void generateCodeWithInjectConfig() {
        String packageName = "com.baomidou.springboot";
        enableTableFieldAnnotation = false;
        tableIdType = null;
        generateByTablesWithInjectConfig(packageName, "user");
    }
}
