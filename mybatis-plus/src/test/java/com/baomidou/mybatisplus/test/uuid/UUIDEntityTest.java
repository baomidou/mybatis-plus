package com.baomidou.mybatisplus.test.uuid;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.baomidou.mybatisplus.test.BaseDbTest;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class UUIDEntityTest extends BaseDbTest<UUIDEntityMapper> {

    @Test
    void test() {
        doTest(m -> {
            var uuidEntity = new UUIDEntity();
            uuidEntity.setId(UUID.randomUUID());
            uuidEntity.setName("test1");
            Assertions.assertEquals(1, m.insert(uuidEntity));
            Assertions.assertNotNull(m.selectById(uuidEntity.getId()));
            Assertions.assertEquals(1, m.deleteById(uuidEntity));
        });

        doTest(m -> {
            var uuidEntity = new UUIDEntity();
            uuidEntity.setId(UUID.randomUUID());
            uuidEntity.setName("test2");
            Assertions.assertEquals(1, m.insert(uuidEntity));
            Assertions.assertEquals(1, m.deleteByIds(List.of(uuidEntity.getId())));
        });

        doTest(m -> {
            var uuidEntity = new UUIDEntity();
            uuidEntity.setId(UUID.randomUUID());
            uuidEntity.setName("test3");
            Assertions.assertEquals(1, m.insert(uuidEntity));
            Assertions.assertEquals(1, m.deleteByIds(List.of(uuidEntity)));
        });

        doTest(m -> Assertions.assertDoesNotThrow(()-> m.deleteByIds(
            List.of(
                UUID.randomUUID().toString(),
                UUID.randomUUID(), 123, 321L,
                Map.of("id", UUID.randomUUID()),
                Map.of("id", UUID.randomUUID().toString()),
                new DeleteByIdDto<>(UUID.randomUUID()),
                new DeleteByIdDto<>(UUID.randomUUID().toString())
            ))));

        doTest(m -> Assertions.assertDoesNotThrow(()-> m.deleteById(UUID.randomUUID())));
        doTest(m -> Assertions.assertDoesNotThrow(()-> m.deleteById(UUID.randomUUID().toString())));
        doTest(m -> Assertions.assertDoesNotThrow(()-> m.deleteById(new UUIDEntity(){})));
        doTest(m -> Assertions.assertDoesNotThrow(()-> m.deleteById(new DeleteByIdDto<>(UUID.randomUUID()))));
        doTest(m -> Assertions.assertDoesNotThrow(()-> m.deleteById(new DeleteByIdDto<>(UUID.randomUUID().toString()))));
        doTest(m -> Assertions.assertDoesNotThrow(()-> SqlRunner.db(UUIDEntity.class).delete("delete from entity where id = {0}", UUID.randomUUID())));
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
        return Arrays.asList("drop table if exists entity",
            "CREATE TABLE IF NOT EXISTS entity (\n" +
                "id VARCHAR(50) NOT NULL,\n" +
                "name VARCHAR(30) NULL DEFAULT NULL,\n" +
                "PRIMARY KEY (id)" +
                ")");
    }

    @Override
    protected GlobalConfig globalConfig() {
        return super.globalConfig().setEnableSqlRunner(true);
    }
}
