package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试
 *
 * @author HCL
 * 2018/7/26 9:53
 */
class ClassUtilsTest {

    /**
     * 测试实例化方法
     */
    @Test
    void newInstanceTest() {
        // 根据设计，只要能实例化，并且 对象不为 null ， 即认为执行成功
        Demo demo = ClassUtils.newInstance(Demo.class);
        Assertions.assertNotNull(demo);

        Map<?,?> map = ClassUtils.newInstance(HashMap.class);
        Assertions.assertNotNull(map);
    }

    /**
     * 实例化的 demo class
     */
    private static class Demo {}
}
