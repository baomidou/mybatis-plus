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
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        // MP 提取 lambda 必经 JDK 序列化机制（writeReplace 产生 SerializedLambda 流），
        // 该机制可达才说明项目用到了 lambda 条件构造器，此时才扫描注册，其余项目零开销
        access.registerReachabilityHandler(
                MybatisPlusLambdaCapturingFeature::registerCapturingClasses, SerializedLambda.class);
    }

    private static void registerCapturingClasses(DuringAnalysisAccess access) {
        for (Path entry : access.getApplicationClassPath()) {
            // 依赖以 jar 进入 image classpath，目录才是应用自己的 class（含 spring-aot 生成目录）
            if (!Files.isDirectory(entry)) {
                continue;
            }
            for (String className : collectClassNames(entry)) {
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

    static List<String> collectClassNames(Path directory) {
        try (Stream<Path> stream = Files.walk(directory)) {
            return stream.map(directory::relativize).map(Path::toString)
                    .filter(name -> name.endsWith(".class") && !name.endsWith("module-info.class"))
                    .map(MybatisPlusLambdaCapturingFeature::toClassName).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static String toClassName(String classFilePath) {
        String name = classFilePath.substring(0, classFilePath.length() - ".class".length());
        return name.replace(File.separatorChar, '.');
    }
}
