package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.AnnotationUtils;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author nieqiurong
 */
public class AnnotationUtilsTest {

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @TableField(exist = false)
    @interface TableField2 {

    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @TableField(exist = true)
    @interface TableField3 {

    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @TableField2
    @interface TableField4 {

    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @TableName(schema = "test")
    @interface MyTable {

    }


    @Data
    @MyTable
    public static class Demo {

        private String id;

        @TableField
        private String name;

        @TableField2
        private String name1;

        @TableField3
        private String name2;

        @TableField4
        private String name3;

        @TableField4
        @TableField3
        private String name4;

        @TableField3
        @TableField4
        private String name5;

        @TableField(fill = FieldFill.UPDATE)
        @TableLogic(delval = "true", value = "false")
        private Boolean deleted;

    }

    @Test
    void test() throws Exception {
        TableField tableField = AnnotationUtils.findFirstAnnotation(TableField.class, Demo.class.getDeclaredField("id"));
        Assertions.assertNull(tableField);
    }

    @Test
    void test1() throws Exception {
        TableField tableField = AnnotationUtils.findFirstAnnotation(TableField.class, Demo.class.getDeclaredField("name"));
        Assertions.assertNotNull(tableField);
        Assertions.assertTrue(tableField.exist());
    }

    @Test
    void test2() throws Exception {
        TableField tableField = AnnotationUtils.findFirstAnnotation(TableField.class, Demo.class.getDeclaredField("name1"));
        Assertions.assertNotNull(tableField);
        Assertions.assertFalse(tableField.exist());
    }

    @Test
    void test3() throws Exception {
        TableField tableField = AnnotationUtils.findFirstAnnotation(TableField.class, Demo.class.getDeclaredField("name2"));
        Assertions.assertNotNull(tableField);
        Assertions.assertTrue(tableField.exist());
    }

    @Test
    void test4() throws Exception {
        TableField tableField = AnnotationUtils.findFirstAnnotation(TableField.class, Demo.class.getDeclaredField("name3"));
        Assertions.assertNotNull(tableField);
        Assertions.assertFalse(tableField.exist());
    }

    @Test
    void test5() throws Exception {
        TableField tableField = AnnotationUtils.findFirstAnnotation(TableField.class, Demo.class.getDeclaredField("name4"));
        Assertions.assertNotNull(tableField);
        Assertions.assertFalse(tableField.exist());
    }

    @Test
    void test6() throws Exception {
        TableField tableField = AnnotationUtils.findFirstAnnotation(TableField.class, Demo.class.getDeclaredField("name5"));
        Assertions.assertNotNull(tableField);
        Assertions.assertTrue(tableField.exist());
    }

    @Test
    void test7() throws Exception {
        TableField tableField = AnnotationUtils.findFirstAnnotation(TableField.class, Demo.class.getDeclaredField("deleted"));
        Assertions.assertNotNull(tableField);
        Assertions.assertTrue(tableField.exist());
        TableLogic tableLogic = AnnotationUtils.findFirstAnnotation(TableLogic.class, Demo.class.getDeclaredField("deleted"));
        Assertions.assertNotNull(tableLogic);
    }

    @Test
    void test8() {
        TableName tableName = AnnotationUtils.findFirstAnnotation(TableName.class, Demo.class);
        Assertions.assertNotNull(tableName);
        Assertions.assertEquals(tableName.schema(),"test");
    }

}
