package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class Hive2DialectTest {

    @ParameterizedTest
    @CsvSource({"0, 2, 'LIMIT 2'", "2, 2, 'LIMIT 2,2'", "0, 0, 'LIMIT 0'"})
    void appendsHiveLimitToOrderedProjection(long offset, long limit, String clause) {
        String sql = "SELECT value FROM events WHERE category = ? ORDER BY event_id";
        assertThat(new Hive2Dialect().buildPaginationSql(sql, offset, limit).getDialectSql())
                .isEqualTo(sql + " " + clause);
    }
}
