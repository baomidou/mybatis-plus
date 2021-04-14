package com.baomidou.mybatisplus.test.strategy;

import com.baomidou.mybatisplus.test.BaseDbTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2021-01-27
 */
public class StrategyTest extends BaseDbTest<EntityMapper> {

    @Test
    void test() {
        long id = 1L;
        doTestAutoCommit(i -> {
            Entity entity = new Entity();
            entity.setId(id);
            entity.setName("entity");
            entity.setInsertStr("");
            i.insert(entity);
        });
        doTest(i -> {
            Entity entity = i.selectById(id);
            assertThat(entity).isNotNull();
            assertThat(entity.getInsertStr()).isNull();
            assertThat(entity.getUpdateStr()).isNull();
        });
        doTestAutoCommit(i -> {
            Entity entity = new Entity();
            entity.setId(id);
            entity.setName("entity");
            entity.setUpdateStr("");
            i.updateById(entity);
        });
        doTest(i -> {
            Entity entity = i.selectById(id);
            assertThat(entity).isNotNull();
            assertThat(entity.getInsertStr()).isNull();
            assertThat(entity.getUpdateStr()).isNull();
        });
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity", "CREATE TABLE IF NOT EXISTS entity (" +
            "id BIGINT NOT NULL," +
            "name VARCHAR(30) NULL DEFAULT NULL," +
            "insert_str VARCHAR(30) NULL DEFAULT NULL," +
            "update_str VARCHAR(30) NULL DEFAULT NULL," +
            "PRIMARY KEY (id))");
    }
}
