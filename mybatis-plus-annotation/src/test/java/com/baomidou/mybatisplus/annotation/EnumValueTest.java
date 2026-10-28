package com.baomidou.mybatisplus.annotation;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link EnumValue} 用来在普通枚举里指出“哪一列字段是数据库存储值”，core 只判断字段上有没有它、不读属性。<br>
 * 没标它也没实现 IEnum 的枚举会在初始化时抛异常，所以这个标记的位置规则要钉住。
 *
 * @author taoyu
 */
@Tag("unit")
class EnumValueTest {

    @Test
    void itIsAMarkerOnEnumFields() {
        assertThat(EnumValue.class.getDeclaredMethods()).as("纯标记注解：core 只看存在性，加属性也不会被读").isEmpty();
        assertThat(EnumValue.class.getAnnotation(Retention.class).value())
                .as("降级成 CLASS 会让运行时的 isAnnotationPresent 全部落空").isSameAs(RetentionPolicy.RUNTIME);
        assertThat(EnumValue.class.getAnnotation(Target.class).value())
                .containsExactlyInAnyOrder(ElementType.FIELD, ElementType.ANNOTATION_TYPE);
    }

    @Test
    void theMarkedFieldIsTheOneReflectionFinds() throws NoSuchFieldException {
        assertThat(annotated("code").isAnnotationPresent(EnumValue.class)).as("存储值取的是被标记的那个字段").isTrue();
        assertThat(annotated("descp").isAnnotationPresent(EnumValue.class)).as("没标记的描述字段不能被当成列值").isFalse();
    }

    private static Field annotated(String name) {
        try {
            return Grade.class.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new AssertionError("夹具里缺字段 " + name, e);
        }
    }

    enum Grade {

        PRIMARY(1, "小学"),
        SECONDARY(2, "中学");

        @EnumValue
        private final int code;

        private final String descp;

        Grade(int code, String descp) {
            this.code = code;
            this.descp = descp;
        }
    }

}
