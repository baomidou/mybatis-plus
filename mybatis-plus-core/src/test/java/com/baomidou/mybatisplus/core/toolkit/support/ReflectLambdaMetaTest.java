package com.baomidou.mybatisplus.core.toolkit.support;

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.JRE;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * @author Deycoesr@gmail.com
 * @see ReflectLambdaMeta
 * @since Fri 05/11/2021
 */
class ReflectLambdaMetaTest {

    @SneakyThrows
    @Test
    void illegalReflectiveAccessTest() {

        JRE jre = JRE.currentVersion();
        System.out.println("jre = " + jre);

        SFunction<TestBean, String> getNameFunc = TestBean::getName;

        Method method = getNameFunc.getClass().getDeclaredMethod("writeReplace");

        Class<?> instantiatedClass = new ReflectLambdaMeta(
            (SerializedLambda) ReflectionKit.setAccessible(method).invoke(getNameFunc)
        ).getInstantiatedClass();

        Assertions.assertEquals(instantiatedClass, TestBean.class);
    }

    @Data
    static class TestBean {

        private String name;

    }

}
