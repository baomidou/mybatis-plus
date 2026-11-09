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

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeSerialization;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * lambda 条件构造器在 native 下的唯一兜底注册点。MP 提取 lambda 依赖 ObjectInputStream 反序列化， 流里的 lambda 类名（如
 * TeController$$Lambda/0x...）带运行期哈希，无法在构建期静态登记，只能登记捕获类（定义 lambda 的用户业务类）放行运行期合成。 GraalVM reachability metadata 能描述具体
 * lambda，但捕获类集合只有应用构建时才确定，不能随 MP 库发布；Spring AOT 的公开 SerializationHints 也没有 lambda capturing-class 注册 API，因此保留此 Feature
 * 在 Native Image 分析期发现应用中的捕获类。 通过 jar 内 native-image.properties 声明 --features= 自动发现，只做 lambda 捕获类注册，与静态元数据零重叠。只有
 * manifest 明确声明 Start-Class 时才把该 classpath 条目当作应用产物收窄扫描范围； 没有可信入口时保留全 classpath 候选扫描，兼容非标准 native-image 启动配置。
 *
 * @author xiaochen
 * @since 2026-11-04
 */
public class MybatisPlusLambdaCapturingFeature implements Feature {

    /**
     * javac 给含可序列化 lambda 的类生成的合成方法名， 它的常量池条目是纯 ASCII， 可直接在 class 字节里搜到
     */
    private static final byte[] DESERIALIZE_LAMBDA_MARKER = "$deserializeLambda$".getBytes(StandardCharsets.US_ASCII);

