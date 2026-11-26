package com.baomidou.mybatisplus.generator.config.converts;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong
 */
@Tag("unit")
public class TypeConvertsTest {

    @Test
    void preservesPostgresTimeOfDay() {
        var converter = PostgreSqlTypeConvert.INSTANCE;
        var javaTime = new GlobalConfig.Builder().dateType(DateType.TIME_PACK).build();
        var jdbc = new GlobalConfig.Builder().dateType(DateType.SQL_PACK).build();
        for (String type : new String[]{"time", "time without time zone", "time(3) without time zone"}) {
            assertThat(converter.processTypeConvert(javaTime, type)).isEqualTo(DbColumnType.LOCAL_TIME);
            assertThat(converter.processTypeConvert(jdbc, type)).isEqualTo(DbColumnType.TIME);
        }
        assertThat(converter.processTypeConvert(javaTime, "timestamp(3) without time zone"))
                .isEqualTo(DbColumnType.LOCAL_DATE_TIME);
    }

    @Test
    void shouldGetTypeConvertForGaussDbTypes() {
        assertThat(TypeConverts.getTypeConvert(DbType.GAUSS)).isInstanceOf(DmTypeConvert.class);
        assertThat(TypeConverts.getTypeConvert(DbType.GAUSS_DB)).isInstanceOf(GaussDBSqlTypeConvert.class);
    }

}
