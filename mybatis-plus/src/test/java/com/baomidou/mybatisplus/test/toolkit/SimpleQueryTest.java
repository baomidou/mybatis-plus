package com.baomidou.mybatisplus.test.toolkit;

import static com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.mapping;
import static org.apache.ibatis.util.MapUtil.entry;
import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SimpleQuery;
import com.baomidou.mybatisplus.test.BaseDbTest;
import com.baomidou.mybatisplus.test.rewrite.Entity;
import com.baomidou.mybatisplus.test.rewrite.EntityMapper;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * 简单查询工具类测试
 *
 * @author <achao1441470436@gmail.com>
 * @since 2021/11/9 18:30
 */
class SimpleQueryTest extends BaseDbTest<EntityMapper> {

    @Test
    void testList() {
        // 我要这张表里的ids
        List<Long> entityIds = SimpleQuery.list(lambdaQuery(), Entity::getId);
        assertThat(entityIds).containsExactly(1L, 2L);

        // 可叠加后续操作
        List<String> names = SimpleQuery.list(lambdaQuery(), Entity::getName,
            e -> Optional.ofNullable(e.getName())
                .map(String::toUpperCase)
                .ifPresent(e::setName));
        assertThat(names).containsExactly("RUBEN", null);
    }

    @Test
    void testMap() {
        // 我要这个表里对应条件的用户，用id作为key给我一个map
        Map<Long, Entity> idEntityMap = SimpleQuery.keyMap(
            Wrappers.<Entity>lambdaQuery().eq(Entity::getId, 1L), Entity::getId);
        // 校验结果
        Entity entity = new Entity();
        entity.setId(1L);
        entity.setName("ruben");
        Assert.isTrue(idEntityMap.equals(Collections.singletonMap(1L, entity)), "Ops!");

        // 如果我只想要id和name组成的map
        Map<Long, String> idNameMap = SimpleQuery.map(lambdaQuery(), Entity::getId, Entity::getName);
        // 校验结果
        Map<Long, String> map = new HashMap<>(1 << 2);
        map.put(1L, "ruben");
        map.put(2L, null);
        Assert.isTrue(idNameMap.equals(map), "Ops!");
    }

    @Test
    void testGroup() {
        // 我需要相同名字的用户的分为一组，再造一条数据
        doTestAutoCommit(m -> {
            Entity entity = new Entity();
            entity.setId(3L);
            entity.setName("ruben");
            m.insert(entity);
        });

        // 简单查询
        Map<String, List<Entity>> nameUsersMap = SimpleQuery.group(lambdaQuery(), Entity::getName);

        // 校验结果
        Map<String, List<Entity>> map = new HashMap<>(1 << 2);
        Entity chao = new Entity();
        chao.setId(2L);
        chao.setName(null);
        map.put(null, Collections.singletonList(chao));

        Entity ruben = new Entity();
        ruben.setId(1L);
        ruben.setName("ruben");
        Entity ruben2 = new Entity();
        ruben2.setId(3L);
        ruben2.setName("ruben");
        map.put("ruben", Arrays.asList(ruben, ruben2));
        Assert.isTrue(nameUsersMap.equals(map), "Ops!");

        // 解锁高级玩法：
        // 获取Map<name,List<id>>
        Map<String, List<Long>> nameIdMap = SimpleQuery.group(lambdaQuery(), Entity::getName,
            mapping(Entity::getId, toList()));
        assertThat(nameIdMap).containsExactly(entry(null, Arrays.asList(2L)), entry("ruben", Arrays.asList(1L, 3L)));

        // 获取Map<name,个数>
        Map<String, Long> nameCountMap = SimpleQuery.group(lambdaQuery(), Entity::getName, counting());
        assertThat(nameCountMap).containsExactly(entry(null, 1L), entry("ruben", 2L));
        // ...超多花样
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity(id,name) values(1, 'ruben'), (2, null);";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity", "CREATE TABLE IF NOT EXISTS entity (" +
            "id BIGINT NOT NULL," +
            "name VARCHAR(30) NULL DEFAULT NULL," +
            "PRIMARY KEY (id))");
    }

}

