package com.baomidou.mybatisplus.test.convention;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试写法的机器闸门：纯文本扫描仓库内所有测试源文件，一条规范落成一个用例。<br>
 * 用例断言消息里的 T 编号就是规范条目号，规范文档按同一个编号逐条说明。<br>
 * 硬规则目前零违规，出现即红；存量太大的规则先钉住 2026-10-21 实测的数量，只准降不准升。<br>
 * 扫描不解析语法：注释与字面量先遮掉，按文件名认被测类、按大括号配对取用例方法体。
 *
 * @author taoyu
 */
class TestConventionTest {

    private static final Pattern CASE_ANNOTATION = Pattern
            .compile("@(?:Test|ParameterizedTest|RepeatedTest|TestFactory)\\b");

    /**
     * Java 的 assert 语句与 Kotlin 的 assert() 函数，两者都不产生失败（或要 -ea 才生效）。
     */
    private static final Pattern BARE_ASSERT = Pattern.compile("(?<![\\w.$])assert\\s*[({]");

    /**
     * AssertJ 的入口方法（{@code assertThat}、{@code assertThatCode}、{@code assertThatThrownBy} 等），<br>
     * 后面捕获的是括号还是大括号，决定去哪里找它的收尾。
     */
    private static final Pattern ASSERTJ_ENTRY = Pattern.compile("\\bassertThat[A-Za-z]*\\s*([({])");

    private static final Pattern DISABLED = Pattern.compile("@Disabled\\b");

    private static final Pattern DISABLED_WITH_REASON = Pattern.compile("@Disabled\\s*\\(\\s*\"\\s*\\S");

    private static final Pattern TAG = Pattern.compile("@Tag\\(\\s*\"([^\"]+)\"");

    private static final Pattern PACKAGE = Pattern.compile("^package\\s+([\\w.]+)", Pattern.MULTILINE);

    private static final Pattern JRE_GATE = Pattern.compile("@(?:Enabled|Disabled)(?:OnJre|ForJreRange)\\b");

    private static final Pattern JUNIT_FOUR = Pattern.compile("\\borg\\.junit\\.(Test|Before|After|Ignore|runner)\\b");

    /**
     * JUnit 自己的断言类，用于判断一个测试文件是否沾了 JUnit 断言。
     */
    private static final Pattern JUNIT_ASSERTIONS = Pattern.compile("\\borg\\.junit\\.jupiter\\.api\\.Assertions\\b");

    /**
     * 文件里实际调用到的 JUnit 断言方法名，{@code Assertions.xxx()} 这种带限定的写法。
     */
    private static final Pattern ASSERTION_CALL = Pattern.compile("Assertions\\.([A-Za-z]+)\\s*\\(");

    /**
     * {@code import static org.junit.jupiter.api.Assertions.assertEquals;}，静态导入后调用点没有前缀，只能读导入。
     */
    private static final Pattern ASSERTION_IMPORT = Pattern
            .compile("import\\s+static\\s+org\\.junit\\.jupiter\\.api\\.Assertions\\.(\\w+|\\*)\\s*;");

    /**
     * {@code import org.junit.jupiter.api.*;} 同样把 {@code Assertions} 请进了文件，<br>
     * 只按全限定类名找 JUnit 断言会漏掉这批通配导入的存量，欠账数就少算一截。
     */
    private static final Pattern JUPITER_WILDCARD_IMPORT = Pattern
            .compile("import\\s+org\\.junit\\.jupiter\\.api\\.\\*\\s*;");

    /**
     * 有 AssertJ 等价写法的 JUnit 断言，才算 T4 的欠账；{@code assertAll}、{@code assertTimeout} 系列<br>
     * AssertJ 没有对应物，留着是正确写法，不能拿它们凑转换数。
     */
    private static final Set<String> CONVERTIBLE_ASSERTIONS = Set.of(
            "assertEquals", "assertNotEquals", "assertTrue", "assertFalse", "assertNull", "assertNotNull", "assertSame",
            "assertNotSame", "assertArrayEquals", "assertThrows", "assertThrowsExactly", "assertDoesNotThrow",
            "assertInstanceOf", "fail");

    /**
     * 形如 {@code 名字(参数) [throws ...] {} 的方法声明；参数表里不允许出现分号和大括号。
     */
    private static final Pattern METHOD = Pattern.compile("(\\w+)\\s*\\([^;{}]*\\)(?:\\s+throws[\\w.,\\s]+)?\\s*\\{");

