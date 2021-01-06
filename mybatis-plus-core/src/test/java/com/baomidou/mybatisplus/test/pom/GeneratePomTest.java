/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
            Dependency annotation = dependenciesMap.get("mybatis-plus-annotation");
            Assertions.assertEquals("compile", annotation.getScope());
            Assertions.assertFalse(annotation.isOptional());
            Dependency mybatis = dependenciesMap.get("mybatis");
            Assertions.assertEquals("compile", mybatis.getScope());
            Assertions.assertFalse(mybatis.isOptional());
            Dependency jsqlParser = dependenciesMap.get("jsqlparser");
            Assertions.assertEquals("compile", jsqlParser.getScope());
            Assertions.assertFalse(jsqlParser.isOptional());
            Dependency cglib = dependenciesMap.get("cglib");
            Assertions.assertEquals("compile", cglib.getScope());
            Assertions.assertTrue(cglib.isOptional());
        }
    }

}
