package com.baomidou.mybatisplus.test.logicdel;

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
public class LogicDelTest extends BaseDbTest<EntityMapper> {

    @Test
    void logicDel() {
        doTestAutoCommit(i -> {
            int delete = i.deleteById(1);
            assertThat(delete).isEqualTo(1);

            delete = i.delete(Wrappers.<Entity>lambdaQuery().eq(Entity::getId, 2));
            assertThat(delete).isEqualTo(1);
        });

        doTest(i -> {
            Entity entity = i.byId(1L);
            assertThat(entity).isNotNull();
            assertThat(entity.getDeleted()).isTrue();

            entity = i.byId(2L);
            assertThat(entity).isNotNull();
            assertThat(entity.getDeleted()).isTrue();
        });
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
            "deleted BOOLEAN NOT NULL DEFAULT false," +
            "PRIMARY KEY (id))");
    }
}
