package com.baomidou.mybatisplus.base;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

final class InnerClassRemover {

    private InnerClassRemover() {}

    static String remove(String source, String className, String entryName) throws IOException {
        List<int[]> ranges = new ArrayList<>();
        JavaFileObject file =
                new SimpleJavaFileObject(URI.create("string:///Source.java"), JavaFileObject.Kind.SOURCE) {
                    @Override
                    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                        return source;
                    }
                };
        var compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("删除内部类需要使用 JDK 运行");
        }
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (var fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
            // 只解析源码，不做类型分析，不需要加载上游依赖。
            JavacTask task = (JavacTask)
                    compiler.getTask(null, fileManager, diagnostics, List.of("-proc:none"), null, List.of(file));
            var positions = Trees.instance(task).getSourcePositions();
            DocTrees docs = DocTrees.instance(task);
            var units = task.parse();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                    throw new IllegalArgumentException(entryName + " - 无法解析源码以删除内部类: " + diagnostic.getMessage(null));
                }
            }
            for (CompilationUnitTree unit : units) {
                new TreePathScanner<Void, Void>() {
                    @Override
                    public Void visitClass(ClassTree tree, Void unused) {
                        String name = tree.getSimpleName().toString();
                        if (getCurrentPath().getParentPath().getLeaf() instanceof ClassTree && className.equals(name)) {
                            int start = (int) positions.getStartPosition(unit, tree);
                            int end = (int) positions.getEndPosition(unit, tree);
                            if (docs.getDocCommentTree(getCurrentPath()) != null) {
                                start = source.lastIndexOf("/**", start);
                            }
                            ranges.add(declarationRange(source, start, end));
                        }
                        return super.visitClass(tree, unused);
                    }
                }.scan(unit, null);
            }
        }
        if (ranges.isEmpty()) {
            throw new IllegalArgumentException(entryName + " - 无法定位内部类: " + className);
        }
        ranges.sort(Comparator.comparingInt(range -> range[0]));
        StringBuilder result = new StringBuilder();
        int cursor = 0;
        for (int[] range : ranges) {
            // 删除父类时，其内部类的范围已经包含在父类内。
            if (range[0] >= cursor) {
                result.append(source, cursor, range[0]);
                cursor = range[1];
            }
        }
        return result.append(source, cursor, source.length()).toString();
    }

    private static int[] declarationRange(String source, int start, int end) {
        int lineStart = Math.max(source.lastIndexOf('\n', start - 1), source.lastIndexOf('\r', start - 1)) + 1;
        boolean startsLine = source.substring(lineStart, start).isBlank();
        if (startsLine) {
            start = lineStart;
        }
        int lineEnd = end;
        while (lineEnd < source.length() && source.charAt(lineEnd) != '\n' && source.charAt(lineEnd) != '\r') {
            lineEnd++;
        }
        if (startsLine && source.substring(end, lineEnd).isBlank()) {
            end = lineEnd;
            if (end < source.length() && source.charAt(end) == '\r') {
                end++;
            }
            if (end < source.length() && source.charAt(end) == '\n') {
                end++;
            }
        }
        return new int[] {start, end};
    }
}
