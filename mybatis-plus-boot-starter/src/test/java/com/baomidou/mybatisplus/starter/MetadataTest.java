/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.starter;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusLanguageDriverAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 检查元数据生成
 *
 * @author nieqiurong 2019/2/9.
 */
class MetadataTest {

    @Data
    @AllArgsConstructor
    private static class Metadata {
        private String name;
        private String type;
        private String sourceType;
    }

    @Test
    void checkSpringAutoconfigureMetadataProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileSystemResource("build/classes/java/main/META-INF/spring-autoconfigure-metadata.properties").getInputStream());
        Assertions.assertEquals(DataSourceAutoConfiguration.class.getName() + "," + MybatisPlusLanguageDriverAutoConfiguration.class.getName(), properties.getProperty(MybatisPlusAutoConfiguration.class.getName() + ".AutoConfigureAfter"));
        Assertions.assertEquals(DataSource.class.getName(), properties.getProperty(MybatisPlusAutoConfiguration.class.getName() + ".ConditionalOnSingleCandidate"));
        Assertions.assertEquals(SqlSessionFactory.class.getName() + "," + SqlSessionFactoryBean.class.getName(), properties.getProperty(MybatisPlusAutoConfiguration.class.getName() + ".ConditionalOnClass"));
    }

    @Test
    void checkSpringConfigurationMetadataJson() throws IOException {
        DocumentContext documentContext = JsonPath.parse(new FileSystemResource("build/classes/java/main/META-INF/spring-configuration-metadata.json").getInputStream());
        List<Map<String, String>> properties = documentContext.read("$.properties");
        Map<String, Metadata> metadataMap = properties.stream().map(map -> new Metadata(map.get("name"), map.get("type"), map.get("sourceType"))).collect(Collectors.toMap(Metadata::getName, metadata -> metadata));
        Assertions.assertEquals(metadataMap.get("mybatis-plus.type-enums-package"), new Metadata("mybatis-plus.type-enums-package", String.class.getName(), MybatisPlusProperties.class.getName()));
        Assertions.assertEquals(metadataMap.get("mybatis-plus.type-aliases-package"), new Metadata("mybatis-plus.type-aliases-package", String.class.getName(), MybatisPlusProperties.class.getName()));
        Assertions.assertEquals(metadataMap.get("mybatis-plus.global-config.db-config.table-underline"), new Metadata("mybatis-plus.global-config.db-config.table-underline", Boolean.class.getName(), GlobalConfig.DbConfig.class.getName()));
        Assertions.assertEquals(metadataMap.get("mybatis-plus.config-location"), new Metadata("mybatis-plus.config-location", String.class.getName(), MybatisPlusProperties.class.getName()));
        Assertions.assertEquals(metadataMap.get("mybatis-plus.configuration.call-setters-on-nulls"), new Metadata("mybatis-plus.configuration.call-setters-on-nulls", Boolean.class.getName(), MybatisConfiguration.class.getName()));
        Assertions.assertEquals(metadataMap.get("mybatis-plus.configuration.jdbc-type-for-null"), new Metadata("mybatis-plus.configuration.jdbc-type-for-null", JdbcType.class.getName(), MybatisConfiguration.class.getName()));
        Assertions.assertEquals(metadataMap.get("mybatis-plus.configuration.map-underscore-to-camel-case"), new Metadata("mybatis-plus.configuration.map-underscore-to-camel-case", Boolean.class.getName(), MybatisConfiguration.class.getName()));
    }
}
