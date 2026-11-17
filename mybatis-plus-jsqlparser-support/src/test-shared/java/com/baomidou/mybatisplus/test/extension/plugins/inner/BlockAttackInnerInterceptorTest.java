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
        // 常量 BETWEEN 恒为真
        checkEx("update user set name = null where 1 between 0 and 2", "1 between 0 and 2");
        checkEx("update user set name = null where 'b' between 'a' and 'c'", "'b' between 'a' and 'c'");
        checkEx("update user set name = null where 1 not between 2 and 3", "1 not between 2 and 3");
        // 常量 IN 恒为真
        checkEx("update user set name = null where 1 in (1, 2)", "1 in (1, 2)");
        checkEx("update user set name = null where 'a' in ('a', 'b')", "'a' in ('a', 'b')");
        checkEx("update user set name = null where 1 not in (2, 3)", "1 not in (2, 3)");
        // 常量 LIKE 恒为真
        checkEx("update user set name = null where 'abc' like 'a%'", "'abc' like 'a%'");
        checkEx("update user set name = null where 'abc' like 'a_c'", "'abc' like 'a_c'");
        checkEx("update user set name = null where 'abc' not like 'x%'", "'abc' not like 'x%'");

        checkNotEx("update user set name = null where 1=?", "1=?");
        checkNotEx("update user set name = null where 1 between ? and ?", "1 between ? and ?");
        checkNotEx("update user set name = null where 1 in (?)", "1 in (?)");
        checkNotEx("update user set name = null where 'abc' like ?", "'abc' like ?");
    }

    @Test
    void delete() {
        checkEx("delete from user", "null where");
        checkEx("delete from user where 1=1", "1=1");
        checkEx("delete from user where 1<>2", "1<>2");
        checkEx("delete from user where 1!=2", "1!=2");
        checkEx("delete from user where 1=1 and 2=2", "1=1 and 2=2");
        checkEx("delete from user where 1=1 and 2=3 or 1=1", "1=1 and 2=3 or 1=1");
        // 常量 BETWEEN/IN/LIKE 恒为真
        checkEx("delete from user where 1 between 0 and 2", "1 between 0 and 2");
        checkEx("delete from user where 1 in (1, 2)", "1 in (1, 2)");
        checkEx("delete from user where 'abc' like 'a%'", "'abc' like 'a%'");
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
        checkEx("update logic_delete_user set name = null where del_flag in ('0')", "logic delete only via IN");
        checkEx(
                "update logic_delete_user set name = null where del_flag between '0' and '0'",
                "logic delete only via BETWEEN");
        checkEx("update logic_delete_user set name = null where del_flag like '0'", "logic delete only via LIKE");
        checkNotEx(
                "update logic_delete_user set name = null where id in (?) and del_flag = '0'",
                "logic delete and id filter");
        checkNotEx(
                "update logic_delete_user set name = null where id in (?) and del_flag in ('0')",
                "logic delete via IN and id filter");
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
