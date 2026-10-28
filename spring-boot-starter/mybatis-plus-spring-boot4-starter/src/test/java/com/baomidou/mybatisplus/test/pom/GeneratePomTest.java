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
@Tag("build-metadata")
class GeneratePomTest {

    @Data
    @AllArgsConstructor
    private static class Dependency {

        private String artifactId;
        private String scope;
        private String version;
        private boolean optional;
    }

    @Test
    void shouldVerifyBoot4StarterPomDependencies() throws IOException {
        try (InputStream inputStream = new FileInputStream("build/publications/maven/pom-default.xml")) {
            JerryParser jerryParser = Jerry.create(new LagartoDOMBuilder().enableXmlMode());
            Jerry doc = jerryParser.parse(FileUtil.readUTFString(inputStream));
            Jerry dependencies = doc.s("dependencies dependency");
            Map<String, Dependency> dependenciesMap = new HashMap<>();
            dependencies.forEach($this -> {
                String artifactId = $this.s("artifactId").text();
                dependenciesMap.put(
                        artifactId, new Dependency(artifactId, $this.s("scope").text(), $this.s("version").text(),
                                Boolean.parseBoolean($this.s("optional").text())));
            });
            Dependency mp = dependenciesMap.get("mybatis-plus");
            assertThat(mp.getScope()).isEqualTo("compile");
            assertThat(mp.isOptional()).isFalse();
            Dependency autoconfigure = dependenciesMap.get("spring-boot-autoconfigure");
            assertThat(autoconfigure.getScope()).isEqualTo("compile");
            assertThat(autoconfigure.isOptional()).isFalse();
            Dependency jdbc = dependenciesMap.get("spring-boot-starter-jdbc");
            assertThat(jdbc.getScope()).isEqualTo("compile");
            assertThat(jdbc.isOptional()).isFalse();
            Dependency configurationProcessor = dependenciesMap.get("spring-boot-configuration-processor");
            assertThat(configurationProcessor.getScope()).isEqualTo("compile");
            assertThat(configurationProcessor.isOptional()).isTrue();
            Dependency autoconfigureProcessor = dependenciesMap.get("spring-boot-autoconfigure-processor");
            assertThat(autoconfigureProcessor.getScope()).isEqualTo("compile");
            assertThat(autoconfigureProcessor.isOptional()).isTrue();
            Dependency bom = dependenciesMap.get("spring-boot-dependencies");
            assertThat(bom.getScope()).isEqualTo("import");
            assertThat(bom.isOptional()).isFalse();
            assertThat(dependenciesMap.get("spring-cloud-commons").getVersion()).isEqualTo("5.0.0");
            assertThat(dependenciesMap.get("mybatis-spring").getVersion()).isEqualTo("4.1.0");
            assertThat(dependenciesMap.get("spring-boot-dependencies").getVersion()).isEqualTo("4.1.0");
        }
    }
}
