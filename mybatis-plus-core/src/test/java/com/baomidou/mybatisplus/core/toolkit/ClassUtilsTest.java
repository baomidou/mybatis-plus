package com.baomidou.mybatisplus.core.toolkit;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * 测试
 *
 * @author HCL
 * 2018/7/26 9:53
 */
public class ClassUtilsTest {

    /**
     * 测试实例化方法
     */
    @Test
    public void newInstanceTest() {
        // 根据设计，只要能实例化，并且 对象不为 null ， 即认为执行成功
        Demo demo = ClassUtils.newInstance(Demo.class);
        Assert.assertNotNull(demo);

        Map map = ClassUtils.newInstance(HashMap.class);
        Assert.assertNotNull(map);
    }

    /**
     * 实例化的 demo class
     */
    private static class Demo {

    }
}
