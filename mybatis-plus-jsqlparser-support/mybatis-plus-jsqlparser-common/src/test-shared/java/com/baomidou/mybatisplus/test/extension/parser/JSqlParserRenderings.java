package com.baomidou.mybatisplus.test.extension.parser;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;

import java.net.URL;
import java.security.CodeSource;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 四个 JSqlParser 版本模块（4.9/5.0/5.2/5.4）共用同一份测试源，个别语句的解析能力与关键字渲染不同。<br>
 * 期望值按版本显式登记在下表，<b>不拿解析器自己的输出当期望</b>：渲染随版本变化时，用解析器输出拼出来的期望会跟着变， 等于没有钉住 MP 拦截器的行为。版本从 classpath 上的
 * {@code jsqlparser-x.y.jar} 识别，未登记的版本直接构建失败， 逼着补一份画像；画像与当前依赖的实际行为是否吻合，由 {@code JSqlParserRenderingsTest} 在每个版本模块里逐项校验。
 */
public final class JSqlParserRenderings {

    private record Profile(String unionAll, boolean forUpdateBeforeOrderBy, boolean supportsDropProcedure,
            boolean supportsOracleHintedUpdate, boolean supportsFulltextIndexDdl,
            boolean parsesTextWithTrailingStatements) {
    }

    /**
     * 版本 → 该版本下所有断言的期望分支。新增版本必须同时补这里和 {@code JSqlParserRenderingsTest} 的探针语句。
     */
    private static final Map<String, Profile> PROFILES = Map.of(
            "4.9", new Profile("UNION ALL", true, false, false, false, false), "5.0",
            new Profile("UNION ALL", false, false, false, false, true), "5.2",
            new Profile("UNION ALL", false, false, false, true, true), "5.4",
            new Profile("UNION all", false, true, true, true, true));

    private static final Pattern JAR_VERSION = Pattern.compile("jsqlparser-(\\d+\\.\\d+)");

    private static final String CURRENT_VERSION = detectVersion();

    private static final Profile CURRENT = PROFILES.get(CURRENT_VERSION);

    static {
        if (CURRENT == null) {
            throw new IllegalStateException("未登记的 JSqlParser 版本 " + CURRENT_VERSION
                    + "，请在 JSqlParserRenderings.PROFILES 补一份版本画像，并用 JSqlParserRenderingsTest 校验");
        }
    }

    /**
     * 5.4 起保留输入里 {@code union all} 的小写 {@code all}，旧版本统一渲染成 {@code UNION ALL}。
     */
    public static final String UNION_ALL = CURRENT.unionAll();

    /**
     * 4.9 把 {@code FOR UPDATE} 渲染在 {@code ORDER BY} 之前，5.x 起渲染在语句末尾。
     */
    public static final boolean FOR_UPDATE_BEFORE_ORDER_BY = CURRENT.forUpdateBeforeOrderBy();

    /**
     * 5.4 起才能解析 {@code DROP PROCEDURE}，旧版本解析失败，拦截器按原文返回。
     */
    public static final boolean SUPPORTS_DROP_PROCEDURE = CURRENT.supportsDropProcedure();

    /**
     * 5.4 起才能解析带 Oracle hint 与 {@code INTERVAL ... DAY TO SECOND} 的 UPDATE，旧版本解析失败走原文替换。
     */
    public static final boolean SUPPORTS_ORACLE_HINTED_UPDATE = CURRENT.supportsOracleHintedUpdate();

    /**
     * 5.2 起才能解析 {@code FULLTEXT INDEX} DDL，旧版本解析失败走原文替换（保留输入里的无空格写法）。
     */
    public static final boolean SUPPORTS_FULLTEXT_INDEX_DDL = CURRENT.supportsFulltextIndexDdl();

    /**
     * 5.0 起能解析分号续接第二条语句的文本（只取第一条渲染），4.9 解析失败走原文替换。
     */
    public static final boolean PARSES_TEXT_WITH_TRAILING_STATEMENTS = CURRENT.parsesTextWithTrailingStatements();

    static String currentVersion() {
        return CURRENT_VERSION;
    }

    static Set<String> registeredVersions() {
        return PROFILES.keySet();
    }

    private JSqlParserRenderings() {
    }

    private static String detectVersion() {
        CodeSource codeSource = CCJSqlParserUtil.class.getProtectionDomain().getCodeSource();
        URL location = codeSource == null ? null : codeSource.getLocation();
        Matcher matcher = JAR_VERSION.matcher(String.valueOf(location));
        if (!matcher.find()) {
            throw new IllegalStateException("无法从 classpath 识别 JSqlParser 版本: " + location);
        }
        return matcher.group(1);
    }
}
