/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * SQL 解析工具类
 *
 * @author hubin
 * @since 2018-07-22
 */
public class SqlParserUtils {

    /**
     * 获取 COUNT 原生 SQL 包装
     *
     * @param originalSql ignore
     * @return ignore
     */
    public static String getOriginalCountSql(String originalSql) {
        return String.format("SELECT COUNT(*) FROM (%s) TOTAL", originalSql);
    }

    /**
     * 去除表或字段包裹符号
     *
     * @param tableOrColumn 表名或字段名
     * @return str
     * @since 3.5.6
     */
    public static String removeWrapperSymbol(String tableOrColumn) {
        if (StringUtils.isBlank(tableOrColumn)) {
            return null;
        }
        if (tableOrColumn.startsWith("`") || tableOrColumn.startsWith("\"")
            || tableOrColumn.startsWith("[") || tableOrColumn.startsWith("<")) {
            return tableOrColumn.substring(1, tableOrColumn.length() - 1);
        }
        return tableOrColumn;
    }

}
