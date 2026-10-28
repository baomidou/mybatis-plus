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
 * {@link TableName} 的每一个默认值都对应一条推导规则，写错不报错，只会静默换表名或换映射。<br>
 * 这里只钉注解本身能被反射读成什么，推导行为由 core 的 TableInfoHelperTest 负责。
 *
 * @author taoyu
 */
@Tag("unit")
class TableNameTest {

    @Test
    void bareTableNameLeavesEveryOptionUnset() {
        TableName table = Bare.class.getAnnotation(TableName.class);
        assertThat(table).as("夹具没挂上注解，后面的断言全都无意义").isNotNull();
        assertThat(table.value()).as("不写 value 就退回类名推导表名").isEmpty();
        assertThat(table.schema()).as("不写 schema 才交给全局配置，写了会覆盖全局").isEmpty();
        assertThat(table.keepGlobalPrefix()).as("默认 false：显式写了表名后，全局 tablePrefix 不再拼上去").isFalse();
        assertThat(table.resultMap()).as("不指定就不复用现成 resultMap").isEmpty();
        assertThat(table.autoResultMap()).as("默认不自动构建 resultMap，typeHandler 字段会被静默忽略").isFalse();
        assertThat(table.properties()).as("默认不设白名单，全部属性参与映射").isEmpty();
        assertThat(table.excludeProperty()).as("默认不排除任何属性").isEmpty();
    }

    @Test
    void explicitValuesAreStoredVerbatim() {
        TableName table = Explicit.class.getAnnotation(TableName.class);
        assertThat(table.value()).as("表名原样保存，注解层不做大小写和下划线转换").isEqualTo("Sys_User");
        assertThat(table.schema()).isEqualTo("my_schema");
        assertThat(table.keepGlobalPrefix()).isTrue();
        assertThat(table.resultMap()).isEqualTo("userMap");
        assertThat(table.autoResultMap()).isTrue();
        assertThat(table.properties()).containsExactly("id", "name");
        assertThat(table.excludeProperty()).as("白名单和黑名单可以同时写，谁生效由 core 判").containsExactly("password");
    }

    @Test
    void annotationIsReadableOnTypesButIsNotInherited() {
        assertThat(TableName.class.getAnnotation(Retention.class).value()).as("TableInfoHelper 靠运行时反射读它")
                .isSameAs(RetentionPolicy.RUNTIME);
        assertThat(TableName.class.getAnnotation(Target.class).value()).as("只能挂类或组合注解，挂字段上编译不过")
                .containsExactlyInAnyOrder(ElementType.TYPE, ElementType.ANNOTATION_TYPE);
        assertThat(TableName.class.isAnnotationPresent(Inherited.class))
                .as("@Inherited 只在 JDK 的 getAnnotation 路径生效，本注解刻意没带：MP 自己会往上找父类").isFalse();
    }

    @TableName
    static class Bare {

    }

    @TableName(value = "Sys_User", schema = "my_schema", keepGlobalPrefix = true, resultMap = "userMap", autoResultMap = true, properties = {
            "id", "name"}, excludeProperty = {"password"})
    static class Explicit {

    }

}
