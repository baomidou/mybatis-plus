package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author miemie
 * @author hubin
 * @since 2020-08-18
 */
@Tag("unit")
class BlockAttackInnerInterceptorTest {

    private static final String[] RECORDS_STATEMENTS = {"update records set status = 2", "delete from records"};

    private final BlockAttackInnerInterceptor interceptor = new BlockAttackInnerInterceptor();

    @Test
    void singleStatement() {
        for (String statement : RECORDS_STATEMENTS) {
            assertBlocked(statement);
            for (String predicate : new String[]{"1 = 1", "1 <> 2", "1 != 2", "1 = 1 and 2 = 2",
                    "1 = 1 and 2 = 3 or 1 = 1", "1 = 1 and (2 = 2)", "(1 = 1 and 2 = 2)",
                    "(1 = 1 and (2 = 3 or 3 = 3))"}) {
                assertBlocked(statement + " where " + predicate);
            }
            assertAllowed(statement + " where 1 = ?");
        }
    }

    @Test
    void xor() {
        for (String statement : RECORDS_STATEMENTS) {
            assertBlocked(statement + " where 1 xor 0");
            assertBlocked(statement + " where 1 XOR 0");
            assertBlocked(statement + " where 0 xor 1");
            assertBlocked(statement + " where 1 xor 1 = 0");
            assertBlocked(statement + " where 1 = 1 xor 1 = 0");
            assertBlocked(statement + " where 1 = 1 and 1 xor 0");
            assertBlocked(statement + " where (1 xor 0) xor 0");
            assertAllowed(statement + " where (1 xor 0) xor 1");
        }
    }

    @Test
    void not() {
        for (String statement : RECORDS_STATEMENTS) {
            assertBlocked(statement + " where not 1 = 2");
            assertBlocked(statement + " where not (1 = 2)");
            assertBlocked(statement + " where not (1 <> 1)");
            assertAllowed(statement + " where not 1 = 1");
            assertAllowed(statement + " where not (1 <> 2)");
        }
    }

    @Test
    void comparison() {
        for (String statement : RECORDS_STATEMENTS) {
            assertBlocked(statement + " where 1 = 1");
            assertBlocked(statement + " where 1 <= 1");
            assertBlocked(statement + " where 2 > 1");
            assertBlocked(statement + " where 1.0 = 1");
            assertBlocked(statement + " where 'a' = 'a'");
            assertAllowed(statement + " where 1 = 2");
            assertAllowed(statement + " where 1 > 2");
            assertAllowed(statement + " where 'a' = 'b'");
        }
    }

    @Test
    void constantComparisonWithPrecisionOrCollationDifferences() {
        for (String statement : RECORDS_STATEMENTS) {
            assertAllowed(statement + " where 0.10000000000000001 = 0.1");
            assertAllowed(statement + " where not (0.10000000000000001 > 0.1)");
            assertAllowed(statement + " where 9223372036854775808 < 9223372036854775808");
            assertAllowed(statement + " where 9223372036854775809 > 9223372036854775808");
            assertAllowed(statement + " where 'A' <> 'a'");
            assertAllowed(statement + " where not ('A' = 'a')");
            assertAllowed(statement + " where '' = ''");
            assertBlocked(statement + " where -0.0 = 0.0");
            assertBlocked(statement + " where 9223372036854775808 = 9223372036854775808");
        }
    }

    @Test
    void longComparisonPreservesPrecision() {
        for (String statement : RECORDS_STATEMENTS) {
            for (String predicate : new String[]{"9007199254740992 <> 9007199254740993",
                    "9007199254740992 != 9007199254740993", "9007199254740992 < 9007199254740993",
                    "9007199254740993 > 9007199254740992", "9223372036854775806 < 9223372036854775807",
                    "9223372036854775806 <> 9223372036854775807", "9223372036854775807 = 9223372036854775807",
                    "9223372036854775807 >= 9223372036854775807", "9223372036854775807 <= 9223372036854775807"}) {
                assertBlocked(statement + " where " + predicate);
                assertBlocked(statement + " where id = ? or (" + predicate + ")");
                assertAllowed(statement + " where id = ? and (" + predicate + ")");
            }
        }
    }

    @Test
    void falseLongComparisonPreservesPrecision() {
        for (String statement : RECORDS_STATEMENTS) {
            for (String predicate : new String[]{"9007199254740992 = 9007199254740993",
                    "9007199254740992 >= 9007199254740993", "9007199254740993 <= 9007199254740992",
                    "9223372036854775806 = 9223372036854775807", "9223372036854775806 > 9223372036854775807",
                    "9223372036854775807 < 9223372036854775806"}) {
                assertAllowed(statement + " where " + predicate);
                assertBlocked(statement + " where not (" + predicate + ")");
            }
        }
    }

