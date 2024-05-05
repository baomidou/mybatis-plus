package com.baomidou.mybatisplus.test.h2.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

/**
 * @author nieqiurong 2018/8/14 13:18.
 */
@Configuration
@MapperScan("com.baomidou.mybatisplus.test.h2.mapper")
public class MybatisXmlConfig {


    @Bean("mybatisSqlSession")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setTypeAliasesPackage("com.baomidou.mybatisplus.test.h2.entity.persistent");
        sqlSessionFactory.setConfigLocation(new ClassPathResource("mybatis-config-object-factory.xml"));
        sqlSessionFactory.setTypeEnumsPackage("com.baomidou.mybatisplus.test.h2.enums");
        return sqlSessionFactory.getObject();
    }

}
