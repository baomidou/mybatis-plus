/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.core.parser.SqlInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;

/**
 * SQL 解析工具类
 *
 * @author hubin
 * @since 2018-07-22
 */
public class SqlParserUtils {
    private static ISqlParser COUNT_SQL_PARSER = null;

    /**
     * 获取 COUNT 原生 SQL 包装
     *
     * @param originalSql ignore
     * @return ignore
     */
    public static String getOriginalCountSql(String originalSql) {
        return String.format("SELECT COUNT(1) FROM ( %s ) TOTAL", originalSql);
    }

    /**
     * 获取CountOptimize
     *
     * @param optimizeCountSql 是否优化 Count SQL
     * @param sqlParser        Count SQL 解析类
     * @param originalSql      需要计算Count SQL
     * @return SqlInfo
     */
    public static SqlInfo getOptimizeCountSql(boolean optimizeCountSql, ISqlParser sqlParser, String originalSql) {
        if (!optimizeCountSql) {
            return SqlInfo.newInstance().setSql(getOriginalCountSql(originalSql));
        }
        // COUNT SQL 解析器
        if (null == COUNT_SQL_PARSER) {
            if (null != sqlParser) {
                // 用户自定义 COUNT SQL 解析
                COUNT_SQL_PARSER = sqlParser;
            } else {
                // 默认 JsqlParser 优化 COUNT
                COUNT_SQL_PARSER = new JsqlParserCountOptimize();
            }
        }
        return COUNT_SQL_PARSER.parser(null, originalSql);
    }
}
