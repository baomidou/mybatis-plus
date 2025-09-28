package com.baomidou.mybatisplus.code;

import com.baomidou.mybatisplus.code.sub.MapperMethod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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

    public static void main(String[] args) throws Exception {
        List<OverwriteFile> files = List.of(new MapperMethod());
        Map<String, OverwriteFile> map = finJar(files);
        map.forEach((k, v) -> {
            System.out.println(k);
        });
    }

    private static Map<String, OverwriteFile> finJar(List<OverwriteFile> files) throws IOException {
        Map<String, OverwriteFile> map = files.stream().collect(Collectors.toMap(i -> i.getClass().getSimpleName(), i -> i));
        Map<String, OverwriteFile> result = new HashMap<>();
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
                    result.put(name, map.get(className));
                }
            }
        }
        return result;
    }

    private static String findVer() throws IOException {
        String userDir = System.getProperty("user.dir");
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
