package com.baomidou.mybatisplus.core;

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
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2019/2/23.
 */
class MybatisConfigurationTest {

    @Test
    void testXml() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config-empty.xml");
        SqlSessionFactory factory = new MybatisSqlSessionFactoryBuilder().build(reader);
        Configuration configuration = factory.getConfiguration();
        assertThat(configuration.isCacheEnabled()).isTrue();
        //3.4.1以下是true
        assertThat(configuration.isAggressiveLazyLoading()).isFalse();
        assertThat(configuration.isMultipleResultSetsEnabled()).isTrue();
        assertThat(configuration.isUseColumnLabel()).isTrue();
        assertThat(configuration.isUseGeneratedKeys()).isFalse();
        assertThat(configuration.getAutoMappingBehavior()).isEqualTo(AutoMappingBehavior.PARTIAL);
        assertThat(configuration.getAutoMappingUnknownColumnBehavior())
                .isEqualTo(AutoMappingUnknownColumnBehavior.NONE);
        assertThat(configuration.getDefaultExecutorType()).isEqualTo(ExecutorType.SIMPLE);
        assertThat(configuration.getDefaultStatementTimeout()).isNull();
        assertThat(configuration.getDefaultFetchSize()).isNull();
        assertThat(configuration.isSafeRowBoundsEnabled()).isFalse();
        assertThat(configuration.isSafeResultHandlerEnabled()).isTrue();
        //这里原生的是false
        assertThat(configuration.isMapUnderscoreToCamelCase()).isTrue();
        assertThat(configuration.getLocalCacheScope()).isEqualTo(LocalCacheScope.SESSION);
        assertThat(configuration.getJdbcTypeForNull()).isEqualTo(JdbcType.OTHER);
        assertThat(configuration.getLazyLoadTriggerMethods())
                .isEqualTo(Stream.of("equals", "clone", "hashCode", "toString").collect(Collectors.toSet()));
        assertThat(configuration.getDefaultScriptingLanguageInstance().getClass())
                .isEqualTo(MybatisXMLLanguageDriver.class);
        assertThat(configuration.isCallSettersOnNulls()).isFalse();
        assertThat(configuration.isReturnInstanceForEmptyRow()).isFalse();
        assertThat(configuration.getLogPrefix()).isNull();
        assertThat(configuration.getLogImpl()).isNull();
        assertThat(configuration.getProxyFactory().getClass()).isEqualTo(JavassistProxyFactory.class);
        assertThat(configuration.getVfsImpl()).isNull();
        assertThat(configuration.isUseActualParamName()).isTrue();
        assertThat(configuration.getConfigurationFactory()).isNull();

    }

    @Test
    void testBean() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        assertThat(configuration.isCacheEnabled()).isTrue();
        //3.4.1以下是true
        assertThat(configuration.isAggressiveLazyLoading()).isFalse();
        assertThat(configuration.isMultipleResultSetsEnabled()).isTrue();
        assertThat(configuration.isUseColumnLabel()).isTrue();
        assertThat(configuration.isUseGeneratedKeys()).isFalse();
        assertThat(configuration.getAutoMappingBehavior()).isEqualTo(AutoMappingBehavior.PARTIAL);
        assertThat(configuration.getAutoMappingUnknownColumnBehavior())
                .isEqualTo(AutoMappingUnknownColumnBehavior.NONE);
        assertThat(configuration.getDefaultExecutorType()).isEqualTo(ExecutorType.SIMPLE);
        assertThat(configuration.getDefaultStatementTimeout()).isNull();
        assertThat(configuration.getDefaultFetchSize()).isNull();
        assertThat(configuration.isSafeRowBoundsEnabled()).isFalse();
        assertThat(configuration.isSafeResultHandlerEnabled()).isTrue();
        //这里原生的是false
        assertThat(configuration.isMapUnderscoreToCamelCase()).isTrue();
        assertThat(configuration.getLocalCacheScope()).isEqualTo(LocalCacheScope.SESSION);
        assertThat(configuration.getJdbcTypeForNull()).isEqualTo(JdbcType.OTHER);
        assertThat(configuration.getLazyLoadTriggerMethods())
                .isEqualTo(Stream.of("equals", "clone", "hashCode", "toString").collect(Collectors.toSet()));
        assertThat(configuration.getDefaultScriptingLanguageInstance().getClass())
                .isEqualTo(MybatisXMLLanguageDriver.class);
        assertThat(configuration.isCallSettersOnNulls()).isFalse();
        assertThat(configuration.isReturnInstanceForEmptyRow()).isFalse();
        assertThat(configuration.getLogPrefix()).isNull();
        assertThat(configuration.getLogImpl()).isNull();
        assertThat(configuration.getProxyFactory().getClass()).isEqualTo(JavassistProxyFactory.class);
        assertThat(configuration.getVfsImpl()).isNull();
        assertThat(configuration.isUseActualParamName()).isTrue();
        assertThat(configuration.getConfigurationFactory()).isNull();
    }

    @Test
    void testUseGeneratedShortKey() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setUseGeneratedShortKey(true);
        addMappedStatement(configuration, 200);
        assertThat(configuration.getMappedStatements().size()).isEqualTo(400);

        configuration = new MybatisConfiguration();
        configuration.setUseGeneratedShortKey(false);
        addMappedStatement(configuration, 200);
        assertThat(configuration.getMappedStatements().size()).isEqualTo(200);
    }

    private void addMappedStatement(Configuration configuration, int size) {
        for (int i = 0; i < size; i++) {
            configuration.addMappedStatement(
                    new MappedStatement.Builder(configuration, "com.baomidou.test.method" + i,
                            new StaticSqlSource(configuration, "select * from test"), SqlCommandType.SELECT)
                            .statementType(StatementType.STATEMENT).resource("xxxxxx").useCache(true)
                            .keyGenerator(NoKeyGenerator.INSTANCE).build());
        }
    }

    @Test
    void testReload() {
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.addMapper(BMapper.class);
        mybatisConfiguration.addMapper(AMapper.class);
        assertThat(mybatisConfiguration.getMappedStatementNames().size()).isEqualTo(42);
        assertThat(mybatisConfiguration.getSqlFragments().size()).isEqualTo(4);
        assertThat(mybatisConfiguration.getResultMaps().size()).isEqualTo(2);
        assertThat(mybatisConfiguration.getCaches().size()).isEqualTo(2);
        assertThat(mybatisConfiguration.getMapperRegistry().getMappers().size()).isEqualTo(2);
        mybatisConfiguration.addNewMapper(BMapper.class);
        mybatisConfiguration.addNewMapper(AMapper.class);
        assertThat(mybatisConfiguration.getMappedStatementNames().size()).isEqualTo(42);
        assertThat(mybatisConfiguration.getSqlFragments().size()).isEqualTo(4);
        assertThat(mybatisConfiguration.getResultMaps().size()).isEqualTo(2);
        assertThat(mybatisConfiguration.getCaches().size()).isEqualTo(2);
        assertThat(mybatisConfiguration.getMapperRegistry().getMappers().size()).isEqualTo(2);
    }

}
