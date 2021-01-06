package com.baomidou.mybatisplus.test.pagination;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.BaseDbTest;
import lombok.Data;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.plugin.Interceptor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-23
 */
class PaginationTest extends BaseDbTest<EntityMapper> {

    @Test
    void page() {
        Cache cache = sqlSessionFactory.getConfiguration().getCache(EntityMapper.class.getName());
        assertThat(cache).as("使用 @CacheNamespace 指定了使用缓存").isNotNull();

        doTestAutoCommit(m -> {
            Page<Entity> page = new Page<>(1, 5);
            IPage<Entity> result = m.selectPage(page, null);
            assertThat(page).isEqualTo(result);
            assertThat(result.getTotal()).isEqualTo(2L);
            assertThat(result.getRecords().size()).isEqualTo(2);
        });
        assertThat(cache.getSize()).as("一条count缓存一条分页缓存").isEqualTo(2);


        doTestAutoCommit(m -> {
            Page<Entity> page = new Page<>(1, 5);
            IPage<Entity> result = m.selectPage(page, null);
            assertThat(page).isEqualTo(result);
            assertThat(result.getTotal()).isEqualTo(2L);
            assertThat(result.getRecords().size()).isEqualTo(2);
        });
        assertThat(cache.getSize()).as("因为命中缓存了所以还是2条").isEqualTo(2);

        doTestAutoCommit(m -> {
            Page<Entity> page = new Page<>(1, 5);
            page.addOrder(OrderItem.asc("id"));
            IPage<Entity> result = m.selectPage(page, null);
            assertThat(page).isEqualTo(result);
            assertThat(result.getTotal()).isEqualTo(2L);
            assertThat(result.getRecords().size()).isEqualTo(2);
        });
        assertThat(cache.getSize()).as("条件不一样了,缓存变为3条").isEqualTo(3);


        doTestAutoCommit(m -> m.insert(new Entity()));
        assertThat(cache.getSize()).as("update 操作清除了所有缓存").isEqualTo(0);


        doTestAutoCommit(m -> {
            Page<Entity> page = new Page<>(1, 5);
            IPage<Entity> result = m.selectPage(page, null);
            assertThat(page).isEqualTo(result);
            assertThat(result.getTotal()).isEqualTo(3L);
            assertThat(result.getRecords().size()).isEqualTo(3);
        });
        assertThat(cache.getSize()).as("一条count缓存一条分页缓存").isEqualTo(2);

        doTest(i -> {
            Page<?> page = new Page<>(1, 2);
            page.setCountId("otherCount");
            i.otherPage(page, new Tj());
        });
    }

    @Override
    protected List<Interceptor> interceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return Collections.singletonList(interceptor);
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity(id,name) values(1,'1'),(2,'2');";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity", "CREATE TABLE IF NOT EXISTS entity (" +
            "id BIGINT NOT NULL," +
            "name VARCHAR(30) NULL DEFAULT NULL," +
            "PRIMARY KEY (id))");
    }

    @Data
    public static class Tj {
        private boolean name = true;
    }
}
