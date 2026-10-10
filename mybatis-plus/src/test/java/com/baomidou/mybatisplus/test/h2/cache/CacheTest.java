package com.baomidou.mybatisplus.test.h2.cache;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.h2.cache.mapper.CacheMapper;
import com.baomidou.mybatisplus.test.h2.cache.mapper.CacheRefMapper;
import com.baomidou.mybatisplus.test.h2.cache.model.CacheModel;
import com.baomidou.mybatisplus.test.h2.cache.service.ICacheService;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Collections;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-cache-h2.xml"})
class CacheTest {

    @Autowired
    private ICacheService cacheService;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private CacheMapper cacheMapper;

    @Autowired
    private CacheRefMapper cacheRefMapper;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    @Order(1)
    void testPageCache() {
        Cache cache = getCache();
        IPage<CacheModel> cacheModelIPage1 = cacheService.page(new Page<>(1, 3), new QueryWrapper<>());
        IPage<CacheModel> cacheModelIPage2 = cacheService.page(new Page<>(1, 3), new QueryWrapper<>());
        Assertions.assertEquals(2, cache.getSize());
        Assertions.assertEquals(cacheModelIPage1.getTotal(), cacheModelIPage2.getTotal());
        Assertions.assertEquals(cacheModelIPage1.getRecords().size(), cacheModelIPage2.getRecords().size());
        IPage<CacheModel> cacheModelIPage3 = cacheService.page(new Page<>(2, 3), new QueryWrapper<>());
        Assertions.assertEquals(cacheModelIPage1.getTotal(), cacheModelIPage3.getTotal());
        Assertions.assertEquals(2, cacheModelIPage3.getRecords().size());
        Assertions.assertEquals(3, cache.getSize());
        IPage<CacheModel> cacheModelIPage4 = cacheService.page(new Page<>(2, 3, false), new QueryWrapper<>());
        Assertions.assertEquals(0L, cacheModelIPage4.getTotal());
        Assertions.assertEquals(2, cacheModelIPage4.getRecords().size());
        Assertions.assertEquals(3, cache.getSize());
        IPage<CacheModel> cacheModelIPage5 = cacheService.page(new Page<>(2, 3, true), new QueryWrapper<>());
        Assertions.assertEquals(cacheModelIPage5.getTotal(), cacheModelIPage3.getTotal());
        Assertions.assertEquals(2, cacheModelIPage5.getRecords().size());
        IPage<CacheModel> cacheModelIPage6 = cacheService.page(new Page<>(1, 3, true), new QueryWrapper<CacheModel>().ge("id", 2L));
        Assertions.assertEquals(4, cacheModelIPage6.getTotal());
        Assertions.assertEquals(3, cacheModelIPage6.getRecords().size());
        IPage<CacheModel> cacheModelIPage7 = cacheService.page(new Page<>(1, 3, false), new QueryWrapper<CacheModel>().ge("id", 2L));
        Assertions.assertEquals(0L, cacheModelIPage7.getTotal());
        Assertions.assertEquals(3, cacheModelIPage7.getRecords().size());
        IPage<CacheModel> cacheModelIPage8 = cacheService.page(new Page<>(1, 3, false), new QueryWrapper<CacheModel>().ge("id", 3L));
        Assertions.assertEquals(0L, cacheModelIPage8.getTotal());
        Assertions.assertEquals(3, cacheModelIPage8.getRecords().size());
        cacheModelIPage8 = cacheService.page(new Page<>(1, 3, false), new QueryWrapper<CacheModel>().ge("id", 3L));
        Assertions.assertEquals(0L, cacheModelIPage8.getTotal());
        Assertions.assertEquals(3, cacheModelIPage8.getRecords().size());
        IPage<CacheModel> cacheModelIPage9 = cacheService.page(new Page<>(1, 3, true), new QueryWrapper<CacheModel>().ge("id", 3L));
        Assertions.assertEquals(3L, cacheModelIPage9.getTotal());
        Assertions.assertEquals(3, cacheModelIPage9.getRecords().size());
        cacheModelIPage9 = cacheService.page(new Page<>(1, 3, true), new QueryWrapper<CacheModel>().ge("id", 3L));
        Assertions.assertEquals(3L, cacheModelIPage9.getTotal());
        Assertions.assertEquals(3, cacheModelIPage9.getRecords().size());
    }

