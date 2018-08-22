package com.baomidou.mybatisplus.test.mysql;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.base.entity.CommonData;
import com.baomidou.mybatisplus.test.base.entity.CommonLogicData;
import com.baomidou.mybatisplus.test.base.entity.mysql.MysqlData;
import com.baomidou.mybatisplus.test.base.enums.TestEnum;
import com.baomidou.mybatisplus.test.base.mapper.commons.CommonDataMapper;
import com.baomidou.mybatisplus.test.base.mapper.commons.CommonLogicDataMapper;
import com.baomidou.mybatisplus.test.base.mapper.mysql.MysqlDataMapper;
import com.baomidou.mybatisplus.test.mysql.config.MysqlDb;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * Mybatis Plus mysql Junit Test
 * </p>
 *
 * @author hubin
 * @since 2018-06-05
 */
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(locations = {"classpath:mysql/spring-test-mysql.xml"})
public class MysqlTestDataMapperTest {

    @Resource
    private CommonDataMapper commonMapper;
    @Resource
    private CommonLogicDataMapper commonLogicMapper;
    @Resource
    private MysqlDataMapper mysqlMapper;

    @BeforeClass
    public static void init() throws Exception {
        MysqlDb.initMysqlData();
        System.out.println("init success");
    }

    @Test
    public void a_insertForeach() {
        for (int i = 1; i < 20; i++) {
            Long id = (long) i;
            commonMapper.insert(new CommonData().setTestInt(i).setTestStr(String.format("第%s条数据", i)).setId(id)
                .setTestEnum(TestEnum.ONE));
            commonLogicMapper.insert(new CommonLogicData().setTestInt(i).setTestStr(String.format("第%s条数据", i)).setId(id));
            mysqlMapper.insert(new MysqlData().setOrder(i).setGroup(i).setId(id));
        }
    }

    @Test
    public void b1_deleteById() {
        Assert.assertEquals(1, commonMapper.deleteById(1L));
        Assert.assertEquals(1, commonLogicMapper.deleteById(1L));
        Assert.assertEquals(1, mysqlMapper.deleteById(1L));
    }

