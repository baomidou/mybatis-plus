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
 * DM 字段类型转换
 *
 * @author halower
 * @since 2019-06-27
 */
public class DmTypeConvert implements ITypeConvert {

    @Override
    public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
        String t = fieldType.toLowerCase();
        return new BasicDataTypeHandler().getDbColumnType(t);
    }

     interface Handler {
         DbColumnType getDbColumnType(String fieldType);
    }

    /**
     * 常规数据类型
     */
    class BasicDataTypeHandler implements Handler {
        @Override
        public DbColumnType getDbColumnType(String fieldType) {
            //字符数据类型: CHAR,CHARACTER,VARCHAR
            if(fieldType.contains("char")) {
                return DbColumnType.STRING;
            }
            /**
             *  数值数据类型:
             *  NUMERIC,DECIMAL,DEC,MONEY,BIT,BOOL,BOOLEAN,INTEGER,INT,BIGINT
             *  TINYINT,BYTE,SMALLINT,BINARY,VARBINARY
             */
            else if (fieldType.contains("numeric")
                || fieldType.contains("decimal")
                || fieldType.contains("dec")
                || fieldType.contains("money")) {
                return DbColumnType.BIG_DECIMAL;
            }
            else if(fieldType.contains("bit")
                || fieldType.contains("bool")
                || fieldType.contains("boolean")){
                return DbColumnType.BOOLEAN;
            }
            else if(fieldType.contains("integer") || fieldType.contains("int")){
                return DbColumnType.INTEGER;
            }
            else if(fieldType.contains("bigint") ){
                return DbColumnType.BIG_INTEGER;
            }
            else if(fieldType.contains("tinyint")
                || fieldType.contains("byte")
                || fieldType.contains("smallint")
                ){
                return DbColumnType.INTEGER;
            }
            else if (fieldType.contains("binary")
                || fieldType.contains("varbinary")
                ) {
                return DbColumnType.BYTE_ARRAY;
            }
            /**
             * 近似数值数据类型:
             * FLOAT
             */
            else if (fieldType.contains("float")) {
                return DbColumnType.FLOAT;
            }
            /**
             * DOUBLE, DOUBLE PRECISION,REAL
             */
            else if (fieldType.contains("double") || fieldType.contains("real")) {
                return DbColumnType.DOUBLE;
            }
           return new DateTimeDataTypeHandler().getDbColumnType(fieldType);
        }
    }

    /**
     *  日期时间数据类型
     */
    class DateTimeDataTypeHandler implements Handler{

        @Override
        public DbColumnType getDbColumnType(String fieldType) {
           if (fieldType.contains("date")
               || fieldType.contains("time")
               || fieldType.contains("timestamp")) {
                return DbColumnType.DATE;
            }
            return new MultimediaDataTypeHandler().getDbColumnType(fieldType);
        }
    }

    /**
     *  多媒体数据类型
     *  TEXT,LONGVARCHAR,CLOB,BLOB,IMAGE
     */

    class MultimediaDataTypeHandler implements Handler{
        @Override
        public DbColumnType getDbColumnType(String fieldType) {
            if (fieldType.contains("text")|| fieldType.contains("longvarchar")) {
                return DbColumnType.STRING;
            } else if (fieldType.contains("clob")) {
                return DbColumnType.CLOB;
            } else if (fieldType.contains("blob")) {
                return DbColumnType.BLOB;
            } else if (fieldType.contains("image")) {
                return DbColumnType.BYTE_ARRAY;
            }
            return DbColumnType.STRING;
        }
    }
}
