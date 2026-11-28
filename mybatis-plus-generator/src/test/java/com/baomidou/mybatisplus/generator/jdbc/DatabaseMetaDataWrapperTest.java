package com.baomidou.mybatisplus.generator.jdbc;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
public class DatabaseMetaDataWrapperTest {

    @Test
    void skipsTheWholeIndexIfAnyKeyPartIsAnExpression() throws SQLException {
        Connection connection = mock(Connection.class);
        DatabaseMetaData metadata = mock(DatabaseMetaData.class);
        ResultSet rows = mock(ResultSet.class);
        when(connection.getMetaData()).thenReturn(metadata);
        when(metadata.getIndexInfo(null, null, "account", false, true)).thenReturn(rows);
        when(rows.next()).thenReturn(true, true, true, true, true, false);
        when(rows.getString("INDEX_NAME")).thenReturn("email_key", "email_key", "code_key", "name_key", "name_key");
        when(rows.getString("COLUMN_NAME")).thenReturn("tenant_id", null, "code", null, "tenant_id");

        var indexes = new DatabaseMetaDataWrapper(connection, null).getIndex("account");
        assertThat(indexes).extracting(DatabaseMetaDataWrapper.Index::getColumnName).containsExactly("code");
    }

    @Test
    void readsCompositeIndexWithoutRequestingExactStatistics() throws SQLException {
        try (var connection = DriverManager.getConnection("jdbc:h2:mem:index_statistics");
                var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE shipment (warehouse INT, tracking_code VARCHAR(30))");
            statement.execute("CREATE UNIQUE INDEX uq_shipment ON shipment(warehouse, tracking_code)");
            var metadata = spy(connection.getMetaData());
            var wrapped = mock(Connection.class);
            when(wrapped.getMetaData()).thenReturn(metadata);
            when(wrapped.getCatalog()).thenReturn(connection.getCatalog());

            var indexes = new DatabaseMetaDataWrapper(wrapped, "PUBLIC").getIndex("SHIPMENT");

            verify(metadata).getIndexInfo(connection.getCatalog(), "PUBLIC", "SHIPMENT", false, true);
            assertThat(indexes).extracting(DatabaseMetaDataWrapper.Index::getColumnName)
                    .containsExactly("WAREHOUSE", "TRACKING_CODE");
            assertThat(indexes).allMatch(DatabaseMetaDataWrapper.Index::isUnique);
        }
    }

    @Test
    void shouldRetrieveColumnMetadataFromH2Database() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(
                "jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "").build();
        DatabaseMetaDataWrapper databaseMetaDataWrapper = new DatabaseMetaDataWrapper(dataSourceConfig.getConn(),
                dataSourceConfig.getSchemaName());
        Map<String, DatabaseMetaDataWrapper.Column> columnsInfo = databaseMetaDataWrapper
                .getColumnsInfo(null, null, "USERS", true);
        assertThat(columnsInfo).isNotNull();
        DatabaseMetaDataWrapper.Column name = columnsInfo.get("user_name");
        assertThat(name.isNullable()).isTrue();
        assertThat(name.getJdbcType()).isEqualTo(JdbcType.VARCHAR);
    }

}
