package com.baomidou.mybatisplus.test.h2.cache;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.h2.cache.mapper.CacheMapper;
import com.baomidou.mybatisplus.test.h2.cache.model.CacheModel;
import com.baomidou.mybatisplus.test.h2.cache.service.ICacheService;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-cache-h2.xml"})
class CacheTest {

    @Autowired
    private ICacheService cacheService;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    @Order(1)
    void testPageCache() {
        Cache cache = getCache();
        IPage<CacheModel> cacheModelIPage1 = cacheService.page(new Page<>(1, 3), new QueryWrapper<>());
        IPage<CacheModel> cacheModelIPage2 = cacheService.page(new Page<>(1, 3), new QueryWrapper<>());
        assertThat(cache.getSize()).isEqualTo(2);
        assertThat(cacheModelIPage2.getTotal()).isEqualTo(cacheModelIPage1.getTotal());
        assertThat(cacheModelIPage2.getRecords().size()).isEqualTo(cacheModelIPage1.getRecords().size());
        IPage<CacheModel> cacheModelIPage3 = cacheService.page(new Page<>(2, 3), new QueryWrapper<>());
        assertThat(cacheModelIPage3.getTotal()).isEqualTo(cacheModelIPage1.getTotal());
        assertThat(cacheModelIPage3.getRecords().size()).isEqualTo(2);
        assertThat(cache.getSize()).isEqualTo(3);
        IPage<CacheModel> cacheModelIPage4 = cacheService.page(new Page<>(2, 3, false), new QueryWrapper<>());
        assertThat(cacheModelIPage4.getTotal()).isEqualTo(0L);
        assertThat(cacheModelIPage4.getRecords().size()).isEqualTo(2);
        assertThat(cache.getSize()).isEqualTo(3);
        IPage<CacheModel> cacheModelIPage5 = cacheService.page(new Page<>(2, 3, true), new QueryWrapper<>());
        assertThat(cacheModelIPage3.getTotal()).isEqualTo(cacheModelIPage5.getTotal());
        assertThat(cacheModelIPage5.getRecords().size()).isEqualTo(2);
        IPage<CacheModel> cacheModelIPage6 = cacheService
                .page(new Page<>(1, 3, true), new QueryWrapper<CacheModel>().ge("id", 2L));
        assertThat(cacheModelIPage6.getTotal()).isEqualTo(4);
        assertThat(cacheModelIPage6.getRecords().size()).isEqualTo(3);
        IPage<CacheModel> cacheModelIPage7 = cacheService
                .page(new Page<>(1, 3, false), new QueryWrapper<CacheModel>().ge("id", 2L));
        assertThat(cacheModelIPage7.getTotal()).isEqualTo(0L);
        assertThat(cacheModelIPage7.getRecords().size()).isEqualTo(3);
        IPage<CacheModel> cacheModelIPage8 = cacheService
                .page(new Page<>(1, 3, false), new QueryWrapper<CacheModel>().ge("id", 3L));
        assertThat(cacheModelIPage8.getTotal()).isEqualTo(0L);
        assertThat(cacheModelIPage8.getRecords().size()).isEqualTo(3);
        cacheModelIPage8 = cacheService.page(new Page<>(1, 3, false), new QueryWrapper<CacheModel>().ge("id", 3L));
        assertThat(cacheModelIPage8.getTotal()).isEqualTo(0L);
        assertThat(cacheModelIPage8.getRecords().size()).isEqualTo(3);
        IPage<CacheModel> cacheModelIPage9 = cacheService
                .page(new Page<>(1, 3, true), new QueryWrapper<CacheModel>().ge("id", 3L));
        assertThat(cacheModelIPage9.getTotal()).isEqualTo(3L);
        assertThat(cacheModelIPage9.getRecords().size()).isEqualTo(3);
        cacheModelIPage9 = cacheService.page(new Page<>(1, 3, true), new QueryWrapper<CacheModel>().ge("id", 3L));
        assertThat(cacheModelIPage9.getTotal()).isEqualTo(3L);
        assertThat(cacheModelIPage9.getRecords().size()).isEqualTo(3);
    }

    @Test
    @Order(2)
    void testCleanBatchCache() {
        CacheModel model = new CacheModel("靓仔");
        cacheService.save(model);
        Cache cache = getCache();
        assertThat(cache.getSize()).isEqualTo(0);
        cacheService.getById(model.getId());
        assertThat(cache.getSize()).isEqualTo(1);
        cacheService.updateBatchById(Collections.singletonList(new CacheModel(model.getId(), "旺仔")));
        assertThat(cache.getSize()).isEqualTo(0);
        assertThat(cacheService.getById(model.getId()).getName()).isEqualTo("旺仔");
        assertThat(cache.getSize()).isEqualTo(1);
    }

    @Test
    @Order(3)
    void testBatchTransactionalClear1() {
        Cache cache = getCache();
        long id = cacheService.testBatchTransactionalClear1();
        assertThat(cache.getSize()).isEqualTo(0);
        CacheModel cacheModel = cacheService.getById(id);
        assertThat(cache.getSize()).isEqualTo(1);
        assertThat(cacheModel.getName()).isEqualTo("旺仔");
    }

