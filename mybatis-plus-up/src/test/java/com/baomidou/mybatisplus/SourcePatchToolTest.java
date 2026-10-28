package com.baomidou.mybatisplus;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
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
        assertThat(patch).contains("--- a/core/src/main/java/enhanced/Enhanced.java\n");
        assertThat(patch).contains("+++ b/core/src/main/java/enhanced/Enhanced.java\n");
        assertThat(patch).contains("-package example;\n");
        assertThat(patch).contains("+package enhanced;\n");
        assertThat(patch).doesNotContain("\r");
        tool.check(Map.of("sample", "1.0"));
        tool.generatePatches();
        assertThat(Files.readString(patch())).isEqualTo(patch);
    }

    @Test
    void detectsStalePatchWithoutChangingSourcesRecordsOrPatch() throws Exception {
        SourcePatchTool tool = fixture(SOURCE, TARGET);
        tool.generatePatches();
        String patch = Files.readString(patch());
        String records = Files.readString(records());
        String changed = TARGET.replace("value = 1", "value = 2");
        Files.writeString(enhanced(), changed);
        assertThatThrownBy(() -> tool.check(Map.of("sample", "1.0"))).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Patch is out of date");
        assertThat(Files.readString(patch())).isEqualTo(patch);
        assertThat(Files.readString(records())).isEqualTo(records);
        assertThat(Files.readString(upstream())).isEqualTo(SOURCE);
        assertThat(Files.readString(enhanced())).isEqualTo(changed);
    }

    @Test
    void checksResolvedVersionBeforeCheckingMissingPatch() throws Exception {
        SourcePatchTool tool = fixture(SOURCE, TARGET);
        assertThatThrownBy(() -> tool.check(Map.of("sample", "2.0"))).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("recorded 1.0, dependency 2.0");
        assertThat(patch()).doesNotExist();
        assertThatThrownBy(() -> tool.check(Map.of("sample", "1.0"))).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("generate the missing patch");
    }

    @Test
    void rejectsEditedPatch() throws Exception {
        SourcePatchTool tool = fixture(SOURCE, TARGET);
        tool.generatePatches();
        Files.writeString(patch(), Files.readString(patch()).replace("value = 1", "value = 9"));
        assertThatThrownBy(() -> tool.check(Map.of("sample", "1.0"))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void supportsEmptyPatchesAndFilesWithoutFinalNewline() throws Exception {
        SourcePatchTool tool = fixture(SOURCE, SOURCE);
        tool.generatePatches();
        assertThat(Files.readString(patch())).isEmpty();
        tool.check(Map.of("sample", "1.0"));
        Files.writeString(enhanced(), TARGET.stripTrailing());
        tool.generatePatches();
        assertThat(Files.readString(patch())).contains("\\ No newline at end of file");
        tool.check(Map.of("sample", "1.0"));
    }

    @Test
    void extractionPreservesPackageAndDefersVersionUpdate() throws Exception {
        SourcePatchTool tool = fixture("old", TARGET);
        String records = Files.readString(records());
        tool.extract(Map.of("sample", List.of(sourceJar(SOURCE.replace("\n", "\r\n")))));
        assertThat(Files.readString(upstream())).isEqualTo(SOURCE);
        assertThat(Files.readString(enhanced())).isEqualTo(TARGET);
        assertThat(Files.readString(records())).isEqualTo(records);
        tool.recordVersions(Map.of("sample", "2.0"));
        Properties updated = new Properties();
        try (var reader = Files.newBufferedReader(records())) {
            updated.load(reader);
        }
        assertThat(updated.getProperty("version")).isEqualTo("2.0");
        assertThat(updated.getProperty("moudle")).isEqualTo("core");
        assertThat(updated.getProperty("example.Original")).isEqualTo("enhanced.Enhanced");
        String first = Files.readString(records());
        tool.recordVersions(Map.of("sample", "2.0"));
        assertThat(Files.readString(records())).isEqualTo(first);
    }

    @Test
    void extractsOneGroupFromMultipleArchivesThroughTheCommandLine() throws Exception {
        fixture("old", TARGET);
        Files.writeString(records(), HEADER + MAPPING + "example.TestSupport=?\n");
        Path main = sourceJar("main.jar", SOURCE);
        String support = "package example;\nclass TestSupport {}\n";
        Path test = sourceJar("test.jar", "example/TestSupport.java", support);
        SourcePatchTool.main(
                new String[]{"extract", repository.toString(), module().toString(), "sample", "1.0", main.toString(),
                        "sample", "1.0", test.toString()});
        assertThat(Files.readString(upstream())).isEqualTo(SOURCE);
        assertThat(Files.readString(upstream().resolveSibling("TestSupport.java"))).isEqualTo(support);
        assertThat(Files.readString(records())).isEqualTo(HEADER + MAPPING + "example.TestSupport=?\n");
        SourcePatchTool tool = new SourcePatchTool(repository, module());
        tool.generatePatches();
        tool.check(Map.of("sample", "1.0"));
    }

    @Test
    void rejectsConflictingArchiveVersionsBeforeReplacingSources() throws Exception {
        fixture("old", TARGET);
        Path main = sourceJar("main.jar", SOURCE);
        Path test = sourceJar("test.jar", SOURCE);
        assertThatThrownBy(
                () -> SourcePatchTool.main(
                        new String[]{"extract", repository.toString(), module().toString(), "sample", "1.0",
                                main.toString(), "sample", "2.0", test.toString()}))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Conflicting upstream versions");
        assertThat(Files.readString(upstream())).isEqualTo("old");
    }

    @Test
    void rejectsDuplicateArchiveEntriesBeforeReplacingSources() throws Exception {
        SourcePatchTool tool = fixture("old", TARGET);
        Path main = sourceJar("main.jar", SOURCE);
        Path test = sourceJar("test.jar", SOURCE);
        assertThatThrownBy(() -> tool.extract(Map.of("sample", List.of(main, test))))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Duplicate upstream source");
        assertThat(Files.readString(upstream())).isEqualTo("old");
    }

    @Test
    void missingArchiveEntryDoesNotOverwriteAnyBaseline() throws Exception {
        fixture("old", TARGET);
        Files.writeString(records(), HEADER + MAPPING + "example.ZMissing=enhanced.Missing\n");
        SourcePatchTool tool = new SourcePatchTool(repository, module());
        Path main = sourceJar("main.jar", SOURCE);
        Path test = sourceJar("test.jar", "example/Other.java", "package example;\nclass Other {}\n");
        assertThatThrownBy(() -> tool.extract(Map.of("sample", List.of(main, test))))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(Files.readString(upstream())).isEqualTo("old");
        assertThat(module().resolve("src/main/java/example/ZMissing.java")).doesNotExist();
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
        assertThatThrownBy(updated::generatePatches).isInstanceOf(IllegalStateException.class);
        assertThat(Files.readString(patch())).isEqualTo(patch);
    }

    @Test
    void rejectsMappingsOutsideRepositoryAndDuplicateTargets() throws Exception {
        fixture(SOURCE, TARGET);
        Files.writeString(records(), "version=1.0\nmoudle=../Outside\n" + MAPPING);
        assertThatThrownBy(() -> new SourcePatchTool(repository, module()))
                .isInstanceOf(IllegalArgumentException.class);
        Files.writeString(records(), HEADER + MAPPING + "example.Other=enhanced.Enhanced\n");
        assertThatThrownBy(() -> new SourcePatchTool(repository, module()))
                .isInstanceOf(IllegalArgumentException.class);
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
        tool.extract(
                Map.of(
                        "sample", List.of(sourceJar("sample.jar", SOURCE)), "boot2",
                        List.of(sourceJar("boot2.jar", boot2)), "boot3", List.of(sourceJar("boot3.jar", boot3))));
        assertThat(Files.readString(module().resolve("src/main/java/boot2/example/Original.java"))).isEqualTo(boot2);
        assertThat(Files.readString(module().resolve("src/main/java/boot3/example/Original.java"))).isEqualTo(boot3);
        assertThat(Files.readString(enhanced())).isEqualTo(TARGET);
        tool.generatePatches();
        assertThat(patch()).isRegularFile();
        assertThat(module().resolve("src/main/java/boot2/example/Original.patch")).isRegularFile();
        assertThat(module().resolve("src/main/java/boot3/example/Original.patch")).isRegularFile();
        tool.check(Map.of("sample", "1.0", "boot2", "2.0", "boot3", "3.0"));
        assertThatThrownBy(() -> tool.check(Map.of("sample", "1.0", "boot2", "2.0", "boot3", "3.1")))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("boot3 version mismatch");
        tool.recordVersions(Map.of("sample", "1.1", "boot2", "2.1", "boot3", "3.1"));
        assertThat(Files.readString(records())).startsWith("version=1.1\nmoudle=core\n");
        String boot2Records = Files.readString(records().resolveSibling("boot2.properties"));
        assertThat(boot2Records).startsWith("version=2.1\nmoudle=core2\n");
        assertThat(boot2Records.lines().count()).isEqualTo(3);
        String boot3Records = Files.readString(records().resolveSibling("boot3.properties"));
        assertThat(boot3Records).startsWith("version=3.1\nmoudle=core3\n");
    }

    @Test
    void maintainsSpringVersionsWithOneSharedEnhancedClass() throws Exception {
        fixture(SOURCE, TARGET);
        Files.move(records(), records().resolveSibling("spring2.properties"));
        Files.delete(upstream());
        Files.writeString(records().resolveSibling("spring3.properties"), HEADER + MAPPING);
        Files.writeString(records().resolveSibling("spring4.properties"), HEADER + MAPPING);
        SourcePatchTool tool = new SourcePatchTool(repository, module());
        tool.extract(
                Map.of(
                        "spring2", List.of(sourceJar("spring2.jar", SOURCE)), "spring3",
                        List.of(
                                sourceJar(
                                        "spring3.jar", SOURCE.replace("Original {}", "Original { int version = 3; }"))),
                        "spring4",
                        List.of(
                                sourceJar(
                                        "spring4.jar",
                                        SOURCE.replace("Original {}", "Original { int version = 4; }")))));
        tool.generatePatches();
        for (String group : new String[]{"spring2", "spring3", "spring4"}) {
            Path source = module().resolve("src/main/java/" + group + "/example/Original.java");
            assertThat(source).isRegularFile();
            assertThat(source.resolveSibling("Original.patch")).isRegularFile();
        }
        assertThat(upstream()).doesNotExist();
        assertThat(Files.readString(enhanced())).isEqualTo(TARGET);
        tool.check(Map.of("spring2", "1.0", "spring3", "1.0", "spring4", "1.0"));
        Map<String, String> versions = Map.of("spring2", "2.1", "spring3", "3.1", "spring4", "4.1");
        tool.recordVersions(versions);
        for (var version : versions.entrySet()) {
            assertThat(Files.readString(records().resolveSibling(version.getKey() + ".properties")))
                    .startsWith("version=" + version.getValue() + "\n");
        }
        new SourcePatchTool(repository, module()).check(versions);
    }

    @Test
    void comparesAdjacentUpstreamVersionsInBothFamiliesIncludingCopyOnlyClasses() throws Exception {
        fixture(SOURCE, TARGET);
        Map<String, String> versions = new LinkedHashMap<>(Map.of("sample", "1.0"));
        for (String family : new String[]{"spring", "boot"}) {
            for (int version = 2; version <= 4; version++) {
                String group = family + version;
                Files.writeString(records().resolveSibling(group + ".properties"), HEADER + "example.Original=?\n");
                Path source = module().resolve("src/main/java/" + group + "/example/Original.java");
                Files.createDirectories(source.getParent());
                Files.writeString(source, SOURCE.replace("Original {}", "Original { int version = " + version + "; }"));
                versions.put(group, "1.0");
            }
        }
        SourcePatchTool tool = new SourcePatchTool(repository, module());
        tool.generatePatches();
        for (String family : new String[]{"spring", "boot"}) {
            assertThat(module().resolve("src/main/java/" + family + "2/example/Original.upstream.patch"))
                    .doesNotExist();
            for (int version = 3; version <= 4; version++) {
                Path directory = module().resolve("src/main/java/" + family + version + "/example");
                String patch = Files.readString(directory.resolve("Original.upstream.patch"));
                assertThat(patch).contains("-class Original { int version = " + (version - 1) + "; }\n");
                assertThat(patch).contains("+class Original { int version = " + version + "; }\n");
                assertThat(directory.resolve("Original.patch")).doesNotExist();
            }
        }
        tool.check(versions);
        Path patch = module().resolve("src/main/java/boot3/example/Original.upstream.patch");
        String saved = Files.readString(patch);
        Path changed = module().resolve("src/main/java/boot2/example/Original.java");
        Files.writeString(changed, SOURCE);
        assertThatThrownBy(() -> tool.check(versions)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Patch is out of date: " + patch.toRealPath());
        assertThat(Files.readString(patch)).isEqualTo(saved);
        assertThat(Files.readString(changed)).isEqualTo(SOURCE);
    }

    @Test
    void validatesEmptyAndMissingUpstreamVersionPatches() throws Exception {
        fixture(SOURCE, TARGET);
        for (String group : new String[]{"boot2", "boot3"}) {
            Files.writeString(records().resolveSibling(group + ".properties"), HEADER + "example.Original=?\n");
            Path source = module().resolve("src/main/java/" + group + "/example/Original.java");
            Files.createDirectories(source.getParent());
            Files.writeString(source, SOURCE);
        }
        SourcePatchTool tool = new SourcePatchTool(repository, module());
        tool.generatePatches();
        Path patch = module().resolve("src/main/java/boot3/example/Original.upstream.patch");
        assertThat(Files.readString(patch)).isEmpty();
        Map<String, String> versions = Map.of("sample", "1.0", "boot2", "1.0", "boot3", "1.0");
        tool.check(versions);
        Files.delete(patch);
        assertThatThrownBy(() -> tool.check(versions)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("generate the missing patch");
        assertThat(patch).doesNotExist();
    }

    @Test
    void allowsUpWithAnUnfinishedLocalClassButRequiresItForPatchAndCheck() throws Exception {
        fixture(SOURCE, TARGET);
        Files.writeString(records(), HEADER + "example.Original=\n");
        SourcePatchTool tool = new SourcePatchTool(repository, module());
        tool.extract(Map.of("sample", List.of(sourceJar(SOURCE))));
        assertThatThrownBy(tool::generatePatches).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Missing local class for example.Original");
        assertThatThrownBy(() -> tool.check(Map.of("sample", "1.0"))).isInstanceOf(IllegalStateException.class);
        assertThat(patch()).doesNotExist();
        tool.recordVersions(Map.of("sample", "1.1"));
        assertThat(Files.readString(records())).startsWith("version=1.1\nmoudle=core\n");
        Files.writeString(records(), HEADER + "example.Original=../../Outside.java\n");
        assertThatThrownBy(() -> new SourcePatchTool(repository, module()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void copiesClassesMarkedWithQuestionMarkAndPreservesThemAcrossVersionUpdates() throws Exception {
        fixture("old", TARGET);
        Files.writeString(records(), HEADER + "# example.Original=enhanced.Enhanced\n  example.Original = ?  \n");
        SourcePatchTool tool = new SourcePatchTool(repository, module());
        tool.extract(Map.of("sample", List.of(sourceJar(SOURCE))));
        assertThat(Files.readString(upstream())).isEqualTo(SOURCE);
        tool.generatePatches();
        assertThat(patch()).doesNotExist();
        tool.check(Map.of("sample", "1.0"));
        tool.recordVersions(Map.of("sample", "1.1"));
        assertThat(Files.readString(records())).isEqualTo("version=1.1\nmoudle=core\nexample.Original=?  \n");
        tool = new SourcePatchTool(repository, module());
        tool.generatePatches();
        tool.check(Map.of("sample", "1.1"));
        assertThat(patch()).doesNotExist();
        Files.delete(upstream());
        SourcePatchTool missing = tool;
        assertThatThrownBy(() -> missing.check(Map.of("sample", "1.1"))).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Missing file");
    }

    @Test
    void generatesPatchesOnlyForMappedClassesInMixedRecords() throws Exception {
        fixture(SOURCE, TARGET);
        Files.writeString(records(), HEADER + MAPPING + "example.Reference=?\n");
        Path reference = upstream().resolveSibling("Reference.java");
        Files.writeString(reference, "package example;\nclass Reference {}\n");
        SourcePatchTool tool = new SourcePatchTool(repository, module());
        tool.generatePatches();
        assertThat(patch()).isRegularFile();
        assertThat(reference.resolveSibling("Reference.patch")).doesNotExist();
        tool.check(Map.of("sample", "1.0"));
        tool.recordVersions(Map.of("sample", "1.1"));
        assertThat(Files.readString(records()))
                .isEqualTo("version=1.1\nmoudle=core\n" + MAPPING + "example.Reference=?\n");
    }

    @Test
    void requiresLocalClassForEntriesWithoutEquals() throws Exception {
        fixture(SOURCE, TARGET);
        Files.writeString(records(), HEADER + "example.Original\n");
        SourcePatchTool tool = new SourcePatchTool(repository, module());
        assertThatThrownBy(tool::generatePatches).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Missing local class for example.Original");
        assertThatThrownBy(() -> tool.check(Map.of("sample", "1.0"))).isInstanceOf(IllegalStateException.class);
        assertThat(patch()).doesNotExist();
    }

    @Test
    void maintainsCoreSourcesAndPatchesInTheirSourceSetDirectory() throws Exception {
        fixture(SOURCE, TARGET);
        Files.move(records(), records().resolveSibling("core.properties"));
        Files.delete(upstream());
        SourcePatchTool tool = new SourcePatchTool(repository, module());
        tool.extract(Map.of("core", List.of(sourceJar(SOURCE))));
        Path coreSource = module().resolve("src/main/java/core/example/Original.java");
        Path corePatch = coreSource.resolveSibling("Original.patch");
        assertThat(Files.readString(coreSource)).isEqualTo(SOURCE);
        assertThat(upstream()).doesNotExist();
        tool.generatePatches();
        assertThat(corePatch).isRegularFile();
        assertThat(patch()).doesNotExist();
        tool.check(Map.of("core", "1.0"));
        tool.recordVersions(Map.of("core", "1.1"));
        assertThat(Files.readString(records().resolveSibling("core.properties")))
                .startsWith("version=1.1\nmoudle=core\n");
    }

    @Test
    void mapsBootClassesToMultipleModulesWithOneSharedRecord() throws Exception {
        fixture(SOURCE, TARGET);
        Map<String, List<Path>> jars = new LinkedHashMap<>();
        jars.put("sample", List.of(sourceJar(SOURCE)));
        Map<String, String> versions = new LinkedHashMap<>();
        versions.put("sample", "1.0");
        for (int version = 2; version <= 4; version++) {
            String group = "boot" + version;
            String module = "test" + version;
            Path enhanced = repository.resolve(module + "/src/main/java/enhanced/Enhanced.java");
            Files.createDirectories(enhanced.getParent());
            Files.writeString(enhanced, TARGET);
            Files.writeString(
                    records().resolveSibling(group + ".properties"), HEADER + "example.Original=" + module
                            + ":enhanced.Enhanced\nexample.Reference=enhanced.Enhanced\n");
            jars.put(
                    group,
                    List.of(
                            sourceJar(
                                    group + "-main.jar", "example/Reference.java",
                                    SOURCE.replace("Original", "Reference")),
                            sourceJar(
                                    group + ".jar",
                                    SOURCE.replace("Original {}", "Original { int version = " + version + "; }"))));
            versions.put(group, "1.0");
        }
        SourcePatchTool tool = new SourcePatchTool(repository, module());
        tool.extract(jars);
        tool.generatePatches();
        tool.check(versions);
        for (int version = 2; version <= 4; version++) {
            Path directory = module().resolve("src/main/java/boot" + version + "/example");
            assertThat(directory.resolve("Original.java")).isRegularFile();
            assertThat(Files.readString(directory.resolve("Original.patch")))
                    .contains("+++ b/test" + version + "/src/main/java/enhanced/Enhanced.java");
            assertThat(Files.exists(directory.resolve("Original.upstream.patch"))).isEqualTo(version > 2);
            assertThat(Files.readString(directory.resolve("Reference.patch")))
                    .contains("+++ b/core/src/main/java/enhanced/Enhanced.java");
        }
    }

    @Test
    void rejectsDuplicatePhysicalSourcesAcrossGroups() throws Exception {
        fixture(SOURCE, TARGET);
        Files.writeString(records().resolveSibling("duplicate.properties"), HEADER + MAPPING);
        assertThatThrownBy(() -> new SourcePatchTool(repository, module())).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Duplicate upstream source mapping");
        assertThat(Files.readString(records())).startsWith("version=1.0\n");
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
        return sourceJar(name, "example/Original.java", source);
    }

    private Path sourceJar(String name, String entry, String source) throws Exception {
        Path jar = repository.resolve(name);
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar))) {
            output.putNextEntry(new JarEntry(entry));
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
