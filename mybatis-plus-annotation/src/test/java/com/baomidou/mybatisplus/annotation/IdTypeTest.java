package com.baomidou.mybatisplus.annotation;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * {@link IdType} 的 key 不是普通编号：core 用 {@code idType.getKey() >= 3} 决定插入前要不要自动补主键。<br>
 * 所以 3 这条分界线、每个枚举的 key 以及 key 不重复都必须钉死，改动等于改主键生成行为。
 *
 * @author taoyu
 */
class IdTypeTest {

    @Test
    void keysKeepTheFrozenNumbering() {
        assertThat(keys()).as("配置按名字取枚举，代码按 key 比较，改名字或改编号都会影响主键生成").containsOnly(
                entry("AUTO", 0), entry("NONE", 1), entry("INPUT", 2), entry("ASSIGN_ID", 3), entry("ASSIGN_UUID", 4));
    }

    @Test
    void onlyAssignTypesPassTheAutoIdGate() {
        assertThat(Arrays.stream(IdType.values()).filter(type -> type.getKey() >= 3).toList())
                .as("只有 ASSIGN_* 会让框架在 ID 为空时生成主键；AUTO/NONE/INPUT 一旦挪进这条线就会抢库里的自增 ID")
                .containsExactlyInAnyOrder(IdType.ASSIGN_ID, IdType.ASSIGN_UUID);
        assertThat(Arrays.stream(IdType.values()).filter(type -> type.getKey() < 3).toList())
                .containsExactlyInAnyOrder(IdType.AUTO, IdType.NONE, IdType.INPUT);
    }

    private static Map<String, Integer> keys() {
        return Arrays.stream(IdType.values()).collect(Collectors.toMap(Enum::name, IdType::getKey));
    }

}