    /**
     * {@code if (x) &#123;} 这类语句长得和方法声明一样，按名字排除掉。
     */
    private static final Set<String> NON_METHOD_NAMES = Set.of(
            "if", "for", "while", "switch", "catch", "do", "else", "try", "synchronized", "return", "new", "class",
            "interface", "enum", "record");

    private static final Pattern SUPER = Pattern.compile("class\\s+[\\w.]+[^{;]*?\\bextends\\s+([\\w.]+)");

    private static final Path ROOT = locateRoot();

    private static final List<Source> TEST_SOURCES = scanTestSources();

    /**
     * 模块名 -> (main 里的简单类名 -> 它的包名)，用于判断测试包有没有镜像被测类。
     */
    private static final Map<String, Map<String, Set<String>>> MAIN = scanMainPackages();

    /**
     * T3 分层标签词表；仓库里目前还没有 {@code @Tag}，S2 开始打标签这条闸门才真正起作用。
     */
    private static final Set<String> TIERS = Set.of("unit", "integration", "external-db", "build-metadata", "stress");

    /**
     * T5 零断言用例数量（含 @Disabled 的用例，通过本文件或基类的辅助方法间接断言的不计入，2026-10-21 实测）。<br>
     * 剩下 7 个都有明确落点，补不了不是漏了：6 个挂在真实外部库上（达梦、Oracle、PostgreSQL、MySQL 与 ZooKeeper）， 1 个在 {@code postgresql} 包里被根
     * {@code build.gradle} 的 Test 任务整包排除、根本不执行。 前者等 external-db 分层，后者等执行方式改造，两者都需要改动主干构建脚本。
     */
    private static final int MAX_ASSERTION_FREE_CASES = 7;

    /**
     * T6 显式禁用用例数量。
     */
    private static final int MAX_DISABLED_CASES = 6;

    /**
     * T1 被测类在同模块但测试包没镜像的存量数量（S6 逐模块治理时下调）。<br>
     * 2026-10-21 归位 core 的两批：{@code test.toolkit} 12 个类迁到 {@code core.toolkit} 并合掉 {@code test.pom} 下重复的
     * {@code ReflectionKitTest}（66 → 53）；再把
     * {@code test/MybatisConfigurationTest}、{@code test/MybatisParameterHandlerTest}、
     * {@code test/metadata/TableInfoHelperTest}、{@code test/override/MybatisMapperMethodTest} 迁到被测类所在包（53 → 49）。
     */
    private static final int MAX_PACKAGE_MISMATCHES = 49;

    /**
     * T4 仍在调用可转换 JUnit 断言的测试文件数。2026-10-21 逐模块清完 core、extension、spring、
     * spring-boot-starter、jsqlparser-support、mybatis-plus、generator、up，<b>欠账已清零，本常量只能是 0</b>： 再调大等于允许有人往测试里塞回 JUnit
     * 断言。{@code assertAll}、{@code assertTimeout} 系列 AssertJ 没有对应物，不计入欠账。
     */
    private static final int MAX_JUNIT_ASSERTION_FILES = 0;

    /**
     * T2 历史遗留的同名测试类（复制出来的版本矩阵与 starter 副本），去重前不允许新增。
     */
    private static final Set<String> REGISTERED_DUPLICATE_NAMES = Set.of(
            "BlockAttackInnerInterceptorTest", "ChainWrappersTest", "DataChangeRecorderInnerInterceptorTest",
            "DataPermissionInterceptorTest", "DbTest", "DynamicTableNameInnerInterceptorTest",
            "DynamicTableNameJsqlParserInnerInterceptorTest", "FstFactoryTest", "FuryFactoryTest", "GeneratePomTest",
            "IllegalSQLInnerInterceptorTest", "JSqlParserTest", "JsqlParserSimpleSerialTest", "MetaObjectHandlerTest",
            "MetadataTest", "MultiDataPermissionInterceptorTest", "MybatisEnumTypeHandlerTest",
            "MybatisPlusInterceptorTest", "MybatisPlusPropertiesTest", "MybatisPlusSampleTest",
            "PaginationInnerInterceptorTest", "SelectBodyToPlainSelectTest", "SqlRunnerTest", "StringUtilsTest",
            "TableInfoTest", "TenantLineInnerInterceptorTest");