    @Test
    @Order(2)
    void testCleanBatchCache() {
        CacheModel model = new CacheModel("靓仔");
        cacheService.save(model);
        Cache cache = getCache();
        Assertions.assertEquals(0, cache.getSize());
        cacheService.getById(model.getId());
        Assertions.assertEquals(1, cache.getSize());
        cacheService.updateBatchById(Collections.singletonList(new CacheModel(model.getId(), "旺仔")));
        Assertions.assertEquals(0, cache.getSize());
        Assertions.assertEquals("旺仔", cacheService.getById(model.getId()).getName());
        Assertions.assertEquals(1, cache.getSize());
    }

    @Test
    @Order(3)
    void testBatchTransactionalClear1() {
        Cache cache = getCache();
        long id = cacheService.testBatchTransactionalClear1();
        Assertions.assertEquals(0, cache.getSize());
        CacheModel cacheModel = cacheService.getById(id);
        Assertions.assertEquals(1, cache.getSize());
        Assertions.assertEquals("旺仔", cacheModel.getName());
    }

    @Test
    @Order(4)
    void testBatchTransactionalClear2() {
        long id = cacheService.testBatchTransactionalClear2();
        Cache cache = getCache();
        Assertions.assertEquals(0, cache.getSize());
        CacheModel cacheModel = cacheService.getById(id);
        Assertions.assertEquals(1, cache.getSize());
        Assertions.assertEquals("小红", cacheModel.getName());
    }

    @Test
    @Order(5)
    void testBatchTransactionalClear3() {
        long id = cacheService.testBatchTransactionalClear3();
        Cache cache = getCache();
        Assertions.assertEquals(1, cache.getSize());
        CacheModel cacheModel = cacheService.getById(id);
        Assertions.assertEquals(1, cache.getSize());
        Assertions.assertEquals("小红", cacheModel.getName());
    }

    @Test
    @Order(6)
    void testBatchTransactionalClear4() {
        long id = cacheService.testBatchTransactionalClear4();
        Cache cache = getCache();
        Assertions.assertEquals(0, cache.getSize());
        CacheModel cacheModel = cacheService.getById(id);
        Assertions.assertEquals(1, cache.getSize());
        Assertions.assertEquals("旺仔", cacheModel.getName());
    }

    @Test
    @Order(7)
    void testBatchTransactionalClear5() {
        long id = cacheService.testBatchTransactionalClear5();
        Cache cache = getCache();
        Assertions.assertEquals(0, cache.getSize());
        CacheModel cacheModel = cacheService.getById(id);
        Assertions.assertEquals(1, cache.getSize());
        Assertions.assertNull(cacheModel);
    }

    @Test
    @Order(8)
    void testBatchTransactionalClear6() {
        long id = cacheService.testBatchTransactionalClear6();
        Cache cache = getCache();
        Assertions.assertEquals(0, cache.getSize());
        CacheModel cacheModel = cacheService.getById(id);
        Assertions.assertEquals(1, cache.getSize());
        Assertions.assertNull(cacheModel);
    }

    @Test
    @Order(9)
    void testBatchTransactionalClear7() {
        long id = cacheService.testBatchTransactionalClear7();
        Cache cache = getCache();
        Assertions.assertEquals(0, cache.getSize());
        CacheModel cacheModel = cacheService.getById(id);
        Assertions.assertEquals(1, cache.getSize());
        Assertions.assertNull(cacheModel);
    }

    @Test
    @Order(10)
    @Transactional
    void testBaseMapperBatchInsertClearsFirstLevelCache() {
        CacheModel model = new CacheModel(10001L, "batch-insert");
        Assertions.assertNull(cacheMapper.selectById(model.getId()));

        cacheMapper.insert(Collections.singletonList(model));

        Assertions.assertEquals("batch-insert", cacheMapper.selectById(model.getId()).getName());
    }

    @Test
    @Order(11)
    @Transactional
    void testBaseMapperBatchUpdateClearsFirstLevelCache() {
        CacheModel model = new CacheModel(10002L, "before-batch");
        cacheMapper.insert(model);
        Assertions.assertEquals("before-batch", cacheMapper.selectById(model.getId()).getName());

        cacheMapper.updateById(Collections.singletonList(new CacheModel(model.getId(), "after-batch")));

        Assertions.assertEquals("after-batch", cacheMapper.selectById(model.getId()).getName());
    }

