package com.baomidou.mybatisplus.generator.type;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class TypeRegistryTest {

    @ParameterizedTest
    @CsvSource({"NUMERIC,0,0", "DECIMAL,0,0", "NUMERIC,0,-127", "NUMERIC,4,-3", "DECIMAL,9,-2", "NUMERIC,18,-1"})
    void shouldKeepUnboundedOrNegativeScaleNumbersAsBigDecimal(JdbcType jdbcType, int precision, int scale) {
        TypeRegistry registry = new TypeRegistry(new GlobalConfig.Builder().build());
        assertThat(registry.getColumnType(numeric(jdbcType, precision, scale))).isEqualTo(DbColumnType.BIG_DECIMAL);
    }

    @ParameterizedTest
    @CsvSource({"4,0,SHORT", "5,0,INTEGER", "9,0,INTEGER", "10,0,LONG", "18,0,LONG", "19,0,BIG_DECIMAL",
            "4,2,BIG_DECIMAL"})
    void shouldPreserveBoundedNumericMappings(int precision, int scale, DbColumnType expected) {
        TypeRegistry registry = new TypeRegistry(new GlobalConfig.Builder().build());
        assertThat(registry.getColumnType(numeric(JdbcType.NUMERIC, precision, scale))).isEqualTo(expected);
    }

    private TableField.MetaInfo numeric(JdbcType type, int precision, int scale) {
        TableField.MetaInfo metadata = new TableField.MetaInfo(null, null);
        metadata.setJdbcType(type);
        metadata.setLength(precision);
        metadata.setScale(scale);
        return metadata;
    }
}
