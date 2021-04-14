package com.baomidou.mybatisplus.test.enums;

import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.test.BaseDbTest;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-23
 */
class EnumTest extends BaseDbTest<EntityMapper> {

    @Test
    void test() {
        Long id = 1L;
        doTestAutoCommit(m -> {
            Entity entity = new Entity().setId(id).setEnumInt(EnumInt.ONE).setEnumStr(EnumStr.TWO).setEnumOrdinal(EnumOrdinal.TWO);
            int insert = m.insert(entity);
            assertThat(insert).as("插入成功").isEqualTo(1);
        });

        doTest(m -> {
            Entity entity = m.selectById(id);
            assertThat(entity).as("查出刚刚插入的数据").isNotNull();
            assertThat(entity.getEnumInt()).as("枚举正确").isEqualTo(EnumInt.ONE);
            assertThat(entity.getEnumStr()).as("枚举正确").isEqualTo(EnumStr.TWO);
            assertThat(entity.getEnumOrdinal()).as("枚举正确").isEqualTo(EnumOrdinal.TWO);
            entity.setEnumOrdinal(EnumOrdinal.ONE);
            m.updateById(entity);
        });
    }

    @Override
    protected Consumer<Configuration> consumer() {
        return i -> i.setDefaultEnumTypeHandler(MybatisEnumTypeHandler.class);
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity",
            "CREATE TABLE IF NOT EXISTS entity (\n" +
                "id BIGINT(20) NOT NULL,\n" +
                "enum_int integer NULL DEFAULT NULL,\n" +
                "enum_str VARCHAR(30) NULL DEFAULT NULL,\n" +
                "enum_ordinal integer NULL DEFAULT NULL,\n" +
                "PRIMARY KEY (id)" +
                ")");
    }
}
