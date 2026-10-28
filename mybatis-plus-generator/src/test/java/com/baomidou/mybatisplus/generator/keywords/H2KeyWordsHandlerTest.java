package com.baomidou.mybatisplus.generator.keywords;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2020/5/8.
 */
@Tag("unit")
class H2KeyWordsHandlerTest {

    @Test
    void test() {
        H2KeyWordsHandler keyWordsHandler = new H2KeyWordsHandler();
        assertThat(keyWordsHandler.isKeyWords("CHECK")).isTrue();
        assertThat(keyWordsHandler.isKeyWords("check")).isTrue();
        assertThat(keyWordsHandler.isKeyWords("USER_NAME")).isFalse();
        assertThat(keyWordsHandler.isKeyWords("user_name")).isFalse();
        assertThat(keyWordsHandler.formatColumn("CHECK")).isEqualTo("\"CHECK\"");

        keyWordsHandler.getKeyWords().remove("CHECK");
        assertThat(keyWordsHandler.isKeyWords("CHECK")).isFalse();
        assertThat(keyWordsHandler.isKeyWords("check")).isFalse();

        keyWordsHandler = new H2KeyWordsHandler(new ArrayList<>(Arrays.asList("TEST", "AAA")));
        assertThat(keyWordsHandler.isKeyWords("TEST")).isTrue();
        assertThat(keyWordsHandler.isKeyWords("test")).isTrue();
        assertThat(keyWordsHandler.isKeyWords("AAA")).isTrue();
        assertThat(keyWordsHandler.isKeyWords("aaa")).isTrue();
        assertThat(keyWordsHandler.isKeyWords("FILE")).isFalse();
        assertThat(keyWordsHandler.isKeyWords("file")).isFalse();
    }

}
