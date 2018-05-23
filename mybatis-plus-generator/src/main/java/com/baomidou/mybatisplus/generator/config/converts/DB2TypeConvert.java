/**
 * Copyright (c) 2011-2016, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.generator.config.converts;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.ITypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;

/**
 * <p>
 * DB2 字段类型转换
 * </p>
 *
 * @author zhanyao
 * @date 2018-05-16
 */
public class DB2TypeConvert implements ITypeConvert {

    @Override
    public DbColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
        String t = fieldType.toLowerCase();
        if (t.contains("char")) {
            return DbColumnType.STRING;
        } else if (t.contains("date") || t.contains("time")) {
            switch (globalConfig.getDateType()) {
                case ONLY_DATE:
                    return DbColumnType.DATE;
                case SQL_PACK:
                    switch (t) {
                        case "time":
                            return DbColumnType.TIME;
                        case "date":
                            return DbColumnType.DATE_SQL;
                        case "timestamp":
                            return DbColumnType.TIMESTAMP;
                    }
                case TIME_PACK:
                    switch (t) {
                        case "time":
                            return DbColumnType.LOCAL_TIME;
                        case "date":
                            return DbColumnType.LOCAL_DATE;
                        case "timestamp":
                            return DbColumnType.LOCAL_DATE_TIME;
                    }
            }
        } else if (t.contains("float")) {//todo float类型真多,心累,慢慢来
            return DbColumnType.FLOAT;
        } else if (t.contains("double")) {
            return DbColumnType.DOUBLE;
        } else if (t.contains("clob")) {
            return DbColumnType.CLOB;
        } else if (t.contains("blob")) {
            return DbColumnType.OBJECT;
        } else if (t.contains("binary")) {
            return DbColumnType.BYTE_ARRAY;
        } else if (t.contains("raw")) {
            return DbColumnType.BYTE_ARRAY;
        } else if (t.contains("boolean")) {
            return DbColumnType.BOOLEAN;
        }
        return DbColumnType.STRING;
    }

}
