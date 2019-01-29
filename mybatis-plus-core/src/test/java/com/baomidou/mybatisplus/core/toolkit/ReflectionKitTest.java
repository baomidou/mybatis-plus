package com.baomidou.mybatisplus.core.toolkit;

import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 反射工具类测试
 *
 * @author nieqiuqiu 2019/1/16.
 */
class ReflectionKitTest {

    @Data
    private static class A {

        private transient String test;

        private static String testStatic;

        private String name;

        private Boolean testWrap;

        private boolean testSimple;

    }

    @Data
    private static class B extends A {

        private Integer age;

    }

    @Data
    private static class C extends B {

        private String sex;

    }

    @Test
    void testGetFieldList() {
        List<Field> fieldList = ReflectionKit.getFieldList(C.class);
        Assertions.assertEquals(5, fieldList.size());
    }

    @Test
    void testGetFieldMap() throws NoSuchFieldException {
        Map<String, Field> fieldMap = ReflectionKit.getFieldMap(C.class);
        Assertions.assertEquals(5, fieldMap.size());
        Assertions.assertEquals(fieldMap.get("sex"), C.class.getDeclaredField("sex"));
        Assertions.assertEquals(fieldMap.get("age"), B.class.getDeclaredField("age"));
        Assertions.assertEquals(fieldMap.get("name"), A.class.getDeclaredField("name"));
    }

    @Test
    void testGetMethodCapitalize() throws NoSuchFieldException {
        Field field = C.class.getDeclaredField("sex");
        String getMethod = ReflectionKit.getMethodCapitalize(field, "sex");
        Assertions.assertEquals("getSex", getMethod);
        field = A.class.getDeclaredField("testWrap");
        getMethod = ReflectionKit.getMethodCapitalize(field, "testWrap");
        Assertions.assertEquals("getTestWrap", getMethod);
        field = A.class.getDeclaredField("testSimple");
        getMethod = ReflectionKit.getMethodCapitalize(field, "testSimple");
        Assertions.assertEquals("isTestSimple", getMethod);
    }

    @Test
    void testGetMethodValue() {
        C c = new C();
        c.setSex("女");
        c.setName("妹纸");
        c.setAge(18);
        Assertions.assertEquals(c.getSex(), ReflectionKit.getMethodValue(c.getClass(), c, "sex"));
        Assertions.assertEquals(c.getAge(), ReflectionKit.getMethodValue(c, "age"));
    }
}
