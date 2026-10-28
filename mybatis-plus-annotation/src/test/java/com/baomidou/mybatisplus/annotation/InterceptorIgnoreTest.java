package com.baomidou.mybatisplus.annotation;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link InterceptorIgnore} 的属性不是布尔而是字符串词汇表，取值直接决定插件跳过与否。<br>
 * 特别注意只有 {@code dataPermission} 有非空默认值：“默认关闭数据权限插件”，其余属性留空表示不表态。<br>
 * 词汇表由 core 的 InterceptorIgnoreHelper#getBoolean 解释，注解层只负责原样保存。
 *
 * @author taoyu
 */
@Tag("unit")
class InterceptorIgnoreTest {

    @Test
    void onlyDataPermissionIsIgnoredByDefault() {
        InterceptorIgnore ignore = Bare.class.getAnnotation(InterceptorIgnore.class);
        assertThat(ignore).as("夹具没挂上注解，后面的断言全都无意义").isNotNull();
        assertThat(ignore.tenantLine()).as("空串＝不表态，交给方法级注解或插件默认").isEmpty();
        assertThat(ignore.dynamicTableName()).isEmpty();
        assertThat(ignore.blockAttack()).as("默认不跳过全表更新拦截").isEmpty();
        assertThat(ignore.illegalSql()).isEmpty();
        assertThat(ignore.dataPermission()).as("默认值 1 是“跳过数据权限插件”，改成空串会让所有没写注解的 Mapper 突然被加数据权限").isEqualTo("1");
        assertThat(ignore.others()).isEmpty();
    }

    @Test
    void flagValuesAreStoredVerbatim() {
        InterceptorIgnore ignore = Vocabulary.class.getAnnotation(InterceptorIgnore.class);
        assertThat(ignore.tenantLine()).as("1/true/on 三种写法都合法，注解层不归一").isEqualTo("1");
        assertThat(ignore.dynamicTableName()).isEqualTo("on");
        assertThat(ignore.blockAttack()).isEqualTo("true");
        assertThat(ignore.illegalSql()).as("0 是显式不跳过，和留空不同").isEqualTo("0");
        assertThat(ignore.dataPermission()).as("写 false 才会启用数据权限插件").isEqualTo("false");
    }

    @Test
    void othersKeepTheKeyAtValueShape() {
        assertThat(Others.class.getAnnotation(InterceptorIgnore.class).others())
                .as("others 靠 @ 分隔键值，拆分发生在 core；注解层顺序即书写顺序").containsExactly("tenant@1", "block@0");
    }

    @Test
    void annotationAppliesToBothMapperTypeAndMapperMethod() throws NoSuchMethodException {
        assertThat(Mapper.class.getAnnotation(InterceptorIgnore.class)).as("类级：整个 Mapper 生效").isNotNull();
        assertThat(Mapper.class.getDeclaredMethod("select").getAnnotation(InterceptorIgnore.class))
                .as("方法级：方法上的取值覆盖类上的取值").isNotNull();
    }

    @Test
    void annotationIsReadableAtRuntimeButNotInherited() {
        assertThat(InterceptorIgnore.class.getAnnotation(Retention.class).value()).isSameAs(RetentionPolicy.RUNTIME);
        assertThat(InterceptorIgnore.class.getAnnotation(Target.class).value())
                .as("没有 ANNOTATION_TYPE：不能靠组合注解转发，只能直接标在 Mapper 或方法上")
                .containsExactlyInAnyOrder(ElementType.TYPE, ElementType.METHOD);
        assertThat(InterceptorIgnore.class.isAnnotationPresent(Inherited.class))
                .as("core 走的是 JDK 的 getAnnotation，没带 @Inherited 意味着标在父接口上对子接口不生效").isFalse();
        assertThat(Child.class.getAnnotation(InterceptorIgnore.class)).as("子接口看不到父接口那份注解，注解必须落在真正被调用的 Mapper 上")
                .isNull();
    }

    @InterceptorIgnore
    interface Bare {

    }

    @InterceptorIgnore(tenantLine = "1", dynamicTableName = "on", blockAttack = "true", illegalSql = "0", dataPermission = "false")
    interface Vocabulary {

    }

    @InterceptorIgnore(others = {"tenant@1", "block@0"})
    interface Others {

    }

    @InterceptorIgnore(tenantLine = "1")
    interface Mapper {

        @InterceptorIgnore(tenantLine = "0")
        String select();

    }

    interface Child extends Mapper {

    }

}
