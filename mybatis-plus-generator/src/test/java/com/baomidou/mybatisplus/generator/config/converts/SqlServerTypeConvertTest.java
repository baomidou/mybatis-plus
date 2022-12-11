package com.baomidou.mybatisplus.generator.config.converts;

import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.*;

import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;

/**
 * @author lanjerry 2020/10/23.
 */
public class SqlServerTypeConvertTest {

    @Test
    void processTypeConvertTest() {
        // 常用格式
        GlobalConfig globalConfig = GeneratorBuilder.globalConfig();
        SqlServerTypeConvert convert = SqlServerTypeConvert.INSTANCE;
        Assertions.assertEquals(STRING, convert.processTypeConvert(globalConfig, "char"));
        Assertions.assertEquals(STRING, convert.processTypeConvert(globalConfig, "xml"));
        Assertions.assertEquals(STRING, convert.processTypeConvert(globalConfig, "text"));
        Assertions.assertEquals(LONG, convert.processTypeConvert(globalConfig, "bigint"));
        Assertions.assertEquals(INTEGER, convert.processTypeConvert(globalConfig, "int"));
        Assertions.assertEquals(BOOLEAN, convert.processTypeConvert(globalConfig, "bit"));
        Assertions.assertEquals(DOUBLE, convert.processTypeConvert(globalConfig, "decimal"));
        Assertions.assertEquals(DOUBLE, convert.processTypeConvert(globalConfig, "numeric"));
        Assertions.assertEquals(BIG_DECIMAL, convert.processTypeConvert(globalConfig, "money"));
        Assertions.assertEquals(BYTE_ARRAY, convert.processTypeConvert(globalConfig, "binary"));
        Assertions.assertEquals(BYTE_ARRAY, convert.processTypeConvert(globalConfig, "image"));
        Assertions.assertEquals(FLOAT, convert.processTypeConvert(globalConfig, "float"));
        Assertions.assertEquals(FLOAT, convert.processTypeConvert(globalConfig, "real"));

        // 日期格式
        globalConfig = GeneratorBuilder.globalConfigBuilder().dateType(DateType.SQL_PACK).build();
        Assertions.assertEquals(DATE_SQL, convert.processTypeConvert(globalConfig, "date"));
        Assertions.assertEquals(TIME, convert.processTypeConvert(globalConfig, "time"));
        Assertions.assertEquals(TIMESTAMP, convert.processTypeConvert(globalConfig, "timestamp"));
        Assertions.assertEquals(TIMESTAMP, convert.processTypeConvert(globalConfig, "datetime"));

        globalConfig = GeneratorBuilder.globalConfigBuilder().dateType(DateType.TIME_PACK).build();
        Assertions.assertEquals(LOCAL_DATE, convert.processTypeConvert(globalConfig, "date"));
        Assertions.assertEquals(LOCAL_TIME, convert.processTypeConvert(globalConfig, "time"));
        Assertions.assertEquals(LOCAL_DATE_TIME, convert.processTypeConvert(globalConfig, "timestamp"));
        Assertions.assertEquals(LOCAL_DATE_TIME, convert.processTypeConvert(globalConfig, "datetime"));

        globalConfig = GeneratorBuilder.globalConfigBuilder().dateType(DateType.ONLY_DATE).build();
        Assertions.assertEquals(DATE, convert.processTypeConvert(globalConfig, "date"));
        Assertions.assertEquals(DATE, convert.processTypeConvert(globalConfig, "time"));
        Assertions.assertEquals(DATE, convert.processTypeConvert(globalConfig, "timestamp"));
        Assertions.assertEquals(DATE, convert.processTypeConvert(globalConfig, "datetime"));
    }
}
