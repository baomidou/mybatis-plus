package com.baomidou.mybatisplus.test.pom;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 聚合模块发布 pom 的依赖契约。
 *
 * @author nieqiurong 2019/2/9.
 */
@Tag("build-metadata")
class AggregateGeneratedPomTest {

    @Test
    void shouldVerifyAggregatePomDependencies() throws IOException {
        GeneratedPom pom = GeneratedPom.read();
        var spring = pom.dependency("mybatis-plus-spring");
        assertThat(spring.scope()).isEqualTo("compile");
        assertThat(spring.optional()).isFalse();
        var mybatisSpring = pom.dependency("mybatis-spring");
        assertThat(mybatisSpring.scope()).isEqualTo("compile");
        assertThat(mybatisSpring.optional()).isTrue();
        var kotlinStdlib = pom.dependency("kotlin-stdlib-jdk8");
        assertThat(kotlinStdlib.scope()).isEqualTo("compile");
        assertThat(kotlinStdlib.optional()).isTrue();
        var jsqlparser = pom.dependency("mybatis-plus-jsqlparser-4.9");
        assertThat(jsqlparser.scope()).isEqualTo("compile");
        assertThat(jsqlparser.optional()).isTrue();
    }
}
