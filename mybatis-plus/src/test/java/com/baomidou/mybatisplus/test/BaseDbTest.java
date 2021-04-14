package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.apache.ibatis.type.TypeReference;
import org.h2.Driver;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author miemie
 * @since 2020-06-23
 */
public abstract class BaseDbTest<T> extends TypeReference<T> {

    protected SqlSessionFactory sqlSessionFactory;
    protected Class<T> mapper;
    protected JdbcTemplate jdbcTemplate;

    @SuppressWarnings("unchecked")
    public BaseDbTest() {
        DataSource ds = dataSource();
        List<String> tableSql = tableSql();
        String tableDataSql = tableDataSql();
        String mapperXml = mapperXml();
        GlobalConfig globalConfig = globalConfig();
        List<Interceptor> interceptors = interceptors();
        Consumer<Configuration> consumer = consumer();
        mapper = (Class<T>) getRawType();

        jdbcTemplate = new JdbcTemplate(ds);
        if (CollectionUtils.isNotEmpty(tableSql)) {
            for (String sql : tableSql) {
                if (StringUtils.isNotBlank(sql)) {
                    jdbcTemplate.execute(sql);
                }
            }
        }

        if (StringUtils.isNotBlank(tableDataSql)) {
            jdbcTemplate.execute(tableDataSql);
        }
        MybatisSqlSessionFactoryBuilder builder = new MybatisSqlSessionFactoryBuilder();
        Environment environment = new Environment("test", new ManagedTransactionFactory(), ds);
        MybatisConfiguration configuration = new MybatisConfiguration(environment);
        configuration.setUseDeprecatedExecutor(false);
        if (consumer != null) {
            consumer.accept(configuration);
        }
        GlobalConfigUtils.setGlobalConfig(configuration, globalConfig);
        configuration.setLogImpl(Slf4jImpl.class);
        if (StringUtils.isNotBlank(mapperXml)) {
            try {
                InputStream inputStream = Resources.getResourceAsStream(mapperXml);
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(inputStream,
                    configuration, mapperXml, configuration.getSqlFragments());
                xmlMapperBuilder.parse();
            } catch (IOException e) {
                throw ExceptionUtils.mpe(e);
            }
        }
        configuration.addMapper(mapper);
        if (CollectionUtils.isNotEmpty(interceptors)) {
            interceptors.forEach(configuration::addInterceptor);
        }
        sqlSessionFactory = builder.build(configuration);
    }

    private DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new Driver());
        dataSource.setUrl("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    protected SqlSession sqlSession(@Nullable ExecutorType type) {
        return sqlSessionFactory.openSession(type);
    }

    protected void doTest(Consumer<T> consumer) {
        try (SqlSession sqlSession = sqlSession(null)) {
            doTest(sqlSession, consumer);
        }
    }

    protected void doTestAutoCommit(Consumer<T> consumer) {
        try (SqlSession sqlSession = sqlSession(null)) {
            doTestAutoCommit(sqlSession, consumer);
        }
    }

    protected void doTest(SqlSession sqlSession, Consumer<T> consumer) {
        doMapper(sqlSession, false, consumer);
    }

    protected void doTestAutoCommit(SqlSession sqlSession, Consumer<T> consumer) {
        doMapper(sqlSession, true, consumer);
    }

    protected void doMapper(SqlSession sqlSession, boolean commit, Consumer<T> consumer) {
        T t = sqlSession.getMapper(mapper);
        consumer.accept(t);
        if (commit) {
            sqlSession.commit();
        } else {
            sqlSession.rollback();
        }
    }

    protected List<String> tableSql() {
        return null;
    }

    protected String tableDataSql() {
        return null;
    }

    protected String mapperXml() {
        return null;
    }

    protected List<Interceptor> interceptors() {
        return null;
    }

    protected GlobalConfig globalConfig() {
        return GlobalConfigUtils.defaults();
    }

    protected Consumer<Configuration> consumer() {
        return null;
    }
}
