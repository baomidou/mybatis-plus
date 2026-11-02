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
 * 常量恒等式绕过全表更新/删除校验的回归测试
 *
 * @author hubin
 * @since 3.5.18
 */
@Tag("unit")
class BlockAttackInnerInterceptorTautologyTest {

    private final BlockAttackInnerInterceptor interceptor = new BlockAttackInnerInterceptor();

    @Test
    void xor() {
        checkEx("update records set status = 2 where 1 xor 0");
        checkEx("update records set status = 2 where 1 XOR 0");
        checkEx("update records set status = 2 where 0 xor 1");
        checkEx("update records set status = 2 where 1 xor 1 = 0");
        checkEx("update records set status = 2 where 1 = 1 xor 1 = 0");
        checkEx("update records set status = 2 where 1 = 1 and 1 xor 0");
        checkEx("delete from records where 1 xor 0");
        checkEx("delete from records where (1 xor 0) xor 0");
        checkNotEx("delete from records where (1 xor 0) xor 1");
    }

    @Test
    void not() {
        checkEx("update records set status = 2 where not 1 = 2");
        checkEx("update records set status = 2 where not (1 <> 1)");
        checkEx("delete from records where not (1 = 2)");
        checkNotEx("update records set status = 2 where not 1 = 1");
        checkNotEx("update records set status = 2 where not (1 <> 2)");
    }

    @Test
    void comparison() {
        checkEx("update records set status = 2 where 1 <= 1");
        checkEx("update records set status = 2 where 2 > 1");
        checkEx("update records set status = 2 where 1.0 = 1");
        checkEx("update records set status = 2 where 'a' = 'a'");
        checkEx("delete from records where 1 = 1");
        checkNotEx("update records set status = 2 where 1 = 2");
        checkNotEx("update records set status = 2 where 1 > 2");
        checkNotEx("update records set status = 2 where 'a' = 'b'");
    }

    @Test
    void longComparisonPreservesPrecision() {
        for (String statement : new String[]{"update records set status = 2", "delete from records"}) {
            for (String predicate : new String[]{"9007199254740992 <> 9007199254740993",
                    "9007199254740992 != 9007199254740993", "9007199254740992 < 9007199254740993",
                    "9007199254740993 > 9007199254740992", "9223372036854775806 < 9223372036854775807",
                    "9223372036854775806 <> 9223372036854775807", "9223372036854775807 = 9223372036854775807",
                    "9223372036854775807 >= 9223372036854775807", "9223372036854775807 <= 9223372036854775807"}) {
                checkEx(statement + " where " + predicate);
                checkEx(statement + " where id = ? or (" + predicate + ")");
                checkNotEx(statement + " where id = ? and (" + predicate + ")");
            }
        }
    }

    @Test
    void falseLongComparisonPreservesPrecision() {
        for (String statement : new String[]{"update records set status = 2", "delete from records"}) {
            for (String predicate : new String[]{"9007199254740992 = 9007199254740993",
                    "9007199254740992 >= 9007199254740993", "9007199254740993 <= 9007199254740992",
                    "9223372036854775806 = 9223372036854775807", "9223372036854775806 > 9223372036854775807",
                    "9223372036854775807 < 9223372036854775806"}) {
                checkNotEx(statement + " where " + predicate);
                checkEx(statement + " where not (" + predicate + ")");
            }
        }
    }

    @Test
    void constant() {
        checkEx("update records set status = 2 where 1");
        checkEx("update records set status = 2 where 1 and 1 = 1");
        checkEx("delete from records where 1");
        checkNotEx("update records set status = 2 where 0");
    }

    @Test
    void normalWhere() {
        checkNotEx("update records set status = 2 where id = 1");
        checkNotEx("update records set status = 2 where id = ?");
        checkNotEx("update records set status = 2 where status = 0");
        checkNotEx("update records set status = 2 where status > 0 and name = 'x'");
        checkNotEx("update records set status = 2 where status = 1 xor archived = 1");
        checkNotEx("update records set status = 2 where id in (1, 2)");
        checkNotEx("update records set status = 2 where status is null");
        checkNotEx("update records set status = 2 where name like 'a%'");
        checkNotEx("delete from records where id = 1");
    }

