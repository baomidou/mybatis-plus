package com.baomidou.mybatisplus.core.test;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.EncryptUtils;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import lombok.Data;
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
        System.out.println(TableInfoHelper.checkRelated(true, "order", "order"));
        System.out.println(TableInfoHelper.checkRelated(true, "orderFile", "'order_file'"));
    }

    @Test
    public void testTableInfoHelper() {
        TableInfo info = TableInfoHelper.initTableInfo(null, Xx.class);
        System.out.println(info.getAllSqlSet(true, null));
    }

    @Data
    public static class Xx {
        private Long id;
        @TableField(exist = false)
        private String x1;
        private String x2;
        private String x3;
        private String x4;
        private String x5;
    }
}
