package com.baomidou.mybatisplus.test.logicdel;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.injector.methods.LogicDeleteBatchByIds;
import com.baomidou.mybatisplus.test.BaseDbTest;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-23
 */
public class LogicDelTest extends BaseDbTest<EntityMapper> {

    @Test
    void logicDel() {
        doTestAutoCommit(i -> {
            int delete = i.deleteById(1L);
            assertThat(delete).isEqualTo(1);

            delete = i.delete(Wrappers.<Entity>lambdaQuery().eq(Entity::getId, 2));
            assertThat(delete).isEqualTo(1);
        });

        doTest(i -> {
            Entity entity = i.byId(1L);
            assertThat(entity).isNotNull();
            assertThat(entity.getDeleted()).isTrue();

            entity = i.byId(2L);
            assertThat(entity).isNotNull();
            assertThat(entity.getDeleted()).isTrue();
        });

        doTest(mapper -> {
            Entity entity = new Entity();
            entity.setName("测试根据实体删除");
            mapper.insert(entity);
            assertThat(mapper.deleteById(entity)).isEqualTo(1);
        });

        doTest(mapper -> {
            Entity entity1 = new Entity();
            entity1.setName("测试根据实体主键批量删除");
            mapper.insert(entity1);
            Entity entity2 = new Entity();
            entity2.setName("测试根据实体主键批量删除");
            mapper.insert(entity2);
            assertThat(mapper.deleteByIds(Arrays.asList(entity1.getId(), entity2.getId()))).isEqualTo(2);
        });

        doTest(mapper -> {
            Entity entity1 = new Entity();
            entity1.setName("测试根据实体批量删除");
            mapper.insert(entity1);
            Entity entity2 = new Entity();
            entity2.setName("测试根据实体批量删除");
            mapper.insert(entity2);
            List<Entity> entityList = new ArrayList<>();
            entityList.add(entity1);
            entityList.add(entity2);
            assertThat(mapper.deleteByIds(entityList)).isEqualTo(2);
            entityList.forEach(entity -> {
                //TODO 3.5.7 修改为使用IN的方式删除而不是使用行记录删除.
//                Assertions.assertEquals("聂秋秋", entity.getDeleteBy());
            });
        });

        doTest(mapper -> {
            Entity entity1 = new Entity();
            entity1.setName("测试自定义方法根据实体批量删除");
            mapper.insert(entity1);
            Entity entity2 = new Entity();
            entity2.setName("测试自定义方法根据实体批量删除");
            mapper.insert(entity2);
            List<Entity> entityList = new ArrayList<>();
            entityList.add(entity1);
            entityList.add(entity2);
            assertThat(mapper.testDeleteBatch(entityList)).isEqualTo(2);
            entityList.forEach(entity -> {
                Assertions.assertEquals("聂秋秋", entity.getDeleteBy());
            });
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
            "delete_by VARCHAR(30) NULL DEFAULT NULL," +
            "deleted BOOLEAN NOT NULL DEFAULT false," +
            "PRIMARY KEY (id))");
    }

    @Override
    protected GlobalConfig globalConfig() {
        GlobalConfig globalConfig = super.globalConfig();
        globalConfig.setMetaObjectHandler(new MetaObjectHandler() {

            @Override
            public void insertFill(MetaObject metaObject) {

            }

            @Override
            public void updateFill(MetaObject metaObject) {
                strictUpdateFill(metaObject, "deleteBy", String.class, "聂秋秋");
            }
        });
        globalConfig.setSqlInjector(new DefaultSqlInjector() {
            @Override
            public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
                List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
                methodList.add(new LogicDeleteBatchByIds("testDeleteBatch"));
                return methodList;
            }
        });
        return globalConfig;
    }
}
