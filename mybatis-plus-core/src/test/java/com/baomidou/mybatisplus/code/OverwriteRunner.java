package com.baomidou.mybatisplus.code;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.XmlUtil;
import com.baomidou.mybatisplus.code.sub.*;
import com.baomidou.mybatisplus.code.sub.javassist.ClassPool;
import com.baomidou.mybatisplus.code.sub.javassist.ClassPoolTail;
import com.baomidou.mybatisplus.code.sub.javassist.CtClassType;
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
public class OverwriteRunner {

    private static final String userDir = System.getProperty("user.dir");
    private static final String[] pathParent = new String[]{"mybatis-plus-core", "src", "main", "java"};

    public static List<OverwriteFile> overwriteFileList = List.of(
        new Configuration(),
        new DefaultParameterHandler(),
        new MapperMethod(),
        new MapperProxy(),
        new TypeHandlerRegistry(),
        new XMLConfigBuilder(),
        new XMLLanguageDriver(),
        new ClassPool(),
        new ClassPoolTail(),
        new CtClassType()
    );

    public static void main(String[] args) throws Exception {
        Map<String, OverwriteFile> map = overwriteFileList.stream().collect(Collectors.toMap(i -> i.getClass().getSimpleName(), i -> i));
        String ver = findVer();
        Path jarPath = Paths.get(System.getProperty("user.home"), ".m2", "repository", "org", "mybatis", "mybatis",
            ver, "mybatis-" + ver + "-sources.jar");
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
                            case DELETE -> sourceCode = sourceCode.replace(s, "");
                            case APPEND -> {
                                String t = Stream.of(step.getTarget().split("\n")).map(String::trim).collect(Collectors.joining("\n"));
                                sourceCode = sourceCode.replace(s, s + "\n" + t);
                            }
                            case COVERAGE -> {
                                String t = Stream.of(step.getTarget().split("\n")).map(String::trim).collect(Collectors.joining("\n"));
                                sourceCode = sourceCode.replace(s, t);
                            }
                        }
                    }
                    targets.put(name, sourceCode.getBytes(StandardCharsets.UTF_8));
                } else {
                    targets.put(name, IoUtil.readBytes(jarFile.getInputStream(entry)));
                }
            }
            targets.forEach(OverwriteRunner::writeFile);
        }
    }

    private static void writeFile(String jarEntryName, byte[] bytes) {
        // 写入到本地
        try {
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

    private static String findVer() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(userDir + "/build.gradle")));
        // 定义正则表达式，匹配 mybatisVersion = '3.5.19'
        Pattern pattern = Pattern.compile("mybatisVersion\\s*=\\s*['\"]([\\d.]+)['\"]");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new RuntimeException();
    }
}
