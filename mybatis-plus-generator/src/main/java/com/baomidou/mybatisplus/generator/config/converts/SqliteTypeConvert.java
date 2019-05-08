package com.baomidou.mybatisplus.generator.config.converts;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.ITypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
/**
 * SQLite 字段类型转换
 *
 * @author chen_wj
 * @since 2019-05-08
 */
public class SqliteTypeConvert implements ITypeConvert {
    @Override
    public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
        String t = fieldType.toLowerCase();
       if (t.contains("bigint")) {
            return DbColumnType.LONG;
        } else if (t.contains("tinyint(1)") || t.contains("boolean")) {
            return DbColumnType.BOOLEAN;
        } else if (t.contains("int")) {
            return DbColumnType.INTEGER;
        } else if (t.contains("text") || t.contains("char") || t.contains("enum") || t.contains("set")) {
            return DbColumnType.STRING;
        } else if (t.contains("decimal") || t.contains("numeric")) {
            return DbColumnType.BIG_DECIMAL;
        } else if (t.contains("clob")) {
            return DbColumnType.CLOB;
        } else if (t.contains("blob")) {
           return DbColumnType.BLOB;
       } else if (t.contains("float")) {
            return DbColumnType.FLOAT;
        } else if (t.contains("double")) {
            return DbColumnType.DOUBLE;
        } else if (t.contains("date") || t.contains("time") || t.contains("year")) {
            switch (globalConfig.getDateType()) {
                case ONLY_DATE:
                    return DbColumnType.DATE;
                case SQL_PACK:
                    switch (t) {
                        case "date":
                            return DbColumnType.DATE_SQL;
                        case "time":
                            return DbColumnType.TIME;
                        case "year":
                            return DbColumnType.DATE_SQL;
                        default:
                            return DbColumnType.TIMESTAMP;
                    }
                case TIME_PACK:
                    switch (t) {
                        case "date":
                            return DbColumnType.LOCAL_DATE;
                        case "time":
                            return DbColumnType.LOCAL_TIME;
                        case "year":
                            return DbColumnType.YEAR;
                        default:
                            return DbColumnType.LOCAL_DATE_TIME;
                    }
            }
        }
        return DbColumnType.STRING;
    }
}
