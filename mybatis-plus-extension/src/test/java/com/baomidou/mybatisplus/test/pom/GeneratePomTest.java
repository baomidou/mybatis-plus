package com.baomidou.mybatisplus.test.pom;

import jodd.io.FileUtil;
import jodd.jerry.Jerry;
import jodd.jerry.JerryParser;
import jodd.lagarto.dom.LagartoDOMBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 检查pom依赖
 *
 * @author nieqiurong 2019/2/9.
 */
@Tag("unit")
class GeneratePomTest {

    @Data
    @AllArgsConstructor
    private static class Dependency {

        private String artifactId;
        private String scope;
        private boolean optional;
    }

    @Test
    void shouldVerifyExtensionPomDependencies() throws IOException {
        try (InputStream inputStream = new FileInputStream("build/publications/maven/pom-default.xml")) {
            JerryParser jerryParser = Jerry.create(new LagartoDOMBuilder().enableXmlMode());
            Jerry doc = jerryParser.parse(FileUtil.readUTFString(inputStream));
            Jerry dependencies = doc.s("dependencies dependency");
            Map<String, Dependency> dependenciesMap = new HashMap<>();
            dependencies.forEach($this -> {
                String artifactId = $this.s("artifactId").text();
                dependenciesMap.put(
                        artifactId, new Dependency(artifactId, $this.s("scope").text(),
                                Boolean.parseBoolean($this.s("optional").text())));
            });
            Dependency core = dependenciesMap.get("mybatis-plus-core");
            assertThat(core.getScope()).isEqualTo("compile");
            assertThat(core.isOptional()).isFalse();
            Dependency kotlinStdlib = dependenciesMap.get("kotlin-stdlib-jdk8");
            assertThat(kotlinStdlib.getScope()).isEqualTo("compile");
            assertThat(kotlinStdlib.isOptional()).isTrue();
            Dependency kotlinReflect = dependenciesMap.get("kotlin-reflect");
            assertThat(kotlinReflect.getScope()).isEqualTo("compile");
            assertThat(kotlinReflect.isOptional()).isTrue();
            Dependency slf4jApi = dependenciesMap.get("slf4j-api");
            assertThat(slf4jApi.getScope()).isEqualTo("compile");
            assertThat(slf4jApi.isOptional()).isTrue();
        }
    }

}
