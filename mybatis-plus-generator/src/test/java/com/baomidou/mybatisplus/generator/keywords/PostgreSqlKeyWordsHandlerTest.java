package com.baomidou.mybatisplus.generator.keywords;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2020/5/9.
 */
class PostgreSqlKeyWordsHandlerTest {

    @Test
    void test() {
        PostgreSqlKeyWordsHandler keyWordsHandler = new PostgreSqlKeyWordsHandler();
        assertThat(keyWordsHandler.isKeyWords("with")).isTrue();
        assertThat(keyWordsHandler.isKeyWords("WITH")).isTrue();
        assertThat(keyWordsHandler.isKeyWords("system")).isFalse();
        assertThat(keyWordsHandler.isKeyWords("SYSTEM")).isFalse();
        assertThat(keyWordsHandler.formatColumn("with")).isEqualTo("\"with\"");
    }

}
