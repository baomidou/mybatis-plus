package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.spi.CompatibleHelper;
import com.baomidou.mybatisplus.core.spi.CompatibleSet;
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
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 以静态方式调用Service中的函数
 *
 * @author VampireAchao
 * @since 2022-05-03
 */
class DbTest extends BaseDbTest<EntityMapper> {

    @Test
    void testMulIn() {
        List<List<?>> values = new ArrayList<>();
        values.add(Arrays.asList("1", "ruben"));
        values.add(Arrays.asList("2", "chocolate"));

        // 测试 1：匹配一个不存在的组合，预期结果为空
        List<Entity> list = Db.lambdaQuery(Entity.class)
            .in(true, List.of(Entity::getId, Entity::getName), List.of(Arrays.asList("2", "ruben")))
            .list();
        assertEquals(0, list.size());

        // 测试 2：根据单个字段 id 匹配 "2"，预期返回一条记录
        list = Db.lambdaQuery(Entity.class)
            .in(List.of(Entity::getId), List.of(List.of("2")))
            .list();
        assertEquals(1, list.size());

        // 测试 3：匹配组合条件，预期返回一条记录
        list = Db.lambdaQuery(Entity.class)
            .in(true, List.of(Entity::getId, Entity::getName), List.of(Arrays.asList("1", "ruben")))
            .list();
        assertEquals(1, list.size());

        // 测试 4：匹配 values 列表中的两个组合值，预期返回两条记录
        list = Db.lambdaQuery(Entity.class)
            .in(true, List.of(Entity::getId, Entity::getName), values)
            .list();
        assertEquals(2, list.size());

        // 测试 5：普通 Query 方式实现相同的 IN 查询（字段名写法），预期同样返回两条记录
        list = Db.query(Entity.class)
            .in(true, List.of("id", "name"), values)
            .list();
        assertEquals(2, list.size());

        // 测试 6：抛出异常的情况
        assertThrows(Exception.class, () -> Db.lambdaQuery(Entity.class)
            .in(true, List.of(Entity::getId, Entity::getName), new ArrayList<>())
            .list()
        );

        assertThrows(Exception.class, () -> Db.lambdaQuery(Entity.class)
            .in(true, new ArrayList<>(), new ArrayList<>())
            .list()
        );

        // 字段数量和值数量不匹配的情况
        assertThrows(Exception.class, () -> Db.lambdaQuery(Entity.class)
            .in(true, List.of(Entity::getId, Entity::getName), List.of(List.of("1")))
            .list()
        );

        // 测试 7：Lambda Update 使用 in 进行更新操作，将匹配 values 中的组合记录的 name 改为 "hjl"
        boolean update = Db.lambdaUpdate(Entity.class)
            .in(true, List.of(Entity::getId, Entity::getName), values)
            .set(Entity::getName, "hjl")
            .update();
        assertTrue(update);

        // 测试 8：普通 Update 使用 in 进行更新操作，上面的name已被更新， 所以预期更新失败
        update = Db.update(Entity.class)
            .in(true, List.of("id", "name"), values)
            .set("name", "robot")
            .update();
        assertFalse(update);
    }

    @Test
    void testMulNotIn() {
        List<List<?>> values = new ArrayList<>();
        values.add(Arrays.asList("1", "ruben"));
        values.add(Arrays.asList("2", "chocolate"));

        // 测试 1：排除一个不存在的组合 ("2", "ruben")，预期返回所有其他记录
        List<Entity> list = Db.lambdaQuery(Entity.class)
            .notIn(true, List.of(Entity::getId, Entity::getName), List.of(Arrays.asList("2", "ruben")))
            .list();
        assertEquals(2, list.size());

        // 测试 2：排除单个字段 id 为 "2" 的记录，预期返回除该记录外的所有数据
        list = Db.lambdaQuery(Entity.class)
            .notIn(List.of(Entity::getId), List.of(List.of("2")))
            .list();
        assertEquals(1, list.size());

        // 测试 3：排除组合条件，预期返回除该记录外的所有数据
        list = Db.lambdaQuery(Entity.class)
            .notIn(true, List.of(Entity::getId, Entity::getName), List.of(Arrays.asList("1", "ruben")))
            .list();
        assertEquals(1, list.size());

        // 测试 4：排除 values 列表中的两个组合值，预期返回除这两条外的所有数据
        list = Db.lambdaQuery(Entity.class)
            .notIn(true, List.of(Entity::getId, Entity::getName), values)
            .list();
        assertEquals(0, list.size());

        // 测试 5：普通 Query 方式实现相同的 NOT IN 查询（字段名写法），预期结果一致
        list = Db.query(Entity.class)
            .notIn(true, List.of("id", "name"), values)
            .list();
        assertEquals(0, list.size());

        // 测试 6：当查询值为空时，会抛出异常，和in查询一个效果
        assertThrows(Exception.class, () -> Db.lambdaQuery(Entity.class)
            .notIn(true, List.of(Entity::getId, Entity::getName), new ArrayList<>())
            .list()
        );

        // 测试 7：LambdaUpdate，排除单个记录
        boolean update = Db.lambdaUpdate(Entity.class)
            .notIn(true, List.of(Entity::getId, Entity::getName), List.of(Arrays.asList("1", "ruben")))
            .set(Entity::getName, "hjl")
            .update();
        assertTrue(update);

        // 测试 8：普通 Update
        update = Db.update(Entity.class)
            .notIn(true, List.of("id", "name"), values)
            .set("name", "robot")
            .update();
        assertTrue(update);
    }

