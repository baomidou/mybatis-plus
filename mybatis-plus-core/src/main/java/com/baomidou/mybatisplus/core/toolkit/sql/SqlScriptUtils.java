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
package com.baomidou.mybatisplus.core.toolkit.sql;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * <p>
 * sql 脚本工具类
 * </p>
 *
 * @author miemie
 * @since 2018-08-15
 */
public final class SqlScriptUtils {

    private SqlScriptUtils() {
        // ignore
    }

    /**
     * <p>
     * 获取 带 if 标签的脚本
     * </p>
     *
     * @param sqlScript sql 脚本片段
     * @return if 脚本
     */
    public static String convertIf(final String sqlScript, final String ifTest, boolean newLine) {
        String newSqlScript = sqlScript;
        if (newLine) {
            newSqlScript = StringPool.NEWLINE + newSqlScript + StringPool.NEWLINE;
        }
        return String.format("<if test=\"%s\">%s</if>", ifTest, newSqlScript);
    }

    /**
     * <p>
     * 获取 带 trim 标签的脚本
     * </p>
     *
     * @param sqlScript       sql 脚本片段
     * @param prefix          以...开头
     * @param suffix          以...结尾
     * @param prefixOverrides 干掉最前一个...
     * @param suffixOverrides 干掉最后一个...
     * @return trim 脚本
     */
    public static String convertTrim(final String sqlScript, final String prefix, final String suffix,
                                     final String prefixOverrides, final String suffixOverrides) {
        StringBuilder sb = new StringBuilder("<trim");
        if (StringUtils.isNotEmpty(prefix)) {
            sb.append(" prefix=\"").append(prefix).append(StringPool.QUOTE);
        }
        if (StringUtils.isNotEmpty(suffix)) {
            sb.append(" suffix=\"").append(suffix).append(StringPool.QUOTE);
        }
        if (StringUtils.isNotEmpty(prefixOverrides)) {
            sb.append(" prefixOverrides=\"").append(prefixOverrides).append(StringPool.QUOTE);
        }
        if (StringUtils.isNotEmpty(suffixOverrides)) {
            sb.append(" suffixOverrides=\"").append(suffixOverrides).append(StringPool.QUOTE);
        }
        return sb.append(StringPool.RIGHT_CHEV).append(StringPool.NEWLINE).append(sqlScript)
            .append(StringPool.NEWLINE).append("</trim>").toString();
    }

    /**
     * <p>
     * 生成 choose 标签的脚本
     * </p>
     *
     * @param whenTest  when 内 test 的内容
     * @param otherwise otherwise 内容
     * @return choose 脚本
     */
    public static String convertChoose(final String whenTest, final String whenSqlScript, final String otherwise) {
        return "<choose>" + StringPool.NEWLINE +
            "<when test=\"" + whenTest + StringPool.QUOTE + StringPool.RIGHT_CHEV + StringPool.NEWLINE +
            whenSqlScript + StringPool.NEWLINE + "</when>" + StringPool.NEWLINE +
            "<otherwise>" + otherwise + "</otherwise>" + StringPool.NEWLINE +
            "</choose>";
    }

    /**
     * <p>
     * 生成 foreach 标签的脚本
     * </p>
     *
     * @param sqlScript  foreach 内部的 sql 脚本
     * @param collection collection
     * @param index      index
     * @param item       item
     * @param separator  separator
     * @return foreach 脚本
     */
    public static String convertForeach(final String sqlScript, final String collection, final String index,
                                        final String item, final String separator) {
        StringBuilder sb = new StringBuilder("<foreach");
        if (StringUtils.isNotEmpty(collection)) {
            sb.append(" collection=\"").append(collection).append(StringPool.QUOTE);
        }
        if (StringUtils.isNotEmpty(index)) {
            sb.append(" index=\"").append(index).append(StringPool.QUOTE);
        }
        if (StringUtils.isNotEmpty(item)) {
            sb.append(" item=\"").append(item).append(StringPool.QUOTE);
        }
        if (StringUtils.isNotEmpty(separator)) {
            sb.append(" separator=\"").append(separator).append(StringPool.QUOTE);
        }
        return sb.append(StringPool.RIGHT_CHEV).append(StringPool.NEWLINE).append(sqlScript)
            .append(StringPool.NEWLINE).append("</foreach>").toString();
    }

    /**
     * <p>
     * 生成 where 标签的脚本
     * </p>
     *
     * @param sqlScript where 内部的 sql 脚本
     * @return where 脚本
     */
    public static String convertWhere(final String sqlScript) {
        return "<where>" + StringPool.NEWLINE + sqlScript + StringPool.NEWLINE + "</where>";
    }

    /**
     * <p>
     * 安全入参:  #{入参}
     * </p>
     *
     * @param param 入参
     * @return 脚本
     */
    public static String safeParam(final String param) {
        return StringPool.HASH_LEFT_BRACE + param + StringPool.RIGHT_BRACE;
    }

    /**
     * <p>
     * 非安全入参:  ${入参}
     * </p>
     *
     * @param param 入参
     * @return 脚本
     */
    public static String unSafeParam(final String param) {
        return StringPool.DOLLAR_LEFT_BRACE + param + StringPool.RIGHT_BRACE;
    }
}
