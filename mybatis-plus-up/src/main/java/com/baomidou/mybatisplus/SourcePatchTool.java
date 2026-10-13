package com.baomidou.mybatisplus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;

/** Maintains formatted upstream copies, enhancement patches and upstream version patches. */
public final class SourcePatchTool {
    private final Path repository;
    private final Path module;
    private final Map<Path, Properties> recordFiles = new LinkedHashMap<>();
    private final Map<String, Properties> records = new LinkedHashMap<>();
    private final List<Source> sources = new ArrayList<>();
    private final List<Mapping> mappings = new ArrayList<>();

    public SourcePatchTool(Path repository, Path module) throws IOException {
        this.repository = repository.toRealPath();
        this.module = module.toRealPath();
        if (!this.module.startsWith(this.repository)) {
            throw new IllegalArgumentException("The upgrade module must be inside the repository");
        }
        Path recordsDirectory = resolve(this.module, "upstream");
        if (!Files.isDirectory(recordsDirectory)) {
            throw new IllegalArgumentException("Missing upstream record directory: " + recordsDirectory);
        }
        try (var files = Files.list(recordsDirectory)) {
            for (Path candidate : files.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".properties"))
                    .sorted()
                    .toList()) {
                Path recordFile =
                        resolve(recordsDirectory, candidate.getFileName().toString());
                Properties fragment = new Properties();
                try (var reader = Files.newBufferedReader(recordFile, StandardCharsets.UTF_8)) {
                    fragment.load(reader);
                }
                String artifact = recordId(recordFile);
                if (!artifact.matches("[a-zA-Z0-9_-]+")
                        || fragment.getProperty("version", "").isBlank()
                        || fragment.getProperty("moudle", "").isBlank()) {
                    throw new IllegalArgumentException("Expected version and moudle in " + recordFile);
                }
                records.put(artifact, fragment);
                recordFiles.put(recordFile, fragment);
                Path localModule = resolveModule(fragment.getProperty("moudle").strip());
                Path sourceRoot = this.module.resolve("src/main/java");
                if (artifact.equals("core") || artifact.matches("(?:boot|spring)[0-9]+")) {
                    sourceRoot = sourceRoot.resolve(artifact);
                }
                for (String sourceClass :
                        fragment.stringPropertyNames().stream().sorted().toList()) {
                    if (sourceClass.equals("version") || sourceClass.equals("moudle")) {
                        continue;
                    }
                    String source = classPath(sourceClass);
                    String localClass = fragment.getProperty(sourceClass).strip();
                    Path upstream = resolve(sourceRoot, source);
                    if (sources.stream().anyMatch(s -> s.upstream().equals(upstream))) {
                        throw new IllegalArgumentException("Duplicate upstream source mapping: " + sourceClass);
                    }
                    sources.add(new Source(artifact, source, upstream));
                    if (localClass.equals("?")) {
                        continue;
                    }
                    Path enhanced = localClass.isEmpty()
                            ? null
                            : resolve(localModule.resolve("src/main/java"), classPath(localClass));
                    if (enhanced != null
                            && mappings.stream()
                                    .anyMatch(m -> artifact.equals(m.artifact()) && enhanced.equals(m.enhanced()))) {
                        throw new IllegalArgumentException("Duplicate enhanced source mapping: " + localClass);
                    }
                    String target = enhanced == null
                            ? null
                            : this.repository.relativize(enhanced).toString().replace('\\', '/');
                    Path patch = upstream.resolveSibling(
                            upstream.getFileName().toString().replaceFirst("\\.java$", ".patch"));
                    mappings.add(new Mapping(artifact, sourceClass, target, upstream, enhanced, patch));
                }
            }
        }
        if (sources.isEmpty()) {
            throw new IllegalArgumentException("No source mappings in " + recordsDirectory);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            throw new IllegalArgumentException("Expected action, repository directory and upgrade module directory");
        }
        SourcePatchTool tool = new SourcePatchTool(Path.of(args[1]), Path.of(args[2]));
        Map<String, String> versions = new LinkedHashMap<>();
        Map<String, Path> jars = new LinkedHashMap<>();
        int width = args[0].equals("extract") ? 3 : 2;
        if ((args.length - 3) % width != 0) {
            throw new IllegalArgumentException("Invalid upstream artifact arguments");
        }
        for (int i = 3; i < args.length; i += width) {
            versions.put(args[i], args[i + 1]);
            if (width == 3) {
                jars.put(args[i], Path.of(args[i + 2]));
            }
        }
        switch (args[0]) {
            case "extract" -> tool.extract(jars);
            case "record" -> tool.recordVersions(versions);
            case "patch" -> tool.generatePatches();
            case "check" -> tool.check(versions);
            default -> throw new IllegalArgumentException("Unknown source patch action: " + args[0]);
        }
    }

    public void extract(Map<String, Path> jars) throws IOException {
        Map<Path, String> sources = new LinkedHashMap<>();
        // Read every entry before replacing any tracked baseline.
        for (Source source : this.sources) {
            Path jar = jars.get(source.artifact());
            if (jar == null) {
                throw new IllegalArgumentException("Missing sources jar for " + source.artifact());
            }
            try (JarFile archive = new JarFile(jar.toFile())) {
                var entry = archive.getJarEntry(source.entry());
                if (entry == null) {
                    throw new IllegalArgumentException("Missing upstream source " + source.entry() + " in " + jar);
                }
                try (var input = archive.getInputStream(entry)) {
                    sources.put(source.upstream(), normalize(new String(input.readAllBytes(), StandardCharsets.UTF_8)));
                }
            }
        }
        for (var source : sources.entrySet()) {
            write(source.getKey(), source.getValue());
        }
        System.out.println("Extracted " + sources.size() + " upstream sources with their original packages");
    }

    public void recordVersions(Map<String, String> versions) throws IOException {
        for (Source source : sources) {
            String version = versions.get(source.artifact());
            if (version == null || version.isBlank()) {
                throw new IllegalArgumentException("Missing dependency version for " + source.artifact());
            }
            requireFile(source.upstream(), "Run :mybatis-plus-up:up to extract upstream sources");
        }
        for (var record : recordFiles.entrySet()) {
            Properties fragment = record.getValue();
            String version = versions.get(recordId(record.getKey()));
            if (version == null || version.isBlank()) {
                throw new IllegalArgumentException("Missing dependency version for " + record.getKey());
            }
            fragment.setProperty("version", version);
            StringBuilder text = new StringBuilder("version=")
                    .append(version)
                    .append("\nmoudle=")
                    .append(fragment.getProperty("moudle"))
                    .append('\n');
            for (String key : fragment.stringPropertyNames().stream().sorted().toList()) {
                if (!key.equals("version") && !key.equals("moudle")) {
                    text.append(key)
                            .append('=')
                            .append(fragment.getProperty(key))
                            .append('\n');
                }
            }
            write(record.getKey(), text.toString());
        }
        System.out.println("Recorded upstream versions: " + versions);
    }

    public void generatePatches() throws IOException, InterruptedException {
        requireLocalMappings();
        Map<Path, String> patches = new LinkedHashMap<>();
        for (Comparison comparison : comparisons()) {
            patches.put(comparison.patch(), diff(comparison));
        }
        for (var patch : patches.entrySet()) {
            write(patch.getKey(), patch.getValue());
        }
        System.out.println("Generated " + patches.size() + " patches alongside upstream sources in "
                + module.resolve("src/main/java"));
    }

    public void check(Map<String, String> versions) throws IOException, InterruptedException {
        for (Source source : sources) {
            String actual = versions.get(source.artifact());
            String recorded = records.get(source.artifact()).getProperty("version");
            if (!recorded.equals(actual)) {
                throw new IllegalStateException(source.artifact() + " version mismatch: recorded " + recorded
                        + ", dependency " + actual + ". Run :mybatis-plus-up:up and review the upstream changes.");
            }
            requireFile(source.upstream(), "Run :mybatis-plus-up:up to extract upstream sources");
        }
        requireLocalMappings();
        List<Comparison> comparisons = comparisons();
        for (Comparison comparison : comparisons) {
            requireFile(comparison.patch(), "Run :mybatis-plus-up:patch to generate the missing patch");
            String patch = read(comparison.patch());
            if (!patch.equals(diff(comparison))) {
                throw new IllegalStateException("Patch is out of date: " + comparison.patch()
                        + ". Review the two Java sources, then run :mybatis-plus-up:patch.");
            }
            verifyPatch(comparison, patch);
        }
        System.out.println("Checked dependency versions and " + comparisons.size() + " source/patch comparisons");
    }

    private List<Comparison> comparisons() {
        List<Comparison> comparisons = new ArrayList<>();
        for (Mapping mapping : mappings) {
            comparisons.add(new Comparison(mapping.target(), mapping.upstream(), mapping.enhanced(), mapping.patch()));
        }
        for (String family : List.of("spring", "boot")) {
            for (int version = 3; version <= 4; version++) {
                String previous = family + (version - 1);
                String current = family + version;
                for (Source source : sources) {
                    if (!source.artifact().equals(current)) {
                        continue;
                    }
                    Source before = sources.stream()
                            .filter(s ->
                                    s.artifact().equals(previous) && s.entry().equals(source.entry()))
                            .findFirst()
                            .orElse(null);
                    if (before != null) {
                        String target = repository
                                .relativize(source.upstream())
                                .toString()
                                .replace('\\', '/');
                        Path patch = source.upstream()
                                .resolveSibling(source.upstream()
                                        .getFileName()
                                        .toString()
                                        .replaceFirst("\\.java$", ".upstream.patch"));
                        comparisons.add(new Comparison(target, before.upstream(), source.upstream(), patch));
                    }
                }
            }
        }
        return comparisons;
    }

    private String diff(Comparison comparison) throws IOException, InterruptedException {
        requireFile(comparison.before(), "Run :mybatis-plus-up:up to extract upstream sources");
        requireFile(
                comparison.after(), "Check the target source path in upstream/*.properties or run :mybatis-plus-up:up");
        Path work = workspace("diff-");
        try {
            write(work.resolve("before.java"), read(comparison.before()));
            write(work.resolve("after.java"), read(comparison.after()));
            String output = git(
                    work,
                    List.of(
                            "diff",
                            "--no-index",
                            "--no-renames",
                            "--text",
                            "--no-color",
                            "--no-ext-diff",
                            "--no-textconv",
                            "--full-index",
                            "--diff-algorithm=myers",
                            "--no-indent-heuristic",
                            "--unified=3",
                            "--src-prefix=a/",
                            "--dst-prefix=b/",
                            "--",
                            "before.java",
                            "after.java"),
                    true);
            if (output.isEmpty()) {
                return "";
            }
            StringBuilder patch = new StringBuilder();
            boolean header = true;
            for (String line : output.lines().toList()) {
                if (line.startsWith("@@")) {
                    header = false;
                }
                if (header && line.startsWith("diff --git ")) {
                    line = "diff --git a/" + comparison.target() + " b/" + comparison.target();
                } else if (header && line.startsWith("--- ")) {
                    line = "--- a/" + comparison.target();
                } else if (header && line.startsWith("+++ ")) {
                    line = "+++ b/" + comparison.target();
                }
                patch.append(line).append('\n');
            }
            return patch.toString();
        } finally {
            clean(work);
        }
    }

    private void verifyPatch(Comparison comparison, String patch) throws IOException, InterruptedException {
        Path work = workspace("apply-");
        try {
            Path target = resolve(work, comparison.target());
            write(target, read(comparison.before()));
            if (!patch.isEmpty()) {
                write(work.resolve("source.patch"), patch);
                git(work, List.of("apply", "--whitespace=nowarn", "source.patch"), false);
            }
            if (!read(target).equals(read(comparison.after()))) {
                throw new IllegalStateException("Patch does not reproduce target source: " + comparison.after());
            }
        } finally {
            clean(work);
        }
    }

    private static String git(Path work, List<String> arguments, boolean diff)
            throws IOException, InterruptedException {
        List<String> command =
                new ArrayList<>(List.of("git", "-c", "core.autocrlf=false", "-c", "core.quotePath=false"));
        command.addAll(arguments);
        ProcessBuilder builder = new ProcessBuilder(command).directory(work.toFile());
        // Keep patch checks outside the enclosing repository's index and working tree.
        builder.environment().remove("GIT_DIR");
        builder.environment().remove("GIT_WORK_TREE");
        builder.environment().put("GIT_CEILING_DIRECTORIES", work.getParent().toString());
        Path errors = work.resolve("git-errors.txt");
        builder.redirectError(errors.toFile());
        Process process;
        try {
            process = builder.start();
        } catch (IOException e) {
            throw new IOException("Git must be installed and available on PATH for source patch tasks", e);
        }
        String output;
        try (var stdout = process.getInputStream()) {
            output = normalize(new String(stdout.readAllBytes(), StandardCharsets.UTF_8));
        }
        int status = process.waitFor();
        if (status != 0 && !(diff && status == 1)) {
            throw new IOException("Git " + arguments.getFirst() + " failed (" + status + "): " + read(errors));
        }
        return output;
    }

    private Path workspace(String prefix) throws IOException {
        Path parent = resolve(module, "build/source-patches");
        Files.createDirectories(parent);
        return Files.createTempDirectory(parent, prefix);
    }

    private static String recordId(Path path) {
        String name = path.getFileName().toString();
        return name.substring(0, name.length() - ".properties".length());
    }

    private static String classPath(String name) {
        if (!name.matches("[A-Za-z_$][\\w$]*(\\.[A-Za-z_$][\\w$]*)+")) {
            throw new IllegalArgumentException("Expected a fully qualified Java class name: " + name);
        }
        return name.replace('.', '/') + ".java";
    }

    private Path resolveModule(String name) throws IOException {
        String relative = name.replace(':', '/').replaceFirst("^/", "");
        Path direct = resolve(repository, relative);
        if (Files.isDirectory(direct)) {
            return direct;
        }
        if (!relative.contains("/")) {
            try (var paths = Files.find(
                    repository,
                    2,
                    (path, attributes) -> attributes.isDirectory()
                            && path.getFileName().toString().equals(relative)
                            && (Files.isRegularFile(path.resolve("build.gradle"))
                                    || Files.isRegularFile(path.resolve("build.gradle.kts"))))) {
                List<Path> matches = paths.limit(2).toList();
                if (matches.size() == 1) {
                    return matches.getFirst();
                }
            }
        }
        throw new IllegalArgumentException("Cannot uniquely resolve local moudle: " + name);
    }

    private void requireLocalMappings() {
        for (Mapping mapping : mappings) {
            if (mapping.enhanced() == null) {
                throw new IllegalStateException(
                        "Missing local class for " + mapping.source() + "; complete its entry in upstream/"
                                + mapping.artifact() + ".properties before patch or check.");
            }
        }
    }

    private void clean(Path work) throws IOException {
        if (!work.toAbsolutePath().normalize().startsWith(module.resolve("build/source-patches"))) {
            throw new IllegalArgumentException("Temporary directory is outside the upgrade workspace: " + work);
        }
        try (var paths = Files.walk(work)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    private Path resolve(Path root, String relative) throws IOException {
        Path name = Path.of(relative);
        Path path = root.resolve(name).toAbsolutePath().normalize();
        if (name.isAbsolute()
                || !path.startsWith(root.toAbsolutePath().normalize())
                || relative.contains("\\")
                || relative.contains("\n")
                || relative.contains("\r")) {
            throw new IllegalArgumentException("Expected a contained relative path with forward slashes: " + relative);
        }
        Path ancestor = path;
        while (!Files.exists(ancestor)) {
            ancestor = ancestor.getParent();
        }
        if (!ancestor.toRealPath().startsWith(repository)) {
            throw new IllegalArgumentException("Source path escapes through a symbolic link: " + relative);
        }
        return path;
    }

    private static void requireFile(Path path, String hint) {
        if (!Files.isRegularFile(path)) {
            throw new IllegalStateException("Missing file: " + path + ". " + hint);
        }
    }

    private static String read(Path path) throws IOException {
        return normalize(Files.readString(path, StandardCharsets.UTF_8));
    }

    private static String normalize(String source) {
        return source.replace("\r\n", "\n").replace('\r', '\n');
    }

    private static void write(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }

    private record Mapping(String artifact, String source, String target, Path upstream, Path enhanced, Path patch) {}

    private record Source(String artifact, String entry, Path upstream) {}

    private record Comparison(String target, Path before, Path after, Path patch) {}
}
