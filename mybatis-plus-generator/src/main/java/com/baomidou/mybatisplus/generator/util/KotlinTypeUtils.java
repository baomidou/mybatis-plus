/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.generator.util;

import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;

import java.util.HashMap;
import java.util.Map;

/**
 * Kotlin类型处理工具类
 *
 * @author nieqiurong
 * @since 3.5.10
 */
public class KotlinTypeUtils {

    private static final Map<IColumnType, String> JAVA_TO_KOTLIN_TYPE = new HashMap<>();

    static {
        JAVA_TO_KOTLIN_TYPE.put(DbColumnType.BASE_INT, "Int");
        JAVA_TO_KOTLIN_TYPE.put(DbColumnType.INTEGER, "Int");
        JAVA_TO_KOTLIN_TYPE.put(DbColumnType.BASE_DOUBLE, "Double");
        JAVA_TO_KOTLIN_TYPE.put(DbColumnType.BASE_FLOAT, "Float");
        JAVA_TO_KOTLIN_TYPE.put(DbColumnType.BASE_LONG, "Long");
        JAVA_TO_KOTLIN_TYPE.put(DbColumnType.BASE_BOOLEAN, "Boolean");
        JAVA_TO_KOTLIN_TYPE.put(DbColumnType.BASE_CHAR, "Char");
    }

    /**
     * 转换Java类型至Kotlin类型
     *
     * @param columnType {@link DbColumnType}
     * @return Kotlin类型
     */
    public static String getStringType(IColumnType columnType) {
        return JAVA_TO_KOTLIN_TYPE.getOrDefault(columnType, columnType.getType());
    }

}
