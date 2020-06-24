package com.baomidou.mybatisplus.test.tenant;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantInnerInterceptor;
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
    }

    @Override
    protected List<Interceptor> interceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new TenantInnerInterceptor(select -> new LongValue(1)));
        return Collections.singletonList(interceptor);
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
