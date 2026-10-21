package com.baomidou.mybatisplus.generator.keywords;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2020/5/8.
 */
class MySqlKeyWordsHandlerTest {

    @Test
    void test() {
        MySqlKeyWordsHandler keyWordsHandler = new MySqlKeyWordsHandler();
        assertThat(keyWordsHandler.isKeyWords("FILE")).isTrue();
        assertThat(keyWordsHandler.isKeyWords("file")).isTrue();
        assertThat(keyWordsHandler.isKeyWords("USER_NAME")).isFalse();
        assertThat(keyWordsHandler.isKeyWords("user_name")).isFalse();
        assertThat(keyWordsHandler.formatColumn("FILE")).isEqualTo("`FILE`");

        keyWordsHandler.getKeyWords().remove("FILE");
        assertThat(keyWordsHandler.isKeyWords("FILE")).isFalse();
        assertThat(keyWordsHandler.isKeyWords("file")).isFalse();

        keyWordsHandler = new MySqlKeyWordsHandler(new ArrayList<>(Arrays.asList("TEST", "AAA")));
        assertThat(keyWordsHandler.isKeyWords("TEST")).isTrue();
        assertThat(keyWordsHandler.isKeyWords("test")).isTrue();
        assertThat(keyWordsHandler.isKeyWords("AAA")).isTrue();
        assertThat(keyWordsHandler.isKeyWords("aaa")).isTrue();
        assertThat(keyWordsHandler.isKeyWords("FILE")).isFalse();
        assertThat(keyWordsHandler.isKeyWords("file")).isFalse();
    }

}