    /**
     * T5 用 Thread.sleep 当同步的文件。
     */
    private static final Set<String> REGISTERED_THREAD_SLEEP_FILES = Set
            .of("mybatis-plus/src/test/java/com/baomidou/mybatisplus/test/postgresql/PostgresqlTest.java");

    /**
     * @param path 相对仓库根的路径，统一用 /
     * @param code 遮掉注释的源码，字面量保留
     * @param blanks 注释与字面量都遮掉的版本
     */
    private record Source(String path, String code, String blanks) {
    }

    @Test
    void scanReallyFoundTheRepository() {
        assertThat(TEST_SOURCES).as("T0 扫不到测试源文件说明根目录定位失败，规范扫描不能假绿").hasSizeGreaterThanOrEqualTo(200);
        assertThat(MAIN).as("T0 找不到 src/main 就无从判断包镜像").isNotEmpty();
        List<String> legacyNames = TEST_SOURCES.stream().map(Source::path).filter(
                path -> path.endsWith("Tests.java") || path.endsWith("Tests.kt") || path.endsWith("TestCase.java")
                        || path.endsWith("TestCase.kt") || path.endsWith("IT.java") || path.endsWith("IT.kt"))
                .toList();
        assertThat(legacyNames).as("T2 测试类只用 Test 后缀，不再引入 *Tests/*TestCase/*IT").isEmpty();
    }

    @Test
    void noBareAssertKeyword() {
        List<String> offenders = new ArrayList<>();
        for (Source source : TEST_SOURCES) {
            if (BARE_ASSERT.matcher(source.blanks()).find()) {
                offenders.add(source.path());
            }
        }
        assertThat(offenders).as("T4 断言只用 AssertJ，Java 的 assert 关键字运行时根本不执行").isEmpty();
    }

    @Test
    void noPermanentSkipBehindJdkGates() {
        List<String> offenders = new ArrayList<>();
        for (Source source : TEST_SOURCES) {
            if (JRE_GATE.matcher(source.code()).find()) {
                offenders.add(source.path());
            }
        }
        assertThat(offenders).as("T9 按 JRE 版本门禁的写法在当前工具链上等于永久静默跳过").isEmpty();
    }

    @Test
    void noJUnitFourLeft() {
        List<String> offenders = new ArrayList<>();
        for (Source source : TEST_SOURCES) {
            if (JUNIT_FOUR.matcher(source.code()).find()) {
                offenders.add(source.path());
            }
        }
        assertThat(offenders).as("T4 JUnit 4 与 Jupiter 混用会让用例计数不可信").isEmpty();
    }

    @Test
    void junitAssertionFilesDoNotGrow() {
        List<String> offenders = new ArrayList<>();
        for (Source source : TEST_SOURCES) {
            if (junitAssertionsInScope(source.code()) && usesConvertibleAssertion(source.code())) {
                offenders.add(source.path());
            }
        }
        assertThat(offenders).as("T4 断言只用 AssertJ，这批存量只准减；转换一个就把常量调小一格，禁止调大")
                .hasSizeLessThanOrEqualTo(MAX_JUNIT_ASSERTION_FILES);
    }

    /**
     * {@code org.junit.jupiter.api.*} 通配导入把 {@code Assertions} 一起带进文件，<br>
     * 上一批欠账盘点就是这么漏掉这批文件的，所以通配导入也不许用。
     */
    @Test
    void noWildcardJupiterImport() {
        List<String> offenders = new ArrayList<>();
        for (Source source : TEST_SOURCES) {
            if (JUPITER_WILDCARD_IMPORT.matcher(source.code()).find()) {
                offenders.add(source.path());
            }
        }
        assertThat(offenders).as("T4 禁用 import org.junit.jupiter.api.*，它让 JUnit 断言在文件里看不出来源").isEmpty();
    }

    /**
     * 这个文件里的 {@code Assertions.assertEquals()} 到底是不是 JUnit 的：直接导入，或整个 jupiter api 包通配导入。
     */
    private static boolean junitAssertionsInScope(String code) {
        return JUNIT_ASSERTIONS.matcher(code).find() || JUPITER_WILDCARD_IMPORT.matcher(code).find();
    }

