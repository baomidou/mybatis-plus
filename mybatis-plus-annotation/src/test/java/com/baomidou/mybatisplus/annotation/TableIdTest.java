package com.baomidou.mybatisplus.annotation;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 主键注解的默认值决定“ID 由谁生成”，写错不会报错只会静默换策略，所以钉住默认取值。
 *
 * @author taoyu
 */
@Tag("unit")
class TableIdTest {

    @Test
    void bareTableIdDefersToTheGlobalIdType() {
        TableId annotation = annotated("plain");
        assertThat(annotation.type()).as("注解上不写 type 是 NONE，用什么策略交给全局配置；直接改成 AUTO 会抢库里的自增 ID").isSameAs(IdType.NONE);
        assertThat(annotation.value()).as("不写 value 就靠驼峰转下划线推主键列名").isEmpty();
    }

    @Test
    void explicitTypeIsKeptAsWritten() {
        assertThat(annotated("input").type()).as("字段上显式声明的策略不该被默认值盖掉").isSameAs(IdType.INPUT);
    }

    @Test
    void annotationIsReadableAtRuntimeOnFieldsAndComposedAnnotations() {
        assertThat(TableId.class.getAnnotation(Retention.class).value()).isSameAs(RetentionPolicy.RUNTIME);
        assertThat(TableId.class.getAnnotation(Target.class).value())
                .containsExactlyInAnyOrder(ElementType.FIELD, ElementType.ANNOTATION_TYPE);
    }

    private static TableId annotated(String name) {
        try {
            return Probe.class.getDeclaredField(name).getAnnotation(TableId.class);
        } catch (NoSuchFieldException e) {
            throw new AssertionError("夹具里缺字段 " + name, e);
        }
    }

    static class Probe {

        @TableId
        private Long plain;

        @TableId(type = IdType.INPUT)
        private Long input;

    }

}
