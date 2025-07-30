package com.baomidou.mybatisplus.generator.config.converts;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.BIG_DECIMAL;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.BOOLEAN;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.DATE;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.DATE_SQL;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.FLOAT;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.INTEGER;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.LOCAL_DATE;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.LOCAL_TIME;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.LONG;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.OBJECT;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.STRING;
import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.TIME;

/**
 * @author nieqiurong
 */
public class GaussDBSqlTypeConvertTest {

    @Test
    void test() {
        GlobalConfig globalConfig = GeneratorBuilder.globalConfig();
        GaussDBSqlTypeConvert convert = GaussDBSqlTypeConvert.INSTANCE;
        Assertions.assertEquals(STRING, convert.processTypeConvert(globalConfig, "char"));
        Assertions.assertEquals(STRING, convert.processTypeConvert(globalConfig, "xml"));
        Assertions.assertEquals(STRING, convert.processTypeConvert(globalConfig, "text"));

        Assertions.assertEquals(OBJECT, convert.processTypeConvert(globalConfig, "uuid"));
        Assertions.assertEquals(OBJECT, convert.processTypeConvert(globalConfig, "json"));

        Assertions.assertEquals(LONG, convert.processTypeConvert(globalConfig, "bigint"));
        Assertions.assertEquals(INTEGER, convert.processTypeConvert(globalConfig, "int"));
        Assertions.assertEquals(BOOLEAN, convert.processTypeConvert(globalConfig, "bit"));
        Assertions.assertEquals(BIG_DECIMAL, convert.processTypeConvert(globalConfig, "decimal"));
        Assertions.assertEquals(BIG_DECIMAL, convert.processTypeConvert(globalConfig, "numeric"));
        Assertions.assertEquals(FLOAT, convert.processTypeConvert(globalConfig, "float"));

        globalConfig = GeneratorBuilder.globalConfigBuilder().dateType(DateType.SQL_PACK).build();
        Assertions.assertEquals(DATE_SQL, convert.processTypeConvert(globalConfig, "date"));
        Assertions.assertEquals(TIME, convert.processTypeConvert(globalConfig, "time"));

        globalConfig = GeneratorBuilder.globalConfigBuilder().dateType(DateType.TIME_PACK).build();
        Assertions.assertEquals(LOCAL_DATE, convert.processTypeConvert(globalConfig, "date"));
        Assertions.assertEquals(LOCAL_TIME, convert.processTypeConvert(globalConfig, "time"));

        globalConfig = GeneratorBuilder.globalConfigBuilder().dateType(DateType.ONLY_DATE).build();
        Assertions.assertEquals(DATE, convert.processTypeConvert(globalConfig, "date"));
        Assertions.assertEquals(DATE, convert.processTypeConvert(globalConfig, "time"));
    }

}