    @Test
    @Order(12)
    @Transactional
    void testBaseMapperBatchInsertOrUpdateClearsFirstLevelCacheAfterInsert() {
        CacheModel model = new CacheModel(10003L, "batch-insert-or-update");
        Assertions.assertNull(cacheMapper.selectById(model.getId()));

        cacheMapper.insertOrUpdate(Collections.singletonList(model));

        Assertions.assertEquals("batch-insert-or-update", cacheMapper.selectById(model.getId()).getName());
    }

    @Test
    @Order(13)
    @Transactional
    void testBaseMapperBatchInsertOrUpdateClearsFirstLevelCacheAfterUpdate() {
        CacheModel model = new CacheModel(10004L, "before-batch");
        cacheMapper.insert(model);
        Assertions.assertEquals("before-batch", cacheMapper.selectById(model.getId()).getName());

        cacheMapper.insertOrUpdate(Collections.singletonList(new CacheModel(model.getId(), "after-batch")));

        Assertions.assertEquals("after-batch", cacheMapper.selectById(model.getId()).getName());
    }

    @Test
    @Order(14)
    void testBaseMapperBatchClearsSecondLevelCache() {
        Cache cache = getCache();
        cache.clear();
        CacheModel model = new CacheModel("before-batch");
        cacheMapper.insert(model);
        cacheMapper.selectById(model.getId());
        Assertions.assertEquals(1, cache.getSize());

        cacheMapper.updateById(Collections.singletonList(new CacheModel(model.getId(), "after-batch")));

        Assertions.assertEquals(0, cache.getSize());
        Assertions.assertEquals("after-batch", cacheMapper.selectById(model.getId()).getName());
    }

    @Test
    @Order(15)
    @Transactional
    void testBaseMapperBatchClearsFirstLevelCacheAfterPartialFailure() {
        CacheModel inserted = new CacheModel(10005L, "inserted-before-failure");
        Assertions.assertNull(cacheMapper.selectById(inserted.getId()));
        CacheModel duplicate = new CacheModel(1L, "duplicate");

        Assertions.assertThrows(PersistenceException.class,
            () -> cacheMapper.insert(Arrays.asList(inserted, duplicate), 1));

        Assertions.assertEquals("inserted-before-failure", cacheMapper.selectById(inserted.getId()).getName());
    }

    @Test
    @Order(16)
    void testBaseMapperBatchDoesNotPublishStaleSecondLevelCacheOnCommit() {
        Cache cache = getCache();
        cache.clear();
        Long id = new TransactionTemplate(transactionManager).execute(status -> {
            CacheModel model = new CacheModel(10006L, "before-batch");
            cacheMapper.insert(model);
            Assertions.assertEquals("before-batch", cacheMapper.selectById(model.getId()).getName());

            cacheMapper.updateById(Collections.singletonList(new CacheModel(model.getId(), "after-batch")));
            return model.getId();
        });

        Assertions.assertEquals("after-batch", cacheMapper.selectById(id).getName());
    }

    @Test
    @Order(17)
    void testBaseMapperBatchClearsReferencedSecondLevelCache() {
        Cache cache = getCache();
        cache.clear();
        Long id = new TransactionTemplate(transactionManager).execute(status -> {
            CacheModel model = new CacheModel(10007L, "before-batch");
            cacheMapper.insert(model);
            Assertions.assertEquals("before-batch", cacheRefMapper.selectById(model.getId()).getName());

            cacheRefMapper.updateById(Collections.singletonList(new CacheModel(model.getId(), "after-batch")));
            return model.getId();
        });

        Assertions.assertEquals("after-batch", cacheMapper.selectById(id).getName());
    }

