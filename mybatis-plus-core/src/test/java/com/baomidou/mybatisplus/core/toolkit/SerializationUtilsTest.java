package com.baomidou.mybatisplus.core.toolkit;

import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 序列化深拷贝与失败路径，异常类型和消息是给调用方的契约
 */
class SerializationUtilsTest {

    @Test
    void cloneIsANullSafeDeepCopy() {
        Payload original = new Payload("demo");
        original.tags.add("a");

        Payload cloned = SerializationUtils.clone(original);

        assertThat(cloned).isNotSameAs(original);
        assertThat(cloned.name).isEqualTo("demo");
        assertThat(cloned.tags).isEqualTo(List.of("a"));
        assertThat(cloned.tags).isNotSameAs(original.tags);
        assertThat(SerializationUtils.<Payload>clone(null)).isNull();
    }

    @Test
    void serializeRoundTripsThroughBytes() {
        byte[] bytes = SerializationUtils.serialize(new Payload("demo"));
        assertThat(bytes).isNotEmpty();
        assertThat(((Payload) SerializationUtils.deserialize(bytes)).name).isEqualTo("demo");
        assertThat(SerializationUtils.serialize(null)).isNull();
        assertThat(SerializationUtils.deserialize(null)).isNull();
    }

    @Test
    void nonSerializablePayloadIsRejectedWithTheTypeInTheMessage() {
        assertThatThrownBy(() -> SerializationUtils.serialize(new Object()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Failed to serialize object of type: class java.lang.Object");
    }

    @Test
    void garbageBytesAreRejectedAsIllegalArgument() {
        assertThatThrownBy(() -> SerializationUtils.deserialize(new byte[]{1, 2, 3}))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Failed to deserialize object");
    }

    private static class Payload implements Serializable {

        private final String name;

        private final List<String> tags = new ArrayList<>();

        private Payload(String name) {
            this.name = name;
        }
    }
}
