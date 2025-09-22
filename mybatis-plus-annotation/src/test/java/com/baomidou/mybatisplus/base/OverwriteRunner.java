package com.baomidou.mybatisplus.base;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.XmlUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author miemie
 * @since 2025/9/1
 */
@RequiredArgsConstructor
public class OverwriteRunner {
    private static final String userDir = System.getProperty("user.dir");
    protected final String baseModule;
    protected final String buildVerDf;
    protected final String mybatisModule;
    protected final List<OverwriteFile> fileList;
    @Setter
    @Accessors(chain = true)
    protected boolean onlyFile = true;
    @Setter
    @Accessors(chain = true)
    protected List<String> ignoreClasses = new ArrayList<>();

    public void run() throws Exception {
        Map<String, OverwriteFile> map = fileList.stream().collect(Collectors.toMap(i -> i.getClass().getSimpleName(), i -> i));
        String ver = findVer();
        Path jarPath = Paths.get(System.getProperty("user.home"), ".m2", "repository", "org", "mybatis", mybatisModule,
            ver, "%s-%s-sources.jar".formatted(mybatisModule, ver));
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            Map<String, byte[]> targets = new LinkedHashMap<>();
            for (JarEntry entry : jarFile.stream().toList()) {
                String name = entry.getName();
                if (name.endsWith("pom.xml") && name.contains("mybatis")) {
                    Document pom = XmlUtil.readXML(jarFile.getInputStream(entry));
                    NodeList dependencyNodes = pom.getElementsByTagName("dependency");
                    for (int i = 0; i < dependencyNodes.getLength(); i++) {
                        Element dependency = (Element) dependencyNodes.item(i);
                        String optional = XmlUtil.elementText(dependency, "optional");
                        if ("true".equals(optional)) {
                            String groupId = XmlUtil.elementText(dependency, "groupId");
                            String artifactId = XmlUtil.elementText(dependency, "artifactId");
                            String version = XmlUtil.elementText(dependency, "version");
                            if (version.startsWith("${") && version.endsWith("}")) {
                                String key = version.substring(2, version.length() - 1);
                                version = XmlUtil.elementText((Element) pom.getElementsByTagName("properties").item(0), key);
                            }
                            System.out.printf("implementation '%s:%s:%s'%n", groupId, artifactId, version);
                        }
                    }
                }
                if (!name.endsWith(".java")) {
                    continue;
                }
                String className = name.substring(name.lastIndexOf("/") + 1, name.length() - 5);
                if (ignoreClasses.contains(className)) {
                    continue;
                }
                if (map.containsKey(className)) {
                    List<String> lines = IoUtil.readUtf8Lines(jarFile.getInputStream(entry), new ArrayList<>());
                    String sourceCode = lines.stream().map(String::trim).collect(Collectors.joining("\n"));
                    for (Overwrite step : map.get(className).getSteps()) {
                        String s = Stream.of(step.getSource().split("\n")).map(String::trim).collect(Collectors.joining("\n"));
                        if (!sourceCode.contains(s)) {
                            System.err.println(name + " - 无法进行定位 \n" + step.getSource());
                            return;
                        }
                        switch (step.getOperate()) {
                            case Overwrite.Operate.DELETE -> sourceCode = sourceCode.replace(s, "");
                            case Overwrite.Operate.APPEND -> {
                                String t = Stream.of(step.getTarget().split("\n")).map(String::trim).collect(Collectors.joining("\n"));
                                sourceCode = sourceCode.replace(s, s + "\n" + t);
                            }
                            case Overwrite.Operate.COVERAGE -> {
                                String t = Stream.of(step.getTarget().split("\n")).map(String::trim).collect(Collectors.joining("\n"));
                                sourceCode = sourceCode.replace(s, t);
                            }
                        }
                    }
                    targets.put(name, sourceCode.getBytes(StandardCharsets.UTF_8));
                } else {
                    if (!onlyFile) {
                        targets.put(name, IoUtil.readBytes(jarFile.getInputStream(entry)));
                    }
                }
            }
            targets.forEach(this::writeFile);
        }
    }

    private void writeFile(String jarEntryName, byte[] bytes) {
        // 写入到本地
        try {
            String[] pathParent = new String[]{baseModule, "src", "main", "java"};
            Path classPath = Paths.get(userDir, ArrayUtil.addAll(pathParent, jarEntryName.split("/")));
            Path parentDir = classPath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                // 创建所有不存在的父目录
                Files.createDirectories(parentDir);
            }
            Files.write(classPath, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String findVer() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(userDir + "/build.gradle")));
        Pattern pattern = Pattern.compile(buildVerDf + "Version\\s*=\\s*['\"]([\\d.]+)['\"]");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new RuntimeException();
    }
}
