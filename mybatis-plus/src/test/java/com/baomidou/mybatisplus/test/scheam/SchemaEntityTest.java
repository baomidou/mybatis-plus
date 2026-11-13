package com.baomidou.mybatisplus.test.scheam;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.DbTestSupport;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong
 */
@Tag("integration")
public class SchemaEntityTest extends DbTestSupport<SchemaEntityTest.SchemaEntityMapper> {

    @Test
    void shouldQueryEntityWithSchemaVariable() {
        doTest(mapper -> {
            SchemaEntity schemaEntity = mapper.selectById(1);
            assertThat(schemaEntity).isNotNull();
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
        return Arrays.asList(
                "drop table if exists SCHEMA_ENTITY", "CREATE TABLE IF NOT EXISTS SCHEMA_ENTITY ("
                        + "id BIGINT NOT NULL," + "name VARCHAR(30) NULL DEFAULT NULL," + "PRIMARY KEY (id))");
    }

    @Mapper
    public interface SchemaEntityMapper extends BaseMapper<SchemaEntity> {

    }

    @Data
    @TableName(value = "${my.tableName}", schema = "${my.schema}")
    public static class SchemaEntity {

        private Long id;

        private String name;

    }
}
