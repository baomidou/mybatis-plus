package com.baomidou.mybatisplus.generator.config.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.fill.Property;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author nieqiurong 2020/10/8.
 */
public class TableFieldTest {

    @Test
    void convertTest() {
        ConfigBuilder configBuilder;
        TableField tableField;
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig, GeneratorBuilder.strategyConfig(), null, GeneratorBuilder.globalConfig(), null);
        Assertions.assertFalse(new TableField(configBuilder, "desc").setColumnName("desc").setPropertyName("desc", DbColumnType.STRING).isConvert());
        Assertions.assertTrue(new TableField(configBuilder, "desc").setColumnName("desc").setPropertyName("desc1", DbColumnType.STRING).isConvert());
        Assertions.assertFalse(new TableField(configBuilder, "DESC").setColumnName("DESC").setPropertyName("desc", DbColumnType.STRING).isConvert());
        Assertions.assertFalse(new TableField(configBuilder, "desc").setColumnName("desc").setPropertyName("desc", DbColumnType.STRING).isConvert());
        Assertions.assertTrue(new TableField(configBuilder, "desc").setColumnName("desc").setPropertyName("desc1", DbColumnType.STRING).isConvert());

        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig, GeneratorBuilder.strategyConfigBuilder().entityBuilder().enableTableFieldAnnotation().build(), null, GeneratorBuilder.globalConfig(), null);
        Assertions.assertTrue(new TableField(configBuilder, "name").setColumnName("name").setPropertyName("name", DbColumnType.STRING).isConvert());
        Assertions.assertTrue(new TableField(configBuilder, "name").setColumnName("name").setPropertyName("name1", DbColumnType.STRING).isConvert());

        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig, GeneratorBuilder.strategyConfigBuilder().entityBuilder().enableChainModel().build(), null, GeneratorBuilder.globalConfig(), null);
        Assertions.assertFalse(new TableField(configBuilder, "NAME").setColumnName("NAME").setPropertyName("name", DbColumnType.STRING).isConvert());
        Assertions.assertTrue(new TableField(configBuilder, "USER_NAME").setColumnName("USER_NAME").setPropertyName("userName1", DbColumnType.STRING).isConvert());
        Assertions.assertFalse(new TableField(configBuilder, "USER_NAME").setColumnName("USER_NAME").setPropertyName("userName", DbColumnType.STRING).isConvert());

        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig, GeneratorBuilder.strategyConfigBuilder().entityBuilder().columnNaming(NamingStrategy.underline_to_camel).build(), null, GeneratorBuilder.globalConfig(), null);
        Assertions.assertFalse(new TableField(configBuilder, "user_name").setColumnName("user_name").setPropertyName("userName", DbColumnType.STRING).isConvert());
        Assertions.assertFalse(new TableField(configBuilder, "USER_NAME").setColumnName("USER_NAME").setPropertyName("userName", DbColumnType.STRING).isConvert());

        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig, GeneratorBuilder.strategyConfigBuilder().entityBuilder().columnNaming(NamingStrategy.no_change).build(), null, GeneratorBuilder.globalConfig(), null);
        Assertions.assertTrue(new TableField(configBuilder, "user_name").setColumnName("user_name").setPropertyName("userName", DbColumnType.STRING).isConvert());
        Assertions.assertFalse(new TableField(configBuilder, "USER_NAME").setColumnName("USER_NAME").setPropertyName("USER_NAME", DbColumnType.STRING).isConvert());
        Assertions.assertFalse(new TableField(configBuilder, "NAME").setColumnName("NAME").setPropertyName("name", DbColumnType.STRING).isConvert());

        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig, GeneratorBuilder.strategyConfigBuilder().entityBuilder().enableRemoveIsPrefix().build(), null, GeneratorBuilder.globalConfig(), null);
        tableField = new TableField(configBuilder, "delete").setColumnName("delete").setPropertyName("delete", DbColumnType.BOOLEAN);
        Assertions.assertEquals("delete", tableField.getPropertyName());
        Assertions.assertFalse(tableField.isConvert());
        tableField = new TableField(configBuilder, "delete").setColumnName("delete").setPropertyName("delete", DbColumnType.BOOLEAN);
        Assertions.assertEquals("delete", tableField.getPropertyName());
        Assertions.assertFalse(tableField.isConvert());
        tableField = new TableField(configBuilder, "is_delete").setColumnName("is_delete").setPropertyName("isDelete", DbColumnType.BOOLEAN);
        Assertions.assertEquals("delete", tableField.getPropertyName());
        Assertions.assertTrue(tableField.isConvert());
        tableField = new TableField(configBuilder, "is_delete").setColumnName("is_delete").setPropertyName("isDelete", DbColumnType.BOOLEAN);
        Assertions.assertEquals("delete", tableField.getPropertyName());
        Assertions.assertTrue(tableField.isConvert());
    }

    @Test
    void versionFieldTest() {
        ConfigBuilder configBuilder;
        StrategyConfig strategyConfig;
        strategyConfig = new StrategyConfig.Builder().entityBuilder().versionColumnName("c_version").build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig, strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        Assertions.assertFalse(new TableField(configBuilder, "version").setPropertyName("version", DbColumnType.LONG).isVersionField());
        Assertions.assertFalse(new TableField(configBuilder, "version").setPropertyName("version", DbColumnType.LONG).isVersionField());
        Assertions.assertTrue(new TableField(configBuilder, "c_version").setPropertyName("version", DbColumnType.LONG).isVersionField());
        Assertions.assertTrue(new TableField(configBuilder, "C_VERSION").setPropertyName("version", DbColumnType.LONG).isVersionField());

        strategyConfig = new StrategyConfig.Builder().entityBuilder().versionPropertyName("version").build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig, strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        Assertions.assertTrue(new TableField(configBuilder, "version").setPropertyName("version", DbColumnType.LONG).isVersionField());
        Assertions.assertTrue(new TableField(configBuilder, "VERSION").setPropertyName("version", DbColumnType.LONG).isVersionField());
        Assertions.assertFalse(new TableField(configBuilder, "c_version").setPropertyName("cVersion", DbColumnType.LONG).isVersionField());
        Assertions.assertFalse(new TableField(configBuilder, "C_VERSION").setPropertyName("cVersion", DbColumnType.LONG).isVersionField());
    }

    @Test
    void logicDeleteFiledTest() {
        ConfigBuilder configBuilder;
        StrategyConfig strategyConfig;
        strategyConfig = new StrategyConfig.Builder().entityBuilder().logicDeleteColumnName("delete").build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig, strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        Assertions.assertTrue(new TableField(configBuilder, "DELETE").setPropertyName("delete", DbColumnType.BOOLEAN).isLogicDeleteField());
        Assertions.assertTrue(new TableField(configBuilder, "delete").setPropertyName("delete", DbColumnType.BOOLEAN).isLogicDeleteField());

        strategyConfig = new StrategyConfig.Builder().entityBuilder().logicDeletePropertyName("delete").build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig, strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        Assertions.assertTrue(new TableField(configBuilder, "IS_DELETE").setPropertyName("delete", DbColumnType.BOOLEAN).isLogicDeleteField());
        Assertions.assertTrue(new TableField(configBuilder, "is_delete").setPropertyName("delete", DbColumnType.BOOLEAN).isLogicDeleteField());
        Assertions.assertFalse(new TableField(configBuilder, "is_delete").setPropertyName("isDelete", DbColumnType.BOOLEAN).isLogicDeleteField());

        strategyConfig = new StrategyConfig.Builder().entityBuilder().enableRemoveIsPrefix().logicDeletePropertyName("delete").build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig, strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        Assertions.assertTrue(new TableField(configBuilder, "IS_DELETE").setPropertyName("delete", DbColumnType.BOOLEAN).isLogicDeleteField());
        Assertions.assertTrue(new TableField(configBuilder, "is_delete").setPropertyName("delete", DbColumnType.BOOLEAN).isLogicDeleteField());
        Assertions.assertTrue(new TableField(configBuilder, "is_delete").setPropertyName("isDelete", DbColumnType.BOOLEAN).isLogicDeleteField());
        Assertions.assertFalse(new TableField(configBuilder, "is_delete").setPropertyName("isDelete", DbColumnType.INTEGER).isLogicDeleteField());
    }

    @Test
    void fillTest() {
        ConfigBuilder configBuilder;
        StrategyConfig strategyConfig;
        strategyConfig = new StrategyConfig.Builder()
            .entityBuilder()
            .addTableFills(
                new Column("create_time", FieldFill.INSERT), new Column("update_time", FieldFill.UPDATE),
                new Property("createBy", FieldFill.INSERT), new Property("updateBy", FieldFill.INSERT),
                new Column("create_user")
            ).build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig, strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        Assertions.assertNotNull(new TableField(configBuilder, "create_time").getFill());
        Assertions.assertNotNull(new TableField(configBuilder, "update_time").getFill());
        Assertions.assertNull(new TableField(configBuilder, "name").getFill());
        Assertions.assertNull(new TableField(configBuilder, "createBy").getFill());
        Assertions.assertNull(new TableField(configBuilder, "updateBy").getFill());
        Assertions.assertNull(new TableField(configBuilder, "create_by").getFill());
        Assertions.assertNull(new TableField(configBuilder, "update_by").getFill());
        Assertions.assertNotNull(new TableField(configBuilder, "createBy").setPropertyName("createBy", DbColumnType.STRING).getFill());
        Assertions.assertNotNull(new TableField(configBuilder, "updateBy").setPropertyName("createBy", DbColumnType.STRING).getFill());
        Assertions.assertNotNull(new TableField(configBuilder, "create_user").setPropertyName("createUser", DbColumnType.STRING).getFill());
    }
}