    /**
     * 该文件是否调用到有 AssertJ 等价物的 JUnit 断言。通配静态导入按最坏情况算——它把整套断言都请进了文件。
     */
    private static boolean usesConvertibleAssertion(String code) {
        Matcher imported = ASSERTION_IMPORT.matcher(code);
        while (imported.find()) {
            if ("*".equals(imported.group(1)) || CONVERTIBLE_ASSERTIONS.contains(imported.group(1))) {
                return true;
            }
        }
        Matcher called = ASSERTION_CALL.matcher(code);
        while (called.find()) {
            if (CONVERTIBLE_ASSERTIONS.contains(called.group(1))) {
                return true;
            }
        }
        return false;
    }

    @Test
    void everyDisabledCaseExplainsItself() {
        List<String> offenders = new ArrayList<>();
        int disabled = 0;
        for (Source source : TEST_SOURCES) {
            // 命中位置取自遮掉字面量的 blanks，本文件里的正则字面量才不会把自己扫成假命中；
            // blanks 与 code 逐字符等长，所以同一个下标回到 code 上就能读到原因文本。
            int found = 0;
            int explained = 0;
            Matcher hits = DISABLED.matcher(source.blanks());
            while (hits.find()) {
                found++;
                if (DISABLED_WITH_REASON.matcher(source.code().substring(hits.start())).lookingAt()) {
                    explained++;
                }
            }
            disabled += found;
            if (explained != found) {
                offenders.add(source.path());
            }
        }
        assertThat(offenders).as("T6 每个 @Disabled 都要写清依赖或原因").isEmpty();
        assertThat(disabled).as("T6 禁用用例数量只准降；放开或新增时请同步改这个常量").isEqualTo(MAX_DISABLED_CASES);
    }

    @Test
    void tierTagsComeFromTheVocabulary() {
        Set<String> unknown = new TreeSet<>();
        for (Source source : TEST_SOURCES) {
            Matcher matcher = TAG.matcher(source.code());
            while (matcher.find()) {
                if (!TIERS.contains(matcher.group(1))) {
                    unknown.add(matcher.group(1) + " @ " + source.path());
                }
            }
        }
        assertThat(unknown).as("T3 分层标签只能用登记的层名").isEmpty();
    }

    @Test
    void noCaseFreeTestFileRemains() {
        Set<String> found = new TreeSet<>();
        for (Source source : testClasses()) {
            if (!CASE_ANNOTATION.matcher(source.code()).find()) {
                found.add(source.path());
            }
        }
        assertThat(found).as("T5 名为 *Test 却不含用例的基类与夹具（零容忍）：改名 *Support/*Fixture/*Sample").isEmpty();
    }

    @Test
    void duplicatedTestNamesStayRegistered() {
        Map<String, List<String>> byName = new TreeMap<>();
        for (Source source : testClasses()) {
            if (!CASE_ANNOTATION.matcher(source.code()).find()) {
                continue;
            }
            byName.computeIfAbsent(fileName(source), key -> new ArrayList<>()).add(source.path());
        }
        Set<String> found = new TreeSet<>();
        byName.forEach((name, paths) -> {
            if (paths.size() > 1) {
                found.add(name);
            }
        });
        assertThat(found).as("T2 同名测试类是复制出来的，去重只准减不准加，登记表也要同步收窄").isEqualTo(REGISTERED_DUPLICATE_NAMES);
    }

    @Test
    void threadSleepStaysRegistered() {
        Set<String> found = new TreeSet<>();
        for (Source source : TEST_SOURCES) {
            if (source.blanks().contains("Thread.sleep")) {
                found.add(source.path());
            }
        }
        assertThat(found).as("T5 用 Thread.sleep 当同步的文件必须逐个登记").isEqualTo(REGISTERED_THREAD_SLEEP_FILES);
    }

    @Test
    void assertionFreeCasesDoNotGrow() {
        List<String> offenders = assertionFreeCases();
        assertThat(offenders).as("T5 零断言用例存量只准降不准升，下面是要补断言的位置").hasSizeLessThanOrEqualTo(MAX_ASSERTION_FREE_CASES);
    }

