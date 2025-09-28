package com.baomidou.mybatisplus.test.uuid;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.test.BaseDbTest;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class UUIDLogicEntityTest extends BaseDbTest<UUIDLogicEntityMapper> {

    @Test
    void test() {
        doTest(m -> {
            var uuidEntity = new UUIDLogicEntity();
            uuidEntity.setId(UUID.randomUUID());
            uuidEntity.setName("test1");
            uuidEntity.setDeleted(false);
            Assertions.assertEquals(1, m.insert(uuidEntity));
            Assertions.assertNotNull(m.selectById(uuidEntity.getId()));
            Assertions.assertEquals(1, m.deleteById(uuidEntity));
        });

        doTest(m -> {
            var uuidEntity = new UUIDLogicEntity();
            uuidEntity.setId(UUID.randomUUID());
            uuidEntity.setName("test2");
            uuidEntity.setDeleted(false);
            Assertions.assertEquals(1, m.insert(uuidEntity));
            Assertions.assertEquals(1, m.deleteByIds(List.of(uuidEntity.getId())));
        });

        doTest(m -> {
            var uuidEntity = new UUIDLogicEntity();
            uuidEntity.setId(UUID.randomUUID());
            uuidEntity.setName("test3");
            uuidEntity.setDeleted(false);
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
        doTest(m -> Assertions.assertDoesNotThrow(()-> m.deleteById(new UUIDLogicEntity(){})));
        // TODO 下面三种类型无法转为UUID，看是否增加一个类型转换器让用户能注册处理自己的类型转换
//        doTest(m -> Assertions.assertDoesNotThrow(()-> m.deleteById(UUID.randomUUID().toString())));
//        doTest(m -> Assertions.assertDoesNotThrow(()-> m.deleteById(new DeleteByIdDto<>(UUID.randomUUID()))));
//        doTest(m -> Assertions.assertDoesNotThrow(()-> m.deleteById(new DeleteByIdDto<>(UUID.randomUUID().toString()))));
    }


    @Override
    protected Consumer<Configuration> consumer() {
        return configuration -> configuration.getTypeHandlerRegistry().register(UUIDTypeHandler.class);
    }

    @Override
    protected GlobalConfig globalConfig() {
        return super.globalConfig().setMetaObjectHandler(new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {

            }

            @Override
            public void updateFill(MetaObject metaObject) {
                metaObject.setValue("deleteBy", "baomidou");
            }
        });
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity values('0824eb71-e124-5ba1-56b9-87185d91f309','test',null, false)";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity",
            "CREATE TABLE IF NOT EXISTS entity (\n" +
                "id VARCHAR(50) NOT NULL,\n" +
                "name VARCHAR(30) NULL DEFAULT NULL,\n" +
                "delete_by VARCHAR(30) NULL DEFAULT NULL," +
                "deleted BOOLEAN NOT NULL DEFAULT false," +
                "PRIMARY KEY (id)" +
                ")");
    }
}
