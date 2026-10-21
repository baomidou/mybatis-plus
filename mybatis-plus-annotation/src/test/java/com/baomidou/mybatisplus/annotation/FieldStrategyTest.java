package com.baomidou.mybatisplus.annotation;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link FieldStrategy} 的成员名是外部配置词汇：yml、生成器配置、{@code @TableField} 都按名字绑定，<br>
 * 改名或加别名会让已有工程在启动时直接失败，所以名字集合必须冻结。
 *
 * @author taoyu
 */
class FieldStrategyTest {

    @Test
    void namesAreTheExternalVocabulary() {
        assertThat(Arrays.stream(FieldStrategy.values()).map(Enum::name).toList())
                .as("五个策略名一个都不能动：NOT_EMPTY 只对字符串生效、DEFAULT 表示跟随全局，含义都写在名字里")
                .containsExactlyInAnyOrder("ALWAYS", "NOT_NULL", "NOT_EMPTY", "DEFAULT", "NEVER");
    }

    @Test
    void everyNameLooksUpTheSameConstant() {
        for (FieldStrategy strategy : FieldStrategy.values()) {
            assertThat(FieldStrategy.valueOf(strategy.name())).as("配置按名字取值要拿回同一个常量").isSameAs(strategy);
        }
        assertThat(FieldStrategy.values()).as("没有多余常量").hasSize(5);
    }

}
