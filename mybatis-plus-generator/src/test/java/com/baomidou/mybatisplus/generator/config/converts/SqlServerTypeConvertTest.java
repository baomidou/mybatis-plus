package com.baomidou.mybatisplus.generator.config.converts;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author lanjerry 2020/10/23.
 */
@Tag("unit")
public class SqlServerTypeConvertTest {

    @Test
    void processTypeConvertTest() {
        // 常用格式
        GlobalConfig globalConfig = GeneratorBuilder.globalConfig();
        SqlServerTypeConvert convert = SqlServerTypeConvert.INSTANCE;
        assertThat(convert.processTypeConvert(globalConfig, "char")).isEqualTo(STRING);
        assertThat(convert.processTypeConvert(globalConfig, "xml")).isEqualTo(STRING);
        assertThat(convert.processTypeConvert(globalConfig, "text")).isEqualTo(STRING);
        assertThat(convert.processTypeConvert(globalConfig, "bigint")).isEqualTo(LONG);
        assertThat(convert.processTypeConvert(globalConfig, "int")).isEqualTo(INTEGER);
        assertThat(convert.processTypeConvert(globalConfig, "bit")).isEqualTo(BOOLEAN);
        assertThat(convert.processTypeConvert(globalConfig, "decimal")).isEqualTo(DOUBLE);
        assertThat(convert.processTypeConvert(globalConfig, "numeric")).isEqualTo(DOUBLE);
        assertThat(convert.processTypeConvert(globalConfig, "money")).isEqualTo(BIG_DECIMAL);
        assertThat(convert.processTypeConvert(globalConfig, "binary")).isEqualTo(BYTE_ARRAY);
        assertThat(convert.processTypeConvert(globalConfig, "image")).isEqualTo(BYTE_ARRAY);
        assertThat(convert.processTypeConvert(globalConfig, "float")).isEqualTo(FLOAT);
        assertThat(convert.processTypeConvert(globalConfig, "real")).isEqualTo(FLOAT);

        // 日期格式
        globalConfig = GeneratorBuilder.globalConfigBuilder().dateType(DateType.SQL_PACK).build();
        assertThat(convert.processTypeConvert(globalConfig, "date")).isEqualTo(DATE_SQL);
        assertThat(convert.processTypeConvert(globalConfig, "time")).isEqualTo(TIME);
        assertThat(convert.processTypeConvert(globalConfig, "timestamp")).isEqualTo(TIMESTAMP);
        assertThat(convert.processTypeConvert(globalConfig, "datetime")).isEqualTo(TIMESTAMP);

        globalConfig = GeneratorBuilder.globalConfigBuilder().dateType(DateType.TIME_PACK).build();
        assertThat(convert.processTypeConvert(globalConfig, "date")).isEqualTo(LOCAL_DATE);
        assertThat(convert.processTypeConvert(globalConfig, "time")).isEqualTo(LOCAL_TIME);
        assertThat(convert.processTypeConvert(globalConfig, "timestamp")).isEqualTo(LOCAL_DATE_TIME);
        assertThat(convert.processTypeConvert(globalConfig, "datetime")).isEqualTo(LOCAL_DATE_TIME);

        globalConfig = GeneratorBuilder.globalConfigBuilder().dateType(DateType.ONLY_DATE).build();
        assertThat(convert.processTypeConvert(globalConfig, "date")).isEqualTo(DATE);
        assertThat(convert.processTypeConvert(globalConfig, "time")).isEqualTo(DATE);
        assertThat(convert.processTypeConvert(globalConfig, "timestamp")).isEqualTo(DATE);
        assertThat(convert.processTypeConvert(globalConfig, "datetime")).isEqualTo(DATE);
    }
}
