package com.baomidou.mybatisplus.test.non;

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
public class NonTest extends BaseDbTest<EntityMapper> {

    @Test
    void test() {
        doTest(i -> {
            Entity entity = i.selectOne(Wrappers.<Entity>lambdaQuery().eq(Entity::getId, 1));
            assertThat(entity).isNotNull();
            assertThat(entity.getName()).isNotNull();

            entity.setName("老吴");
            int update = i.updateById(entity);
            assertThat(update).isEqualTo(1);
        });
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity(t_id,t_name) values(1,'1'),(2,'2');";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity", "CREATE TABLE IF NOT EXISTS entity (" +
            "t_id BIGINT NOT NULL," +
            "t_name VARCHAR(30) NULL DEFAULT NULL," +
            "PRIMARY KEY (t_id))");
    }
}
