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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.ResourcePatternHint;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * 特征化 native-image 支持装配与元数据文件。
 *
 * @author xiaochen
 * @since 2026/1/12
 */
@Tag("integration")
class RuntimeHintsRegistrarTest {

    private static final Path NATIVE_IMAGE_METADATA_ROOT = Path.of("src/main/resources/META-INF/native-image");

    /**
     * Mapper XML 的资源提示必须跟随 mybatis-plus.mapper-locations：用户把 XML 放在哪个目录，注册就覆盖哪个目录， 不能靠固定目录名（mapper/mappers）猜，否则用户改目录后
     * native 下 XML 直接不加载。
     */
    @Test
    void configuredMapperLocationsRegisterOnlyResolvedResources() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("mybatis-plus.mapper-locations", "classpath*:/aot/mapper-location/**/*.xml");
        assertThat(registeredResourcePaths(environment)).containsExactly(
                "aot/mapper-location/User1.xml", "aot/mapper-location/User12.xml",
                "aot/mapper-location/nested/User2.xml");
    }

    /**
     * 未配置 mapper-locations 时回退到 MP 默认位置，并只注册该 pattern 实际解析到的资源。
     */
    @Test
    void defaultMapperLocationUsedWhenUnconfigured() {
        assertThat(registeredResourcePaths(new MockEnvironment())).containsExactly("mapper/DefaultMapper.xml");
    }

    /**
     * YAML 列表写法（mybatis-plus.mapper-locations[0]）也必须按 Spring 的资源解析语义注册命中的资源。
     */
    @Test
    void indexedMapperLocationsAreBound() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("mybatis-plus.mapper-locations[0]", "classpath*:/aot/mapper-location/User1.xml")
                .withProperty("mybatis-plus.mapper-locations[1]", "classpath*:/aot/mapper-location/nested/*.xml");
        assertThat(registeredResourcePaths(environment))
                .containsExactly("aot/mapper-location/User1.xml", "aot/mapper-location/nested/User2.xml");
    }

    /**
     * 开关打开时解析并登记 classpath 下全部 XML，而不是向镜像声明一个覆盖任意路径的 glob。
     */
    @Test
    void registerAllXmlSwitchRegistersEveryXml() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty(MyBatisPlusNativeImageConfiguration.REGISTER_ALL_XML_PROPERTY, "true");
        assertThat(registeredResourcePaths(environment)).contains(
                "mapper/DefaultMapper.xml", "aot/mapper-location/User1.xml", "aot/mapper-location/nested/User2.xml");
    }

    /**
     * 非 classpath 前缀（file:、http: 等）无法被打包进 native image，保留由 register-all-xml 开关接管。
     */
    @Test
    void nonClasspathLocationsAreSkipped() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("mybatis-plus.mapper-locations", "file:/opt/sql/*.xml");
        assertThat(registeredResourcePaths(environment)).isEmpty();
    }

    @Test
    void questionMarkWildcardRegistersOnlyResourcesMatchingOneCharacter() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("mybatis-plus.mapper-locations", "classpath*:/aot/mapper-location/User?.xml");
        assertThat(registeredResourcePaths(environment)).containsExactly("aot/mapper-location/User1.xml");
    }

    @Test
    void multipleDoubleStarSegmentsResolveMixedDepthsExactly() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("mybatis-plus.mapper-locations", "classpath*:/aot/sql/**/mapper/**/*.xml");
        assertThat(registeredResourcePaths(environment)).containsExactly(
                "aot/sql/a/mapper/b/UserMapper.xml", "aot/sql/mapper/UserMapper.xml",
                "aot/sql/sub/mapper/UserMapper.xml");
    }

    @Test
    void samePathMapperXmlIsRegistered() {
        RuntimeHints hints = new RuntimeHints();
        MyBatisPlusNativeImageConfiguration.MyBatisBeanFactoryInitializationAotProcessor
                .registerSamePathMapperXml(RuntimeHintsRegistrarTest.class, hints);
        assertThat(resourcePaths(hints)).containsExactly("com/baomidou/mybatisplus/aot/RuntimeHintsRegistrarTest.xml");
    }

    @Test
    void reachabilityMetadataIsSplitByOwningArtifact() throws IOException {
        try (Stream<Path> files = Files.walk(NATIVE_IMAGE_METADATA_ROOT)) {
            List<String> names = files.filter(Files::isRegularFile).map(NATIVE_IMAGE_METADATA_ROOT::relativize)
                    .map(Path::toString).map(name -> name.replace('\\', '/')).sorted().collect(Collectors.toList());
            assertThat(names).containsExactly(
                    "com.baomidou/mybatis-plus-core/reachability-metadata.json",
                    "com.baomidou/mybatis-plus-spring-boot-native-image/native-image.properties",
                    "org.mybatis/mybatis-spring/reachability-metadata.json",
                    "org.mybatis/mybatis/reachability-metadata.json");
        }
    }

    @Test
    void everyReachabilityEntryHasAConditionAndResolvableTypes() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        for (Path file : reachabilityMetadataFiles()) {
            JsonNode root = mapper.readTree(file.toFile());
            Set<String> rootKeys = new TreeSet<>();
            root.fieldNames().forEachRemaining(rootKeys::add);
            assertThat(rootKeys).isSubsetOf(Set.of("reflection", "resources")).contains("reflection");
            for (String category : List.of("reflection", "resources")) {
                if (!root.has(category)) {
                    continue;
                }
                JsonNode entries = root.path(category);
                assertThat(entries.isArray()).as("%s in %s must be an array", category, file).isTrue();
                for (JsonNode entry : entries) {
                    JsonNode condition = entry.path("condition");
                    assertThat(condition.path("typeReached").isTextual())
                            .as("every %s entry in %s needs a typeReached condition", category, file).isTrue();
                    assertResolvable(condition.path("typeReached").asText(), classLoader, file);
                    assertThat(entry.has("allDeclaredConstructors")).isFalse();
                    assertThat(entry.has("allDeclaredMethods")).isFalse();
                    assertThat(entry.has("allDeclaredFields")).isFalse();
                    if ("reflection".equals(category)) {
                        assertResolvableTypes(entry.path("type"), classLoader, file);
                    }
                }
            }
        }
    }

    @Test
    void reachabilityMetadataRegistersMybatisDtdAndXsdResources() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(
                NATIVE_IMAGE_METADATA_ROOT.resolve("org.mybatis/mybatis/reachability-metadata.json").toFile());
        List<String> globs = new ArrayList<>();
        root.path("resources").forEach(entry -> globs.add(entry.path("glob").asText()));
        AntPathMatcher matcher = new AntPathMatcher();
        assertThat(globs).contains("org/apache/ibatis/builder/xml/*.dtd", "org/apache/ibatis/builder/xml/*.xsd");
        assertThat(getClass().getClassLoader().getResource("org/apache/ibatis/builder/xml/mybatis-3-mapper.dtd"))
                .isNotNull();
        assertThat(getClass().getClassLoader().getResource("org/apache/ibatis/builder/xml/mybatis-config.xsd"))
                .isNotNull();
        assertThat(
                globs.stream()
                        .anyMatch(glob -> matcher.match(glob, "org/apache/ibatis/builder/xml/mybatis-3-mapper.dtd")))
                .isTrue();
        assertThat(
                globs.stream()
                        .anyMatch(glob -> matcher.match(glob, "org/apache/ibatis/builder/xml/mybatis-config.xsd")))
                .isTrue();
    }

    private List<String> registeredResourcePaths(MockEnvironment environment) {
        RuntimeHints hints = new RuntimeHints();
        MyBatisPlusNativeImageConfiguration.MyBatisBeanFactoryInitializationAotProcessor
                .registerMapperXmlResources(hints, environment);
        return resourcePaths(hints);
    }

    private List<String> resourcePaths(RuntimeHints hints) {
        return hints.resources().resourcePatternHints().flatMap(group -> group.getIncludes().stream())
                .map(ResourcePatternHint::getPattern).filter(path -> path.endsWith(".xml")).distinct().sorted()
                .collect(Collectors.toList());
    }

    private List<Path> reachabilityMetadataFiles() throws IOException {
        try (Stream<Path> files = Files.walk(NATIVE_IMAGE_METADATA_ROOT)) {
            return files.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals("reachability-metadata.json")).sorted()
                    .collect(Collectors.toList());
        }
    }

    private void assertResolvableTypes(JsonNode type, ClassLoader classLoader, Path file) {
        if (type.isTextual()) {
            assertResolvable(type.asText(), classLoader, file);
        } else if (type.has("proxy")) {
            type.path("proxy").forEach(proxyType -> assertResolvable(proxyType.asText(), classLoader, file));
        } else if (type.has("lambda")) {
            JsonNode lambda = type.path("lambda");
            assertResolvable(lambda.path("declaringClass").asText(), classLoader, file);
            lambda.path("interfaces")
                    .forEach(interfaceType -> assertResolvable(interfaceType.asText(), classLoader, file));
        }
    }

    private void assertResolvable(String type, ClassLoader classLoader, Path file) {
        assertThatCode(() -> Class.forName(type, false, classLoader))
                .as("metadata type %s in %s must exist on the test classpath", type, file).doesNotThrowAnyException();
    }
}
