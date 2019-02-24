package com.baomidou.mybatisplus.core;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;

/**
 * @author nieqiurong 2019/2/23.
 */
class MybatisConfigurationTest {
    
    @Test
    void testXml() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory factory = new MybatisSqlSessionFactoryBuilder().build(reader);
        Configuration configuration = factory.getConfiguration();
        boolean mapUnderscoreToCamelCase = configuration.isMapUnderscoreToCamelCase();
        Assertions.assertTrue(mapUnderscoreToCamelCase);
        Assertions.assertEquals(configuration.getDefaultScriptingLanguageInstance().getClass(),MybatisXMLLanguageDriver.class);
    }
    
    @Test
    void testBean() {
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        Assertions.assertTrue(mybatisConfiguration.isMapUnderscoreToCamelCase());
        Assertions.assertEquals(mybatisConfiguration.getDefaultScriptingLanguageInstance().getClass(),MybatisXMLLanguageDriver.class);
    }
}
