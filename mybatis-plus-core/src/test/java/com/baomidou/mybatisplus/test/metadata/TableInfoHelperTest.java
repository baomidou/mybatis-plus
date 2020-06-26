package com.baomidou.mybatisplus.test.metadata;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
    void testIsExistTableId() {
        Assertions.assertThat(TableInfoHelper.isExistTableId(Arrays.asList(ModelOne.class.getDeclaredFields()))).isTrue();
        assertThat(TableInfoHelper.isExistTableId(Arrays.asList(ModelTwo.class.getDeclaredFields()))).isFalse();
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
}
