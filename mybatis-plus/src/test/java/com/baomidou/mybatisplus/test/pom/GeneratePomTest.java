package com.baomidou.mybatisplus.test.pom;

import jodd.io.FileUtil;
import jodd.jerry.Jerry;
import jodd.jerry.JerryParser;
import jodd.lagarto.dom.LagartoDOMBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
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
class GeneratePomTest {

    @Data
    @AllArgsConstructor
    private static class Dependency {

        private String artifactId;
        private String scope;
        private boolean optional;
    }

    @Test
    void test() throws IOException {
        try (InputStream inputStream = new FileInputStream("build/publications/maven/pom-default.xml")) {
            JerryParser jerryParser = Jerry.create((new LagartoDOMBuilder().enableXmlMode()));
            Jerry doc = jerryParser.parse(FileUtil.readUTFString(inputStream));
            Jerry dependencies = doc.s("dependencies dependency");
            Map<String, Dependency> dependenciesMap = new HashMap<>();
            dependencies.forEach($this -> {
                String artifactId = $this.s("artifactId").text();
                dependenciesMap.put(
                        artifactId, new Dependency(artifactId, $this.s("scope").text(),
                                Boolean.parseBoolean($this.s("optional").text())));
            });
            Dependency extension = dependenciesMap.get("mybatis-plus-spring");
            assertThat(extension.getScope()).isEqualTo("compile");
            assertThat(extension.isOptional()).isFalse();
            Dependency mybatisSpring = dependenciesMap.get("mybatis-spring");
            assertThat(mybatisSpring.getScope()).isEqualTo("compile");
            assertThat(mybatisSpring.isOptional()).isTrue();
            Dependency kotlinStdlib = dependenciesMap.get("kotlin-stdlib-jdk8");
            assertThat(kotlinStdlib.getScope()).isEqualTo("compile");
            assertThat(kotlinStdlib.isOptional()).isTrue();
            Dependency jsqlparserLib = dependenciesMap.get("mybatis-plus-jsqlparser-4.9");
            assertThat(jsqlparserLib.getScope()).isEqualTo("compile");
            assertThat(jsqlparserLib.isOptional()).isTrue();

        }
    }

}
