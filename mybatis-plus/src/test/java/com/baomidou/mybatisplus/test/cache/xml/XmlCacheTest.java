package com.baomidou.mybatisplus.test.cache.xml;

import com.baomidou.mybatisplus.test.BaseDbTest;
import org.apache.ibatis.cache.Cache;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-23
 */
class XmlCacheTest extends BaseDbTest<XmlCacheMapper> {

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
        return "insert into xml_cache(id,name) values(1,'1'),(2,'2'),(3,'3'),(4,'4'),(5,'5');";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists xml_cache", "CREATE TABLE IF NOT EXISTS xml_cache (" +
            "id BIGINT NOT NULL," +
            "name VARCHAR(30) NULL DEFAULT NULL," +
            "PRIMARY KEY (id))");
    }
}
