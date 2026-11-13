package com.baomidou.mybatisplus.test.optimisticlocker;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.baomidou.mybatisplus.test.DbTestSupport;
import org.apache.ibatis.plugin.Interceptor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-24
 */
@Tag("integration")
public class OptimisticLockerTest extends DbTestSupport<EntityMapper> {

    @Test
    void shouldApplyOptimisticLockingOnUpdate() {
        doTestAutoCommit(m -> {
            Entity entity = new Entity().setId(1L).setName("老王");
            int updateById = m.updateById(entity);
            assertThat(updateById).as("更新成功").isEqualTo(1);

            entity = m.selectById(1L);
            assertThat(entity.getVersion()).as("因为没给version赋值就没走插件").isEqualTo(0);

            updateById = m.updateById(entity);
            assertThat(updateById).as("更新成功").isEqualTo(1);

            entity = m.selectById(1L);
            assertThat(entity.getVersion()).as("因为version有值就走了插件").isEqualTo(1);
        });

        doTestAutoCommit(m -> {
            int update = m.update(new Entity().setName("老王"), Wrappers.<Entity>query().eq("id", 2));
            assertThat(update).as("更新成功").isEqualTo(1);

            Entity entity = m.selectById(2L);
            assertThat(entity.getVersion()).as("因为没给version赋值就没走插件").isEqualTo(0);

            update = m.update(new Entity().setName("老王").setVersion(0), Wrappers.<Entity>query().eq("id", 2));
            assertThat(update).as("更新成功").isEqualTo(1);

            entity = m.selectById(2L);
            assertThat(entity.getVersion()).as("因为version有值就走了插件").isEqualTo(1);
        });
    }

    @Test
    void shouldUpdateBatchWithOptimisticLock() {
        var entity1 = new Entity().setName("testBatch").setVersion(1);
        var entity2 = new Entity().setName("testBatch").setVersion(1);
        var entity3 = new Entity().setName("testBatch").setVersion(1);
        var entityList = List.of(entity1, entity2, entity3);
        doTest(mapper -> {
            assertThat(SqlHelper.retBool(mapper.insert(entityList))).isTrue();
            assertThat(SqlHelper.retBool(mapper.updateById(entityList))).isTrue();
            entity2.setVersion(6);
            assertThat(SqlHelper.retBool(mapper.updateById(entityList))).isFalse();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"query", "update", "lambda-query", "lambda-update", "apply", "nested"})
    void versionPredicateMustApplyToEveryOrBranch(String wrapperType) {
        jdbcTemplate.update("UPDATE entity SET version = 5 WHERE id = 1");
        AbstractWrapper<Entity, ?, ?> wrapper;
        switch (wrapperType) {
            case "update" :
                wrapper = Wrappers.<Entity>update().eq("id", 1).or().eq("id", 2).set("name", "after");
                break;
            case "lambda-query" :
                wrapper = Wrappers.<Entity>lambdaQuery().eq(Entity::getId, 1).or().eq(Entity::getId, 2);
                break;
            case "lambda-update" :
                wrapper = Wrappers.<Entity>lambdaUpdate().eq(Entity::getId, 1).or().eq(Entity::getId, 2)
                        .set(Entity::getName, "after");
                break;
            case "apply" :
                wrapper = Wrappers.<Entity>query().apply("id = {0} OR id = {1}", 1, 2);
                break;
            case "nested" :
                wrapper = Wrappers.<Entity>query().nested(w -> w.eq("id", 1).or().eq("id", 2));
                break;
            default :
                wrapper = Wrappers.<Entity>query().eq("id", 1).or().eq("id", 2);
        }
        wrapper.last("/* retain tail */");
        // Render once before interception to cover already cached segments and parameters.
        assertThat(wrapper.getSqlSegment()).contains("OR");
        doTest(mapper -> {
            Entity update = new Entity().setVersion(0);
            if (!wrapperType.endsWith("update")) {
                update.setName("after");
            }
            assertThat(mapper.update(update, wrapper)).isEqualTo(1);
            assertThat(mapper.selectById(1).getVersion()).isEqualTo(5);
            assertThat(mapper.selectById(1).getName()).isEqualTo("1");
            assertThat(mapper.selectById(2).getVersion()).isEqualTo(1);
            assertThat(mapper.selectById(2).getName()).isEqualTo("after");
            assertThat(mapper.selectById(3).getVersion()).isZero();
            assertThat(wrapper.getSqlSegment()).endsWith("/* retain tail */");
        });
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void versionPredicateAlsoFiltersEmptyWrappers(boolean nullWrapper) {
        jdbcTemplate.update("UPDATE entity SET version = 5 WHERE id = 1");
        doTest(mapper -> {
            assertThat(
                    mapper.update(
                            new Entity().setName("after").setVersion(0), nullWrapper ? null : Wrappers.<Entity>query()))
                    .isEqualTo(2);
            assertThat(mapper.selectById(1).getName()).isEqualTo("1");
            assertThat(mapper.selectById(1).getVersion()).isEqualTo(5);
            assertThat(mapper.selectById(2).getVersion()).isEqualTo(1);
            assertThat(mapper.selectById(3).getVersion()).isEqualTo(1);
        });
    }

    @Override
    protected List<Interceptor> interceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return Collections.singletonList(interceptor);
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity (id,name) values (1,'1'),(2,'2'),(3,'3')";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList(
                "drop table if exists entity",
                "CREATE TABLE IF NOT EXISTS entity (\n" + "id BIGINT(20) NOT NULL,\n"
                        + "name VARCHAR(30) NULL DEFAULT NULL,\n" + "version integer not NULL default 0,\n"
                        + "PRIMARY KEY (id)" + ")");
    }
}
