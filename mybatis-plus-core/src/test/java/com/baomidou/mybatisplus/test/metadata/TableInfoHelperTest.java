package com.baomidou.mybatisplus.test.metadata;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.ResultMap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class TableInfoHelperTest {

    @Data
    private static class BaseModel {

        private Long id;

        private String test;
    }


    @Data
    private static class ModelOne {

        @TableId
        private Long id;

        private String name;
    }

    @Data
    private static class ModelTwo {

        private Long id;

        private String name;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @TableName(excludeProperty = "test")
    private static class ModelThree extends BaseModel {

        private String sex;

        private String name;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @TableName(excludeProperty = {"test", "id"})
    private static class ModelFour extends BaseModel {

        private String sex;

        private String name;
    }

    @Test
    void testExcludeProperty() {
        TableInfo tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ModelThree.class);
        assertThat(tableInfo.havePK()).isTrue();
        assertThat(tableInfo.getKeyProperty()).isEqualTo("id");
        assertThat(tableInfo.getFieldList().size()).isEqualTo(2);
        assertThat(tableInfo.getFieldList()).noneMatch(i -> i.getProperty().equals("test"));

        tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ModelFour.class);
        assertThat(tableInfo.getFieldList().size()).isEqualTo(2);
        assertThat(tableInfo.getFieldList()).noneMatch(i -> i.getProperty().equals("test"));

        tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ModelFour2.class);
        assertThat(tableInfo.getFieldList().size()).isEqualTo(1);
        assertThat(tableInfo.getFieldList()).anyMatch(i -> i.getProperty().equals("name"));
        assertThat(tableInfo.havePK()).isTrue();
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @TableField(exist = false)
    @interface NotExistsField {

    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @TableId
    @interface MyId {

    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @TableName(schema = "test")
    @interface MyTable {

    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @TableLogic(value = "false", delval = "true")
    @interface MyTableLogic {

    }


    @Data
    @MyTable
    private static class CustomAnnotation {

        @MyId
        private Long id;

        private String name;

        @NotExistsField
        private String test;

        @MyTableLogic
        private Boolean del;
    }

    @Test
    void testCustomAnnotation() {
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        TableInfo tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(mybatisConfiguration, ""), CustomAnnotation.class);
        List<Field> fieldList = TableInfoHelper.getAllFields(CustomAnnotation.class);
        Assertions.assertThat(fieldList.size()).isEqualTo(3);
        Assertions.assertThat(TableInfoHelper.isExistTableId(CustomAnnotation.class, Arrays.asList(CustomAnnotation.class.getDeclaredFields()))).isTrue();
        Assertions.assertThat(TableInfoHelper.isExistTableLogic(CustomAnnotation.class, Arrays.asList(CustomAnnotation.class.getDeclaredFields()))).isTrue();
        TableFieldInfo logicDeleteFieldInfo = TableInfoHelper.getTableInfo(CustomAnnotation.class).getLogicDeleteFieldInfo();
        Assertions.assertThat(logicDeleteFieldInfo).isNotNull();
        Assertions.assertThat(logicDeleteFieldInfo.getLogicDeleteValue()).isNotNull().isEqualTo("true");
        Assertions.assertThat(logicDeleteFieldInfo.getLogicNotDeleteValue()).isNotNull().isEqualTo("false");
        Assertions.assertThat(tableInfo.getTableName()).isEqualTo("test.custom_annotation");
    }


    @Test
    void testIsExistTableId() {
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(mybatisConfiguration, ""), ModelOne.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(mybatisConfiguration, ""), ModelTwo.class);
        Assertions.assertThat(TableInfoHelper.isExistTableId(ModelOne.class, Arrays.asList(ModelOne.class.getDeclaredFields()))).isTrue();
        assertThat(TableInfoHelper.isExistTableId(ModelTwo.class, Arrays.asList(ModelTwo.class.getDeclaredFields()))).isFalse();
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @TableName(properties = {"id", "name"}, excludeProperty = {"sex", "test"})
    private static class ModelFour2 extends BaseModel {

        private String sex;

        private String name;
    }

    @Test
    void testMoreTableId() {
        Exception ex = null;
        try {
            TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ModelFive.class);
        } catch (Exception e) {
            ex = e;
        }
        assertThat(ex).isNotNull();
        assertThat(ex).isInstanceOf(MybatisPlusException.class);
        System.out.println(ex.getMessage());
    }

    @Test
    void testPriorityTableId() {
        TableInfo tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ModelSex.class);
        assertThat(tableInfo.havePK()).isTrue();
        assertThat(tableInfo.getKeyProperty()).isEqualTo("realId");
    }

    @Test
    void testVersion() {
        Exception ex = null;
        try {
            TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Versions.class);
        } catch (Exception e) {
            ex = e;
        }
        assertThat(ex).isNotNull();
        assertThat(ex).isInstanceOf(MybatisPlusException.class);
        System.out.println(ex.getMessage());
    }

    @Test
    void testLogic() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        GlobalConfig config = GlobalConfigUtils.defaults();
        config.getDbConfig().setLogicDeleteField("logic");
        GlobalConfigUtils.setGlobalConfig(configuration, config);
        TableInfo tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, ""), Logic.class);
        assertThat(tableInfo.isWithLogicDelete()).isTrue();
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        List<TableFieldInfo> logic = fieldList.stream().filter(TableFieldInfo::isLogicDelete).collect(Collectors.toList());
        assertThat(logic.size()).isEqualTo(1);
        assertThat(logic.get(0).getProperty()).isEqualTo("deleted");
    }

    @Test
    void testColumnFormat() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        GlobalConfig config = GlobalConfigUtils.defaults();
        GlobalConfig.DbConfig dbConfig = config.getDbConfig();
        dbConfig.setColumnFormat("pxx_%s");
        dbConfig.setTableFormat("mp_%s");
        GlobalConfigUtils.setGlobalConfig(configuration, config);
        TableInfo tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, ""), Logic.class);
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        fieldList.forEach(i -> {
            assertThat(i.getColumn()).startsWith("pxx_");
        });
        assertThat(tableInfo.getTableName()).startsWith("mp_");
    }

    @Data
    private static class ModelFive {

        @TableId
        private String id1;

        @TableId
        private String id2;
    }

    @Data
    private static class ModelSex {

        @TableId
        private String realId;
    }

    @Data
    private static class Logic {

        private Integer logic;

        @TableLogic
        private Integer deleted;
    }

    @Data
    private static class Versions {

        @Version
        private Integer version1;
        @Version
        private Integer version2;
    }

    @Test
    void testTableNamePrefix() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        GlobalConfig config = GlobalConfigUtils.defaults();
        config.getDbConfig().setTablePrefix("ttt_");
        GlobalConfigUtils.setGlobalConfig(configuration, config);
        TableInfo tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, ""), Table.class);
        assertThat(tableInfo.getTableName()).isEqualTo("xxx");
    }

    @Data
    @TableName("xxx")
    private static class Table {

    }

    @Test
    void testTableNamePrefix2() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        GlobalConfig config = GlobalConfigUtils.defaults();
        config.getDbConfig().setTablePrefix("ttt_");
        GlobalConfigUtils.setGlobalConfig(configuration, config);
        TableInfo tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, ""), Table2.class);
        assertThat(tableInfo.getTableName()).isEqualTo("ttt_xxx");
    }

    @Test
    void getTableInfoInterface() {
        TableInfoHelper.getTableInfo(Mapper.class);
    }

    @Data
    @TableName(value = "xxx", keepGlobalPrefix = true)
    private static class Table2 {

    }


    @Test
    void testTableAutoResultMap() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        TableInfo tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, ""), AutoResultMapTable.class);
        final ResultMap resultMap = tableInfo.getConfiguration().getResultMap(tableInfo.getResultMap());

        assertThat(resultMap)
            .isNotNull()
            .extracting(ResultMap::getMappedColumns)
            .matches((set) -> set.stream().noneMatch(StringUtils::isNotColumnName));
    }

    @Data
    @TableName(value = "xxx", autoResultMap = true)
    private static class AutoResultMapTable {

        @TableId("`id`")
        private Long id;

        @TableField("`name`")
        private Long name;
    }

    @Test
    void testNewInstance() {
        TableInfo tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ModelOne.class);
        ModelOne entity = tableInfo.newInstance();
        tableInfo.setPropertyValue(entity, tableInfo.getKeyColumn(), 1L);
        assertThat(entity.id).isNotNull().isEqualTo(1L);
    }

}
