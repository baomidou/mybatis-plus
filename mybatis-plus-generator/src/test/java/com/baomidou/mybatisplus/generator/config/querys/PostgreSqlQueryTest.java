package com.baomidou.mybatisplus.generator.config.querys;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("external-db")
@EnabledIfEnvironmentVariable(named = "MP_POSTGRES_TEST_URL", matches = ".+")
class PostgreSqlQueryTest {

    @Test
    void onlyMarksPrimaryKeyColumns() throws SQLException {
        try (Connection connection = DriverManager.getConnection(System.getenv("MP_POSTGRES_TEST_URL"));
                Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TEMP TABLE indexed_item (tag text, code int, tenant int, id int, PRIMARY KEY (tenant, id) INCLUDE (tag))");
            statement.execute("CREATE INDEX ON indexed_item (tag)");
            statement.execute("CREATE UNIQUE INDEX ON indexed_item (code)");
            statement.execute("CREATE INDEX ON indexed_item (code, tag)");

            String schema;
            try (ResultSet rows = statement.executeQuery("SELECT pg_my_temp_schema()::regnamespace::text")) {
                rows.next();
                schema = rows.getString(1);
            }
            PostgreSqlQuery query = new PostgreSqlQuery();
            String sql = String.format(query.tableFieldsSql(), "indexed_item", "indexed_item", "indexed_item", schema);
            List<String> fields = new ArrayList<>();
            List<String> keys = new ArrayList<>();
            try (ResultSet rows = statement.executeQuery(sql)) {
                while (rows.next()) {
                    String field = rows.getString(query.fieldName());
                    fields.add(field);
                    if ("PRI".equals(rows.getString(query.fieldKey()))) {
                        keys.add(field);
                    }
                }
            }
            assertThat(fields).containsExactly("tag", "code", "tenant", "id");
            assertThat(keys).containsExactly("tenant", "id");
        }
    }
}
