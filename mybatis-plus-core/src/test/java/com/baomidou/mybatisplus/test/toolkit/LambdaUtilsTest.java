package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.IdeaProxyLambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.ReflectLambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 测试 Lambda 解析类
 */
class LambdaUtilsTest {

    @Test
    void test1() {
        LambdaMeta meta = LambdaUtils.extract(TestModel::getName);
        assertNotNull(meta);
        assertThat(meta).isInstanceOf(ReflectLambdaMeta.class);
        assertThat(meta.getInstantiatedClass()).isEqualTo(TestModel.class);
        assertThat(meta.getImplMethodName()).isEqualTo("getName");
    }

    @Test
    @SuppressWarnings("unchecked")
    void test2() throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle getter = lookup.findVirtual(TestModel.class, "getId", MethodType.methodType(int.class));
        SFunction<TestModel, Object> function = (SFunction<TestModel, Object>) MethodHandleProxies.asInterfaceInstance(SFunction.class, getter);
        LambdaMeta meta = LambdaUtils.extract(function);
        assertNotNull(meta);
        assertThat(meta).isInstanceOf(IdeaProxyLambdaMeta.class);
        assertThat(meta.getInstantiatedClass()).isEqualTo(TestModel.class);
        assertThat(meta.getImplMethodName()).isEqualTo("getId");
    }

    @Getter
    public static class TestModel extends Parent implements Named {
        private String name;
    }

    @Getter
    private static abstract class Parent {
        private int id;
    }

    private interface Named {
        String getName();
    }
}
