package com.baomidou.mybatisplus.test.tenant;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.BaseDbTest;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.plugin.Interceptor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-24
 */
public class TenantTest extends BaseDbTest<EntityMapper> {

    @Test
    void test() {
        Cache cache = sqlSessionFactory.getConfiguration().getCache(EntityMapper.class.getName());
        assertThat(cache).as("使用 @CacheNamespace 指定了使用缓存").isNotNull();


        Long id = 1L;
        doTestAutoCommit(m -> {
            int insert = m.insert(new Entity().setId(id));
            assertThat(insert).as("插入成功").isEqualTo(1);
        });


        doTest(m -> {
            Entity entity = m.selectById(id);
            assertThat(entity).as("插入成功").isNotNull();
            assertThat(entity.getTenantId()).as("有租户信息").isEqualTo(1);
        });
        assertThat(cache.getSize()).as("有一条缓存").isEqualTo(1);


        doTest(m -> {
            Entity entity = m.selectById(id);
            assertThat(entity).as("插入成功").isNotNull();
            assertThat(entity.getTenantId()).as("有租户信息").isEqualTo(1);
        });
        assertThat(cache.getSize()).as("依然只有一条缓存,命中了缓存").isEqualTo(1);


        doTestAutoCommit(m -> {
            int delete = m.deleteById(id);
            assertThat(delete).as("删除成功").isEqualTo(1);
        });
        assertThat(cache.getSize()).as("update操作清空了缓存").isEqualTo(0);


        doTestAutoCommit(m -> {
            int insert = m.insert(new Entity().setId(id).setTenantId(2));
            assertThat(insert).as("故意插入一个其他租户的信息,插入成功").isEqualTo(1);
        });


        doTest(m -> {
            Entity entity = m.selectById(id);
            assertThat(entity).as("搜索不到数据").isNull();
        });
        assertThat(cache.getSize()).as("缓存了个寂寞").isEqualTo(1);


        doTest(m -> {
            Entity entity = m.selectById(id);
            assertThat(entity).as("搜索不到数据").isNull();
        });
        assertThat(cache.getSize()).as("依然缓存了个寂寞,说明命中的缓存").isEqualTo(1);

        doTest(m -> {
            Page<Entity> page = m.selectPage(new Page<>(), null);
            assertThat(page.getTotal()).as("count 正常").isEqualTo(0);
        });
    }

    @Override
    protected List<Interceptor> interceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(() -> new LongValue(1)));
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.H2));
        return Collections.singletonList(interceptor);
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity values(1111,'娇妹',3)";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity",
            "CREATE TABLE IF NOT EXISTS entity (\n" +
                "id BIGINT(20) NOT NULL,\n" +
                "name VARCHAR(30) NULL DEFAULT NULL,\n" +
                "tenant_id integer not NULL,\n" +
                "PRIMARY KEY (id)" +
                ")");
    }
}
