package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-08-18
 */
@Tag("unit")
class BlockAttackInnerInterceptorTest {

    private final BlockAttackInnerInterceptor interceptor = new BlockAttackInnerInterceptor();

    @Test
    void update() {
        checkEx("update user set name = null", "null where");
        checkEx("update user set name = null where 1=1", "1=1");
        checkEx("update user set name = null where 1<>2", "1<>2");
        checkEx("update user set name = null where 1!=2", "1!=2");
        checkEx("update user set name = null where 1=1 and 2=2", "1=1 and 2=2");
        checkEx("update user set name = null where 1=1 and 2=3 or 1=1", "1=1 and 2=3 or 1=1");
        checkEx("update user set name = null where 1=1 and (2=2)", "1=1 and (2=2)");
        checkEx("update user set name = null where (1=1 and 2=2)", "(1=1 and 2=2)");
        checkEx("update user set name = null where (1=1 and (2=3 or 3=3))", "(1=1 and (2=3 or 3=3))");
        checkEx("update user set name = null where 1 between 1 and 2", "1 between 1 and 2");
        checkEx("update user set name = null where 1 in (1, 2)", "1 in (1, 2)");
        checkEx("update user set name = null where 'a' like '%'", "'a' like '%'");
        checkEx(
                "update user set name = null where 9007199254740992 not in (9007199254740993)",
                "large integer NOT IN precision");
        checkEx("update user set name = null where 'a\nb' like '%'", "LIKE matches newline");
        checkEx("update user set name = null where 'it''s' like 'it_s'", "LIKE unescapes doubled quote");

        checkNotEx("update user set name = null where 1=?", "1=?");
        checkNotEx("update user set name = null where 'abc' regexp 'a.*'", "REGEXP is not LIKE");
        checkNotEx(
                "update user set name = null where not (id in (1) and 'it''s' like 'it_s')",
                "valid filter must not be blocked");
    }

    @Test
    void delete() {
        checkEx("delete from user", "null where");
        checkEx("delete from user where 1=1", "1=1");
        checkEx("delete from user where 1<>2", "1<>2");
        checkEx("delete from user where 1!=2", "1!=2");
        checkEx("delete from user where 1=1 and 2=2", "1=1 and 2=2");
        checkEx("delete from user where 1=1 and 2=3 or 1=1", "1=1 and 2=3 or 1=1");
        checkEx("delete from user where 9007199254740992 not in (9007199254740993)", "large integer NOT IN precision");
        checkNotEx(
                "delete from user where not (id in (1) and 'it''s' like 'it_s')", "valid filter must not be blocked");
    }

    @Test
    void logicDeleteConditionWithIdFilter() {
        TableInfoHelper
                .initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), LogicDeleteUser.class);
        TableInfo tableInfo = TableInfoHelper.getTableInfo("logic_delete_user");
        assertThat(tableInfo).isNotNull();
        assertThat(tableInfo.isWithLogicDelete()).isTrue();
        assertThat(tableInfo.getLogicDeleteFieldInfo().getColumn()).isEqualTo("del_flag");

        checkEx("update logic_delete_user set name = null where del_flag = '0'", "logic delete only");
        checkNotEx(
                "update logic_delete_user set name = null where id in (?) and del_flag = '0'",
                "logic delete and id filter");
    }

    @TableName("logic_delete_user")
    static class LogicDeleteUser {

        @TableId
        private Long id;
        @TableField("del_flag")
        @TableLogic
        private Integer delFlag;
    }

    void checkEx(String sql, String as) {
        Exception e = null;
        try {
            interceptor.parserSingle(sql, null);
        } catch (Exception x) {
            e = x;
        }
        assertThat(e).as(as).isNotNull();
        assertThat(e).as(as).isInstanceOf(MybatisPlusException.class);
    }

    void checkNotEx(String sql, String as) {
        Exception e = null;
        try {
            interceptor.parserSingle(sql, null);
        } catch (Exception x) {
            e = x;
        }
        assertThat(e).as(as).isNull();
    }
}
