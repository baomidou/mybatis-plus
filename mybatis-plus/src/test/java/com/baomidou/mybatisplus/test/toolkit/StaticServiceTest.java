package com.baomidou.mybatisplus.test.toolkit;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.plugin.Interceptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.StaticService;
import com.baomidou.mybatisplus.test.BaseDbTest;
import com.baomidou.mybatisplus.test.sqlrunner.Entity;
import com.baomidou.mybatisplus.test.sqlrunner.EntityMapper;

/**
 * 以静态方式调用Service中的函数
 *
 * @author VampireAchao
 * @since 2022-05-03
 */
class StaticServiceTest extends BaseDbTest<EntityMapper> {

    @Test
    void testSave() {
        Entity entity = new Entity();
        entity.setName("ruben");
        boolean isSuccess = StaticService.save(entity);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals(3L, StaticService.count(Entity.class));
    }

    @Test
    void testSaveBatch() {
        List<Entity> list = Arrays.asList(new Entity(), new Entity());
        boolean isSuccess = StaticService.saveBatch(list);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals(4, StaticService.count(Entity.class));
    }

    @Test
    void testSaveOrUpdateBatch() {
        Entity entity = new Entity();
        entity.setId(1L);
        entity.setName("cat");
        List<Entity> list = Arrays.asList(new Entity(), entity);
        boolean isSuccess = StaticService.saveOrUpdateBatch(list);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals(3, StaticService.count(Entity.class));
    }

    @Test
    void testRemoveById() {
        Entity entity = new Entity();
        entity.setId(1L);
        boolean isSuccess = StaticService.removeById(entity);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals(1, StaticService.count(Entity.class));
        isSuccess = StaticService.removeById(2L, Entity.class);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals(0, StaticService.count(Entity.class));
    }

    @Test
    void testUpdateById() {
        Entity entity = new Entity();
        entity.setId(1L);
        entity.setName("bee bee I'm a sheep");
        boolean isSuccess = StaticService.updateById(entity);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals("bee bee I'm a sheep", StaticService.getById(1L, Entity.class).getName());
    }

    @Test
    void testUpdate() {
        boolean isSuccess = StaticService.update(Wrappers.lambdaUpdate(Entity.class).eq(Entity::getId, 1L).set(Entity::getName, "be better"));
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals("be better", StaticService.getById(1L, Entity.class).getName());

        Entity entity = new Entity();
        entity.setId(1L);
        entity.setName("bee bee I'm a sheep");
        isSuccess = StaticService.update(entity, Wrappers.lambdaQuery(Entity.class).eq(Entity::getId, 1L));
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals("bee bee I'm a sheep", StaticService.getById(1L, Entity.class).getName());
    }

    @Test
    void testUpdateBatchById() {
        Entity sheep = new Entity();
        sheep.setId(1L);
        sheep.setName("bee bee I'm a sheep");

        Entity ruben = new Entity();
        ruben.setId(2L);
        ruben.setName("rabbit");
        boolean isSuccess = StaticService.updateBatchById(Arrays.asList(sheep, ruben));
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals("bee bee I'm a sheep", StaticService.getById(1L, Entity.class).getName());
        Assertions.assertEquals("rabbit", StaticService.getById(2L, Entity.class).getName());
    }

    @Test
    void testRemove() {
        boolean isSuccess = StaticService.remove(Wrappers.lambdaQuery(Entity.class).eq(Entity::getId, 1L));
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals(1, StaticService.count(Entity.class));
    }

    @Test
    void testRemoveByIds() {
        boolean isSuccess = StaticService.removeByIds(Arrays.asList(1L, 2L), Entity.class);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals(0, StaticService.count(Entity.class));
    }

    @Test
    void testRemoveByMap() {
        boolean isSuccess = StaticService.removeByMap(Collections.singletonMap("id", 1L), Entity.class);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals(1, StaticService.count(Entity.class));
    }

