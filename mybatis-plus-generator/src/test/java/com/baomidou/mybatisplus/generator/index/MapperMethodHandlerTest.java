package com.baomidou.mybatisplus.generator.index;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class MapperMethodHandlerTest {

    @Test
    void shouldRecognizePrimaryKeyNames() {
        var method = new DefaultGenerateMapperMethodHandler();
        assertThat(method.isPrimaryKey("PRIMARY")).isTrue();
        assertThat(method.isPrimaryKey("primary")).isTrue();
        assertThat(method.isPrimaryKey("PRIMARY_KEY_1")).isTrue();
        assertThat(method.isPrimaryKey("primary_key_1")).isTrue();
        assertThat(method.isPrimaryKey("id_idx")).isFalse();
    }

}
