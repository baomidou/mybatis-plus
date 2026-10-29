package com.baomidou.mybatisplus.generator.jdbc;

import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
public class DatabaseMetaDataWrapperTest {

    @Test
    void shouldRetrieveColumnMetadataFromH2Database() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:" + UUID.randomUUID());
                Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE ZZ_METADATA_COLUMNS (USER_NAME VARCHAR(30))");
            DatabaseMetaDataWrapper databaseMetaDataWrapper = new DatabaseMetaDataWrapper(connection, "PUBLIC");
            Map<String, DatabaseMetaDataWrapper.Column> columnsInfo = databaseMetaDataWrapper
                    .getColumnsInfo("ZZ_METADATA_COLUMNS", true);
            assertThat(columnsInfo).containsOnlyKeys("user_name");
            DatabaseMetaDataWrapper.Column name = columnsInfo.get("user_name");
            assertThat(name.isNullable()).isTrue();
            assertThat(name.getJdbcType()).isEqualTo(JdbcType.VARCHAR);
        }
    }

    @Test
    void shouldReadOnlyColumnsOfExactTableName() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:" + UUID.randomUUID());
                Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE ZZ_META_USER (ID INT PRIMARY KEY, OWN_VALUE VARCHAR(20))");
            statement.execute("CREATE TABLE ZZ_METAXUSER (ID VARCHAR(30), FOREIGN_VALUE INT)");
            DatabaseMetaDataWrapper wrapper = new DatabaseMetaDataWrapper(connection, "PUBLIC");
            Map<String, DatabaseMetaDataWrapper.Column> columns = wrapper.getColumnsInfo("ZZ_META_USER", true);
            assertThat(columns).containsOnlyKeys("id", "own_value");
            assertThat(columns.get("id").getJdbcType()).isEqualTo(JdbcType.INTEGER);
            assertThat(columns.get("id").isPrimaryKey()).isTrue();
        }
    }

    @Test
    void shouldEscapeLiteralPercentAndEscapeCharactersInTableName() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:" + UUID.randomUUID());
                Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE \"ZZ%META\\NAME\" (OWN_VALUE INT)");
            statement.execute("CREATE TABLE ZZOTHER_METANAME (FOREIGN_VALUE INT)");
            DatabaseMetaDataWrapper wrapper = new DatabaseMetaDataWrapper(connection, "PUBLIC");
            assertThat(wrapper.getColumnsInfo("ZZ%META\\NAME", false)).containsOnlyKeys("own_value");
        }
    }

    @Test
    void shouldReadOnlyColumnsOfExactSchemaName() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:" + UUID.randomUUID());
                Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA ZZ_META");
            statement.execute("CREATE SCHEMA ZZAMETA");
            statement.execute("CREATE TABLE ZZ_META.RECORDS (OWN_VALUE INT)");
            statement.execute("CREATE TABLE ZZAMETA.RECORDS (FOREIGN_VALUE INT)");
            DatabaseMetaDataWrapper wrapper = new DatabaseMetaDataWrapper(connection, "ZZ_META");
            assertThat(wrapper.getColumnsInfo("RECORDS", false)).containsOnlyKeys("own_value");
        }
    }

    @Test
    void shouldKeepTableDiscoveryPatternsWorking() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:" + UUID.randomUUID());
                Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE ZZ_PATTERN_ONE (ID INT)");
            statement.execute("CREATE TABLE ZZ_PATTERN_TWO (ID INT)");
            DatabaseMetaDataWrapper wrapper = new DatabaseMetaDataWrapper(connection, "PUBLIC");
            assertThat(wrapper.getTables("ZZ_PATTERN_%", new String[]{"TABLE"}))
                    .extracting(DatabaseMetaDataWrapper.Table::getName)
                    .containsExactlyInAnyOrder("ZZ_PATTERN_ONE", "ZZ_PATTERN_TWO");
        }
    }

}
