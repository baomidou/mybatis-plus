package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
class LambdaUpdateWrapperTest extends BaseWrapperTest {

    @BeforeAll
    static void initUser() {
        TableInfo tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), User.class);
        Assertions.assertEquals("sys_user", tableInfo.getTableName());
    }

    @Test
    void testIncrByAndDecrBy() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.setIncrBy(true, User::getRoleId, 1).setDecrBy(true, User::getName, 1).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id+1,username=username-1", wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setIncrBy(User::getRoleId, 1).setIncrBy(User::getName, 1).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id+1,username=username+1", wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setIncrBy(false, User::getRoleId, 1).setIncrBy(User::getName, 1).eq(User::getId, 1);
        Assertions.assertEquals("username=username+1", wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setDecrBy(User::getRoleId, 1).setDecrBy(User::getName, 1).eq(false, User::getId, 1);
        Assertions.assertEquals("role_id=role_id-1,username=username-1", wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setDecrBy(true, User::getRoleId, 1).setDecrBy(true, User::getName, 1).eq(false, User::getId, 1);
        Assertions.assertEquals("role_id=role_id-1,username=username-1", wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setDecrBy(false, User::getRoleId, 1).setDecrBy(User::getName, 1).eq(User::getId, 1);
        Assertions.assertEquals("username=username-1", wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setDecrBy(User::getRoleId, 1).setDecrBy(false, User::getName, 1).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id-1", wrapper.getSqlSet());


    }

    @Test
    void testIncrByAndDecrByBigDecimal() {
        var wrapper = new LambdaUpdateWrapper<User>();
        wrapper.setDecrBy(User::getRoleId, new BigDecimal("1")).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id-1", wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setDecrBy(User::getRoleId, new BigDecimal(1)).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id-1", wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setDecrBy(User::getRoleId, new BigDecimal(1.0000)).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id-1", wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setDecrBy(User::getRoleId, new BigDecimal("1.0000")).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id-1.0000", wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setDecrBy(User::getRoleId, new BigDecimal(0.01)).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id-" + new BigDecimal(0.01), wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setDecrBy(User::getRoleId, new BigDecimal("0.01")).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id-0.01", wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setDecrBy(User::getRoleId, new BigDecimal("2340").setScale(-1)).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id-2340", wrapper.getSqlSet());


        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setIncrBy(User::getRoleId, new BigDecimal("1")).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id+1", wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setIncrBy(User::getRoleId, new BigDecimal(1)).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id+1", wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setIncrBy(User::getRoleId, new BigDecimal(1.0000)).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id+1", wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setIncrBy(User::getRoleId, new BigDecimal("1.0000")).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id+1.0000", wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setIncrBy(User::getRoleId, new BigDecimal(0.01)).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id+" + new BigDecimal(0.01), wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setIncrBy(User::getRoleId, new BigDecimal("0.01")).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id+0.01", wrapper.getSqlSet());

        wrapper = new LambdaUpdateWrapper<>();
        wrapper.setIncrBy(User::getRoleId, new BigDecimal("2340").setScale(-1)).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id+2340", wrapper.getSqlSet());
    }

}
