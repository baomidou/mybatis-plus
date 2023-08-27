package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.baomidou.mybatisplus.test.BaseDbTest;
import com.baomidou.mybatisplus.test.sqlrunner.Entity;
import com.baomidou.mybatisplus.test.sqlrunner.EntityMapper;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.plugin.Interceptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * 以静态方式调用Service中的函数
 *
 * @author VampireAchao
 * @since 2022-05-03
 */
class DbTest extends BaseDbTest<EntityMapper> {

    @Test
    void testSave() {
        Entity entity = new Entity();
        entity.setName("ruben");
        boolean isSuccess = Db.save(entity);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals(3L, Db.count(Entity.class));
    }

    @Test
    void testSaveBatch() {
        List<Entity> list = Arrays.asList(new Entity(), new Entity());
        boolean isSuccess = Db.saveBatch(list);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals(4, Db.count(Entity.class));
    }

    @Test
    void testSaveOrUpdateBatch() {
        Entity entity = new Entity();
        entity.setId(1L);
        entity.setName("cat");
        List<Entity> list = Arrays.asList(new Entity(), entity);
        boolean isSuccess = Db.saveOrUpdateBatch(list);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals(3, Db.count(Entity.class));
    }

    @Test
    void testRemoveById() {
        Entity entity = new Entity();
        entity.setId(1L);
        boolean isSuccess = Db.removeById(entity);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals(1, Db.count(Entity.class));
        isSuccess = Db.removeById(2L, Entity.class);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals(0, Db.count(Entity.class));
    }

    @Test
    void testUpdateById() {
        Entity entity = new Entity();
        entity.setId(1L);
        entity.setName("bee bee I'm a sheep");
        boolean isSuccess = Db.updateById(entity);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals("bee bee I'm a sheep", Db.getById(1L, Entity.class).getName());
    }

    @Test
    void testUpdate() {
        boolean isSuccess = Db.update(Wrappers.lambdaUpdate(Entity.class).eq(Entity::getId, 1L).set(Entity::getName, "be better"));
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals("be better", Db.getById(1L, Entity.class).getName());

        Entity entity = new Entity();
        entity.setId(1L);
        entity.setName("bee bee I'm a sheep");
        isSuccess = Db.update(entity, Wrappers.lambdaQuery(Entity.class).eq(Entity::getId, 1L));
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals("bee bee I'm a sheep", Db.getById(1L, Entity.class).getName());
    }

    @Test
    void testUpdateBatchById() {
        Entity sheep = new Entity();
        sheep.setId(1L);
        sheep.setName("bee bee I'm a sheep");

        Entity ruben = new Entity();
        ruben.setId(2L);
        ruben.setName("rabbit");
        boolean isSuccess = Db.updateBatchById(Arrays.asList(sheep, ruben));
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals("bee bee I'm a sheep", Db.getById(1L, Entity.class).getName());
        Assertions.assertEquals("rabbit", Db.getById(2L, Entity.class).getName());
    }

    @Test
    void testRemove() {
        boolean isSuccess = Db.remove(Wrappers.lambdaQuery(Entity.class).eq(Entity::getId, 1L));
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals(1, Db.count(Entity.class));
    }

    @Test
    void testRemoveByIds() {
        boolean isSuccess = Db.removeByIds(Arrays.asList(1L, 2L), Entity.class);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals(0, Db.count(Entity.class));
    }

    @Test
    void testRemoveByMap() {
        boolean isSuccess = Db.removeByMap(Collections.singletonMap("id", 1L), Entity.class);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals(1, Db.count(Entity.class));
    }

    @Test
    void testSaveOrUpdate() {
        Entity entity = new Entity();
        entity.setId(null);
        entity.setName("bee bee I'm a sheep");
        boolean isSuccess = Db.saveOrUpdate(entity);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals("bee bee I'm a sheep", Db.getById(entity.getId(), Entity.class).getName());

        entity.setName("be better");
        isSuccess = Db.saveOrUpdate(entity, Wrappers.lambdaQuery(Entity.class).eq(Entity::getId, entity.getId()));
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals("be better", Db.getById(entity.getId(), Entity.class).getName());
    }

    @Test
    void testGetOne() {
        LambdaQueryWrapper<Entity> wrapper = Wrappers.lambdaQuery(Entity.class);
        Assertions.assertThrows(TooManyResultsException.class, () -> Db.getOne(wrapper));
        Entity one = Db.getOne(wrapper, false);
        Assertions.assertNotNull(one);
        Entity entity = new Entity();
        entity.setId(1L);
        one = Db.getOne(entity);
        Assertions.assertNotNull(one);
    }

