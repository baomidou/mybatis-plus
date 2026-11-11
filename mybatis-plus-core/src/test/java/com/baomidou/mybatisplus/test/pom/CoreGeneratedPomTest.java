package com.baomidou.mybatisplus.test.pom;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * core 发布 pom 的依赖契约。
 *
 * @author nieqiurong 2019/2/9.
 */
@Tag("build-metadata")
class CoreGeneratedPomTest {

    @Test
    void shouldGeneratePomWithCorrectCompileDependencies() throws IOException {
        GeneratedPom pom = GeneratedPom.read();
        var annotation = pom.dependency("mybatis-plus-annotation");
        assertThat(annotation.scope()).as("mybatis-plus-annotation 必须是 compile 作用域").isEqualTo("compile");
        assertThat(annotation.optional()).as("mybatis-plus-annotation 不能是 optional").isFalse();
        var mybatis = pom.dependency("mybatis");
        assertThat(mybatis.scope()).as("mybatis 必须是 compile 作用域").isEqualTo("compile");
        assertThat(mybatis.optional()).as("mybatis 不能是 optional").isFalse();
        var cglib = pom.dependency("cglib");
        assertThat(cglib.scope()).as("cglib 必须是 compile 作用域").isEqualTo("compile");
        assertThat(cglib.optional()).as("cglib 必须是 optional(仅需要代理时引入)").isTrue();
    }
}