    /**
     * {@code assertThat(x)} 单独一句只是造了个断言对象，没有任何校验，永远绿。<br>
     * T5 的零断言扫描按方法体里有没有 assert 字样判，会被这种写法骗过去，所以单独一条规则盯收尾。
     */
    @Test
    void assertjEntriesMustFinishWithAnAssertion() {
        List<String> offenders = new ArrayList<>();
        for (Source source : TEST_SOURCES) {
            String blanks = source.blanks();
            Matcher matcher = ASSERTJ_ENTRY.matcher(blanks);
            while (matcher.find()) {
                char opener = matcher.group(1).charAt(0);
                int after = skipBalanced(blanks, matcher.end() - 1, opener, opener == '(' ? ')' : '}');
                int next = after;
                while (next < blanks.length() && Character.isWhitespace(blanks.charAt(next))) {
                    next++;
                }
                if (next >= blanks.length() || blanks.charAt(next) != '.') {
                    offenders.add(source.path() + ':' + lineOf(blanks, matcher.start()));
                }
            }
        }
        assertThat(offenders).as("T5 AssertJ 入口后面必须链一个断言方法，下面是光调用不断言的位置").isEmpty();
    }

    @Test
    void packageMirroringViolationsDoNotGrow() {
        List<String> offenders = new ArrayList<>();
        for (Source source : testClasses()) {
            String name = fileName(source);
            if (!name.endsWith("Test")) {
                continue;
            }
            Set<String> candidates = MAIN.getOrDefault(moduleOf(source.path()), Map.of())
                    .getOrDefault(name.substring(0, name.length() - "Test".length()), Set.of());
            if (candidates.isEmpty()) {
                // 被测类不在本模块（跨模块行为测试、夹具或改名），T1 先由人工判断
                continue;
            }
            String testPackage = packageOf(source.code());
            if (!candidates.contains(testPackage)) {
                offenders.add(source.path() + " 在 " + testPackage + "，被测类在 " + candidates);
            }
        }
        assertThat(offenders).as("T1 包镜像缺口存量只准降不准升，S6 逐模块收窄").hasSizeLessThanOrEqualTo(MAX_PACKAGE_MISMATCHES);
    }

    /**
     * 找出只有调用没有断言的用例，元素形如 {@code 路径#方法名}。<br>
     * 用例经常通过基类的辅助方法（如 {@code WrapperTestSupport#logSqlWhere}）间接断言，这种要认下来：<br>
     * 只按方法体里有没有 assert 字样判，存量会被假阳性填满，真实欠账反而看不见。
     */
    private static List<String> assertionFreeCases() {
        List<String> offenders = new ArrayList<>();
        for (Source source : testClasses()) {
            Set<String> helpers = assertingHelpers(source);
            Matcher matcher = CASE_ANNOTATION.matcher(source.code());
            while (matcher.find()) {
                int brace = bodyStart(source.blanks(), matcher.end());
                if (brace < 0) {
                    continue;
                }
                String body = bodyOf(source.blanks(), brace);
                if (body != null && !asserts(body, helpers)) {
                    offenders.add(source.path() + '#' + caseName(source.blanks(), brace));
                }
            }
        }
        return offenders;
    }

    /**
     * 本文件与其测试基类里"会断言"的辅助方法名：方法体自己出现 assert/verify/fail，或调用了同族里<br>
     * 已经判定会断言的方法，迭代到不再新增为止。
     */
    private static Set<String> assertingHelpers(Source source) {
        Set<String> helpers = new HashSet<>();
        Set<String> visited = new HashSet<>();
        for (Source current = source; current != null; current = superClass(current)) {
            if (!visited.add(current.path())) {
                break;
            }
            List<String> names = new ArrayList<>();
            List<String> bodies = new ArrayList<>();
            collectMethods(current.blanks(), names, bodies);
            boolean grown = true;
            while (grown) {
                grown = false;
                for (int i = 0; i < names.size(); i++) {
                    String name = names.get(i);
                    if (!helpers.contains(name) && asserts(bodies.get(i), helpers)) {
                        helpers.add(name);
                        grown = true;
                    }
                }
            }
        }
        return helpers;
    }

    /**
     * 逐个取出方法声明的名字与方法体。关键字和大括号是误匹配的来源：{@code if (x) &#123;} 长得和声明一样，<br>
     * 所以名字落在 {@link #NON_METHOD_NAMES} 里的直接跳过。
     */
    private static void collectMethods(String blanks, List<String> names, List<String> bodies) {
        Matcher matcher = METHOD.matcher(blanks);
        while (matcher.find()) {
            String name = matcher.group(1);
            if (NON_METHOD_NAMES.contains(name)) {
                continue;
            }
            String body = bodyOf(blanks, matcher.end() - 1);
            if (body != null) {
                names.add(name);
                bodies.add(body);
            }
        }
    }