    @Test
    void numericAndBooleanConstants() {
        for (String statement : RECORDS_STATEMENTS) {
            assertBlocked(statement + " where 1");
            assertBlocked(statement + " where 1 and 1 = 1");
            assertBlocked(statement + " where TRUE");
            assertBlocked(statement + " where not (false)");
            assertBlocked(statement + " where id = ? or true");
            assertAllowed(statement + " where 0");
            assertAllowed(statement + " where FALSE");
            assertAllowed(statement + " where not true");
            assertAllowed(statement + " where id = ? and true");
        }
    }

    @Test
    void booleanLiteralComparison() {
        for (String statement : RECORDS_STATEMENTS) {
            assertBlocked(statement + " where true = true");
            assertBlocked(statement + " where true <> false");
            assertBlocked(statement + " where not (true = false)");
            assertAllowed(statement + " where true = false");
            assertAllowed(statement + " where active = true");
        }
    }

    @Test
    void constantNullPredicate() {
        for (String statement : RECORDS_STATEMENTS) {
            assertBlocked(statement + " where null is null");
            assertBlocked(statement + " where not (null is not null)");
            assertBlocked(statement + " where 1 is not null");
            assertAllowed(statement + " where null is not null");
            assertAllowed(statement + " where null = null");
            assertAllowed(statement + " where not (null = null)");
        }
    }

    @Test
    void normalWhere() {
        for (String statement : RECORDS_STATEMENTS) {
            assertAllowed(statement + " where id = 1");
            assertAllowed(statement + " where id = ?");
            assertAllowed(statement + " where status = 0");
            assertAllowed(statement + " where status > 0 and name = 'x'");
            assertAllowed(statement + " where status = 1 xor archived = 1");
            assertAllowed(statement + " where id in (1, 2)");
            assertAllowed(statement + " where status is null");
            assertAllowed(statement + " where name like 'a%'");
        }
    }

    @Test
    void comparisonWithUnknownOperands() {
        for (String statement : RECORDS_STATEMENTS) {
            assertAllowed(statement + " where id <> ?");
            assertAllowed(statement + " where id != ?");
            assertAllowed(statement + " where ? = ?");
            assertAllowed(statement + " where not (id = ?)");
            assertAllowed(statement + " where not (id = ? or 1 = 0)");
            assertBlocked(statement + " where id <> ? or 1 = 1");
        }
    }

    @Test
    void orWithUnknownPredicates() {
        for (String statement : RECORDS_STATEMENTS) {
            for (String predicate : new String[]{"rfid is null", "id in (1, 2)", "name like 'a%'", "id > 0"}) {
                assertAllowed(statement + " where " + predicate + " or rfid = ''");
                assertAllowed(statement + " where rfid = '' or " + predicate);
                assertAllowed(statement + " where " + predicate + " or rfid is null");
            }
            assertAllowed(statement + " where id in (1, 2) and (rfid is null or rfid = '')");
            assertBlocked(statement + " where rfid is null or 1 = 1");
            assertBlocked(statement + " where 1 = 1 or rfid is null");
            assertAllowed(statement + " where rfid is null or 1 = 0");
            assertAllowed(statement + " where 1 = 0 or rfid is null");
            assertAllowed(statement + " where not (rfid is null or 1 = 0)");
            assertAllowed(statement + " where not (1 = 0 or rfid is null)");
            assertAllowed(statement + " where 1 = 0 or 2 = 0");
            assertBlocked(statement + " where not (1 = 0 or 2 = 0)");
            assertBlocked(statement + " where (rfid is null or rfid = '') or 1 = 1");
        }
    }

    @Test
    void andWithUnknownAndConstant() {
        for (String statement : RECORDS_STATEMENTS) {
            assertAllowed(statement + " where rfid is null and 1 = 1");
            assertAllowed(statement + " where 1 = 1 and rfid is null");
            assertAllowed(statement + " where not (rfid is null and 1 = 1)");
            assertAllowed(statement + " where not (1 = 1 and rfid is null)");
            assertAllowed(statement + " where rfid is null and 1 = 0");
            assertAllowed(statement + " where 1 = 0 and rfid is null");
            assertBlocked(statement + " where not (rfid is null and 1 = 0)");
            assertBlocked(statement + " where not (1 = 0 and rfid is null)");
            assertBlocked(statement + " where (id in (?) and 1 = 1) or 1 = 1");
        }
    }

