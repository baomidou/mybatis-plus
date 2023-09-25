package com.baomidou.mybatisplus.core.toolkit.reflect;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Create by hcl at 2023/9/25
 */
class TypeParameterResolverTest {

    static class CA<E> {
    }

    static class CB<A, B> extends CA<B> {
    }

    static class MyEntity extends CB<String, Number> {
    }

    interface Mapper1<T> extends BaseMapper<T> {
    }

    interface Mapper2 extends Mapper<MyEntity> {
    }

    interface Mapper3 extends Mapper2 {
    }

    interface Mapper4<A, B, C> extends Mapper<B> {
    }

    interface Mapper5 extends Mapper4<Number, MyEntity, Boolean> {
    }

    @Test
    void test() {
        assertSame(TypeParameterResolver.resolveClassIndexedParameter(Mapper1.class, Mapper.class, 0), null);
        assertSame(TypeParameterResolver.resolveClassIndexedParameter(Mapper2.class, Mapper.class, 0), MyEntity.class);
        assertSame(TypeParameterResolver.resolveClassIndexedParameter(Mapper3.class, Mapper.class, 0), MyEntity.class);
        assertSame(TypeParameterResolver.resolveClassIndexedParameter(Mapper5.class, Mapper.class, 0), MyEntity.class);

        assertSame(TypeParameterResolver.resolveClassIndexedParameter(MyEntity.class, CA.class, 0), Number.class);
        assertSame(TypeParameterResolver.resolveClassIndexedParameter(MyEntity.class, CB.class, 1), Number.class);
    }

}