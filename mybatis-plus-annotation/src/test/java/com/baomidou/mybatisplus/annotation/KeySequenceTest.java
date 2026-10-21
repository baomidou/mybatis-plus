package com.baomidou.mybatisplus.annotation;

import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link KeySequence} 只在配了 IKeyGenerator 时才被读，取值直接决定插入前用哪个生成器、执行哪段取号 SQL。<br>
 * 注意 {@code value()} 没有任何兜底：core 把它原样交给 {@code IKeyGenerator#executeSql} 拼接，不写就拼出坏 SQL。
 *
 * @author taoyu
 */
class KeySequenceTest {

    @Test
    void bareKeySequenceLeavesTheGeneratorUnselected() {
        KeySequence sequence = Bare.class.getAnnotation(KeySequence.class);
        assertThat(sequence).as("夹具没挂上注解，后面的断言全都无意义").isNotNull();
        assertThat(sequence.value()).as("序列名为空不会按表名推导，直接拼进取号 SQL").isEmpty();
        assertThat(sequence.dbType()).as("OTHER 是哨兵值：多个生成器时它表示不做筛选，用第一个").isSameAs(DbType.OTHER);
    }

    @Test
    void explicitValuesAreKeptAsWritten() {
        KeySequence sequence = Explicit.class.getAnnotation(KeySequence.class);
        assertThat(sequence.value()).as("序列名原样保存").isEqualTo("seq_user");
        assertThat(sequence.dbType()).as("指定 dbType 才会从多个生成器里筛出对应实现").isSameAs(DbType.ORACLE);
    }

    @Test
    void inheritedFlagSeparatesItFromTableName() {
        assertThat(KeySequence.class.isAnnotationPresent(Inherited.class))
                .as("本注解带 @Inherited，JDK 的 getAnnotation 能顺着父类找到").isTrue();
        KeySequence inherited = Inheriting.class.getAnnotation(KeySequence.class);
        assertThat(inherited).as("子类没写注解时，JDK 路径拿到的是父类那份").isNotNull();
        assertThat(inherited.value()).as("继承来的仍是父类写的序列名").isEqualTo("seq_base");
        assertThat(Inheriting.class.getDeclaredAnnotations())
                .as("@Inherited 不会把注解复制进 getDeclaredAnnotations，MP 自己遍历父类才补上了这条路径").isEmpty();
    }

    @Test
    void annotationIsReadableAtRuntimeOnTypesAndComposedAnnotations() {
        assertThat(KeySequence.class.getAnnotation(Retention.class).value()).as("生成器在注入语句时才反射读它，Retention 降级会静默丢掉序列名")
                .isSameAs(RetentionPolicy.RUNTIME);
        assertThat(KeySequence.class.getAnnotation(Target.class).value())
                .containsExactlyInAnyOrder(ElementType.TYPE, ElementType.ANNOTATION_TYPE);
    }

    @KeySequence
    static class Bare {

    }

    @KeySequence(value = "seq_user", dbType = DbType.ORACLE)
    static class Explicit {

    }

    @KeySequence("seq_base")
    static class Base {

    }

    static class Inheriting extends Base {

    }

}
