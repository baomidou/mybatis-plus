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

import com.baomidou.mybatisplus.annotation.FieldStrategy;
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

    /**
     * 脚本符号: #{
     */
    public static final String HASH_LEFT_BRACE = StringPool.HASH + StringPool.LEFT_BRACE;

    private SqlScriptUtils() {
        // ignore
    }

    /**
     * <p>
     * 获取 带 if 标签的脚本
     * </p>
     *
     * @param sqlScript      sql 脚本片段
     * @param property       entity 属性
     * @param isCharSequence 是 CharSequence 类型否
     * @param fieldStrategy  验证逻辑
     * @return if 脚本
     */
    public static String convertIf(String sqlScript, String property, boolean isCharSequence, FieldStrategy fieldStrategy) {
        if (fieldStrategy == FieldStrategy.IGNORED) {
            return sqlScript;
        }
        if (fieldStrategy == FieldStrategy.NOT_EMPTY && isCharSequence) {
            return String.format("<if test=\"%s != null and %s != ''\">%s</if>", property, property, sqlScript);
        }
        return String.format("<if test=\"%s != null\">%s</if>", property, sqlScript);
    }

    /**
     * <p>
     * 获取 带 trim 标签的脚本
     * </p>
     *
     * @param sqlScript       sql 脚本片段
     * @param prefix          以...开头
     * @param suffix          以...结尾
     * @param prefixOverride  干掉最前一个...
     * @param suffixOverrides 干掉最后一个...
     * @return trim 脚本
     */
    public static String convertTrim(String sqlScript, String prefix, String suffix, String prefixOverride,
                                     String suffixOverrides) {
        StringBuilder sb = new StringBuilder();
        sb.append("<trim");
        if (StringUtils.isNotEmpty(prefix)) {
            sb.append(StringPool.SPACE).append("prefix=\"").append(prefix).append("\"");
        }
        if (StringUtils.isNotEmpty(suffix)) {
            sb.append(StringPool.SPACE).append("suffix=\"").append(suffix).append("\"");
        }
        if (StringUtils.isNotEmpty(prefixOverride)) {
            sb.append(StringPool.SPACE).append("prefixOverride=\"").append(prefixOverride).append("\"");
        }
        if (StringUtils.isNotEmpty(suffixOverrides)) {
            sb.append(StringPool.SPACE).append("suffixOverrides=\"").append(suffixOverrides).append("\"");
        }
        return sb.append(">").append(sqlScript).append("</trim>").toString();
    }
}
