package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusLanguageDriverAutoConfiguration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
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
@Tag("integration")
class MetadataTest {

    /**
     * Boot 4 把 {@code DataSourceAutoConfiguration} 挪进了 {@code org.springframework.boot.jdbc.autoconfigure}， 三个 starter
     * 各有一份写死包名的副本。这里按当前 starter 类路径上实际存在的类挑包名， 断言仍然是全限定名逐字比对，没有放宽。
     */
    private static final String DATASOURCE_AUTO_CONFIGURATION = MetadataTest.class.getClassLoader()
            .getResource("org/springframework/boot/jdbc/autoconfigure/DataSourceAutoConfiguration.class") != null
                    ? "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration"
                    : "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration";

    @Test
    void checkSpringAutoconfigureMetadataProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(
                new FileSystemResource("build/classes/java/main/META-INF/spring-autoconfigure-metadata.properties")
                        .getInputStream());
        assertThat(properties.getProperty(MybatisPlusAutoConfiguration.class.getName() + ".AutoConfigureAfter"))
                .isEqualTo(
                        DATASOURCE_AUTO_CONFIGURATION + ","
                                + MybatisPlusLanguageDriverAutoConfiguration.class.getName());
        assertThat(
                properties.getProperty(MybatisPlusAutoConfiguration.class.getName() + ".ConditionalOnSingleCandidate"))
                .isEqualTo(DataSource.class.getName());
        assertThat(properties.getProperty(MybatisPlusAutoConfiguration.class.getName() + ".ConditionalOnClass"))
                .isEqualTo(SqlSessionFactory.class.getName() + "," + SqlSessionFactoryBean.class.getName());
    }
}
