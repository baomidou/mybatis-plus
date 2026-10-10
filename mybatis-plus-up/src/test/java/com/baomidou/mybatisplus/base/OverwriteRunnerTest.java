package com.baomidou.mybatisplus.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OverwriteRunnerTest {

    @TempDir
    Path directory;

    @Test
    void renamesClassAfterApplyingSteps() throws Exception {
        Original file = new Original();
        file.setOverwriteClass("example.target.Renamed$Type");
        file.addStep(i -> i.source("public Original() {}")
                .target("public Original() { System.out.println(Original.class); }")
                .operate(Overwrite.Operate.COVERAGE));
        String result = rewrite(file, """
                package example.source;
                public class Original {
                  public Original() {}
                  Original copy() { return new Original(); }
                  Class<?> type = Original.class;
                  Object OriginalFactory, Original$Helper;
                  String name = "Original";
                  // Original remains in comments.
                  /* Original remains here too. */
                }
                """);
        assertEquals("""
                package example.target;
                public class Renamed$Type {
                  public Renamed$Type() { System.out.println(Renamed$Type.class); }
                  Renamed$Type copy() { return new Renamed$Type(); }
                  Class<?> type = Renamed$Type.class;
                  Object OriginalFactory, Original$Helper;
                  String name = "Original";
                  // Original remains in comments.
                  /* Original remains here too. */
                }
                """, result);
    }

    @Test
    void relocatesPackageWhenSimpleNameIsUnchanged() throws Exception {
        Original file = new Original();
        file.setOverwriteClass("example.target.Original");
        assertEquals(
                "package example.target;\r\npublic class Original { public Original() {} }\r\n",
                rewrite(file, "package example.source;\r\npublic class Original { public Original() {} }\r\n"));
    }

    private String rewrite(OverwriteFile file, String source) throws Exception {
        Path jar = directory.resolve("sources.jar");
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar))) {
            output.putNextEntry(new JarEntry("example/source/Original.java"));
            output.write(source.getBytes(StandardCharsets.UTF_8));
            output.closeEntry();
        }
        Path module = directory.resolve("module");
        String baseModule =
                Path.of(System.getProperty("user.dir")).relativize(module).toString();
        new OverwriteRunner(baseModule, jar, List.of(file)).run();
        Path target = module.resolve("src/main/java/" + file.getOverwriteClass().replace('.', '/') + ".java");
        assertTrue(Files.exists(target));
        return Files.readString(target);
    }

    private static class Original extends OverwriteFile {}
}
