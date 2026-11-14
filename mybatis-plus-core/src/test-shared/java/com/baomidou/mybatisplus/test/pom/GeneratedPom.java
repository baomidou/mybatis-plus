package com.baomidou.mybatisplus.test.pom;

import jodd.io.FileUtil;
import jodd.jerry.Jerry;
import jodd.jerry.JerryParser;
import jodd.lagarto.dom.LagartoDOMBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 当前模块发布用的 pom（{@code generatePomFileForMavenPublication} 写到 {@code build/publications/maven/pom-default.xml}）读成
 * artifactId 到依赖的映射。 各模块的 pom 契约测试只有要钉的依赖不同，解析这一段收在这里，不再逐模块复制。
 *
 * @author nieqiurong 2019/2/9.
 */
public final class GeneratedPom {

    private static final String POM_FILE = "build/publications/maven/pom-default.xml";

    private final Map<String, Dependency> dependencies;

    private GeneratedPom(Map<String, Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public static GeneratedPom read() throws IOException {
        File pomFile = new File(POM_FILE);
        assertThat(pomFile).as("找不到 %s，先跑 generatePomFileForMavenPublication 再跑 pom 契约测试", POM_FILE).exists();
        Map<String, Dependency> dependencies = new HashMap<>();
        try (InputStream inputStream = new FileInputStream(pomFile)) {
            JerryParser parser = Jerry.create(new LagartoDOMBuilder().enableXmlMode());
            Jerry doc = parser.parse(FileUtil.readUTFString(inputStream));
            doc.s("dependencies dependency").forEach($this -> {
                String artifactId = $this.s("artifactId").text();
                dependencies.put(
                        artifactId, new Dependency(artifactId, $this.s("scope").text(), $this.s("version").text(),
                                Boolean.parseBoolean($this.s("optional").text())));
            });
        }
        return new GeneratedPom(dependencies);
    }

    /**
     * 按 artifactId 取依赖。取不到就直接红并列出 pom 里实际有什么：返回 null 会让下一个断言报 NPE， 看不出是依赖丢了还是改名了。
     */
    public Dependency dependency(String artifactId) {
        Dependency dependency = dependencies.get(artifactId);
        assertThat(dependency).as("pom 里没有 %s 这条依赖，实际依赖为 %s", artifactId, dependencies.keySet()).isNotNull();
        return dependency;
    }

    /**
     * pom 里一条依赖的作用域、版本与 optional 标记；没有 {@code <version>} 子节点时版本为空串。
     */
    public record Dependency(String artifactId, String scope, String version, boolean optional) {

    }
}
