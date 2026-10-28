package com.baomidou.mybatisplus.generator.config.converts;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import org.junit.jupiter.api.Tag;
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
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong
 */
@Tag("unit")
public class GaussDBSqlTypeConvertTest {

    @Test
    void shouldConvertGaussDbSqlTypesToFieldTypes() {
        GlobalConfig globalConfig = GeneratorBuilder.globalConfig();
        GaussDBSqlTypeConvert convert = GaussDBSqlTypeConvert.INSTANCE;
        assertThat(convert.processTypeConvert(globalConfig, "char")).isEqualTo(STRING);
        assertThat(convert.processTypeConvert(globalConfig, "xml")).isEqualTo(STRING);
        assertThat(convert.processTypeConvert(globalConfig, "text")).isEqualTo(STRING);

        assertThat(convert.processTypeConvert(globalConfig, "uuid")).isEqualTo(OBJECT);
        assertThat(convert.processTypeConvert(globalConfig, "json")).isEqualTo(OBJECT);

        assertThat(convert.processTypeConvert(globalConfig, "bigint")).isEqualTo(LONG);
        assertThat(convert.processTypeConvert(globalConfig, "int")).isEqualTo(INTEGER);
        assertThat(convert.processTypeConvert(globalConfig, "bit")).isEqualTo(BOOLEAN);
        assertThat(convert.processTypeConvert(globalConfig, "decimal")).isEqualTo(BIG_DECIMAL);
        assertThat(convert.processTypeConvert(globalConfig, "numeric")).isEqualTo(BIG_DECIMAL);
        assertThat(convert.processTypeConvert(globalConfig, "float")).isEqualTo(FLOAT);

        globalConfig = GeneratorBuilder.globalConfigBuilder().dateType(DateType.SQL_PACK).build();
        assertThat(convert.processTypeConvert(globalConfig, "date")).isEqualTo(DATE_SQL);
        assertThat(convert.processTypeConvert(globalConfig, "time")).isEqualTo(TIME);

        globalConfig = GeneratorBuilder.globalConfigBuilder().dateType(DateType.TIME_PACK).build();
        assertThat(convert.processTypeConvert(globalConfig, "date")).isEqualTo(LOCAL_DATE);
        assertThat(convert.processTypeConvert(globalConfig, "time")).isEqualTo(LOCAL_TIME);

        globalConfig = GeneratorBuilder.globalConfigBuilder().dateType(DateType.ONLY_DATE).build();
        assertThat(convert.processTypeConvert(globalConfig, "date")).isEqualTo(DATE);
        assertThat(convert.processTypeConvert(globalConfig, "time")).isEqualTo(DATE);
    }

}
