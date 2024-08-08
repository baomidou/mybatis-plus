package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.test.User;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * @author miemie
 * @since 2021-01-27
 */
class UpdateWrapperIncrDecrTest extends BaseWrapperTest {

    @BeforeAll
    static void initUser() {
        TableInfo tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), User.class);
        Assertions.assertEquals("sys_user", tableInfo.getTableName());
    }

    @Test
    void testIncrByAndDecrBy() {
        assertEquals(new UpdateWrapper<User>()
                .setIncrBy("role_id", 1).setDecrBy("username", 1),
            "role_id=role_id + 1,username=username - 1");

        assertEquals(new LambdaUpdateWrapper<User>()
                .setIncrBy(User::getRoleId, 1).setDecrBy(User::getName, 1),
            "role_id=role_id + 1,username=username - 1");
    }

    @Test
    void testIncrByAndDecrByBigDecimal() {
        assertEquals(new LambdaUpdateWrapper<User>()
                .setIncrBy(User::getRoleId, new BigDecimal("1"))
                .setIncrBy(User::getRoleId, new BigDecimal(1))
                .setIncrBy(User::getRoleId, new BigDecimal(1.0000))
                .setIncrBy(User::getRoleId, new BigDecimal("1.0000"))
                .setIncrBy(User::getRoleId, new BigDecimal("0.01"))
                .setIncrBy(User::getRoleId, new BigDecimal("2340")),
            "role_id=role_id + 1,role_id=role_id + 1,role_id=role_id + 1," +
                "role_id=role_id + 1.0000,role_id=role_id + 0.01,role_id=role_id + 2340");

        assertEquals(new LambdaUpdateWrapper<User>()
                .setDecrBy(User::getRoleId, new BigDecimal("1"))
                .setDecrBy(User::getRoleId, new BigDecimal(1))
                .setDecrBy(User::getRoleId, new BigDecimal(1.0000))
                .setDecrBy(User::getRoleId, new BigDecimal("1.0000"))
                .setDecrBy(User::getRoleId, new BigDecimal("0.01"))
                .setDecrBy(User::getRoleId, new BigDecimal("2340")),
            "role_id=role_id - 1,role_id=role_id - 1,role_id=role_id - 1," +
                "role_id=role_id - 1.0000,role_id=role_id - 0.01,role_id=role_id - 2340");
    }


    private void assertEquals(Update<?, ?> update, String sql) {
        Assertions.assertEquals(sql, update.getSqlSet());
    }
}
