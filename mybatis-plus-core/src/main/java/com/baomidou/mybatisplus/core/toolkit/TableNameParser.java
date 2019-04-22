/*
 * Copyright (c) 2011-2020, Nadeem Mohammad.
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
package com.baomidou.mybatisplus.core.toolkit;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL 表名解析
 * https://github.com/mnadeem/sql-table-name-parser
 * Ultra light, Ultra fast parser to extract table name out SQLs, supports oracle dialect SQLs as well.
 * USE: new TableNameParser(sql).tables()
 *
 * @author Nadeem Mohammad
 * @since 2019-04-22
 */
public final class TableNameParser {

    private static final int NO_INDEX = -1;
    private static final String SPACE = " ";
    private static final String REGEX_SPACE = "\\s+";

    private static final String TOKEN_ORACLE_HINT_START = "/*+";
    private static final String TOKEN_ORACLE_HINT_END = "*/";
    private static final String TOKEN_SINGLE_LINE_COMMENT = "--";
    private static String TOKEN_NEWLINE = "\\r\\n|\\r|\\n|\\n\\r";
    private static final String TOKEN_SEMI_COLON = ";";
    private static final String TOKEN_PARAN_START = "(";
    private static final String TOKEN_COMMA = ",";
    private static final String TOKEN_SET = "set";
    private static final String TOKEN_OF = "of";
    private static final String TOKEN_DUAL = "dual";
    private static final String TOKEN_DELETE = "delete";
    private static final String TOKEN_CREATE = "create";
    private static final String TOKEN_INDEX = "index";
    private static final String TOKEN_ASTERICK = "*";

    private static final String KEYWORD_JOIN = "join";
    private static final String KEYWORD_INTO = "into";
    private static final String KEYWORD_TABLE = "table";
    private static final String KEYWORD_FROM = "from";
    private static final String KEYWORD_USING = "using";
    private static final String KEYWORD_UPDATE = "update";

    private static final List<String> concerned = Arrays.asList(KEYWORD_TABLE, KEYWORD_INTO, KEYWORD_JOIN, KEYWORD_USING, KEYWORD_UPDATE);
    private static final List<String> ignored = Arrays.asList(TOKEN_PARAN_START, TOKEN_SET, TOKEN_OF, TOKEN_DUAL);

    private Map<String, String> tables = new HashMap<>();

    /**
     * Extracts table names out of SQL
     * @param sql
     */
    public TableNameParser(final String sql) {
        String noComments = removeComments(sql);
        String normalized = normalized(noComments);
        String cleansed = clean(normalized);
        String[] tokens = cleansed.split(REGEX_SPACE);
        int index = 0;

        String firstToken = tokens[index];
        if (isOracleSpecialDelete(firstToken, tokens, index)) {
            handleSpecialOracleSpecialDelete(firstToken, tokens, index);
        } else if (isCreateIndex(firstToken, tokens, index)) {
            handleCreateIndex(firstToken, tokens, index);
        } else {
            while (moreTokens(tokens, index)) {
                String currentToken = tokens[index++];

                if (isFromToken(currentToken)) {
                    processFromToken(tokens, index);
                } else if (shouldProcess(currentToken)) {
                    String nextToken = tokens[index++];
                    considerInclusion(nextToken);

                    if (moreTokens(tokens, index)) {
                        nextToken = tokens[index++];
                    }
                }
            }
        }
    }

    private String removeComments(final String sql) {
        StringBuilder sb = new StringBuilder(sql);
        int nextCommentPosition = sb.indexOf(TOKEN_SINGLE_LINE_COMMENT);
        while (nextCommentPosition > -1) {
            int end = indexOfRegex(TOKEN_NEWLINE, sb.substring(nextCommentPosition));
            if (end == -1) {
                return sb.substring(0, nextCommentPosition);
            } else {
                sb.replace(nextCommentPosition, end + nextCommentPosition, "");
            }
            nextCommentPosition = sb.indexOf(TOKEN_SINGLE_LINE_COMMENT);
        }
        return sb.toString();
    }

