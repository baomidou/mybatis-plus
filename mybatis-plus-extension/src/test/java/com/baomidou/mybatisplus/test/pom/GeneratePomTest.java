package com.baomidou.mybatisplus.test.pom;

import jodd.io.FileUtil;
import jodd.jerry.Jerry;
import jodd.jerry.JerryParser;
import jodd.lagarto.dom.LagartoDOMBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
        try (InputStream inputStream = new FileInputStream("build/publications/mavenJava/pom-default.xml");) {
            JerryParser jerryParser = Jerry.create(new LagartoDOMBuilder().enableXmlMode());
            Jerry doc = jerryParser.parse(FileUtil.readUTFString(inputStream));
            Jerry dependencies = doc.s("dependencies dependency");
            Map<String, Dependency> dependenciesMap = new HashMap<>();
            dependencies.forEach($this -> {
                String artifactId = $this.s("artifactId").text();
                dependenciesMap.put(artifactId, new Dependency(artifactId, $this.s("scope").text(), Boolean.parseBoolean($this.s("optional").text())));
            });
            Dependency core = dependenciesMap.get("mybatis-plus-core");
            Assertions.assertEquals("compile", core.getScope());
            Assertions.assertFalse(core.isOptional());
            Dependency kotlinStdlib = dependenciesMap.get("kotlin-stdlib-jdk8");
            Assertions.assertEquals("compile", kotlinStdlib.getScope());
            Assertions.assertTrue(kotlinStdlib.isOptional());
            Dependency kotlinReflect = dependenciesMap.get("kotlin-reflect");
            Assertions.assertEquals("compile", kotlinReflect.getScope());
            Assertions.assertTrue(kotlinReflect.isOptional());
            Dependency slf4jApi = dependenciesMap.get("slf4j-api");
            Assertions.assertEquals("compile", slf4jApi.getScope());
            Assertions.assertTrue(slf4jApi.isOptional());
        }
    }

}
