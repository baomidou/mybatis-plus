package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.baomidou.mybatisplus.test.DbTestSupport;
import com.baomidou.mybatisplus.test.sqlrunner.Entity;
import com.baomidou.mybatisplus.test.sqlrunner.EntityMapper;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.plugin.Interceptor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 以静态方式调用Service中的函数
 *
 * @author VampireAchao
 * @since 2022-05-03
 */
@Tag("integration")
class DbTest extends DbTestSupport<EntityMapper> {

    @Test
    void testSave() {
        Entity entity = new Entity();
        entity.setName("ruben");
        boolean isSuccess = Db.save(entity);
        assertThat(isSuccess).isTrue();
        assertThat(Db.count(Entity.class)).isEqualTo(3L);
    }

    @Test
    void testSaveBatch() {
        List<Entity> list = Arrays.asList(new Entity(), new Entity());
        boolean isSuccess = Db.saveBatch(list);
        assertThat(isSuccess).isTrue();
        assertThat(Db.count(Entity.class)).isEqualTo(4);
    }

    @Test
    void testSaveOrUpdateBatch() {
        Entity entity = new Entity();
        entity.setId(1L);
        entity.setName("cat");
        List<Entity> list = Arrays.asList(new Entity(), entity);
        boolean isSuccess = Db.saveOrUpdateBatch(list);
        assertThat(isSuccess).isTrue();
        assertThat(Db.count(Entity.class)).isEqualTo(3);
    }

    @Test
    void testRemoveById() {
        Entity entity = new Entity();
        entity.setId(1L);
        boolean isSuccess = Db.removeById(entity);
        assertThat(isSuccess).isTrue();
        assertThat(Db.count(Entity.class)).isEqualTo(1);
        isSuccess = Db.removeById(2L, Entity.class);
        assertThat(isSuccess).isTrue();
        assertThat(Db.count(Entity.class)).isEqualTo(0);
    }

    @Test
    void testUpdateById() {
        Entity entity = new Entity();
        entity.setId(1L);
        entity.setName("bee bee I'm a sheep");
        boolean isSuccess = Db.updateById(entity);
        assertThat(isSuccess).isTrue();
        assertThat(Db.getById(1L, Entity.class).getName()).isEqualTo("bee bee I'm a sheep");
    }

    @Test
    void testUpdate() {
        boolean isSuccess = Db
                .update(Wrappers.lambdaUpdate(Entity.class).eq(Entity::getId, 1L).set(Entity::getName, "be better"));
        assertThat(isSuccess).isTrue();
        assertThat(Db.getById(1L, Entity.class).getName()).isEqualTo("be better");

        Entity entity = new Entity();
        entity.setId(1L);
        entity.setName("bee bee I'm a sheep");
        isSuccess = Db.update(entity, Wrappers.lambdaQuery(Entity.class).eq(Entity::getId, 1L));
        assertThat(isSuccess).isTrue();
        assertThat(Db.getById(1L, Entity.class).getName()).isEqualTo("bee bee I'm a sheep");
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
        assertThat(isSuccess).isTrue();
        assertThat(Db.getById(1L, Entity.class).getName()).isEqualTo("bee bee I'm a sheep");
        assertThat(Db.getById(2L, Entity.class).getName()).isEqualTo("rabbit");
    }

    @Test
    void testRemove() {
        boolean isSuccess = Db.remove(Wrappers.lambdaQuery(Entity.class).eq(Entity::getId, 1L));
        assertThat(isSuccess).isTrue();
        assertThat(Db.count(Entity.class)).isEqualTo(1);
    }

    @Test
    void testRemoveByIds() {
        boolean isSuccess = Db.removeByIds(Arrays.asList(1L, 2L), Entity.class);
        assertThat(isSuccess).isTrue();
        assertThat(Db.count(Entity.class)).isEqualTo(0);
    }

    @Test
    void testRemoveByMap() {
        boolean isSuccess = Db.removeByMap(Collections.singletonMap("id", 1L), Entity.class);
        assertThat(isSuccess).isTrue();
        assertThat(Db.count(Entity.class)).isEqualTo(1);
    }

    @Test
    void testSaveOrUpdate() {
        Entity entity = new Entity();
        entity.setId(null);
        entity.setName("bee bee I'm a sheep");
        boolean isSuccess = Db.saveOrUpdate(entity);
        assertThat(isSuccess).isTrue();
        assertThat(Db.getById(entity.getId(), Entity.class).getName()).isEqualTo("bee bee I'm a sheep");

        entity.setName("be better");
        isSuccess = Db.saveOrUpdate(entity, Wrappers.lambdaQuery(Entity.class).eq(Entity::getId, entity.getId()));
        assertThat(isSuccess).isTrue();
        assertThat(Db.getById(entity.getId(), Entity.class).getName()).isEqualTo("be better");
    }

    @Test
    void testGetOne() {
        LambdaQueryWrapper<Entity> wrapper = Wrappers.lambdaQuery(Entity.class);
        assertThatThrownBy(() -> Db.getOne(wrapper)).isInstanceOf(TooManyResultsException.class);
        Entity one = Db.getOne(wrapper, false);
        assertThat(one).isNotNull();
        Entity entity = new Entity();
        entity.setId(1L);
        one = Db.getOne(entity);
        assertThat(one).isNotNull();
    }

