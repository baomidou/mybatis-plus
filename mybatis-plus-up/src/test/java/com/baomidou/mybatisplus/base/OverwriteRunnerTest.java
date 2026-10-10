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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
                  Original.Inner nested;
                  Object instance = Original.factory();
                  Object OriginalFactory, Original$Helper;
                  String name = "Original";
                  // Original remains in comments.
                  /* Original remains here too. */
                }
                """);
        assertEquals("""
                package example.target;
                public class Renamed$Type {
                  public Renamed$Type() { System.out.println(Original.class); }
                  Renamed$Type copy() { return new Renamed$Type(); }
                  Class<?> type = Original.class;
                  Original.Inner nested;
                  Object instance = Original.factory();
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

    @Test
    void addsImportsWithoutDependingOnAnUpstreamImport() throws Exception {
        Original file = new Original();
        file.setOverwriteClass("example.target.Original");
        file.setImports("""
                import java.util.List;
                import static java.util.Collections.emptyList;
                """);
        assertEquals("""
                package example.target;

                import java.util.Map;
                import java.util.List;
                import static java.util.Collections.emptyList;

                public class Original {}
                """, rewrite(file, """
                package example.source;

                import java.util.Map;

                public class Original {}
                """));
    }

    @Test
    void addsImportsWhenSourceHasNoImportsAndPreservesLineEndings() throws Exception {
        Original file = new Original();
        file.setOverwriteClass("example.target.Original");
        file.setImports("import java.util.List;\nimport java.util.Map;\n");
        assertEquals(
                "package example.target;\r\nimport java.util.List;\r\nimport java.util.Map;\r\n\r\npublic class Original {}\r\n",
                rewrite(file, "package example.source;\r\n\r\npublic class Original {}\r\n"));
    }

    @Test
    void deletesCompleteMemberClassesIncludingDocumentationAndAnnotations() throws Exception {
        Original file = new Original();
        file.setOverwriteClass("example.target.Original");
        file.addStep(i -> i.source("Nested").operate(Overwrite.Operate.DELETE_INNER_CLASS));
        file.addStep(i -> i.source("Removed").operate(Overwrite.Operate.DELETE_INNER_CLASS));
        file.addStep(i -> i.source("Second").operate(Overwrite.Operate.DELETE_INNER_CLASS));
        file.addStep(i -> i.source("static class Keeper {}")
                .target("static class Keeper { int value; }")
                .operate(Overwrite.Operate.COVERAGE));
        String source = """
                package example.source;
                public class Original {
                  // class Removed { } is only a comment.
                  /** Documentation to remove. */
                  @SuppressWarnings({"unchecked", "rawtypes"})
                  protected static class Removed<T> {
                    String braces = "} {";
                    char brace = '}';
                    /* } misleading brace */
                    void method() { if (true) { System.out.println(braces); } }
                    static class Nested {}
                    String block = %s;
                  }
                  static class Second {}
                  /** Documentation to keep. */
                  static class Keeper {}
                  void method() { class Removed {} }
                }
                """.formatted("\"\"\"\nclass Removed { }\n}\n\"\"\"");
        assertEquals("""
                package example.target;
                public class Original {
                  // class Removed { } is only a comment.
                  /** Documentation to keep. */
                  static class Keeper { int value; }
                  void method() { class Removed {} }
                }
                """, rewrite(file, source));
    }

    @Test
    void deletesInlineInnerClassAndPreservesSurroundingSource() throws Exception {
        Original file = new Original();
        file.setOverwriteClass("example.target.Original");
        file.addStep(i -> i.source("Removed").operate(Overwrite.Operate.DELETE_INNER_CLASS));
        assertEquals(
                "package example.target;\r\npublic class Original {  int value; }\r\n",
                rewrite(file, "package example.source;\r\npublic class Original { class Removed {} int value; }\r\n"));
    }

    @Test
    void failsWhenConfiguredNameIsNotAMemberClass() {
        Original file = new Original();
        file.setOverwriteClass("example.target.Original");
        file.addStep(i -> i.source("Original").operate(Overwrite.Operate.DELETE_INNER_CLASS));
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> rewrite(file, "package example.source;\npublic class Original {}\n"));
        assertTrue(error.getMessage().contains("无法定位内部类: Original"));
        assertFalse(Files.exists(directory.resolve("module/src/main/java/example/target/Original.java")));
    }

    @Test
    void rejectsEmptyInnerClassName() {
        Original file = new Original();
        file.setOverwriteClass("example.target.Original");
        file.addStep(i -> i.source(" ").operate(Overwrite.Operate.DELETE_INNER_CLASS));
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> rewrite(file, "package example.source;\npublic class Original {}\n"));
        assertTrue(error.getMessage().contains("删除内部类的类名不能为空"));
    }

    @Test
    void replacesAllLiteralMatchesIncludingQualifiedNames() throws Exception {
        Original file = new Original();
        file.setOverwriteClass("example.target.Original");
        file.addStep(
                i -> i.source("Original.Inner").target("Replacement$Type.Inner").operate(Overwrite.Operate.REPLACE));
        assertEquals("""
                package example.target;
                public class Original {
                  Replacement$Type.Inner value;
                  Class<?> type = Replacement$Type.Inner.class;
                  Object OriginalXInner;
                }
                """, rewrite(file, """
                package example.source;
                public class Original {
                  Original.Inner value;
                  Class<?> type = Original.Inner.class;
                  Object OriginalXInner;
                }
                """));
    }

    @Test
    void replacesTextWithoutNormalizingWhitespaceOrReplacementCharacters() throws Exception {
        Original file = new Original();
        file.setOverwriteClass("example.target.Original");
        file.addStep(i -> i.source("  // old\r\n").target("\t// $1\\new\n").operate(Overwrite.Operate.REPLACE));
        assertEquals(
                "package example.target;\r\npublic class Original {\r\n\t// $1\\new\n}\r\n",
                rewrite(file, "package example.source;\r\npublic class Original {\r\n  // old\r\n}\r\n"));
    }

    @Test
    void keepsSourceWhenReplaceTextIsAbsent() throws Exception {
        Original file = new Original();
        file.setOverwriteClass("example.target.Original");
        file.addStep(i -> i.source("missing").target("").operate(Overwrite.Operate.REPLACE));
        assertEquals(
                "package example.target;\npublic class Original {}\n",
                rewrite(file, "package example.source;\npublic class Original {}\n"));
    }

    @Test
    void appendsMethodsAtClassEndInStepOrderAndPreservesTrailingComments() throws Exception {
        Original file = new Original();
        file.setOverwriteClass("example.target.Renamed");
        file.addStep(i -> i.source("""
                public Original copy() {
                    return new Original();
                }
                """).operate(Overwrite.Operate.APPEND_METHOD));
        file.addStep(i -> i.source("public int last() { return 1; }").operate(Overwrite.Operate.APPEND_METHOD));
        assertEquals("""
                package example.target;
                public class Renamed {
                  static class Nested { String brace = "}"; }

                    public Renamed copy() {
                        return new Renamed();
                    }

                    public int last() { return 1; }
                }
                // trailing }
                /* more trailing } */
                """, rewrite(file, """
                package example.source;
                public class Original {
                  static class Nested { String brace = "}"; }
                }
                // trailing }
                /* more trailing } */
                """));
    }

    @Test
    void appendsMethodToInlineEmptyClassAndPreservesCrLf() throws Exception {
        Original file = new Original();
        file.setOverwriteClass("example.target.Original");
        file.addStep(i -> i.source("""
                public int value() {
                    return 1;
                }
                """).operate(Overwrite.Operate.APPEND_METHOD));
        assertEquals(
                "package example.target;\r\npublic class Original {\r\n\r\n    public int value() {\r\n        return 1;\r\n    }\r\n}\r\n",
                rewrite(file, "package example.source;\r\npublic class Original {}\r\n"));
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
