package com.baomidou.mybatisplus.test.autoresultmap;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.test.BaseDbTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-23
 */
public class AutoResultMapTest extends BaseDbTest<EntityMapper> {

    @Test
    void test() {
        doTestAutoCommit(m -> m.insert(new Entity().setName("老王").setGg(new Entity.Gg("老王"))));
        doTest(m -> {
            Entity entity = m.selectOne(null);
            assertThat(entity).as("插入正常").isNotNull();
            assertThat(entity.getName()).as("名称不一致正常").isNotNull();
            assertThat(entity.getGg()).as("typeHandler正常").isNotNull();
            assertThat(entity.getGg().getName()).as("是老王").isEqualTo("老王");
        });
        doTest(m -> {
            Entity entity = new Entity().setName("老王");
            m.selectOne(Wrappers.lambdaQuery(entity).ne(Entity::getId, 1));
        });
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity",
            "CREATE TABLE IF NOT EXISTS entity (\n" +
                "id BIGINT(20) NOT NULL,\n" +
                "x_name VARCHAR(20) NOT NULL,\n" +
                "gg VARCHAR(255) NULL DEFAULT NULL,\n" +
                "PRIMARY KEY (id)" +
                ")");
    }
}
