package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.IFill;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.po.TableInfoTest;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.entity.BaseEntity;
import com.baomidou.mybatisplus.generator.entity.SuperEntity;
import com.baomidou.mybatisplus.generator.fill.Column;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
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
        assertThat(strategyConfig.controller().isGenerate()).isTrue();
        assertThat(strategyConfig.entity().isGenerate()).isTrue();
        assertThat(strategyConfig.service().isGenerateService()).isTrue();
        assertThat(strategyConfig.service().isGenerateServiceImpl()).isTrue();
        assertThat(strategyConfig.mapper().isGenerateMapper()).isTrue();
        assertThat(strategyConfig.mapper().isGenerateMapperXml()).isTrue();
        strategyConfig = new StrategyConfig.Builder().entityBuilder().javaTemplate("/templates/entity.java").disable()
                .serviceBuilder().disable().disableService().serviceTemplate("/templates/service.java")
                .serviceImplTemplate("/templates/serviceImpl.java").mapperBuilder().disableMapper().disableMapperXml()
                .controllerBuilder().disable().template("").build();
        assertThat(strategyConfig.controller().isGenerate()).isFalse();
        assertThat(strategyConfig.entity().isGenerate()).isFalse();
        assertThat(strategyConfig.service().isGenerateService()).isFalse();
        assertThat(strategyConfig.service().isGenerateServiceImpl()).isFalse();
        assertThat(strategyConfig.mapper().isGenerateMapper()).isFalse();
        assertThat(strategyConfig.mapper().isGenerateMapperXml()).isFalse();
    }

    @Test
    void baseEntity() {
        StrategyConfig strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().superClass(BaseEntity.class);
        Set<String> columns = strategyConfig.entity().getSuperEntityColumns();
        columns.forEach(System.out::println);
        assertThat(columns).containsAll(Arrays.asList("deleted", "createTime", "id"));
        assertThat(columns).hasSize(3);
    }

    @Test
    void baseEntityNaming() {
        StrategyConfig strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().superClass(BaseEntity.class).columnNaming(NamingStrategy.underline_to_camel);
        Set<String> columns = strategyConfig.entity().getSuperEntityColumns();
        columns.forEach(System.out::println);
        assertThat(columns).containsAll(Arrays.asList("deleted", "create_time", "id"));
        assertThat(columns).hasSize(3);

        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().addSuperEntityColumns("aa", "bb").entityBuilder().superClass(BaseEntity.class)
                .columnNaming(NamingStrategy.underline_to_camel);
        assertThat(strategyConfig.entity().getSuperEntityColumns()).hasSize(5);
        assertThat(strategyConfig.entity().getSuperEntityColumns())
                .containsAll(Arrays.asList("aa", "bb", "deleted", "create_time", "id"));

        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().superClass(BaseEntity.class).columnNaming(NamingStrategy.underline_to_camel)
                .addSuperEntityColumns("aa", "bb");
        assertThat(strategyConfig.entity().getSuperEntityColumns()).hasSize(5);
        assertThat(strategyConfig.entity().getSuperEntityColumns())
                .containsAll(Arrays.asList("aa", "bb", "deleted", "create_time", "id"));
    }

    @Test
    void superEntity() {
        StrategyConfig strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().superClass(SuperEntity.class);
        Set<String> columns = strategyConfig.entity().getSuperEntityColumns();
        columns.forEach(System.out::println);
        assertThat(columns).containsAll(Arrays.asList("deleted", "id"));
        assertThat(columns).hasSize(2);
    }

    @Test
    void testSuperAnnotation() {
        StrategyConfig strategyConfig;

        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().superClass(SuperBean.class).columnNaming(NamingStrategy.no_change);
        assertThat(strategyConfig.entity().getSuperEntityColumns())
                .containsAll(Arrays.asList("test_id", "aa_name", "ok", "testName"));

        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().superClass(SuperBean.class).columnNaming(NamingStrategy.no_change);
        assertThat(strategyConfig.entity().getSuperEntityColumns())
                .containsAll(Arrays.asList("test_id", "aa_name", "ok", "testName"));

        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().superClass(SuperBean.class).columnNaming(NamingStrategy.underline_to_camel);
        assertThat(strategyConfig.entity().getSuperEntityColumns())
                .containsAll(Arrays.asList("test_id", "aa_name", "ok", "test_name"));

        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().superClass(SuperBean.class).columnNaming(NamingStrategy.underline_to_camel);
        assertThat(strategyConfig.entity().getSuperEntityColumns())
                .containsAll(Arrays.asList("test_id", "aa_name", "ok", "test_name"));

    }

    @Test
    void startsWithTablePrefixTest() {
        StrategyConfig.Builder strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        assertThat(strategyConfigBuilder.build().startsWithTablePrefix("t_name")).isFalse();
        strategyConfigBuilder.addTablePrefix("a_", "t_");
        assertThat(strategyConfigBuilder.build().startsWithTablePrefix("t_name")).isTrue();
    }

    @Test
    void addTableFillsTest() {
        Column column = new Column("test", FieldFill.INSERT);
        List<IFill> columnList = new ArrayList<>();
        columnList.add(column);
        StrategyConfig strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().addTableFills(columnList);
        assertThat(strategyConfig.entity().getTableFillList()).isNotEmpty();
        strategyConfig = GeneratorBuilder.strategyConfig();
        strategyConfig.entityBuilder().addTableFills(columnList);
        assertThat(strategyConfig.entity().getTableFillList()).isNotEmpty();
    }

    @Test
    void entityNameConvertTest() {
        TableInfo tableInfo = new TableInfo(new ConfigBuilder(GeneratorBuilder.packageConfig(),
                TableInfoTest.dataSourceConfig, GeneratorBuilder.strategyConfig(), null, null, null), "t_user");

        StrategyConfig.Builder strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        assertThat(strategyConfigBuilder.build().entity().getNameConvert().entityNameConvert(tableInfo))
                .isEqualTo("TUser");
        strategyConfigBuilder.addTablePrefix("t_", "a_");
        assertThat(strategyConfigBuilder.build().entity().getNameConvert().entityNameConvert(tableInfo))
                .isEqualTo("User");

        strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        strategyConfigBuilder.entityBuilder().naming(NamingStrategy.underline_to_camel);
        assertThat(strategyConfigBuilder.build().entity().getNameConvert().entityNameConvert(tableInfo))
                .isEqualTo("TUser");

        strategyConfigBuilder.entityBuilder().naming(NamingStrategy.underline_to_camel);
        strategyConfigBuilder.addTablePrefix("t_", "a_");
        assertThat(strategyConfigBuilder.build().entity().getNameConvert().entityNameConvert(tableInfo))
                .isEqualTo("User");

        strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        strategyConfigBuilder.entityBuilder().naming(NamingStrategy.underline_to_camel);
        strategyConfigBuilder.addTableSuffix("_user");
        assertThat(strategyConfigBuilder.build().entity().getNameConvert().entityNameConvert(tableInfo)).isEqualTo("T");

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
        assertThat(strategyConfigBuilder.build().entity().getNameConvert().entityNameConvert(tableInfo))
                .isEqualTo("aaaa");
    }

    @Test
    void propertyNameConvertTest() {
        ConfigBuilder configBuilder;
        StrategyConfig.Builder strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        configBuilder = new ConfigBuilder(GeneratorBuilder.packageConfig(), TableInfoTest.dataSourceConfig,
                strategyConfigBuilder.build(), null, null, null);
        TableField tableField = new TableField(configBuilder, "c_user_name");
        assertThat(strategyConfigBuilder.build().entity().getNameConvert().propertyNameConvert(tableField))
                .isEqualTo("cUserName");
        strategyConfigBuilder.addFieldPrefix("t_", "c_");
        assertThat(strategyConfigBuilder.build().entity().getNameConvert().propertyNameConvert(tableField))
                .isEqualTo("userName");

        strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        strategyConfigBuilder.entityBuilder().naming(NamingStrategy.underline_to_camel);
        assertThat(strategyConfigBuilder.build().entity().getNameConvert().propertyNameConvert(tableField))
                .isEqualTo("cUserName");

        strategyConfigBuilder.entityBuilder().naming(NamingStrategy.underline_to_camel);
        strategyConfigBuilder.addFieldPrefix("t_", "c_");
        assertThat(strategyConfigBuilder.build().entity().getNameConvert().propertyNameConvert(tableField))
                .isEqualTo("userName");

        strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        strategyConfigBuilder.entityBuilder().naming(NamingStrategy.underline_to_camel);
        strategyConfigBuilder.addFieldSuffix("_name");
        assertThat(strategyConfigBuilder.build().entity().getNameConvert().propertyNameConvert(tableField))
                .isEqualTo("cUser");

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
        assertThat(strategyConfigBuilder.build().entity().getNameConvert().propertyNameConvert(tableField))
                .isEqualTo("bbbb");
    }

    @Test
    void matchExcludeTableTest() {
        StrategyConfig.Builder strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        strategyConfigBuilder.addExclude("system", "user_1", "test[a|b]");
        StrategyConfig strategyConfig = strategyConfigBuilder.build();
        assertThat(strategyConfig.matchExcludeTable("system")).isTrue();
        assertThat(strategyConfig.matchExcludeTable("test_exclude")).isFalse();
        assertThat(strategyConfig.matchExcludeTable("testa")).isTrue();
        assertThat(strategyConfig.matchExcludeTable("testb")).isTrue();
        assertThat(strategyConfig.matchExcludeTable("testc")).isFalse();
    }

    @Test
    void matchIncludeTableTest() {
        StrategyConfig.Builder strategyConfigBuilder = GeneratorBuilder.strategyConfigBuilder();
        strategyConfigBuilder.addInclude("system", "user_1", "test[a|b]");
        StrategyConfig strategyConfig = strategyConfigBuilder.build();
        assertThat(strategyConfig.matchIncludeTable("system")).isTrue();
        assertThat(strategyConfig.matchIncludeTable("test_exclude")).isFalse();
        assertThat(strategyConfig.matchIncludeTable("testa")).isTrue();
        assertThat(strategyConfig.matchIncludeTable("testb")).isTrue();
        assertThat(strategyConfig.matchIncludeTable("testc")).isFalse();
    }

    @Test
    void isCapitalModeNamingTest() {
        assertThat(GeneratorBuilder.strategyConfig().isCapitalModeNaming("T_USER")).isFalse();
        assertThat(GeneratorBuilder.strategyConfigBuilder().enableCapitalMode().build().isCapitalModeNaming("user"))
                .isFalse();
        assertThat(
                GeneratorBuilder.strategyConfigBuilder().enableCapitalMode().build().isCapitalModeNaming("user_name"))
                .isFalse();
        assertThat(
                GeneratorBuilder.strategyConfigBuilder().enableCapitalMode().build().isCapitalModeNaming("USER_NAME"))
                .isTrue();
        assertThat(GeneratorBuilder.strategyConfigBuilder().enableCapitalMode().build().isCapitalModeNaming("T_USER"))
                .isTrue();
        assertThat(GeneratorBuilder.strategyConfigBuilder().enableCapitalMode().build().isCapitalModeNaming("NAME"))
                .isTrue();
    }

    private void buildAssert(StrategyConfig strategyConfig) {
        assertThat(strategyConfig.isSkipView()).isTrue();
        assertThat(strategyConfig.entity().isChain()).isTrue();
        assertThat(strategyConfig.entity().isLombok()).isTrue();
        assertThat(strategyConfig.entity().isSerialVersionUID()).isTrue();
        assertThat(strategyConfig.entity().isFileOverride()).isTrue();
        assertThat(strategyConfig.controller().isHyphenStyle()).isTrue();
        assertThat(strategyConfig.controller().isRestStyle()).isTrue();
        assertThat(strategyConfig.controller().isFileOverride()).isFalse();
        assertThat(strategyConfig.controllerBuilder().get().getSuperClass())
                .isEqualTo("com.baomidou.mp.SuperController");
        assertThat(strategyConfig.mapper().getSuperClass()).isEqualTo("com.baomidou.mp.SuperMapper");
    }

    @Test
    void builderTest() {
        StrategyConfig strategyConfig;
        strategyConfig = GeneratorBuilder.strategyConfigBuilder().enableCapitalMode().enableSkipView().entityBuilder()
                .enableChainModel().enableLombok().enableFileOverride().controllerBuilder().enableHyphenStyle()
                .enableRestStyle().superClass("com.baomidou.mp.SuperController").mapperBuilder()
                .superClass("com.baomidou.mp.SuperMapper").build();

        buildAssert(strategyConfig);
        strategyConfig = GeneratorBuilder.strategyConfigBuilder().enableSkipView().entityBuilder().enableChainModel()
                .enableLombok().enableFileOverride().controllerBuilder().superClass("com.baomidou.mp.SuperController")
                .enableHyphenStyle().enableRestStyle().mapperBuilder().superClass("com.baomidou.mp.SuperMapper")
                .build();
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
