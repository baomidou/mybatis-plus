/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.aot;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.ResourcePatternHint;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试RuntimeHintsRegistrar是否正常生成反射、资源配置
 *
 * @author xiaochen
 * @since 2026/1/12
 */
@Tag("integration")
class RuntimeHintsRegistrarTest {

    /**
     * 特征化装配入口：native-image 的资源提示全靠配置类上的 @ImportRuntimeHints 挂进来，注解被摘掉就静默失效。
     */
    @Test
    void configurationImportsItsOwnRegistrar() {
        ImportRuntimeHints imported = MyBatisPlusNativeImageConfiguration.class.getAnnotation(ImportRuntimeHints.class);
        assertThat(imported).as("@ImportRuntimeHints 是 runtime 保留注解，没了它 AOT 不会注册任何提示").isNotNull();
        assertThat(imported.value()).as("挂进来的必须是 MP 自己的 registrar，不能悄悄换成别人的")
                .containsExactly(MyBatisPlusNativeImageConfiguration.MyBatisRuntimeHintsRegistrar.class);
    }

    /**
     * 特征化 registrar 的产出：classpath 上 mapper 目录里的 xml 会被登记成 resource pattern，非 mapper 的 xml 不收。
     */
    @Test
    void registrarRegistersMapperXmlAsResourcePattern() throws IOException {
        assertThat(registeredPatterns(new MyBatisPlusNativeImageConfiguration.MyBatisRuntimeHintsRegistrar()))
                .contains("mapper/AotProbeMapper.xml").doesNotContain("logback.xml");
        assertThat(registeredPatterns(new MyBaitsRuntimeHintsRegistrar())).contains("mapper/AotProbeMapper.xml");
    }

    /**
     * 用只挂着探针资源的类加载器跑一遍注册，避免结果随测试类路径上的 jar 漂移。
     */
    private List<String> registeredPatterns(RuntimeHintsRegistrar registrar) throws IOException {
        Path root = Files.createTempDirectory("aot-hints");
        Files.createDirectories(root.resolve("mapper"));
        Files.writeString(root.resolve("mapper/AotProbeMapper.xml"), "<mapper/>", StandardCharsets.UTF_8);
        Files.writeString(root.resolve("logback.xml"), "<configuration/>", StandardCharsets.UTF_8);
        RuntimeHints hints = new RuntimeHints();
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{root.toUri().toURL()}, null)) {
            registrar.registerHints(hints, classLoader);
        }
        return hints.resources().resourcePatternHints().flatMap(group -> group.getIncludes().stream())
                .map(ResourcePatternHint::getPattern).collect(Collectors.toList());
    }

}
