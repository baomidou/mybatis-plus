/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

        @SuppressWarnings("unused")
        private static String testStatic;

        private String name;

        private Boolean testWrap;

        private boolean testSimple;

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class B extends A {

        private Integer age;

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class C extends B {

        private String sex;

    }

    @Data
    private static class EntityByLombok {

        private Long id;

        private String pId;

        private String parentId;

    }

    private static class Entity {

        private Long id;

        private String pId;

        private String parentId;


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getpId() {
            return pId;
        }

        public void setpId(String pId) {
            this.pId = pId;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }
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
    void testGetFieldValue() {
        C c = new C();
        c.setSex("女");
        c.setName("妹纸");
        c.setAge(18);
        Assertions.assertEquals(c.getSex(), ReflectionKit.getFieldValue(c, "sex"));
        Assertions.assertEquals(c.getAge(), ReflectionKit.getFieldValue(c, "age"));

        EntityByLombok entityByLombok = new EntityByLombok();
        entityByLombok.setPId("6666");
        entityByLombok.setParentId("123");
        Assertions.assertEquals(entityByLombok.getPId(), ReflectionKit.getFieldValue(entityByLombok, "pId"));
        Assertions.assertEquals(entityByLombok.getParentId(), ReflectionKit.getFieldValue(entityByLombok, "parentId"));

        Entity entity = new Entity();
        entity.setpId("6666");
        entity.setParentId("123");
        Assertions.assertEquals(entity.getParentId(), ReflectionKit.getFieldValue(entity, "parentId"));
        Assertions.assertEquals(entity.getpId(), ReflectionKit.getFieldValue(entity, "pId"));
    }

    @Test
    void testIsPrimitiveOrWrapper() {
        Assertions.assertFalse(ReflectionKit.isPrimitiveOrWrapper(String.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(Boolean.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(boolean.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(byte.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(Byte.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(char.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(Character.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(char.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(Character.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(double.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(Double.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(float.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(Float.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(int.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(Integer.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(long.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(Long.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(short.class));
        Assertions.assertTrue(ReflectionKit.isPrimitiveOrWrapper(Short.class));
    }
}
