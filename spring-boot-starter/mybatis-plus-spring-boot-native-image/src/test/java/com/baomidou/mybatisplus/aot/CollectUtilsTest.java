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
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectUtilsTest {

    @Test
    void findResourcesReturnsFullClasspathPathForFileResources() throws Exception {
        Path root = Files.createTempDirectory("aot-resources");
        Files.createDirectories(root.resolve("org/apache/ibatis/builder/xml"));
        Files.writeString(
                root.resolve("org/apache/ibatis/builder/xml/mybatis-3-mapper.dtd"), "", StandardCharsets.UTF_8);

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{root.toUri().toURL()}, null)) {
            CollectUtils collectUtils = new CollectUtils(classLoader);

            Set<String> resources = collectUtils
                    .findResources("org/apache/ibatis/builder/xml", name -> name.endsWith(".dtd"));

            assertTrue(resources.contains("org/apache/ibatis/builder/xml/mybatis-3-mapper.dtd"));
        }
    }

    @Test
    void findClassNamesFiltersJarEntriesByRequestedPackage() throws Exception {
        Path jar = Files.createTempFile("aot-classes", ".jar");
        writeJar(
                jar, "com/", "com/example/", "com/example/Target.class", "org/", "org/other/", "org/other/Other.class",
                "mapper/", "mapper/UserMapper.xml", "logback.xml");

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jar.toUri().toURL()}, null)) {
            CollectUtils collectUtils = new CollectUtils(classLoader);

            Set<String> classNames = collectUtils.findClassNames("com.example");
            Set<String> resources = collectUtils.findResources("mapper", name -> name.endsWith(".xml"));

            assertTrue(classNames.contains("com.example.Target"));
            assertFalse(classNames.contains("org.other.Other"));
            assertTrue(resources.contains("mapper/UserMapper.xml"));
            assertFalse(resources.contains("logback.xml"));
        }
    }

    @Test
    void findResourcesScansJarEntriesWithoutDirectoryEntries() throws Exception {
        Path jar = Files.createTempFile("aot-resources-no-directories", ".jar");
        writeJar(jar, "com/example/mapper/UserMapper.xml", "mapper/OrderMapper.xml", "logback.xml");

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jar.toUri().toURL()}, null)) {
            CollectUtils collectUtils = new CollectUtils(classLoader);

            Set<String> mapperResources = collectUtils.findResources("", CollectUtils::isMapperXmlResource);
            Set<String> resourcesUnderMapper = collectUtils.findResources("mapper", name -> name.endsWith(".xml"));

            assertTrue(mapperResources.contains("com/example/mapper/UserMapper.xml"));
            assertTrue(mapperResources.contains("mapper/OrderMapper.xml"));
            assertFalse(mapperResources.contains("logback.xml"));
            assertTrue(resourcesUnderMapper.contains("mapper/OrderMapper.xml"));
        }
    }

    @Test
    void mapperXmlFilterExcludesNonMapperXmlByDefault() {
        assertTrue(CollectUtils.isMapperXmlResource("mapper/UserMapper.xml"));
        assertTrue(CollectUtils.isMapperXmlResource("com/example/mapper/UserMapper.xml"));
        assertTrue(CollectUtils.isMapperXmlResource("mappers/UserMapper.xml"));
        assertFalse(CollectUtils.isMapperXmlResource("logback.xml"));
        assertFalse(CollectUtils.isMapperXmlResource("META-INF/spring/app.xml"));
    }

    @Test
    void mapperXmlFilterCanKeepLegacyAllXmlBehavior() {
        String previous = System.getProperty(CollectUtils.REGISTER_ALL_XML_PROPERTY);
        try {
            System.setProperty(CollectUtils.REGISTER_ALL_XML_PROPERTY, "true");

            assertTrue(CollectUtils.isMapperXmlResource("logback.xml"));
        } finally {
            if (previous == null) {
                System.clearProperty(CollectUtils.REGISTER_ALL_XML_PROPERTY);
            } else {
                System.setProperty(CollectUtils.REGISTER_ALL_XML_PROPERTY, previous);
            }
        }
    }

    @Test
    void findMainClassesFromNativeImagePropertiesUsesGenericInputStreamAndUtf8() throws Exception {
        Path root = Files.createTempDirectory("aot-native-image");
        Path properties = root.resolve("META-INF/native-image/example/native-image.properties");
        Files.createDirectories(properties.getParent());
        Files.writeString(properties, "Args = -H:Class=" + CollectUtilsTest.class.getName(), StandardCharsets.UTF_8);

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{root.toUri().toURL()},
                CollectUtilsTest.class.getClassLoader())) {
            CollectUtils collectUtils = new CollectUtils(classLoader);

            Set<Class<?>> mainClasses = collectUtils.findMainClassesFromNativeImageProperties();

            assertTrue(mainClasses.contains(CollectUtilsTest.class));
        }
    }

    public static void main(String[] args) {
    }

    private static void writeJar(Path jar, String... entries) throws IOException {
        try (JarOutputStream outputStream = new JarOutputStream(Files.newOutputStream(jar))) {
            for (String entry : entries) {
                JarEntry jarEntry = new JarEntry(entry);
                outputStream.putNextEntry(jarEntry);
                outputStream.closeEntry();
            }
        }
    }
}
