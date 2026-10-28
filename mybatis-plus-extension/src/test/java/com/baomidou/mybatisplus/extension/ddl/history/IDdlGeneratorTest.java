package com.baomidou.mybatisplus.extension.ddl.history;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 四个方言 DDL 生成器的 SQL 文本闸门，改模板或改表名都会红
 */
@Tag("unit")
class IDdlGeneratorTest {

    @Test
    void defaultContractKeysOffTheDefaultTableName() {
        IDdlGenerator generator = new IDdlGenerator() {

            @Override
            public String createDdlHistory() {
                return "not used";
            }
        };
        assertThat(generator.getDdlHistory()).isEqualTo("ddl_history");
        assertThat(generator.selectDdlHistory("V1__init.sql", "sql"))
                .isEqualTo("SELECT version FROM ddl_history WHERE script='V1__init.sql' AND type='sql'");
        assertThat(generator.insertDdlHistory("V1__init.sql", "sql", "1"))
                .isEqualTo("INSERT INTO ddl_history(script,type,version) VALUES ('V1__init.sql','sql','1')");
        assertThat(existSql(generator, "any")).isNull();
        assertThat(generator.existTable("any", sql -> true)).isFalse();
    }

    @Test
    void mysqlRendersItsOwnCreateAndExistenceSql() {
        IDdlGenerator generator = MysqlDdlGenerator.newInstance();
        assertThat(generator).isInstanceOf(MysqlDdlGenerator.class);
        assertThat(generator.getDdlHistory()).isEqualTo("ddl_history");
        assertThat(generator.createDdlHistory()).isEqualTo(
                "CREATE TABLE IF NOT EXISTS `ddl_history` (`script` varchar(500) NOT NULL COMMENT '脚本',"
                        + "`type` varchar(30) NOT NULL COMMENT '类型'," + "`version` varchar(30) NOT NULL COMMENT '版本',"
                        + "PRIMARY KEY (`script`)) COMMENT = 'DDL 版本';");
        assertThat(existSql(generator, "testdb")).isEqualTo(
                "SELECT COUNT(1) AS NUM from INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='ddl_history'"
                        + " AND TABLE_TYPE='BASE TABLE' AND TABLE_SCHEMA='testdb'");
        assertThat(existSql(generator, null)).isEqualTo(
                "SELECT COUNT(1) AS NUM from INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='ddl_history'"
                        + " AND TABLE_TYPE='BASE TABLE'");
        assertThat(generator.existTable("testdb", sql -> false)).isFalse();
    }

    @Test
    void oracleUsesUppercaseTableAndOptionalSchema() {
        IDdlGenerator generator = OracleDdlGenerator.newInstance();
        assertThat(generator.getDdlHistory()).isEqualTo("DDL_HISTORY");
        assertThat(generator.createDdlHistory()).isEqualTo(
                "CREATE TABLE DDL_HISTORY(script NVARCHAR2(500) NOT NULL,type NVARCHAR2(30) NOT NULL,"
                        + "version NVARCHAR2(30) NOT NULL);");
        assertThat(existSql(generator, null))
                .isEqualTo("SELECT COUNT(1) AS NUM FROM user_tables WHERE table_name='DDL_HISTORY'");

        IDdlGenerator withSchema = new OracleDdlGenerator("test");
        assertThat(withSchema.getDdlHistory()).isEqualTo("test.DDL_HISTORY");
        assertThat(withSchema.createDdlHistory()).isEqualTo(
                "CREATE TABLE test.DDL_HISTORY(script NVARCHAR2(500) NOT NULL,type NVARCHAR2(30) NOT NULL,"
                        + "version NVARCHAR2(30) NOT NULL);");
        assertThat(existSql(withSchema, null))
                .isEqualTo("SELECT COUNT(1) AS NUM FROM user_tables WHERE table_name='test.DDL_HISTORY'");
    }

    @Test
    void postgresQuotesSchemaAndEmitsColumnComments() {
        IDdlGenerator generator = PostgreDdlGenerator.newInstance();
        assertThat(generator.getDdlHistory()).isEqualTo("\"public\".\"ddl_history\"");
        assertThat(generator.createDdlHistory()).isEqualTo(
                "CREATE TABLE IF NOT EXISTS \"public\".\"ddl_history\" (" + "\"script\" varchar(500) NOT NULL,"
                        + "\"type\" varchar(30) NOT NULL," + "\"version\" varchar(30) NOT NULL);"
                        + "COMMENT ON COLUMN \"public\".\"ddl_history\".\"script\" IS '脚本';"
                        + "COMMENT ON COLUMN \"public\".\"ddl_history\".\"type\" IS '类型';"
                        + "COMMENT ON COLUMN \"public\".\"ddl_history\".\"version\" IS '版本';"
                        + "COMMENT ON TABLE \"public\".\"ddl_history\" IS 'DDL 版本';");
    }

    @Test
    void postgresAutoSchemaDropsTheQualifier() {
        IDdlGenerator generator = PostgreDdlGenerator.newInstanceWithAutoSchema();
        assertThat(generator.getDdlHistory()).isEqualTo("\"ddl_history\"");
        assertThat(generator.createDdlHistory()).startsWith("CREATE TABLE IF NOT EXISTS \"ddl_history\" (");
    }

    @Test
    void postgresExistenceSqlFollowsTheConfiguredSchema() {
        assertThat(existSql(PostgreDdlGenerator.newInstance(), null)).isEqualTo(
                "SELECT COUNT(1) AS NUM from INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='ddl_history'"
                        + " AND TABLE_TYPE='BASE TABLE' AND TABLE_SCHEMA='public'");
        assertThat(existSql(PostgreDdlGenerator.newInstanceWithAutoSchema(), null)).isEqualTo(
                "SELECT COUNT(1) AS NUM from INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='ddl_history'"
                        + " AND TABLE_TYPE='BASE TABLE'");
    }

    @Test
    void sqliteRendersItsOwnCreateAndExistenceSql() {
        IDdlGenerator generator = SQLiteDdlGenerator.newInstance();
        assertThat(generator.getDdlHistory()).isEqualTo("ddl_history");
        assertThat(generator.createDdlHistory()).isEqualTo(
                "CREATE TABLE IF NOT EXISTS `ddl_history` (script  TEXT primary key,type    TEXT,version TEXT);");
        assertThat(existSql(generator, null))
                .isEqualTo("SELECT count(1) NUM FROM sqlite_master WHERE name='ddl_history' AND type='table'");
    }

    @SuppressWarnings("deprecation")
    private static String existSql(IDdlGenerator generator, String databaseName) {
        AtomicReference<String> captured = new AtomicReference<>();
        generator.existTable(databaseName, sql -> {
            captured.set(sql);
            return true;
        });
        return captured.get();
    }
}