    @Test
    void orWithUnknownPredicate() {
        for (String statement : new String[]{"update records set status = 2", "delete from records"}) {
            for (String predicate : new String[]{"rfid is null", "id in (1, 2)", "name like 'a%'", "id > 0"}) {
                checkNotEx(statement + " where " + predicate + " or rfid = ''");
                checkNotEx(statement + " where rfid = '' or " + predicate);
                checkNotEx(statement + " where " + predicate + " or rfid is null");
            }
            checkNotEx(statement + " where id in (1, 2) and (rfid is null or rfid = '')");
        }
    }

    @Test
    void orWithUnknownAndConstant() {
        for (String statement : new String[]{"update records set status = 2", "delete from records"}) {
            checkEx(statement + " where rfid is null or 1 = 1");
            checkEx(statement + " where 1 = 1 or rfid is null");
            checkNotEx(statement + " where rfid is null or 1 = 0");
            checkNotEx(statement + " where 1 = 0 or rfid is null");
            checkNotEx(statement + " where not (rfid is null or 1 = 0)");
            checkNotEx(statement + " where not (1 = 0 or rfid is null)");
            checkNotEx(statement + " where 1 = 0 or 2 = 0");
            checkEx(statement + " where not (1 = 0 or 2 = 0)");
            checkEx(statement + " where (rfid is null or rfid = '') or 1 = 1");
        }
    }

    @Test
    void andWithUnknownAndConstant() {
        for (String statement : new String[]{"update records set status = 2", "delete from records"}) {
            for (String predicate : new String[]{"id in (?)", "id > ?", "rfid is null", "name like 'a%'"}) {
                checkNotEx(statement + " where " + predicate + " and 1 = 1");
                checkNotEx(statement + " where 1 = 1 and " + predicate);
                checkNotEx(statement + " where not (" + predicate + " and 1 = 1)");
                checkNotEx(statement + " where not (1 = 1 and " + predicate + ")");
                checkNotEx(statement + " where " + predicate + " and 1 = 0");
                checkNotEx(statement + " where 1 = 0 and " + predicate);
                checkEx(statement + " where not (" + predicate + " and 1 = 0)");
                checkEx(statement + " where not (1 = 0 and " + predicate + ")");
            }
            checkEx(statement + " where (id in (?) and 1 = 1) or 1 = 1");
        }
    }

    @Test
    void andWithLogicDelete() {
        // https://github.com/baomidou/mybatis-plus/issues/7218
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), LogicRecord.class);
        try {
            for (String statement : new String[]{"update block_attack_or_logic set del_flag = '1'",
                    "delete from block_attack_or_logic"}) {
                for (String predicate : new String[]{"id in (?)", "id = ?", "id > ?", "rfid is null",
                        "rfid like 'a%'"}) {
                    checkNotEx(statement + " where " + predicate + " and del_flag = '0'");
                    checkNotEx(statement + " where del_flag = '0' and " + predicate);
                    checkNotEx(statement + " where (" + predicate + " and del_flag = '0') and 1 = 1");
                }
                checkEx(statement + " where del_flag = '0'");
                checkEx(statement + " where del_flag = '0' and 1 = 1");
                checkEx(statement + " where 1 = 1 and del_flag = '0'");
            }
        } finally {
            TableInfoHelper.remove(LogicRecord.class);
        }
    }

    @Test
    void orWithLogicDelete() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), LogicRecord.class);
        try {
            for (String statement : new String[]{"update block_attack_or_logic set rfid = ?",
                    "delete from block_attack_or_logic"}) {
                checkNotEx(statement + " where id in (1, 2) and del_flag = 0 and (rfid is null or rfid = '')");
                checkEx(statement + " where del_flag = 0 or rfid is null");
                checkEx(statement + " where rfid is null or del_flag = 0");
            }
        } finally {
            TableInfoHelper.remove(LogicRecord.class);
        }
    }

    @TableName("block_attack_or_logic")
    private static class LogicRecord {

        @TableId
        private Long id;
        @TableLogic
        private Integer delFlag;
        private String rfid;
    }

    void checkEx(String sql) {
        assertThatThrownBy(() -> interceptor.parserMulti(sql, null)).as(sql).isInstanceOf(MybatisPlusException.class);
    }

    void checkNotEx(String sql) {
        assertThat(interceptor.parserMulti(sql, null)).as(sql).isNotBlank();
    }
}
