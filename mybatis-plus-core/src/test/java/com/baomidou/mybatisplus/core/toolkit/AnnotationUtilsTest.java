package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong
 */
@Tag("unit")
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
    @TableField
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
    void shouldReturnNullWhenAnnotationNotPresentOnField() throws Exception {
        TableField tableField = AnnotationUtils
                .findFirstAnnotation(TableField.class, Demo.class.getDeclaredField("id"));
        assertThat(tableField).isNull();
    }

    @Test
    void shouldFindDirectTableFieldAnnotation() throws Exception {
        TableField tableField = AnnotationUtils
                .findFirstAnnotation(TableField.class, Demo.class.getDeclaredField("name"));
        assertThat(tableField).isNotNull();
        assertThat(tableField.exist()).isTrue();
    }

    @Test
    void shouldFindTableFieldViaMetaAnnotationWithExistFalse() throws Exception {
        TableField tableField = AnnotationUtils
                .findFirstAnnotation(TableField.class, Demo.class.getDeclaredField("name1"));
        assertThat(tableField).isNotNull();
        assertThat(tableField.exist()).isFalse();
    }

    @Test
    void shouldFindTableFieldViaTableField3MetaAnnotation() throws Exception {
        TableField tableField = AnnotationUtils
                .findFirstAnnotation(TableField.class, Demo.class.getDeclaredField("name2"));
        assertThat(tableField).isNotNull();
        assertThat(tableField.exist()).isTrue();
    }

    @Test
    void shouldFindTableFieldViaDeepMetaAnnotationChain() throws Exception {
        TableField tableField = AnnotationUtils
                .findFirstAnnotation(TableField.class, Demo.class.getDeclaredField("name3"));
        assertThat(tableField).isNotNull();
        assertThat(tableField.exist()).isFalse();
    }

    @Test
    void shouldFindFirstMetaAnnotationWhenFieldHasMultipleSources() throws Exception {
        TableField tableField = AnnotationUtils
                .findFirstAnnotation(TableField.class, Demo.class.getDeclaredField("name4"));
        assertThat(tableField).isNotNull();
        assertThat(tableField.exist()).isFalse();
    }

    @Test
    void shouldResolveAnnotationWithReversedMetaAnnotationOrder() throws Exception {
        TableField tableField = AnnotationUtils
                .findFirstAnnotation(TableField.class, Demo.class.getDeclaredField("name5"));
        assertThat(tableField).isNotNull();
        assertThat(tableField.exist()).isTrue();
    }

    @Test
    void shouldFindBothTableFieldAndTableLogicOnSameField() throws Exception {
        TableField tableField = AnnotationUtils
                .findFirstAnnotation(TableField.class, Demo.class.getDeclaredField("deleted"));
        assertThat(tableField).isNotNull();
        assertThat(tableField.exist()).isTrue();
        TableLogic tableLogic = AnnotationUtils
                .findFirstAnnotation(TableLogic.class, Demo.class.getDeclaredField("deleted"));
        assertThat(tableLogic).isNotNull();
    }

    @Test
    void shouldFindMetaTableNameAnnotationOnClass() {
        TableName tableName = AnnotationUtils.findFirstAnnotation(TableName.class, Demo.class);
        assertThat(tableName).isNotNull();
        assertThat(tableName.schema()).isEqualTo("test");
    }

}
