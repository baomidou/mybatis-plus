package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.test.mapper.AMapper;
import com.baomidou.mybatisplus.test.mapper.BMapper;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.session.*;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author nieqiurong 2019/2/23.
 */
class MybatisConfigurationTest {

    @Test
    void testXml() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config-empty.xml");
        SqlSessionFactory factory = new MybatisSqlSessionFactoryBuilder().build(reader);
        Configuration configuration = factory.getConfiguration();
        Assertions.assertTrue(configuration.isCacheEnabled());
        //3.4.1以下是true
        Assertions.assertFalse(configuration.isAggressiveLazyLoading());
        Assertions.assertTrue(configuration.isMultipleResultSetsEnabled());
        Assertions.assertTrue(configuration.isUseColumnLabel());
        Assertions.assertFalse(configuration.isUseGeneratedKeys());
        Assertions.assertEquals(AutoMappingBehavior.PARTIAL,configuration.getAutoMappingBehavior());
        Assertions.assertEquals(AutoMappingUnknownColumnBehavior.NONE,configuration.getAutoMappingUnknownColumnBehavior());
        Assertions.assertEquals(ExecutorType.SIMPLE,configuration.getDefaultExecutorType());
        Assertions.assertNull(configuration.getDefaultStatementTimeout());
        Assertions.assertNull(configuration.getDefaultFetchSize());
        Assertions.assertFalse(configuration.isSafeRowBoundsEnabled());
        Assertions.assertTrue(configuration.isSafeResultHandlerEnabled());
        //这里原生的是false
        Assertions.assertTrue(configuration.isMapUnderscoreToCamelCase());
        Assertions.assertEquals(LocalCacheScope.SESSION,configuration.getLocalCacheScope());
        Assertions.assertEquals(JdbcType.OTHER,configuration.getJdbcTypeForNull());
        Assertions.assertEquals(Stream.of("equals","clone","hashCode","toString").collect(Collectors.toSet()), configuration.getLazyLoadTriggerMethods());
        Assertions.assertEquals(MybatisXMLLanguageDriver.class,configuration.getDefaultScriptingLanguageInstance().getClass());
        Assertions.assertFalse(configuration.isCallSettersOnNulls());
        Assertions.assertFalse(configuration.isReturnInstanceForEmptyRow());
        Assertions.assertNull(configuration.getLogPrefix());
        Assertions.assertNull(configuration.getLogImpl());
        Assertions.assertEquals(JavassistProxyFactory.class,configuration.getProxyFactory().getClass());
        Assertions.assertNull(configuration.getVfsImpl());
        Assertions.assertTrue(configuration.isUseActualParamName());
        Assertions.assertNull(configuration.getConfigurationFactory());

    }

    @Test
    void testBean() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        Assertions.assertTrue(configuration.isCacheEnabled());
        //3.4.1以下是true
        Assertions.assertFalse(configuration.isAggressiveLazyLoading());
        Assertions.assertTrue(configuration.isMultipleResultSetsEnabled());
        Assertions.assertTrue(configuration.isUseColumnLabel());
        Assertions.assertFalse(configuration.isUseGeneratedKeys());
        Assertions.assertEquals(AutoMappingBehavior.PARTIAL,configuration.getAutoMappingBehavior());
        Assertions.assertEquals(AutoMappingUnknownColumnBehavior.NONE,configuration.getAutoMappingUnknownColumnBehavior());
        Assertions.assertEquals(ExecutorType.SIMPLE,configuration.getDefaultExecutorType());
        Assertions.assertNull(configuration.getDefaultStatementTimeout());
        Assertions.assertNull(configuration.getDefaultFetchSize());
        Assertions.assertFalse(configuration.isSafeRowBoundsEnabled());
        Assertions.assertTrue(configuration.isSafeResultHandlerEnabled());
        //这里原生的是false
        Assertions.assertTrue(configuration.isMapUnderscoreToCamelCase());
        Assertions.assertEquals(LocalCacheScope.SESSION,configuration.getLocalCacheScope());
        Assertions.assertEquals(JdbcType.OTHER,configuration.getJdbcTypeForNull());
        Assertions.assertEquals(Stream.of("equals","clone","hashCode","toString").collect(Collectors.toSet()), configuration.getLazyLoadTriggerMethods());
        Assertions.assertEquals(MybatisXMLLanguageDriver.class,configuration.getDefaultScriptingLanguageInstance().getClass());
        Assertions.assertFalse(configuration.isCallSettersOnNulls());
        Assertions.assertFalse(configuration.isReturnInstanceForEmptyRow());
        Assertions.assertNull(configuration.getLogPrefix());
        Assertions.assertNull(configuration.getLogImpl());
        Assertions.assertEquals(JavassistProxyFactory.class,configuration.getProxyFactory().getClass());
        Assertions.assertNull(configuration.getVfsImpl());
        Assertions.assertTrue(configuration.isUseActualParamName());
        Assertions.assertNull(configuration.getConfigurationFactory());
    }

    @Test
    void testUseGeneratedShortKey(){
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setUseGeneratedShortKey(true);
        addMappedStatement(configuration, 200);
        Assertions.assertEquals(400, configuration.getMappedStatements().size());

        configuration = new MybatisConfiguration();
        configuration.setUseGeneratedShortKey(false);
        addMappedStatement(configuration, 200);
        Assertions.assertEquals(200, configuration.getMappedStatements().size());
    }

    private void addMappedStatement(Configuration configuration, int size) {
        for (int i = 0; i < size; i++) {
            configuration.addMappedStatement(
                new MappedStatement.Builder(configuration, "com.baomidou.test.method" + i,
                    new StaticSqlSource(configuration, "select * from test"), SqlCommandType.SELECT)
                    .statementType(StatementType.STATEMENT).resource("xxxxxx").useCache(true).keyGenerator(NoKeyGenerator.INSTANCE)
                    .build()
            );
        }
    }

    @Test
    void testReload() {
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.addMapper(BMapper.class);
        mybatisConfiguration.addMapper(AMapper.class);
        Assertions.assertEquals(39, mybatisConfiguration.getMappedStatementNames().size());
        Assertions.assertEquals(4, mybatisConfiguration.getSqlFragments().size());
        Assertions.assertEquals(2, mybatisConfiguration.getResultMaps().size());
        Assertions.assertEquals(2, mybatisConfiguration.getCaches().size());
        Assertions.assertEquals(2, mybatisConfiguration.getMapperRegistry().getMappers().size());
        mybatisConfiguration.addNewMapper(BMapper.class);
        mybatisConfiguration.addNewMapper(AMapper.class);
        Assertions.assertEquals(39, mybatisConfiguration.getMappedStatementNames().size());
        Assertions.assertEquals(4, mybatisConfiguration.getSqlFragments().size());
        Assertions.assertEquals(2, mybatisConfiguration.getResultMaps().size());
        Assertions.assertEquals(2, mybatisConfiguration.getCaches().size());
        Assertions.assertEquals(2, mybatisConfiguration.getMapperRegistry().getMappers().size());
    }

}
