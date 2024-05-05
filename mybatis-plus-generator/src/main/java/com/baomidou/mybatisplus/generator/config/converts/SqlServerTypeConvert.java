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
package com.baomidou.mybatisplus.generator.config.converts;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.ITypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;

import static com.baomidou.mybatisplus.generator.config.converts.TypeConverts.contains;
import static com.baomidou.mybatisplus.generator.config.converts.TypeConverts.containsAny;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.*;

/**
 * SQLServer 字段类型转换
 *
 * @author hubin, hanchunlin
 * @since 2017-01-20
 */
public class SqlServerTypeConvert implements ITypeConvert {

    public static final SqlServerTypeConvert INSTANCE = new SqlServerTypeConvert();

    /**
     * {@inheritDoc}
     */
    @Override
    public IColumnType processTypeConvert(GlobalConfig config, String fieldType) {
        return TypeConverts.use(fieldType)
            .test(containsAny("char", "xml", "text").then(STRING))
            .test(contains("bigint").then(LONG))
            .test(contains("int").then(INTEGER))
            .test(containsAny("date", "time").then(t -> toDateType(config, t)))
            .test(contains("bit").then(BOOLEAN))
            .test(containsAny("decimal", "numeric").then(DOUBLE))
            .test(contains("money").then(BIG_DECIMAL))
            .test(containsAny("binary", "image").then(BYTE_ARRAY))
            .test(containsAny("float", "real").then(FLOAT))
            .or(STRING);
    }

    /**
     * 转换为日期类型
     *
     * @param config 配置信息
     * @param fieldType   类型
     * @return 返回对应的列类型
     */
    public static IColumnType toDateType(GlobalConfig config, String fieldType) {
        switch (config.getDateType()) {
            case SQL_PACK:
                switch (fieldType) {
                    case "date":
                        return DATE_SQL;
                    case "time":
                        return TIME;
                    default:
                        return TIMESTAMP;
                }
            case TIME_PACK:
                switch (fieldType) {
                    case "date":
                        return LOCAL_DATE;
                    case "time":
                        return LOCAL_TIME;
                    default:
                        return LOCAL_DATE_TIME;
                }
            default:
                return DATE;
        }
    }
}
