package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.test.User;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2021-01-27
 */
@Tag("unit")
class UpdateWrapperIncrDecrTest extends WrapperTestSupport {

    @BeforeAll
    static void initUser() {
        TableInfo tableInfo = TableInfoHelper
                .initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), User.class);
        assertThat(tableInfo.getTableName()).isEqualTo("sys_user");
    }

    @Test
    void shouldGenerateIncrByAndDecrByForIntValues() {
        assertSqlSet(
                new UpdateWrapper<User>().setIncrBy("role_id", 1).setDecrBy("username", 1),
                "role_id=role_id + 1,username=username - 1");

        assertSqlSet(
                new LambdaUpdateWrapper<User>().setIncrBy(User::getRoleId, 1).setDecrBy(User::getName, 1),
                "role_id=role_id + 1,username=username - 1");
    }

    @Test
    void shouldGenerateIncrByAndDecrByForBigDecimalValues() {
        assertSqlSet(
                new LambdaUpdateWrapper<User>().setIncrBy(User::getRoleId, new BigDecimal("1"))
                        .setIncrBy(User::getRoleId, new BigDecimal(1))
                        .setIncrBy(User::getRoleId, new BigDecimal(1.0000))
                        .setIncrBy(User::getRoleId, new BigDecimal("1.0000"))
                        .setIncrBy(User::getRoleId, new BigDecimal("0.01"))
                        .setIncrBy(User::getRoleId, new BigDecimal("2340")),
                "role_id=role_id + 1,role_id=role_id + 1,role_id=role_id + 1,"
                        + "role_id=role_id + 1.0000,role_id=role_id + 0.01,role_id=role_id + 2340");

        assertSqlSet(
                new LambdaUpdateWrapper<User>().setDecrBy(User::getRoleId, new BigDecimal("1"))
                        .setDecrBy(User::getRoleId, new BigDecimal(1))
                        .setDecrBy(User::getRoleId, new BigDecimal(1.0000))
                        .setDecrBy(User::getRoleId, new BigDecimal("1.0000"))
                        .setDecrBy(User::getRoleId, new BigDecimal("0.01"))
                        .setDecrBy(User::getRoleId, new BigDecimal("2340")),
                "role_id=role_id - 1,role_id=role_id - 1,role_id=role_id - 1,"
                        + "role_id=role_id - 1.0000,role_id=role_id - 0.01,role_id=role_id - 2340");
    }

    private void assertSqlSet(Update<?, ?> update, String sql) {
        assertThat(update.getSqlSet()).isEqualTo(sql);
    }
}
