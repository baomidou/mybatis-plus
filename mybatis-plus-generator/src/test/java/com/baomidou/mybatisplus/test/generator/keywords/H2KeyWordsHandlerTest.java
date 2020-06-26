package com.baomidou.mybatisplus.test.generator.keywords;

import com.baomidou.mybatisplus.generator.keywords.H2KeyWordsHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author nieqiurong 2020/5/8.
 */
class H2KeyWordsHandlerTest {
    
    @Test
    void test() {
        H2KeyWordsHandler keyWordsHandler = new H2KeyWordsHandler();
        Assertions.assertTrue(keyWordsHandler.isKeyWords("CHECK"));
        Assertions.assertTrue(keyWordsHandler.isKeyWords("check"));
        Assertions.assertFalse(keyWordsHandler.isKeyWords("USER_NAME"));
        Assertions.assertFalse(keyWordsHandler.isKeyWords("user_name"));
        Assertions.assertEquals(keyWordsHandler.formatColumn("CHECK"), "\"CHECK\"");
    
        keyWordsHandler.getKeyWords().remove("CHECK");
        Assertions.assertFalse(keyWordsHandler.isKeyWords("CHECK"));
        Assertions.assertFalse(keyWordsHandler.isKeyWords("check"));
    
        keyWordsHandler = new H2KeyWordsHandler(new ArrayList<>(Arrays.asList("TEST","AAA")));
        Assertions.assertTrue(keyWordsHandler.isKeyWords("TEST"));
        Assertions.assertTrue(keyWordsHandler.isKeyWords("test"));
        Assertions.assertTrue(keyWordsHandler.isKeyWords("AAA"));
        Assertions.assertTrue(keyWordsHandler.isKeyWords("aaa"));
        Assertions.assertFalse(keyWordsHandler.isKeyWords("FILE"));
        Assertions.assertFalse(keyWordsHandler.isKeyWords("file"));
    }
    
}
