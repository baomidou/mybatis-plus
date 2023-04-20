package com.baomidou.mybatisplus.generator.keywords;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author nieqiurong 2020/5/9.
 */
class PostgreSqlKeyWordsHandlerTest {

    @Test
    void test() {
        PostgreSqlKeyWordsHandler keyWordsHandler = new PostgreSqlKeyWordsHandler();
        Assertions.assertTrue(keyWordsHandler.isKeyWords("with"));
        Assertions.assertTrue(keyWordsHandler.isKeyWords("WITH"));
        Assertions.assertFalse(keyWordsHandler.isKeyWords("system"));
        Assertions.assertFalse(keyWordsHandler.isKeyWords("SYSTEM"));
        Assertions.assertEquals(keyWordsHandler.formatColumn("with"), "\"with\"");
    }

}
