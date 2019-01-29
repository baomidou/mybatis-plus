package com.baomidou.mybatisplus.test.mysql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.base.entity.CommonData;
import com.baomidou.mybatisplus.test.base.entity.CommonLogicData;
import com.baomidou.mybatisplus.test.base.mapper.commons.CommonDataMapper;
import com.baomidou.mybatisplus.test.base.mapper.commons.CommonLogicDataMapper;
import com.baomidou.mybatisplus.test.mysql.config.MysqlDb;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:mysql/spring-test-mysql.xml"})
class SelectCountDistinctTest {

    @Resource
    private CommonLogicDataMapper commonLogicMapper;
    @Resource
    private CommonDataMapper commonDataMapper;

    @BeforeAll
    static void init() throws SQLException {
        MysqlDb.initMysqlData();
        System.out.println("init table and data success");
    }

    @Test
    @Order(1)
    void testCountDistinct() {
        QueryWrapper<CommonData> distinct = new QueryWrapper<>();
        distinct.select("distinct test_int");
        distinct.eq("test_int", 25).or().eq("test_str", "test");
        int count = commonDataMapper.selectCount(distinct);
        Assertions.assertEquals(1, count);
    }

    @Test
    @Order(2)
    void testCountDistinctTwoColumn() {
        QueryWrapper<CommonData> distinct = new QueryWrapper<>();
        distinct.select("distinct test_int, test_str");
        distinct.eq("test_int", 25).or().eq("test_str", "test");
        int count = commonDataMapper.selectCount(distinct);
        Assertions.assertEquals(1, count);
    }

    @Test
    @Order(3)
    void testLogicCountDistinct() {
        QueryWrapper<CommonLogicData> distinct = new QueryWrapper<>();
        distinct.select("distinct test_int");
        distinct.eq("test_int", 25).or().eq("test_str", "test");
        int count = commonLogicMapper.selectCount(distinct);
        Assertions.assertEquals(1, count);
    }

    @Test
    @Order(4)
    void testLogicSelectList() {
        QueryWrapper<CommonLogicData> commonQuery = new QueryWrapper<>();
        List<CommonLogicData> commonLogicDataList = commonLogicMapper.selectList(commonQuery);
        CommonLogicData commonLogicData = commonLogicDataList.get(0);
        Assertions.assertEquals(25, commonLogicData.getTestInt().intValue());
        Assertions.assertEquals("test", commonLogicData.getTestStr());
    }

    @Test
    @Order(5)
    void testLogicCountDistinctUseLambda() {
        LambdaQueryWrapper<CommonLogicData> lambdaQueryWrapper =
            new QueryWrapper<CommonLogicData>().select("distinct test_int").lambda();
        int count = commonLogicMapper.selectCount(lambdaQueryWrapper);
        Assertions.assertEquals(1, count);
    }

    @Test
    @Order(6)
    void testCountDistinctUseLambda() {
        LambdaQueryWrapper<CommonData> lambdaQueryWrapper =
            new QueryWrapper<CommonData>().select("distinct test_int, test_str").lambda();
        int count = commonDataMapper.selectCount(lambdaQueryWrapper);
        Assertions.assertEquals(1, count);
    }

    @Test
    @Order(7)
    void testLogicSelectCountWithoutDistinct() {
        QueryWrapper<CommonLogicData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("test_int", 25).or().eq("test_str", "test");
        int count = commonLogicMapper.selectCount(queryWrapper);
        Assertions.assertEquals(2, count);
    }

    @Test
    @Order(8)
    void testCountDistinctWithoutDistinct() {
        QueryWrapper<CommonData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("test_int", 25).or().eq("test_str", "test");
        int count = commonDataMapper.selectCount(queryWrapper);
        Assertions.assertEquals(2, count);
    }

    @Test
    @Order(9)
    void testSelectPageWithoutDistinct() {
        QueryWrapper<CommonData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("test_int", 25).or().eq("test_str", "test");
        IPage<CommonData> page = commonDataMapper.selectPage(new Page<>(1, 10), queryWrapper);
        Assertions.assertEquals(2, page.getTotal());
        Assertions.assertNotNull(page.getRecords().get(0));
        Assertions.assertNotNull(page.getRecords().get(1));
    }

    @Test
    @Order(10)
    void testSelectPageWithDistinct() {
        QueryWrapper<CommonData> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("distinct test_int, test_str");
        queryWrapper.eq("test_int", 25).or().eq("test_str", "test");
        IPage<CommonData> page = commonDataMapper.selectPage(new Page<>(1, 10), queryWrapper);
        Assertions.assertEquals(1, page.getTotal());
        Assertions.assertNotNull(page.getRecords().get(0));
    }

}
