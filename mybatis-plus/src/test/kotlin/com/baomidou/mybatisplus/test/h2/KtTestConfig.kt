package com.baomidou.mybatisplus.test.h2

import com.baomidou.mybatisplus.core.MybatisConfiguration
import com.baomidou.mybatisplus.core.config.GlobalConfig
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean
import com.baomidou.mybatisplus.test.h2.config.DBConfig
import org.apache.ibatis.session.ExecutorType
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.type.EnumOrdinalTypeHandler
import org.apache.ibatis.type.JdbcType
import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import javax.sql.DataSource

/**
 * @author nieqiurong
 */
@Configuration
@Import(DBConfig::class)
@ComponentScan("com.baomidou.mybatisplus.test.h2.kotlin")
@MapperScan("com.baomidou.mybatisplus.test.h2.kotlin.mapper")
open class KtTestConfig {

    @Bean("sqlSessionFactory")
    open fun sqlSessionFactory(dataSource: DataSource): SqlSessionFactory? {
        val sqlSessionFactory = MybatisSqlSessionFactoryBean()
        sqlSessionFactory.setDataSource(dataSource)
        val configuration = MybatisConfiguration()
        configuration.jdbcTypeForNull = JdbcType.NULL
        configuration.isMapUnderscoreToCamelCase = true
        configuration.defaultExecutorType = ExecutorType.REUSE
        configuration.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler::class.java) //默认枚举处理
        sqlSessionFactory.configuration = configuration
        val mybatisPlusInterceptor = MybatisPlusInterceptor()
        mybatisPlusInterceptor.addInnerInterceptor(PaginationInnerInterceptor())
        sqlSessionFactory.setPlugins(mybatisPlusInterceptor)
        val globalConfig = GlobalConfig()
        globalConfig.addMetaObjectHandler(MyMetaObjectHandler())
        globalConfig.setSqlInjector(DefaultSqlInjector())
        sqlSessionFactory.setGlobalConfig(globalConfig)
        return sqlSessionFactory.getObject()
    }

}
