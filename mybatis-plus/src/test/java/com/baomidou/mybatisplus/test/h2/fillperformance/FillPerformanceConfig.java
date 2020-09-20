package com.baomidou.mybatisplus.test.h2.fillperformance;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.handlers.StrictFill;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.reflection.MetaObject;
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
@MapperScan("com.baomidou.mybatisplus.test.h2.fillperformance.mapper")
public class FillPerformanceConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setDefaultExecutorType(ExecutorType.REUSE);
        configuration.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);
        sqlSessionFactory.setConfiguration(configuration);
        sqlSessionFactory.setGlobalConfig(new GlobalConfig().setMetaObjectHandler(metaObjectHandler()));
        PaginationInterceptor pagination = new PaginationInterceptor();
        sqlSessionFactory.setPlugins(pagination);
        return sqlSessionFactory.getObject();
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
//                strictInsertFill(metaObject,"c",String.class,"1234567890");
//                strictInsertFill(metaObject,"d",String.class,"1234567890");
//                strictInsertFill(metaObject,"e",String.class,"1234567890");
//                strictInsertFill(metaObject,"f",String.class,"1234567890");
//                strictInsertFill(metaObject,"g",String.class,"1234567890");
//                strictInsertFill(metaObject,"h",String.class,"1234567890");
//                strictInsertFill(metaObject,"i",String.class,"1234567890");
//                strictInsertFill(metaObject,"j",String.class,"1234567890");
//                strictInsertFill(metaObject,"l",String.class,"1234567890");
//                strictInsertFill(metaObject,"m",String.class,"1234567890");
                setFieldValByName("c","1234567890",metaObject);
                setFieldValByName("d","1234567890",metaObject);
                setFieldValByName("e","1234567890",metaObject);
                setFieldValByName("f","1234567890",metaObject);
                setFieldValByName("g","1234567890",metaObject);
                setFieldValByName("h","1234567890",metaObject);
                setFieldValByName("i","1234567890",metaObject);
                setFieldValByName("j","1234567890",metaObject);
                setFieldValByName("l","1234567890",metaObject);
                setFieldValByName("m","1234567890",metaObject);
//                setInsertFieldValByName("c","1234567890",metaObject);
//                setInsertFieldValByName("d","1234567890",metaObject);
//                setInsertFieldValByName("e","1234567890",metaObject);
//                setInsertFieldValByName("f","1234567890",metaObject);
//                setInsertFieldValByName("g","1234567890",metaObject);
//                setInsertFieldValByName("h","1234567890",metaObject);
//                setInsertFieldValByName("i","1234567890",metaObject);
//                setInsertFieldValByName("j","1234567890",metaObject);
//                setInsertFieldValByName("l","1234567890",metaObject);
//                setInsertFieldValByName("m","1234567890",metaObject);

//                strictInsertFill(findTableInfo(metaObject),metaObject, Arrays.asList(
//                    StrictFill.of("c",String.class,"1234567890"),
//                    StrictFill.of("d",String.class,"1234567890"),
//                    StrictFill.of("e",String.class,"1234567890"),
//                    StrictFill.of("f",String.class,"1234567890"),
//                    StrictFill.of("g",String.class,"1234567890"),
//                    StrictFill.of("h",String.class,"1234567890"),
//                    StrictFill.of("i",String.class,"1234567890"),
//                    StrictFill.of("j",String.class,"1234567890"),
//                    StrictFill.of("l",String.class,"1234567890"),
//                    StrictFill.of("m",String.class,"1234567890")
//                ));
            }

            @Override
            public void updateFill(MetaObject metaObject) {

            }
        };
    }

}