    @Test
    @Order(4)
    void testBatchTransactionalClear2() {
        long id = cacheService.testBatchTransactionalClear2();
        Cache cache = getCache();
        assertThat(cache.getSize()).isEqualTo(0);
        CacheModel cacheModel = cacheService.getById(id);
        assertThat(cache.getSize()).isEqualTo(1);
        assertThat(cacheModel.getName()).isEqualTo("小红");
    }

    @Test
    @Order(5)
    void testBatchTransactionalClear3() {
        long id = cacheService.testBatchTransactionalClear3();
        Cache cache = getCache();
        assertThat(cache.getSize()).isEqualTo(1);
        CacheModel cacheModel = cacheService.getById(id);
        assertThat(cache.getSize()).isEqualTo(1);
        assertThat(cacheModel.getName()).isEqualTo("小红");
    }

    @Test
    @Order(6)
    void testBatchTransactionalClear4() {
        long id = cacheService.testBatchTransactionalClear4();
        Cache cache = getCache();
        assertThat(cache.getSize()).isEqualTo(0);
        CacheModel cacheModel = cacheService.getById(id);
        assertThat(cache.getSize()).isEqualTo(1);
        assertThat(cacheModel.getName()).isEqualTo("旺仔");
    }

    @Test
    @Order(7)
    void testBatchTransactionalClear5() {
        long id = cacheService.testBatchTransactionalClear5();
        Cache cache = getCache();
        assertThat(cache.getSize()).isEqualTo(0);
        CacheModel cacheModel = cacheService.getById(id);
        assertThat(cache.getSize()).isEqualTo(1);
        assertThat(cacheModel).isNull();
    }

    @Test
    @Order(8)
    void testBatchTransactionalClear6() {
        long id = cacheService.testBatchTransactionalClear6();
        Cache cache = getCache();
        assertThat(cache.getSize()).isEqualTo(0);
        CacheModel cacheModel = cacheService.getById(id);
        assertThat(cache.getSize()).isEqualTo(1);
        assertThat(cacheModel).isNull();
    }

    @Test
    @Order(9)
    void testBatchTransactionalClear7() {
        long id = cacheService.testBatchTransactionalClear7();
        Cache cache = getCache();
        assertThat(cache.getSize()).isEqualTo(0);
        CacheModel cacheModel = cacheService.getById(id);
        assertThat(cache.getSize()).isEqualTo(1);
        assertThat(cacheModel).isNull();
    }

    @Test
    void testOrder() {
        Cache cache = getCache();
        cache.clear();
        Page<CacheModel> page = new Page<>(1, 10, false);
        page.setOrders(Collections.singletonList(OrderItem.asc("id")));
        cacheService.page(page);
        assertThat(cache.getSize()).isEqualTo(1);
        page.setOrders(Arrays.asList(OrderItem.asc("id"), OrderItem.asc("name")));
        cacheService.page(page);
        assertThat(cache.getSize()).isEqualTo(2);
        page.setOrders(Arrays.asList(OrderItem.asc("name"), OrderItem.asc("id")));
        cacheService.page(page);
        assertThat(cache.getSize()).isEqualTo(3);
        page.setOrders(Collections.singletonList(OrderItem.desc("id")));
        cacheService.page(page);
        assertThat(cache.getSize()).isEqualTo(4);
        page = new Page<>(1, 10, true);
        page.setOrders(Collections.singletonList(OrderItem.asc("id")));
        cacheService.page(page);
        assertThat(cache.getSize()).isEqualTo(5);
        page.setOrders(Arrays.asList(OrderItem.asc("id"), OrderItem.asc("name")));
        cacheService.page(page);
        assertThat(cache.getSize()).isEqualTo(5);
        page.setOrders(Arrays.asList(OrderItem.asc("name"), OrderItem.asc("id")));
        cacheService.page(page);
        assertThat(cache.getSize()).isEqualTo(5);
        page.setOrders(Collections.singletonList(OrderItem.desc("id")));
        cacheService.page(page);
        assertThat(cache.getSize()).isEqualTo(5);
    }

    @Test
    void testCustomOffset() {
        Cache cache = getCache();
        cache.clear();
        CustomPage<CacheModel> page1 = new CustomPage<>(2, 10, false);
        assertThat(cache.getSize()).isEqualTo(0);
        cacheService.page(page1);
        assertThat(cache.getSize()).isEqualTo(1);
        cacheService.page(page1);
        assertThat(cache.getSize()).isEqualTo(1);
        //页数其他条件不变，改变分页偏移量的骚操作.
        page1.setOffset(12L);
        cacheService.page(page1);
        assertThat(cache.getSize()).isEqualTo(2);
        cacheService.page(page1);
        assertThat(cache.getSize()).isEqualTo(2);
    }

    //    @Test
    //    void testCustomSaveOrUpdateBatch(){
    //        Assertions.assertTrue(cacheService.testCustomSaveOrUpdateBatch());
    //    }

    private Cache getCache() {
        return sqlSessionFactory.getConfiguration().getCache(CacheMapper.class.getName());
    }
}