    @Test
    void testListByMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1L);
        List<Entity> list = Db.listByMap(map, Entity.class);
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getName()).isEqualTo("ruben");
    }

    @Test
    void testByIds() {
        List<Entity> list = Db.listByIds(Arrays.asList(1L, 2L), Entity.class);
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    void testGetMap() {
        Map<String, Object> map = Db.getMap(Wrappers.lambdaQuery(Entity.class));
        assertThat(map).isNotNull();

        Entity entity = new Entity();
        entity.setId(1L);
        map = Db.getMap(entity);
        assertThat(map).isNotNull();
    }

    @Test
    void testList() {
        List<Entity> list = Db.list(Wrappers.lambdaQuery(Entity.class));
        assertThat(list.size()).isEqualTo(2);

        list = Db.list(Entity.class);
        assertThat(list.size()).isEqualTo(2);

        Entity entity = new Entity();
        entity.setId(1L);
        list = Db.list(entity);
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    void testListMaps() {
        List<Map<String, Object>> list = Db.listMaps(Wrappers.lambdaQuery(Entity.class));
        assertThat(list.size()).isEqualTo(2);

        list = Db.listMaps(Entity.class);
        assertThat(list.size()).isEqualTo(2);

        Entity entity = new Entity();
        entity.setId(1L);
        list = Db.listMaps(entity);
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    void testListObjs() {
        List<Entity> list = Db.listObjs(Entity.class);
        assertThat(list.size()).isEqualTo(2);

        List<Long> objectList = Db.listObjs(Wrappers.lambdaQuery(Entity.class), Entity::getId);
        assertThat(objectList.size()).isEqualTo(2);

        List<String> names = Db.listObjs(Entity.class, Entity::getName);
        assertThat(names).containsExactly("ruben", "chocolate");
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
        assertThat(page.getTotal()).isEqualTo(2);

        assertThat(page.getRecords().size()).isEqualTo(Db.listMaps(new Page<>(1, 1, false), Entity.class).size());

        page = Db.pageMaps(new Page<>(1, 1), Wrappers.lambdaQuery(Entity.class));
        assertThat(page.getRecords().size()).isEqualTo(1);

        assertThat(page.getRecords().size())
                .isEqualTo(Db.listMaps(new Page<>(1, 1, false), Wrappers.lambdaQuery(Entity.class)).size());
    }

    @Test
    void testPage() {
        IPage<Entity> page = Db.page(new Page<>(1, 1), Entity.class);
        assertThat(page.getTotal()).isEqualTo(2);
        assertThat(page.getRecords().size()).isEqualTo(Db.list(new Page<Entity>(1, 1), Entity.class).size());

        page = Db.page(new Page<>(1, 1), Wrappers.lambdaQuery(Entity.class));
        assertThat(page.getRecords().size()).isEqualTo(1);

        assertThat(page.getRecords().size())
                .isEqualTo(Db.list(new Page<Entity>(1, 1), Wrappers.lambdaQuery(Entity.class)).size());
    }

    @Test
    void testChain() {
        QueryChainWrapper<Entity> query = Db.query(Entity.class);
        List<Entity> list = query.eq("id", 1L).list();
        assertThat(list.size()).isEqualTo(1);

        LambdaQueryChainWrapper<Entity> lambdaQuery = Db.lambdaQuery(Entity.class);
        list = lambdaQuery.eq(Entity::getId, 1L).list();
        assertThat(list.size()).isEqualTo(1);

        UpdateChainWrapper<Entity> update = Db.update(Entity.class);
        update.eq("id", 1L).set("name", "bee bee I'm a sheep").update();
        assertThat(lambdaQuery.eq(Entity::getId, 1L).one().getName()).isEqualTo("bee bee I'm a sheep");

        LambdaUpdateChainWrapper<Entity> lambdaUpdate = Db.lambdaUpdate(Entity.class);
        lambdaUpdate.eq(Entity::getId, 1L).set(Entity::getName, "be better").update();
        assertThat(lambdaQuery.eq(Entity::getId, 1L).one().getName()).isEqualTo("be better");
    }

    @Test
    void testGetObj() {
        String name = Db.getObj(Wrappers.lambdaQuery(Entity.class).eq(Entity::getId, 1L), Entity::getName);
        assertThat(name).isEqualTo("ruben");
    }

    @Test
    void testCount() {
        verifyCount(0, null);
        verifyCount(0, 0L);
        verifyCount(1, 1L);
        verifyCount(12, 12L);
    }

    private void verifyCount(long expected, Long mockValue) {
        EntityMapper entityMapper = mock(EntityMapper.class);
        when(entityMapper.selectCount(any())).thenReturn(mockValue);
        CompatibleSet compatibleSet = mock(CompatibleSet.class);
        when(compatibleSet.getBean(EntityMapper.class)).thenReturn(entityMapper);
        try (MockedStatic<CompatibleHelper> compatibleHelperMockedStatic = Mockito.mockStatic(CompatibleHelper.class)) {
            compatibleHelperMockedStatic.when(CompatibleHelper::hasCompatibleSet).thenReturn(true);
            compatibleHelperMockedStatic.when(CompatibleHelper::getCompatibleSet).thenReturn(compatibleSet);
            assertThat(Db.count(Entity.class)).isEqualTo(expected);
            assertThat(Db.count(new Entity())).isEqualTo(expected);
            assertThat(Db.count(Wrappers.lambdaQuery(new Entity()))).isEqualTo(expected);
            assertThat(Db.count(Wrappers.lambdaQuery(Entity.class))).isEqualTo(expected);
        }
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity(id,name) values(1,'ruben'),(2,'chocolate');";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList(
                "drop table if exists entity", "CREATE TABLE IF NOT EXISTS entity (" + "id BIGINT NOT NULL,"
                        + "name VARCHAR(30) NULL DEFAULT NULL," + "PRIMARY KEY (id))");
    }
}