    @Test
    @Order(18)
    void testBaseMapperBatchCacheInvalidationRollsBackWithTransaction() {
        Cache cache = getCache();
        cache.clear();
        CacheModel model = new CacheModel(10008L, "before-batch");
        cacheMapper.insert(model);
        Assertions.assertEquals("before-batch", cacheMapper.selectById(model.getId()).getName());

        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            cacheMapper.updateById(Collections.singletonList(new CacheModel(model.getId(), "after-batch")));
            Assertions.assertEquals("after-batch", cacheMapper.selectById(model.getId()).getName());
            status.setRollbackOnly();
        });

        Assertions.assertEquals("before-batch", cacheMapper.selectById(model.getId()).getName());
    }

    @Test
    @Order(19)
    void testBaseMapperBatchPublishesFreshSecondLevelCacheOnCommit() {
        Cache cache = getCache();
        cache.clear();
        Long id = new TransactionTemplate(transactionManager).execute(status -> {
            CacheModel model = new CacheModel(10009L, "before-batch");
            cacheMapper.insert(model);
            Assertions.assertEquals("before-batch", cacheMapper.selectById(model.getId()).getName());

            cacheMapper.updateById(Collections.singletonList(new CacheModel(model.getId(), "after-batch")));
            Assertions.assertEquals("after-batch", cacheMapper.selectById(model.getId()).getName());
            return model.getId();
        });

        Assertions.assertEquals(1, cache.getSize());
        Assertions.assertEquals("after-batch", cacheMapper.selectById(id).getName());
    }

    @Test
    @Order(20)
    void testDirectSpringSqlSessionDoesNotPublishBatchResultOnRollback() {
        Cache cache = getCache();
        cache.clear();
        CacheModel model = new CacheModel(10010L, "before-batch");
        cacheMapper.insert(model);
        Assertions.assertEquals("before-batch", cacheMapper.selectById(model.getId()).getName());

        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            SqlSession sqlSession = SqlSessionUtils.getSqlSession(sqlSessionFactory);
            try {
                CacheMapper mapper = sqlSession.getMapper(CacheMapper.class);
                mapper.updateById(Collections.singletonList(new CacheModel(model.getId(), "after-batch")));
                Assertions.assertEquals("after-batch", mapper.selectById(model.getId()).getName());
                status.setRollbackOnly();
            } finally {
                SqlSessionUtils.closeSqlSession(sqlSession, sqlSessionFactory);
            }
        });

        Assertions.assertEquals("before-batch", cacheMapper.selectById(model.getId()).getName());
    }

    @Test
    void testOrder() {
        Cache cache = getCache();
        cache.clear();
        Page<CacheModel> page = new Page<>(1, 10, false);
        page.setOrders(Collections.singletonList(OrderItem.asc("id")));
        cacheService.page(page);
        Assertions.assertEquals(1, cache.getSize());
        page.setOrders(Arrays.asList(OrderItem.asc("id"), OrderItem.asc("name")));
        cacheService.page(page);
        Assertions.assertEquals(2, cache.getSize());
        page.setOrders(Arrays.asList(OrderItem.asc("name"), OrderItem.asc("id")));
        cacheService.page(page);
        Assertions.assertEquals(3, cache.getSize());
        page.setOrders(Collections.singletonList(OrderItem.desc("id")));
        cacheService.page(page);
        Assertions.assertEquals(4, cache.getSize());
        page = new Page<>(1, 10, true);
        page.setOrders(Collections.singletonList(OrderItem.asc("id")));
        cacheService.page(page);
        Assertions.assertEquals(5, cache.getSize());
        page.setOrders(Arrays.asList(OrderItem.asc("id"), OrderItem.asc("name")));
        cacheService.page(page);
        Assertions.assertEquals(5, cache.getSize());
        page.setOrders(Arrays.asList(OrderItem.asc("name"), OrderItem.asc("id")));
        cacheService.page(page);
        Assertions.assertEquals(5, cache.getSize());
        page.setOrders(Collections.singletonList(OrderItem.desc("id")));
        cacheService.page(page);
        Assertions.assertEquals(5, cache.getSize());
    }

    @Test
    void testCustomOffset(){
        Cache cache = getCache();
        cache.clear();
        CustomPage<CacheModel> page1 = new CustomPage<>(2, 10, false);
        Assertions.assertEquals(0, cache.getSize());
        cacheService.page(page1);
        Assertions.assertEquals(1, cache.getSize());
        cacheService.page(page1);
        Assertions.assertEquals(1, cache.getSize());
        //页数其他条件不变，改变分页偏移量的骚操作.
        page1.setOffset(12L);
        cacheService.page(page1);
        Assertions.assertEquals(2, cache.getSize());
        cacheService.page(page1);
        Assertions.assertEquals(2, cache.getSize());
    }

//    @Test
//    void testCustomSaveOrUpdateBatch(){
//        Assertions.assertTrue(cacheService.testCustomSaveOrUpdateBatch());
//    }

    private Cache getCache() {
        return sqlSessionFactory.getConfiguration().getCache(CacheMapper.class.getName());
    }
}
