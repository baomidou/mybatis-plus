package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.junit.jupiter.api.Test;

/**
 * @author miemie
 * @since 2020-11-07
 */
class MybatisMapperAnnotationBuilderTest {

    @Test
    void parse() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        MybatisMapperAnnotationBuilder a = new MybatisMapperAnnotationBuilder(configuration, AMapper.class);
        a.parse();
        MybatisMapperAnnotationBuilder b = new MybatisMapperAnnotationBuilder(configuration, BMapper.class);
        b.parse();
        configuration.getMappedStatement(AMapper.class.getName() + ".insert");
    }

    @CacheNamespaceRef(BMapper.class)
    interface AMapper extends BaseMapper<A> {

    }

    @CacheNamespace
    interface BMapper extends BaseMapper<B> {

    }

    @Data
    private static class A {
        private Long id;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class B extends A {

    }
}
