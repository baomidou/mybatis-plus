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
     * @param sqlScript sql 脚本片段
     * @return if 脚本
     */
    public static String convertIf(String sqlScript, String testInValue) {
        return String.format("<if test=\"%s\">%s</if>", testInValue, sqlScript);
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
        StringBuilder sb = new StringBuilder("<trim");
        if (StringUtils.isNotEmpty(prefix)) {
            sb.append(StringPool.SPACE).append("prefix=\"").append(prefix).append(StringPool.QUOTE);
        }
        if (StringUtils.isNotEmpty(suffix)) {
            sb.append(StringPool.SPACE).append("suffix=\"").append(suffix).append(StringPool.QUOTE);
        }
        if (StringUtils.isNotEmpty(prefixOverride)) {
            sb.append(StringPool.SPACE).append("prefixOverride=\"").append(prefixOverride).append(StringPool.QUOTE);
        }
        if (StringUtils.isNotEmpty(suffixOverrides)) {
            sb.append(StringPool.SPACE).append("suffixOverrides=\"").append(suffixOverrides).append(StringPool.QUOTE);
        }
        return sb.append(StringPool.RIGHT_CHEV).append(StringPool.NEWLINE).append(sqlScript)
            .append(StringPool.NEWLINE).append("</trim>").toString();
    }
}
