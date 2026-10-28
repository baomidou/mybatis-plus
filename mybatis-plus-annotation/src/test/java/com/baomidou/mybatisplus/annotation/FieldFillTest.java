package com.baomidou.mybatisplus.annotation;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link FieldFill} 决定注入的 insert/update 脚本要不要把字段交给 {@code MetaObjectHandler}，<br>
 * core 按常量做等值比较（INSERT 与 INSERT_UPDATE 都算插入填充，UPDATE 与 INSERT_UPDATE 都算更新填充），<br>
 * 所以四个名字的组合关系一旦变化，老配置里的填充字段会静默不再被填。
 *
 * @author taoyu
 */
@Tag("unit")
class FieldFillTest {

    @Test
    void namesAreTheExternalVocabulary() {
        assertThat(Arrays.stream(FieldFill.values()).map(Enum::name).toList())
                .as("INSERT_UPDATE 是唯一同时命中插入和更新的组合项，改名会让 yml 与生成器配置失配")
                .containsExactlyInAnyOrder("DEFAULT", "INSERT", "UPDATE", "INSERT_UPDATE");
        for (FieldFill fill : FieldFill.values()) {
            assertThat(FieldFill.valueOf(fill.name())).as("按名字取值要拿回同一个常量").isSameAs(fill);
        }
    }

    @Test
    void defaultIsWhatABareTableFieldSees() throws NoSuchMethodException {
        assertThat(TableField.class.getDeclaredMethod("fill").getDefaultValue())
                .as("@TableField 不写 fill 时 core 读到的就是 DEFAULT，两个填充钩子都跳过").isSameAs(FieldFill.DEFAULT);
    }

}
