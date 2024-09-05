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
        private String version;
        private boolean optional;
    }

    @Test
    void test() throws IOException {
        try (InputStream inputStream = new FileInputStream("build/publications/mavenJava/pom-default.xml")) {
            JerryParser jerryParser = Jerry.create(new LagartoDOMBuilder().enableXmlMode());
            Jerry doc = jerryParser.parse(FileUtil.readUTFString(inputStream));
            Jerry dependencies = doc.s("dependencies dependency");
            Map<String, Dependency> dependenciesMap = new HashMap<>();
            dependencies.forEach($this -> {
                String artifactId = $this.s("artifactId").text();
                dependenciesMap.put(artifactId, new Dependency(artifactId, $this.s("scope").text(), $this.s("version").text(), Boolean.parseBoolean($this.s("optional").text())));
            });
            Dependency mp = dependenciesMap.get("mybatis-plus");
            Assertions.assertEquals("compile", mp.getScope());
            Assertions.assertFalse(mp.isOptional());
            Dependency autoconfigure = dependenciesMap.get("spring-boot-autoconfigure");
            Assertions.assertEquals("compile", autoconfigure.getScope());
            Assertions.assertFalse(autoconfigure.isOptional());
            Dependency jdbc = dependenciesMap.get("spring-boot-starter-jdbc");
            Assertions.assertEquals("compile", jdbc.getScope());
            Assertions.assertFalse(jdbc.isOptional());
            Dependency configurationProcessor = dependenciesMap.get("spring-boot-configuration-processor");
            Assertions.assertEquals("compile", configurationProcessor.getScope());
            Assertions.assertTrue(configurationProcessor.isOptional());
            Dependency autoconfigureProcessor = dependenciesMap.get("spring-boot-autoconfigure-processor");
            Assertions.assertEquals("compile", autoconfigureProcessor.getScope());
            Assertions.assertTrue(autoconfigureProcessor.isOptional());
            Dependency bom = dependenciesMap.get("spring-boot-dependencies");
            Assertions.assertEquals("import", bom.getScope());
            Assertions.assertFalse(bom.isOptional());
            Assertions.assertEquals(dependenciesMap.get("spring-cloud-commons").getVersion(), "4.1.4");
            Assertions.assertEquals(dependenciesMap.get("mybatis-spring").getVersion(), "3.0.4");
            Assertions.assertEquals(dependenciesMap.get("spring-boot-dependencies").getVersion(), "3.3.3");
        }
    }

}
