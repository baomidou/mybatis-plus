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
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author xiaochen
 * @since 2026-11-04
 */
class MybatisPlusLambdaCapturingFeatureTest {

    private static final String PACKAGE_PATH = "com/baomidou/mybatisplus/aot/";

    private static final String LAMBDA_CLASS = "MybatisPlusLambdaCapturingFeatureTest$WithSerializableLambda";

    private static final String PLAIN_CLASS = "MybatisPlusLambdaCapturingFeatureTest$SerializableSupplier";

    interface SerializableSupplier extends Serializable {

        Object get();
    }

    static class WithSerializableLambda {

        static final SerializableSupplier SUPPLIER = () -> "x";
    }

    static class ApplicationMain {

        public static void main(String[] args) {
        }
    }

    static class InheritedApplicationMain extends ApplicationMain {
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
     * 目录型 classpath 元素：按 class 字节里的方法名标记粗筛，含可序列化 lambda 的类被留下，其余类不加载。
     */
    @Test
    void directoryScanKeepsOnlyClassesWithSerializableLambdas() throws IOException {
        Path dir = Files.createTempDirectory("aot-classes");
        writeClassFile(dir, LAMBDA_CLASS);
        writeClassFile(dir, PLAIN_CLASS);
        Files.write(dir.resolve("module-info.class"), new byte[0]);
        Files.write(dir.resolve(PACKAGE_PATH + "note.txt"), new byte[0]);
        assertThat(MybatisPlusLambdaCapturingFeature.collectCandidateClassNames(dir))
                .containsExactly("com.baomidou.mybatisplus.aot." + LAMBDA_CLASS);
    }

