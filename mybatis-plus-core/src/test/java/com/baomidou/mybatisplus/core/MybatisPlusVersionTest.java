package com.baomidou.mybatisplus.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MybatisPlusVersionTest {

    @Test
    void shouldNeverReturnNullVersion() {
        assertThat(MybatisPlusVersion.getVersion()).isNotNull();
    }
}
