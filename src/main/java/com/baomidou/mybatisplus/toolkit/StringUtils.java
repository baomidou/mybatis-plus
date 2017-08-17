/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.toolkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * String 工具类
 * </p>
 *
 * @author D.Yang
 * @Date 2016-08-18
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
    public static final char UNDERLINE = '_';

    /**
     * 占位符
     */
    public static final String PLACE_HOLDER = "{%s}";

    private static boolean separatorBeforeDigit = false;
    private static boolean separatorAfterDigit = true;


    private StringUtils() {
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
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
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
     * 字符串驼峰转下划线格式
     * </p>
     *
     * @param param 需要转换的字符串
     * @return 转换好的字符串
     */
    public static String camelToUnderline(String param) {
        if (isEmpty(param)) {
            return EMPTY;
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(UNDERLINE);
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
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
        for (int i = 0; i < len; i++) {
            char c = temp.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(temp.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
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
        StringBuilder sb = new StringBuilder(param.length());
        sb.append(param.substring(0, 1).toLowerCase());
        sb.append(param.substring(1));
        return sb.toString();
    }

    /**
     * <p>
     * 判断字符串是否为纯大写字母
     * </p>
     *
     * @param str 要匹配的字符串
     * @return
     */
    public static boolean isUpperCase(String str) {
        return match("^[A-Z]+$", str);
    }

    /**
     * <p>
     * 正则表达式匹配
     * </p>
     *
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    public static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * <p>
     * SQL 参数填充
     * </p>
     *
     * @param content 填充内容
     * @param args    填充参数
     * @return
     */
    public static String sqlArgsFill(String content, Object... args) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        if (args != null) {
            int length = args.length;
            if (length >= 1) {
                for (int i = 0; i < length; i++) {
                    content = content.replace(String.format(PLACE_HOLDER, i), sqlParam(args[i]));
                }
            }
        }
        return content;
    }

    /**
     * <p>
     * 获取SQL PARAMS字符串
     * </p>
     *
     * @param obj
     * @return
     */
    public static String sqlParam(Object obj) {
        String repStr;
        if (obj instanceof Collection) {
            repStr = StringUtils.quotaMarkList((Collection<?>) obj);
        } else {
            repStr = StringUtils.quotaMark(obj);
        }
        return repStr;
    }

    /**
     * <p>
     * 使用单引号包含字符串
     * </p>
     *
     * @param obj 原字符串
     * @return 单引号包含的原字符串
     */
    public static String quotaMark(Object obj) {
        String srcStr = String.valueOf(obj);
        if (obj instanceof CharSequence) {
            // fix #79
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
    public static String quotaMarkList(Collection<?> coll) {
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("(");
        int _size = coll.size();
        int i = 0;
        Iterator<?> iterator = coll.iterator();
        while (iterator.hasNext()) {
            String tempVal = StringUtils.quotaMark(iterator.next());
            sqlBuild.append(tempVal);
            if (i + 1 < _size) {
                sqlBuild.append(",");
            }
            i++;
        }
        sqlBuild.append(")");
        return sqlBuild.toString();
    }

    /**
     * <p>
     * 拼接字符串第二个字符串第一个字母大写
     * </p>
     *
     * @param concatStr
     * @param str
     * @return
     */
    public static String concatCapitalize(String concatStr, final String str) {
        if (isEmpty(concatStr)) {
            concatStr = EMPTY;
        }
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        final char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }

        StringBuilder sb = new StringBuilder(strLen);
        sb.append(concatStr);
        sb.append(Character.toTitleCase(firstChar));
        sb.append(str.substring(1));
        return sb.toString();
    }

    /**
     * <p>
     * 字符串第一个字母大写
     * </p>
     *
     * @param str
     * @return
     */
    public static String capitalize(final String str) {
        return concatCapitalize(null, str);
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
     * @see java.lang.String#endsWith(String)
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
     * @see java.lang.String#endsWith(String)
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
     * @see java.lang.String#endsWith(String)
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
        return strings.toArray(new String[strings.size()]);
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
     * @param cls
     * @return
     */
    public static Boolean isCharSequence(Class<?> cls) {
        return cls != null && CharSequence.class.isAssignableFrom(cls);
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
    public static Boolean isBoolean(Class<?> propertyCls) {
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
        boolean _betweenUpperCases = false;
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
            if ((lastOneIsNotUnderscore) && ((isUpperCaseAndPreviousIsLowerCase) || (previousIsWhitespace) || ((_betweenUpperCases)
                    && (containsLowerCase) && (isUpperCaseAndPreviousIsUpperCase)))) {
                buf.append("_");
            } else if (((separatorAfterDigit) && (Character.isDigit(previousChar))
                    && (Character.isLetter(c))) || ((separatorBeforeDigit) && (Character
                    .isDigit(c)) && (Character.isLetter(previousChar)))) {
                buf.append('_');
            }
            if ((shouldReplace(c)) && (lastOneIsNotUnderscore)) {
                buf.append('_');
            } else if ((!Character.isWhitespace(c)) && ((isNotUnderscore) || (lastOneIsNotUnderscore))) {
                buf.append(Character.toUpperCase(c));
            }
            previousChar = c;
        }
        if (Character.isWhitespace(previousChar)) {
            buf.append("_");
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
                buf.append("-");
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
            buf.append("-");
        }
        return buf.toString();
    }
}
