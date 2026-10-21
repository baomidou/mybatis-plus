package com.baomidou.mybatisplus.test.record;

import com.baomidou.mybatisplus.test.BaseDbTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2023年9月14日
 */
public class RecordEntityTest extends BaseDbTest<RecordEntityMapper> {

    @Test
    void testInsert() {
        doTest(mapper -> {
            //TODO 无法使用自动填充与主键生成,不建议充当PO使用
            RecordEntity recordEntity = new RecordEntity(123L, "苗人凤", "13388888888");
            assertThat(mapper.insert(recordEntity)).isEqualTo(1);
        });
    }

    @Test
    void testUpdate() {
        doTest(mapper -> {
            RecordEntity recordEntity = new RecordEntity(2L, "苗人凤2", null);
            assertThat(mapper.updateById(recordEntity)).isEqualTo(1);
            recordEntity = mapper.selectById(2L);
            assertThat(recordEntity.name()).isEqualTo("苗人凤2");
            assertThat(recordEntity.phone()).as("13322222222").isNotNull();
        });
    }

    @Test
    void testSelect() {
        doTest(mapper -> {
            RecordEntity recordEntity = mapper.selectById(3L);
            assertThat(recordEntity.name()).isEqualTo("demo3");
            assertThat(recordEntity.phone()).as("13333333333").isNotNull();
        });
    }

    @Test
    void testDelete() {
        doTest(mapper -> assertThat(mapper.deleteById(new RecordEntity(1L, "-----", "1"))).isEqualTo(1));
    }

    @Override
    protected String tableDataSql() {
        return "insert into t_record(id,name,tel) values(1,'demo1','13311111111'),(2,'demo2','13322222222'),(3,'demo3','13333333333');";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList(
                "drop table if exists t_record",
                "CREATE TABLE IF NOT EXISTS t_record (" + "id BIGINT NOT NULL," + "name VARCHAR(30) NULL DEFAULT NULL,"
                        + "tel VARCHAR(30) NULL DEFAULT NULL," + "PRIMARY KEY (id))");
    }

}
