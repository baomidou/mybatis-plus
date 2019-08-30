package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.junit.jupiter.api.Test;

/**
 * 测试 StringUtils工具类测试
 *
 * @author XiaoBingBy
 * @since 2019-08-30
 */
public class StringUtilsTests {

    @Test
    public void isEmptyTest() {
        Assert.isTrue(StringUtils.isEmpty(""), "error not empty");

        Assert.isTrue(StringUtils.isEmpty(null), "error not empty");

        Assert.isTrue(StringUtils.isEmpty("   "), "error not empty");
    }

}
