package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.IFill;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.po.TableInfoTest;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.entity.BaseEntity;
import com.baomidou.mybatisplus.generator.entity.SuperEntity;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 策略测试
 * </p>
 *
 * @author hubin
 * @since 2019-02-20
 */
class StrategyConfigTest {

    @Test
    void test() {
        StrategyConfig strategyConfig;
        // 默认全开
        strategyConfig = new StrategyConfig.Builder().build();
        Assertions.assertTrue(strategyConfig.controller().isGenerate());
        Assertions.assertTrue(strategyConfig.entity().isGenerate());
        Assertions.assertTrue(strategyConfig.service().isGenerateService());
        Assertions.assertTrue(strategyConfig.service().isGenerateServiceImpl());
        Assertions.assertTrue(strategyConfig.mapper().isGenerateMapper());
        Assertions.assertTrue(strategyConfig.mapper().isGenerateMapperXml());
        strategyConfig =
            new StrategyConfig.Builder()
                .entityBuilder()
                .javaTemplate("/templates/entity.java")
                .disable()
                .serviceBuilder().disable()
                .disableService().serviceTemplate("/templates/service.java").serviceImplTemplate("/templates/serviceImpl.java")
                .mapperBuilder().disableMapper().disableMapperXml()
                .controllerBuilder().disable().template("")
                .build();
        Assertions.assertFalse(strategyConfig.controller().isGenerate());
        Assertions.assertFalse(strategyConfig.entity().isGenerate());
        Assertions.assertFalse(strategyConfig.service().isGenerateService());
        Assertions.assertFalse(strategyConfig.service().isGenerateServiceImpl());
        Assertions.assertFalse(strategyConfig.mapper().isGenerateMapper());
        Assertions.assertFalse(strategyConfig.mapper().isGenerateMapperXml());
    }

    @Test
    void baseEntity() {
        StrategyConfig strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().superClass(BaseEntity.class);
        Set<String> columns = strategyConfig.entity().getSuperEntityColumns();
        columns.forEach(System.out::println);
        assertThat(columns).containsAll(Arrays.asList("deleted", "createTime", "id"));
        Assertions.assertEquals(columns.size(), 3);
    }

    @Test
    void baseEntityNaming() {
        StrategyConfig strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().superClass(BaseEntity.class).columnNaming(NamingStrategy.underline_to_camel);
        Set<String> columns = strategyConfig.entity().getSuperEntityColumns();
        columns.forEach(System.out::println);
        assertThat(columns).containsAll(Arrays.asList("deleted", "create_time", "id"));
        Assertions.assertEquals(columns.size(), 3);

        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().addSuperEntityColumns("aa", "bb").entityBuilder().superClass(BaseEntity.class).columnNaming(NamingStrategy.underline_to_camel);
        Assertions.assertEquals(strategyConfig.entity().getSuperEntityColumns().size(), 5);
        assertThat(strategyConfig.entity().getSuperEntityColumns()).containsAll(Arrays.asList("aa", "bb", "deleted", "create_time", "id"));

        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().superClass(BaseEntity.class).columnNaming(NamingStrategy.underline_to_camel).addSuperEntityColumns("aa", "bb");
        Assertions.assertEquals(strategyConfig.entity().getSuperEntityColumns().size(), 5);
        assertThat(strategyConfig.entity().getSuperEntityColumns()).containsAll(Arrays.asList("aa", "bb", "deleted", "create_time", "id"));
    }

    @Test
    void superEntity() {
        StrategyConfig strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().superClass(SuperEntity.class);
        Set<String> columns = strategyConfig.entity().getSuperEntityColumns();
        columns.forEach(System.out::println);
        assertThat(columns).containsAll(Arrays.asList("deleted", "id"));
        Assertions.assertEquals(columns.size(), 2);
    }

    @Test
    void testSuperAnnotation() {
        StrategyConfig strategyConfig;

        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().superClass(SuperBean.class).columnNaming(NamingStrategy.no_change);
        assertThat(strategyConfig.entity().getSuperEntityColumns()).containsAll(Arrays.asList("test_id", "aa_name", "ok", "testName"));

        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().superClass(SuperBean.class).columnNaming(NamingStrategy.no_change);
        assertThat(strategyConfig.entity().getSuperEntityColumns()).containsAll(Arrays.asList("test_id", "aa_name", "ok", "testName"));

        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().superClass(SuperBean.class).columnNaming(NamingStrategy.underline_to_camel);
        assertThat(strategyConfig.entity().getSuperEntityColumns()).containsAll(Arrays.asList("test_id", "aa_name", "ok", "test_name"));

        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().superClass(SuperBean.class).columnNaming(NamingStrategy.underline_to_camel);
        assertThat(strategyConfig.entity().getSuperEntityColumns()).containsAll(Arrays.asList("test_id", "aa_name", "ok", "test_name"));

    }

