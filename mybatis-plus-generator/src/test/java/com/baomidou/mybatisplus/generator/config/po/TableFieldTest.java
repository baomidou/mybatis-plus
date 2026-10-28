package com.baomidou.mybatisplus.generator.config.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.fill.Property;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2020/10/8.
 */
@Tag("unit")
public class TableFieldTest {

    @Test
    void convertTest() {
        ConfigBuilder configBuilder;
        TableField tableField;
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig,
                GeneratorBuilder.strategyConfig(), null, GeneratorBuilder.globalConfig(), null);
        assertThat(
                new TableField(configBuilder, "desc").setColumnName("desc").setPropertyName("desc", DbColumnType.STRING)
                        .isConvert())
                .isFalse();
        assertThat(
                new TableField(configBuilder, "desc").setColumnName("desc")
                        .setPropertyName("desc1", DbColumnType.STRING).isConvert())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "DESC").setColumnName("DESC").setPropertyName("desc", DbColumnType.STRING)
                        .isConvert())
                .isFalse();
        assertThat(
                new TableField(configBuilder, "desc").setColumnName("desc").setPropertyName("desc", DbColumnType.STRING)
                        .isConvert())
                .isFalse();
        assertThat(
                new TableField(configBuilder, "desc").setColumnName("desc")
                        .setPropertyName("desc1", DbColumnType.STRING).isConvert())
                .isTrue();

        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig,
                GeneratorBuilder.strategyConfigBuilder().entityBuilder().enableTableFieldAnnotation().build(), null,
                GeneratorBuilder.globalConfig(), null);
        assertThat(
                new TableField(configBuilder, "name").setColumnName("name").setPropertyName("name", DbColumnType.STRING)
                        .isConvert())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "name").setColumnName("name")
                        .setPropertyName("name1", DbColumnType.STRING).isConvert())
                .isTrue();

        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig,
                GeneratorBuilder.strategyConfigBuilder().entityBuilder().enableChainModel().build(), null,
                GeneratorBuilder.globalConfig(), null);
        assertThat(
                new TableField(configBuilder, "NAME").setColumnName("NAME").setPropertyName("name", DbColumnType.STRING)
                        .isConvert())
                .isFalse();
        assertThat(
                new TableField(configBuilder, "USER_NAME").setColumnName("USER_NAME")
                        .setPropertyName("userName1", DbColumnType.STRING).isConvert())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "USER_NAME").setColumnName("USER_NAME")
                        .setPropertyName("userName", DbColumnType.STRING).isConvert())
                .isFalse();

        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig,
                GeneratorBuilder.strategyConfigBuilder().entityBuilder().columnNaming(NamingStrategy.underline_to_camel)
                        .build(),
                null, GeneratorBuilder.globalConfig(), null);
        assertThat(
                new TableField(configBuilder, "user_name").setColumnName("user_name")
                        .setPropertyName("userName", DbColumnType.STRING).isConvert())
                .isFalse();
        assertThat(
                new TableField(configBuilder, "USER_NAME").setColumnName("USER_NAME")
                        .setPropertyName("userName", DbColumnType.STRING).isConvert())
                .isFalse();

        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig,
                GeneratorBuilder.strategyConfigBuilder().entityBuilder().columnNaming(NamingStrategy.no_change).build(),
                null, GeneratorBuilder.globalConfig(), null);
        assertThat(
                new TableField(configBuilder, "user_name").setColumnName("user_name")
                        .setPropertyName("userName", DbColumnType.STRING).isConvert())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "USER_NAME").setColumnName("USER_NAME")
                        .setPropertyName("USER_NAME", DbColumnType.STRING).isConvert())
                .isFalse();
        assertThat(
                new TableField(configBuilder, "NAME").setColumnName("NAME").setPropertyName("name", DbColumnType.STRING)
                        .isConvert())
                .isFalse();

        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig,
                GeneratorBuilder.strategyConfigBuilder().entityBuilder().enableRemoveIsPrefix().build(), null,
                GeneratorBuilder.globalConfig(), null);
        tableField = new TableField(configBuilder, "delete").setColumnName("delete")
                .setPropertyName("delete", DbColumnType.BOOLEAN);
        assertThat(tableField.getPropertyName()).isEqualTo("delete");
        assertThat(tableField.isConvert()).isFalse();
        tableField = new TableField(configBuilder, "delete").setColumnName("delete")
                .setPropertyName("delete", DbColumnType.BOOLEAN);
        assertThat(tableField.getPropertyName()).isEqualTo("delete");
        assertThat(tableField.isConvert()).isFalse();
        tableField = new TableField(configBuilder, "is_delete").setColumnName("is_delete")
                .setPropertyName("isDelete", DbColumnType.BOOLEAN);
        assertThat(tableField.getPropertyName()).isEqualTo("delete");
        assertThat(tableField.isConvert()).isTrue();
        tableField = new TableField(configBuilder, "is_delete").setColumnName("is_delete")
                .setPropertyName("isDelete", DbColumnType.BOOLEAN);
        assertThat(tableField.getPropertyName()).isEqualTo("delete");
        assertThat(tableField.isConvert()).isTrue();
    }

    @Test
    void versionFieldTest() {
        ConfigBuilder configBuilder;
        StrategyConfig strategyConfig;
        strategyConfig = new StrategyConfig.Builder().entityBuilder().versionColumnName("c_version").build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig,
                strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        assertThat(
                new TableField(configBuilder, "version").setPropertyName("version", DbColumnType.LONG).isVersionField())
                .isFalse();
        assertThat(
                new TableField(configBuilder, "version").setPropertyName("version", DbColumnType.LONG).isVersionField())
                .isFalse();
        assertThat(
                new TableField(configBuilder, "c_version").setPropertyName("version", DbColumnType.LONG)
                        .isVersionField())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "C_VERSION").setPropertyName("version", DbColumnType.LONG)
                        .isVersionField())
                .isTrue();

        strategyConfig = new StrategyConfig.Builder().entityBuilder().versionPropertyName("version").build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig,
                strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        assertThat(
                new TableField(configBuilder, "version").setPropertyName("version", DbColumnType.LONG).isVersionField())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "VERSION").setPropertyName("version", DbColumnType.LONG).isVersionField())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "c_version").setPropertyName("cVersion", DbColumnType.LONG)
                        .isVersionField())
                .isFalse();
        assertThat(
                new TableField(configBuilder, "C_VERSION").setPropertyName("cVersion", DbColumnType.LONG)
                        .isVersionField())
                .isFalse();
    }

    @Test
    void logicDeleteFiledTest() {
        ConfigBuilder configBuilder;
        StrategyConfig strategyConfig;
        strategyConfig = new StrategyConfig.Builder().entityBuilder().logicDeleteColumnName("delete").build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig,
                strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        assertThat(
                new TableField(configBuilder, "DELETE").setPropertyName("delete", DbColumnType.BOOLEAN)
                        .isLogicDeleteField())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "delete").setPropertyName("delete", DbColumnType.BOOLEAN)
                        .isLogicDeleteField())
                .isTrue();

        strategyConfig = new StrategyConfig.Builder().entityBuilder().logicDeletePropertyName("delete").build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig,
                strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        assertThat(
                new TableField(configBuilder, "IS_DELETE").setPropertyName("delete", DbColumnType.BOOLEAN)
                        .isLogicDeleteField())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "is_delete").setPropertyName("delete", DbColumnType.BOOLEAN)
                        .isLogicDeleteField())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "is_delete").setPropertyName("isDelete", DbColumnType.BOOLEAN)
                        .isLogicDeleteField())
                .isFalse();

        strategyConfig = new StrategyConfig.Builder().entityBuilder().enableRemoveIsPrefix()
                .logicDeletePropertyName("delete").build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig,
                strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        assertThat(
                new TableField(configBuilder, "IS_DELETE").setPropertyName("delete", DbColumnType.BOOLEAN)
                        .isLogicDeleteField())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "is_delete").setPropertyName("delete", DbColumnType.BOOLEAN)
                        .isLogicDeleteField())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "is_delete").setPropertyName("isDelete", DbColumnType.BOOLEAN)
                        .isLogicDeleteField())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "is_delete").setPropertyName("isDelete", DbColumnType.INTEGER)
                        .isLogicDeleteField())
                .isFalse();
    }

    @Test
    void fillTest() {
        ConfigBuilder configBuilder;
        StrategyConfig strategyConfig;
        strategyConfig = new StrategyConfig.Builder().entityBuilder()
                .addTableFills(
                        new Column("create_time", FieldFill.INSERT), new Column("update_time", FieldFill.UPDATE),
                        new Property("createBy", FieldFill.INSERT), new Property("updateBy", FieldFill.INSERT),
                        new Column("create_user"))
                .build();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig,
                strategyConfig, null, GeneratorBuilder.globalConfig(), null);
        assertThat(new TableField(configBuilder, "create_time").getFill()).isNotNull();
        assertThat(new TableField(configBuilder, "update_time").getFill()).isNotNull();
        assertThat(new TableField(configBuilder, "name").getFill()).isNull();
        assertThat(new TableField(configBuilder, "createBy").getFill()).isNull();
        assertThat(new TableField(configBuilder, "updateBy").getFill()).isNull();
        assertThat(new TableField(configBuilder, "create_by").getFill()).isNull();
        assertThat(new TableField(configBuilder, "update_by").getFill()).isNull();
        assertThat(new TableField(configBuilder, "createBy").setPropertyName("createBy", DbColumnType.STRING).getFill())
                .isNotNull();
        assertThat(new TableField(configBuilder, "updateBy").setPropertyName("createBy", DbColumnType.STRING).getFill())
                .isNotNull();
        assertThat(
                new TableField(configBuilder, "create_user").setPropertyName("createUser", DbColumnType.STRING)
                        .getFill())
                .isNotNull();
    }

    @Test
    void shouldSetConvertFlagWhenPropertyNameDiffersFromColumnName() {
        ConfigBuilder configBuilder;
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig,
                GeneratorBuilder.strategyConfig(), null, GeneratorBuilder.globalConfig(), null);
        assertThat(
                new TableField(configBuilder, "id").primaryKey(true).setPropertyName("id", DbColumnType.LONG)
                        .isConvert())
                .isFalse();
        assertThat(
                new TableField(configBuilder, "id").primaryKey(false).setPropertyName("id", DbColumnType.LONG)
                        .isConvert())
                .isFalse();
        assertThat(
                new TableField(configBuilder, "id").primaryKey(true).setPropertyName("ids", DbColumnType.LONG)
                        .isConvert())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "id").primaryKey(false).setPropertyName("ids", DbColumnType.LONG)
                        .isConvert())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "user_id").primaryKey(true).setPropertyName("userId", DbColumnType.LONG)
                        .isConvert())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "user_id").primaryKey(false).setPropertyName("userId", DbColumnType.LONG)
                        .isConvert())
                .isTrue();
        // 开启tableFieldAnnotationEnable
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig,
                GeneratorBuilder.strategyConfig().entityBuilder().enableTableFieldAnnotation().build(), null,
                GeneratorBuilder.globalConfig(), null);
        assertThat(
                new TableField(configBuilder, "id").primaryKey(true).setPropertyName("id", DbColumnType.LONG)
                        .isConvert())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "id").primaryKey(false).setPropertyName("id", DbColumnType.LONG)
                        .isConvert())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "id").primaryKey(true).setPropertyName("ids", DbColumnType.LONG)
                        .isConvert())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "id").primaryKey(false).setPropertyName("ids", DbColumnType.LONG)
                        .isConvert())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "user_id").primaryKey(true).setPropertyName("userId", DbColumnType.LONG)
                        .isConvert())
                .isTrue();
        assertThat(
                new TableField(configBuilder, "user_id").primaryKey(false).setPropertyName("userId", DbColumnType.LONG)
                        .isConvert())
                .isTrue();
    }

}