    @Test
    public void b2_deleteByMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 2L);
        map.put("test_int", 5);
        Assert.assertEquals(0, commonMapper.deleteByMap(map));
        Assert.assertEquals(0, commonLogicMapper.deleteByMap(map));
        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", 2L);
        map2.put("`order`", 5);
        Assert.assertEquals(0, mysqlMapper.deleteByMap(map2));
    }

    @Test
    public void b3_delete() {
        Assert.assertEquals(1, commonMapper.delete(new QueryWrapper<CommonData>().lambda()
            .eq(CommonData::getId, 2L)
            .eq(CommonData::getTestInt, 2)));
        Assert.assertEquals(1, commonLogicMapper.delete(new QueryWrapper<CommonLogicData>().lambda()
            .eq(CommonLogicData::getId, 2L)
            .eq(CommonLogicData::getTestInt, 2)));
        Assert.assertEquals(1, mysqlMapper.delete(new QueryWrapper<MysqlData>().lambda()
            .eq(MysqlData::getId, 2L)
            .eq(MysqlData::getOrder, 2)));
    }

    @Test
    public void b4_deleteBatchIds() {
        List<Long> ids = Arrays.asList(3L, 4L);
        Assert.assertEquals(2, commonMapper.deleteBatchIds(ids));
        Assert.assertEquals(2, commonLogicMapper.deleteBatchIds(ids));
        Assert.assertEquals(2, mysqlMapper.deleteBatchIds(ids));
    }

    @Test
    public void c1_updateById() {
        Assert.assertEquals(1, commonMapper.updateById(new CommonData().setId(5L).setTestInt(555).setVersion(0)));
        Assert.assertEquals(1, commonLogicMapper.updateById(new CommonLogicData().setId(5L).setTestInt(555)));
        Assert.assertEquals(1, mysqlMapper.updateById(new MysqlData().setId(5L).setOrder(555)));
    }

    @Test
    public void c2_optimisticUpdateById() {
        Assert.assertEquals(1, commonMapper.updateById(new CommonData().setId(7L).setTestInt(778)
            .setVersion(0)));
    }

    @Test
    public void c3_update() {
        Assert.assertEquals(1, commonMapper.update(
            new CommonData().setTestInt(666).setVersion(0),
            new UpdateWrapper<CommonData>().lambda().eq(CommonData::getId, 6L)
                .eq(CommonData::getTestInt, 6)));
        Assert.assertEquals(1, commonLogicMapper.update(
            new CommonLogicData().setTestInt(666),
            new UpdateWrapper<CommonLogicData>().lambda().eq(CommonLogicData::getId, 6L)
                .eq(CommonLogicData::getTestInt, 6)));
        Assert.assertEquals(1, mysqlMapper.update(
            new MysqlData().setOrder(666),
            new UpdateWrapper<MysqlData>().lambda().eq(MysqlData::getId, 6L)
                .eq(MysqlData::getOrder, 6)));
    }

    @Test
    public void d1_getAllNoTenant() {
        commonMapper.getAllNoTenant();
    }

    @Test
    public void d2_selectById() {
        long id = 6L;
        Assert.assertNotNull(commonMapper.selectById(id).getTestEnum());
        Assert.assertNotNull(commonLogicMapper.selectById(id));
        Assert.assertNotNull(mysqlMapper.selectById(id));
    }

    @Test
    public void d3_selectBatchIds() {
        List<Long> ids = Arrays.asList(7L, 8L);
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonMapper.selectBatchIds(ids)));
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectBatchIds(ids)));
        Assert.assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectBatchIds(ids)));
    }

    @Test
    public void d4_selectByMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 9L);
        map.put("test_int", 9);
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonMapper.selectByMap(map)));
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectByMap(map)));
        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", 9L);
        map2.put("`order`", 9);
        Assert.assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectByMap(map2)));
    }

    @Test
    public void d5_selectOne() {
        Assert.assertNotNull(commonMapper.selectOne(new QueryWrapper<CommonData>().lambda()
            .eq(CommonData::getId, 10L).eq(CommonData::getTestInt, 10)));
        Assert.assertNotNull(commonLogicMapper.selectOne(new QueryWrapper<CommonLogicData>().lambda()
            .eq(CommonLogicData::getId, 10L).eq(CommonLogicData::getTestInt, 10)));
        Assert.assertNotNull(mysqlMapper.selectOne(new QueryWrapper<MysqlData>().lambda()
            .eq(MysqlData::getId, 10L).eq(MysqlData::getOrder, 10)));
    }

    @Test
    public void d6_selectList() {
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonMapper.selectList(new QueryWrapper<CommonData>()
            .lambda().eq(CommonData::getTestInt, 10))));
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectList(new QueryWrapper<CommonLogicData>()
            .lambda().eq(CommonLogicData::getId, 10L).eq(CommonLogicData::getTestInt, 10))));
        Assert.assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(new QueryWrapper<MysqlData>()
            .lambda().eq(MysqlData::getId, 10L).eq(MysqlData::getOrder, 10))));
    }

    @Test
    public void d7_selectPage() {
        IPage<CommonData> page = new Page<>(1, 5);
        IPage<CommonData> dataPage = commonMapper.selectPage(page, null);
        Assert.assertSame(dataPage, page);
        Assert.assertNotEquals(0L, dataPage.getTotal());
        Assert.assertNotEquals(0, dataPage.getRecords().size());
        Assert.assertTrue(CollectionUtils.isNotEmpty(dataPage.getRecords()));
        System.out.println(JSON.toJSONString(dataPage));

        IPage<CommonLogicData> logicPage = new Page<>(1, 5);
        IPage<CommonLogicData> logicDataPage = commonLogicMapper.selectPage(logicPage, null);
        Assert.assertSame(logicDataPage, logicPage);
        Assert.assertNotEquals(0L, logicDataPage.getTotal());
        Assert.assertNotEquals(0, logicDataPage.getRecords().size());
        Assert.assertTrue(CollectionUtils.isNotEmpty(logicDataPage.getRecords()));
        System.out.println(JSON.toJSONString(logicDataPage));

        IPage<MysqlData> mysqlPage = new Page<>(1, 5);
        IPage<MysqlData> mysqlDataPage = mysqlMapper.selectPage(mysqlPage, null);
        Assert.assertSame(mysqlDataPage, mysqlPage);
        Assert.assertNotEquals(0L, mysqlDataPage.getTotal());
        Assert.assertNotEquals(0, mysqlDataPage.getRecords().size());
        Assert.assertTrue(CollectionUtils.isNotEmpty(mysqlDataPage.getRecords()));
        System.out.println(JSON.toJSONString(mysqlDataPage));
    }

    @Test
    public void d8_testApply() {
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonMapper.selectList(new QueryWrapper<CommonData>()
            .apply("test_int = 12"))));
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectList(new QueryWrapper<CommonLogicData>()
            .apply("test_int = 12"))));
        Assert.assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(new QueryWrapper<MysqlData>()
            .apply("`order` = 12"))));
    }
}
