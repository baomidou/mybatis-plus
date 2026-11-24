package com.baomidou.mybatisplus.test.pom;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * extension 发布 pom 的依赖契约。
 *
 * @author nieqiurong 2019/2/9.
 */
@Tag("build-metadata")
class ExtensionGeneratedPomTest {

    @Test
    void shouldVerifyExtensionPomDependencies() throws IOException {
        GeneratedPom pom = GeneratedPom.read();
        var core = pom.dependency("mybatis-plus-core");
        assertThat(core.scope()).isEqualTo("compile");
        assertThat(core.optional()).isFalse();
        var kotlinStdlib = pom.dependency("kotlin-stdlib-jdk8");
        assertThat(kotlinStdlib.scope()).isEqualTo("compile");
        assertThat(kotlinStdlib.optional()).isTrue();
        var kotlinReflect = pom.dependency("kotlin-reflect");
        assertThat(kotlinReflect.scope()).isEqualTo("compile");
        assertThat(kotlinReflect.optional()).isTrue();
        var slf4jApi = pom.dependency("slf4j-api");
        assertThat(slf4jApi.scope()).isEqualTo("compile");
        assertThat(slf4jApi.optional()).isTrue();
    }
}
