package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
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
        // 常量 BETWEEN / IN / LIKE 条件同样无法过滤数据
        checkEx("update user set name = null where 1 between 1 and 2", "1 between 1 and 2");
        checkEx("update user set name = null where 1 not between 2 and 3", "1 not between 2 and 3");
        checkEx("update user set name = null where 1 in (1, 2)", "1 in (1, 2)");
        checkEx("update user set name = null where 1 not in (2, 3)", "1 not in (2, 3)");
        checkEx("update user set name = null where 'a' like '%'", "'a' like '%'");
        checkEx("update user set name = null where 'a' not like 'b'", "'a' not like 'b'");

        checkNotEx("update user set name = null where 1=?", "1=?");
        // 操作数包含参数或字段时沿用原有判断
        checkNotEx("update user set name = null where 1 between ? and ?", "1 between ? and ?");
        checkNotEx("update user set name = null where 1 in (?)", "1 in (?)");
        checkNotEx("update user set name = null where 'a' like ?", "'a' like ?");
        checkNotEx("update user set name = null where id in (1, 2)", "id in (1, 2)");
        checkNotEx("update user set name = null where name like 'a%'", "name like 'a%'");
    }

    @Test
    void delete() {
        checkEx("delete from user", "null where");
        checkEx("delete from user where 1=1", "1=1");
        checkEx("delete from user where 1<>2", "1<>2");
        checkEx("delete from user where 1!=2", "1!=2");
        checkEx("delete from user where 1=1 and 2=2", "1=1 and 2=2");
        checkEx("delete from user where 1=1 and 2=3 or 1=1", "1=1 and 2=3 or 1=1");
        // 常量 BETWEEN / IN / LIKE 条件同样无法过滤数据
        checkEx("delete from user where 1 between 1 and 2", "1 between 1 and 2");
        checkEx("delete from user where 1 in (1, 2)", "1 in (1, 2)");
        checkEx("delete from user where 'a' like '%'", "'a' like '%'");
        checkNotEx("delete from user where id in (1, 2)", "id in (1, 2)");
    }

    @Test
    void constantConditionInCombinations() {
        // 常量条件在 AND 中只说明该条件恒真,整个条件仍可能过滤数据
        checkNotEx("update user set name = null where id > ? and 1 in (1, 2)", "id > ? and 1 in (1, 2)");
        checkNotEx("delete from user where id > ? and 'a' like '%'", "id > ? and 'a' like '%'");
        checkNotEx("update user set name = null where 1 between 1 and 2 and id > ?", "1 between 1 and 2 and id > ?");
        // 常量条件在 OR 中使整个条件恒真
        checkEx("update user set name = null where id > ? or 1 in (1, 2)", "id > ? or 1 in (1, 2)");
        checkEx("delete from user where id > ? or 'a' like '%'", "id > ? or 'a' like '%'");
        checkEx("delete from user where 1 between 1 and 2 or id > ?", "1 between 1 and 2 or id > ?");
        // NOT 取反后不再匹配全部数据
        checkNotEx("update user set name = null where not (1 in (1, 2))", "not (1 in (1, 2))");
        checkNotEx("delete from user where not ('a' like '%')", "not ('a' like '%')");
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
