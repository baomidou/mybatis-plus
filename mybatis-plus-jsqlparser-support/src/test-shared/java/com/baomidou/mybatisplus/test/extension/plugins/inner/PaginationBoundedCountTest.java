package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class PaginationBoundedCountTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "SELECT TOP 2 n FROM (VALUES (1),(1),(2),(3)) v(n) WHERE n >= ? ORDER BY n | 2",
            "SELECT n FROM (VALUES (1),(1),(2),(3)) v(n) WHERE n >= ? LIMIT 2 | 2",
            "SELECT n FROM (VALUES (1),(1),(2),(3)) v(n) LIMIT ? | 1",
            "SELECT n FROM (VALUES (1),(1),(2),(3)) v(n) WHERE n >= ? ORDER BY n OFFSET 1 ROW | 3",
            "SELECT n FROM (VALUES (1),(1),(2),(3)) v(n) WHERE n >= ? ORDER BY n FETCH FIRST ROW WITH TIES | 2",
            "SELECT n FROM (VALUES (1),(1),(2),(3)) v(n) WHERE n >= ? LIMIT 0 | 0"})
    void countsRowsOfBoundedQuery(String sql, int expected) throws SQLException {
        String countSql = new PaginationInnerInterceptor().autoCountSql(new Page<>(), sql);
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:");
                PreparedStatement count = connection.prepareStatement(countSql)) {
            count.setInt(1, 1);
            try (ResultSet rows = count.executeQuery()) {
                assertThat(rows.next()).isTrue();
                assertThat(rows.getLong(1)).isEqualTo(expected);
                assertThat(rows.next()).isFalse();
            }
        }
    }
}
