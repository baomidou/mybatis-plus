package com.baomidou.mybatisplus.test.kotlin

import com.baomidou.mybatisplus.core.MybatisConfiguration
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder
import com.baomidou.mybatisplus.core.MybatisXMLMapperBuilder
import com.baomidou.mybatisplus.core.config.GlobalConfig
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils
import com.baomidou.mybatisplus.core.toolkit.StringUtils
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner
import org.apache.ibatis.io.Resources
import org.apache.ibatis.logging.slf4j.Slf4jImpl
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.plugin.Interceptor
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.ExecutorType
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory
import org.apache.ibatis.type.TypeReference
import org.h2.Driver
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.SimpleDriverDataSource
import java.io.IOException
import java.util.function.Consumer
import javax.sql.DataSource

/**
 * @author miemie
 * @since 2020-06-23
 */
abstract class BaseDbTest<T> : TypeReference<T>() {
    protected var sqlSessionFactory: SqlSessionFactory
    protected var mapper: Class<T>
    protected var jdbcTemplate: JdbcTemplate

    init {
        SqlRunner.DEFAULT.close()
        val ds = dataSource()
        val tableSql = tableSql()
        val tableDataSql = tableDataSql()
        val mapperXml = mapperXml()
        val globalConfig = globalConfig()
        val interceptors = interceptors()
        val consumer = consumer()
        mapper = rawType as Class<T>
        jdbcTemplate = JdbcTemplate(ds)
        if (CollectionUtils.isNotEmpty(tableSql)) {
            for (sql in tableSql!!) {
                if (StringUtils.isNotBlank(sql)) {
                    jdbcTemplate.execute(sql)
                }
            }
        }
        if (StringUtils.isNotBlank(tableDataSql)) {
            jdbcTemplate.execute(tableDataSql)
        }
        val builder = MybatisSqlSessionFactoryBuilder()
        val environment = Environment("test", ManagedTransactionFactory(), ds)
        val configuration = MybatisConfiguration(environment)
        consumer?.accept(configuration)
        GlobalConfigUtils.setGlobalConfig(configuration, globalConfig)
        configuration.logImpl = Slf4jImpl::class.java
        if (StringUtils.isNotBlank(mapperXml)) {
            try {
                val inputStream = Resources.getResourceAsStream(mapperXml)
                val xmlMapperBuilder = MybatisXMLMapperBuilder(
                    inputStream,
                    configuration, mapperXml, configuration.sqlFragments
                )
                xmlMapperBuilder.parse()
            } catch (e: IOException) {
                throw ExceptionUtils.mpe(e)
            }
        }
        configuration.addMapper(mapper)
        otherMapper().forEach(Consumer { type: Class<*>? -> configuration.addMapper(type) })
        if (CollectionUtils.isNotEmpty(interceptors)) {
            interceptors!!.forEach(Consumer { interceptor: Interceptor? -> configuration.addInterceptor(interceptor) })
        }
        sqlSessionFactory = builder.build(configuration)
    }

    private fun dataSource(): DataSource {
        val dataSource = SimpleDriverDataSource()
        dataSource.driver = Driver()
        dataSource.url = "jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
        dataSource.username = "sa"
        dataSource.password = ""
        return dataSource
    }

    protected fun sqlSession(type: ExecutorType?): SqlSession {
        return sqlSessionFactory.openSession(type)
    }

    protected fun doTest(consumer: Consumer<T>) {
        sqlSession(null).use { sqlSession -> doTest(sqlSession, consumer) }
    }

    protected fun doTestAutoCommit(consumer: Consumer<T>) {
        sqlSession(null).use { sqlSession -> doTestAutoCommit(sqlSession, consumer) }
    }

    protected fun doTest(sqlSession: SqlSession, consumer: Consumer<T>) {
        doMapper(sqlSession, false, consumer)
    }

    protected fun doTestAutoCommit(sqlSession: SqlSession, consumer: Consumer<T>) {
        doMapper(sqlSession, true, consumer)
    }

    protected fun doMapper(sqlSession: SqlSession, commit: Boolean, consumer: Consumer<T>) {
        val t = sqlSession.getMapper(mapper)
        consumer.accept(t)
        if (commit) {
            sqlSession.commit()
        } else {
            sqlSession.rollback()
        }
    }

    protected open fun tableSql(): List<String?>? {
        return null
    }

    protected open fun tableDataSql(): String? {
        return null
    }

    protected fun mapperXml(): String? {
        return null
    }

    protected fun interceptors(): List<Interceptor?>? {
        return null
    }

    protected fun globalConfig(): GlobalConfig {
        return GlobalConfigUtils.defaults()
    }

    protected fun consumer(): Consumer<Configuration>? {
        return null
    }

    protected fun otherMapper(): List<Class<*>> {
        return emptyList()
    }
}
