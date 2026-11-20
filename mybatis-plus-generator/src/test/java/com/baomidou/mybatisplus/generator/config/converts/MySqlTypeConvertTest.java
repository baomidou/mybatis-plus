package com.baomidou.mybatisplus.generator.config.converts;

import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class MySqlTypeConvertTest {

    @ParameterizedTest
    @CsvSource({"BIT,BOOLEAN", "bit(1),BOOLEAN", "bit(2),BYTE_ARRAY", "BIT(8),BYTE_ARRAY", "bit(16),BYTE_ARRAY",
            "bit(64),BYTE_ARRAY", "tinyint(1),BOOLEAN"})
    void mapsBitColumnsByWidth(String columnType, DbColumnType expected) {
        assertThat(MySqlTypeConvert.INSTANCE.processTypeConvert(GeneratorBuilder.globalConfig(), columnType))
                .isEqualTo(expected);
    }
}
