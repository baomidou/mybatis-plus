package com.baomidou.mybatisplus.core;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Regression for #7150: version must resolve from Implementation-Version in the JAR manifest.
 */
class MybatisPlusVersionTest {

    @Test
    void getVersionDoesNotThrow() {
        assertThatCode(MybatisPlusVersion::getVersion).doesNotThrowAnyException();
    }

    @Test
    void readsImplementationVersionFromJarManifest() throws Exception {
        Path jarPath = Files.createTempFile("mybatis-plus-version-", ".jar");
        try {
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
            manifest.getMainAttributes().put(Attributes.Name.IMPLEMENTATION_VERSION, "3.5.17");
            try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(jarPath), manifest)) {
                jos.putNextEntry(new ZipEntry("dummy.txt"));
                jos.write("x".getBytes());
                jos.closeEntry();
            }
            try (JarFile jarFile = new JarFile(jarPath.toFile())) {
                assertThat(invokeGetImplementationVersion(jarFile)).isEqualTo("3.5.17");
            }
        } finally {
            Files.deleteIfExists(jarPath);
        }
    }

    @Test
    void returnsNullWhenJarHasNoManifest() throws Exception {
        Path jarPath = Files.createTempFile("mybatis-plus-no-manifest-", ".jar");
        try {
            // Zip without META-INF/MANIFEST.MF so JarFile#getManifest() is null
            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(jarPath))) {
                zos.putNextEntry(new ZipEntry("dummy.txt"));
                zos.write("x".getBytes());
                zos.closeEntry();
            }
            try (JarFile jarFile = new JarFile(jarPath.toFile())) {
                assertThat(invokeGetImplementationVersion(jarFile)).isNull();
            }
        } finally {
            Files.deleteIfExists(jarPath);
        }
    }

    private static String invokeGetImplementationVersion(JarFile jarFile) throws Exception {
        Method method = MybatisPlusVersion.class.getDeclaredMethod("getImplementationVersion", JarFile.class);
        method.setAccessible(true);
        return (String) method.invoke(null, jarFile);
    }
}