    private int indexOfRegex(String regex, String string) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        return matcher.find() ? matcher.start() : -1;
    }

    private String normalized(final String sql) {
        String normalized = sql.trim().replaceAll(TOKEN_NEWLINE, SPACE).replaceAll(TOKEN_COMMA, " , ")
            .replaceAll("\\(", " ( ").replaceAll("\\)", " ) ");
        if (normalized.endsWith(TOKEN_SEMI_COLON)) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private String clean(final String normalized) {
        int start = normalized.indexOf(TOKEN_ORACLE_HINT_START);
        int end;
        if (start != NO_INDEX) {
            end = normalized.indexOf(TOKEN_ORACLE_HINT_END);
            if (end != NO_INDEX) {
                String firstHalf = normalized.substring(0, start);
                String secondHalf = normalized.substring(end + 2, normalized.length());
                return firstHalf.trim() + SPACE + secondHalf.trim();
            }
        }
        return normalized;
    }

    private boolean isOracleSpecialDelete(final String currentToken, final String[] tokens, int index) {
        index++;// Point to next token
        if (TOKEN_DELETE.equals(currentToken)) {
            if (moreTokens(tokens, index)) {
                String nextToken = tokens[index++];
                if (!KEYWORD_FROM.equals(nextToken) && !TOKEN_ASTERICK.equals(nextToken)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void handleSpecialOracleSpecialDelete(final String currentToken, final String[] tokens, int index) {
        String tableName = tokens[index + 1];
        considerInclusion(tableName);
    }

    private boolean isCreateIndex(String currentToken, String[] tokens, int index) {
        index++; // Point to next token
        if (TOKEN_CREATE.equals(currentToken.toLowerCase()) && hasIthToken(tokens, index, 3)) {
            String nextToken = tokens[index++];
            if (TOKEN_INDEX.equals(nextToken.toLowerCase())) {
                return true;
            }

        }
        return false;
    }

    private void handleCreateIndex(String currentToken, String[] tokens, int index) {
        String tableName = tokens[index + 4];
        considerInclusion(tableName);
    }

    private boolean hasIthToken(String[] tokens, int currentIndex, int tokenNumber) {
        if (moreTokens(tokens, currentIndex) && tokens.length > currentIndex + tokenNumber) {
            return true;
        }
        return false;
    }

    private boolean shouldProcess(final String currentToken) {
        return concerned.contains(currentToken.toLowerCase());
    }

    private boolean isFromToken(final String currentToken) {
        return KEYWORD_FROM.equals(currentToken.toLowerCase());
    }

    private void processFromToken(final String[] tokens, int index) {
        String currentToken = tokens[index++];
        considerInclusion(currentToken);

        String nextToken = null;
        if (moreTokens(tokens, index)) {
            nextToken = tokens[index++];
        }

        if (shouldProcessMultipleTables(nextToken)) {
            processNonAliasedMultiTables(tokens, index, nextToken);
        } else {
            processAliasedMultiTables(tokens, index, currentToken);
        }
    }

    private void processNonAliasedMultiTables(final String[] tokens, int index, String nextToken) {
        while (nextToken.equals(TOKEN_COMMA)) {
            String currentToken = tokens[index++];
            considerInclusion(currentToken);
            if (moreTokens(tokens, index)) {
                nextToken = tokens[index++];
            } else {
                break;
            }
        }
    }

    private void processAliasedMultiTables(final String[] tokens, int index, String currentToken) {
        String nextNextToken = null;
        if (moreTokens(tokens, index)) {
            nextNextToken = tokens[index++];
        }

        if (shouldProcessMultipleTables(nextNextToken)) {
            while (moreTokens(tokens, index) && nextNextToken.equals(TOKEN_COMMA)) {
                if (moreTokens(tokens, index)) {
                    currentToken = tokens[index++];
                }
                if (moreTokens(tokens, index)) {
                    index++;
                }
                if (moreTokens(tokens, index)) {
                    nextNextToken = tokens[index++];
                }
                considerInclusion(currentToken);
            }
        }
    }

    private boolean shouldProcessMultipleTables(final String nextToken) {
        return nextToken != null && nextToken.equals(TOKEN_COMMA);
    }

    private boolean moreTokens(final String[] tokens, int index) {
        return index < tokens.length;
    }

    private void considerInclusion(final String token) {
        if (!ignored.contains(token.toLowerCase()) && !this.tables.containsKey(token.toLowerCase())) {
            this.tables.put(token.toLowerCase(), token);
        }
    }

    /**
     * parser tables
     *
     * @return table names extracted out of sql
     */
    public Collection<String> tables() {
        return new HashSet<>(this.tables.values());
    }
}
