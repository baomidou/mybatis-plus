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
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * lambda 条件构造器在 native 下的唯一兜底注册点。MP 提取 lambda 依赖 ObjectInputStream 反序列化， 流里的 lambda 类名（如
 * TeController$$Lambda/0x...）带运行期哈希，无法在构建期静态登记， 只能登记捕获类（定义 lambda 的用户业务类）放行运行期合成。捕获类名 MP 无法预知， 静态 json 写不了，Spring AOT 的
 * SerializationHints 也不支持该语义， 故保留这一个极小 Feature，经 jar 内 native-image.properties 声明 --features= 自动发现（无需用户配置）， 只做 lambda
 * 捕获类注册，与静态元数据零重叠。
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
        // MP 提取 lambda 必经 JDK 序列化机制（writeReplace 产生 SerializedLambda 流），
        // 该机制可达才说明项目用到了 lambda 条件构造器，此时才扫描注册，其余项目零开销
        access.registerReachabilityHandler(
                MybatisPlusLambdaCapturingFeature::registerCapturingClasses, SerializedLambda.class);
    }

    private static void registerCapturingClasses(DuringAnalysisAccess access) {
        for (Path entry : access.getApplicationClassPath()) {
            // 应用既可能以目录（IDE、spring-boot:run）也可能以 jar（打包后构建镜像）进入 image classpath，两种形态都要扫
            for (String className : collectCandidateClassNames(entry)) {
                try {
                    Class<?> type = access.findClassByName(className);
                    if (type != null && hasDeserializeLambdaMethod(type)) {
                        RuntimeSerialization.registerLambdaCapturingClass(type);
                    }
                } catch (LinkageError e) {
                    // 类引用了缺失的依赖等加载失败场景，直接跳过
                }
            }
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
     * 收集 classpath 条目里可能含可序列化 lambda 的类： 先按 class 字节里的方法名标记粗筛， 再由调用方加载类做精确判断， 避免把整个 classpath 的类都加载一遍。
     */
    static List<String> collectCandidateClassNames(Path entry) {
        if (Files.isDirectory(entry)) {
            return collectFromDirectory(entry);
        }
        if (Files.isRegularFile(entry)) {
            return collectFromJar(entry);
        }
        return new ArrayList<>();
    }

    private static List<String> collectFromDirectory(Path directory) {
        try (Stream<Path> stream = Files.walk(directory)) {
            return stream.map(directory::relativize).map(Path::toString)
                    .filter(name -> name.endsWith(".class") && !name.endsWith("module-info.class"))
                    .filter(name -> containsMarker(directory.resolve(name)))
                    .map(MybatisPlusLambdaCapturingFeature::toClassName).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
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
                if (entry != null && containsMarker(jarFile, entry)) {
                    candidates.add(toClassName(name));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return candidates;
    }

    private static boolean containsMarker(Path classFile) {
        try {
            return containsMarker(Files.readAllBytes(classFile));
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean containsMarker(JarFile jarFile, ZipEntry entry) {
        try (InputStream input = jarFile.getInputStream(entry)) {
            return containsMarker(input.readAllBytes());
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean containsMarker(byte[] content) {
        for (int i = 0; i <= content.length - DESERIALIZE_LAMBDA_MARKER.length; i++) {
            int j = 0;
            while (j < DESERIALIZE_LAMBDA_MARKER.length && content[i + j] == DESERIALIZE_LAMBDA_MARKER[j]) {
                j++;
            }
            if (j == DESERIALIZE_LAMBDA_MARKER.length) {
                return true;
            }
        }
        return false;
    }

    private static String toClassName(String classFilePath) {
        String name = classFilePath.substring(0, classFilePath.length() - ".class".length());
        return name.replace(File.separatorChar, '.').replace('/', '.');
    }
}
