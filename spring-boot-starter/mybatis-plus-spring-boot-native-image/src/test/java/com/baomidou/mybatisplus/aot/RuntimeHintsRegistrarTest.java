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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.ResourcePatternHint;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.mock.env.MockEnvironment;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
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

    private static final Path NATIVE_IMAGE_METADATA_DIR = Path
            .of("src/main/resources/META-INF/native-image/com.baomidou/mybatis-plus-spring-boot-native-image");

    /**
     * Mapper XML 的资源提示必须跟随 mybatis-plus.mapper-locations：用户把 XML 放在哪个目录，注册就覆盖哪个目录， 不能靠固定目录名（mapper/mappers）猜，否则用户改目录后
     * native 下 XML 直接不加载。
     */
    @Test
    void mapperXmlPatternsFollowConfiguredMapperLocations() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("mybatis-plus.mapper-locations", "classpath*:/sql/**/*.xml");
        assertThat(registeredPatterns(environment)).contains("sql/**/*.xml", "sql/*.xml")
                .doesNotContain("mapper/*.xml", "mapper/**/*.xml");
    }

    /**
     * 未配置 mapper-locations 时回退到 MP 自身的默认值（mapper 目录下的全部 xml），与 MybatisPlusProperties 保持一致。
     */
    @Test
    void defaultMapperLocationUsedWhenUnconfigured() {
        assertThat(registeredPatterns(new MockEnvironment())).contains("mapper/**/*.xml", "mapper/*.xml")
                .doesNotContain("**/*.xml");
    }

    /**
     * yaml 里的列表写法（mybatis-plus.mapper-locations[0]）也要能绑定上，否则数组形式的配置会静默失效。
     */
    @Test
    void indexedMapperLocationsAreBound() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("mybatis-plus.mapper-locations[0]", "classpath*:/sql/**/*.xml")
                .withProperty("mybatis-plus.mapper-locations[1]", "classpath*:/mapper/*.xml");
        assertThat(registeredPatterns(environment)).contains("sql/**/*.xml", "sql/*.xml", "mapper/*.xml");
    }

    /**
     * 开关打开时兜底注册 classpath 下全部 xml（历史上默认即全量）。
     */
    @Test
    void registerAllXmlSwitchRegistersEveryXml() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty(MyBatisPlusNativeImageConfiguration.REGISTER_ALL_XML_PROPERTY, "true");
        assertThat(registeredPatterns(environment)).contains("**/*.xml", "*.xml")
                .doesNotContain("mapper/*.xml", "mapper/**/*.xml");
    }

    /**
     * 非 classpath 前缀（file:、http: 等）不是 classpath 资源，转不出资源 pattern，只能跳过。
     */
    @Test
    void nonClasspathLocationsAreSkipped() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("mybatis-plus.mapper-locations", "file:/opt/sql/*.xml");
        assertThat(registeredPatterns(environment)).isEmpty();
    }

    /**
     * Spring 6.2（Boot3）写 resource-config.json 时把单星和双星都转成 .* 并保留字面斜杠， 双星接斜杠的写法会漏掉 mapper/UserMapper.xml 单层布局；Spring
     * 7（Boot4）原样透传 glob， 单星只匹配单层。两版语义相反，注册集合必须在两种语义下都覆盖所有常见布局。
     */
    @Test
    void mapperPatternsCoverLayoutsUnderBothSpringSemantics() {
        List<ResourcePatternHint> hints = registeredHints(new MockEnvironment());
        assertCoveredBySemantics(hints, "mapper/UserMapper.xml");
        assertCoveredBySemantics(hints, "mapper/sub/UserMapper.xml");
        assertCoveredBySemantics(hints, "mapper/sub/deep/UserMapper.xml");
    }

    private void assertCoveredBySemantics(List<ResourcePatternHint> hints, String path) {
        assertThat(hints.stream().anyMatch(hint -> hint.matches(path))).as("Spring 7 glob 语义下 %s 必须被命中", path).isTrue();
        assertThat(
                hints.stream().anyMatch(
                        hint -> java.util.regex.Pattern.compile(spring62ToRegex(hint.getPattern())).matcher(path)
                                .matches()))
                .as("Spring 6.2 regex 转换语义下 %s 必须被命中", path).isTrue();
    }

    /**
     * 复刻 Spring 6.2 ResourcePatternHint.toRegex 的转换（Spring 7 已删除该实现）， 用于在 Spring 7 测试环境里保真 Boot3 的 resource-config.json
     * 生成行为。
     */
    private String spring62ToRegex(String pattern) {
        String prefix = pattern.startsWith("*") ? ".*" : "";
        String suffix = pattern.endsWith("*") ? ".*" : "";
        return java.util.Arrays.stream(pattern.split("\\*")).filter(s -> !s.isEmpty())
                .map(java.util.regex.Pattern::quote).collect(Collectors.joining(".*", prefix, suffix));
    }

    /**
     * 元数据目录内容白名单：四个静态 json + 声明 lambda Feature 的 native-image.properties（自动发现注册点，非双轨），多余文件一律拒绝。
     */
    @Test
    void nativeImageMetadataDirContainsOnlyKnownFiles() throws IOException {
        try (Stream<Path> files = Files.list(NATIVE_IMAGE_METADATA_DIR)) {
            List<String> names = files.map(f -> f.getFileName().toString()).sorted().collect(Collectors.toList());
            assertThat(names).containsExactly(
                    "native-image.properties", "proxy-config.json", "reflect-config.json", "resource-config.json",
                    "serialization-config.json");
        }
    }

    /**
     * json 里登记的每个类都必须真实存在：静态元数据的类名拼写错误在 JVM 模式测试里就能暴露，不必等到 native 构建才炸。 同时校验字段名符合旧 schema（reflect 用 name、proxy 用
     * interfaces），新 schema 字段名会被老 GraalVM 拒绝。
     */
    @Test
    void registeredTypesInStaticJsonAreResolvable() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();

        List<String> reflectNames = readList(mapper, "reflect-config.json").stream()
                .map(entry -> (String) entry.get("name")).collect(Collectors.toList());
        List<String> serializedTypes = readList(mapper, "serialization-config.json").stream()
                .map(entry -> (String) entry.get("name")).collect(Collectors.toList());
        List<String> proxyInterfaces = readList(mapper, "proxy-config.json").stream()
                .map(entry -> (List<?>) entry.get("interfaces")).flatMap(List::stream).map(String::valueOf)
                .collect(Collectors.toList());

        assertThat(reflectNames).isNotEmpty();
        assertThat(proxyInterfaces).isNotEmpty();
        for (String type : reflectNames) {
            assertThatCode(() -> Class.forName(type, false, classLoader))
                    .as("reflect-config 登记的类必须在编译期 classpath 上: %s", type).doesNotThrowAnyException();
        }
        for (String type : serializedTypes) {
            assertThatCode(() -> Class.forName(type, false, classLoader))
                    .as("serialization-config 登记的类必须在编译期 classpath 上: %s", type).doesNotThrowAnyException();
        }
        for (String type : proxyInterfaces) {
            assertThatCode(() -> Class.forName(type, false, classLoader))
                    .as("proxy-config 登记的接口必须在编译期 classpath 上: %s", type).doesNotThrowAnyException();
        }
    }

    /**
     * resource-config.json 的 DTD/XSD regex 必须能命中 mybatis jar 内真实资源，否则 XML mapper 解析在 native 下会失败。
     */
    @Test
    void resourceConfigRegexMatchesMybatisDtd() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> root = mapper
                .readValue(NATIVE_IMAGE_METADATA_DIR.resolve("resource-config.json").toFile(), Map.class);
        List<?> includes = (List<?>) ((Map<?, ?>) root.get("resources")).get("includes");
        assertThat(includes).isNotEmpty();
        String regex = (String) ((Map<?, ?>) includes.get(0)).get("pattern");
        assertThat(java.util.regex.Pattern.compile(regex).matcher("org/apache/ibatis/builder/xml/mybatis-3-mapper.dtd"))
                .matches();
        assertThat(java.util.regex.Pattern.compile(regex).matcher("org/apache/ibatis/builder/xml/mybatis-config.xsd"))
                .matches();
    }

    private List<String> registeredPatterns(MockEnvironment environment) {
        return registeredHints(environment).stream().map(ResourcePatternHint::getPattern).collect(Collectors.toList());
    }

    private List<ResourcePatternHint> registeredHints(MockEnvironment environment) {
        RuntimeHints hints = new RuntimeHints();
        MyBatisPlusNativeImageConfiguration.MyBatisBeanFactoryInitializationAotProcessor
                .registerMapperXmlResources(hints, environment);
        return hints.resources().resourcePatternHints().flatMap(group -> group.getIncludes().stream()).distinct()
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> readList(ObjectMapper mapper, String fileName) {
        try {
            return mapper.readValue(NATIVE_IMAGE_METADATA_DIR.resolve(fileName).toFile(), List.class);
        } catch (IOException e) {
            throw new UncheckedIOException(fileName, e);
        }
    }

}
