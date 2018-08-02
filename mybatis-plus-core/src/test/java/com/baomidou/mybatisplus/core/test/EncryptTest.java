package com.baomidou.mybatisplus.core.test;

import org.junit.Assert;
import org.junit.Test;

import com.baomidou.mybatisplus.core.toolkit.EncryptUtils;

/**
 * 加密测试
 */
public class EncryptTest {

    @Test
    public void md5Base64() {
        Assert.assertEquals("Jgmg8jeuq9EyB1ybYtj1fg==",
            EncryptUtils.md5Base64("犯我中华者虽远必诛"));
    }
}
