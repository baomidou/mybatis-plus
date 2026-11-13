package com.baomidou.mybatisplus.test.pom;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Boot 2 starter 发布 pom 的依赖契约：公共形状见 {@link StarterPomSupport}，这里只钉本版本线的依赖下限。
 *
 * @author nieqiurong 2019/2/9.
 */
@Tag("build-metadata")
class BootStarterGeneratedPomTest extends StarterPomSupport {

    @Test
    void shouldVerifyBootStarterPomDependencies() throws IOException {
        GeneratedPom pom = GeneratedPom.read();
        assertStarterPom(pom);
        assertThat(pom.dependency("spring-cloud-commons").version()).isEqualTo("3.1.8");
        assertThat(pom.dependency("mybatis-spring").version()).isEqualTo("2.1.2");
        assertThat(pom.dependency("spring-boot-dependencies").version()).isEqualTo("2.7.18");
    }
}
