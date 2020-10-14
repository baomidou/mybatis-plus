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
package com.baomidou.mybatisplus.generator.keywords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * h2数据库关键字处理
 * http://www.h2database.com/html/advanced.html#keywords
 *
 * @author nieqiurong 2020/5/7.
 * @since 3.3.2
 */
public class H2KeyWordsHandler extends BaseKeyWordsHandler {
    
    private static List<String> KEY_WORDS = new ArrayList<>(Arrays.asList(
        "ALL",
        "AND",
        "ARRAY",
        "AS",
        "BETWEEN",
        "BOTH",
        "CASE",
        "CHECK",
        "CONSTRAINT",
        "CROSS",
        "CURRENT_CATALOG",
        "CURRENT_DATE",
        "CURRENT_SCHEMA",
        "CURRENT_TIME",
        "CURRENT_TIMESTAMP",
        "CURRENT_USER",
        "DISTINCT",
        "EXCEPT",
        "EXISTS",
        "FALSE",
        "FETCH",
        "FILTER",
        "FOR",
        "FOREIGN",
        "FROM",
        "FULL",
        "GROUP",
        "GROUPS",
        "HAVING",
        "IF",
        "ILIKE",
        "IN",
        "INNER",
        "INTERSECT",
        "INTERSECTS",
        "INTERVAL",
        "IS",
        "JOIN",
        "LEADING",
        "LEFT",
        "LIKE",
        "LIMIT",
        "LOCALTIME",
        "LOCALTIMESTAMP",
        "MINUS",
        "NATURAL",
        "NOT",
        "NULL",
        "OFFSET",
        "ON",
        "OR",
        "ORDER",
        "OVER",
        "PARTITION",
        "PRIMARY",
        "QUALIFY",
        "RANGE",
        "REGEXP",
        "RIGHT",
        "ROW",
        "_ROWID_",
        "ROWNUM",
        "ROWS",
        "SELECT",
        "SYSDATE",
        "SYSTIME",
        "SYSTIMESTAMP",
        "TABLE",
        "TODAY",
        "TOP",
        "TRAILING",
        "TRUE",
        "UNION",
        "UNIQUE",
        "UNKNOWN",
        "USING",
        "VALUES",
        "WHERE",
        "WINDOW",
        "WITH"
    ));
    
    public H2KeyWordsHandler() {
        super(KEY_WORDS);
    }
    
    public H2KeyWordsHandler(List<String> keyWords) {
        super(keyWords);
    }
    
    @Override
    public String formatStyle() {
        return "\"%s\"";
    }
    
}
