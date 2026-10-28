package com.baomidou.mybatisplus.core.toolkit.reflect;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Create by hcl at 2023/9/25
 */
@Tag("unit")
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
        assertThat(TypeParameterResolver.resolveClassIndexedParameter(Mapper1.class, Mapper.class, 0)).isNull();
        assertThat(TypeParameterResolver.resolveClassIndexedParameter(Mapper2.class, Mapper.class, 0))
                .isSameAs(MyEntity.class);
        assertThat(TypeParameterResolver.resolveClassIndexedParameter(Mapper3.class, Mapper.class, 0))
                .isSameAs(MyEntity.class);
        assertThat(TypeParameterResolver.resolveClassIndexedParameter(Mapper5.class, Mapper.class, 0))
                .isSameAs(MyEntity.class);

        assertThat(TypeParameterResolver.resolveClassIndexedParameter(MyEntity.class, CA.class, 0))
                .isSameAs(Number.class);
        assertThat(TypeParameterResolver.resolveClassIndexedParameter(MyEntity.class, CB.class, 1))
                .isSameAs(Number.class);
    }

}
