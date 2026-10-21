package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.MappedStatement;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-11-07
 */
class MybatisMapperAnnotationBuilderTest {

    @Test
    void parse() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        // AMapper 先解析，它 @CacheNamespaceRef 指向的 BMapper 此时还不存在，只能挂成待处理引用
        new MybatisMapperAnnotationBuilder(configuration, AMapper.class).parse();
        new MybatisMapperAnnotationBuilder(configuration, BMapper.class).parse();

        assertThat(configuration.hasStatement(AMapper.class.getName() + ".insert")).isTrue();
        assertThat(configuration.hasStatement(BMapper.class.getName() + ".insert")).isTrue();
        // @CacheNamespace 注册的名字就是命名空间本身，引用方不会拿到自己的缓存
        assertThat(configuration.hasCache(BMapper.class.getName())).isTrue();
        assertThat(configuration.hasCache(AMapper.class.getName())).isFalse();
        MappedStatement aInsert = configuration.getMappedStatement(AMapper.class.getName() + ".insert");
        MappedStatement bInsert = configuration.getMappedStatement(BMapper.class.getName() + ".insert");
        // 取语句时补解析才把引用接上：两边共用 BMapper 的那个缓存
        assertThat(aInsert.getCache()).isSameAs(bInsert.getCache());
        assertThat(aInsert.getCache().getId()).isEqualTo(BMapper.class.getName());
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

    @Test
    void genericReturnTypesComeFromTheParsedResultMap() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        new MybatisMapperAnnotationBuilder(configuration, RowMapper.class).parse();

        assertThat(resultType(configuration, "one")).isEqualTo(Row.class);
        assertThat(resultType(configuration, "list")).isEqualTo(Row.class);
        // 支援 IPage 及其子类作为返回值：取泛型参数，Map 这种嵌套泛型取原始类型
        assertThat(resultType(configuration, "rowPage")).isEqualTo(Row.class);
        assertThat(resultType(configuration, "mapPage")).isEqualTo(Map.class);
        // 没有 @MapKey 的 Map 返回值不拆开，带 @MapKey 的取 value 类型
        assertThat(resultType(configuration, "map")).isEqualTo(Map.class);
        assertThat(resultType(configuration, "keyedMap")).isEqualTo(Object.class);
        assertThat(resultType(configuration, "optional")).isEqualTo(Row.class);
    }

    private Class<?> resultType(MybatisConfiguration configuration, String method) {
        return configuration.getMappedStatement(RowMapper.class.getName() + '.' + method).getResultMaps().get(0)
                .getType();
    }

    interface RowMapper {

        @Select("select id from zz_return_type")
        Row one();

        @Select("select id from zz_return_type")
        List<Row> list();

        @Select("select id from zz_return_type")
        IPage<Row> rowPage();

        @Select("select id from zz_return_type")
        IPage<Map<String, Object>> mapPage();

        @Select("select id from zz_return_type")
        Map<String, Object> map();

        @MapKey("id")
        @Select("select id from zz_return_type")
        Map<String, Object> keyedMap();

        @Select("select id from zz_return_type")
        Optional<Row> optional();
    }

    static class Row {

        private Long id;
    }
}
