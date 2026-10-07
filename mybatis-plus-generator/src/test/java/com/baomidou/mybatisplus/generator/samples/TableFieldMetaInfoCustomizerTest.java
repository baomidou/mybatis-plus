package com.baomidou.mybatisplus.generator.samples;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.querys.H2Query;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.model.AnnotationAttributes;
import com.baomidou.mybatisplus.generator.query.DefaultQuery;
import com.baomidou.mybatisplus.generator.query.SQLQuery;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class TableFieldMetaInfoCustomizerTest extends BaseGeneratorTest {

    private static final String H2URL = "jdbc:h2:mem:test-h2-meta-customizer;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;MODE=MYSQL;DATABASE_TO_LOWER=TRUE";

    private static final DataSourceConfig DATA_SOURCE_CONFIG = new DataSourceConfig.Builder(H2URL, "sa", "")
        .databaseQueryClass(DefaultQuery.class).build();

    private static final DataSourceConfig SQL_QUERY_DATA_SOURCE_CONFIG = new DataSourceConfig.Builder(H2URL, "sa", "")
        .dbQuery(new H2SqlQueryForTest())
        .databaseQueryClass(SQLQuery.class).build();

    @BeforeAll
    static void before() throws SQLException {
        initDataSource(DATA_SOURCE_CONFIG);
    }

    @Test
    void shouldCustomizeFieldMetaInfoAfterTypeConversion() {
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
            .addInclude("t_simple")
            .entityBuilder()
            .tableFieldMetaInfoCustomizer((tableInfo, tableField) -> {
                if ("name".equals(tableField.getColumnName())) {
                    tableField.getMetaInfo().setJdbcType(JdbcType.OTHER);
                    tableField.getMetaInfo().setTypeName("POINT");
                    tableField.setColumnType(DbColumnType.OBJECT);
                }
            })
            .build();
        GlobalConfig globalConfig = globalConfig().build();
        ConfigBuilder configBuilder = new ConfigBuilder(null, DATA_SOURCE_CONFIG, strategyConfig, null, globalConfig, null);

        TableField field = findField(configBuilder.getTableInfoList(), "t_simple", "name");

        assertThat(field.getMetaInfo().getJdbcType()).isEqualTo(JdbcType.OTHER);
        assertThat(field.getMetaInfo().getTypeName()).isEqualTo("POINT");
        assertThat(field.getColumnType()).isEqualTo(DbColumnType.OBJECT);
    }

    @Test
    void shouldCustomizeFieldMetaInfoWithSqlQuery() {
        AtomicBoolean customized = new AtomicBoolean();
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
            .addInclude("t_simple")
            .entityBuilder()
            .tableFieldMetaInfoCustomizer((tableInfo, tableField) -> {
                if ("name".equals(tableField.getColumnName())) {
                    customized.set(true);
                    tableField.getMetaInfo().setJdbcType(JdbcType.OTHER);
                    tableField.getMetaInfo().setTypeName("POINT");
                    tableField.setColumnType(DbColumnType.OBJECT);
                }
            })
            .build();
        ConfigBuilder configBuilder = new ConfigBuilder(null, SQL_QUERY_DATA_SOURCE_CONFIG, strategyConfig, null, globalConfig().build(), null);

        TableField field = findField(configBuilder.getTableInfoList(), "t_simple", "name");

        assertThat(customized).isTrue();
        assertThat(field.getMetaInfo().getJdbcType()).isEqualTo(JdbcType.OTHER);
        assertThat(field.getMetaInfo().getTypeName()).isEqualTo("POINT");
        assertThat(field.getColumnType()).isEqualTo(DbColumnType.OBJECT);
    }

    @Test
    void shouldCustomizeFieldMetaInfoBeforePropertyNameConversion() {
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
            .addInclude("t_simple")
            .entityBuilder()
            .enableRemoveIsPrefix()
            .tableFieldMetaInfoCustomizer((tableInfo, tableField) -> {
                if ("is_ok".equals(tableField.getColumnName())) {
                    tableField.setColumnType(DbColumnType.BOOLEAN);
                }
            })
            .build();
        ConfigBuilder configBuilder = new ConfigBuilder(null, DATA_SOURCE_CONFIG, strategyConfig, null, globalConfig().build(), null);

        TableField field = findField(configBuilder.getTableInfoList(), "t_simple", "is_ok");

        assertThat(field.getColumnType()).isEqualTo(DbColumnType.BOOLEAN);
        assertThat(field.getPropertyName()).isEqualTo("ok");
    }

    @Test
    void shouldProvideTableInfoToCustomizer() {
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
            .addInclude("t_simple", "t_test")
            .entityBuilder()
            .tableFieldMetaInfoCustomizer((tableInfo, tableField) -> {
                if ("name".equals(tableField.getColumnName()) && "t_simple".equals(tableInfo.getName())) {
                    tableField.setColumnType(DbColumnType.OBJECT);
                }
                if ("name".equals(tableField.getColumnName()) && "t_test".equals(tableInfo.getName())) {
                    tableField.setColumnType(DbColumnType.BYTE_ARRAY);
                }
            })
            .build();
        ConfigBuilder configBuilder = new ConfigBuilder(null, DATA_SOURCE_CONFIG, strategyConfig, null, globalConfig().build(), null);

        assertThat(findField(configBuilder.getTableInfoList(), "t_simple", "name").getColumnType())
            .isEqualTo(DbColumnType.OBJECT);
        assertThat(findField(configBuilder.getTableInfoList(), "t_test", "name").getColumnType())
            .isEqualTo(DbColumnType.BYTE_ARRAY);
    }

    private TableField findField(List<TableInfo> tableInfoList, String tableName, String columnName) {
        return tableInfoList.stream()
            .filter(tableInfo -> tableName.equals(tableInfo.getName()))
            .findFirst()
            .orElseThrow()
            .getFields()
            .stream()
            .filter(tableField -> columnName.equals(tableField.getColumnName()))
            .findFirst()
            .orElseThrow();
    }

    /**
     * Test-local SQLQuery compatibility shim for current H2 metadata.
     * This test does not assert primary-key behavior; it aliases current H2 metadata
     * columns so the SQLQuery path continues to exercise ITableFieldMetaInfoCustomizer semantics.
     */
    private static class H2SqlQueryForTest extends H2Query {

        @Override
        public String primaryKeySql(DataSourceConfig dataSourceConfig, String tableName) {
            return "";
        }

        @Override
        public String fieldKey() {
            return "COLUMN_NAME";
        }

        @Override
        public String fieldType() {
            return "DATA_TYPE";
        }
    }

    @Test
    void shouldGenerateImportForCustomizedFieldAnnotation(@TempDir Path outputDir) throws IOException {
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
            .addInclude("t_simple")
            .addTablePrefix("t_")
            .entityBuilder()
            .tableFieldMetaInfoCustomizer((tableInfo, tableField) -> {
                if ("name".equals(tableField.getColumnName())) {
                    tableField.addAnnotationAttributesList(new AnnotationAttributes("@Sensitive",
                        "com.example.security.Sensitive"));
                }
            })
            .mapperBuilder().disable()
            .serviceBuilder().disable()
            .controllerBuilder().disable()
            .build();
        GlobalConfig globalConfig = globalConfig().disableOpenDir().outputDir(outputDir.toString()).build();

        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.config(new ConfigBuilder(null, DATA_SOURCE_CONFIG, strategyConfig, null, globalConfig, null));
        generator.execute();

        String entity = Files.readString(outputDir.resolve("com/baomidou/entity/Simple.java"));
        assertThat(entity).contains("import com.example.security.Sensitive;");
        assertThat(entity).contains("@Sensitive");
    }
}
