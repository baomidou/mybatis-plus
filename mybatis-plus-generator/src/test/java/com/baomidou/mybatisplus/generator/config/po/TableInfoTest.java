package com.baomidou.mybatisplus.generator.config.po;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.INameConvert;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.fill.Column;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Date;


/**
 * @author nieqiurong 2020/9/21.
 */
public class TableInfoTest {

    public static final DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "").build();

    @Test
    void getFieldNamesTest() {
        TableInfo tableInfo;
        ConfigBuilder configBuilder;
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, GeneratorBuilder.strategyConfig(), null, null, null);
        tableInfo = new TableInfo(configBuilder, "name");
        tableInfo.addField(new TableField(configBuilder, "name").setColumnName("name"));
        Assertions.assertEquals(tableInfo.getFieldNames(), "name");

        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, GeneratorBuilder.strategyConfig(), null, null, null);
        tableInfo = new TableInfo(configBuilder, "name");
        tableInfo.addField(new TableField(configBuilder, "name").setColumnName("name"));
        tableInfo.addField(new TableField(configBuilder, "age").setColumnName("age"));
        Assertions.assertEquals(tableInfo.getFieldNames(), "name, age");

        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, GeneratorBuilder.strategyConfig(), null, null, null);
        tableInfo = new TableInfo(configBuilder, "name");
        tableInfo.addField(new TableField(configBuilder, "name").setColumnName("name"));
        tableInfo.addField(new TableField(configBuilder, "age").setColumnName("age"));
        tableInfo.addField(new TableField(configBuilder, "phone").setColumnName("phone"));
        Assertions.assertEquals(tableInfo.getFieldNames(), "name, age, phone");
    }

    @Test
    void processTableTest() {
        StrategyConfig strategyConfig = GeneratorBuilder.strategyConfig();
        TableInfo tableInfo = new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig, null, GeneratorBuilder.globalConfig(), null), "user");
        tableInfo.processTable();
        Assertions.assertFalse(tableInfo.isConvert());
        Assertions.assertEquals("UserMapper", tableInfo.getMapperName());
        Assertions.assertEquals("UserMapper", tableInfo.getXmlName());
        Assertions.assertEquals("IUserService", tableInfo.getServiceName());
        Assertions.assertEquals("UserServiceImpl", tableInfo.getServiceImplName());
        Assertions.assertEquals("UserController", tableInfo.getControllerName());

        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().formatFileName("%sEntity")
            .mapperBuilder().formatMapperFileName("%sDao").formatXmlFileName("%sXml")
            .controllerBuilder().formatFileName("%sAction")
            .serviceBuilder().formatServiceFileName("%sService").formatServiceImplFileName("%sServiceImp");
        tableInfo = new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig,
            null, null, null), "user");
        tableInfo.processTable();
        Assertions.assertTrue(tableInfo.isConvert());
        Assertions.assertEquals("UserEntity", tableInfo.getEntityName());
        Assertions.assertEquals("UserDao", tableInfo.getMapperName());
        Assertions.assertEquals("UserXml", tableInfo.getXmlName());
        Assertions.assertEquals("UserService", tableInfo.getServiceName());
        Assertions.assertEquals("UserServiceImp", tableInfo.getServiceImplName());
        Assertions.assertEquals("UserAction", tableInfo.getControllerName());
        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().nameConvert(new INameConvert() {
            @Override
            public @NotNull String entityNameConvert(@NotNull TableInfo tableInfo) {
                return "E" + tableInfo.getName();
            }

            @Override
            public @NotNull String propertyNameConvert(@NotNull TableField field) {
                return field.getName();
            }
        });
        tableInfo = new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig, null, null, null), "user");
        tableInfo.processTable();
        Assertions.assertTrue(tableInfo.isConvert());
        Assertions.assertEquals("Euser", tableInfo.getEntityName());
    }

    @Test
    void importPackageTest() {
        TableInfo tableInfo;
        StrategyConfig strategyConfig;
        ConfigBuilder configBuilder;
        tableInfo = new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, GeneratorBuilder.strategyConfig(), null, null, null), "user");
        tableInfo.importPackage();
        Assertions.assertEquals(1, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));

        tableInfo = new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, GeneratorBuilder.strategyConfig(), null, null, null), "user").setEntityName("userEntity").setConvert();
        tableInfo.importPackage();
        Assertions.assertEquals(2, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(TableName.class.getName()));

        strategyConfig = GeneratorBuilder.strategyConfigBuilder().entityBuilder().superClass("con.baomihua.demo.SuperEntity").build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig, null, null, null);
        tableInfo = new TableInfo(configBuilder, "user");
        tableInfo.importPackage();
        Assertions.assertEquals(2, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains("con.baomihua.demo.SuperEntity"));

        tableInfo = new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, GeneratorBuilder.strategyConfigBuilder()
            .entityBuilder().enableActiveRecord().build(), null, null, null), "user");
        tableInfo.importPackage();
        Assertions.assertEquals(2, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Model.class.getName()));

        strategyConfig = GeneratorBuilder.strategyConfig();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        tableInfo = new TableInfo(configBuilder, "user");
        tableInfo.addField(new TableField(configBuilder, "u_id").setColumnName("u_id").setPropertyName("uid", DbColumnType.LONG).primaryKey(true));
        tableInfo.importPackage();
        Assertions.assertEquals(3, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(TableId.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(IdType.class.getName()));

        strategyConfig = GeneratorBuilder.strategyConfig();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        tableInfo = new TableInfo(configBuilder, "user");
        tableInfo.addField(new TableField(configBuilder, "u_id").setPropertyName("uid", DbColumnType.LONG).primaryKey(true));
        tableInfo.importPackage();
        Assertions.assertEquals(3, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(TableId.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(IdType.class.getName()));

        strategyConfig = GeneratorBuilder.strategyConfigBuilder().entityBuilder().logicDeleteColumnName("delete_flag").build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        tableInfo = new TableInfo(configBuilder, "user");
        tableInfo.addField(new TableField(configBuilder, "u_id").setColumnName("u_id").setPropertyName("uid", DbColumnType.LONG).primaryKey(true));
        tableInfo.addField(new TableField(configBuilder, "delete_flag").setColumnName("delete_flag").setPropertyName("deleteFlag", DbColumnType.BOOLEAN));
        tableInfo.importPackage();
        Assertions.assertEquals(4, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(TableLogic.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(TableId.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(IdType.class.getName()));

        strategyConfig = GeneratorBuilder.strategyConfig();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig.entityBuilder().idType(IdType.ASSIGN_ID).build(), null, null, null);
        tableInfo = new TableInfo(configBuilder, "user");
        tableInfo.addField(new TableField(configBuilder, "name").setPropertyName("name", DbColumnType.STRING));
        tableInfo.importPackage();
        Assertions.assertEquals(1, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));

        strategyConfig = GeneratorBuilder.strategyConfig();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig.entityBuilder().idType(IdType.ASSIGN_ID).build(), null, null, null);
        tableInfo = new TableInfo(configBuilder, "user").setHavePrimaryKey(true);
        tableInfo.addField(new TableField(configBuilder, "u_id").setColumnName("u_id").setPropertyName("uid", DbColumnType.LONG).primaryKey(true));
        tableInfo.importPackage();
        Assertions.assertEquals(3, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(TableId.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(IdType.class.getName()));

        strategyConfig = GeneratorBuilder.strategyConfig().entityBuilder().addTableFills(new Column("create_time", FieldFill.DEFAULT)).build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        tableInfo = new TableInfo(configBuilder, "user").setHavePrimaryKey(true);
        tableInfo.addField(new TableField(configBuilder, "u_id").setColumnName("u_id").setPropertyName("uid", DbColumnType.LONG).primaryKey(true));
        tableInfo.addField(new TableField(configBuilder, "create_time").setColumnName("create_time").setPropertyName("createTime", DbColumnType.DATE));
        tableInfo.importPackage();
        Assertions.assertEquals(6, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Date.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(IdType.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(TableId.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(com.baomidou.mybatisplus.annotation.TableField.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(FieldFill.class.getName()));

        strategyConfig = GeneratorBuilder.strategyConfigBuilder().entityBuilder().versionColumnName("version").build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        tableInfo = new TableInfo(configBuilder, "user").setHavePrimaryKey(true);
        tableInfo.addField(new TableField(configBuilder, "u_id").setPropertyName("uid", DbColumnType.LONG).primaryKey(true));
        tableInfo.addField(new TableField(configBuilder, "version").setPropertyName("version", DbColumnType.LONG));
        tableInfo.importPackage();
        Assertions.assertEquals(4, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(IdType.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(TableId.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Version.class.getName()));
    }

    @Test
    void setEntityNameTest() {
        ConfigBuilder configBuilder;
        Assertions.assertTrue(new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, GeneratorBuilder.strategyConfig(), null, null, null), "user").setEntityName("UserEntity").isConvert());
        Assertions.assertFalse(new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, GeneratorBuilder.strategyConfig(), null, null, null), "user").setEntityName("User").isConvert());
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, GeneratorBuilder.strategyConfigBuilder().enableCapitalMode().build(), null, null, null);
        Assertions.assertFalse(new TableInfo(configBuilder, "USER").setEntityName("User").isConvert());
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, GeneratorBuilder.strategyConfigBuilder().enableCapitalMode().build(), null, null, null);
        Assertions.assertTrue(new TableInfo(configBuilder, "USER").setEntityName("UserEntity").isConvert());
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, GeneratorBuilder.strategyConfigBuilder().entityBuilder().naming(NamingStrategy.no_change).build(), null, null, null);
        Assertions.assertTrue(new TableInfo(configBuilder, "test_user").setEntityName("TestUser").isConvert());
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, GeneratorBuilder.strategyConfigBuilder().entityBuilder().naming(NamingStrategy.no_change).build(), null, null, null);
        Assertions.assertFalse(new TableInfo(configBuilder, "user").setEntityName("User").isConvert());
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, GeneratorBuilder.strategyConfigBuilder().entityBuilder().naming(NamingStrategy.underline_to_camel).build(), null, null, null);
        Assertions.assertTrue(new TableInfo(configBuilder, "test_user").setEntityName("TestUser").isConvert());
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, GeneratorBuilder.strategyConfigBuilder().entityBuilder().naming(NamingStrategy.underline_to_camel).build(), null, null, null);
        Assertions.assertTrue(new TableInfo(configBuilder, "TEST_USER").setEntityName("TestUser").isConvert());
    }
}
