package com.baomidou.mybatisplus.core.toolkit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author HCL
 * Create at 2018/9/17
 */

class StringUtilsTest {

    @Test
    void camelToUnderlineTest() {
        String s = "userId";
        Assertions.assertEquals("user_id", StringUtils.camelToUnderline(s));
    }
}
