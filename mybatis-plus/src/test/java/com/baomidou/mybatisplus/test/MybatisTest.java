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
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.core.toolkit.MybatisBatchUtils;
import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.mapper.H2UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;


/**
 * 原生Mybatis测试
 *
 * @author nieqiurong 2019/2/27.
 */
@ExtendWith(MockitoExtension.class)
class MybatisTest {

    private static SqlSessionFactory sqlSessionFactory;


    @BeforeAll
    public static void init() throws IOException, SQLException {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(reader);
        DataSource dataSource = sqlSessionFactory.getConfiguration().getEnvironment().getDataSource();
        Configuration configuration = sqlSessionFactory.getConfiguration();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        /*
         *  如果是将defaultEnumTypeHandler设置成MP的处理器,
         *  请自行注册处理非MP枚举处理类的原生枚举类型
         */
        typeHandlerRegistry.register(AgeEnum.class, MybatisEnumTypeHandler.class);     //这里我举起了个栗子
        Connection connection = dataSource.getConnection();
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        scriptRunner.runScript(Resources.getResourceAsReader("h2/user.ddl.sql"));
    }


    @Test
    void test(){
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
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
        sqlSession.close();

        System.out.println("------------------批量测试开始-----------------------------");
        sqlSession = sqlSessionFactory.openSession();
        mapper = sqlSession.getMapper(H2UserMapper.class);
        List<H2User> userList = Arrays.asList(new H2User(1000L, "测试"), new H2User(1001L, "测试"));
        try {
            MybatisBatchUtils.execute(sqlSessionFactory, userList, H2UserMapper.class.getName() + ".insert", parameter -> {
                if (parameter.getTestId() == 1001L) {
                    throw new RuntimeException("报错了");
                }
                return parameter;
            });
        } catch (Exception exception) {
            for (H2User u : userList) {
                Assertions.assertNull(mapper.selectById(u.getTestId()));
            }
        }

        userList = Arrays.asList(new H2User(2000L, "测试"), new H2User(2001L, "测试"));
        MybatisBatchUtils.execute(sqlSessionFactory, userList, H2UserMapper.class.getName() + ".insert");
        for (H2User u : userList) {
            Assertions.assertNotNull(mapper.selectById(u.getTestId()));
        }
        // 自动提交事务
        userList = Arrays.asList(new H2User(3000L,"测试"),new H2User(3001L,"测试"));
        sqlSession = sqlSessionFactory.openSession();
        mapper = sqlSession.getMapper(H2UserMapper.class);
        MybatisBatchUtils.execute(sqlSessionFactory, userList, true, H2UserMapper.class.getName() + ".insert");
        for (H2User u : userList) {
            Assertions.assertNotNull(mapper.selectById(u.getTestId()));
        }
        System.out.println("------------------批量测试结束-----------------------------");
    }

}
