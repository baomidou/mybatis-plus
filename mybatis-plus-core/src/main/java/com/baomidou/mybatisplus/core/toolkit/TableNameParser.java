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
package com.baomidou.mybatisplus.core.toolkit;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL 表名解析
 * <p>
 * https://github.com/mnadeem/sql-table-name-parser
 * <p>
 * Ultra light, Ultra fast parser to extract table name out SQLs, supports oracle dialect SQLs as well.
 * USE: new TableNameParser(sql).tables()
 *
 * @author Nadeem Mohammad, hcl
 * @since 2019-04-22
 */
public final class TableNameParser {
    private static final String TOKEN_GROUP_START = "(";
    private static final String TOKEN_COMMA = ",";
    private static final String TOKEN_SET = "set";
    private static final String TOKEN_OF = "of";
    private static final String TOKEN_DUAL = "dual";
    private static final String TOKEN_DELETE = "delete";
    private static final String TOKEN_CREATE = "create";
    private static final String TOKEN_INDEX = "index";
    private static final String TOKEN_ALL = "*";

    private static final String KEYWORD_JOIN = "join";
    private static final String KEYWORD_INTO = "into";
    private static final String KEYWORD_TABLE = "table";
    private static final String KEYWORD_FROM = "from";
    private static final String KEYWORD_USING = "using";
    private static final String KEYWORD_UPDATE = "update";

    private static final List<String> concerned = Arrays.asList(KEYWORD_TABLE, KEYWORD_INTO, KEYWORD_JOIN, KEYWORD_USING, KEYWORD_UPDATE);
    private static final List<String> ignored = Arrays.asList(TOKEN_GROUP_START, TOKEN_SET, TOKEN_OF, TOKEN_DUAL);

    /**
     * 该表达式会匹配 SQL 中不是 SQL TOKEN 的部分，比如换行符，注释信息，结尾的 {@code ;} 等。
     * <p>
     * 排除的项目包括：
     * 1、以 -- 开头的注释信息
     * 2、;
     * 3、空白字符
     * 4、使用 /* * / 注释的信息
     * 5、把 ,() 也要分出来
     */
    private static final Pattern NON_SQL_TOKEN_PATTERN = Pattern.compile("(--[^\\v]+)|;|(\\s+)|((?s)/[*].*?[*]/)"
            + "|(((\\b|\\B)(?=[,()]))|((?<=[,()])(\\b|\\B)))"
    );

    private final List<SqlToken> tokens;

    /**
     * 从 SQL 中提取表名称
     *
     * @param sql 需要解析的 SQL 语句
     */
    public TableNameParser(String sql) {
        tokens = fetchAllTokens(sql);
    }

    /**
     * 接受一个新的访问者，并访问当前 SQL 的表名称
     * <p>
     * 现在我们改成了访问者模式，不在对以前的 SQL 做改动
     * 同时，你可以方便的获得表名位置的索引
     *
     * @param visitor 访问者
     */
    public void accept(TableNameVisitor visitor) {
        int index = 0;
        String first = tokens.get(index).getValue();
        if (isOracleSpecialDelete(first, tokens, index)) {
            visitNameToken(tokens.get(index + 1), visitor);
        } else if (isCreateIndex(first, tokens, index)) {
            visitNameToken(tokens.get(index + 4), visitor);
        } else {
            while (hasMoreTokens(tokens, index)) {
                String current = tokens.get(index++).getValue();
                if (isFromToken(current)) {
                    processFromToken(tokens, index, visitor);
                } else if (concerned.contains(current.toLowerCase())) {
                    if (hasMoreTokens(tokens, index)) {
                        SqlToken next = tokens.get(index++);
                        visitNameToken(next, visitor);
                    }
                }
            }
        }
    }

    /**
     * 表名访问器
     */
    public interface TableNameVisitor {
        /**
         * @param name 表示表名称的 token
         */
        void visit(SqlToken name);
    }

    /**
     * 从 SQL 语句中提取出 所有的 SQL Token
     *
     * @param sql SQL
     * @return 语句
     */
    protected List<SqlToken> fetchAllTokens(String sql) {
        List<SqlToken> tokens = new ArrayList<>();
        Matcher matcher = NON_SQL_TOKEN_PATTERN.matcher(sql);
        int last = 0;
        while (matcher.find()) {
            int start = matcher.start();
            if (start != last) {
                tokens.add(new SqlToken(last, start, sql.substring(last, start)));
            }
            last = matcher.end();
        }
        if (last != sql.length()) {
            tokens.add(new SqlToken(last, sql.length(), sql.substring(last)));
        }
        return tokens;
    }

