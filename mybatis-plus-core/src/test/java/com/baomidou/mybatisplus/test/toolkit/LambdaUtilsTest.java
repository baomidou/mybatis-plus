package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * 测试 Lambda 解析类
 */
class LambdaUtilsTest {

    /**
     * 测试解析
     */
    @Test
    @SuppressWarnings("unchecked")
    void testExtract() throws IllegalAccessException, NoSuchMethodException {
        SFunction<TestModel, Object> function = TestModel::getName;
        test(function);
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle getter = lookup.findVirtual(TestModel.class, "getId", MethodType.methodType(int.class));
        assertNotNull(SerializedLambda.extract(function));
        function = (SFunction<TestModel, Object>) MethodHandleProxies.asInterfaceInstance(SFunction.class, getter);
        test(function);
    }

    private void test(SFunction<TestModel, Object> function) {
        function.apply(new TestModel());
        LambdaMeta meta = LambdaUtils.extract(function);
        assertNotNull(meta);
        assertSame(TestModel.class, meta.getInstantiatedClass());
    }

    /**
     * 用于测试的 Model
     */
    @Getter
    public static class TestModel extends Parent implements Named {
        private String name;
    }

    @Getter
    private static abstract class Parent {
        private int id;
    }

    // 处理 ISSUE:https://gitee.com/baomidou/mybatis-plus/issues/I13Y8Y，由于 java 本身处理的问题，这里无法获取到实例
    private abstract static class BaseHolder<T extends Named> {

        LambdaMeta toLambda() {
            return LambdaUtils.extract(T::getName);
        }

    }

    private static class TestModelHolder extends BaseHolder<TestModel> {
    }

    private interface Named {
        String getName();
    }

}
