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
    void test() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.setSelfIncr(true, User::getRoleId, 1).setSelfDecr(true, User::getName, 1).eq(User::getId, 1);
        Assertions.assertEquals("role_id=role_id+1,username=username-1", wrapper.getSqlSet());
    }
}
