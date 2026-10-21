package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试
 *
 * @author HCL <br>
 *         2018/7/26 9:53
 */
class ClassUtilsTest {

    /**
     * 测试实例化方法
     */
    @Test
    void newInstanceTest() {
        // 根据设计，只要能实例化，并且 对象不为 null ， 即认为执行成功
        Demo demo = ClassUtils.newInstance(Demo.class);
        assertThat(demo).isNotNull();

        Map<?, ?> map = ClassUtils.newInstance(HashMap.class);
        assertThat(map).isNotNull();
    }

    /**
     * 实例化的 demo class
     */
    private static class Demo {
    }
}
