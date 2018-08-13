package com.baomidou.mybatisplus.core.test;

import com.baomidou.mybatisplus.core.toolkit.EncryptUtils;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * 加密测试
 */
public class EncryptTest {

    @Test
    public void md5Base64() {
        Assert.assertEquals("Jgmg8jeuq9EyB1ybYtj1fg==",
            EncryptUtils.md5Base64("犯我中华者虽远必诛"));
    }

    @Test
    public void other() {
        System.out.println(TableInfoHelper.checkRelated(true, "order", "'order'"));
    }
}