    /**
     * jar 型 classpath 元素：应用打成 jar 再构建 native 镜像时，捕获类在 jar 里，同样要能被扫出来。
     */
    @Test
    void jarScanKeepsOnlyClassesWithSerializableLambdas() throws IOException {
        Path jar = Files.createTempFile("aot-classes", ".jar");
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar))) {
            writeJarEntry(out, LAMBDA_CLASS);
            writeJarEntry(out, PLAIN_CLASS);
            out.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
            out.closeEntry();
        }
        assertThat(MybatisPlusLambdaCapturingFeature.collectCandidateClassNames(jar))
                .containsExactly("com.baomidou.mybatisplus.aot." + LAMBDA_CLASS);
    }

    @Test
    void startClassFromManifestIsAvailableForApplicationPackageFiltering() throws IOException {
        Path jar = createJar("aot-start-classes", "com.example.application.Main", null);
        assertThat(MybatisPlusLambdaCapturingFeature.readStartClassName(jar)).isEqualTo("com.example.application.Main");
    }

    /**
     * 回归：依赖 jar 里带 public static main 的类（byte-buddy、JNA 等真实依赖就是这种形态）不能被当成应用入口。 否则应用包被替换成依赖包的集合，业务类里的捕获类会被全部过滤掉，native 下
     * lambda 反序列化失败。
     */
    @Test
    void dependencyJarWithMainMethodButNoStartClassDoesNotNarrowApplicationPackages() throws IOException {
        Path jar = createJar("aot-dependency-main", null, "MybatisPlusLambdaCapturingFeatureTest$ApplicationMain");
        assertThat(MybatisPlusLambdaCapturingFeature.resolveApplicationEntries(List.of(jar), this::loadClass))
                .isEmpty();
    }

    /**
     * 目录型 classpath（target/classes、build/classes）没有 manifest，不能凭类里的 main 方法猜应用入口。
     */
    @Test
    void directoryWithMainMethodDoesNotNarrowApplicationEntries() throws IOException {
        Path dir = Files.createTempDirectory("aot-main-classes");
        writeClassFile(dir, "MybatisPlusLambdaCapturingFeatureTest$ApplicationMain");
        assertThat(MybatisPlusLambdaCapturingFeature.resolveApplicationEntries(List.of(dir), this::loadClass))
                .isEmpty();
    }

    /**
     * manifest 的 Start-Class 指向的类把 main 定义在父类时同样是合法入口，必须能识别（getMethod 而非 getDeclaredMethod）。
     */
    @Test
    void startClassWithInheritedMainContributesApplicationEntry() throws IOException {
        String className = "com.baomidou.mybatisplus.aot.MybatisPlusLambdaCapturingFeatureTest$InheritedApplicationMain";
        Path jar = createJar("aot-inherited-main", className, null);
        assertThat(MybatisPlusLambdaCapturingFeature.resolveApplicationEntries(List.of(jar), this::loadClass))
                .containsExactly(jar);
    }

    private Path createJar(String prefix, String startClass, String nestedClassName) throws IOException {
        Path jar = Files.createTempFile(prefix, ".jar");
        java.util.jar.Manifest manifest = new java.util.jar.Manifest();
        manifest.getMainAttributes().put(java.util.jar.Attributes.Name.MANIFEST_VERSION, "1.0");
        if (startClass != null) {
            manifest.getMainAttributes().putValue("Start-Class", startClass);
        }
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar), manifest)) {
            if (nestedClassName != null) {
                writeJarEntry(out, nestedClassName);
            }
        }
        return jar;
    }

    private Class<?> loadClass(String className) {
        try {
            return Class.forName(className, false, getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * 多版本 jar：基础版无 lambda、版本化实现有 lambda 时，运行版本视图下该类仍要被扫出来。
     */
    @Test
    void multiReleaseJarUsesVersionedEntries() throws IOException {
        Path jar = Files.createTempFile("aot-mr", ".jar");
        java.util.jar.Manifest manifest = new java.util.jar.Manifest();
        manifest.getMainAttributes().put(java.util.jar.Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().putValue("Multi-Release", "true");
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar), manifest)) {
            out.putNextEntry(new ZipEntry(PACKAGE_PATH + "Impl.class"));
            out.write(classBytes(PLAIN_CLASS));
            out.closeEntry();
            out.putNextEntry(
                    new ZipEntry(
                            "META-INF/versions/" + Runtime.version().feature() + "/" + PACKAGE_PATH + "Impl.class"));
            out.write(classBytes(LAMBDA_CLASS));
            out.closeEntry();
        }
        assertThat(MybatisPlusLambdaCapturingFeature.collectCandidateClassNames(jar))
                .containsExactly("com.baomidou.mybatisplus.aot.Impl");
    }

    @Test
    void invalidJarFailsInsteadOfSilentlySkippingLambdaCandidates() throws IOException {
        Path jar = Files.createTempFile("invalid-aot-classes", ".jar");
        Files.writeString(jar, "not a jar");
        assertThatThrownBy(() -> MybatisPlusLambdaCapturingFeature.collectCandidateClassNames(jar))
                .isInstanceOf(UncheckedIOException.class)
                .hasMessageContaining("Failed to scan lambda capturing classes");
    }

    /**
     * javac 只给含可序列化 lambda 的类生成 $deserializeLambda$ 合成方法，据此把注册范围收敛到真正需要放行的类。
     */
    @Test
    void onlyClassesWithSerializableLambdasPassTheFilter() {
        assertThat(MybatisPlusLambdaCapturingFeature.hasDeserializeLambdaMethod(WithSerializableLambda.class)).isTrue();
        assertThat(MybatisPlusLambdaCapturingFeature.hasDeserializeLambdaMethod(Object.class)).isFalse();
    }

    private void writeClassFile(Path root, String className) throws IOException {
        Path target = root.resolve(PACKAGE_PATH + className + ".class");
        Files.createDirectories(target.getParent());
        Files.write(target, classBytes(className));
    }

    private void writeJarEntry(JarOutputStream out, String className) throws IOException {
        out.putNextEntry(new ZipEntry(PACKAGE_PATH + className + ".class"));
        out.write(classBytes(className));
        out.closeEntry();
    }

    private byte[] classBytes(String className) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(className + ".class")) {
            assertThat(in).as("测试自身的 class 文件必须在 classpath 上: %s", className).isNotNull();
            return in.readAllBytes();
        }
    }
}