    @Test
    void testSaveOrUpdate() {
        Entity entity = new Entity();
        entity.setId(null);
        entity.setName("bee bee I'm a sheep");
        boolean isSuccess = StaticService.saveOrUpdate(entity);
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals("bee bee I'm a sheep", StaticService.getById(entity.getId(), Entity.class).getName());

        entity.setName("be better");
        isSuccess = StaticService.saveOrUpdate(entity, Wrappers.lambdaQuery(Entity.class).eq(Entity::getId, entity.getId()));
        Assertions.assertTrue(isSuccess);
        Assertions.assertEquals("be better", StaticService.getById(entity.getId(), Entity.class).getName());
    }

    @Test
    void testGetOne() {
        LambdaQueryWrapper<Entity> wrapper = Wrappers.lambdaQuery(Entity.class);
        Assertions.assertThrows(MybatisPlusException.class, () -> StaticService.getOne(wrapper));
        Entity one = StaticService.getOne(wrapper, false);
        Assertions.assertNotNull(one);
    }

    @Test
    void testListByMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1L);
        List<Entity> list = StaticService.listByMap(map, Entity.class);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("ruben", list.get(0).getName());
    }

    @Test
    void testByIds() {
        List<Entity> list = StaticService.listByIds(Arrays.asList(1L, 2L), Entity.class);
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void testGetMap() {
        Map<String, Object> map = StaticService.getMap(Wrappers.lambdaQuery(Entity.class));
        Assertions.assertNotNull(map);
    }

    @Test
    void testList() {
        List<Entity> list = StaticService.list(Wrappers.lambdaQuery(Entity.class));
        Assertions.assertEquals(2, list.size());

        list = StaticService.list(Entity.class);
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void testListMaps() {
        List<Map<String, Object>> list = StaticService.listMaps(Wrappers.lambdaQuery(Entity.class));
        Assertions.assertEquals(2, list.size());

        list = StaticService.listMaps(Entity.class);
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void testListObjs() {
        List<Entity> list = StaticService.listObjs(Entity.class);
        Assertions.assertEquals(2, list.size());

        List<Long> objectList = StaticService.listObjs(Wrappers.lambdaQuery(Entity.class), Entity::getId);
        Assertions.assertEquals(2, objectList.size());

        List<String> names = StaticService.listObjs(Entity.class, Entity::getName);
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
        Page<Map<String, Object>> page = StaticService.pageMaps(new Page<>(1, 1), Entity.class);
        Assertions.assertEquals(2, page.getTotal());

        page = StaticService.pageMaps(new Page<>(1, 1), Wrappers.lambdaQuery(Entity.class));
        Assertions.assertEquals(1, page.getRecords().size());
    }

    @Test
    void testPage() {
        IPage<Entity> page = StaticService.page(new Page<>(1, 1), Entity.class);
        Assertions.assertEquals(2, page.getTotal());

        page = StaticService.page(new Page<>(1, 1), Wrappers.lambdaQuery(Entity.class));
        Assertions.assertEquals(1, page.getRecords().size());
    }

    @Test
    void testChain() {
        QueryChainWrapper<Entity> query = StaticService.query(Entity.class);
        List<Entity> list = query.eq("id", 1L).list();
        Assertions.assertEquals(1, list.size());

        LambdaQueryChainWrapper<Entity> lambdaQuery = StaticService.lambdaQuery(Entity.class);
        list = lambdaQuery.eq(Entity::getId, 1L).list();
        Assertions.assertEquals(1, list.size());

        UpdateChainWrapper<Entity> update = StaticService.update(Entity.class);
        update.eq("id", 1L).set("name", "bee bee I'm a sheep").update();
        Assertions.assertEquals("bee bee I'm a sheep", lambdaQuery.eq(Entity::getId, 1L).one().getName());

        LambdaUpdateChainWrapper<Entity> lambdaUpdate = StaticService.lambdaUpdate(Entity.class);
        lambdaUpdate.eq(Entity::getId, 1L).set(Entity::getName, "be better").update();
        Assertions.assertEquals("be better", lambdaQuery.eq(Entity::getId, 1L).one().getName());
    }

    @Test
    void testGetObj() {
        String name = StaticService.getObj(Wrappers.lambdaQuery(Entity.class).eq(Entity::getId, 1L), Entity::getName);
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
