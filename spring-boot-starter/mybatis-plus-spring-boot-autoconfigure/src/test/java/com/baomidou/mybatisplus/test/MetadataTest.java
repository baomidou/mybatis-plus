package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

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
    void checkSpringConfigurationMetadataJson() throws IOException {
        DocumentContext documentContext = JsonPath.parse(
                new FileSystemResource("build/classes/java/main/META-INF/spring-configuration-metadata.json")
                        .getInputStream());
        List<Map<String, String>> properties = documentContext.read("$.properties");
        Map<String, Metadata> metadataMap = properties.stream()
                .map(map -> new Metadata(map.get("name"), map.get("type"), map.get("sourceType")))
                .collect(Collectors.toMap(Metadata::getName, metadata -> metadata));
        assertThat(metadataMap.get("mybatis-plus.type-aliases-package")).isEqualTo(
                new Metadata("mybatis-plus.type-aliases-package", String.class.getName(),
                        MybatisPlusProperties.class.getName()));
        assertThat(metadataMap.get("mybatis-plus.global-config.db-config.table-underline")).isEqualTo(
                new Metadata("mybatis-plus.global-config.db-config.table-underline", Boolean.class.getName(),
                        GlobalConfig.DbConfig.class.getName()));
        assertThat(metadataMap.get("mybatis-plus.config-location")).isEqualTo(
                new Metadata("mybatis-plus.config-location", String.class.getName(),
                        MybatisPlusProperties.class.getName()));
        assertThat(metadataMap.get("mybatis-plus.configuration.call-setters-on-nulls")).isEqualTo(
                new Metadata("mybatis-plus.configuration.call-setters-on-nulls", Boolean.class.getName(),
                        MybatisPlusProperties.CoreConfiguration.class.getName()));
        assertThat(metadataMap.get("mybatis-plus.configuration.jdbc-type-for-null")).isEqualTo(
                new Metadata("mybatis-plus.configuration.jdbc-type-for-null", JdbcType.class.getName(),
                        MybatisPlusProperties.CoreConfiguration.class.getName()));
        assertThat(metadataMap.get("mybatis-plus.configuration.map-underscore-to-camel-case")).isEqualTo(
                new Metadata("mybatis-plus.configuration.map-underscore-to-camel-case", Boolean.class.getName(),
                        MybatisPlusProperties.CoreConfiguration.class.getName()));
    }
}
