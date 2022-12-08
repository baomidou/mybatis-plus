package com.baomidou.mybatisplus.test.chainwrapper;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.baomidou.mybatisplus.test.BaseDbTest;

/**
 * @author VampireAchao
 * @since 2022-04-18
 */
public class ChainWrappersTest extends BaseDbTest<EntityMapper> {

    @Test
    void test() {
        final String id = "id";
        Entity entity = new Entity();
        Assertions.assertAll(() -> ChainWrappers.queryChain(entity.getClass())
            .func(j -> j.isNotNull(id))
            .func(entity.getId() != null, j -> j.eq("id", entity.getId()))// 不会npe,也不会加入sql
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

