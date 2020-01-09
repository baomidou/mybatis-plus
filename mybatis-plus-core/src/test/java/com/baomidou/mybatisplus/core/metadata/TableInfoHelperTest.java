package com.baomidou.mybatisplus.core.metadata;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
        Assertions.assertTrue(TableInfoHelper.isExistTableId(Arrays.asList(ModelOne.class.getDeclaredFields())));
        Assertions.assertFalse(TableInfoHelper.isExistTableId(Arrays.asList(ModelTwo.class.getDeclaredFields())));
    }

    @Test
    void testExcludeProperty(){
        TableInfo tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ModelThree.class);
        Assertions.assertEquals(tableInfo.getFieldList().size(), 2);
        tableInfo.getFieldList().forEach(field -> Assertions.assertNotEquals("test", field.getProperty()));
        tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ModelFour.class);
        Assertions.assertEquals(tableInfo.getFieldList().size(), 2);
        tableInfo.getFieldList().forEach(field -> Assertions.assertNotEquals("test", field.getProperty()));
    }
}
