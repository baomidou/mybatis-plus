package com.baomidou.mybatisplus.test.chainwrapper;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.baomidou.mybatisplus.test.BaseDbTest;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
        Entity entity = new Entity();
        doTest(i -> i.queryChain()
            .func(j -> j.isNotNull(id))
            .func(entity.getId() != null, j -> j.eq("id", entity.getId()))// 不会npe,也不会加入sql
            .and(j -> j.isNotNull(id))
            .or(j -> j.isNotNull(id))
            .nested(j -> j.isNotNull(id))
            .not(j -> j.isNull(id))
            .list());
        doTest(i -> i.queryChain().groupBy("id").list());
        doTest(i -> i.queryChain().groupBy(List.of("id")).list());
        doTest(i -> i.queryChain().groupBy(List.of("id", "name")).list());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void oneWithNoResults(boolean throwEx) {
        doTest(mapper -> {
            Assertions.assertNull(mapper.queryChain().eq("id", 0).one(throwEx));
            Assertions.assertNull(ChainWrappers.lambdaQueryChain(mapper).eq(Entity::getId, 0).one(throwEx));
        });
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void oneWithSingleResult(boolean throwEx) {
        doTest(mapper -> {
            Assertions.assertEquals(Long.valueOf(2), mapper.queryChain().eq("id", 2).one(throwEx).getId());
            Assertions.assertEquals(Long.valueOf(2),
                ChainWrappers.lambdaQueryChain(mapper).eq(Entity::getId, 2).one(throwEx).getId());
        });
    }

    @Test
    void oneWithMultipleResultsThrowsWhenRequested() {
        doTest(mapper -> {
            Assertions.assertThrows(TooManyResultsException.class, () -> mapper.queryChain().one(true));
            Assertions.assertThrows(TooManyResultsException.class,
                () -> ChainWrappers.lambdaQueryChain(mapper).one(true));
        });
    }

    @Test
    void oneWithMultipleResultsReturnsFirstWhenAllowed() {
        doTest(mapper -> {
            Assertions.assertEquals(Long.valueOf(2), mapper.queryChain().orderByDesc("id").one(false).getId());
            Assertions.assertEquals(Long.valueOf(2),
                ChainWrappers.lambdaQueryChain(mapper).orderByDesc(Entity::getId).one(false).getId());
        });
    }

    @Test
    void oneWithoutArgumentKeepsExistingBehavior() {
        doTest(mapper -> {
            Assertions.assertNull(mapper.queryChain().eq("id", 0).one());
            Assertions.assertEquals(Long.valueOf(2), mapper.queryChain().eq("id", 2).one().getId());
            Assertions.assertThrows(TooManyResultsException.class, () -> mapper.queryChain().one());
            Assertions.assertNull(ChainWrappers.lambdaQueryChain(mapper).eq(Entity::getId, 0).one());
            Assertions.assertEquals(Long.valueOf(2),
                ChainWrappers.lambdaQueryChain(mapper).eq(Entity::getId, 2).one().getId());
            Assertions.assertThrows(TooManyResultsException.class,
                () -> ChainWrappers.lambdaQueryChain(mapper).one());
        });
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
