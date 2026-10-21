package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusLanguageDriverAutoConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;

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
    void checkSpringAutoconfigureMetadataProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(
                new FileSystemResource("build/classes/java/main/META-INF/spring-autoconfigure-metadata.properties")
                        .getInputStream());
        assertThat(properties.getProperty(MybatisPlusAutoConfiguration.class.getName() + ".AutoConfigureAfter"))
                .isEqualTo(
                        DataSourceAutoConfiguration.class.getName() + ","
                                + MybatisPlusLanguageDriverAutoConfiguration.class.getName());
        assertThat(
                properties.getProperty(MybatisPlusAutoConfiguration.class.getName() + ".ConditionalOnSingleCandidate"))
                .isEqualTo(DataSource.class.getName());
        assertThat(properties.getProperty(MybatisPlusAutoConfiguration.class.getName() + ".ConditionalOnClass"))
                .isEqualTo(SqlSessionFactory.class.getName() + "," + SqlSessionFactoryBean.class.getName());
    }
}
