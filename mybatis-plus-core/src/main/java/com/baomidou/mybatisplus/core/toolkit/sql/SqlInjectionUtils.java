/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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
    // SQL语法检查正则：符合两个关键字（有先后顺序）才算匹配
    private static final Pattern sqlSyntaxPattern = Pattern.compile("(insert|delete|update|select|create|drop|truncate|grant|alter|deny|revoke|call|execute|exec|declare|show|rename|set)" +
        ".+(into|from|set|where|table|database|view|index|on|cursor|procedure|trigger|for|password|union|and|or)", Pattern.CASE_INSENSITIVE);
    // 使用'、;或注释截断SQL检查正则
    private static final Pattern sqlCommentPattern = Pattern.compile("'.*(or|union|--|#|/*|;)", Pattern.CASE_INSENSITIVE);

    /**
     * 检查参数是否存在 SQL 注入
     *
     * @param value 检查参数
     * @return true 非法 false 合法
     */
    public static boolean check(String value) {
        Objects.requireNonNull(value);
        // 处理是否包含SQL注释字符
        if (sqlCommentPattern.matcher(value).find()) {
            return true;
        }
        // 检查是否包含SQL注入敏感字符
        if (sqlSyntaxPattern.matcher(value).find()) {
            return true;
        }
        return false;
    }
}
