package com.baomidou.mybatisplus.code;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.code.sub.MapperMethod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author miemie
 * @since 2025/9/1
 */
public class OverwriteRunner {

    private static final String userDir = System.getProperty("user.dir");
    private static final String[] pathParent = new String[]{"mybatis-plus-core", "src", "main", "java"};

    public static void main(String[] args) throws Exception {
        List<OverwriteFile> OverwriteFileList = List.of(new MapperMethod());
        Map<String, OverwriteFile> map = OverwriteFileList.stream().collect(Collectors.toMap(i -> i.getClass().getSimpleName(), i -> i));
        String ver = findVer();
        Path jarPath = Paths.get(System.getProperty("user.home"), ".m2", "repository", "org", "mybatis", "mybatis",
            ver, "mybatis-" + ver + "-sources.jar");
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            for (JarEntry entry : jarFile.stream().toList()) {
                String name = entry.getName();
                if (!name.endsWith(".java")) {
                    continue;
                }
                String className = name.substring(name.lastIndexOf("/") + 1, name.length() - 5);
                if (map.containsKey(className)) {
                    // todo
                    List<String> lines = IoUtil.readUtf8Lines(jarFile.getInputStream(entry), new ArrayList<>());
                    for (Overwrite step : map.get(className).getSteps()) {
                        List<String> tl = new ArrayList<>();
                        int index = -1;
                        for (int i = 0; i < lines.size(); i++) {
                            String line = lines.get(i).trim();
                            if (index > 1) {
                                if (!step.getImports().isEmpty()) {
                                    tl.addAll(step.getImports());
                                    lines.addAll(i + 1, step.getImports().stream().map("import %s;"::formatted).toList());
                                    break;
                                }
                                if (line.equals(step.getBehind().getContent())) {
                                    // todo
                                    if (tl.size() != (step.getBehind().getLine() - step.getFront().getLine() - 1)) {
                                        throw new RuntimeException("xxx1");
                                    }
                                    for (Overwrite.Content content : step.getContents()) {
                                        List<String> code = Arrays.asList(content.getCode().split("\n"));
                                        if (content.getFrontDown() > 0) {
                                            tl.addAll(content.getFrontDown(), code);
                                        } else {
                                            tl.addAll(tl.size() - content.getBehindUp(), code);
                                        }
                                    }
                                    lines.addAll(i, tl);
                                    break;
                                } else {
                                    tl.add(lines.remove(i));
                                    i--;
                                }
                            } else {
                                if (line.equals(step.getFront().getContent())) {
                                    index = i;
                                }
                            }
                        }
                        if (tl.isEmpty()) {
                            throw new RuntimeException("xxx");
                        }
                    }
                    writeFile(name, String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
                } else {
//                    writeFile(name, IoUtil.readBytes(jarFile.getInputStream(entry)));
                }
            }
        }
    }

    private static void writeFile(String jarEntryName, byte[] bytes) throws IOException {
        // 写入到本地
        Path classPath = Paths.get(userDir, ArrayUtil.addAll(pathParent, jarEntryName.split("/")));
        Path parentDir = classPath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            // 创建所有不存在的父目录
            Files.createDirectories(parentDir);
        }
        Files.write(classPath, bytes);
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