    /**
     * 沿 {@code extends} 往上找测试基类，只按简单类名在本仓库的测试源文件里认，找不到就当没有基类。
     */
    private static Source superClass(Source source) {
        Matcher matcher = SUPER.matcher(source.code());
        if (!matcher.find()) {
            return null;
        }
        String name = matcher.group(1);
        name = name.substring(name.lastIndexOf('.') + 1);
        int generic = name.indexOf('<');
        name = generic < 0 ? name : name.substring(0, generic);
        for (Source candidate : TEST_SOURCES) {
            if (fileName(candidate).equals(name)) {
                return candidate;
            }
        }
        return null;
    }

    private static List<Source> testClasses() {
        return TEST_SOURCES.stream()
                .filter(source -> fileName(source).endsWith("Test") || fileName(source).endsWith("Tests")).toList();
    }

    private static String fileName(Source source) {
        String path = source.path();
        String name = path.substring(path.lastIndexOf('/') + 1);
        int dot = name.lastIndexOf('.');
        return dot < 0 ? name : name.substring(0, dot);
    }

    private static String moduleOf(String relativePath) {
        String[] segments = relativePath.split("/");
        int src = Arrays.asList(segments).indexOf("src");
        return src > 0 ? String.join("/", Arrays.copyOfRange(segments, 0, src)) : relativePath;
    }

    private static String packageOf(String code) {
        Matcher matcher = PACKAGE.matcher(code);
        return matcher.find() ? matcher.group(1) : "";
    }

    /**
     * 从注解位置往后找到方法体的起始大括号：注解的括号（如 {@code @CsvSource({"a","b"})}）里也有大括号， 必须整对跳过，否则会把参数表当成方法体。
     *
     * @return 起始大括号下标，找不到返回 -1
     */
    private static int bodyStart(String blanks, int from) {
        for (int i = from; i < blanks.length();) {
            char current = blanks.charAt(i);
            if (current == '(') {
                i = skipBalanced(blanks, i, '(', ')');
            } else if (current == '{') {
                return i;
            } else {
                i++;
            }
        }
        return -1;
    }

    private static int skipBalanced(String blanks, int from, char open, char close) {
        int depth = 0;
        for (int i = from; i < blanks.length(); i++) {
            char current = blanks.charAt(i);
            if (current == open) {
                depth++;
            } else if (current == close) {
                depth--;
                if (depth == 0) {
                    return i + 1;
                }
            }
        }
        return blanks.length();
    }