    /**
     * 如果是 DELETE 后面紧跟的不是 FROM 或者 * ,则 返回 true
     *
     * @param current 当前的 token
     * @param tokens  token 列表
     * @param index   索引
     * @return 判断是不是 Oracle 特殊的删除手法
     */
    private static boolean isOracleSpecialDelete(String current, List<SqlToken> tokens, int index) {
        if (TOKEN_DELETE.equals(current)) {
            if (hasMoreTokens(tokens, index++)) {
                String next = tokens.get(index).getValue();
                return !KEYWORD_FROM.equals(next) && !TOKEN_ALL.equals(next);
            }
        }
        return false;
    }

    private boolean isCreateIndex(String current, List<SqlToken> tokens, int index) {
        index++; // Point to next token
        if (TOKEN_CREATE.equals(current.toLowerCase()) && hasIthToken(tokens, index)) {
            String next = tokens.get(index).getValue();
            return TOKEN_INDEX.equals(next.toLowerCase());
        }
        return false;
    }

    private static boolean hasIthToken(List<SqlToken> tokens, int currentIndex) {
        return hasMoreTokens(tokens, currentIndex) && tokens.size() > currentIndex + 3;
    }

    private static boolean isFromToken(String currentToken) {
        return KEYWORD_FROM.equals(currentToken.toLowerCase());
    }

    private static void processFromToken(List<SqlToken> tokens, int index, TableNameVisitor visitor) {
        SqlToken sqlToken = tokens.get(index++);
        visitNameToken(sqlToken, visitor);

        String next = null;
        if (hasMoreTokens(tokens, index)) {
            next = tokens.get(index++).getValue();
        }

        if (shouldProcessMultipleTables(next)) {
            processNonAliasedMultiTables(tokens, index, next, visitor);
        } else {
            processAliasedMultiTables(tokens, index, sqlToken, visitor);
        }
    }

    private static void processNonAliasedMultiTables(List<SqlToken> tokens, int index, String nextToken, TableNameVisitor visitor) {
        while (nextToken.equals(TOKEN_COMMA)) {
            visitNameToken(tokens.get(index++), visitor);
            if (hasMoreTokens(tokens, index)) {
                nextToken = tokens.get(index++).getValue();
            } else {
                break;
            }
        }
    }

    private static void processAliasedMultiTables(List<SqlToken> tokens, int index, SqlToken current, TableNameVisitor visitor) {
        String nextNextToken = null;
        if (hasMoreTokens(tokens, index)) {
            nextNextToken = tokens.get(index++).getValue();
        }

        if (shouldProcessMultipleTables(nextNextToken)) {
            while (hasMoreTokens(tokens, index) && nextNextToken.equals(TOKEN_COMMA)) {
                if (hasMoreTokens(tokens, index)) {
                    current = tokens.get(index++);
                }
                if (hasMoreTokens(tokens, index)) {
                    index++;
                }
                if (hasMoreTokens(tokens, index)) {
                    nextNextToken = tokens.get(index++).getValue();
                }
                visitNameToken(current, visitor);
            }
        }
    }

    private static boolean shouldProcessMultipleTables(final String nextToken) {
        return nextToken != null && nextToken.equals(TOKEN_COMMA);
    }

    private static boolean hasMoreTokens(List<SqlToken> tokens, int index) {
        return index < tokens.size();
    }

    private static void visitNameToken(SqlToken token, TableNameVisitor visitor) {
        String value = token.getValue().toLowerCase();
        if (!ignored.contains(value)) {
            visitor.visit(token);
        }
    }

    /**
     * parser tables
     *
     * @return table names extracted out of sql
     * @see #accept(TableNameVisitor)
     */
    public Collection<String> tables() {
        Map<String, String> tableMap = new HashMap<>();
        accept(token -> {
            String name = token.getValue();
            tableMap.putIfAbsent(name.toLowerCase(), name);
        });
        return new HashSet<>(tableMap.values());
    }

    /**
     * SQL 词
     */
    public static class SqlToken implements Comparable<SqlToken> {
        private final int start;
        private final int end;
        private final String value;

        private SqlToken(int start, int end, String value) {
            this.start = start;
            this.end = end;
            this.value = value;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public String getValue() {
            return value;
        }

        @Override
        public int compareTo(SqlToken o) {
            return Integer.compare(start, o.start);
        }

        @Override
        public String toString() {
            return value;
        }

    }

}
