package com.baomidou.mybatisplus.test.chainwrapper;

import com.baomidou.mybatisplus.test.BaseDbTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author miemie
 * @since 2020-06-23
 */
public class ChainWrapperTest extends BaseDbTest<EntityMapper> {

    @Test
    void test() {
        final String id = "id";
        doTest(i -> i.queryChain()
            .func(j -> j.isNotNull(id))
            .and(j -> j.isNotNull(id))
            .or(j -> j.isNotNull(id))
            .nested(j -> j.isNotNull(id))
            .not(j -> j.isNull(id))
            .list());
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity(id,name) values(1,'1'),(2,'2');";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity", "CREATE TABLE IF NOT EXISTS entity (" +
            "id BIGINT NOT NULL," +
            "name VARCHAR(30) NULL DEFAULT NULL," +
            "PRIMARY KEY (id))");
    }
}
