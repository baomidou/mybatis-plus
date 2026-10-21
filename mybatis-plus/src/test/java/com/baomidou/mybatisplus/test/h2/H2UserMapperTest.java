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
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.entity.SuperEntity;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.mapper.H2UserMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Mybatis Plus H2 Junit Test
 *
 * @author hubin
 * @since 2018-06-05
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
class H2UserMapperTest extends H2TestSupport {

    @Autowired
    protected H2UserMapper userMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void testMapperSaveBatch() {
        var list = List.of(new H2User("秋秋1"), new H2User("秋秋2"));
        List<BatchResult> batchResults = userMapper.insert(list);
        assertThat(SqlHelper.retBool(batchResults)).isTrue();
        assertThat(batchResults.getFirst().getUpdateCounts().length).isEqualTo(2);
    }

    @Test
    void testMapperUpdateBatch() {
        var list = List.of(new H2User("秋秋1"), new H2User("秋秋2"));
        userMapper.insert(list);
        for (H2User h2User : list) {
            h2User.setName("test" + 1);
        }
        List<BatchResult> batchResults = userMapper.updateById(list);
        assertThat(SqlHelper.retBool(batchResults)).isTrue();
        assertThat(batchResults.getFirst().getUpdateCounts().length).isEqualTo(2);
    }

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
                assertThat(userMapper.selectById(h2User.getTestId())).isNull();
            }
        }
        transactionTemplate.execute(status -> {
            MybatisBatch.Method<H2User> mapperMethod = new MybatisBatch.Method<>(H2UserMapper.class);
            // 执行批量插入
            return MybatisBatchUtils.execute(sqlSessionFactory, h2UserList, mapperMethod.insert());
        });
        for (H2User h2User : h2UserList) {
            assertThat(userMapper.selectById(h2User.getTestId())).isNotNull();
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
        List<BatchResult> batchResults = MybatisBatchUtils
                .execute(sqlSessionFactory, h2UserList, mapperMethod.insert());
        assertThat(SqlHelper.retBool(batchResults)).isTrue();
        int[] updateCounts = batchResults.getFirst().getUpdateCounts();
        assertThat(updateCounts.length).isEqualTo(batchSize);
        List<Long> ids = Arrays.asList(120000L, 120001L);
        MybatisBatch.Method<H2User> method = new MybatisBatch.Method<>(H2UserMapper.class);
        assertThat(SqlHelper.retBool(MybatisBatchUtils.execute(sqlSessionFactory, ids, method.insert(H2User::ofId))))
                .isTrue();
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
        assertThat(SqlHelper.retBool(batchResults)).isTrue();
        updateCounts = batchResults.getFirst().getUpdateCounts();
        assertThat(updateCounts.length).isEqualTo(batchSize);
        h2UserList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            h2UserList.add(new H2User("myInsertWithParam" + i));
        }
        // 执行批量插入
        batchResults = MybatisBatchUtils.execute(
                sqlSessionFactory, h2UserList,
                method.get("myInsertWithParam", parameter -> Map.of("user1", parameter)));
        assertThat(SqlHelper.retBool(batchResults)).isTrue();
        updateCounts = batchResults.getFirst().getUpdateCounts();
        assertThat(updateCounts.length).isEqualTo(batchSize);
        h2UserList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            h2UserList.add(new H2User("myInsertWithParam" + i));
        }
        batchResults = MybatisBatchUtils.execute(
                sqlSessionFactory, h2UserList,
                method.get("myInsertWithParam", parameter -> Map.of("user1", parameter)));
        assertThat(SqlHelper.retBool(batchResults)).isTrue();
        updateCounts = batchResults.getFirst().getUpdateCounts();
        assertThat(updateCounts.length).isEqualTo(batchSize);
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
        assertThat(SqlHelper.retBool(batchResults)).isFalse();
        int[] updateCounts = batchResults.getFirst().getUpdateCounts();
        assertThat(updateCounts.length).isEqualTo(batchSize);
        for (H2User h2User : userList) {
            assertThat(h2User.getLastUpdatedDt()).isNotNull();
        }
        // 不能走填充
        batchResults = MybatisBatchUtils.execute(sqlSessionFactory, ids, method.deleteById());
        assertThat(SqlHelper.retBool(batchResults)).isFalse();
        updateCounts = batchResults.getFirst().getUpdateCounts();
        assertThat(updateCounts.length).isEqualTo(batchSize);
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
        List<BatchResult> batchResults = MybatisBatchUtils
                .execute(sqlSessionFactory, h2UserList, mapperMethod.updateById());
        assertThat(SqlHelper.retBool(batchResults)).isFalse();
        int[] updateCounts = batchResults.getFirst().getUpdateCounts();
        assertThat(updateCounts.length).isEqualTo(batchSize);

        List<Long> ids = Arrays.asList(120000L, 120001L);
        MybatisBatch.Method<H2User> method = new MybatisBatch.Method<>(H2UserMapper.class);

        assertThat(
                SqlHelper.retBool(
                        MybatisBatchUtils.execute(
                                sqlSessionFactory, ids,
                                method.update(
                                        id -> Wrappers.<H2User>lambdaUpdate().set(H2User::getName, "updateTest")
                                                .eq(H2User::getTestId, id)))))
                .isFalse();
        assertThat(
                SqlHelper.retBool(
                        MybatisBatchUtils.execute(
                                sqlSessionFactory, ids,
                                method.update(
                                        id -> new H2User().setName("updateTest2"),
                                        id -> Wrappers.<H2User>lambdaUpdate().eq(H2User::getTestId, id)))))
                .isFalse();

        assertThat(
                SqlHelper.retBool(
                        MybatisBatchUtils.execute(
                                sqlSessionFactory, h2UserList,
                                method.update(
                                        user -> Wrappers.<H2User>update().set("name", "updateTest3")
                                                .eq("test_id", user.getTestId())))))
                .isFalse();
        assertThat(
                SqlHelper.retBool(
                        MybatisBatchUtils.execute(
                                sqlSessionFactory, h2UserList,
                                method.update(
                                        user -> new H2User("updateTests4"),
                                        p -> Wrappers.<H2User>update().eq("test_id", p.getTestId())))))
                .isFalse();
    }

    @Test
    void testSaveOrUpdateBatch1() {
        int batchSize = 10;
        List<H2User> h2UserList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            h2UserList.add(new H2User(Long.valueOf(40000 + i), "test" + i));
        }
        MybatisBatch.Method<H2User> mapperMethod = new MybatisBatch.Method<>(H2UserMapper.class);
        List<BatchResult> batchResults = MybatisBatchUtils.saveOrUpdate(
                sqlSessionFactory, h2UserList, mapperMethod.insert(),
                ((sqlSession, h2User) -> userMapper.selectById(h2User.getTestId()) == null), mapperMethod.updateById());
        // 没有使用共享的sqlSession,由于都是新增返回还是一个批次
        assertThat(SqlHelper.retBool(batchResults)).isTrue();
        int[] updateCounts = batchResults.getFirst().getUpdateCounts();
        assertThat(updateCounts.length).isEqualTo(batchSize);
    }

    @Test
    void testSaveOrUpdateBatchMapper1() {
        int batchSize = 10;
        List<H2User> h2UserList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            h2UserList.add(new H2User(Long.valueOf(140000 + i), "test" + i));
        }
        List<BatchResult> batchResults = userMapper.insertOrUpdate(h2UserList);
        assertThat(SqlHelper.retBool(batchResults)).isTrue();
        assertThat(batchResults.size()).isEqualTo(batchSize);
        // 使用共享的sqlSession,等于每次都是刷新了,批次总结果集就等于数据大小了
        for (BatchResult batchResult : batchResults) {
            assertThat(batchResult.getUpdateCounts().length).isEqualTo(1);
            assertThat(batchResult.getUpdateCounts()[0]).isEqualTo(1);
        }
    }

    @Test
    void testSaveOrUpdateBatchMapper2() {
        int batchSize = 10;
        List<H2User> h2UserList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            h2UserList.add(new H2User(Long.valueOf(40000 + i), "test" + i));
        }
        List<BatchResult> batchResults = userMapper.insertOrUpdate(
                h2UserList, ((sqlSession, h2User) -> userMapper.selectById(h2User.getTestId()) == null));
        assertThat(SqlHelper.retBool(batchResults)).isTrue();
        // 没有使用共享的sqlSession,由于都是新增返回还是一个批次
        int[] updateCounts = batchResults.getFirst().getUpdateCounts();
        assertThat(updateCounts.length).isEqualTo(batchSize);
    }

    @Test
    void testSaveOrUpdateBatchMapper3() {
        var id = IdWorker.getId();
        var h2UserList = List
                .of(new H2User(id, "testSaveOrUpdateBatchMapper3"), new H2User(id, "testSaveOrUpdateBatchMapper3-1"));
        // 由于没有共享一个sqlSession,第二条记录selectById的时候第一个sqlSession的数据还没提交,会执行插入导致主键冲突.
        assertThatThrownBy(
                () -> userMapper.insertOrUpdate(
                        h2UserList, ((sqlSession, h2User) -> userMapper.selectById(h2User.getTestId()) == null)))
                .isExactlyInstanceOf(PersistenceException.class);
    }

    @Test
    void testSaveOrUpdateBatchMapper4() {
        var id = IdWorker.getId();
        var h2UserList = List
                .of(new H2User(id, "testSaveOrUpdateBatchMapper4"), new H2User(id, "testSaveOrUpdateBatchMapper4-1"));
        var mapperMethod = new MybatisBatch.Method<H2User>(H2UserMapper.class);
        // 共享一个sqlSession,每次selectById都会刷新一下,第二条记录为update.
        var batchResults = userMapper.insertOrUpdate(
                h2UserList, ((sqlSession, h2User) -> sqlSession
                        .selectList(mapperMethod.get("selectById").getStatementId(), h2User.getTestId()).isEmpty()));
        assertThat(SqlHelper.retBool(batchResults)).isTrue();
        assertThat(batchResults.stream().flatMapToInt(r -> IntStream.of(r.getUpdateCounts())).count())
                .isEqualTo(h2UserList.size());
        assertThat(userMapper.selectById(id).getName()).isEqualTo("testSaveOrUpdateBatchMapper4-1");
    }

    @Test
    void testRemoveByIds() {
        assertThat(userMapper.deleteByIds(List.of(666666661, "2"), false))
                .isEqualTo(userMapper.deleteByIds(List.of(666666661, "2")));
        H2User h2User = new H2User("testRemoveByIds");
        assertThat(userMapper.insert(h2User)).isEqualTo(1);
        assertThat(userMapper.deleteByIds(List.of(h2User))).isEqualTo(1);
        assertThat(userMapper.getById(h2User.getTestId()).getLastUpdatedDt()).isNotNull();
        h2User = new H2User("testRemoveByIds");
        assertThat(userMapper.insert(h2User)).isEqualTo(1);
        assertThat(userMapper.deleteByIds(List.of(h2User), false)).isEqualTo(1);
        assertThat(userMapper.getById(h2User.getTestId()).getLastUpdatedDt()).isNull();
    }

    @Test
    void testSaveOrUpdateBatch2() {
        int batchSize = 10;
        List<H2User> h2UserList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            h2UserList.add(new H2User(Long.valueOf(50000 + i), "test" + i));
        }
        MybatisBatch.Method<H2User> mapperMethod = new MybatisBatch.Method<>(H2UserMapper.class);
        List<BatchResult> batchResults = MybatisBatchUtils.saveOrUpdate(
                sqlSessionFactory, h2UserList, mapperMethod.insert(),
                ((sqlSession, h2User) -> sqlSession
                        .selectList(H2UserMapper.class.getName() + ".selectById", h2User.getTestId()).isEmpty()),
                mapperMethod.updateById());
        // 使用共享的sqlSession,等于每次都是刷新了,批次总结果集就等于数据大小了
        assertThat(batchResults.size()).isEqualTo(batchSize);
        for (BatchResult batchResult : batchResults) {
            assertThat(batchResult.getUpdateCounts().length).isEqualTo(1);
            assertThat(batchResult.getUpdateCounts()[0]).isEqualTo(1);
        }
    }

    @Test
    void testSaveOrUpdateBatch3() {
        var id = IdWorker.getId();
        var h2UserList = List.of(new H2User(id, "testSaveOrUpdateBatch3"), new H2User(id, "testSaveOrUpdateBatch3-1"));
        var mapperMethod = new MybatisBatch.Method<H2User>(H2UserMapper.class);
        // 由于没有共享一个sqlSession,第二条记录selectById的时候第一个sqlSession的数据还没提交,会执行插入导致主键冲突.
        assertThatThrownBy(
                () -> MybatisBatchUtils.saveOrUpdate(
                        sqlSessionFactory, h2UserList, mapperMethod.insert(),
                        ((sqlSession, h2User) -> userMapper.selectById(h2User.getTestId()) == null),
                        mapperMethod.updateById()))
                .isExactlyInstanceOf(PersistenceException.class);

    }

    @Test
    void testSaveOrUpdateBatch4() {
        var id = IdWorker.getId();
        var h2UserList = List.of(new H2User(id, "testSaveOrUpdateBatch4"), new H2User(id, "testSaveOrUpdateBatch4-1"));
        var mapperMethod = new MybatisBatch.Method<H2User>(H2UserMapper.class);
        // 共享一个sqlSession,每次selectById都会刷新一下,第二条记录为update.
        var batchResults = MybatisBatchUtils.saveOrUpdate(
                sqlSessionFactory, h2UserList, mapperMethod.insert(),
                ((sqlSession, h2User) -> sqlSession
                        .selectList(mapperMethod.get("selectById").getStatementId(), h2User.getTestId()).isEmpty()),
                mapperMethod.updateById());
        var updateCounts = batchResults.getFirst().getUpdateCounts();
        for (int updateCount : updateCounts) {
            assertThat(updateCount).isEqualTo(1);
        }
        assertThat(userMapper.selectById(id).getName()).isEqualTo("testSaveOrUpdateBatch4-1");
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
        assertThat(userMapper.insert(h2User)).isEqualTo(1);

        log(h2User.getTestId());

        // 新增一条自定义 ID = 1 的测试删除数据
        h2User.setTestId(1L);
        h2User.setName("测试");
        userMapper.insert(h2User);
        for (int i = 0; i < 10; i++) {
            userMapper.insert(new H2User("mp" + i, AgeEnum.ONE));
        }
        assertThat(userMapper.deleteById(1L)).isEqualTo(1);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "mp0");
        map.put("age", AgeEnum.ONE);

        // 根据 map 查询
        h2User = userMapper.selectByMap(map).getFirst();
        assertThat(h2User.getAge()).isSameAs(AgeEnum.ONE);

        // 根据 map 删除
        assertThat(userMapper.deleteByMap(map)).isEqualTo(1);

        // 查询列表
        LambdaQueryWrapper<H2User> wrapper = new QueryWrapper<H2User>().lambda().like(H2User::getName, "mp");
        log(wrapper.getSqlSegment());

        List<H2User> h2UserList = userMapper.selectList(wrapper);
        assertThat(CollectionUtils.isNotEmpty(h2UserList)).isTrue();

        // 查询总数
        long count = userMapper.selectCount(wrapper.clone());
        assertThat(count > 1).isTrue();

        // 批量删除
        assertThat(userMapper.deleteByIds(h2UserList.stream().map(SuperEntity::getTestId).collect(toList())))
                .isEqualTo(count);

        // 更新
        h2User = new H2User();
        h2User.setAge(AgeEnum.TWO);
        h2User.setDesc("测试置空");
        assertThat(userMapper.update(h2User, new QueryWrapper<H2User>().eq("name", NQQ))).isEqualTo(1);

        log(userMapper.selectOne(new QueryWrapper<>(new H2User().setName(NQQ).setAge(AgeEnum.TWO))));

        h2User.setAge(AgeEnum.THREE);
        h2User.setDesc(null);
        assertThat(
                userMapper.update(
                        h2User,
                        new UpdateWrapper<H2User>().lambda().set(H2User::getDesc, "").eq(H2User::getName, "Jerry")) > 0)
                .isTrue();

        log(userMapper.selectOne(new QueryWrapper<>(new H2User().setName(NQQ).setAge(AgeEnum.THREE))));

        assertThat(userMapper.insert(h2User)).isEqualTo(1);
        // 根据主键更新 age = 18
        h2User.setAge(AgeEnum.TWO);
        assertThat(userMapper.updateById(h2User)).isEqualTo(1);
        long testId = h2User.getTestId();
        // https://github.com/baomidou/mybatis-plus/issues/299
        assertThat(userMapper.updateById(new H2User() {

            {
                setTestId(testId);
                setAge(AgeEnum.TWO);
            }
        })).isEqualTo(1);
        // 查询一条记录
        assertThat(userMapper.selectOne(new QueryWrapper<>(new H2User().setName("Joe").setTestType(1)))).isNotNull();

        log(h2User.toString());

        // 分页查询
        IPage<H2User> h2UserPage = userMapper.selectPage(new Page<>(1, 10), null);
        if (null != h2UserPage) {
            System.out.println(h2UserPage.getTotal());
            System.out.println(h2UserPage.getSize());
        }
        assertThat(userMapper.selectPage(new Page<>(1, 10), new QueryWrapper<H2User>().orderByAsc("name"))).isNotNull();

        // 查询结果集，测试 lambda 对象后 QueryWrapper 是否参数继续传递
        QueryWrapper<H2User> qw = new QueryWrapper<>();
        qw.lambda().eq(H2User::getName, NQQ);
        List<Map<String, Object>> mapList = userMapper.selectMaps(qw);
        if (CollectionUtils.isNotEmpty(mapList)) {
            for (Map<String, Object> m : mapList) {
                System.out.println(m);
            }
        }
        assertThat(CollectionUtils.isNotEmpty(userMapper.selectMaps(new QueryWrapper<>(new H2User().setTestType(3)))))
                .isTrue();

        // 测试自定义注入方法
        h2User.setDesc("");
        h2User.setTestDate(new Date());
        assertThat(userMapper.alwaysUpdateSomeColumnById(h2User) > 0).isTrue();
        assertThat(userMapper.selectById(h2User.getTestId()).getDesc()).isEqualTo("");
    }

    @Test
    void testCall() {
        assertThat(userMapper.testCall()).isEqualTo("1");
    }

    /**
     * 逻辑删除：delete 走 UPDATE 打 deleted 标记，MP 的查询自动过滤，裸 SQL 仍能读到该行。
     */
    @Test
    @Order(Integer.MAX_VALUE)
    void delete() {
        H2User h2User = new H2User(IdWorker.getId(), "testDeleteLogic").setAge(AgeEnum.TWO);
        assertThat(userMapper.insert(h2User)).isEqualTo(1);
        QueryWrapper<H2User> wrapper = new QueryWrapper<>(new H2User().setAge(AgeEnum.TWO))
                .eq("name", "testDeleteLogic");
        // 实体只当参数容器，delete 的条件来自 wrapper 本身
        assertThat(userMapper.delete(wrapper)).isEqualTo(1);
        assertThat(userMapper.selectById(h2User.getTestId())).isNull();
        assertThat(userMapper.getById(h2User.getTestId())).isNotNull().satisfies(raw -> {
            assertThat(raw.getName()).isEqualTo("testDeleteLogic");
            assertThat(raw.getDeleted()).isEqualTo(1);
        });
    }

    @Test
    @Order(Integer.MAX_VALUE)
    void sqlCommentTest() {
        userMapper.delete(new QueryWrapper<H2User>().comment("deleteAllUsers"));
        String name = "name1", nameNew = "name1New";
        int insertCount = userMapper.insert(new H2User().setName(name).setAge(AgeEnum.ONE));
        assertThat(insertCount).isEqualTo(1);
        int updateCount = userMapper.update(
                new H2User(), new UpdateWrapper<H2User>().comment("updateUserName1").lambda()
                        .set(H2User::getName, nameNew).eq(H2User::getName, name));
        assertThat(updateCount).isEqualTo(1);
        H2User h2User = userMapper.selectOne(
                new QueryWrapper<H2User>().lambda().comment("getUserByUniqueName").eq(H2User::getName, nameNew));
        assertThat(h2User).isNotNull();
        LambdaQueryWrapper<H2User> queryWrapper = new QueryWrapper<H2User>().lambda().ge(H2User::getAge, 1);
        long userCount = userMapper.selectCount(queryWrapper.comment("getUserCount"));
        assertThat(userCount).isEqualTo(1);
        List<H2User> h2UserList = userMapper.selectList(queryWrapper.comment("getUserList"));
        assertThat(h2UserList.size()).isEqualTo(1);
        IPage<H2User> h2UserIPage = userMapper.selectPage(new Page<>(1, 10), queryWrapper.comment("getUserPage"));
        assertThat(h2UserIPage.getRecords().size()).isEqualTo(1);
        List<Map<String, Object>> selectMaps = userMapper.selectMaps(queryWrapper.comment("getUserMaps"));
        assertThat(selectMaps.size()).isEqualTo(1);
        IPage<Map<String, Object>> selectMapsPage = userMapper
                .selectMapsPage(new Page<>(1, 10), queryWrapper.comment("getUserMapsPage"));
        assertThat(selectMapsPage.getRecords().size()).isEqualTo(1);
        List<Object> selectObjs = userMapper.selectObjs(queryWrapper.comment("getUserObjs"));
        assertThat(selectObjs.size()).isEqualTo(1);
    }

    /**
     * 测试自定义注入的分页方法：分页插件按参数类型找 IPage，不看 @Param 名字， 参数位置调换或名字对不上照样分页；searchCount=false 时不查总数。
     */
    @Test
    void test() {
        Page<H2User> page = new Page<>();
        IPage<H2User> result = userMapper.testPage1(new H2User(), page);
        // 分页结果原地写回传入的 page，返回值就是它
        assertThat(result).isSameAs(page);
        assertThat(result.getSize()).isEqualTo(10L);
        assertThat(result.getRecords()).hasSize((int) Math.min(result.getSize(), result.getTotal()));
        assertThat(result.getPages()).isEqualTo((result.getTotal() + result.getSize() - 1) / result.getSize());

        Page<H2User> firstPage = new Page<>(1, 3);
        Page<H2User> secondPage = new Page<>(2, 3);
        // testPage2 的 IPage 挂在 @Param("user") 上，"page" 反而是实体参数
        assertThat(userMapper.testPage2(secondPage, new H2User())).isSameAs(secondPage);
        userMapper.testPage1(new H2User(), firstPage);
        assertThat(secondPage.getRecords()).hasSize(3);
        List<Long> firstIds = firstPage.getRecords().stream().map(H2User::getTestId).collect(toList());
        List<Long> secondIds = secondPage.getRecords().stream().map(H2User::getTestId).collect(toList());
        // 主键不重叠才说明 offset 真的加上了
        assertThat(firstIds).isNotEmpty().doesNotContainAnyElementsOf(secondIds);

        Page<H2User> noCount = new Page<>(1, 3, false);
        userMapper.testPage1(new H2User(), noCount);
        assertThat(noCount.getRecords()).hasSize(3);
        assertThat(noCount.getTotal()).as("searchCount=false 不该发 count 语句").isZero();
    }

    @Test
    void testCountLong() {
        long physical = userMapper.selectCountLong();
        long notDeleted = userMapper.selectCount(null);
        assertThat(physical).isPositive();
        H2User h2User = new H2User(IdWorker.getId(), "testCountLong").setAge(AgeEnum.ONE);
        userMapper.insert(h2User);
        // 自定义 @Select 返回包装类型 Long 也能正常映射
        assertThat(userMapper.selectCountLong()).isEqualTo(physical + 1);
        assertThat(userMapper.selectCount(null)).isEqualTo(notDeleted + 1);
        userMapper.deleteById(h2User.getTestId());
        // count(*) 数得到逻辑删除的行，MP 注入的 selectCount 数不到
        assertThat(userMapper.selectCountLong()).isEqualTo(physical + 1);
        assertThat(userMapper.selectCount(null)).isEqualTo(notDeleted);
    }

    @Test
    void testUpdateByWrapper() {
        var h2User = new H2User();
        userMapper.insert(h2User);
        var wrapper = Wrappers.<H2User>lambdaUpdate().set(H2User::getName, "testUpdateByWrapper")
                .eq(H2User::getTestId, h2User.getTestId());
        assertThat(userMapper.update(wrapper)).isEqualTo(1);
        assertThat(userMapper.selectById(h2User.getTestId()).getName()).isEqualTo("testUpdateByWrapper");
    }

    @Test
    void testSaveOrUpdate() {
        var h2User = new H2User();
        userMapper.insertOrUpdate(h2User);
        assertThat(h2User.getTestId()).isNotNull();
        assertThat(h2User.getLastUpdatedDt()).isNull();
        h2User.setName("test");
        userMapper.insertOrUpdate(h2User);
        assertThat(h2User.getLastUpdatedDt()).isNotNull();
    }

}
