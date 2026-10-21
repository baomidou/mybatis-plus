package com.baomidou.mybatisplus.test.uuid;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.spring.toolkit.SqlRunner;
import com.baomidou.mybatisplus.test.DbTestSupport;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class UUIDEntityTest extends DbTestSupport<UUIDEntityMapper> {

    @Test
    void test() {
        doTest(m -> {
            var uuidEntity = new UUIDEntity();
            uuidEntity.setId(UUID.randomUUID());
            uuidEntity.setName("test1");
            assertThat(m.insert(uuidEntity)).isEqualTo(1);
            assertThat(m.selectById(uuidEntity.getId())).isNotNull();
            assertThat(m.deleteById(uuidEntity)).isEqualTo(1);
        });

        doTest(m -> {
            var uuidEntity = new UUIDEntity();
            uuidEntity.setId(UUID.randomUUID());
            uuidEntity.setName("test2");
            assertThat(m.insert(uuidEntity)).isEqualTo(1);
            assertThat(m.deleteByIds(List.of(uuidEntity.getId()))).isEqualTo(1);
        });

        doTest(m -> {
            var uuidEntity = new UUIDEntity();
            uuidEntity.setId(UUID.randomUUID());
            uuidEntity.setName("test3");
            assertThat(m.insert(uuidEntity)).isEqualTo(1);
            assertThat(m.deleteByIds(List.of(uuidEntity))).isEqualTo(1);
        });

        doTest(
                m -> assertThatCode(
                        () -> m.deleteByIds(
                                List.of(
                                        UUID.randomUUID().toString(), UUID.randomUUID(), 123, 321L,
                                        Map.of("id", UUID.randomUUID()), Map.of("id", UUID.randomUUID().toString()),
                                        new DeleteByIdDto<>(UUID.randomUUID()),
                                        new DeleteByIdDto<>(UUID.randomUUID().toString()))))
                        .doesNotThrowAnyException());

        doTest(m -> assertThatCode(() -> m.deleteById(UUID.randomUUID())).doesNotThrowAnyException());
        doTest(m -> assertThatCode(() -> m.deleteById(UUID.randomUUID().toString())).doesNotThrowAnyException());
        doTest(m -> assertThatCode(() -> m.deleteById(new UUIDEntity() {
        })).doesNotThrowAnyException());
        doTest(
                m -> assertThatCode(() -> m.deleteById(new DeleteByIdDto<>(UUID.randomUUID())))
                        .doesNotThrowAnyException());
        doTest(
                m -> assertThatCode(() -> m.deleteById(new DeleteByIdDto<>(UUID.randomUUID().toString())))
                        .doesNotThrowAnyException());
        doTest(
                m -> assertThatCode(
                        () -> SqlRunner.db(UUIDEntity.class)
                                .delete("delete from entity where id = {0}", UUID.randomUUID()))
                        .doesNotThrowAnyException());
    }

    @Override
    protected Consumer<Configuration> consumer() {
        return configuration -> configuration.getTypeHandlerRegistry().register(UUIDTypeHandler.class);
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity values('0824eb71-e124-5ba1-56b9-87185d91f309','test')";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList(
                "drop table if exists entity", "CREATE TABLE IF NOT EXISTS entity (\n" + "id VARCHAR(50) NOT NULL,\n"
                        + "name VARCHAR(30) NULL DEFAULT NULL,\n" + "PRIMARY KEY (id)" + ")");
    }

    @Override
    protected GlobalConfig globalConfig() {
        return super.globalConfig().setEnableSqlRunner(true);
    }
}
