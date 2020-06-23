package com.baomidou.mybatisplus.test.pagination;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.BaseDbTest;
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
public class PaginationTest extends BaseDbTest {

    @Test
    void page() {
        doTest(EntityMapper.class, m -> {
            Page<Entity> page = new Page<>(1, 5);
            IPage<Entity> result = m.selectPage(page, null);
            assertThat(page).isEqualTo(result);
            assertThat(result.getTotal()).isEqualTo(2L);
            assertThat(result.getRecords().size()).isEqualTo(2);
        });

        doTest(EntityMapper.class, m -> {
            Page<Entity> page = new Page<>(1, 5);
            IPage<Entity> result = m.selectPage(page, null);
            assertThat(page).isEqualTo(result);
            assertThat(result.getTotal()).isEqualTo(2L);
            assertThat(result.getRecords().size()).isEqualTo(2);
        });
    }

    @Override
    protected List<Interceptor> interceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return Collections.singletonList(interceptor);
    }

    @Override
    protected List<Class<?>> mappers() {
        return Collections.singletonList(EntityMapper.class);
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity(id,name) values(1,'1');\n" +
            "insert into entity(id,name) values(2,'2');";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity",
            "CREATE TABLE IF NOT EXISTS entity (\n" +
                "id BIGINT(20) NOT NULL,\n" +
                "name VARCHAR(30) NULL DEFAULT NULL,\n" +
                "PRIMARY KEY (id)" +
                ")");
    }
}
