/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.toolkit.sql;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * SQL 注入验证工具类
 *
 * @author hubin
 * @since 2021-08-15
 */
public class SqlInjectionUtils {

    /**
     * SQL语法检查正则：符合两个关键字（有先后顺序）才算匹配
     */
    private static final Pattern SQL_SYNTAX_PATTERN = Pattern.compile(
            "(insert|delete|update|select|create|drop|truncate|grant|alter|deny|revoke|call|execute|exec|declare|show|rename|set)"
                    + "\\s+.*(into|from|set|where|table|database|view|index|on|cursor|procedure|trigger|for|password|union|and|or)|(select\\s*\\*\\s*from\\s+)"
                    + "|\\bdatabase\\s*\\(\\s*\\)|if\\s*\\(.*\\)|select\\s*\\(.*\\)|substr\\s*\\(.*\\)|substring\\s*\\(.*\\)|char\\s*\\(.*\\)|concat\\s*\\(.*\\)|benchmark\\s*\\(.*\\)|sleep\\s*\\(.*\\)|(and|or)\\s+.*",
            Pattern.CASE_INSENSITIVE);
    /**
     * 使用'、;或注释截断SQL检查正则
     */
    private static final Pattern SQL_COMMENT_PATTERN = Pattern
            .compile("'.*(or|union|--|#|/\\*|;)", Pattern.CASE_INSENSITIVE);

    /** 常见的数据库信息读取、延时及报错函数。 */
    private static final Pattern SQL_DANGEROUS_FUNCTION_PATTERN = Pattern.compile(
            "\\b(?:pg_sleep|extractvalue|updatexml|load_file|current_setting)\\s*\\(", Pattern.CASE_INSENSITIVE);

    /** 无空格的逻辑运算，如 OR(1=1)。 */
    private static final Pattern SQL_LOGICAL_PATTERN = Pattern
            .compile("\\b(?:and|or)\\s*\\(", Pattern.CASE_INSENSITIVE);
    /**
     * 检查参数是否存在 SQL 注入。此方法仅用于识别部分可疑语法，不能保证输入安全。 对不可信的值应使用参数绑定，对列名等 SQL 结构应使用白名单。
     *
     * @param value 检查参数
     * @return true 存在可疑语法 false 未发现已知可疑语法
     */
    public static boolean check(String value) {
        Objects.requireNonNull(value);
        int scanResult = scan(value);
        if (scanResult != 0) {
            return scanResult > 0;
        }
        return (value.indexOf('\'') >= 0 && SQL_COMMENT_PATTERN.matcher(value).find())
                || SQL_SYNTAX_PATTERN.matcher(value).find()
                || (value.indexOf('(') >= 0 && (SQL_DANGEROUS_FUNCTION_PATTERN.matcher(value).find()
                        || SQL_LOGICAL_PATTERN.matcher(value).find()));
    }

    /**
     * 检测引号之外的语句分隔符及注释，并识别无需正则检查的简单标识符。
     *
     * @return 1 表示发现分隔符或注释，-1 表示简单标识符，0 表示需要正则检查
     */
    private static int scan(String value) {
        char quote = 0;
        boolean simpleIdentifier = true;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_')) {
                simpleIdentifier = false;
            }
            if (quote != 0) {
                if (c == '\\' && i + 1 < value.length()) {
                    i++;
                } else if (c == quote) {
                    if (i + 1 < value.length() && value.charAt(i + 1) == quote) {
                        i++;
                    } else {
                        quote = 0;
                    }
                }
            } else if (c == '\'' || c == '"' || c == '`') {
                quote = c;
            } else if (c == ';' || c == '#' || (i + 1 < value.length()
                    && ((c == '-' && value.charAt(i + 1) == '-') || (c == '/' && value.charAt(i + 1) == '*')))) {
                return 1;
            }
        }
        return simpleIdentifier ? -1 : 0;
    }

    /**
     * 刪除字段转义符单引号双引号
     *
     * @param text 待处理字段
     */
    public static String removeEscapeCharacter(String text) {
        Objects.requireNonNull(text);
        return text.replaceAll("\"", "").replaceAll("'", "");
    }
}
