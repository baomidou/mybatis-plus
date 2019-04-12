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

package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.MybatisMapperRegistry;
import com.baomidou.mybatisplus.core.override.MybatisMapperMethod;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxyFactory;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.baomidou.mybatisplus.test.h2.config.DBConfig;
import com.baomidou.mybatisplus.test.h2.config.MybatisPlusConfig;
import com.baomidou.mybatisplus.test.h2.mapper.H2StudentMapper;
import com.baomidou.mybatisplus.test.h2.mapper.H2UserMapper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author nieqiurong 2019/4/12.
 */
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@MapperScan(value = "com.baomidou.mybatisplus.test.h2")
@ContextConfiguration(classes = {MybatisMapperRegistryTest.class, DBConfig.class})
class MybatisMapperRegistryTest extends BaseTest {
    
    private interface H2StudentChildrenMapper extends H2StudentMapper {
    
    }
    
    @Bean
    DBConfig dbConfig() {
        return new DBConfig();
    }
    
    @Bean
    MybatisPlusConfig mybatisPlusConfig() {
        return new MybatisPlusConfig();
    }
    
    @Bean
    SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        return sqlSessionFactory.getObject();
    }
    
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    
    @SuppressWarnings("unchecked")
    @Test
    void test() throws ReflectiveOperationException {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            MybatisMapperRegistry mapperRegistry = (MybatisMapperRegistry) sqlSessionFactory.getConfiguration().getMapperRegistry();
            Assertions.assertTrue(mapperRegistry.hasMapper(H2UserMapper.class));
            Assertions.assertTrue(mapperRegistry.hasMapper(H2StudentChildrenMapper.class));
            H2StudentMapper studentMapper = mapperRegistry.getMapper(H2StudentMapper.class, sqlSession);
            
            Assertions.assertTrue(configuration.hasStatement(H2StudentMapper.class.getName() + ".selectById"));
            studentMapper.selectById(1);
            
            Field field = mapperRegistry.getClass().getDeclaredField("knownMappers");
            field.setAccessible(true);
            Map<Class<?>, MybatisMapperProxyFactory<?>> knownMappers = (Map<Class<?>, MybatisMapperProxyFactory<?>>) field.get(mapperRegistry);
            MybatisMapperProxyFactory<?> mybatisMapperProxyFactory = knownMappers.get(H2StudentChildrenMapper.class);
            
            
            H2StudentChildrenMapper h2StudentChildrenMapper = mapperRegistry.getMapper(H2StudentChildrenMapper.class, sqlSession);
            Assertions.assertFalse(configuration.hasStatement(H2StudentChildrenMapper.class.getName() + ".selectById"));
            Map<Method, MybatisMapperMethod> methodCache = mybatisMapperProxyFactory.getMethodCache();
            Assertions.assertTrue(methodCache.isEmpty());
            
            h2StudentChildrenMapper.selectById(2);
            methodCache = mybatisMapperProxyFactory.getMethodCache();
            Assertions.assertFalse(methodCache.isEmpty());
        }
    }
}
