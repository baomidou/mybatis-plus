package com.baomidou.mybatisplus.annotation;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.UnknownTypeHandler;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 实体上只写一个不带参数的 {@code @TableField}，core 反射读到的就是下面这些默认值。<br>
 * 默认值一变，插入、更新、查询拼出来的 SQL 整体跟着变，而且不会有任何报错提示，所以逐个钉死。<br>
 * 类名带 Defaults 是为了不和 generator 里同名的 TableFieldTest（测的是另一个类）撞车。
 *
 * @author taoyu
 */
@Tag("unit")
class TableFieldDefaultsTest {

    @Test
    void bareAnnotationLeavesNamingToCamelCaseAndKeepsFieldInSql() {
        TableField annotation = annotated("plain");
        assertThat(annotation.value()).as("不写 value 就靠驼峰转下划线推列名").isEmpty();
        assertThat(annotation.property()).as("不写 property 就不做属性别名").isEmpty();
        assertThat(annotation.exist()).as("标了 @TableField 不等于排除字段，默认照样进 SQL").isTrue();
        assertThat(annotation.select()).as("默认进 select 列表；改成 false 字段就查不回来了").isTrue();
        assertThat(annotation.update()).as("不写 update 走通用 SET，写了则整段替换").isEmpty();
        assertThat(annotation.condition()).as("不写 condition 由 core 兜底成等值比较").isEmpty();
    }

    @Test
    void everyStrategyDefersToGlobalConfig() {
        TableField annotation = annotated("plain");
        assertThat(annotation.insertStrategy()).isSameAs(FieldStrategy.DEFAULT);
        assertThat(annotation.updateStrategy()).isSameAs(FieldStrategy.DEFAULT);
        assertThat(annotation.whereStrategy()).isSameAs(FieldStrategy.DEFAULT);
        assertThat(annotation.fill()).as("不标 fill 就没有填充钩子").isSameAs(FieldFill.DEFAULT);
    }

    @Test
    void typeHandlingStaysUntouchedByDefault() {
        TableField annotation = annotated("plain");
        assertThat(annotation.jdbcType()).as("不写 jdbcType 就不能靠它兜住 null").isSameAs(JdbcType.UNDEFINED);
        assertThat(annotation.typeHandler()).isSameAs(UnknownTypeHandler.class);
        assertThat(annotation.javaType()).isFalse();
        assertThat(annotation.numericScale()).as("不写精度就不给数值列补 scale").isEmpty();
        assertThat(annotation.keepGlobalFormat()).as("全局 format 默认不作用到本字段").isFalse();
    }

    @Test
    void annotationIsReadableAtRuntimeOnFieldsAndComposedAnnotations() {
        assertThat(TableField.class.getAnnotation(Retention.class).value())
                .as("TableInfoHelper 靠运行时反射读它，Retention 降级会让注解静默失效").isSameAs(RetentionPolicy.RUNTIME);
        assertThat(TableField.class.getAnnotation(Target.class).value())
                .containsExactlyInAnyOrder(ElementType.FIELD, ElementType.ANNOTATION_TYPE);
    }

    private static TableField annotated(String name) {
        Field field;
        try {
            field = Probe.class.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new AssertionError("夹具里缺字段 " + name, e);
        }
        return field.getAnnotation(TableField.class);
    }

    static class Probe {

        @TableField
        private String plain;

    }

}
