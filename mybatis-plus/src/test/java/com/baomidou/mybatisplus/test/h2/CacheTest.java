package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.h2.cache.mapper.CacheMapper;
import com.baomidou.mybatisplus.test.h2.cache.model.CacheModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-cache-h2.xml"})
class CacheTest {

    @Autowired
    private CacheMapper cacheMapper;

    @Test
    void testPageCache() {
        IPage<CacheModel> cacheModelIPage1 = cacheMapper.selectPage(new Page<>(1, 3), new QueryWrapper<>());
        IPage<CacheModel> cacheModelIPage2 = cacheMapper.selectPage(new Page<>(1, 3), new QueryWrapper<>());
        Assertions.assertEquals(cacheModelIPage1.getTotal(), cacheModelIPage2.getTotal());
        Assertions.assertEquals(cacheModelIPage1.getRecords().size(), cacheModelIPage2.getRecords().size());
        IPage<CacheModel> cacheModelIPage3 = cacheMapper.selectPage(new Page<>(2, 3), new QueryWrapper<>());
        Assertions.assertEquals(cacheModelIPage1.getTotal(), cacheModelIPage3.getTotal());
        Assertions.assertEquals(cacheModelIPage3.getRecords().size(), 2);
        IPage<CacheModel> cacheModelIPage4 = cacheMapper.selectPage(new Page<>(2, 3, false), new QueryWrapper<>());
        Assertions.assertEquals(cacheModelIPage4.getTotal(), 0L);
        Assertions.assertEquals(cacheModelIPage4.getRecords().size(), 2);
        IPage<CacheModel> cacheModelIPage5 = cacheMapper.selectPage(new Page<>(2, 3, true), new QueryWrapper<>());
        Assertions.assertEquals(cacheModelIPage5.getTotal(), cacheModelIPage3.getTotal());
        Assertions.assertEquals(cacheModelIPage5.getRecords().size(), 2);
        IPage<CacheModel> cacheModelIPage6 = cacheMapper.selectPage(new Page<>(1, 3, true), new QueryWrapper<CacheModel>().ge("id", 2L));
        Assertions.assertEquals(cacheModelIPage6.getTotal(), 4);
        Assertions.assertEquals(cacheModelIPage6.getRecords().size(), 3);
        IPage<CacheModel> cacheModelIPage7 = cacheMapper.selectPage(new Page<>(1, 3, false), new QueryWrapper<CacheModel>().ge("id", 2L));
        Assertions.assertEquals(cacheModelIPage7.getTotal(), 0L);
        Assertions.assertEquals(cacheModelIPage7.getRecords().size(), 3);
    }

}
