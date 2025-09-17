package com.baomidou.mybatisplus.code;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.code.sub.MapperMethod;
import com.baomidou.mybatisplus.code.sub.MapperProxy;

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

    public static void main(String[] args) throws Exception {
        List<OverwriteFile> OverwriteFileList = List.of(new MapperMethod(), new MapperProxy());
        Map<String, OverwriteFile> map = OverwriteFileList.stream().collect(Collectors.toMap(i -> i.getClass().getSimpleName(), i -> i));
        String ver = findVer();
        Path jarPath = Paths.get(System.getProperty("user.home"), ".m2", "repository", "org", "mybatis", "mybatis",
            ver, "mybatis-" + ver + "-sources.jar");
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            Map<String, byte[]> targets = new LinkedHashMap<>();
            for (JarEntry entry : jarFile.stream().toList()) {
                String name = entry.getName();
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
//                    targets.put(name, IoUtil.readBytes(jarFile.getInputStream(entry)));
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
