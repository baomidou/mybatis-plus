package com.baomidou.mybatisplus.test.chainwrapper;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.baomidou.mybatisplus.test.DbTestSupport;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author miemie
 * @since 2020-06-23
 */
public class ChainWrapperTest extends DbTestSupport<EntityMapper> {

    /**
     * 链式包装：func 的条件为 false 时既不 npe 也不留参数，groupBy 传单个值和传集合拼出的片段一致。
     */
    @Test
    void test() {
        final String id = "id";
        Entity entity = new Entity();
        doTest(i -> {
            var chain = i.queryChain().func(j -> j.isNotNull(id))
                    // entity.getId() 为 null，条件不成立：不进 SQL，也不会留下占位符参数
                    .func(entity.getId() != null, j -> j.eq("id", entity.getId())).and(j -> j.isNotNull(id))
                    .or(j -> j.isNotNull(id)).nested(j -> j.isNotNull(id)).not(j -> j.isNull(id));
            assertThat(chain.getWrapper().getTargetSql()).isEqualTo(
                    "(id IS NOT NULL AND (id IS NOT NULL)"
                            + " OR (id IS NOT NULL) AND (id IS NOT NULL) AND NOT (id IS NULL))");
            assertThat(chain.getWrapper().getParamNameValuePairs()).isEmpty();
            assertThat(chain.list()).hasSize(2);
            assertThat(i.queryChain().groupBy("id").getWrapper().getSqlSegment()).isEqualTo(" GROUP BY id");
            assertThat(i.queryChain().groupBy(List.of("id")).getWrapper().getSqlSegment()).isEqualTo(" GROUP BY id");
            assertThat(i.queryChain().groupBy(List.of("id", "name")).getWrapper().getSqlSegment())
                    .isEqualTo(" GROUP BY id,name");
            assertThat(i.queryChain().groupBy(List.of("id", "name")).list()).hasSize(2);
        });
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void oneWithNoResults(boolean throwEx) {
        doTest(mapper -> {
            assertThat(mapper.queryChain().eq("id", 0).one(throwEx)).isNull();
            assertThat(ChainWrappers.lambdaQueryChain(mapper).eq(Entity::getId, 0).one(throwEx)).isNull();
        });
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void oneWithSingleResult(boolean throwEx) {
        doTest(mapper -> {
            assertThat(mapper.queryChain().eq("id", 2).one(throwEx).getId()).isEqualTo(Long.valueOf(2));
            assertThat(ChainWrappers.lambdaQueryChain(mapper).eq(Entity::getId, 2).one(throwEx).getId())
                    .isEqualTo(Long.valueOf(2));
        });
    }

    @Test
    void oneWithMultipleResultsThrowsWhenRequested() {
        doTest(mapper -> {
            assertThatThrownBy(() -> mapper.queryChain().one(true)).isInstanceOf(TooManyResultsException.class);
            assertThatThrownBy(() -> ChainWrappers.lambdaQueryChain(mapper).one(true))
                    .isInstanceOf(TooManyResultsException.class);
        });
    }

    @Test
    void oneWithMultipleResultsReturnsFirstWhenAllowed() {
        doTest(mapper -> {
            assertThat(mapper.queryChain().orderByDesc("id").one(false).getId()).isEqualTo(Long.valueOf(2));
            assertThat(ChainWrappers.lambdaQueryChain(mapper).orderByDesc(Entity::getId).one(false).getId())
                    .isEqualTo(Long.valueOf(2));
        });
    }

    @Test
    void oneWithoutArgumentKeepsExistingBehavior() {
        doTest(mapper -> {
            assertThat(mapper.queryChain().eq("id", 0).one()).isNull();
            assertThat(mapper.queryChain().eq("id", 2).one().getId()).isEqualTo(Long.valueOf(2));
            assertThatThrownBy(() -> mapper.queryChain().one()).isInstanceOf(TooManyResultsException.class);
            assertThat(ChainWrappers.lambdaQueryChain(mapper).eq(Entity::getId, 0).one()).isNull();
            assertThat(ChainWrappers.lambdaQueryChain(mapper).eq(Entity::getId, 2).one().getId())
                    .isEqualTo(Long.valueOf(2));
            assertThatThrownBy(() -> ChainWrappers.lambdaQueryChain(mapper).one())
                    .isInstanceOf(TooManyResultsException.class);
        });
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity(id,name) values(1,'1'),(2,'2');";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList(
                "drop table if exists entity", "CREATE TABLE IF NOT EXISTS entity (" + "id BIGINT NOT NULL,"
                        + "name VARCHAR(30) NULL DEFAULT NULL," + "PRIMARY KEY (id))");
    }
}
