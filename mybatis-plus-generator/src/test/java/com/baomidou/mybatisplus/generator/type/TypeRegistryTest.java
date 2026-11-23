package com.baomidou.mybatisplus.generator.type;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
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

    @ParameterizedTest
    @CsvSource({"TINYINT,TINYINT UNSIGNED,tinyint(1) unsigned,INTEGER",
            "SMALLINT,SMALLINT UNSIGNED,smallint(5) unsigned,INTEGER",
            "INTEGER,MEDIUMINT UNSIGNED,mediumint unsigned,INTEGER",
            "INTEGER,INT UNSIGNED,int(10) unsigned zerofill,LONG", "BIGINT,BIGINT UNSIGNED,bigint unsigned,BIG_INTEGER",
            "INTEGER,INT,int(10),INTEGER", "BIGINT,BIGINT,bigint(20),LONG"})
    void mapsFullMySqlIntegerRange(JdbcType jdbcType, String typeName, String columnDefinition, DbColumnType expected) {
        GlobalConfig config = new GlobalConfig.Builder().build();
        TableField.MetaInfo metadata = new TableField.MetaInfo(null, null);
        metadata.setJdbcType(jdbcType);
        metadata.setTypeName(typeName);

        assertThat(new TypeRegistry(config).getColumnType(metadata)).isEqualTo(expected);
        assertThat(MySqlTypeConvert.INSTANCE.processTypeConvert(config, columnDefinition)).isEqualTo(expected);
    }

    private TableField.MetaInfo numeric(JdbcType type, int precision, int scale) {
        TableField.MetaInfo metadata = new TableField.MetaInfo(null, null);
        metadata.setJdbcType(type);
        metadata.setLength(precision);
        metadata.setScale(scale);
        return metadata;
    }
}
