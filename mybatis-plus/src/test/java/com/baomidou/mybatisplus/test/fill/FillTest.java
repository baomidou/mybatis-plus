package com.baomidou.mybatisplus.test.fill;

import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.MybatisBatchUtils;
import com.baomidou.mybatisplus.test.DbTestSupport;
import org.apache.ibatis.reflection.MetaObject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2023年9月29日
 */
@Tag("integration")
public class FillTest extends DbTestSupport<FillMapper> {

    @Test
    void shouldFillOnInsert() {
        doTest(mapper -> {
            var entity = new FillEntity();
            mapper.insert(entity);
            assertEntity(entity, "insertAdmin", 1);
        });
    }

    @Test
    void shouldFillOnInsertBatch1() {
        doTest(mapper -> {
            var entityList = new ArrayList<>(Arrays.asList(new FillEntity(), new FillEntity()));
            mapper.insertBatch1(entityList);
            for (FillEntity entity : entityList) {
                assertEntity(entity, "insertAdmin", 1);
            }
        });
    }

    @Test
    void shouldFillOnInsertBatch2() {
        doTest(mapper -> {
            var entityList = new ArrayList<>(Arrays.asList(new FillEntity(), new FillEntity()));
            mapper.insertBatch2(entityList);
            for (FillEntity entity : entityList) {
                assertEntity(entity, "insertAdmin", 1);
            }
        });
    }

    @Test
    void shouldFillOnInsertBatch3() {
        doTest(mapper -> {
            var entityList = new ArrayList<>(Arrays.asList(new FillEntity(), new FillEntity()));
            mapper.insertBatch3(entityList);
            for (FillEntity entity : entityList) {
                assertEntity(entity, "insertAdmin", 1);
            }
        });
    }

    @Test
    void shouldFillOnInsertBatch4() {
        doTest(mapper -> {
            var entityList = new ArrayList<>(Arrays.asList(new FillEntity(), new FillEntity()));
            mapper.insertBatch4(entityList, "", "", "");
            for (FillEntity entity : entityList) {
                assertEntity(entity, "insertAdmin", 1);
            }
        });
    }

    @Test
    void shouldFillOnMybatisBatch() {
        var entityList = new ArrayList<>(Arrays.asList(new FillEntity(), new FillEntity()));
        var method = new MybatisBatch.Method<FillEntity>(FillMapper.class);
        MybatisBatchUtils.execute(sqlSessionFactory, entityList, method.insert());
        for (FillEntity entity : entityList) {
            assertEntity(entity, "insertAdmin", 1);
        }
    }

    void assertEntity(FillEntity entity, String name, int seq) {
        assertThat(name).isEqualTo(entity.getName());
        assertThat(seq).isEqualTo(entity.getSeq());
    }

    @Override
    protected GlobalConfig globalConfig() {
        GlobalConfig globalConfig = super.globalConfig();
        globalConfig.setMetaObjectHandler(new MetaObjectHandler() {

            @Override
            public void insertFill(MetaObject metaObject) {
                var entity = (FillEntity) metaObject.getOriginalObject();
                entity.setName("insertAdmin");
                entity.addSeq();
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                var entity = (FillEntity) metaObject.getOriginalObject();
                entity.setName("updateAdmin");
                entity.addSeq();

            }
        });
        return globalConfig;
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList(
                "drop table if exists t_fill", "CREATE TABLE IF NOT EXISTS t_fill (" + "id BIGINT NOT NULL,"
                        + "name VARCHAR(30) NULL DEFAULT NULL," + "PRIMARY KEY (id))");
    }

}
