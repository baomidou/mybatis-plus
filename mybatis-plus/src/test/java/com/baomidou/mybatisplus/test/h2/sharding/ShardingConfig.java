package com.baomidou.mybatisplus.test.h2.sharding;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.sharding.ShardingProcessor;
import com.baomidou.mybatisplus.extension.plugins.handler.sharding.ShardingRuleEnum;
import com.baomidou.mybatisplus.extension.plugins.handler.sharding.ShardingStrategy;
import com.baomidou.mybatisplus.extension.plugins.inner.ShardingInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;

/**
 * @author zengzhihong
 */
@Configuration
@MapperScan("com.baomidou.mybatisplus.test.h2.sharding.mapper")
public class ShardingConfig {


    @Bean("mybatisSqlSession")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, ResourceLoader resourceLoader,
                                               GlobalConfig globalConfig) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
//        sqlSessionFactory.setConfigLocation(resourceLoader.getResource("classpath:mybatis-config-object-factory
//        .xml"));
        MybatisConfiguration configuration = new MybatisConfiguration();
//        configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
//        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        /*
         * 下划线转驼峰开启
         */
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setDefaultExecutorType(ExecutorType.REUSE);
        configuration.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);  //默认枚举处理
        sqlSessionFactory.setConfiguration(configuration);
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:/sharding/*.xml"));
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        final ShardingStrategy orderShardingHashStrategy = new ShardingStrategy("sharding_order", "order_id", ShardingRuleEnum.ABSOLUTE, OrderShardingProcessor.class);

        interceptor.addInnerInterceptor(new ShardingInnerInterceptor(orderShardingHashStrategy));
        sqlSessionFactory.setPlugins(interceptor);
        return sqlSessionFactory.getObject();
    }

    static class OrderShardingProcessor implements ShardingProcessor {

        @Override
        public String doSharding(ShardingStrategy strategy, Map<String, List<Object>> shardingValues) {
            final List<Object> values = shardingValues.get("order_id");
            // value是一个集合 比如 in查询
            // 不管有几个value 此处最终return一个真实表名
            // 未携带分配字段会报错 所以shardingValues 一定是 notEmpty
            return "sharding_order_" + String.format("%02d", ((Long) values.get(0) % 3 + 1));
        }
    }


    @Bean
    public GlobalConfig globalConfiguration() {
        GlobalConfig conf = new GlobalConfig();
        conf.setEnableSqlRunner(true)
                .setDbConfig(new GlobalConfig.DbConfig()
                        .setLogicDeleteValue("1")
                        .setLogicNotDeleteValue("0")
                        .setIdType(IdType.ID_WORKER));
        return conf;
    }

}
