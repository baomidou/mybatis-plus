/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.MybatisBatchUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.entity.SuperEntity;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.mapper.H2UserMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Mybatis Plus H2 Junit Test
 *
 * @author hubin
 * @since 2018-06-05
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
class H2UserMapperTest extends BaseTest {

    @Resource
    protected H2UserMapper userMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private TransactionTemplate transactionTemplate;


    @Test
    void testBatchTransaction() {
        List<H2User> h2UserList = Arrays.asList(new H2User(1000036L, "测试12323232"), new H2User(10000367L, "测试3323232"));
        try {
            transactionTemplate.execute((TransactionCallback<List<BatchResult>>) status -> {
                MybatisBatch.Method<H2User> mapperMethod = new MybatisBatch.Method<>(H2UserMapper.class);
                // 执行批量插入
                MybatisBatchUtils.execute(sqlSessionFactory, h2UserList, mapperMethod.insert());
                throw new RuntimeException("出错了");
            });
        } catch (Exception exception) {
            for (H2User h2User : h2UserList) {
                Assertions.assertNull(userMapper.selectById(h2User.getTestId()));
            }
        }
        transactionTemplate.execute(status -> {
            MybatisBatch.Method<H2User> mapperMethod = new MybatisBatch.Method<>(H2UserMapper.class);
            // 执行批量插入
            return MybatisBatchUtils.execute(sqlSessionFactory, h2UserList, mapperMethod.insert());
        });
        for (H2User h2User : h2UserList) {
            Assertions.assertNotNull(userMapper.selectById(h2User.getTestId()));
        }
    }

    @Test
    void testInsertBatch() {
        int batchSize = 1000;
        List<H2User> h2UserList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            h2UserList.add(new H2User("test" + i));
        }
        MybatisBatch.Method<H2User> mapperMethod = new MybatisBatch.Method<>(H2UserMapper.class);
        // 执行批量插入
        List<BatchResult> batchResults = MybatisBatchUtils.execute(sqlSessionFactory, h2UserList, mapperMethod.insert());
        int[] updateCounts = batchResults.get(0).getUpdateCounts();
        Assertions.assertEquals(batchSize, updateCounts.length);
        for (int updateCount : updateCounts) {
            Assertions.assertEquals(1, updateCount);
        }

