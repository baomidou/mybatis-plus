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
import java.util.Comparator;
import java.util.LinkedHashMap;
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
@RequiredArgsConstructor
public class OverwriteRunner {
    private static final String userDir = System.getProperty("user.dir");
    protected final String baseModule;
    /**
     * 上游源码包路径，由调用方提供，不依赖本地仓库目录结构。
     */
    protected final Path sourcesJar;

    protected final List<OverwriteFile> fileList;

    @Setter
    @Accessors(chain = true)
    protected List<String> ignoreClasses = new ArrayList<>();

    public void run() throws Exception {
        Map<String, OverwriteFile> map =
                fileList.stream().collect(Collectors.toMap(i -> i.getClass().getSimpleName(), i -> i));
        try (JarFile jarFile = new JarFile(sourcesJar.toFile())) {
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
                                version = XmlUtil.elementText(
                                        (Element) pom.getElementsByTagName("properties")
                                                .item(0),
                                        key);
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
                    OverwriteFile overwriteFile = map.get(className);
                    String overwriteClass = overwriteFile.getOverwriteClass();
                    if (overwriteClass == null || overwriteClass.isBlank()) {
                        throw new IllegalArgumentException(name + " - 未配置覆盖类 overwriteClass");
                    }
                    String sourceCode = IoUtil.readUtf8(jarFile.getInputStream(entry));
                    for (Overwrite step : overwriteFile.getSteps()) {
                        sourceCode = applyStep(sourceCode, step, name);
                    }
                    sourceCode = renameClass(sourceCode, className, overwriteClass);
                    String targetName = overwriteClass.replace('.', '/') + ".java";
                    targets.put(targetName, sourceCode.getBytes(StandardCharsets.UTF_8));
                }
            }
            targets.forEach(this::writeFile);
        }
    }

    private static String renameClass(String sourceCode, String className, String overwriteClass) {
        int separator = overwriteClass.lastIndexOf('.');
        String targetPackage = separator < 0 ? "" : overwriteClass.substring(0, separator);
        String targetClass = overwriteClass.substring(separator + 1);
        // 跳过注释和字面量，只替换完整 Java 标识符，避免修改相似类名。
        Pattern tokens = Pattern.compile("//[^\\r\\n]*|/\\*[\\s\\S]*?\\*/|\"\"\"(?:\\\\[\\s\\S]|[^\\\\])*?\"\"\""
                + "|\"(?:\\\\[\\s\\S]|[^\"\\\\])*\"|'(?:\\\\[\\s\\S]|[^'\\\\])*'"
                + "|\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*");
        Matcher matcher = tokens.matcher(sourceCode);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String token = matcher.group();
            matcher.appendReplacement(result, Matcher.quoteReplacement(token.equals(className) ? targetClass : token));
        }
        matcher.appendTail(result);
        String packageDeclaration = targetPackage.isEmpty() ? "" : "package " + targetPackage + ";";
        return Pattern.compile("(?m)^([\\t ]*)package\\s+[\\w.$]+[\\t ]*;")
                .matcher(result)
                .replaceFirst(Matcher.quoteReplacement(packageDeclaration));
    }

    private static String applyStep(String sourceCode, Overwrite step, String entryName) {
        List<String> sourceLines = fragmentLines(step.getSource());
        String expression = sourceLines.stream()
                .map(String::trim)
                .map(Pattern::quote)
                .collect(Collectors.joining("[\\t ]*(?:\\r\\n|\\n|\\r)[\\t ]*"));
        if (expression.equals(Pattern.quote(""))) {
            throw new IllegalArgumentException(entryName + " - 补丁定位内容不能为空");
        }
        Matcher matcher = Pattern.compile(expression).matcher(sourceCode);
        if (!matcher.find()) {
            throw new IllegalArgumentException(entryName + " - 无法进行定位\n" + step.getSource());
        }
        List<String> targetLines =
                step.getOperate() == Overwrite.Operate.DELETE ? List.of() : fragmentLines(step.getTarget());
        StringBuilder result = new StringBuilder();
        int cursor = 0;
        do {
            int start = matcher.start();
            int end = matcher.end();
            int lineStart =
                    Math.max(sourceCode.lastIndexOf('\n', start - 1), sourceCode.lastIndexOf('\r', start - 1)) + 1;
            String prefix = sourceCode.substring(lineStart, start);
            String baseIndent = sourceCode
                    .substring(lineStart, end)
                    .lines()
                    .filter(line -> !line.isBlank())
                    .map(OverwriteRunner::indentation)
                    .min(Comparator.comparingInt(String::length))
                    .orElse("");
            String lineSeparator = lineSeparator(sourceCode, end);
            int lineEnd = end;
            while (lineEnd < sourceCode.length()
                    && sourceCode.charAt(lineEnd) != '\n'
                    && sourceCode.charAt(lineEnd) != '\r') {
                lineEnd++;
            }
            boolean endsLine = sourceCode.substring(end, lineEnd).isBlank();
            switch (step.getOperate()) {
                case Overwrite.Operate.APPEND -> {
                    // 原定位片段（包括行尾空白）保持不变，在其后插入新片段。
                    if (endsLine) {
                        end = lineEnd;
                    }
                    result.append(sourceCode, cursor, end)
                            .append(lineSeparator)
                            .append(renderTarget(targetLines, baseIndent, lineSeparator, true));
                }
                case Overwrite.Operate.COVERAGE -> {
                    boolean startsLine = prefix.isBlank();
                    if (startsLine) {
                        start = lineStart;
                    }
                    result.append(sourceCode, cursor, start)
                            .append(renderTarget(targetLines, baseIndent, lineSeparator, startsLine));
                }
                case Overwrite.Operate.DELETE -> {
                    // 删除整行片段时一并移除其缩进和换行，避免留下空白行。
                    if (prefix.isBlank() && endsLine) {
                        start = lineStart;
                        end = lineEnd;
                        if (end < sourceCode.length()) {
                            end += lineSeparator.length();
                        }
                    }
                    result.append(sourceCode, cursor, start);
                }
            }
            cursor = end;
        } while (matcher.find());
        return result.append(sourceCode, cursor, sourceCode.length()).toString();
    }

    private static List<String> fragmentLines(String fragment) {
        List<String> lines = new ArrayList<>(List.of(fragment.split("\\r\\n|\\n|\\r", -1)));
        // text block 的末尾换行是片段分隔符，不额外生成一个空行。
        if (lines.size() > 1 && lines.getLast().isEmpty()) {
            lines.removeLast();
        }
        int commonIndent = lines.stream()
                .filter(line -> !line.isBlank())
                .mapToInt(line -> indentation(line).length())
                .min()
                .orElse(0);
        return lines.stream()
                .map(line -> line.isBlank() ? "" : line.substring(commonIndent))
                .toList();
    }

    private static String indentation(String line) {
        int end = 0;
        while (end < line.length() && (line.charAt(end) == ' ' || line.charAt(end) == '\t')) {
            end++;
        }
        return line.substring(0, end);
    }

    private static String lineSeparator(String sourceCode, int offset) {
        for (int i = offset; i < sourceCode.length(); i++) {
            char value = sourceCode.charAt(i);
            if (value == '\r') {
                return i + 1 < sourceCode.length() && sourceCode.charAt(i + 1) == '\n' ? "\r\n" : "\r";
            }
            if (value == '\n') {
                return "\n";
            }
        }
        return sourceCode.contains("\r\n") ? "\r\n" : sourceCode.contains("\r") ? "\r" : "\n";
    }

    private static String renderTarget(
            List<String> lines, String indent, String lineSeparator, boolean indentFirstLine) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                result.append(lineSeparator);
            }
            String line = lines.get(i);
            if (!line.isEmpty() && (i > 0 || indentFirstLine)) {
                result.append(indent);
            }
            result.append(line);
        }
        return result.toString();
    }

    private void writeFile(String jarEntryName, byte[] bytes) {
        // 写入到本地
        try {
            String[] pathParent = new String[] {baseModule, "src", "main", "java"};
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
}
