package com.baomidou.mybatisplus.test.mysql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
public class SelectCountTest {

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
    public void testCountAndDistinctWithSelect() {
        QueryWrapper<CommonData> distinct = new QueryWrapper<>();
        distinct.select("test_int").distinct();
        distinct.eq("test_int", 25).or().eq("test_str", "test");
        int count = commonDataMapper.selectCount(distinct);
        Assert.assertEquals(1, count);
        QueryWrapper<CommonData> commonQuery = new QueryWrapper<>();
        commonQuery.select("test_int", "test_str");
        commonQuery.eq("test_int", 25).or().eq("test_str", "test");
        count = commonDataMapper.selectCount(commonQuery);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testLogicCountAndDistinctWithSelect() {
        QueryWrapper<CommonLogicData> distinct = new QueryWrapper<>();
        distinct.select("test_int").distinct();
        distinct.eq("test_int", 25).or().eq("test_str", "test");
        int count = commonLogicMapper.selectCount(distinct);
        Assert.assertEquals(1, count);
        QueryWrapper<CommonLogicData> commonQuery = new QueryWrapper<>();
        commonQuery.select("test_int", "test_str");
        commonQuery.eq("test_int", 25).or().eq("test_str", "test");
        count = commonLogicMapper.selectCount(commonQuery);
        Assert.assertEquals(2, count);
        List<CommonLogicData> commonLogicDataList = commonLogicMapper.selectList(commonQuery);
        CommonLogicData commonLogicData = commonLogicDataList.get(0);
        Assert.assertEquals(25, commonLogicData.getTestInt().intValue());
        Assert.assertEquals("test", commonLogicData.getTestStr());
    }

    @Test
    public void testLogicCountAndDistinctWithSelectUserLambda() {
        // 目前LambdaQueryWrapper还不能支持distinct，后期应该在AbstractWrapper实现一个distinct方法
        LambdaQueryWrapper<CommonLogicData> lambdaQueryWrapper = new QueryWrapper<CommonLogicData>().lambda();
        lambdaQueryWrapper.select(CommonLogicData::getTestInt);
        int count = commonLogicMapper.selectCount(lambdaQueryWrapper);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testCountAndDistinctWithSelectUserLambda() {
        // 目前LambdaQueryWrapper还不能支持distinct，后期应该在AbstractWrapper实现一个distinct方法
        LambdaQueryWrapper<CommonData> lambdaQueryWrapper = new QueryWrapper<CommonData>().lambda();
        lambdaQueryWrapper.select(CommonData::getTestInt);
        int count = commonDataMapper.selectCount(lambdaQueryWrapper);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testLogicCountAndDistinctWithoutSelect() {
        QueryWrapper<CommonLogicData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("test_int", 25).or().eq("test_str", "test");
        int count = commonLogicMapper.selectCount(queryWrapper);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testCountAndDistinctWithoutSelect() {
        QueryWrapper<CommonData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("test_int", 25).or().eq("test_str", "test");
        int count = commonDataMapper.selectCount(queryWrapper);
        Assert.assertEquals(2, count);
    }

}
