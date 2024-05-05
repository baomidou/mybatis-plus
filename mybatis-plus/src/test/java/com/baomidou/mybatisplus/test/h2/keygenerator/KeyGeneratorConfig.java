package com.baomidou.mybatisplus.test.h2.keygenerator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.H2KeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@MapperScan("com.baomidou.mybatisplus.test.h2.keygenerator.mapper")
public class KeyGeneratorConfig {

    @Bean("mybatisSqlSession")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, GlobalConfig globalConfig) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setDefaultExecutorType(ExecutorType.REUSE);
        configuration.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);
        sqlSessionFactory.setConfiguration(configuration);

        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        mybatisPlusInterceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        sqlSessionFactory.setPlugins(mybatisPlusInterceptor);

        sqlSessionFactory.setGlobalConfig(globalConfig);
        return sqlSessionFactory.getObject();
    }

    @Bean
    public GlobalConfig globalConfiguration() {
        GlobalConfig conf = new GlobalConfig();
        conf.setDbConfig(new GlobalConfig.DbConfig().setKeyGenerators(Arrays.asList(
            new H2KeyGenerator(),
            new IKeyGenerator() {

                @Override
                public String executeSql(String incrementerName) {
                    return "select nextval('" + incrementerName + "')";
                }

                @Override
                public DbType dbType() {
                    return DbType.POSTGRE_SQL;
                }
            }
        )));
        return conf;
    }
}
