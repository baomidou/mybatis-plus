package com.baomidou.mybatisplus.test.logicdel;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.test.BaseDbTest;
import org.apache.ibatis.reflection.MetaObject;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for dynamic logic-delete values supplied through the {@link MetaObjectHandler} <br>
 * fill mechanism — addressing i18n / timezone support when the logic-delete field is a <br>
 * timestamp (issue #5111).
 *
 * <p>
 * Scenario: a table uses a nullable {@code deleted_at TIMESTAMP} column as the <br>
 * logic-delete marker. The application wants to control the timestamp value (e.g. to <br>
 * apply a specific timezone) rather than delegating to the database {@code NOW()} <br>
 * function. By annotating the field with {@code @TableField(fill = FieldFill.UPDATE)} <br>
 * <em>and</em> {@code @TableLogic}, the fill handler can inject the correct value at <br>
 * runtime.
 */
public class LogicDelWithFillTest extends BaseDbTest<LogicDelWithFillMapper> {

    /**
     * Sentinel timestamp used by the fill handler – deliberately far in the future so <br>
     * it is easy to distinguish from any real "now".
     */
    private static final LocalDateTime FILL_DELETED_AT = LocalDateTime.of(2099, 6, 15, 12, 0, 0);

    // -----------------------------------------------------------------------
    // Tests
    // -----------------------------------------------------------------------

    /**
     * When {@code deleteById(entity)} is called with an entity object the fill handler <br>
     * is invoked and the entity's {@code deletedAt} is set to the application-computed <br>
     * value. The generated SQL should use {@code #{deletedAt}} (the filled value) <br>
     * rather than the static {@code logicDeleteValue} string.
     */
    @Test
    void deleteByIdWithEntity_usesFillValue() {
        doTestAutoCommit(mapper -> {
            LogicDelWithFillEntity entity = new LogicDelWithFillEntity();
            entity.setId(1L);
            entity.setName("fill-delete-test");
            mapper.insert(entity);
            assertThat(entity.getDeletedAt()).isNull();

            // Delete using entity – fill handler provides the timestamp.
            assertThat(mapper.deleteById(entity)).isEqualTo(1);

            // The fill handler must have set deletedAt on the entity.
            assertThat(entity.getDeletedAt()).isEqualTo(FILL_DELETED_AT);
        });

        doTest(mapper -> {
            // The record must no longer appear in logic-filtered queries.
            assertThat(mapper.selectById(1L)).isNull();
        });

        // Verify via raw JDBC that the row still exists and deleted_at equals the fill value.
        List<LocalDateTime> rows = jdbcTemplate.query(
                "SELECT deleted_at FROM logic_del_fill_entity WHERE id = 1",
                (rs, i) -> rs.getObject("deleted_at", LocalDateTime.class));
        assertThat(rows).hasSize(1);
        assertThat(rows.get(0)).isEqualTo(FILL_DELETED_AT);
    }

    /**
     * When {@code deleteByIds(Collection)} is called the framework creates a fresh <br>
     * entity instance (mpFillEt) for fill purposes. The fill handler sets <br>
     * {@code deletedAt} on that instance, and the generated SQL should use <br>
     * {@code #{mpFillEt.deletedAt}} rather than the static value.
     */
    @Test
    void deleteByIds_usesFillValue() {
        doTestAutoCommit(mapper -> {
            LogicDelWithFillEntity e1 = new LogicDelWithFillEntity();
            e1.setId(2L);
            e1.setName("batch-fill-1");
            mapper.insert(e1);

            LogicDelWithFillEntity e2 = new LogicDelWithFillEntity();
            e2.setId(3L);
            e2.setName("batch-fill-2");
            mapper.insert(e2);

            assertThat(mapper.deleteByIds(Arrays.asList(2L, 3L))).isEqualTo(2);
        });

        doTest(mapper -> {
            assertThat(mapper.selectById(2L)).isNull();
            assertThat(mapper.selectById(3L)).isNull();
        });

        // Both rows should have deleted_at set to the fill value.
        List<LocalDateTime> rows = jdbcTemplate.query(
                "SELECT deleted_at FROM logic_del_fill_entity WHERE id IN (2, 3) ORDER BY id",
                (rs, i) -> rs.getObject("deleted_at", LocalDateTime.class));
        assertThat(rows).hasSize(2);
        assertThat(rows.get(0)).isEqualTo(FILL_DELETED_AT);
        assertThat(rows.get(1)).isEqualTo(FILL_DELETED_AT);
    }

    /**
     * When only a primary-key value is supplied (no entity object), the fill <br>
     * handler cannot be called, so the static {@code logicDeleteValue} configured on the <br>
     * global DbConfig is used as a fallback. In the test configuration the static value is <br>
     * {@code "NOW()"}, which generates {@code deleted_at = NOW()} — the database function. <br>
     * This verifies that the static fallback still works so that callers who do not have an <br>
     * entity object available continue to function correctly.
     */
    @Test
    void deleteById_withOnlyId_usesStaticFallback() {
        // Use a separate record to avoid collision with other tests.
        doTestAutoCommit(mapper -> {
            LogicDelWithFillEntity entity = new LogicDelWithFillEntity();
            entity.setId(10L);
            entity.setName("static-fallback");
            mapper.insert(entity);

            // deleteById(Long) – no entity, fill handler is NOT called.
            assertThat(mapper.deleteById(10L)).isEqualTo(1);
        });

        doTest(mapper -> {
            // Record should be logically deleted: selectById uses WHERE deleted_at IS NULL,
            // but deleted_at was set to NOW() (non-null) by the static fallback.
            assertThat(mapper.selectById(10L)).isNull();
        });

        // Verify the row is still physically present and deleted_at is non-null (set by NOW()).
        List<LocalDateTime> rows = jdbcTemplate.query(
                "SELECT deleted_at FROM logic_del_fill_entity WHERE id = 10",
                (rs, i) -> rs.getObject("deleted_at", LocalDateTime.class));
        assertThat(rows).hasSize(1);
        assertThat(rows.get(0)).isNotNull();
    }

    // -----------------------------------------------------------------------
    // Test infrastructure
    // -----------------------------------------------------------------------

    @Override
    protected GlobalConfig globalConfig() {
        GlobalConfig globalConfig = super.globalConfig();
        globalConfig.setMetaObjectHandler(new MetaObjectHandler() {

            @Override
            public void insertFill(MetaObject metaObject) {
                // No insert fills for this entity.
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                // Supply the application-side timestamp for the logic-delete field.
                // strictUpdateFill will only apply when the field is currently null
                // (i.e., the record has not yet been deleted).
                strictUpdateFill(metaObject, "deletedAt", () -> FILL_DELETED_AT, LocalDateTime.class);
            }
        });
        // Configure a static fallback for callers that pass only a primary key.
        globalConfig.getDbConfig().setLogicDeleteValue("NOW()").setLogicNotDeleteValue("null");
        return globalConfig;
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList(
                "DROP TABLE IF EXISTS logic_del_fill_entity",
                "CREATE TABLE logic_del_fill_entity (" + "id      BIGINT       NOT NULL,"
                        + "name    VARCHAR(30)  NULL DEFAULT NULL," + "deleted_at TIMESTAMP NULL DEFAULT NULL,"
                        + "PRIMARY KEY (id)" + ")");
    }
}
