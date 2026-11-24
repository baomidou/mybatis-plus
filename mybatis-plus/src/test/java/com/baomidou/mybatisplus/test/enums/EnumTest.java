package com.baomidou.mybatisplus.test.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.DbTestSupport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-23
 */
@Tag("integration")
class EnumTest extends DbTestSupport<EnumTest.EntityMapper> {

    @Test
    void shouldHandleMultipleEnumTypes() {
        Long id = 1L;
        doTestAutoCommit(m -> {
            Entity entity = new Entity().setId(id).setEnumInt(EnumInt.ONE).setEnumStr(EnumStr.TWO)
                    .setEnumOrdinal(EnumOrdinal.TWO);
            int insert = m.insert(entity);
            assertThat(insert).as("插入成功").isEqualTo(1);
        });

        doTestAutoCommit(m -> {
            Entity entity = m.selectById(id);
            assertThat(entity).as("查出刚刚插入的数据").isNotNull();
            assertThat(entity.getEnumInt()).as("枚举正确").isEqualTo(EnumInt.ONE);
            assertThat(entity.getEnumStr()).as("枚举正确").isEqualTo(EnumStr.TWO);
            assertThat(entity.getEnumOrdinal()).as("枚举正确").isEqualTo(EnumOrdinal.TWO);
            entity.setEnumOrdinal(EnumOrdinal.ONE);
            m.updateById(entity);
        });

        doTest(m -> {
            Entity entity = m.findById(id);
            assertThat(entity).as("查出刚刚插入的数据").isNotNull();
            assertThat(entity.getEnumInt()).as("枚举正确").isEqualTo(EnumInt.ONE);
            assertThat(entity.getEnumStr()).as("枚举正确").isEqualTo(EnumStr.TWO);
            assertThat(entity.getEnumOrdinal()).as("枚举正确").isEqualTo(EnumOrdinal.ONE);
        });
    }

    @Override
    protected Consumer<Configuration> consumer() {
        return i -> i.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity", """
                CREATE TABLE IF NOT EXISTS entity (
                    id BIGINT(20) NOT NULL,
                    enum_int integer NULL DEFAULT NULL,
                    enum_str VARCHAR(30) NULL DEFAULT NULL,
                    enum_ordinal integer NULL DEFAULT NULL,
                    PRIMARY KEY (id)
                )
                """);
    }

    public interface EntityMapper extends BaseMapper<Entity> {

        Entity findById(Long id);
    }

    @Data
    @Accessors(chain = true)
    @TableName(autoResultMap = true)
    public static class Entity {

        private Long id;

        private EnumStr enumStr;

        private EnumInt enumInt;
        /**
         * 使用 EnumOrdinalTypeHandler
         */
        private EnumOrdinal enumOrdinal;
    }

    @Getter
    @AllArgsConstructor
    public enum EnumInt {

        ONE(1),
        TWO(2);

        @EnumValue
        private final int value;
    }

    @Getter
    @AllArgsConstructor
    public enum EnumStr {

        ONE("1"),
        TWO("2");

        @EnumValue
        private final String value;
    }

    @Getter
    public enum EnumOrdinal {
        ONE,
        TWO,
    }
}
