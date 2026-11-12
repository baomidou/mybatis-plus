package com.baomidou.mybatisplus.test.conditions;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.DbTestSupport;
import lombok.Data;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class StandaloneHavingTest extends DbTestSupport<StandaloneHavingTest.HavingMapper> {

    @Test
    void aggregateThresholdFiltersWithoutOtherClauses() {
        doTest(mapper -> {
            assertThat(
                    mapper.selectMaps(
                            new QueryWrapper<HavingRecord>().select("COUNT(*) AS record_count")
                                    .having("COUNT(*) > {0}", 2)))
                    .isEmpty();
            assertThat(
                    mapper.selectObjs(new QueryWrapper<HavingRecord>().select("COUNT(*)").having("COUNT(*) >= {0}", 2)))
                    .containsExactly(2L);
        });
    }

    @Test
    void emptyTableDoesNotReturnAnUnqualifiedAggregate() {
        jdbcTemplate.update("DELETE FROM zz_standalone_having");
        doTest(
                mapper -> assertThat(
                        mapper.selectMaps(
                                new QueryWrapper<HavingRecord>().select("COUNT(*) AS record_count")
                                        .having("COUNT(*) > {0}", 0)))
                        .isEmpty());
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList(
                "DROP TABLE IF EXISTS zz_standalone_having",
                "CREATE TABLE zz_standalone_having (id BIGINT PRIMARY KEY)",
                "INSERT INTO zz_standalone_having VALUES (1), (2)");
    }

    interface HavingMapper extends BaseMapper<HavingRecord> {
    }

    @Data
    @TableName("zz_standalone_having")
    static class HavingRecord {

        private Long id;
    }
}
