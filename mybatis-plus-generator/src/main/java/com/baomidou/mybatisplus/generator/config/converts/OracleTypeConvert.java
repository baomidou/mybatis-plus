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
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;

/**
 * ORACLE 字段类型转换
 *
 * @author hubin
 * @since 2017-01-20
 */
public class OracleTypeConvert implements ITypeConvert {

    @Override
    public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
        String t = fieldType.toLowerCase();
        if (t.contains("char")) {
            return DbColumnType.STRING;
        } else if (t.contains("date") || t.contains("timestamp")) {
            switch (globalConfig.getDateType()) {
                case ONLY_DATE:
                    return DbColumnType.DATE;
                case SQL_PACK:
                    return DbColumnType.TIMESTAMP;
                case TIME_PACK:
                    return DbColumnType.LOCAL_DATE_TIME;
            }
        } else if (t.contains("number")) {
            if (t.matches("number\\(+\\d\\)")) {
                return DbColumnType.INTEGER;
            } else if (t.matches("number\\(+\\d{2}+\\)")) {
                return DbColumnType.LONG;
            }
            return DbColumnType.BIG_DECIMAL;
        } else if (t.contains("float")) {
            return DbColumnType.FLOAT;
        } else if (t.contains("clob")) {
            return DbColumnType.STRING;
        } else if (t.contains("blob")) {
            return DbColumnType.BLOB;
        } else if (t.contains("binary")) {
            return DbColumnType.BYTE_ARRAY;
        } else if (t.contains("raw")) {
            return DbColumnType.BYTE_ARRAY;
        }
        return DbColumnType.STRING;
    }

}
