package com.baomidou.mybatisplus.generator.query;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultQueryTest {

    private static final String POSTGRESQL_URL = "jdbc:postgresql://localhost/test";

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void includesPostgreSqlPartitionedTable(boolean skipView) throws SQLException {
        StrategyConfig.Builder strategy = strategy(skipView).addInclude("dynamic_record");
        assertEquals(Arrays.asList("dynamic_record"), queryTableNames(POSTGRESQL_URL, strategy));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void respectsSkipViewForPostgreSql(boolean skipView) throws SQLException {
        List<String> expected = new ArrayList<>(Arrays.asList("dynamic_record", "dynamic_record_p0", "ordinary_record"));
        if (!skipView) {
            expected.add("record_view");
        }
        assertEquals(expected, queryTableNames(POSTGRESQL_URL, strategy(skipView)));
    }

    @ParameterizedTest
    @CsvSource({
        "jdbc:mysql://localhost/test, false",
        "jdbc:mysql://localhost/test, true",
        "jdbc:h2:mem:test, false",
        "jdbc:h2:mem:test, true",
        // An external DataSource's JDBC metadata may not provide a URL.
        ", false",
        ", true"
    })
    void preservesOtherDatabaseTableTypes(String url, boolean skipView) throws SQLException {
        List<String> expected = new ArrayList<>(Arrays.asList("dynamic_record_p0", "ordinary_record"));
        if (!skipView) {
            expected.add("record_view");
        }
        assertEquals(expected, queryTableNames(url, strategy(skipView)));
    }

    @Test
    void excludesPartitionChildWithoutExcludingParent() throws SQLException {
        assertEquals(Arrays.asList("dynamic_record", "ordinary_record"),
            queryTableNames(POSTGRESQL_URL, strategy(true).addExclude("dynamic_record_p0")));
    }

    private StrategyConfig.Builder strategy(boolean skipView) {
        StrategyConfig.Builder builder = new StrategyConfig.Builder();
        return skipView ? builder.enableSkipView() : builder;
    }

    private List<String> queryTableNames(String url, StrategyConfig.Builder strategy) throws SQLException {
        Connection connection = mock(Connection.class);
        DatabaseMetaData metadata = mock(DatabaseMetaData.class);
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metadata);
        when(connection.getCatalog()).thenReturn("test");
        when(metadata.getURL()).thenReturn(url);
        when(metadata.getTables(eq("test"), eq("public"), isNull(), any(String[].class)))
            .thenAnswer(invocation -> tables(invocation.getArgument(3)));
        when(metadata.getPrimaryKeys(eq("test"), eq("public"), anyString())).thenAnswer(invocation -> mock(ResultSet.class));
        when(metadata.getColumns(eq("test"), eq("public"), anyString(), eq("%"))).thenAnswer(invocation -> mock(ResultSet.class));
        when(metadata.getIndexInfo(eq("test"), eq("public"), anyString(), eq(false), eq(false)))
            .thenAnswer(invocation -> mock(ResultSet.class));
        DataSourceConfig config = new DataSourceConfig.Builder(dataSource).schema("public").build();
        ConfigBuilder builder = new ConfigBuilder(null, config, strategy.build(), null, null, null);
        return builder.getTableInfoList().stream().map(TableInfo::getName).collect(Collectors.toList());
    }

    private ResultSet tables(String[] types) throws SQLException {
        // Model JDBC's type filtering, including pgjdbc's distinct partition-parent type.
        List<String[]> rows = Arrays.asList(
            new String[]{"dynamic_record", "PARTITIONED TABLE"},
            new String[]{"dynamic_record_p0", "TABLE"},
            new String[]{"ordinary_record", "TABLE"},
            new String[]{"record_view", "VIEW"}
        ).stream().filter(row -> Arrays.asList(types).contains(row[1])).collect(Collectors.toList());
        ResultSet resultSet = mock(ResultSet.class);
        AtomicInteger cursor = new AtomicInteger(-1);
        when(resultSet.next()).thenAnswer(invocation -> cursor.incrementAndGet() < rows.size());
        when(resultSet.getString("TABLE_NAME")).thenAnswer(invocation -> rows.get(cursor.get())[0]);
        when(resultSet.getString("TABLE_TYPE")).thenAnswer(invocation -> rows.get(cursor.get())[1]);
        return resultSet;
    }
}