    @Test
    void startsWithTablePrefixTest() {
        StrategyConfig.Builder strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        Assertions.assertFalse(strategyConfigBuilder.build().startsWithTablePrefix("t_name"));
        strategyConfigBuilder.addTablePrefix("a_", "t_");
        Assertions.assertTrue(strategyConfigBuilder.build().startsWithTablePrefix("t_name"));
    }

    @Test
    void addTableFillsTest() {
        Column column = new Column("test", FieldFill.INSERT);
        List<IFill> columnList = new ArrayList<>();
        columnList.add(column);
        StrategyConfig strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().addTableFills(columnList);
        Assertions.assertFalse(strategyConfig.entity().getTableFillList().isEmpty());
        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().addTableFills(columnList);
        Assertions.assertFalse(strategyConfig.entity().getTableFillList().isEmpty());
    }

    @Test
    void entityNameConvertTest() {
        TableInfo tableInfo = new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(),
            TableInfoTest.dataSourceConfig, GeneratorBuilder.strategyConfig(),
            null, null, null), "t_user");

        StrategyConfig.Builder strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        Assertions.assertEquals("TUser", strategyConfigBuilder.build().entity().getNameConvert().entityNameConvert(tableInfo));
        strategyConfigBuilder.addTablePrefix("t_", "a_");
        Assertions.assertEquals("User", strategyConfigBuilder.build().entity().getNameConvert().entityNameConvert(tableInfo));

        strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        strategyConfigBuilder.entityBuilder().naming(NamingStrategy.underline_to_camel);
        Assertions.assertEquals("TUser", strategyConfigBuilder.build().entity().getNameConvert().entityNameConvert(tableInfo));

        strategyConfigBuilder.entityBuilder().naming(NamingStrategy.underline_to_camel);
        strategyConfigBuilder.addTablePrefix("t_", "a_");
        Assertions.assertEquals("User", strategyConfigBuilder.build().entity().getNameConvert().entityNameConvert(tableInfo));

        strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        strategyConfigBuilder.entityBuilder().naming(NamingStrategy.underline_to_camel);
        strategyConfigBuilder.addTableSuffix("_user");
        Assertions.assertEquals("T", strategyConfigBuilder.build().entity().getNameConvert().entityNameConvert(tableInfo));

        strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        strategyConfigBuilder.entityBuilder().nameConvert(new INameConvert() {
            @Override
            public @NotNull String entityNameConvert(@NotNull TableInfo tableInfo) {
                return "aaaa";
            }

            @Override
            public @NotNull String propertyNameConvert(@NotNull TableField field) {
                return "bbbb";
            }
        });
        Assertions.assertEquals("aaaa", strategyConfigBuilder.build().entity().getNameConvert().entityNameConvert(tableInfo));
    }

    @Test
    void propertyNameConvertTest() {
        ConfigBuilder configBuilder;
        StrategyConfig.Builder strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig, strategyConfigBuilder.build(), null, null, null);
        TableField tableField = new TableField(configBuilder,"c_user_name");
        Assertions.assertEquals("cUserName", strategyConfigBuilder.build().entity().getNameConvert().propertyNameConvert(tableField));
        strategyConfigBuilder.addFieldPrefix("t_", "c_");
        Assertions.assertEquals("userName", strategyConfigBuilder.build().entity().getNameConvert().propertyNameConvert(tableField));

        strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        strategyConfigBuilder.entityBuilder().naming(NamingStrategy.underline_to_camel);
        Assertions.assertEquals("cUserName", strategyConfigBuilder.build().entity().getNameConvert().propertyNameConvert(tableField));

        strategyConfigBuilder.entityBuilder().naming(NamingStrategy.underline_to_camel);
        strategyConfigBuilder.addFieldPrefix("t_", "c_");
        Assertions.assertEquals("userName", strategyConfigBuilder.build().entity().getNameConvert().propertyNameConvert(tableField));

        strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        strategyConfigBuilder.entityBuilder().naming(NamingStrategy.underline_to_camel);
        strategyConfigBuilder.addFieldSuffix("_name");
        Assertions.assertEquals("cUser", strategyConfigBuilder.build().entity().getNameConvert().propertyNameConvert(tableField));

        strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        strategyConfigBuilder.entityBuilder().nameConvert(new INameConvert() {
            @Override
            public @NotNull String entityNameConvert(@NotNull TableInfo tableInfo) {
                return "aaaa";
            }

            @Override
            public @NotNull String propertyNameConvert(@NotNull TableField field) {
                return "bbbb";
            }
        });
        Assertions.assertEquals("bbbb", strategyConfigBuilder.build().entity().getNameConvert().propertyNameConvert(tableField));
    }

    @Test
    void matchExcludeTableTest() {
        StrategyConfig.Builder strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        strategyConfigBuilder.addExclude("system", "user_1", "test[a|b]");
        StrategyConfig strategyConfig = strategyConfigBuilder.build();
        Assertions.assertTrue(strategyConfig.matchExcludeTable("system"));
        Assertions.assertFalse(strategyConfig.matchExcludeTable("test_exclude"));
        Assertions.assertTrue(strategyConfig.matchExcludeTable("testa"));
        Assertions.assertTrue(strategyConfig.matchExcludeTable("testb"));
        Assertions.assertFalse(strategyConfig.matchExcludeTable("testc"));
    }

    @Test
    void matchIncludeTableTest() {
        StrategyConfig.Builder strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        strategyConfigBuilder.addInclude("system", "user_1", "test[a|b]");
        StrategyConfig strategyConfig = strategyConfigBuilder.build();
        Assertions.assertTrue(strategyConfig.matchIncludeTable("system"));
        Assertions.assertFalse(strategyConfig.matchIncludeTable("test_exclude"));
        Assertions.assertTrue(strategyConfig.matchIncludeTable("testa"));
        Assertions.assertTrue(strategyConfig.matchIncludeTable("testb"));
        Assertions.assertFalse(strategyConfig.matchIncludeTable("testc"));
    }

    @Test
    void isCapitalModeNamingTest() {
        Assertions.assertFalse(GeneratorBuilder.strategyConfig().isCapitalModeNaming("T_USER"));
        Assertions.assertFalse(GeneratorBuilder.strategyConfigBuilder().enableCapitalMode().build().isCapitalModeNaming("user"));
        Assertions.assertFalse(GeneratorBuilder.strategyConfigBuilder().enableCapitalMode().build().isCapitalModeNaming("user_name"));
        Assertions.assertTrue(GeneratorBuilder.strategyConfigBuilder().enableCapitalMode().build().isCapitalModeNaming("USER_NAME"));
        Assertions.assertTrue(GeneratorBuilder.strategyConfigBuilder().enableCapitalMode().build().isCapitalModeNaming("T_USER"));
        Assertions.assertTrue(GeneratorBuilder.strategyConfigBuilder().enableCapitalMode().build().isCapitalModeNaming("NAME"));
    }

    private void buildAssert(StrategyConfig strategyConfig) {
        Assertions.assertTrue(strategyConfig.isSkipView());
        Assertions.assertTrue(strategyConfig.entity().isChain());
        Assertions.assertTrue(strategyConfig.entity().isLombok());
        Assertions.assertTrue(strategyConfig.entity().isSerialVersionUID());
        Assertions.assertTrue(strategyConfig.entity().isFileOverride());
        Assertions.assertTrue(strategyConfig.controller().isHyphenStyle());
        Assertions.assertTrue(strategyConfig.controller().isRestStyle());
        Assertions.assertFalse(strategyConfig.controller().isFileOverride());
        Assertions.assertEquals("com.baomidou.mp.SuperController", strategyConfig.controllerBuilder().get().getSuperClass());
        Assertions.assertEquals("com.baomidou.mp.SuperMapper", strategyConfig.mapper().getSuperClass());
    }

    @Test
    void builderTest() {
        StrategyConfig strategyConfig;
        strategyConfig = GeneratorBuilder.strategyConfigBuilder().enableCapitalMode().enableSkipView()
            .entityBuilder().enableChainModel().enableLombok().enableFileOverride()
            .controllerBuilder().enableHyphenStyle().enableRestStyle().superClass("com.baomidou.mp.SuperController")
            .mapperBuilder().superClass("com.baomidou.mp.SuperMapper").build();

        buildAssert(strategyConfig);
        strategyConfig = GeneratorBuilder.strategyConfigBuilder().enableSkipView()
            .entityBuilder().enableChainModel().enableLombok().enableFileOverride()
            .controllerBuilder().superClass("com.baomidou.mp.SuperController").enableHyphenStyle().enableRestStyle()
            .mapperBuilder().superClass("com.baomidou.mp.SuperMapper").build();
        buildAssert(strategyConfig);
    }

    @Data
    static class SuperBean {
        @com.baomidou.mybatisplus.annotation.TableId(value = "test_id")
        private String id;

        @com.baomidou.mybatisplus.annotation.TableField(value = "aa_name")
        private String name;

        private String ok;

        private String testName;

    }
}
