package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.type.StringTypeHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class LambdaAutoMappingTest {

    @BeforeAll
    static void initTableInfo() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Entity.class);
    }

    @Test
    void shouldToggleAutoMappingOnLambdaQueryAndUpdateWrappers() {
        assertConditions(Wrappers.<Entity>lambdaQuery(), false);
        assertConditions(Wrappers.<Entity>lambdaUpdate(), false);
        assertConditions(Wrappers.<Entity>lambdaQuery().enableAutoMapping(), true);
        assertConditions(Wrappers.<Entity>lambdaUpdate().enableAutoMapping(), true);
        assertConditions(Wrappers.<Entity>lambdaQuery().enableAutoMapping().disableAutoMapping(), false);
    }

    private <W extends AbstractLambdaWrapper<Entity, W>> void assertConditions(W wrapper, boolean mapping) {
        wrapper.eq(Entity::getName, "a").notIn(Entity::getName, Arrays.asList("b", "c"))
                .notIn(Entity::getName, "d", "e");
        assertThat(wrapper.getSqlSegment()).isEqualTo(
                "(name = " + param(1, mapping) + " AND name NOT IN (" + param(2, mapping) + "," + param(3, mapping)
                        + ")" + " AND name NOT IN (" + param(4, mapping) + "," + param(5, mapping) + "))");
        assertThat(wrapper.getParamNameValuePairs().size()).isEqualTo(5);
        assertThat(wrapper.getParamNameValuePairs().get("MPGENVAL5")).isEqualTo("e");
    }

    @Test
    void shouldApplyAutoMappingInNestedLambdaConditions() {
        assertNested(Wrappers.<Entity>lambdaQuery().enableAutoMapping(), true);
        assertNested(Wrappers.<Entity>lambdaUpdate().enableAutoMapping(), true);
        assertNested(Wrappers.<Entity>lambdaQuery(), false);
        assertNested(Wrappers.<Entity>lambdaUpdate(), false);
    }

    private <W extends AbstractLambdaWrapper<Entity, W>> void assertNested(W wrapper, boolean mapping) {
        wrapper.and(w -> w.notIn(Entity::getName, Arrays.asList("a")).or(n -> n.notIn(Entity::getName, "b")));
        assertThat(wrapper.getSqlSegment())
                .isEqualTo("((name NOT IN (" + param(1, mapping) + ") OR (name NOT IN (" + param(2, mapping) + "))))");
    }

    private String param(int index, boolean mapping) {
        return "#{ew.paramNameValuePairs.MPGENVAL" + index
                + (mapping ? ",typeHandler=" + StringTypeHandler.class.getName() : "") + "}";
    }

    @Data
    private static class Entity {

        @TableField(typeHandler = StringTypeHandler.class)
        private String name;
    }
}
