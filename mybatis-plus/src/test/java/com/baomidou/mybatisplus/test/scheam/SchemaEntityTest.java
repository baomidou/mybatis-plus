package com.baomidou.mybatisplus.test.scheam;

import com.baomidou.mybatisplus.test.BaseDbTest;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * @author nieqiurong
 */
public class SchemaEntityTest extends BaseDbTest<SchemaEntityMapper> {

    @Test
    void test() {
        doTest(mapper -> {
            SchemaEntity schemaEntity = mapper.selectById(1);
            Assertions.assertNotNull(schemaEntity);
        });
    }

    @Override
    protected Consumer<Configuration> consumer() {
        return configuration -> {
            Properties properties = new Properties();
            properties.put("my.schema", "public");
            properties.put("my.tableName", "SCHEMA_ENTITY");
            configuration.setVariables(properties);
        };
    }

    @Override
    protected String tableDataSql() {
        return "insert into SCHEMA_ENTITY(id,name) values(1,'1'),(2,'2');";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists SCHEMA_ENTITY", "CREATE TABLE IF NOT EXISTS SCHEMA_ENTITY (" +
            "id BIGINT NOT NULL," +
            "name VARCHAR(30) NULL DEFAULT NULL," +
            "PRIMARY KEY (id))");
    }
}