    @Test
    void testListByMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1L);
        List<Entity> list = Db.listByMap(map, Entity.class);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("ruben", list.get(0).getName());
    }

    @Test
    void testByIds() {
        List<Entity> list = Db.listByIds(Arrays.asList(1L, 2L), Entity.class);
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void testGetMap() {
        Map<String, Object> map = Db.getMap(Wrappers.lambdaQuery(Entity.class));
        Assertions.assertNotNull(map);

        Entity entity = new Entity();
        entity.setId(1L);
        map = Db.getMap(entity);
        Assertions.assertNotNull(map);
    }

    @Test
    void testList() {
        List<Entity> list = Db.list(Wrappers.lambdaQuery(Entity.class));
        Assertions.assertEquals(2, list.size());

        list = Db.list(Entity.class);
        Assertions.assertEquals(2, list.size());

        Entity entity = new Entity();
        entity.setId(1L);
        list = Db.list(entity);
        Assertions.assertEquals(1, list.size());
    }

    @Test
    void testListMaps() {
        List<Map<String, Object>> list = Db.listMaps(Wrappers.lambdaQuery(Entity.class));
        Assertions.assertEquals(2, list.size());

        list = Db.listMaps(Entity.class);
        Assertions.assertEquals(2, list.size());

        Entity entity = new Entity();
        entity.setId(1L);
        list = Db.listMaps(entity);
        Assertions.assertEquals(1, list.size());
    }

    @Test
    void testListObjs() {
        List<Entity> list = Db.listObjs(Entity.class);
        Assertions.assertEquals(2, list.size());

        List<Long> objectList = Db.listObjs(Wrappers.lambdaQuery(Entity.class), Entity::getId);
        Assertions.assertEquals(2, objectList.size());

        List<String> names = Db.listObjs(Entity.class, Entity::getName);
        Assertions.assertArrayEquals(new String[]{"ruben", "chocolate"}, names.toArray());
    }

    @Override
    protected List<Interceptor> interceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.SQLITE));
        return Collections.singletonList(interceptor);
    }

    @Test
    void testPageMaps() {
        Page<Map<String, Object>> page = Db.pageMaps(new Page<>(1, 1), Entity.class);
        Assertions.assertEquals(2, page.getTotal());

        Assertions.assertEquals(Db.listMaps(new Page<>(1, 1, false), Entity.class).size(), page.getRecords().size());

        page = Db.pageMaps(new Page<>(1, 1), Wrappers.lambdaQuery(Entity.class));
        Assertions.assertEquals(1, page.getRecords().size());

        Assertions.assertEquals(Db.listMaps(new Page<>(1, 1, false), Wrappers.lambdaQuery(Entity.class)).size(), page.getRecords().size());
    }

    @Test
    void testPage() {
        IPage<Entity> page = Db.page(new Page<>(1, 1), Entity.class);
        Assertions.assertEquals(2, page.getTotal());
        Assertions.assertEquals(Db.list(new Page<Entity>(1, 1), Entity.class).size(),page.getRecords().size());

        page = Db.page(new Page<>(1, 1), Wrappers.lambdaQuery(Entity.class));
        Assertions.assertEquals(1, page.getRecords().size());

        Assertions.assertEquals(Db.list(new Page<Entity>(1, 1), Wrappers.lambdaQuery(Entity.class)).size(),page.getRecords().size());
    }

    @Test
    void testChain() {
        QueryChainWrapper<Entity> query = Db.query(Entity.class);
        List<Entity> list = query.eq("id", 1L).list();
        Assertions.assertEquals(1, list.size());

        LambdaQueryChainWrapper<Entity> lambdaQuery = Db.lambdaQuery(Entity.class);
        list = lambdaQuery.eq(Entity::getId, 1L).list();
        Assertions.assertEquals(1, list.size());

        UpdateChainWrapper<Entity> update = Db.update(Entity.class);
        update.eq("id", 1L).set("name", "bee bee I'm a sheep").update();
        Assertions.assertEquals("bee bee I'm a sheep", lambdaQuery.eq(Entity::getId, 1L).one().getName());

        LambdaUpdateChainWrapper<Entity> lambdaUpdate = Db.lambdaUpdate(Entity.class);
        lambdaUpdate.eq(Entity::getId, 1L).set(Entity::getName, "be better").update();
        Assertions.assertEquals("be better", lambdaQuery.eq(Entity::getId, 1L).one().getName());
    }

    @Test
    void testGetObj() {
        String name = Db.getObj(Wrappers.lambdaQuery(Entity.class).eq(Entity::getId, 1L), Entity::getName);
        Assertions.assertEquals("ruben", name);
    }


    @Override
    protected String tableDataSql() {
        return "insert into entity(id,name) values(1,'ruben'),(2,'chocolate');";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity", "CREATE TABLE IF NOT EXISTS entity (" +
            "id BIGINT NOT NULL," +
            "name VARCHAR(30) NULL DEFAULT NULL," +
            "PRIMARY KEY (id))");
    }
}