    /**
     * 遮 blanks 时保留了换行，所以数换行就能把下标还原成行号。
     */
    private static int lineOf(String blanks, int index) {
        int line = 1;
        for (int i = 0; i < index; i++) {
            if (blanks.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }

    /**
     * 大括号配对取出方法体，注释与字面量已被遮成空格，不会再有游离的括号。
     */
    private static String bodyOf(String blanks, int brace) {
        Deque<Character> stack = new ArrayDeque<>();
        for (int i = brace; i < blanks.length(); i++) {
            char current = blanks.charAt(i);
            if (current == '{') {
                stack.push(current);
            } else if (current == '}') {
                stack.pop();
                if (stack.isEmpty()) {
                    return blanks.substring(brace + 1, i);
                }
            }
        }
        return null;
    }

    /**
     * 方法体起始大括号往前推：参数表左括号之前的标识符就是用例方法名。
     */
    private static String caseName(String blanks, int brace) {
        int open = blanks.lastIndexOf('(', brace);
        if (open < 0) {
            return "?";
        }
        int end = open;
        while (end > 0 && Character.isWhitespace(blanks.charAt(end - 1))) {
            end--;
        }
        int start = end;
        while (start > 0 && Character.isJavaIdentifierPart(blanks.charAt(start - 1))) {
            start--;
        }
        return blanks.substring(start, end);
    }

    /**
     * 方法体里自己断言，或调用了判定为"会断言"的辅助方法，都算这个体有验证。
     */
    private static boolean asserts(String body, Set<String> assertingHelpers) {
        if (body.contains("assert") || body.contains("Assert") || body.contains("verify(") || body.contains("fail(")) {
            return true;
        }
        for (String helper : assertingHelpers) {
            if (Pattern.compile("\\b" + Pattern.quote(helper) + "\\s*\\(").matcher(body).find()) {
                return true;
            }
        }
        return false;
    }

    private static List<Source> scanTestSources() {
        List<Source> sources = new ArrayList<>();
        for (Path module : modules()) {
            Path test = module.resolve("src").resolve("test");
            if (!Files.isDirectory(test)) {
                continue;
            }
            for (Path file : sources(test)) {
                String raw = read(file);
                sources.add(
                        new Source(ROOT.relativize(file).toString().replace('\\', '/'), mask(raw, true),
                                mask(raw, false)));
            }
        }
        return sources;
    }

    private static Map<String, Map<String, Set<String>>> scanMainPackages() {
        Map<String, Map<String, Set<String>>> byModule = new TreeMap<>();
        for (Path module : modules()) {
            Path main = module.resolve("src").resolve("main");
            if (!Files.isDirectory(main)) {
                continue;
            }
            String moduleName = ROOT.relativize(module).toString().replace('\\', '/');
            Map<String, Set<String>> byClass = byModule.computeIfAbsent(moduleName, key -> new TreeMap<>());
            for (Path file : sources(main)) {
                String name = file.getFileName().toString();
                String simpleName = name.substring(0, name.lastIndexOf('.'));
                byClass.computeIfAbsent(simpleName, key -> new LinkedHashSet<>())
                        .add(packageOf(mask(read(file), true)));
            }
        }
        return byModule;
    }

    private static List<Path> modules() {
        List<Path> dirs = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(ROOT, 3)) {
            stream.filter(Files::isDirectory).filter(path -> !"build".equals(lastName(path)))
                    .filter(path -> Files.isDirectory(path.resolve("src"))).forEach(dirs::add);
        } catch (IOException e) {
            throw new UncheckedIOException("遍历 " + ROOT + " 失败", e);
        }
        return dirs;
    }

    private static List<Path> sources(Path root) {
        List<Path> files = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile).filter(path -> {
                String name = path.getFileName().toString();
                return name.endsWith(".java") || name.endsWith(".kt");
            }).sorted().forEach(files::add);
        } catch (IOException e) {
            throw new UncheckedIOException("遍历 " + root + " 失败", e);
        }
        return files;
    }

    private static String lastName(Path path) {
        Path name = path.getFileName();
        return name == null ? "" : name.toString();
    }

    private static String read(Path file) {
        try {
            return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 遮掉注释；keepLiterals 为 false 时把字符串与字符字面量也遮成空格，长度与换行位置不变。
     */
    private static String mask(String source, boolean keepLiterals) {
        StringBuilder out = new StringBuilder(source.length());
        int index = 0;
        while (index < source.length()) {
            char current = source.charAt(index);
            if (source.startsWith("//", index)) {
                int end = source.indexOf('\n', index);
                end = end < 0 ? source.length() : end;
                out.append(blanks(source, index, end));
                index = end;
            } else if (source.startsWith("/*", index)) {
                int end = source.indexOf("*/", index + 2);
                end = end < 0 ? source.length() : end + 2;
                out.append(blanks(source, index, end));
                index = end;
            } else if (!keepLiterals && (current == '"' || current == '\'')) {
                int end = literalEnd(source, index);
                out.append(blanks(source, index, end));
                index = end;
            } else {
                out.append(current);
                index++;
            }
        }
        return out.toString();
    }

    private static String blanks(String source, int from, int to) {
        StringBuilder blanks = new StringBuilder(to - from);
        for (int i = from; i < to; i++) {
            blanks.append(source.charAt(i) == '\n' ? '\n' : ' ');
        }
        return blanks.toString();
    }

    private static int literalEnd(String source, int start) {
        String quote = source.startsWith("\"\"\"", start) ? "\"\"\"" : source.substring(start, start + 1);
        int index = start + quote.length();
        while (index < source.length()) {
            char current = source.charAt(index);
            if (current == '\\') {
                index += 2;
                continue;
            }
            if (source.startsWith(quote, index)) {
                return index + quote.length();
            }
            if (quote.length() == 1 && current == '\n') {
                // 正常源码不会有跨行的普通字面量，遇到就停住，别让扫描器跑飞
                return index;
            }
            index++;
        }
        return source.length();
    }

    private static Path locateRoot() {
        Path start = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        for (Path current = start; current != null; current = current.getParent()) {
            if (Files.isRegularFile(current.resolve("settings.gradle"))) {
                return current;
            }
        }
        throw new AssertionError("从 " + start + " 往上找不到 settings.gradle，定位不到仓库根，规范扫描不能静默通过");
    }

}
