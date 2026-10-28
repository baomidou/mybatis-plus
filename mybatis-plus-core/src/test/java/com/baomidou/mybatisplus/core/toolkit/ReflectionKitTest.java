package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 反射工具类测试
 *
 * @author nieqiuqiu 2019/1/16.
 */
@Tag("unit")
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
        assertThat(fieldList.size()).isEqualTo(5);
    }

    @Test
    void testGetFieldMap() throws NoSuchFieldException {
        Map<String, Field> fieldMap = ReflectionKit.getFieldMap(C.class);
        assertThat(fieldMap.size()).isEqualTo(5);
        assertThat(fieldMap.get("sex")).isEqualTo(C.class.getDeclaredField("sex"));
        assertThat(fieldMap.get("age")).isEqualTo(B.class.getDeclaredField("age"));
        assertThat(fieldMap.get("name")).isEqualTo(A.class.getDeclaredField("name"));
    }

    @Test
    void testGetFieldValue() {
        C c = new C();
        c.setSex("女");
        c.setName("妹纸");
        c.setAge(18);
        assertThat(ReflectionKit.getFieldValue(c, "sex")).isEqualTo(c.getSex());
        assertThat(ReflectionKit.getFieldValue(c, "age")).isEqualTo(c.getAge());

        EntityByLombok entityByLombok = new EntityByLombok();
        entityByLombok.setPId("6666");
        entityByLombok.setParentId("123");
        assertThat(ReflectionKit.getFieldValue(entityByLombok, "pId")).isEqualTo(entityByLombok.getPId());
        assertThat(ReflectionKit.getFieldValue(entityByLombok, "parentId")).isEqualTo(entityByLombok.getParentId());

        Entity entity = new Entity();
        entity.setpId("6666");
        entity.setParentId("123");
        assertThat(ReflectionKit.getFieldValue(entity, "parentId")).isEqualTo(entity.getParentId());
        assertThat(ReflectionKit.getFieldValue(entity, "pId")).isEqualTo(entity.getpId());
    }

    @Test
    void resolvesGenericTypeThroughSeveralLevels() {
        assertThat(ReflectionKit.getSuperClassGenericType(Mapper2.class, Mapper.class, 0)).isEqualTo(MyEntity.class);
        assertThat(ReflectionKit.getSuperClassGenericType(Mapper3.class, Mapper.class, 0)).isEqualTo(MyEntity.class);
    }

    static class MyEntity {
    }

    interface Mapper2 extends Mapper<MyEntity> {
    }

    interface Mapper3 extends Mapper2 {
    }
}
