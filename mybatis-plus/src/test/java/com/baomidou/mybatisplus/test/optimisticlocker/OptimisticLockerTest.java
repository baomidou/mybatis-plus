package com.baomidou.mybatisplus.test.optimisticlocker;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.test.BaseDbTest;
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
public class OptimisticLockerTest extends BaseDbTest<EntityMapper> {

    @Test
    void test() {
        doTestAutoCommit(m -> {
            Entity entity = new Entity().setId(1L).setName("老王");
            int updateById = m.updateById(entity);
            assertThat(updateById).as("更新成功").isEqualTo(1);

            entity = m.selectById(1L);
            assertThat(entity.getVersion()).as("因为没给version赋值就没走插件").isEqualTo(0);

            updateById = m.updateById(entity);
            assertThat(updateById).as("更新成功").isEqualTo(1);

            entity = m.selectById(1L);
            assertThat(entity.getVersion()).as("因为version有值就走了插件").isEqualTo(1);
        });


        doTestAutoCommit(m -> {
            int update = m.update(new Entity().setName("老王"), Wrappers.<Entity>query().eq("id", 2));
            assertThat(update).as("更新成功").isEqualTo(1);

            Entity entity = m.selectById(2L);
            assertThat(entity.getVersion()).as("因为没给version赋值就没走插件").isEqualTo(0);

            update = m.update(new Entity().setName("老王").setVersion(0), Wrappers.<Entity>query().eq("id", 2));
            assertThat(update).as("更新成功").isEqualTo(1);

            entity = m.selectById(2L);
            assertThat(entity.getVersion()).as("因为version有值就走了插件").isEqualTo(1);
        });
    }

    @Override
    protected List<Interceptor> interceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return Collections.singletonList(interceptor);
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity (id,name) values (1,'1'),(2,'2'),(3,'3')";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity",
            "CREATE TABLE IF NOT EXISTS entity (\n" +
                "id BIGINT(20) NOT NULL,\n" +
                "name VARCHAR(30) NULL DEFAULT NULL,\n" +
                "version integer not NULL default 0,\n" +
                "PRIMARY KEY (id)" +
                ")");
    }
}
