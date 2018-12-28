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
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(locations = {"classpath:mysql/spring-test-mysql.xml"})
public class SelectCountDistinctTest {

    @Resource
    private CommonLogicDataMapper commonLogicMapper;
    @Resource
    private CommonDataMapper commonDataMapper;

    @Before
    public void init() throws SQLException {
        MysqlDb.initMysqlData();
        insertLogic();
        insertCommon();
        System.out.println("init table and data success");
    }

    private void insertLogic() {
        commonLogicMapper.insert(new CommonLogicData().setTestInt(25).setTestStr("test"));
        commonLogicMapper.insert(new CommonLogicData().setTestInt(25).setTestStr("test"));
    }

    private void insertCommon() {
        commonDataMapper.insert(new CommonData().setTestInt(25).setTestStr("test"));
        commonDataMapper.insert(new CommonData().setTestInt(25).setTestStr("test"));
    }

    @Test
    public void testCountDistinct() {
        QueryWrapper<CommonData> distinct = new QueryWrapper<>();
        distinct.select("distinct test_int");
        distinct.eq("test_int", 25).or().eq("test_str", "test");
        int count = commonDataMapper.selectCount(distinct);
        Assert.assertEquals(1, count);
    }

    @Test
    public void testCountDistinctTwoColumn() {
        QueryWrapper<CommonData> distinct = new QueryWrapper<>();
        distinct.select("distinct test_int, test_str");
        distinct.eq("test_int", 25).or().eq("test_str", "test");
        int count = commonDataMapper.selectCount(distinct);
        Assert.assertEquals(1, count);
    }

    @Test
    public void testLogicCountDistinct() {
        QueryWrapper<CommonLogicData> distinct = new QueryWrapper<>();
        distinct.select("distinct test_int");
        distinct.eq("test_int", 25).or().eq("test_str", "test");
        int count = commonLogicMapper.selectCount(distinct);
        Assert.assertEquals(1, count);
    }

    @Test
    public void testLogicSelectList() {
        QueryWrapper<CommonLogicData> commonQuery = new QueryWrapper<>();
        List<CommonLogicData> commonLogicDataList = commonLogicMapper.selectList(commonQuery);
        CommonLogicData commonLogicData = commonLogicDataList.get(0);
        Assert.assertEquals(25, commonLogicData.getTestInt().intValue());
        Assert.assertEquals("test", commonLogicData.getTestStr());
    }

    @Test
    public void testLogicCountDistinctUseLambda() {
        LambdaQueryWrapper<CommonLogicData> lambdaQueryWrapper =
            new QueryWrapper<CommonLogicData>().select("distinct test_int").lambda();
        int count = commonLogicMapper.selectCount(lambdaQueryWrapper);
        Assert.assertEquals(1, count);
    }

    @Test
    public void testCountDistinctUseLambda() {
        LambdaQueryWrapper<CommonData> lambdaQueryWrapper =
            new QueryWrapper<CommonData>().select("distinct test_int, test_str").lambda();
        int count = commonDataMapper.selectCount(lambdaQueryWrapper);
        Assert.assertEquals(1, count);
    }

    @Test
    public void testLogicSelectCountWithoutDistinct() {
        QueryWrapper<CommonLogicData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("test_int", 25).or().eq("test_str", "test");
        int count = commonLogicMapper.selectCount(queryWrapper);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testCountDistinctWithoutDistinct() {
        QueryWrapper<CommonData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("test_int", 25).or().eq("test_str", "test");
        int count = commonDataMapper.selectCount(queryWrapper);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testSelectPageWithoutDistinct() {
        QueryWrapper<CommonData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("test_int", 25).or().eq("test_str", "test");
        IPage<CommonData> page = commonDataMapper.selectPage(new Page<CommonData>(1, 10), queryWrapper);
        Assert.assertEquals(2, page.getTotal());
        Assert.assertNotNull(page.getRecords().get(0));
        Assert.assertNotNull(page.getRecords().get(1));
    }

    @Test
    public void testSelectPageWithDistinct() {
        QueryWrapper<CommonData> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("distinct test_int, test_str");
        queryWrapper.eq("test_int", 25).or().eq("test_str", "test");
        IPage<CommonData> page = commonDataMapper.selectPage(new Page<CommonData>(1, 10), queryWrapper);
        Assert.assertEquals(1, page.getTotal());
        Assert.assertNotNull(page.getRecords().get(0));
    }

}
