/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.mapper.H2UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * 原生Mybatis测试
 *
 * @author nieqiurong 2019/2/27.
 */
@ExtendWith(MockitoExtension.class)
class MybatisTest {

    @Test
    void test() throws IOException, SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUser("sa");
        dataSource.setPassword("");
        dataSource.setUrl("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory factory = new MybatisSqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession = factory.openSession(dataSource.getConnection());
        Configuration configuration = factory.getConfiguration();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        /*
         *  如果是将defaultEnumTypeHandler设置成MP的处理器,
         *  请自行注册处理非MP枚举处理类的原生枚举类型
         */
        typeHandlerRegistry.register(AgeEnum.class, MybatisEnumTypeHandler.class);     //这里我举起了个栗子
        Connection connection = dataSource.getConnection();
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        scriptRunner.runScript(Resources.getResourceAsReader("h2/user.ddl.sql"));
        H2UserMapper mapper = sqlSession.getMapper(H2UserMapper.class);
        Assertions.assertEquals(mapper.myInsertWithNameVersion("test", 2), 1);
        Assertions.assertEquals(mapper.insert(new H2User("test")), 1);
        Assertions.assertEquals(mapper.selectCount(new QueryWrapper<H2User>().lambda().eq(H2User::getName, "test")), 2);
        Assertions.assertEquals(mapper.delete(new QueryWrapper<H2User>().lambda().eq(H2User::getName, "test")), 2);
        H2User h2User = new H2User(66L, "66666", AgeEnum.THREE, 666);
        Assertions.assertEquals(mapper.insert(h2User), 1);
        h2User.setName("7777777777");
        H2User user = mapper.selectById(66L);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.getAge(), AgeEnum.THREE);
        Assertions.assertNotNull(user.getTestType());
        Assertions.assertEquals(mapper.updateById(new H2User(66L, "777777")), 1);
        Assertions.assertEquals(mapper.deleteById(66L), 1);
        Assertions.assertNull(mapper.selectById(66L));
    }

}
