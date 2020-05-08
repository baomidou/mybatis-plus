package com.baomidou.mybatisplus.test.generator.keywords;

import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author nieqiurong 2020/5/8.
 */
class MySqlKeyWordsHandlerTest {
    
    @Test
    void test() {
        MySqlKeyWordsHandler keyWordsHandler = new MySqlKeyWordsHandler();
        Assertions.assertTrue(keyWordsHandler.isKeyWords("FILE"));
        Assertions.assertTrue(keyWordsHandler.isKeyWords("file"));
        Assertions.assertFalse(keyWordsHandler.isKeyWords("USER_NAME"));
        Assertions.assertFalse(keyWordsHandler.isKeyWords("user_name"));
        Assertions.assertEquals(keyWordsHandler.formatColumn("FILE"), "`FILE`");
    
        keyWordsHandler.getKeyWords().remove("FILE");
        Assertions.assertFalse(keyWordsHandler.isKeyWords("FILE"));
        Assertions.assertFalse(keyWordsHandler.isKeyWords("file"));
        
        keyWordsHandler = new MySqlKeyWordsHandler(new ArrayList<>(Arrays.asList("TEST","AAA")));
        Assertions.assertTrue(keyWordsHandler.isKeyWords("TEST"));
        Assertions.assertTrue(keyWordsHandler.isKeyWords("test"));
        Assertions.assertTrue(keyWordsHandler.isKeyWords("AAA"));
        Assertions.assertTrue(keyWordsHandler.isKeyWords("aaa"));
        Assertions.assertFalse(keyWordsHandler.isKeyWords("FILE"));
        Assertions.assertFalse(keyWordsHandler.isKeyWords("file"));
    }
    
}