    @Test
    void testSave() {
        Entity entity = new Entity();
        entity.setName("ruben");
        boolean isSuccess = Db.save(entity);
        Assertions.assertTrue(isSuccess);
        assertEquals(3L, Db.count(Entity.class));
    }

    @Test
    void testSaveBatch() {
        List<Entity> list = Arrays.asList(new Entity(), new Entity());
        boolean isSuccess = Db.saveBatch(list);
        Assertions.assertTrue(isSuccess);
        assertEquals(4, Db.count(Entity.class));
    }

    @Test
    void testSaveOrUpdateBatch() {
        Entity entity = new Entity();
        entity.setId(1L);
        entity.setName("cat");
        List<Entity> list = Arrays.asList(new Entity(), entity);
        boolean isSuccess = Db.saveOrUpdateBatch(list);
        Assertions.assertTrue(isSuccess);
        assertEquals(3, Db.count(Entity.class));
    }

    @Test
    void testRemoveById() {
        Entity entity = new Entity();
        entity.setId(1L);
        boolean isSuccess = Db.removeById(entity);
        Assertions.assertTrue(isSuccess);
        assertEquals(1, Db.count(Entity.class));
        isSuccess = Db.removeById(2L, Entity.class);
        Assertions.assertTrue(isSuccess);
        assertEquals(0, Db.count(Entity.class));
    }

    @Test
    void testUpdateById() {
        Entity entity = new Entity();
        entity.setId(1L);
        entity.setName("bee bee I'm a sheep");
        boolean isSuccess = Db.updateById(entity);
        Assertions.assertTrue(isSuccess);
        assertEquals("bee bee I'm a sheep", Db.getById(1L, Entity.class).getName());
    }

    @Test
    void testUpdate() {
        boolean isSuccess = Db.update(Wrappers.lambdaUpdate(Entity.class).eq(Entity::getId, 1L).set(Entity::getName, "be better"));
        Assertions.assertTrue(isSuccess);
        assertEquals("be better", Db.getById(1L, Entity.class).getName());

        Entity entity = new Entity();
        entity.setId(1L);
        entity.setName("bee bee I'm a sheep");
        isSuccess = Db.update(entity, Wrappers.lambdaQuery(Entity.class).eq(Entity::getId, 1L));
        Assertions.assertTrue(isSuccess);
        assertEquals("bee bee I'm a sheep", Db.getById(1L, Entity.class).getName());
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
        assertEquals("bee bee I'm a sheep", Db.getById(1L, Entity.class).getName());
        assertEquals("rabbit", Db.getById(2L, Entity.class).getName());
    }

    @Test
    void testRemove() {
        boolean isSuccess = Db.remove(Wrappers.lambdaQuery(Entity.class).eq(Entity::getId, 1L));
        Assertions.assertTrue(isSuccess);
        assertEquals(1, Db.count(Entity.class));
    }

    @Test
    void testRemoveByIds() {
        boolean isSuccess = Db.removeByIds(Arrays.asList(1L, 2L), Entity.class);
        Assertions.assertTrue(isSuccess);
        assertEquals(0, Db.count(Entity.class));
    }

    @Test
    void testRemoveByMap() {
        boolean isSuccess = Db.removeByMap(Collections.singletonMap("id", 1L), Entity.class);
        Assertions.assertTrue(isSuccess);
        assertEquals(1, Db.count(Entity.class));
    }

    @Test
    void testSaveOrUpdate() {
        Entity entity = new Entity();
        entity.setId(null);
        entity.setName("bee bee I'm a sheep");
        boolean isSuccess = Db.saveOrUpdate(entity);
        Assertions.assertTrue(isSuccess);
        assertEquals("bee bee I'm a sheep", Db.getById(entity.getId(), Entity.class).getName());

        entity.setName("be better");
        isSuccess = Db.saveOrUpdate(entity, Wrappers.lambdaQuery(Entity.class).eq(Entity::getId, entity.getId()));
        Assertions.assertTrue(isSuccess);
        assertEquals("be better", Db.getById(entity.getId(), Entity.class).getName());
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
        assertEquals(1, list.size());
        assertEquals("ruben", list.get(0).getName());
    }

