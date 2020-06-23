package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.h2.Driver;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author miemie
 * @since 2020-06-23
 */
public abstract class BaseDbTest {

    protected SqlSessionFactory sqlSessionFactory;

    public BaseDbTest() {
        DataSource ds = dataSource();
        List<String> tableSql = tableSql();
        String tableDataSql = tableDataSql();
        String mapperXml = mapperXml();
        GlobalConfig globalConfig = globalConfig();
        List<Interceptor> interceptors = interceptors();
        List<Class<?>> mappers = mappers();

        JdbcTemplate template = new JdbcTemplate(ds);
        if (CollectionUtils.isNotEmpty(tableSql)) {
            for (String sql : tableSql) {
                if (StringUtils.isNotBlank(sql)) {
                    template.execute(sql);
                }
            }
        }

        if (StringUtils.isNotBlank(tableDataSql)) {
            template.execute(tableDataSql);
        }
        MybatisSqlSessionFactoryBuilder builder = new MybatisSqlSessionFactoryBuilder();
        Environment environment = new Environment("test", new JdbcTransactionFactory(), ds);
        MybatisConfiguration configuration = new MybatisConfiguration(environment);
        GlobalConfigUtils.setGlobalConfig(configuration, globalConfig);
        configuration.setLogImpl(Slf4jImpl.class);
        if (StringUtils.isNotBlank(mapperXml)) {
            ClassPathResource resource = new ClassPathResource(mapperXml);
            try {
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(resource.getInputStream(),
                    configuration, resource.toString(), configuration.getSqlFragments());
                xmlMapperBuilder.parse();
            } catch (IOException e) {
                throw ExceptionUtils.mpe(e);
            }
        }
        mappers.forEach(configuration::addMapper);
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

    protected <T> void doTest(Class<T> mapper, Consumer<T> consumer) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            doTest(sqlSession, mapper, consumer);
        }
    }

    protected <T> void doTest(SqlSession sqlSession, Class<T> mapper, Consumer<T> consumer) {
        T t = sqlSession.getMapper(mapper);
        consumer.accept(t);
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

    protected abstract List<Class<?>> mappers();

    protected GlobalConfig globalConfig() {
        return GlobalConfigUtils.defaults();
    }
}
