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
     * <p>
     * 获取 带 if 标签的 sql
     * </p>
     *
     * @param sqlSet         set sql 片段
     * @param property       entity 属性
     * @param isCharSequence 是 CharSequence 类型否
     * @param fieldStrategy  验证逻辑
     * @return if 脚本
     */
    public static String convertIf(String sqlSet, String property, boolean isCharSequence, FieldStrategy fieldStrategy) {
        if (fieldStrategy == FieldStrategy.IGNORED) {
            return sqlSet;
        }
        if (fieldStrategy == FieldStrategy.NOT_NULL) {
            return String.format("<if test=\"%s != null\">%s</if>", property, sqlSet);
        }
        if (isCharSequence) {
            return String.format("<if test=\"%s != null and %s != ''\">%s</if>", property, property, sqlSet);
        }
        return String.format("<if test=\"%s != null\">%s</if>", property, sqlSet);
    }
}