        List<Long> ids = Arrays.asList(120000L, 120001L);
        MybatisBatch.Method<H2User> method = new MybatisBatch.Method<>(H2UserMapper.class);
        MybatisBatchUtils.execute(sqlSessionFactory, ids, method.insert(H2User::ofId));
    }

    @Test
    void testInsertBatchByCustomMethod() {
        int batchSize = 1000;
        List<BatchResult> batchResults;
        int[] updateCounts;
        List<H2User> h2UserList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            h2UserList.add(new H2User("myInsertWithoutParam" + i));
        }
        MybatisBatch.Method<H2User> method = new MybatisBatch.Method<>(H2UserMapper.class);
        // 执行批量插入
        batchResults = MybatisBatchUtils.execute(sqlSessionFactory, h2UserList, method.get("myInsertWithoutParam"));
        updateCounts = batchResults.get(0).getUpdateCounts();
        Assertions.assertEquals(batchSize, updateCounts.length);
        for (int updateCount : updateCounts) {
            Assertions.assertEquals(1, updateCount);
        }

        h2UserList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            h2UserList.add(new H2User("myInsertWithParam" + i));
        }
        // 执行批量插入
        batchResults = MybatisBatchUtils.execute(sqlSessionFactory, h2UserList, method.get("myInsertWithParam", parameter -> Map.of("user1", parameter)));
        updateCounts = batchResults.get(0).getUpdateCounts();
        Assertions.assertEquals(batchSize, updateCounts.length);
        for (int updateCount : updateCounts) {
            Assertions.assertEquals(1, updateCount);
        }

        h2UserList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            h2UserList.add(new H2User("myInsertWithParam" + i));
        }
        batchResults = MybatisBatchUtils.execute(sqlSessionFactory, h2UserList, method.get("myInsertWithParam", parameter -> Map.of("user1", parameter)));
        updateCounts = batchResults.get(0).getUpdateCounts();
        Assertions.assertEquals(batchSize, updateCounts.length);
        for (int updateCount : updateCounts) {
            Assertions.assertEquals(1, updateCount);
        }
    }

    @Test
    void testDeleteByIds() {
        List<BatchResult> batchResults;
        int batchSize = 1000;
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            ids.add((long) 7120000 + i);
        }
        List<H2User> userList = new ArrayList<>();
        MybatisBatch.Method<H2User> method = new MybatisBatch.Method<>(H2UserMapper.class);
        // 转换成实体进行逻辑删除
        batchResults = MybatisBatchUtils.execute(sqlSessionFactory, ids, method.deleteById(id -> {
            H2User h2User = H2User.ofId(id);
            userList.add(h2User);
            return h2User;
        }));
        int[] updateCounts = batchResults.get(0).getUpdateCounts();
        Assertions.assertEquals(batchSize, updateCounts.length);
        for (int updateCount : updateCounts) {
            Assertions.assertEquals(0, updateCount);
        }
        for (H2User h2User : userList) {
            Assertions.assertNotNull(h2User.getLastUpdatedDt());
        }
        // 不能走填充
        batchResults = MybatisBatchUtils.execute(sqlSessionFactory, ids, method.deleteById());
        updateCounts = batchResults.get(0).getUpdateCounts();
        for (int updateCount : updateCounts) {
            Assertions.assertEquals(0, updateCount);
        }
    }

    @Test
    void testUpdateBatch() {
        int batchSize = 1000;
        List<H2User> h2UserList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            h2UserList.add(new H2User(Long.valueOf(30000 + i), "test" + i));
        }
        MybatisBatch.Method<H2User> mapperMethod = new MybatisBatch.Method<>(H2UserMapper.class);
        // 执行批量更新
        List<BatchResult> batchResults = MybatisBatchUtils.execute(sqlSessionFactory, h2UserList, mapperMethod.updateById());
        int[] updateCounts = batchResults.get(0).getUpdateCounts();
        Assertions.assertEquals(batchSize, updateCounts.length);
        for (int updateCount : updateCounts) {
            Assertions.assertEquals(0, updateCount);
        }

        List<Long> ids = Arrays.asList(120000L, 120001L);
        MybatisBatch.Method<H2User> method = new MybatisBatch.Method<>(H2UserMapper.class);

        MybatisBatchUtils.execute(sqlSessionFactory, ids, method.update(id -> Wrappers.<H2User>lambdaUpdate().set(H2User::getName, "updateTest").eq(H2User::getTestId, id)));
        MybatisBatchUtils.execute(sqlSessionFactory, ids, method.update(id -> new H2User().setName("updateTest2"), id -> Wrappers.<H2User>lambdaUpdate().eq(H2User::getTestId, id)));

        MybatisBatchUtils.execute(sqlSessionFactory, h2UserList, method.update(user -> Wrappers.<H2User>update().set("name", "updateTest3").eq("test_id", user.getTestId())));
        MybatisBatchUtils.execute(sqlSessionFactory, h2UserList, method.update(user -> new H2User("updateTests4"), p -> Wrappers.<H2User>update().eq("test_id", p.getTestId())));
    }

    @Test
    void testSaveOrUpdateBatch1() {
        int batchSize = 10;
        List<H2User> h2UserList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            h2UserList.add(new H2User(Long.valueOf(40000 + i), "test" + i));
        }
        MybatisBatch.Method<H2User> mapperMethod = new MybatisBatch.Method<>(H2UserMapper.class);
        List<BatchResult> batchResults = MybatisBatchUtils.saveOrUpdate(sqlSessionFactory, h2UserList,
                mapperMethod.insert(),
                ((sqlSession, h2User) -> userMapper.selectById(h2User.getTestId()) == null),
                mapperMethod.updateById());
        // 没有使用共享的sqlSession,由于都是新增返回还是一个批次
        int[] updateCounts = batchResults.get(0).getUpdateCounts();
        Assertions.assertEquals(batchSize, updateCounts.length);
        for (int updateCount : updateCounts) {
            Assertions.assertEquals(1, updateCount);
        }
    }

    @Test
    void testSaveOrUpdateBatch2() {
        int batchSize = 10;
        List<H2User> h2UserList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            h2UserList.add(new H2User(Long.valueOf(50000 + i), "test" + i));
        }
        MybatisBatch.Method<H2User> mapperMethod = new MybatisBatch.Method<>(H2UserMapper.class);
        List<BatchResult> batchResults = MybatisBatchUtils.saveOrUpdate(sqlSessionFactory, h2UserList,
                mapperMethod.insert(),
                ((sqlSession, h2User) -> sqlSession.selectList(H2UserMapper.class.getName() + ".selectById", h2User.getTestId()).isEmpty()),
                mapperMethod.updateById());
        // 使用共享的sqlSession,等于每次都是刷新了,批次总结果集就等于数据大小了
        Assertions.assertEquals(batchSize, batchResults.size());
        for (BatchResult batchResult : batchResults) {
            Assertions.assertEquals(batchResult.getUpdateCounts().length, 1);
            Assertions.assertEquals(1, batchResult.getUpdateCounts()[0]);
        }
    }

    @Test
    void testSaveOrUpdateBatch3() {
        var id = IdWorker.getId();
        var h2UserList = List.of(new H2User(id, "testSaveOrUpdateBatch3"), new H2User(id, "testSaveOrUpdateBatch3-1"));
        var mapperMethod = new MybatisBatch.Method<H2User>(H2UserMapper.class);
        // 由于没有共享一个sqlSession,第二条记录selectById的时候第一个sqlSession的数据还没提交,会执行插入导致主键冲突.
        Assertions.assertThrowsExactly(PersistenceException.class, () -> {
            MybatisBatchUtils.saveOrUpdate(sqlSessionFactory, h2UserList,
                mapperMethod.insert(),
                ((sqlSession, h2User) -> userMapper.selectById(h2User.getTestId()) == null),
                mapperMethod.updateById());
        });

    }

    @Test
    void testSaveOrUpdateBatch4() {
        var id = IdWorker.getId();
        var h2UserList = List.of(new H2User(id, "testSaveOrUpdateBatch4"), new H2User(id, "testSaveOrUpdateBatch4-1"));
        var mapperMethod = new MybatisBatch.Method<H2User>(H2UserMapper.class);
        // 共享一个sqlSession,每次selectById都会刷新一下,第二条记录为update.
        var batchResults = MybatisBatchUtils.saveOrUpdate(sqlSessionFactory, h2UserList,
            mapperMethod.insert(),
            ((sqlSession, h2User) -> sqlSession.selectList(mapperMethod.get("selectById").getStatementId(), h2User.getTestId()).isEmpty()),
            mapperMethod.updateById());
        var updateCounts = batchResults.get(0).getUpdateCounts();
        for (int updateCount : updateCounts) {
            Assertions.assertEquals(1, updateCount);
        }
        Assertions.assertEquals(userMapper.selectById(id).getName(), "testSaveOrUpdateBatch4-1");
    }


    @Test
    @Order(1)
    void crudTest() {
        H2User h2User = new H2User();
        h2User.setName(NQQ);
        h2User.setAge(AgeEnum.ONE);
        h2User.setDeleted(0);
        h2User.setDesc("这是一个不错的小伙子");
        h2User.setTestType(1);
        Assertions.assertEquals(1, userMapper.insert(h2User));

        log(h2User.getTestId());

        // 新增一条自定义 ID = 1 的测试删除数据
        h2User.setTestId(1L);
        h2User.setName("测试");
        userMapper.insert(h2User);
        for (int i = 0; i < 10; i++) {
            userMapper.insert(new H2User("mp" + i, AgeEnum.ONE));
        }
        Assertions.assertEquals(1, userMapper.deleteById(1L));

        Map<String, Object> map = new HashMap<>();
        map.put("name", "mp0");
        map.put("age", AgeEnum.ONE);

        // 根据 map 查询
        h2User = userMapper.selectByMap(map).get(0);
        Assertions.assertSame(AgeEnum.ONE, h2User.getAge());

        // 根据 map 删除
        Assertions.assertEquals(1, userMapper.deleteByMap(map));

        // 查询列表
        LambdaQueryWrapper<H2User> wrapper = new QueryWrapper<H2User>().lambda().like(H2User::getName, "mp");
        log(wrapper.getSqlSegment());

        List<H2User> h2UserList = userMapper.selectList(wrapper);
        Assertions.assertTrue(CollectionUtils.isNotEmpty(h2UserList));

        // 查询总数
        long count = userMapper.selectCount(wrapper.clone());
        Assertions.assertTrue(count > 1);

        // 批量删除
        Assertions.assertEquals(count, userMapper.deleteBatchIds(h2UserList.stream().map(SuperEntity::getTestId).collect(toList())));

        // 更新
        h2User = new H2User();
        h2User.setAge(AgeEnum.TWO);
        h2User.setDesc("测试置空");
        Assertions.assertEquals(1, userMapper.update(h2User, new QueryWrapper<H2User>().eq("name", NQQ)));

        log(userMapper.selectOne(new QueryWrapper<>(new H2User().setName(NQQ).setAge(AgeEnum.TWO))));

        h2User.setAge(AgeEnum.THREE);
        h2User.setDesc(null);
        Assertions.assertTrue(userMapper.update(h2User,
                new UpdateWrapper<H2User>().lambda()
                        .set(H2User::getDesc, "")
                        .eq(H2User::getName, "Jerry")) > 0);

        log(userMapper.selectOne(new QueryWrapper<>(new H2User().setName(NQQ).setAge(AgeEnum.THREE))));

        Assertions.assertEquals(1, userMapper.insert(h2User));
        // 根据主键更新 age = 18
        h2User.setAge(AgeEnum.TWO);
        Assertions.assertEquals(1, userMapper.updateById(h2User));
        long testId = h2User.getTestId();
        // https://github.com/baomidou/mybatis-plus/issues/299
        Assertions.assertEquals(1, userMapper.updateById(new H2User() {{
            setTestId(testId);
            setAge(AgeEnum.TWO);
        }}));
        // 查询一条记录
        Assertions.assertNotNull(userMapper.selectOne(new QueryWrapper<>(new H2User().setName("Joe").setTestType(1))));

        log(h2User.toString());

        // 分页查询
        IPage<H2User> h2UserPage = userMapper.selectPage(new Page<>(1, 10), null);
        if (null != h2UserPage) {
            System.out.println(h2UserPage.getTotal());
            System.out.println(h2UserPage.getSize());
        }
        Assertions.assertNotNull(userMapper.selectPage(new Page<>(1, 10), new QueryWrapper<H2User>().orderByAsc("name")));

        // 查询结果集，测试 lambda 对象后 QueryWrapper 是否参数继续传递
        QueryWrapper<H2User> qw = new QueryWrapper<>();
        qw.lambda().eq(H2User::getName, NQQ);
        List<Map<String, Object>> mapList = userMapper.selectMaps(qw);
        if (CollectionUtils.isNotEmpty(mapList)) {
            for (Map<String, Object> m : mapList) {
                System.out.println(m);
            }
        }
        Assertions.assertTrue(CollectionUtils.isNotEmpty(userMapper.selectMaps(new QueryWrapper<>(new H2User().setTestType(3)))));

        // 测试自定义注入方法
        h2User.setDesc("");
        h2User.setTestDate(new Date());
        Assertions.assertTrue(userMapper.alwaysUpdateSomeColumnById(h2User) > 0);
        Assertions.assertEquals("", userMapper.selectById(h2User.getTestId()).getDesc());
    }

    @Test
    void testCall() {
        Assertions.assertEquals("1", userMapper.testCall());
    }

    @Test
    @Order(Integer.MAX_VALUE)
    void delete() {
        userMapper.delete(new QueryWrapper<>(new H2User().setAge(AgeEnum.TWO))
                .eq("name", "Tony"));
    }

    @Test
    @Order(Integer.MAX_VALUE)
    void sqlCommentTest() {
        userMapper.delete(new QueryWrapper<H2User>().comment("deleteAllUsers"));
        String name = "name1", nameNew = "name1New";
        int insertCount = userMapper.insert(new H2User().setName(name).setAge(AgeEnum.ONE));
        Assertions.assertEquals(1, insertCount);
        int updateCount = userMapper.update(new H2User(),
                new UpdateWrapper<H2User>().comment("updateUserName1").lambda()
                        .set(H2User::getName, nameNew)
                        .eq(H2User::getName, name)
        );
        Assertions.assertEquals(1, updateCount);
        H2User h2User = userMapper.selectOne(
                new QueryWrapper<H2User>().lambda().comment("getUserByUniqueName")
                        .eq(H2User::getName, nameNew)
        );
        Assertions.assertNotNull(h2User);
        LambdaQueryWrapper<H2User> queryWrapper = new QueryWrapper<H2User>().lambda().ge(H2User::getAge, 1);
        long userCount = userMapper.selectCount(queryWrapper.comment("getUserCount"));
        Assertions.assertEquals(1, userCount);
        List<H2User> h2UserList = userMapper.selectList(queryWrapper.comment("getUserList"));
        Assertions.assertEquals(1, h2UserList.size());
        IPage<H2User> h2UserIPage = userMapper.selectPage(new Page<>(1, 10), queryWrapper.comment("getUserPage"));
        Assertions.assertEquals(1, h2UserIPage.getRecords().size());
        List<Map<String, Object>> selectMaps = userMapper.selectMaps(queryWrapper.comment("getUserMaps"));
        Assertions.assertEquals(1, selectMaps.size());
        IPage<Map<String, Object>> selectMapsPage = userMapper.selectMapsPage(new Page<>(1, 10), queryWrapper.comment("getUserMapsPage"));
        Assertions.assertEquals(1, selectMapsPage.getRecords().size());
        List<Object> selectObjs = userMapper.selectObjs(queryWrapper.comment("getUserObjs"));
        Assertions.assertEquals(1, selectObjs.size());
    }

    @Test
    void test() {
        Page<H2User> page = new Page<>();
        userMapper.testPage1(new H2User(), page);
        userMapper.testPage2(page, new H2User());
    }

    @Test
    void testCountLong() {
        Long count = userMapper.selectCountLong();
        System.out.println(count);
    }

    @Test
    void testUpdateByWrapper() {
        var h2User = new H2User();
        userMapper.insert(h2User);
        var wrapper = Wrappers.<H2User>lambdaUpdate().set(H2User::getName, "testUpdateByWrapper").eq(H2User::getTestId, h2User.getTestId());
        Assertions.assertEquals(userMapper.update(wrapper), 1);
        Assertions.assertEquals(userMapper.selectById(h2User.getTestId()).getName(), "testUpdateByWrapper");
    }

}
