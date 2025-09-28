package com.baomidou.mybatisplus.test.gaussdb;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.huawei.gaussdb.jdbc.Driver;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.jdbc.SqlRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author nieqiurong
 * @since 3.5.13
 */
public class GaussdbTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GaussdbTest.class);

    private static final String CREATE_TABLE = """

        DROP TABLE IF EXISTS "demo";

        CREATE TABLE "demo" (
          "id" bigserial,
          "name" varchar(255),
          "age" int4,
          "birthday" date,
          "create_time" timestamp,
          PRIMARY KEY ("id")
        )
        ;

        COMMENT ON COLUMN "demo"."id" IS '主键';

        COMMENT ON COLUMN "demo"."name" IS '姓名';

        COMMENT ON COLUMN "demo"."age" IS '年龄';

        COMMENT ON COLUMN "demo"."birthday" IS '生日';

        COMMENT ON COLUMN "demo"."create_time" IS '创建时间';
        """;

    private static SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.addMapper(DemoMapper.class);
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        sqlSessionFactory.setPlugins(mybatisPlusInterceptor);
        sqlSessionFactory.setConfiguration(configuration);
        return sqlSessionFactory.getObject();
    }

    private static Stream<DbConfig> dataSource() {
        String host = "127.0.0.1";
        int port = 8000;
        String user = "root";
        String dbPassword = "123456";
        return Stream.of(
            new DbConfig("mysql", new PooledDataSource(Driver.class.getName(), "jdbc:gaussdb://" + host + ":" + port + "/test_mysql", user, dbPassword)),
            new DbConfig("oracle", new PooledDataSource(Driver.class.getName(), "jdbc:gaussdb://" + host + ":" + port + "/test_oracle", user, dbPassword))
        );
    }

    @Data
    @AllArgsConstructor
    private static class DbConfig {

        private final String name;

        private final DataSource dataSource;

        @Override
        public String toString() {
            return name;
        }

    }


    @ParameterizedTest
    @MethodSource("dataSource")
    void test(DbConfig dbConfig) throws Exception {
        DataSource dataSource = dbConfig.getDataSource();
        new SqlRunner(dataSource.getConnection()).run(CREATE_TABLE);
        try (SqlSession sqlSession = sqlSessionFactory(dataSource).openSession(true)) {
            DemoMapper demoMapper = sqlSession.getMapper(DemoMapper.class);
            LOGGER.info("delete all data:{}", demoMapper.delete(Wrappers.emptyWrapper()));

            Demo demo = new Demo();
            demo.setAge(12);
            demo.setName("12岁的用户");
            demo.setBirthday(LocalDate.of(2013, 1, 1));
            demo.setCreateTime(LocalDateTime.now());
            LOGGER.info("insert data:{}", demoMapper.insert(demo));

            Demo selectDemo = demoMapper.selectById(demo.getId());
            Assertions.assertNotNull(selectDemo);
            LOGGER.info("select data:{}", selectDemo);

            Demo updateDemo = new Demo();
            updateDemo.setAge(23);
            updateDemo.setId(selectDemo.getId());
            Assertions.assertEquals(1, demoMapper.updateById(updateDemo));

            LOGGER.info("delete by id:{}", demoMapper.deleteById(demo));

            List<Demo> demoList = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                demo = new Demo();
                demo.setAge(1);
                demo.setName("demo" + (i + 1));
                demo.setBirthday(LocalDate.of(2020, 12, 1));
                demo.setCreateTime(LocalDateTime.now());
                demoList.add(demo);
            }
            LOGGER.info("insert data on batch:{}", demoMapper.insert(demoList));

            Long count = demoMapper.selectCount(Wrappers.emptyWrapper());
            LOGGER.info("select count:{}", count);
            Assertions.assertEquals(50L, count);

            Page<Demo> page = demoMapper.selectPage(new Page<>(1, 10), Wrappers.emptyWrapper());
            Assertions.assertEquals(50, page.getTotal());
            Assertions.assertEquals(10, page.getSize());
            for (Demo record : page.getRecords()) {
                LOGGER.info("record:{}", record);
            }
            LOGGER.info("next page .");
            page = demoMapper.selectPage(new Page<>(2, 10), Wrappers.emptyWrapper());
            Assertions.assertEquals(50, page.getTotal());
            Assertions.assertEquals(10, page.getSize());
            for (Demo record : page.getRecords()) {
                LOGGER.info("record:{}", record);
            }
        }
    }

}
