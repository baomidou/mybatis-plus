package com.baomidou.mybatisplus.annotation;

import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link OrderBy} 的默认值合起来是“倒序、排在最后”，两个都不是随便取的：<br>
 * asc 为 false 时 core 生成 DESC，sort 取 Short.MAX_VALUE 才保证不带 sort 的字段不会插到有编号的字段前面。
 *
 * @author taoyu
 */
class OrderByTest {

    @Test
    void bareOrderByIsDescendingAndLast() {
        OrderBy orderBy = annotated("bare");
        assertThat(orderBy.asc()).as("默认倒序；core 把 false 翻成 DESC").isFalse();
        assertThat(orderBy.sort()).as("默认最大短整型，等于“没有编号的字段排最后”").isEqualTo(Short.MAX_VALUE);
    }

    @Test
    void explicitValuesAreKeptAsWritten() {
        assertThat(annotated("ascending").asc()).as("true 才是正序 ASC").isTrue();
        assertThat(annotated("ascending").sort()).as("sort 原样保存，不做正数校验").isEqualTo((short) 1);
        assertThat(annotated("negativeSort").sort()).as("负数也能写，比 0 更靠前").isEqualTo((short) -2);
    }

    @Test
    void unnumberedFieldsTieAndSortAfterNumberedOnes() {
        assertThat(annotated("first").sort()).as("都不写 sort 时取值相同，先后交给字段声明顺序")
                .isEqualTo(annotated("alsoUnnumbered").sort());
        assertThat(annotated("numbered").sort()).as("只有写了 sort 的字段才会被挪到默认值前面").isLessThan(annotated("first").sort());
    }

    @Test
    void annotationIsReadableAtRuntimeOnFieldsAndComposedAnnotations() {
        assertThat(OrderBy.class.getAnnotation(Retention.class).value()).as("建 TableInfo 时才反射读，Retention 降级会让自动排序静默消失")
                .isSameAs(RetentionPolicy.RUNTIME);
        assertThat(OrderBy.class.getAnnotation(Target.class).value())
                .containsExactlyInAnyOrder(ElementType.FIELD, ElementType.ANNOTATION_TYPE);
    }

    private static OrderBy annotated(String name) {
        try {
            return Probe.class.getDeclaredField(name).getAnnotation(OrderBy.class);
        } catch (NoSuchFieldException e) {
            throw new AssertionError("夹具里缺字段 " + name, e);
        }
    }

    static class Probe {

        @OrderBy
        private String bare;

        @OrderBy(asc = true, sort = 1)
        private String ascending;

        @OrderBy(sort = -2)
        private String negativeSort;

        @OrderBy
        private String first;

        @OrderBy
        private String alsoUnnumbered;

        @OrderBy(sort = 7)
        private String numbered;

    }

}
