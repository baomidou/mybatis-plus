package com.baomidou.mybatisplus.core.toolkit;

import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 反射工具类测试
 *
 * @author nieqiuqiu 2019/1/16.
 */
public class ReflectionKitTest {
    
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
    public void testGetFieldList() {
        List<Field> fieldList = ReflectionKit.getFieldList(C.class);
        Assert.assertEquals(5, fieldList.size());
    }
    
    @Test
    public void testGetFieldMap() throws NoSuchFieldException {
        Map<String, Field> fieldMap = ReflectionKit.getFieldMap(C.class);
        Assert.assertEquals(5, fieldMap.size());
        Assert.assertEquals(fieldMap.get("sex"), C.class.getDeclaredField("sex"));
        Assert.assertEquals(fieldMap.get("age"), B.class.getDeclaredField("age"));
        Assert.assertEquals(fieldMap.get("name"), A.class.getDeclaredField("name"));
    }
    
    @Test
    public void testGetMethodCapitalize() throws NoSuchFieldException {
        Field field = C.class.getDeclaredField("sex");
        String getMethod = ReflectionKit.getMethodCapitalize(field, "sex");
        Assert.assertEquals("getSex", getMethod);
        field = A.class.getDeclaredField("testWrap");
        getMethod = ReflectionKit.getMethodCapitalize(field, "testWrap");
        Assert.assertEquals("getTestWrap", getMethod);
        field = A.class.getDeclaredField("testSimple");
        getMethod = ReflectionKit.getMethodCapitalize(field, "testSimple");
        Assert.assertEquals("isTestSimple", getMethod);
    }
    
    @Test
    public void testGetMethodValue() {
        C c = new C();
        c.setSex("女");
        c.setName("妹纸");
        c.setAge(18);
        Assert.assertEquals(c.getSex(), ReflectionKit.getMethodValue(c.getClass(), c, "sex"));
        Assert.assertEquals(c.getAge(), ReflectionKit.getMethodValue(c, "age"));
    }
}
