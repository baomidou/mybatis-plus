package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.MybatisMapperRegistry;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxyFactory;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import com.baomidou.mybatisplus.test.h2.config.DBConfig;
import com.baomidou.mybatisplus.test.h2.config.MybatisPlusConfig;
import com.baomidou.mybatisplus.test.h2.mapper.H2StudentMapper;
import com.baomidou.mybatisplus.test.h2.mapper.H2UserMapper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2019/4/12.
 */
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@MapperScan(value = "com.baomidou.mybatisplus.test.h2")
@ContextConfiguration(classes = {MybatisMapperRegistryTest.class, DBConfig.class})
class MybatisMapperRegistryTest extends H2TestSupport {

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
            MybatisMapperRegistry mapperRegistry = (MybatisMapperRegistry) sqlSessionFactory.getConfiguration()
                    .getMapperRegistry();
            assertThat(mapperRegistry.hasMapper(H2UserMapper.class)).isTrue();
            assertThat(mapperRegistry.hasMapper(H2StudentChildrenMapper.class)).isTrue();
            H2StudentMapper studentMapper = mapperRegistry.getMapper(H2StudentMapper.class, sqlSession);

            assertThat(configuration.hasStatement(H2StudentMapper.class.getName() + ".selectById")).isTrue();
            studentMapper.selectById(1);

            Field field = mapperRegistry.getClass().getDeclaredField("knownMappers");
            field.setAccessible(true);
            Map<Class<?>, MybatisMapperProxyFactory<?>> knownMappers = (Map<Class<?>, MybatisMapperProxyFactory<?>>) field
                    .get(mapperRegistry);
            MybatisMapperProxyFactory<?> mybatisMapperProxyFactory = knownMappers.get(H2StudentChildrenMapper.class);

            H2StudentChildrenMapper h2StudentChildrenMapper = mapperRegistry
                    .getMapper(H2StudentChildrenMapper.class, sqlSession);
            assertThat(configuration.hasStatement(H2StudentChildrenMapper.class.getName() + ".selectById")).isTrue();
            Map<Method, ?> methodCache = mybatisMapperProxyFactory.getMethodCache();
            assertThat(methodCache.isEmpty()).isTrue();

            h2StudentChildrenMapper.selectById(2);
            methodCache = mybatisMapperProxyFactory.getMethodCache();
            assertThat(methodCache.isEmpty()).isFalse();
        }
    }
}