    private static final String VERSIONS_PREFIX = "META-INF/versions/";

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        // MP 提取 lambda 依赖其自带的 SerializedLambda，该类可达才说明项目用到了 lambda 条件构造器，此时才扫描注册，其余项目零开销；
        // 不能以 java.lang.invoke.SerializedLambda 作触发类，它在分析期未必可达
        Class<?> trigger = access.findClassByName("com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda");
        if (trigger != null) {
            access.registerReachabilityHandler(MybatisPlusLambdaCapturingFeature::registerCapturingClasses, trigger);
        }
    }

    private static void registerCapturingClasses(DuringAnalysisAccess access) {
        Collection<Path> applicationClassPath = access.getApplicationClassPath();
        Set<Path> applicationEntries = resolveApplicationEntries(applicationClassPath, access::findClassByName);
        Collection<Path> entriesToScan = applicationEntries.isEmpty() ? applicationClassPath : applicationEntries;
        for (Path entry : entriesToScan) {
            for (String className : collectCandidateClassNames(entry)) {
                try {
                    Class<?> type = access.findClassByName(className);
                    if (type != null && hasDeserializeLambdaMethod(type)) {
                        RuntimeSerialization.registerLambdaCapturingClass(type);
                    }
                } catch (LinkageError e) {
                    throw new IllegalStateException("Failed to inspect lambda capturing class " + className, e);
                }
            }
        }
    }

    /**
     * 只用可信的应用入口收窄扫描范围：Spring Boot 的 Start-Class 是明确的"这是应用"标记，声明它的 classpath 条目整体视为应用产物； Main-Class 不能作数（byte-buddy、JNA
     * 这类依赖 jar 也声明它），也不能只按包名收窄（多模块应用的业务类常在启动类包之外）。 找不到可信入口时返回空集合，调用方退回全量扫描。
     */
    static Set<Path> resolveApplicationEntries(Collection<Path> classPathEntries,
            Function<String, Class<?>> classLookup) {
        Set<Path> applicationEntries = new HashSet<>();
        for (Path entry : classPathEntries) {
            String startClassName = readStartClassName(entry);
            if (startClassName == null) {
                continue;
            }
            try {
                Class<?> type = classLookup.apply(startClassName);
                if (type != null && isMainClass(type)) {
                    applicationEntries.add(entry);
                }
            } catch (LinkageError e) {
                // 启动类加载失败时无法确认应用产物，退回全量扫描
            }
        }
        return applicationEntries;
    }

    static String readStartClassName(Path classPathEntry) {
        if (!Files.isRegularFile(classPathEntry)) {
            return null;
        }
        try (JarFile jarFile = new JarFile(classPathEntry.toFile(), false, ZipFile.OPEN_READ, Runtime.version())) {
            var manifest = jarFile.getManifest();
            return manifest == null ? null : manifest.getMainAttributes().getValue("Start-Class");
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read manifest of " + classPathEntry, e);
        }
    }

    private static boolean isMainClass(Class<?> type) {
        try {
            // getMethod 会返回继承来的 public main，入口类把 main 定义在父类时也能识别
            Method main = type.getMethod("main", String[].class);
            return Modifier.isStatic(main.getModifiers()) && main.getReturnType() == void.class;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    static boolean hasDeserializeLambdaMethod(Class<?> type) {
        // javac 只给含可序列化 lambda（如 MP 的 SFunction）的类生成 $deserializeLambda$ 合成方法，据此过滤
        for (Method method : type.getDeclaredMethods()) {
            if (method.getName().contains("$deserializeLambda$")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 收集 classpath 条目里可能含可序列化 lambda 的类：先按 class 字节里的方法名标记粗筛，再由调用方加载类做精确判断。
     */
    static List<String> collectCandidateClassNames(Path entry) {
        if (Files.isDirectory(entry)) {
            return collectFromDirectory(entry);
        }
        if (Files.isRegularFile(entry)) {
            return collectFromJar(entry);
        }
        throw new IllegalArgumentException("Application classpath entry is neither a directory nor a JAR: " + entry);
    }

    private static List<String> collectFromDirectory(Path directory) {
        try (Stream<Path> stream = Files.walk(directory)) {
            return stream.map(directory::relativize).map(Path::toString)
                    .filter(name -> name.endsWith(".class") && !name.endsWith("module-info.class"))
                    .filter(name -> containsDeserializeLambdaMarker(directory.resolve(name)))
                    .map(MybatisPlusLambdaCapturingFeature::toClassName).collect(Collectors.toList());
        } catch (UncheckedIOException e) {
            throw e;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to scan lambda capturing classes in " + directory, e);
        }
    }

    private static List<String> collectFromJar(Path jar) {
        List<String> candidates = new ArrayList<>();
        try (JarFile jarFile = new JarFile(jar.toFile(), false, ZipFile.OPEN_READ, Runtime.version())) {
            // 按当前运行版本的视图取条目：多版本 jar 里 META-INF/versions/N/ 下的实现会替换基础版同名类，
            // 预筛的字节必须与实际加载的类一致
            Set<String> names = new LinkedHashSet<>();
            var entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (entry.isDirectory()) {
                    continue;
                }
                if (name.startsWith(VERSIONS_PREFIX)) {
                    int slash = name.indexOf('/', VERSIONS_PREFIX.length());
                    if (!jarFile.isMultiRelease() || slash < 0) {
                        continue;
                    }
                    name = name.substring(slash + 1);
                }
                if (name.endsWith(".class") && !name.startsWith("META-INF/") && !name.endsWith("module-info.class")) {
                    names.add(name);
                }
            }
            for (String name : names) {
                ZipEntry entry = jarFile.getEntry(name);
                if (entry != null && containsDeserializeLambdaMarker(jarFile, entry)) {
                    candidates.add(toClassName(name));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to scan lambda capturing classes in " + jar, e);
        }
        return candidates;
    }

    private static boolean containsDeserializeLambdaMarker(Path classFile) {
        try {
            return containsMarker(Files.readAllBytes(classFile), DESERIALIZE_LAMBDA_MARKER);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read candidate class file " + classFile, e);
        }
    }

    private static boolean containsDeserializeLambdaMarker(JarFile jarFile, ZipEntry entry) throws IOException {
        try (InputStream input = jarFile.getInputStream(entry)) {
            return containsMarker(input.readAllBytes(), DESERIALIZE_LAMBDA_MARKER);
        }
    }

    private static boolean containsMarker(byte[] content, byte[] marker) {
        for (int i = 0; i <= content.length - marker.length; i++) {
            int j = 0;
            while (j < marker.length && content[i + j] == marker[j]) {
                j++;
            }
            if (j == marker.length) {
                return true;
            }
        }
        return false;
    }

    private static String toClassName(String classFilePath) {
        String bootClassesPrefix = "BOOT-INF/classes/";
        if (classFilePath.startsWith(bootClassesPrefix)) {
            classFilePath = classFilePath.substring(bootClassesPrefix.length());
        }
        String name = classFilePath.substring(0, classFilePath.length() - ".class".length());
        return name.replace(File.separatorChar, '.').replace('/', '.');
    }
}