    @Test
    void logicDeleteWithAndOr() {
        // https://github.com/baomidou/mybatis-plus/issues/7218
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), LogicRecord.class);
        try {
            for (String statement : new String[]{"update block_attack_or_logic set del_flag = '1'",
                    "delete from block_attack_or_logic"}) {
                for (String predicate : new String[]{"id in (?)", "id = ?", "id <> ?", "id > ?", "rfid is null",
                        "rfid like 'a%'"}) {
                    assertAllowed(statement + " where " + predicate + " and del_flag = '0'");
                    assertAllowed(statement + " where del_flag = '0' and " + predicate);
                    assertAllowed(statement + " where (" + predicate + " and del_flag = '0') and 1 = 1");
                }
                assertBlocked(statement + " where del_flag = '0'");
                assertBlocked(statement + " where del_flag = '0' and 1 = 1");
                assertBlocked(statement + " where 1 = 1 and del_flag = '0'");
                assertAllowed(statement + " where id in (1, 2) and del_flag = 0 and (rfid is null or rfid = '')");
                assertBlocked(statement + " where del_flag = 0 or rfid is null");
                assertBlocked(statement + " where rfid is null or del_flag = 0");
            }
        } finally {
            TableInfoHelper.remove(LogicRecord.class);
        }
    }

    @Test
    void distinguishesLogicDeleteFilterFromOtherPredicates() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), LogicRecord.class);
        try {
            for (String statement : new String[]{"update block_attack_or_logic r set rfid = ?",
                    "delete from block_attack_or_logic r"}) {
                assertAllowed(statement + " where del_flag = id");
                assertAllowed(statement + " where del_flag = 0 and del_flag = 1");
                assertAllowed(statement + " where del_flag > 5");
                assertAllowed(statement + " where del_flag is null");
                assertAllowed(statement + " where del_flag = ?");
                assertAllowed(statement + " where not (r.del_flag = 0)");
                assertAllowed(statement + " where r.DEL_FLAG = 0 and r.id <> ?");
                assertBlocked(statement + " where DEL_FLAG = 0");
                assertBlocked(statement + " where r.del_flag = 0");
                assertBlocked(statement + " where 0 = r.DEL_FLAG");
            }
            assertAllowed("update block_attack_or_logic r set rfid = ? from records s where s.del_flag = 0");
        } finally {
            TableInfoHelper.remove(LogicRecord.class);
        }
    }

    @Test
    void logicDeleteWithNullAsTheNotDeletedValue() {
        TableInfoHelper
                .initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), NullableLogicRecord.class);
        try {
            for (String statement : new String[]{"update block_attack_nullable_logic r set del_flag = 1",
                    "delete from block_attack_nullable_logic r"}) {
                assertBlocked(statement + " where r.del_flag is null");
                assertAllowed(statement + " where r.del_flag is not null");
                assertAllowed(statement + " where r.del_flag is null and r.id = ?");
            }
        } finally {
            TableInfoHelper.remove(NullableLogicRecord.class);
        }
    }

    @Test
    void logicDeleteWithBooleanNotDeletedValue() {
        TableInfoHelper
                .initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), BooleanLogicRecord.class);
        try {
            for (String statement : new String[]{"update block_attack_boolean_logic r set del_flag = true",
                    "delete from block_attack_boolean_logic r"}) {
                assertBlocked(statement + " where r.del_flag = false");
                assertAllowed(statement + " where r.del_flag = true");
                assertAllowed(statement + " where r.del_flag = false and r.id = ?");
            }
        } finally {
            TableInfoHelper.remove(BooleanLogicRecord.class);
        }
    }

    @Test
    void schemaQualifiedLogicDelete() {
        TableInfoHelper
                .initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), SchemaLogicRecord.class);
        try {
            for (String statement : new String[]{"update block_attack_schema.logic_record set del_flag = 1",
                    "delete from block_attack_schema.logic_record"}) {
                assertBlocked(statement + " where del_flag = 0");
                assertAllowed(statement + " where id = ? and del_flag = 0");
            }
        } finally {
            TableInfoHelper.remove(SchemaLogicRecord.class);
        }
    }

    void assertBlocked(String sql) {
        assertThatThrownBy(() -> interceptor.parserSingle(sql, null)).as(sql).isInstanceOf(MybatisPlusException.class);
    }

    void assertAllowed(String sql) {
        assertThat(interceptor.parserSingle(sql, null)).as(sql).isNotBlank();
    }

    @TableName(value = "logic_record", schema = "block_attack_schema")
    private static class SchemaLogicRecord {

        @TableId
        private Long id;
        @TableLogic
        private Integer delFlag;
    }

    @TableName("block_attack_or_logic")
    private static class LogicRecord {

        @TableId
        private Long id;
        @TableLogic
        private Integer delFlag;
        private String rfid;
    }

    @TableName("block_attack_nullable_logic")
    private static class NullableLogicRecord {

        @TableId
        private Long id;
        @TableLogic(value = "null")
        private Integer delFlag;
    }

    @TableName("block_attack_boolean_logic")
    private static class BooleanLogicRecord {

        @TableId
        private Long id;
        @TableLogic(value = "false", delval = "true")
        private Boolean delFlag;
    }
}
