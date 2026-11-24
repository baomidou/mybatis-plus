package com.baomidou.mybatisplus.test.cache;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.DbTestSupport;
import lombok.Data;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.cache.Cache;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-23
 */
@Tag("integration")
class XmlCacheTest extends DbTestSupport<XmlCacheTest.XmlCacheMapper> {

    @Test
    void page() {
        Cache cache = sqlSessionFactory.getConfiguration().getCache(XmlCacheMapper.class.getName());
        assertThat(cache).as("使用 xml-cache 指定了使用缓存").isNotNull();
        final long total = 5;
        doTestAutoCommit(m -> {
            List<XmlCache> result = m.selectList(null);
            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(total);
        });
        assertThat(cache.getSize()).as("一条缓存").isEqualTo(1);
    }

    @Override
    protected String tableDataSql() {
        return """
                insert into xml_cache(id,name) values
                    (1,'1'), (2,'2'), (3,'3'), (4,'4'), (5,'5');
                """;
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists xml_cache", """
                CREATE TABLE IF NOT EXISTS xml_cache (
                    id BIGINT NOT NULL,
                    name VARCHAR(30) NULL DEFAULT NULL,
                    PRIMARY KEY (id)
                )
                """);
    }

    @CacheNamespaceRef(XmlCacheMapper.class)
    public interface XmlCacheMapper extends BaseMapper<XmlCache> {
    }

    @Data
    public static class XmlCache implements Serializable {

        private Long id;
        private String name;
    }
}
