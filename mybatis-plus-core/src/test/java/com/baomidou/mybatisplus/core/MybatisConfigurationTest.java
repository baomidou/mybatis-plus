/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core;

import org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.AutoMappingUnknownColumnBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSessionFactory;
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
}
