package com.baomidou.mybatisplus.annotation;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 逻辑删除开关的两个取值决定 SQL 里 {@code deleted = ?} 写什么，而 {@code value} 是“未删除”、<br>
 * {@code delval} 才是“已删除”，方向容易记反；core 用 isNotBlank 判空，所以空白串等于没写。<br>
 * 本类只钉注解反射读回来的是什么，兜底到全局配置的推导由 core 的 TableInfoHelperTest 钉。
 *
 * @author taoyu
 */
@Tag("unit")
class TableLogicTest {

    @Test
    void bareTableLogicDefersToTheGlobalFlags() {
        TableLogic logic = annotated("followGlobal");
        assertThat(logic.value()).as("不写 value 就跟着全局 logicNotDeleteValue（默认 0）").isEmpty();
        assertThat(logic.delval()).as("不写 delval 就跟着全局 logicDeleteValue（默认 1）").isEmpty();
    }

    @Test
    void valueIsTheAliveFlagAndDelvalIsTheDeletedOne() {
        TableLogic logic = annotated("zeroOne");
        assertThat(logic.value()).as("value 对应未删除值，写死后就不再随全局配置变").isEqualTo("0");
        assertThat(logic.delval()).as("delval 才是更新进库的已删除值").isEqualTo("1");
    }

    @Test
    void argumentsAreKeptAsRawStrings() {
        assertThat(annotated("textFlag").value()).as("不做布尔转换，字符串原样拼进 SQL").isEqualTo("false");
        assertThat(annotated("textFlag").delval()).isEqualTo("true");
        assertThat(annotated("blank").value()).as("注解层不 trim；空格留到 core 的 isNotBlank 判断那里等于没写").isEqualTo(" ");
        assertThat(annotated("blank").delval()).isEqualTo("Y");
    }

    @Test
    void annotationIsReadableAtRuntimeOnFieldsAndComposedAnnotations() {
        assertThat(TableLogic.class.getAnnotation(Retention.class).value())
                .as("字段反射在 TableInfoHelper 初始化时发生，Retention 降级会让逻辑删除整体失效").isSameAs(RetentionPolicy.RUNTIME);
        assertThat(TableLogic.class.getAnnotation(Target.class).value())
                .containsExactlyInAnyOrder(ElementType.FIELD, ElementType.ANNOTATION_TYPE);
    }

    private static TableLogic annotated(String name) {
        try {
            return Probe.class.getDeclaredField(name).getAnnotation(TableLogic.class);
        } catch (NoSuchFieldException e) {
            throw new AssertionError("夹具里缺字段 " + name, e);
        }
    }

    static class Probe {

        @TableLogic
        private Integer followGlobal;

        @TableLogic(value = "0", delval = "1")
        private Integer zeroOne;

        @TableLogic(value = "false", delval = "true")
        private Boolean textFlag;

        @TableLogic(value = " ", delval = "Y")
        private String blank;

    }

}
