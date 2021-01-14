package com.baomidou.mybatisplus.test.batch;

import com.baomidou.mybatisplus.test.BaseDbTest;
import org.apache.ibatis.executor.BatchExecutor;
import org.apache.ibatis.session.ExecutorType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2021-01-06
 */
class BatchTest extends BaseDbTest<EntityMapper> {

    @Test
    void save() {
        doTestAutoCommit(sqlSession(ExecutorType.BATCH), i -> {
            int i1 = i.insert(new Entity("老王"));
            assertThat(i1).isEqualTo(BatchExecutor.BATCH_UPDATE_RETURN_VALUE);
            int i2 = i.insert(new Entity("老李"));
            assertThat(i2).isEqualTo(BatchExecutor.BATCH_UPDATE_RETURN_VALUE);
        });

        doTest(i -> {
            assertThat(i.selectCount(null)).isEqualTo(2);
        });
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity", "CREATE TABLE IF NOT EXISTS entity (" +
            "id BIGINT NOT NULL," +
            "name VARCHAR(30) NULL DEFAULT NULL," +
            "PRIMARY KEY (id))");
    }
}
