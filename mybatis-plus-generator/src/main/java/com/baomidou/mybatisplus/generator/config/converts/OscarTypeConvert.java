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
package com.baomidou.mybatisplus.generator.config.converts;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.ITypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;

import static com.baomidou.mybatisplus.generator.config.converts.TypeConverts.contains;
import static com.baomidou.mybatisplus.generator.config.converts.TypeConverts.containsAny;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.*;

/**
 * KingbaseES 字段类型转换
 *
 * @author kingbase, hanchunlin
 * @since 2019-10-12
 */
public class OscarTypeConvert implements ITypeConvert {
    public static final OscarTypeConvert INSTANCE = new OscarTypeConvert();

    /**
     * @param globalConfig 全局配置
     * @param fieldType    字段类型
     * @return 返回对应的字段类型
     */
    @Override
    public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
        return TypeConverts.use(fieldType)
            .test(containsAny("CHARACTER", "char", "varchar", "text", "character varying").then(STRING))
            .test(containsAny("bigint", "int8").then(LONG))
            .test(containsAny("int", "int1", "int2", "int3", "int4", "tinyint", "integer").then(INTEGER))
            .test(containsAny("date", "time", "timestamp").then(p -> toDateType(globalConfig, p)))
            .test(containsAny("bit", "boolean").then(BOOLEAN))
            .test(containsAny("decimal", "numeric", "number").then(BIG_DECIMAL))
            .test(contains("clob").then(CLOB))
            .test(contains("blob").then(BYTE_ARRAY))
            .test(contains("float").then(FLOAT))
            .test(containsAny("double", "real", "float4", "float8").then(DOUBLE))
            .or(STRING);
    }

    /**
     * 转换为日期类型
     *
     * @param config 配置信息
     * @param type   类型
     * @return 返回对应的列类型
     */
    private IColumnType toDateType(GlobalConfig config, String type) {
        DateType dateType = config.getDateType();
        if (dateType == DateType.SQL_PACK) {
            switch (type) {
                case "date":
                    return DATE_SQL;
                case "time":
                    return TIME;
                default:
                    return TIMESTAMP;
            }
        } else if (dateType == DateType.TIME_PACK) {
            switch (type) {
                case "date":
                    return LOCAL_DATE;
                case "time":
                    return LOCAL_TIME;
                default:
                    return LOCAL_DATE_TIME;
            }
        }
        return DbColumnType.DATE;
    }

}
