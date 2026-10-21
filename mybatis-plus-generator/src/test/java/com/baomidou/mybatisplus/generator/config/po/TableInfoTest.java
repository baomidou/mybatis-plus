package com.baomidou.mybatisplus.generator.config.po;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.INameConvert;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.spring.activerecord.Model;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2020/9/21.
 */
public class TableInfoTest {

    public static final DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(
            "jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "").build();

    @Test
    void getFieldNamesTest() {
        TableInfo tableInfo;
        ConfigBuilder configBuilder;
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig,
                GeneratorBuilder.strategyConfig(), null, null, null);
        tableInfo = new TableInfo(configBuilder, "name");
        tableInfo.addField(new TableField(configBuilder, "name").setColumnName("name"));
        assertThat(tableInfo.getFieldNames()).isEqualTo("name");

        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig,
                GeneratorBuilder.strategyConfig(), null, null, null);
        tableInfo = new TableInfo(configBuilder, "name");
        tableInfo.addField(new TableField(configBuilder, "name").setColumnName("name"));
        tableInfo.addField(new TableField(configBuilder, "age").setColumnName("age"));
        assertThat(tableInfo.getFieldNames()).isEqualTo("name, age");

        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig,
                GeneratorBuilder.strategyConfig(), null, null, null);
        tableInfo = new TableInfo(configBuilder, "name");
        tableInfo.addField(new TableField(configBuilder, "name").setColumnName("name"));
        tableInfo.addField(new TableField(configBuilder, "age").setColumnName("age"));
        tableInfo.addField(new TableField(configBuilder, "phone").setColumnName("phone"));
        assertThat(tableInfo.getFieldNames()).isEqualTo("name, age, phone");
    }

    @Test
    void processTableTest() {
        StrategyConfig strategyConfig = GeneratorBuilder.strategyConfig();
        TableInfo tableInfo = new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig,
                strategyConfig, null, GeneratorBuilder.globalConfig(), null), "user");
        tableInfo.processTable();
        assertThat(tableInfo.isConvert()).isFalse();
        assertThat(tableInfo.getMapperName()).isEqualTo("UserMapper");
        assertThat(tableInfo.getXmlName()).isEqualTo("UserMapper");
        assertThat(tableInfo.getServiceName()).isEqualTo("IUserService");
        assertThat(tableInfo.getServiceImplName()).isEqualTo("UserServiceImpl");
        assertThat(tableInfo.getControllerName()).isEqualTo("UserController");

        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().formatFileName("%sEntity").mapperBuilder().formatMapperFileName("%sDao")
                .formatXmlFileName("%sXml").controllerBuilder().formatFileName("%sAction").serviceBuilder()
                .formatServiceFileName("%sService").formatServiceImplFileName("%sServiceImp");
        tableInfo = new TableInfo(
                new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig, null, null, null),
                "user");
        tableInfo.processTable();
        assertThat(tableInfo.isConvert()).isTrue();
        assertThat(tableInfo.getEntityName()).isEqualTo("UserEntity");
        assertThat(tableInfo.getMapperName()).isEqualTo("UserDao");
        assertThat(tableInfo.getXmlName()).isEqualTo("UserXml");
        assertThat(tableInfo.getServiceName()).isEqualTo("UserService");
        assertThat(tableInfo.getServiceImplName()).isEqualTo("UserServiceImp");
        assertThat(tableInfo.getControllerName()).isEqualTo("UserAction");
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
        tableInfo = new TableInfo(
                new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig, null, null, null),
                "user");
        tableInfo.processTable();
        assertThat(tableInfo.isConvert()).isTrue();
        assertThat(tableInfo.getEntityName()).isEqualTo("Euser");
    }

    @Test
    void importPackageTest() {
        TableInfo tableInfo;
        StrategyConfig strategyConfig;
        ConfigBuilder configBuilder;
        tableInfo = new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig,
                GeneratorBuilder.strategyConfig(), null, null, null), "user");
        tableInfo.importPackage();
        assertThat(tableInfo.getImportPackages()).hasSize(1);
        assertThat(tableInfo.getImportPackages().contains(Serializable.class.getName())).isTrue();

        tableInfo = new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig,
                GeneratorBuilder.strategyConfig(), null, null, null), "user").setEntityName("userEntity").setConvert();
        tableInfo.importPackage();
        assertThat(tableInfo.getImportPackages()).hasSize(2);
        assertThat(tableInfo.getImportPackages().contains(Serializable.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(TableName.class.getName())).isTrue();

        strategyConfig = GeneratorBuilder.strategyConfigBuilder().entityBuilder()
                .superClass("con.baomihua.demo.SuperEntity").build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig, null,
                null, null);
        tableInfo = new TableInfo(configBuilder, "user");
        tableInfo.importPackage();
        assertThat(tableInfo.getImportPackages()).hasSize(2);
        assertThat(tableInfo.getImportPackages().contains(Serializable.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains("con.baomihua.demo.SuperEntity")).isTrue();

        tableInfo = new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig,
                GeneratorBuilder.strategyConfigBuilder().entityBuilder().enableActiveRecord().build(), null, null,
                null), "user");
        tableInfo.importPackage();
        assertThat(tableInfo.getImportPackages()).hasSize(2);
        assertThat(tableInfo.getImportPackages().contains(Serializable.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(Model.class.getName())).isTrue();

        strategyConfig = GeneratorBuilder.strategyConfig();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig, null,
                GeneratorBuilder.globalConfig(), null);
        tableInfo = new TableInfo(configBuilder, "user");
        tableInfo.addField(
                new TableField(configBuilder, "u_id").setColumnName("u_id").primaryKey(true)
                        .setPropertyName("uid", DbColumnType.LONG));
        tableInfo.importPackage();
        assertThat(tableInfo.getImportPackages()).hasSize(3);
        assertThat(tableInfo.getImportPackages().contains(Serializable.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(TableId.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(IdType.class.getName())).isTrue();

        strategyConfig = GeneratorBuilder.strategyConfig();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig, null,
                GeneratorBuilder.globalConfig(), null);
        tableInfo = new TableInfo(configBuilder, "user");
        tableInfo.addField(
                new TableField(configBuilder, "u_id").setPropertyName("uid", DbColumnType.LONG).primaryKey(true));
        tableInfo.importPackage();
        assertThat(tableInfo.getImportPackages()).hasSize(3);
        assertThat(tableInfo.getImportPackages().contains(Serializable.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(TableId.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(IdType.class.getName())).isTrue();

        strategyConfig = GeneratorBuilder.strategyConfigBuilder().entityBuilder().logicDeleteColumnName("delete_flag")
                .build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig, null,
                GeneratorBuilder.globalConfig(), null);
        tableInfo = new TableInfo(configBuilder, "user");
        tableInfo.addField(
                new TableField(configBuilder, "u_id").setColumnName("u_id").primaryKey(true)
                        .setPropertyName("uid", DbColumnType.LONG));
        tableInfo.addField(
                new TableField(configBuilder, "delete_flag").setColumnName("delete_flag")
                        .setPropertyName("deleteFlag", DbColumnType.BOOLEAN));
        tableInfo.importPackage();
        assertThat(tableInfo.getImportPackages()).hasSize(4);
        assertThat(tableInfo.getImportPackages().contains(Serializable.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(TableLogic.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(TableId.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(IdType.class.getName())).isTrue();

        strategyConfig = GeneratorBuilder.strategyConfig();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig,
                strategyConfig.entityBuilder().idType(IdType.ASSIGN_ID).build(), null, null, null);
        tableInfo = new TableInfo(configBuilder, "user");
        tableInfo.addField(new TableField(configBuilder, "name").setPropertyName("name", DbColumnType.STRING));
        tableInfo.importPackage();
        assertThat(tableInfo.getImportPackages()).hasSize(1);
        assertThat(tableInfo.getImportPackages().contains(Serializable.class.getName())).isTrue();

        strategyConfig = GeneratorBuilder.strategyConfig();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig,
                strategyConfig.entityBuilder().idType(IdType.ASSIGN_ID).build(), null, null, null);
        tableInfo = new TableInfo(configBuilder, "user").setHavePrimaryKey(true);
        tableInfo.addField(
                new TableField(configBuilder, "u_id").setColumnName("u_id").primaryKey(true)
                        .setPropertyName("uid", DbColumnType.LONG));
        tableInfo.importPackage();
        assertThat(tableInfo.getImportPackages()).hasSize(3);
        assertThat(tableInfo.getImportPackages().contains(Serializable.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(TableId.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(IdType.class.getName())).isTrue();

        strategyConfig = GeneratorBuilder.strategyConfig().entityBuilder()
                .addTableFills(new Column("create_time", FieldFill.DEFAULT)).build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig, null,
                GeneratorBuilder.globalConfig(), null);
        tableInfo = new TableInfo(configBuilder, "user").setHavePrimaryKey(true);
        tableInfo.addField(
                new TableField(configBuilder, "u_id").setColumnName("u_id").primaryKey(true)
                        .setPropertyName("uid", DbColumnType.LONG));
        tableInfo.addField(
                new TableField(configBuilder, "create_time").setColumnName("create_time")
                        .setPropertyName("createTime", DbColumnType.DATE));
        tableInfo.importPackage();
        assertThat(tableInfo.getImportPackages()).hasSize(6);
        assertThat(tableInfo.getImportPackages().contains(Date.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(Serializable.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(IdType.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(TableId.class.getName())).isTrue();
        assertThat(
                tableInfo.getImportPackages().contains(com.baomidou.mybatisplus.annotation.TableField.class.getName()))
                .isTrue();
        assertThat(tableInfo.getImportPackages().contains(FieldFill.class.getName())).isTrue();

        strategyConfig = GeneratorBuilder.strategyConfigBuilder().entityBuilder().versionColumnName("version").build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, strategyConfig, null,
                GeneratorBuilder.globalConfig(), null);
        tableInfo = new TableInfo(configBuilder, "user").setHavePrimaryKey(true);
        tableInfo.addField(
                new TableField(configBuilder, "u_id").primaryKey(true).setPropertyName("uid", DbColumnType.LONG));
        tableInfo.addField(new TableField(configBuilder, "version").setPropertyName("version", DbColumnType.LONG));
        tableInfo.importPackage();
        assertThat(tableInfo.getImportPackages()).hasSize(4);
        assertThat(tableInfo.getImportPackages().contains(Serializable.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(IdType.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(TableId.class.getName())).isTrue();
        assertThat(tableInfo.getImportPackages().contains(Version.class.getName())).isTrue();
    }

    @Test
    void setEntityNameTest() {
        ConfigBuilder configBuilder;
        assertThat(
                new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig,
                        GeneratorBuilder.strategyConfig(), null, null, null), "user").setEntityName("UserEntity")
                        .isConvert())
                .isTrue();
        assertThat(
                new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig,
                        GeneratorBuilder.strategyConfig(), null, null, null), "user").setEntityName("User").isConvert())
                .isFalse();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig,
                GeneratorBuilder.strategyConfigBuilder().enableCapitalMode().build(), null, null, null);
        assertThat(new TableInfo(configBuilder, "USER").setEntityName("User").isConvert()).isFalse();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig,
                GeneratorBuilder.strategyConfigBuilder().enableCapitalMode().build(), null, null, null);
        assertThat(new TableInfo(configBuilder, "USER").setEntityName("UserEntity").isConvert()).isTrue();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig,
                GeneratorBuilder.strategyConfigBuilder().entityBuilder().naming(NamingStrategy.no_change).build(), null,
                null, null);
        assertThat(new TableInfo(configBuilder, "test_user").setEntityName("TestUser").isConvert()).isTrue();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig,
                GeneratorBuilder.strategyConfigBuilder().entityBuilder().naming(NamingStrategy.no_change).build(), null,
                null, null);
        assertThat(new TableInfo(configBuilder, "user").setEntityName("User").isConvert()).isFalse();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, GeneratorBuilder
                .strategyConfigBuilder().entityBuilder().naming(NamingStrategy.underline_to_camel).build(), null, null,
                null);
        assertThat(new TableInfo(configBuilder, "test_user").setEntityName("TestUser").isConvert()).isTrue();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), dataSourceConfig, GeneratorBuilder
                .strategyConfigBuilder().entityBuilder().naming(NamingStrategy.underline_to_camel).build(), null, null,
                null);
        assertThat(new TableInfo(configBuilder, "TEST_USER").setEntityName("TestUser").isConvert()).isTrue();
    }
}
