package com.baomidou.mybatisplus.test.pom;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 反射工具类测试
 */
public class ReflectionKitTest {

    public class MyEntity {
    }

    public interface Mapper1<T> extends BaseMapper<T> {
    }

    public interface Mapper2 extends Mapper<MyEntity> {
    }

    public interface Mapper3 extends Mapper2 {
    }

    @Test
    void testSuperClassGenericType() {
        // 多重继承测试
        assertThat(ReflectionKit.getSuperClassGenericType(Mapper2.class,
            Mapper.class, 0).equals(MyEntity.class));
        assertThat(ReflectionKit.getSuperClassGenericType(Mapper3.class,
            Mapper.class, 0).equals(MyEntity.class));
    }
}
