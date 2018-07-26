package com.baomidou.mybatisplus.core.toolkit;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * 测试
 *
 * @author HCL
 * 2018/7/26 9:53
 */
public class ClassUtilsTest {

    @Test
    public void newInstanceTest() {
        Demo demo = ClassUtils.newInstance(Demo.class);
        Map map = ClassUtils.newInstance(HashMap.class);
    }

    private static class Demo {

    }
}
