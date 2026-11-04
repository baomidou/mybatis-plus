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

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author xiaochen
 * @since 2026-11-04
 */
class MybatisPlusLambdaCapturingFeatureTest {

    interface SerializableSupplier extends Serializable {

        Object get();
    }

    static class WithSerializableLambda {

        static final SerializableSupplier SUPPLIER = () -> "x";
    }

    /**
     * native 下的发现链路：jar 内 native-image.properties 以 Args 声明 --features，native-image 自动读取合并（无需用户传参）。 注意不能用
     * META-INF/services 自动发现：FeatureHandler 经驱动 JVM 的原始 classloader 加载服务，看不到应用侧 jar。类名或文件路径写错时在 JVM 测试里就能暴露。
     */
    @Test
    void nativeImagePropertiesDeclaresTheFeature() throws IOException {
        Properties properties = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(
                "META-INF/native-image/com.baomidou/mybatis-plus-spring-boot-native-image/native-image.properties")) {
            assertThat(in).isNotNull();
            properties.load(in);
        }
        assertThat(properties.getProperty("Args"))
                .isEqualTo("--features=" + MybatisPlusLambdaCapturingFeature.class.getName());
    }

    /**
     * 捕获类只从目录型 classpath 元素收集（依赖走 jar），子包展开、非 class 文件与 module-info 跳过。
     */
    @Test
    void collectClassNamesWalksNestedPackagesAndSkipsNonClassFiles() throws IOException {
        Path dir = Files.createTempDirectory("aot-classes");
        Files.createDirectories(dir.resolve("com/demo"));
        Files.write(dir.resolve("com/demo/App.class"), new byte[0]);
        Files.write(dir.resolve("com/demo/App$Inner.class"), new byte[0]);
        Files.write(dir.resolve("module-info.class"), new byte[0]);
        Files.write(dir.resolve("com/demo/note.txt"), new byte[0]);
        assertThat(MybatisPlusLambdaCapturingFeature.collectClassNames(dir))
                .containsExactlyInAnyOrder("com.demo.App", "com.demo.App$Inner");
    }

    /**
     * javac 只给含可序列化 lambda 的类生成 $deserializeLambda$ 合成方法，据此把注册范围收敛到真正需要放行的类。
     */
    @Test
    void onlyClassesWithSerializableLambdasPassTheFilter() {
        assertThat(MybatisPlusLambdaCapturingFeature.hasDeserializeLambdaMethod(WithSerializableLambda.class)).isTrue();
        assertThat(MybatisPlusLambdaCapturingFeature.hasDeserializeLambdaMethod(Object.class)).isFalse();
    }
}
