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
package com.baomidou.mybatisplus.generator.config.converts;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.ITypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.baomidou.mybatisplus.generator.config.converts.TypeConverts.contains;
import static com.baomidou.mybatisplus.generator.config.converts.TypeConverts.containsAny;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.BIG_DECIMAL;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.BLOB;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.BOOLEAN;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.BYTE;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.BYTE_ARRAY;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.CLOB;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.DOUBLE;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.FLOAT;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.INTEGER;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.LONG;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.STRING;

/**
 * MYSQL 数据库字段类型转换 <br>
 * bit类型数据转换 bit(1) -> Boolean类型 bit(2->64) -> Byte类型
 *
 * @author hubin, hanchunlin, xiaoliang
 * @since 2017-01-20
 */
public class MySqlTypeConvert implements ITypeConvert {

    private static final Pattern UNSIGNED_INTEGER = Pattern.compile(
            "(tinyint|smallint|mediumint|int|integer|bigint)(?:\\(\\d+\\))?\\s+unsigned(?:\\s+zerofill)?",
            Pattern.CASE_INSENSITIVE);

    public static final MySqlTypeConvert INSTANCE = new MySqlTypeConvert();

    /**
     * {@inheritDoc}
     */
    @Override
    public IColumnType processTypeConvert(@NotNull GlobalConfig config, @NotNull String fieldType) {
        DbColumnType unsignedType = getUnsignedIntegerType(fieldType);
        if (unsignedType != null) {
            return unsignedType;
        }
        return TypeConverts.use(fieldType).test(contains("point").then(BYTE_ARRAY))
                .test(containsAny("char", "text", "json", "enum").then(STRING)).test(contains("bigint").then(LONG))
                .test(containsAny("tinyint(1)", "bit(1)").then(BOOLEAN)).test(contains("bit").then(BYTE))
                .test(contains("int").then(INTEGER)).test(contains("decimal").then(BIG_DECIMAL))
                .test(contains("clob").then(CLOB)).test(contains("blob").then(BLOB))
                .test(contains("binary").then(BYTE_ARRAY)).test(contains("float").then(FLOAT))
                .test(contains("double").then(DOUBLE))
                .test(containsAny("date", "time", "year").then(t -> toDateType(config, t))).or(STRING);
    }

    /**
     * 匹配 JDBC 元数据或 SHOW FULL COLUMNS 返回的 MySQL 无符号整数类型。
     *
     * @return 能容纳整列取值范围的 Java 类型，其他列返回 null
     */
    @Nullable
    public static DbColumnType getUnsignedIntegerType(@Nullable String fieldType) {
        if (fieldType == null) {
            return null;
        }
        Matcher matcher = UNSIGNED_INTEGER.matcher(fieldType);
        if (!matcher.matches()) {
            return null;
        }
        String type = matcher.group(1);
        if ("bigint".equalsIgnoreCase(type)) {
            return DbColumnType.BIG_INTEGER;
        }
        if ("int".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type)) {
            return DbColumnType.LONG;
        }
        return DbColumnType.INTEGER;
    }

    /**
     * 转换为日期类型
     *
     * @param config 配置信息
     * @param type 类型
     * @return 返回对应的列类型
     */
    public static IColumnType toDateType(GlobalConfig config, String type) {
        String dateType = type.replaceAll("\\(\\d+\\)", "");
        switch (config.getDateType()) {
            case ONLY_DATE :
                return DbColumnType.DATE;
            case SQL_PACK :
                switch (dateType) {
                    case "date" :
                    case "year" :
                        return DbColumnType.DATE_SQL;
                    case "time" :
                        return DbColumnType.TIME;
                    default :
                        return DbColumnType.TIMESTAMP;
                }
            case TIME_PACK :
                switch (dateType) {
                    case "date" :
                        return DbColumnType.LOCAL_DATE;
                    case "time" :
                        return DbColumnType.LOCAL_TIME;
                    case "year" :
                        return DbColumnType.YEAR;
                    default :
                        return DbColumnType.LOCAL_DATE_TIME;
                }
        }
        return STRING;
    }

}
