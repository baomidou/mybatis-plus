package com.baomidou.mybatisplus.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SourcePatchToolTest {
    private static final String SOURCE = "package example;\nclass Original {}\n";
    private static final String TARGET = "package enhanced;\nclass Enhanced { int value = 1; }\n";
    private static final String MAPPING = "example.Original=enhanced.Enhanced\n";
    private static final String HEADER = "version=1.0\nmoudle=core\n";

    @TempDir
    Path repository;

    @Test
    void generatesApplicablePatchForDifferentPackagesAndClassNames() throws Exception {
        SourcePatchTool tool = fixture(SOURCE, TARGET.replace("\n", "\r\n"));
        tool.generatePatches();
        String patch = Files.readString(patch());
        assertTrue(patch.contains("--- a/core/src/main/java/enhanced/Enhanced.java\n"));
        assertTrue(patch.contains("+++ b/core/src/main/java/enhanced/Enhanced.java\n"));
        assertTrue(patch.contains("-package example;\n"));
        assertTrue(patch.contains("+package enhanced;\n"));
        assertFalse(patch.contains("\r"));
        tool.check(Map.of("sample", "1.0"));
        tool.generatePatches();
        assertEquals(patch, Files.readString(patch()));
    }

    @Test
    void detectsStalePatchWithoutChangingSourcesRecordsOrPatch() throws Exception {
        SourcePatchTool tool = fixture(SOURCE, TARGET);
        tool.generatePatches();
        String patch = Files.readString(patch());
        String records = Files.readString(records());
        String changed = TARGET.replace("value = 1", "value = 2");
        Files.writeString(enhanced(), changed);
        IllegalStateException error =
                assertThrows(IllegalStateException.class, () -> tool.check(Map.of("sample", "1.0")));
        assertTrue(error.getMessage().contains("Patch is out of date"));
        assertEquals(patch, Files.readString(patch()));
        assertEquals(records, Files.readString(records()));
        assertEquals(SOURCE, Files.readString(upstream()));
        assertEquals(changed, Files.readString(enhanced()));
    }

    @Test
    void checksResolvedVersionBeforeCheckingMissingPatch() throws Exception {
        SourcePatchTool tool = fixture(SOURCE, TARGET);
        IllegalStateException error =
                assertThrows(IllegalStateException.class, () -> tool.check(Map.of("sample", "2.0")));
        assertTrue(error.getMessage().contains("recorded 1.0, dependency 2.0"));
        assertFalse(Files.exists(patch()));
        error = assertThrows(IllegalStateException.class, () -> tool.check(Map.of("sample", "1.0")));
        assertTrue(error.getMessage().contains("generate the missing patch"));
    }

    @Test
    void rejectsEditedPatch() throws Exception {
        SourcePatchTool tool = fixture(SOURCE, TARGET);
        tool.generatePatches();
        Files.writeString(patch(), Files.readString(patch()).replace("value = 1", "value = 9"));
        assertThrows(IllegalStateException.class, () -> tool.check(Map.of("sample", "1.0")));
    }

    @Test
    void supportsEmptyPatchesAndFilesWithoutFinalNewline() throws Exception {
        SourcePatchTool tool = fixture(SOURCE, SOURCE);
        tool.generatePatches();
        assertEquals("", Files.readString(patch()));
        tool.check(Map.of("sample", "1.0"));
        Files.writeString(enhanced(), TARGET.stripTrailing());
        tool.generatePatches();
        assertTrue(Files.readString(patch()).contains("\\ No newline at end of file"));
        tool.check(Map.of("sample", "1.0"));
    }

    @Test
    void extractionPreservesPackageAndDefersVersionUpdate() throws Exception {
        SourcePatchTool tool = fixture("old", TARGET);
        String records = Files.readString(records());
        tool.extract(Map.of("sample", sourceJar(SOURCE.replace("\n", "\r\n"))));
        assertEquals(SOURCE, Files.readString(upstream()));
        assertEquals(TARGET, Files.readString(enhanced()));
        assertEquals(records, Files.readString(records()));
        tool.recordVersions(Map.of("sample", "2.0"));
        Properties updated = new Properties();
        try (var reader = Files.newBufferedReader(records())) {
            updated.load(reader);
        }
        assertEquals("2.0", updated.getProperty("version"));
        assertEquals("core", updated.getProperty("moudle"));
        assertEquals("enhanced.Enhanced", updated.getProperty("example.Original"));
        String first = Files.readString(records());
        tool.recordVersions(Map.of("sample", "2.0"));
        assertEquals(first, Files.readString(records()));
    }

    @Test
    void missingArchiveEntryDoesNotOverwriteAnyBaseline() throws Exception {
        fixture("old", TARGET);
        Files.writeString(records(), HEADER + MAPPING + "example.ZMissing=enhanced.Missing\n");
        SourcePatchTool tool = new SourcePatchTool(repository, module());
        assertThrows(IllegalArgumentException.class, () -> tool.extract(Map.of("sample", sourceJar(SOURCE))));
        assertEquals("old", Files.readString(upstream()));
        assertFalse(Files.exists(module().resolve("src/main/java/example/ZMissing.java")));
    }

    @Test
    void missingEnhancedFileDoesNotPartiallyRegeneratePatches() throws Exception {
        SourcePatchTool tool = fixture(SOURCE, TARGET);
        tool.generatePatches();
        String patch = Files.readString(patch());
        Files.writeString(records(), HEADER + MAPPING + "example.ZMissing=enhanced.Missing\n");
        Files.writeString(upstream(), SOURCE.replace("Original", "Changed"));
        tool = new SourcePatchTool(repository, module());
        SourcePatchTool updated = tool;
        assertThrows(IllegalStateException.class, updated::generatePatches);
        assertEquals(patch, Files.readString(patch()));
    }

    @Test
    void rejectsMappingsOutsideRepositoryAndDuplicateTargets() throws Exception {
        fixture(SOURCE, TARGET);
        Files.writeString(records(), "version=1.0\nmoudle=../Outside\n" + MAPPING);
        assertThrows(IllegalArgumentException.class, () -> new SourcePatchTool(repository, module()));
        Files.writeString(records(), HEADER + MAPPING + "example.Other=enhanced.Enhanced\n");
        assertThrows(IllegalArgumentException.class, () -> new SourcePatchTool(repository, module()));
    }

    @Test
    void maintainsSameClassAcrossGroupsAndUpdatesVersionsInTheirOwnFiles() throws Exception {
        fixture(SOURCE, TARGET);
        Files.createDirectories(repository.resolve("core2/src/main/java/enhanced"));
        Files.createDirectories(repository.resolve("starters/core3/src/main/java/enhanced"));
        Files.writeString(repository.resolve("starters/core3/build.gradle"), "");
        Files.writeString(repository.resolve("core2/src/main/java/enhanced/Enhanced.java"), TARGET);
        Files.writeString(repository.resolve("starters/core3/src/main/java/enhanced/Enhanced.java"), TARGET);
        Files.writeString(records().resolveSibling("boot2.properties"), """
                version=2.0
                moudle=core2
                example.Original=enhanced.Enhanced
                """);
        Files.writeString(records().resolveSibling("boot3.properties"), """
                version=3.0
                moudle=core3
                example.Original=enhanced.Enhanced
                """);
        SourcePatchTool tool = new SourcePatchTool(repository, module());
        String boot2 = SOURCE.replace("Original {}", "Original { int version = 2; }");
        String boot3 = SOURCE.replace("Original {}", "Original { int version = 3; }");
        tool.extract(Map.of(
                "sample",
                sourceJar("sample.jar", SOURCE),
                "boot2",
                sourceJar("boot2.jar", boot2),
                "boot3",
                sourceJar("boot3.jar", boot3)));
        assertEquals(boot2, Files.readString(module().resolve("src/main/java/boot2/example/Original.java")));
        assertEquals(boot3, Files.readString(module().resolve("src/main/java/boot3/example/Original.java")));
        assertEquals(TARGET, Files.readString(enhanced()));
        tool.generatePatches();
        assertTrue(Files.isRegularFile(patch()));
        assertTrue(Files.isRegularFile(module().resolve("src/main/java/boot2/example/Original.patch")));
        assertTrue(Files.isRegularFile(module().resolve("src/main/java/boot3/example/Original.patch")));
        tool.check(Map.of("sample", "1.0", "boot2", "2.0", "boot3", "3.0"));
        IllegalStateException error = assertThrows(
                IllegalStateException.class, () -> tool.check(Map.of("sample", "1.0", "boot2", "2.0", "boot3", "3.1")));
        assertTrue(error.getMessage().contains("boot3 version mismatch"));
        tool.recordVersions(Map.of("sample", "1.1", "boot2", "2.1", "boot3", "3.1"));
        assertTrue(Files.readString(records()).startsWith("version=1.1\nmoudle=core\n"));
        String boot2Records = Files.readString(records().resolveSibling("boot2.properties"));
        assertTrue(boot2Records.startsWith("version=2.1\nmoudle=core2\n"));
        assertEquals(3, boot2Records.lines().count());
        assertTrue(Files.readString(records().resolveSibling("boot3.properties"))
                .startsWith("version=3.1\nmoudle=core3\n"));
    }

    @Test
    void allowsUpWithAnUnfinishedLocalClassButRequiresItForPatchAndCheck() throws Exception {
        fixture(SOURCE, TARGET);
        Files.writeString(records(), HEADER + "example.Original=\n");
        SourcePatchTool tool = new SourcePatchTool(repository, module());
        tool.extract(Map.of("sample", sourceJar(SOURCE)));
        IllegalStateException error = assertThrows(IllegalStateException.class, tool::generatePatches);
        assertTrue(error.getMessage().contains("Missing local class for example.Original"));
        assertThrows(IllegalStateException.class, () -> tool.check(Map.of("sample", "1.0")));
        assertFalse(Files.exists(patch()));
        tool.recordVersions(Map.of("sample", "1.1"));
        assertTrue(Files.readString(records()).startsWith("version=1.1\nmoudle=core\n"));
        Files.writeString(records(), HEADER + "example.Original=../../Outside.java\n");
        assertThrows(IllegalArgumentException.class, () -> new SourcePatchTool(repository, module()));
    }

    @Test
    void maintainsCoreSourcesAndPatchesInTheirSourceSetDirectory() throws Exception {
        fixture(SOURCE, TARGET);
        Files.move(records(), records().resolveSibling("core.properties"));
        Files.delete(upstream());
        SourcePatchTool tool = new SourcePatchTool(repository, module());
        tool.extract(Map.of("core", sourceJar(SOURCE)));
        Path coreSource = module().resolve("src/main/java/core/example/Original.java");
        Path corePatch = coreSource.resolveSibling("Original.patch");
        assertEquals(SOURCE, Files.readString(coreSource));
        assertFalse(Files.exists(upstream()));
        tool.generatePatches();
        assertTrue(Files.isRegularFile(corePatch));
        assertFalse(Files.exists(patch()));
        tool.check(Map.of("core", "1.0"));
        tool.recordVersions(Map.of("core", "1.1"));
        assertTrue(
                Files.readString(records().resolveSibling("core.properties")).startsWith("version=1.1\nmoudle=core\n"));
    }

    @Test
    void rejectsDuplicatePhysicalSourcesAcrossGroups() throws Exception {
        fixture(SOURCE, TARGET);
        Files.writeString(records().resolveSibling("duplicate.properties"), HEADER + MAPPING);
        IllegalArgumentException error =
                assertThrows(IllegalArgumentException.class, () -> new SourcePatchTool(repository, module()));
        assertTrue(error.getMessage().contains("Duplicate upstream source mapping"));
        assertTrue(Files.readString(records()).startsWith("version=1.0\n"));
    }

    private SourcePatchTool fixture(String source, String target) throws Exception {
        Files.createDirectories(records().getParent());
        Files.writeString(records(), HEADER + MAPPING);
        Files.createDirectories(upstream().getParent());
        Files.writeString(upstream(), source);
        Files.createDirectories(enhanced().getParent());
        Files.writeString(enhanced(), target);
        return new SourcePatchTool(repository, module());
    }

    private Path sourceJar(String source) throws Exception {
        return sourceJar("sources.jar", source);
    }

    private Path sourceJar(String name, String source) throws Exception {
        Path jar = repository.resolve(name);
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar))) {
            output.putNextEntry(new JarEntry("example/Original.java"));
            output.write(source.getBytes(StandardCharsets.UTF_8));
            output.closeEntry();
        }
        return jar;
    }

    private Path module() {
        return repository.resolve("up");
    }

    private Path records() {
        return module().resolve("upstream/sample.properties");
    }

    private Path upstream() {
        return module().resolve("src/main/java/example/Original.java");
    }

    private Path enhanced() {
        return repository.resolve("core/src/main/java/enhanced/Enhanced.java");
    }

    private Path patch() {
        return module().resolve("src/main/java/example/Original.patch");
    }
}
