/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.toolkit.sql.StringEscape;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * <p>
 * String 工具类
 * </p>
 *
 * @author D.Yang
 * @since 2016-08-18
 */
public class StringUtils {

    /**
     * 空字符
     */
    public static final String EMPTY = "";

    /**
     * 字符串 is
     */
    public static final String IS = "is";

    /**
     * 下划线字符
     */
    private static final char UNDERLINE = '_';

    /**
     * 验证字符串是否是数据库字段
     */
    private static final Pattern P_IS_COLUMN = Pattern.compile("^\\w\\S*[\\w\\d]*$");

    private StringUtils() {
    }

    /**
     * <p>
     * Blob 转为 String 格式
     * </p>
     *
     * @param blob Blob 对象
     * @return
     */
    public static String blob2String(Blob blob) {
        if (null != blob) {
            try {
                byte[] returnValue = blob.getBytes(1, (int) blob.length());
                return new String(returnValue, StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw ExceptionUtils.mpe("Blob Convert To String Error!");
            }
        }
        return null;
    }

    /**
     * 判断字符串是否存在长度
     *
     * @param cs 字符序列
     * @return 如果为 null 或者长度为 0 ，返回 true
     */
    public static boolean hasLength(CharSequence cs) {
        return null == cs || 0 == cs.length();
    }

    /**
     * <p>
     * 判断字符串是否为空
     * </p>
     *
     * @param cs 需要判断字符串
     * @return 判断结果
     */
    public static boolean isEmpty(final CharSequence cs) {
        return hasLength(cs) || cs.chars().allMatch(Character::isWhitespace);
    }

    /**
     * <p>
     * 判断字符串是否不为空
     * </p>
     *
     * @param cs 需要判断字符串
     * @return 判断结果
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * <p>
     * 判断字符串是否符合数据库字段的命名
     * </p>
     *
     * @param str 字符串
     * @return 判断结果
     */
    public static boolean isNotColumnName(String str) {
        return !P_IS_COLUMN.matcher(str).matches();
    }

    /**
     * <p>
     * 字符串驼峰转下划线格式
     * </p>
     *
     * @param param 需要转换的字符串
     * @return 转换好的字符串
     */
    public static String camelToUnderline(String param) {
        if (hasLength(param)) {
            char[] chars = param.toCharArray();
            StringBuilder sb = new StringBuilder();
            sb.append(Character.toLowerCase(chars[0]));
            char c;
            for (int i = 1, len = chars.length; i < len; i++) {
                c = chars[i];
                sb.append(Character.isUpperCase(c) ? "_" + Character.toLowerCase(c) : c);
            }
            return sb.toString();
        }
        return param;
    }

    /**
     * <p>
     * 字符串下划线转驼峰格式
     * </p>
     *
     * @param param 需要转换的字符串
     * @return 转换好的字符串
     */
    public static String underlineToCamel(String param) {
        if (isEmpty(param)) {
            return EMPTY;
        }
        String temp = param.toLowerCase();
        int len = temp.length();
        StringBuilder sb = new StringBuilder(len);
        // 标记下一个字符是否需要大写
        boolean upper = false;
        for (char c : temp.toCharArray()) {
            if (c == UNDERLINE) {
                upper = true;
            } else {
                sb.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }
        return sb.toString();
    }

    /**
     * <p>
     * 解析 getMethodName -> propertyName
     * </p>
     *
     * @param getMethodName 需要解析的
     * @return 返回解析后的字段名称
     */
    public static String resolveFieldName(String getMethodName) {
        if (getMethodName.startsWith("get")) {
            getMethodName = getMethodName.substring(3);
        } else if (getMethodName.startsWith(IS)) {
            getMethodName = getMethodName.substring(2);
        }
        // 小写第一个字母
        return StringUtils.firstToLowerCase(getMethodName);
    }

    /**
     * <p>
     * 首字母转换小写
     * </p>
     *
     * @param param 需要转换的字符串
     * @return 转换好的字符串
     */
    public static String firstToLowerCase(String param) {
        if (isEmpty(param)) {
            return EMPTY;
        }
        return param.substring(0, 1).toLowerCase() + param.substring(1);
    }

    /**
     * <p>
     * 正则表达式匹配
     * </p>
     *
     * @param regex 正则表达式字符串
     * @param input 要匹配的字符串
     * @return 如果 input 符合 regex 正则表达式格式, 返回true, 否则返回 false;
     */
    public static boolean matches(String regex, String input) {
        if (null == regex || null == input) {
            return false;
        }
        return Pattern.matches(regex, input);
    }

    /**
     * SQL 参数占位符 预编译的正则表达式，匹配配型 {1} , {10} ...
     * 因为字符串中储存的是索引，所以可以在匹配的 Matcher 使用 m.group("idx") 获取匹配的结果 1，2，3，4，5。。。
     */
    private static final Pattern SQL_ARG_PLACE_HOLDER_PATTERN = Pattern.compile("\\{(?<idx>[\\d]+)}");

    /**
     * <p>
     * SQL 参数填充，未填充的 SQL 语句的样子类似：
     * SELECT * FROM TEST WHERE id = {1} AND NAME = {2}
     * </p>
     *
     * @param outerSql 不含有参数的 SQL 语句
     * @param args     填充参数列表
     * @return 返回填充后的字符串
     */
    public static String sqlArgsFill(String outerSql, Object... args) {
        if (StringUtils.isEmpty(outerSql) || null == args) {
            return outerSql;
        }
        return replace(outerSql, SQL_ARG_PLACE_HOLDER_PATTERN, m -> sqlParam(args[Integer.valueOf(m.group("idx"))])).toString();
    }

    /**
     * <p>
     * 获取SQL PARAMS字符串
     * </p>
     *
     * @param obj 参数对象
     * @return 返回处理后的 SQL 参数字符串
     */
    private static String sqlParam(Object obj) {
        if (null == obj) {
            return "null";
        }
        if (obj instanceof Collection) {
            return StringUtils.quotaMarkList((Collection<?>) obj);
        } else if (obj.getClass().isArray()) {
            return arrayToSqlString(obj);
        }
        return StringUtils.quotaMark(obj);
    }

    /**
     * 数组转转为 SQL 语句中的字符串形式
     *
     * @param obj 数组对象
     * @return 返回处理后的字符串
     */
    private static String arrayToSqlString(Object obj) {
        return Stream.iterate(0, i -> i + 1).limit(Array.getLength(obj)).map(StringUtils::quotaMark)
            .collect(sqlJoinCollector());
    }

    /**
     * 适用于 SQL 的结果收集器，这里不能写成常量，因为收集器不是可复用的，必须产生新的实例
     *
     * @return 返回结果收集器
     */
    private static Collector<CharSequence, ?, String> sqlJoinCollector() {
        return joining(StringPool.COMMA, StringPool.LEFT_BRACKET, StringPool.RIGHT_BRACKET);
    }

    /**
     * <p>
     * 使用单引号包含字符串
     * </p>
     *
     * @param obj 原字符串
     * @return 单引号包含的原字符串
     */
    private static String quotaMark(Object obj) {
        String srcStr = String.valueOf(obj);
        if (obj instanceof CharSequence) {
            return StringEscape.escapeString(srcStr);
        }
        return srcStr;
    }

    /**
     * <p>
     * 使用单引号包含字符串
     * </p>
     *
     * @param coll 集合
     * @return 单引号包含的原字符串的集合形式
     */
    private static String quotaMarkList(Collection<?> coll) {
        return coll.stream().map(StringUtils::quotaMark).collect(sqlJoinCollector());
    }

    /**
     * 替换字符序列中满足条件的部分，参数为 匹配的 matcher，返回值需要是字符序列
     * 所有的参数都不能为 null，否则会抛出 NPE 错误，这是来自 JAVA 的错误！
     *
     * @param origin   原字符串
     * @param pattern  用于匹配的正则表达式
     * @param replacer 替换函数
     * @return 返回 StringBuilder
     * @see #replace(CharSequence, Pattern, BiFunction)
     */
    public static StringBuilder replace(CharSequence origin, Pattern pattern, Function<Matcher, CharSequence> replacer) {
        return replace(origin, pattern, (m, i) -> replacer.apply(m));
    }

    /**
     * 替换字符序列中满足条件的部分，参数为 匹配的 matcher，和匹配的索引序号，序号从 0 开始
     * 返回值需要是字符序列
     *
     * @param origin   原字符串
     * @param pattern  用于匹配的正则表达式
     * @param replacer 替换函数
     * @return 返回 StringBuilder
     */
    public static StringBuilder replace(CharSequence origin, Pattern pattern, BiFunction<Matcher, Integer, CharSequence> replacer) {
        StringBuilder builder = new StringBuilder();
        Matcher matcher = pattern.matcher(origin);
        int last = 0, idx = 0;
        while (matcher.find()) {
            builder.append(origin, last, matcher.start()).append(replacer.apply(matcher, idx++));
            last = matcher.end();
        }
        // 如果匹配没有达到结尾，需要追加后面的字符序列;如果没有匹配到任何字符串，该判断同样用于保证字符的完整性
        int length = origin.length();
        if (last < length) {
            builder.append(origin, last, length);
        }
        return builder;
    }

    /**
     * <p>
     * 拼接字符串,第二个字符串第一个字母大写
     * </p>
     * example : concatCapitalize("is", "male") 返回 isMale
     *
     * @param first 第一个字符串
     * @param str   第二个字符串
     * @return 返回拼接后的字符串
     */
    public static String concatCapitalize(String first, String str) {
        return first + Character.toTitleCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * <p>
     * 判断对象是否为空
     * </p>
     *
     * @param object
     * @return
     */
    public static boolean checkValNotNull(Object object) {
        if (object instanceof CharSequence) {
            return isNotEmpty((CharSequence) object);
        }
        return object != null;
    }

    /**
     * <p>
     * 判断对象是否为空
     * </p>
     *
     * @param object
     * @return
     */
    public static boolean checkValNull(Object object) {
        return !checkValNotNull(object);
    }

    /**
     * <p>
     * 包含大写字母
     * </p>
     *
     * @param word 待判断字符串
     * @return
     */
    public static boolean containsUpperCase(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     * 是否为大写命名
     * </p>
     *
     * @param word 待判断字符串
     * @return
     */
    public static boolean isCapitalMode(String word) {
        return null != word && word.matches("^[0-9A-Z/_]+$");
    }

    /**
     * <p>
     * 是否为驼峰下划线混合命名
     * </p>
     *
     * @param word 待判断字符串
     * @return
     */
    public static boolean isMixedMode(String word) {
        return matches(".*[A-Z]+.*", word) && matches(".*[/_]+.*", word);
    }

    /**
     * <p>
     * Check if a String ends with a specified suffix.
     * </p>
     * <p>
     * <p>
     * <code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case sensitive.
     * </p>
     * <p>
     * <pre>
     * StringUtils.endsWith(null, null)      = true
     * StringUtils.endsWith(null, "abcdef")  = false
     * StringUtils.endsWith("def", null)     = false
     * StringUtils.endsWith("def", "abcdef") = true
     * StringUtils.endsWith("def", "ABCDEF") = false
     * </pre>
     *
     * @param str    the String to check, may be null
     * @param suffix the suffix to find, may be null
     * @return <code>true</code> if the String ends with the suffix, case
     * sensitive, or both <code>null</code>
     * @see String#endsWith(String)
     * @since 2.4
     */
    public static boolean endsWith(String str, String suffix) {
        return endsWith(str, suffix, false);
    }

    /**
     * <p>
     * Case insensitive check if a String ends with a specified suffix.
     * </p>
     * <p>
     * <p>
     * <code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case
     * insensitive.
     * </p>
     * <p>
     * <pre>
     * StringUtils.endsWithIgnoreCase(null, null)      = true
     * StringUtils.endsWithIgnoreCase(null, "abcdef")  = false
     * StringUtils.endsWithIgnoreCase("def", null)     = false
     * StringUtils.endsWithIgnoreCase("def", "abcdef") = true
     * StringUtils.endsWithIgnoreCase("def", "ABCDEF") = false
     * </pre>
     *
     * @param str    the String to check, may be null
     * @param suffix the suffix to find, may be null
     * @return <code>true</code> if the String ends with the suffix, case
     * insensitive, or both <code>null</code>
     * @see String#endsWith(String)
     * @since 2.4
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        return endsWith(str, suffix, true);
    }

    /**
     * <p>
     * Check if a String ends with a specified suffix (optionally case
     * insensitive).
     * </p>
     *
     * @param str        the String to check, may be null
     * @param suffix     the suffix to find, may be null
     * @param ignoreCase inidicates whether the compare should ignore case (case
     *                   insensitive) or not.
     * @return <code>true</code> if the String starts with the prefix or both
     * <code>null</code>
     * @see String#endsWith(String)
     */
    private static boolean endsWith(String str, String suffix, boolean ignoreCase) {
        if (str == null || suffix == null) {
            return (str == null && suffix == null);
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        int strOffset = str.length() - suffix.length();
        return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
    }

    /**
     * <p>
     * Splits the provided text into an array, separators specified. This is an
     * alternative to using StringTokenizer.
     * </p>
     * <p>
     * <p>
     * The separator is not included in the returned String array. Adjacent
     * separators are treated as one separator. For more control over the split
     * use the StrTokenizer class.
     * </p>
     * <p>
     * <p>
     * A {@code null} input String returns {@code null}. A {@code null}
     * separatorChars splits on whitespace.
     * </p>
     * <p>
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split("", *)           = []
     * StringUtils.split("abc def", null) = ["abc", "def"]
     * StringUtils.split("abc def", " ")  = ["abc", "def"]
     * StringUtils.split("abc  def", " ") = ["abc", "def"]
     * StringUtils.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str            the String to parse, may be null
     * @param separatorChars the characters used as the delimiters, {@code null} splits on
     *                       whitespace
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] split(final String str, final String separatorChars) {
        List<String> strings = splitWorker(str, separatorChars, -1, false);
        return strings.toArray(new String[0]);
    }

    /**
     * Performs the logic for the {@code split} and
     * {@code splitPreserveAllTokens} methods that return a maximum array
     * length.
     *
     * @param str               the String to parse, may be {@code null}
     * @param separatorChars    the separate character
     * @param max               the maximum number of elements to include in the array. A zero
     *                          or negative value implies no limit.
     * @param preserveAllTokens if {@code true}, adjacent separators are treated as empty
     *                          token separators; if {@code false}, adjacent separators are
     *                          treated as one separator.
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static List<String> splitWorker(final String str, final String separatorChars, final int max,
                                           final boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return Collections.emptyList();
        }
        final List<String> list = new ArrayList<>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            final char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i));
        }
        return list;
    }

    /**
     * <p>
     * 是否为CharSequence类型
     * </p>
     *
     * @param clazz class
     * @return true 为是 CharSequence 类型
     */
    public static boolean isCharSequence(Class<?> clazz) {
        return clazz != null && CharSequence.class.isAssignableFrom(clazz);
    }

    /**
     * <p>
     * 去除boolean类型is开头的字符串
     * </p>
     *
     * @param propertyName 字段名
     * @param propertyType 字段类型
     * @return
     */
    public static String removeIsPrefixIfBoolean(String propertyName, Class<?> propertyType) {
        if (isBoolean(propertyType) && propertyName.startsWith(IS)) {
            String property = propertyName.replaceFirst(IS, EMPTY);
            if (isEmpty(property)) {
                return propertyName;
            } else {
                String firstCharToLowerStr = firstCharToLower(property);
                return property.equals(firstCharToLowerStr) ? propertyName : firstCharToLowerStr;
            }
        }
        return propertyName;
    }

    /**
     * <p>
     * 是否为CharSequence类型
     * </p>
     *
     * @param propertyType
     * @return
     */
    public static Boolean isCharSequence(String propertyType) {
        try {
            return isCharSequence(Class.forName(propertyType));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * <p>
     * 是否为Boolean类型(包含普通类型)
     * </p>
     *
     * @param propertyCls
     * @return
     */
    public static boolean isBoolean(Class<?> propertyCls) {
        return propertyCls != null && (boolean.class.isAssignableFrom(propertyCls) || Boolean.class.isAssignableFrom(propertyCls));
    }

    /**
     * <p>
     * 第一个首字母小写,之后字符大小写的不变<br>
     * StringUtils.firstCharToLower( "UserService" )     = userService
     * StringUtils.firstCharToLower( "UserServiceImpl" ) = userServiceImpl
     * </p>
     *
     * @param rawString 需要处理的字符串
     * @return
     */
    public static String firstCharToLower(String rawString) {
        return prefixToLower(rawString, 1);
    }

    /**
     * <p>
     * 前n个首字母小写,之后字符大小写的不变
     * </p>
     *
     * @param rawString 需要处理的字符串
     * @param index     多少个字符(从左至右)
     * @return
     */
    public static String prefixToLower(String rawString, int index) {
        String beforeChar = rawString.substring(0, index).toLowerCase();
        String afterChar = rawString.substring(index, rawString.length());
        return beforeChar + afterChar;
    }

    /**
     * <p>
     * 删除字符前缀之后,首字母小写,之后字符大小写的不变<br>
     * StringUtils.removePrefixAfterPrefixToLower( "isUser", 2 )     = user
     * StringUtils.removePrefixAfterPrefixToLower( "isUserInfo", 2 ) = userInfo
     * </p>
     *
     * @param rawString 需要处理的字符串
     * @param index     删除多少个字符(从左至右)
     * @return
     */
    public static String removePrefixAfterPrefixToLower(String rawString, int index) {
        return prefixToLower(rawString.substring(index, rawString.length()), 1);
    }

    /**
     * <p>
     * 驼峰转连字符<br>
     * StringUtils.camelToHyphen( "managerAdminUserService" ) = manager-admin-user-service
     * </p>
     *
     * @param input
     * @return 以'-'分隔
     * @see <a href="https://github.com/krasa/StringManipulation">document</a>
     */
    public static String camelToHyphen(String input) {
        return wordsToHyphenCase(wordsAndHyphenAndCamelToConstantCase(input));
    }

    private static String wordsAndHyphenAndCamelToConstantCase(String input) {
        boolean betweenUpperCases = false;
        boolean containsLowerCase = containsLowerCase(input);

        StringBuilder buf = new StringBuilder();
        char previousChar = ' ';
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            boolean isUpperCaseAndPreviousIsUpperCase = (Character.isUpperCase(previousChar)) && (Character.isUpperCase(c));
            boolean isUpperCaseAndPreviousIsLowerCase = (Character.isLowerCase(previousChar)) && (Character.isUpperCase(c));

            boolean previousIsWhitespace = Character.isWhitespace(previousChar);
            boolean lastOneIsNotUnderscore = (buf.length() > 0) && (buf.charAt(buf.length() - 1) != '_');
            boolean isNotUnderscore = c != '_';
            if (lastOneIsNotUnderscore && (isUpperCaseAndPreviousIsLowerCase || previousIsWhitespace
                || (betweenUpperCases && containsLowerCase && isUpperCaseAndPreviousIsUpperCase))) {
                buf.append(StringPool.UNDERSCORE);
            } else if ((Character.isDigit(previousChar) && Character.isLetter(c))) {
                buf.append('_');
            }
            if ((shouldReplace(c)) && (lastOneIsNotUnderscore)) {
                buf.append('_');
            } else if (!Character.isWhitespace(c) && (isNotUnderscore || lastOneIsNotUnderscore)) {
                buf.append(Character.toUpperCase(c));
            }
            previousChar = c;
        }
        if (Character.isWhitespace(previousChar)) {
            buf.append(StringPool.UNDERSCORE);
        }
        return buf.toString();
    }

    public static boolean containsLowerCase(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldReplace(char c) {
        return (c == '.') || (c == '_') || (c == '-');
    }

    private static String wordsToHyphenCase(String s) {
        StringBuilder buf = new StringBuilder();
        char lastChar = 'a';
        for (char c : s.toCharArray()) {
            if ((Character.isWhitespace(lastChar)) && (!Character.isWhitespace(c))
                && ('-' != c) && (buf.length() > 0)
                && (buf.charAt(buf.length() - 1) != '-')) {
                buf.append(StringPool.DASH);
            }
            if ('_' == c) {
                buf.append('-');
            } else if ('.' == c) {
                buf.append('-');
            } else if (!Character.isWhitespace(c)) {
                buf.append(Character.toLowerCase(c));
            }
            lastChar = c;
        }
        if (Character.isWhitespace(lastChar)) {
            buf.append(StringPool.DASH);
        }
        return buf.toString();
    }

    /**
     * 从字符串中移除一个单词及随后的一个逗号
     *
     * @param s 原字符串
     * @param p 移除的单词
     * @return
     */
    public static String removeWordWithComma(String s, String p) {
        String match = "\\s*" + p + "\\s*,{0,1}";
        return s.replaceAll(match, "");
    }
}
