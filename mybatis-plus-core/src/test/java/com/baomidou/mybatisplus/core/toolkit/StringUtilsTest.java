package com.baomidou.mybatisplus.core.toolkit;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author HCL
 * Create at 2018/9/17
 */

public class StringUtilsTest {

    @Test
    public void camelToUnderlineTest() {
        String s = "userId";
        Assert.assertEquals("user_id", StringUtils.camelToUnderline(s));
    }
}
