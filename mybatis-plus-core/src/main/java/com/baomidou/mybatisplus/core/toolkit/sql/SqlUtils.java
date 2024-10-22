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
package com.baomidou.mybatisplus.core.toolkit.sql;

import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SqlUtils工具类
 * !!! 本工具不适用于本框架外的类使用 !!!
 *
 * @author Caratacus
 * @since 2016-11-13
 */
public abstract class SqlUtils implements Constants {
    private static final String tp = "[\\w-,]+?";
    private static final Pattern pattern = Pattern.compile(String.format("\\{@((%s)|(%s:\\w+?)|(%s:\\w+?:\\w+?))}", tp, tp, tp));

    /**
     * 用%连接like
     *
     * @param str 原字符串
     * @return like 的值
     */
    public static String concatLike(Object str, SqlLike type) {
        switch (type) {
            case LEFT:
                return PERCENT + str;
            case RIGHT:
                return str + PERCENT;
            default:
                return PERCENT + str + PERCENT;
        }
    }

    public static List<String> findPlaceholder(String sql) {
        Matcher matcher = pattern.matcher(sql);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    public static String replaceSqlPlaceholder(String sql, List<String> placeHolder, String escapeSymbol) {
        for (String s : placeHolder) {
            String s1 = s.substring(2, s.length() - 1);
            int i1 = s1.indexOf(COLON);
            String tableName;
            String alisa = null;
            String asAlisa = null;
            if (i1 < 0) {
                tableName = s1;
            } else {
                tableName = s1.substring(0, i1);
                s1 = s1.substring(i1 + 1);
                i1 = s1.indexOf(COLON);
                if (i1 < 0) {
                    alisa = s1;
                } else {
                    alisa = s1.substring(0, i1);
                    asAlisa = s1.substring(i1 + 1);
                }
            }
            sql = sql.replace(s, getSelectBody(tableName, alisa, asAlisa, escapeSymbol));
        }
        return sql;
    }

    @SuppressWarnings("all")
    public static String getSelectBody(String tableName, String alisa, String asAlisa, String escapeSymbol) {
        int notSel = tableName.indexOf("-");
        List<String> notSelColl = null;
        if (notSel > 0) {
            notSelColl = Arrays.asList(tableName.substring(notSel + 1).split(COMMA));
            tableName = tableName.substring(0, notSel);
        }
        TableInfo tableInfo = TableInfoHelper.getTableInfo(tableName);
        Assert.notNull(tableInfo, "can not find TableInfo Cache by \"%s\"", tableName);
        String s = tableInfo.chooseSelect(TableFieldInfo::isSelect, notSelColl);
        if (alisa == null) {
            return s;
        }
        return getNewSelectBody(s, alisa, asAlisa, escapeSymbol);
    }

    public static String getNewSelectBody(String selectBody, String alisa, String asAlisa, String escapeSymbol) {
        String[] split = selectBody.split(COMMA);
        StringBuilder sb = new StringBuilder();
        boolean asA = asAlisa != null;
        for (String body : split) {
            final String sa = alisa.concat(DOT);
            if (asA) {
                int as = body.indexOf(AS);
                String column;
                String property;
                if (as < 0) {
                    column = body;
                    property = StringUtils.getTargetColumn(body);
                } else {
                    column = body.substring(0, as);
                    property = body.substring(as + 4);
                    property = StringUtils.getTargetColumn(property);
                }
                sb.append(sa).append(column).append(AS).append(escapeColumn(asAlisa.concat(DOT).concat(property), escapeSymbol));
            } else {
                sb.append(sa).append(body);
            }
            sb.append(COMMA);
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    private static String escapeColumn(String column, String escapeSymbol) {
        return escapeSymbol.concat(column).concat(escapeSymbol);
    }
}
