package com.baomidou.mybatisplus.test.version;

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

import lombok.extern.slf4j.Slf4j;

/**
 * @author miemie
 * @author raylax
 * @since 2020-07-04
 */
@Slf4j
public class VersionTest extends BaseDbTest<EntityMapper> {


    @Test
    void testWrapperMode() {
        log.info("[wrapper mode] test");

        doTestAutoCommit(i -> {
            int result = i.update(null, Wrappers.<Entity>update()
                .eq("id", 3)
                .set("version", 1)
            );
            assertThat(result).as("[wrapper mode] 设置version值成功").isEqualTo(1);
        });

        doTestAutoCommit(i -> {
            int result = i.update(null, Wrappers.<Entity>update()
                .eq("id", 3)
                .eq("version", 1)
            );
            assertThat(result).as("[wrapper mode] 设置version值匹配更新成功").isEqualTo(1);
            final Entity entity = i.selectById(3);
            assertThat(entity.getVersion()).isEqualTo(2);
        });

        doTestAutoCommit(i -> {
            int result = i.update(null, Wrappers.<Entity>update()
                .eq("id", 3)
                .eq("version", 1)
            );
            assertThat(result).as("[wrapper mode] 设置version值匹配更新失败").isEqualTo(0);
            final Entity entity = i.selectById(3);
            assertThat(entity.getVersion()).isEqualTo(2);
        });
    }

    @Test
    void test() {
        doTestAutoCommit(i -> {
            int result = i.updateById(new Entity().setId(1L).setName("老张"));
            assertThat(result).as("没放入version值更新成功").isEqualTo(1);
        });

        doTestAutoCommit(i -> {
            int result = i.updateById(new Entity().setId(1L).setName("老张").setVersion(1));
            assertThat(result).as("放入的version值不匹配更新失败").isEqualTo(0);
        });

        doTestAutoCommit(i -> {
            Entity entity = new Entity().setId(1L).setName("老张").setVersion(0);
            int result = i.updateById(entity);
            assertThat(result).as("放入的version值匹配更新成功").isEqualTo(1);
            assertThat(entity.getVersion()).isEqualTo(1);
        });

        doTestAutoCommit(i -> {
            int result = i.update(new Entity().setName("老张"), Wrappers.<Entity>update().eq("id", 2));
            assertThat(result).as("没放入version值更新成功").isEqualTo(1);
        });

        doTestAutoCommit(i -> {
            int result = i.update(new Entity().setName("老张").setVersion(1), Wrappers.<Entity>update().eq("id", 2));
            assertThat(result).as("放入的version值不匹配更新失败").isEqualTo(0);
        });

        doTestAutoCommit(i -> {
            Entity entity = new Entity().setName("老张").setVersion(0);
            int result = i.update(entity, Wrappers.<Entity>update().eq("id", 2));
            assertThat(result).as("放入的version值匹配更新成功").isEqualTo(1);
            assertThat(entity.getVersion()).isEqualTo(1);
        });
    }

    @Override
    protected List<Interceptor> interceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor(true));
        return Collections.singletonList(interceptor);
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity(id,name) values(1,'老王'),(2,'老李'),(3,'老赵')";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity",
            "CREATE TABLE IF NOT EXISTS entity (\n" +
                "id BIGINT(20) NOT NULL,\n" +
                "name VARCHAR(30) NULL DEFAULT NULL,\n" +
                "version integer NOT NULL DEFAULT 0,\n" +
                "PRIMARY KEY (id)" +
                ")");
    }
}