    @Test
    void testByIds() {
        List<Entity> list = Db.listByIds(Arrays.asList(1L, 2L), Entity.class);
        assertEquals(2, list.size());
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
        assertEquals(2, list.size());

        list = Db.list(Entity.class);
        assertEquals(2, list.size());

        Entity entity = new Entity();
        entity.setId(1L);
        list = Db.list(entity);
        assertEquals(1, list.size());
    }

    @Test
    void testListMaps() {
        List<Map<String, Object>> list = Db.listMaps(Wrappers.lambdaQuery(Entity.class));
        assertEquals(2, list.size());

        list = Db.listMaps(Entity.class);
        assertEquals(2, list.size());

        Entity entity = new Entity();
        entity.setId(1L);
        list = Db.listMaps(entity);
        assertEquals(1, list.size());
    }

    @Test
    void testListObjs() {
        List<Entity> list = Db.listObjs(Entity.class);
        assertEquals(2, list.size());

        List<Long> objectList = Db.listObjs(Wrappers.lambdaQuery(Entity.class), Entity::getId);
        assertEquals(2, objectList.size());

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
        assertEquals(2, page.getTotal());

        assertEquals(Db.listMaps(new Page<>(1, 1, false), Entity.class).size(), page.getRecords().size());

        page = Db.pageMaps(new Page<>(1, 1), Wrappers.lambdaQuery(Entity.class));
        assertEquals(1, page.getRecords().size());

        assertEquals(Db.listMaps(new Page<>(1, 1, false), Wrappers.lambdaQuery(Entity.class)).size(), page.getRecords().size());
    }

    @Test
    void testPage() {
        IPage<Entity> page = Db.page(new Page<>(1, 1), Entity.class);
        assertEquals(2, page.getTotal());
        assertEquals(Db.list(new Page<Entity>(1, 1), Entity.class).size(), page.getRecords().size());

        page = Db.page(new Page<>(1, 1), Wrappers.lambdaQuery(Entity.class));
        assertEquals(1, page.getRecords().size());

        assertEquals(Db.list(new Page<Entity>(1, 1), Wrappers.lambdaQuery(Entity.class)).size(), page.getRecords().size());
    }

    @Test
    void testChain() {
        QueryChainWrapper<Entity> query = Db.query(Entity.class);
        List<Entity> list = query.eq("id", 1L).list();
        assertEquals(1, list.size());

        LambdaQueryChainWrapper<Entity> lambdaQuery = Db.lambdaQuery(Entity.class);
        list = lambdaQuery.eq(Entity::getId, 1L).list();
        assertEquals(1, list.size());

        UpdateChainWrapper<Entity> update = Db.update(Entity.class);
        update.eq("id", 1L).set("name", "bee bee I'm a sheep").update();
        assertEquals("bee bee I'm a sheep", lambdaQuery.eq(Entity::getId, 1L).one().getName());

        LambdaUpdateChainWrapper<Entity> lambdaUpdate = Db.lambdaUpdate(Entity.class);
        lambdaUpdate.eq(Entity::getId, 1L).set(Entity::getName, "be better").update();
        assertEquals("be better", lambdaQuery.eq(Entity::getId, 1L).one().getName());
    }

    @Test
    void testGetObj() {
        String name = Db.getObj(Wrappers.lambdaQuery(Entity.class).eq(Entity::getId, 1L), Entity::getName);
        assertEquals("ruben", name);
    }

    @Test
    void testCount() {
        verifyCount(0, null);
        verifyCount(0, 0L);
        verifyCount(1, 1L);
        verifyCount(12, 12L);
    }

    private void verifyCount(long expected, Long mockValue) {
        BaseMapper<Entity> entityMapper = mock(EntityMapper.class);
        when(entityMapper.selectCount(any())).thenReturn(mockValue);
        CompatibleSet compatibleSet = mock(CompatibleSet.class);
        when(compatibleSet.getBean(EntityMapper.class)).thenReturn((EntityMapper) entityMapper);
        try (MockedStatic<CompatibleHelper> compatibleHelperMockedStatic = Mockito.mockStatic(CompatibleHelper.class)) {
            compatibleHelperMockedStatic.when(CompatibleHelper::hasCompatibleSet).thenReturn(true);
            compatibleHelperMockedStatic.when(CompatibleHelper::getCompatibleSet).thenReturn(compatibleSet);
            assertEquals(expected, Db.count(Entity.class));
            assertEquals(expected, Db.count(new Entity()));
            assertEquals(expected, Db.count(Wrappers.lambdaQuery(new Entity())));
            assertEquals(expected, Db.count(Wrappers.lambdaQuery(Entity.class)));
        }
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
