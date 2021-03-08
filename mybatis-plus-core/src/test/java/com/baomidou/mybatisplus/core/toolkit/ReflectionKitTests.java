package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author lichangfeng
 * @since 2021-03-08
 */
public class ReflectionKitTests {
    public static class MyEntity{}
    public static interface Mapper1 extends BaseMapper<MyEntity>{}
    public static interface Mapper2 extends Mapper1{}
    @Test
    void testGetGenericInterfaces() {
        assertThat(ReflectionKit.getGenericInterfaces(Mapper2.class, Mapper.class, 0).equals(MyEntity.class));
    }
}
