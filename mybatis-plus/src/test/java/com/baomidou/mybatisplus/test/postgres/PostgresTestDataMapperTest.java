package com.baomidou.mybatisplus.test.postgres;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.base.entity.CommonData;
import com.baomidou.mybatisplus.test.base.entity.CommonLogicData;
import com.baomidou.mybatisplus.test.base.entity.pg.PgData;
import com.baomidou.mybatisplus.test.base.enums.TestEnum;
import com.baomidou.mybatisplus.test.base.mapper.commons.CommonDataMapper;
import com.baomidou.mybatisplus.test.base.mapper.commons.CommonLogicDataMapper;
import com.baomidou.mybatisplus.test.base.mapper.pg.PgDataMapper;
import com.baomidou.mybatisplus.test.postgres.config.PostgresDb;
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
@ContextConfiguration(locations = {"classpath:postgres/spring-test-postgres.xml"})
public class PostgresTestDataMapperTest {

    @Resource
    private CommonDataMapper commonMapper;
    @Resource
    private CommonLogicDataMapper commonLogicMapper;
    @Resource
    private PgDataMapper pgMapper;

    @BeforeClass
    public static void init() throws Exception {
        PostgresDb.initPgData();
        System.out.println("init success");
    }

    @Test
    public void a_insertForeach() {
        for (int i = 1; i < 20; i++) {
            Long id = (long) i;
            commonMapper.insert(new CommonData().setTestInt(i).setTestStr(String.format("第%s条数据", i)).setId(id)
                .setTestEnum(TestEnum.ONE));
            commonLogicMapper.insert(new CommonLogicData().setTestInt(i).setTestStr(String.format("第%s条数据", i)).setId(id));
            pgMapper.insert(new PgData().setGroup(i).setId(id).setPgInt(i).setPgInt2(i));
        }
    }

    @Test
    public void b1_deleteById() {
        Assert.assertEquals(1, commonMapper.deleteById(1L));
        Assert.assertEquals(1, commonLogicMapper.deleteById(1L));
        Assert.assertEquals(1, pgMapper.deleteById(1L));
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
        map2.put("\"group\"", 5);
        map2.put("\"pgInt\"", 5);
        Assert.assertEquals(0, pgMapper.deleteByMap(map2));
    }

    @Test
    public void b3_delete() {
        Assert.assertEquals(1, commonMapper.delete(new QueryWrapper<CommonData>().lambda()
            .eq(CommonData::getId, 2L)
            .eq(CommonData::getTestInt, 2)));
        Assert.assertEquals(1, commonLogicMapper.delete(new QueryWrapper<CommonLogicData>().lambda()
            .eq(CommonLogicData::getId, 2L)
            .eq(CommonLogicData::getTestInt, 2)));
        Assert.assertEquals(1, pgMapper.delete(new QueryWrapper<PgData>().lambda()
            .eq(PgData::getId, 2L)
            .eq(PgData::getGroup, 2).eq(PgData::getPgInt, 2)));
    }

    @Test
    public void b4_deleteBatchIds() {
        List<Long> ids = Arrays.asList(3L, 4L);
        Assert.assertEquals(2, commonMapper.deleteBatchIds(ids));
        Assert.assertEquals(2, commonLogicMapper.deleteBatchIds(ids));
        Assert.assertEquals(2, pgMapper.deleteBatchIds(ids));
    }

    @Test
    public void c1_updateById() {
        Assert.assertEquals(1, commonMapper.updateById(new CommonData().setId(5L).setTestInt(555)));
        Assert.assertEquals(1, commonLogicMapper.updateById(new CommonLogicData().setId(5L).setTestInt(555)));
        Assert.assertEquals(1, pgMapper.updateById(new PgData().setId(5L).setGroup(555).setPgInt(555)));
    }

    @Test
    public void c2_optimisticUpdateById() {
        Assert.assertEquals(1, commonMapper.updateById(new CommonData().setId(5L).setTestInt(556)
            .setVersion(0)));
    }

    @Test
    public void c3_update() {
        Assert.assertEquals(1, commonMapper.update(
            new CommonData().setTestInt(666),
            new UpdateWrapper<CommonData>().lambda().eq(CommonData::getId, 6L)
                .eq(CommonData::getTestInt, 6)));
        Assert.assertEquals(1, commonLogicMapper.update(
            new CommonLogicData().setTestInt(666),
            new UpdateWrapper<CommonLogicData>().lambda().eq(CommonLogicData::getId, 6L)
                .eq(CommonLogicData::getTestInt, 6)));
        Assert.assertEquals(1, pgMapper.update(
            new PgData().setGroup(666).setPgInt(555),
            new UpdateWrapper<PgData>().lambda().eq(PgData::getId, 6L)
                .eq(PgData::getGroup, 6).eq(PgData::getPgInt, 6)));
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
        Assert.assertNotNull(pgMapper.selectById(id));
    }

    @Test
    public void d3_selectBatchIds() {
        List<Long> ids = Arrays.asList(7L, 8L);
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonMapper.selectBatchIds(ids)));
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectBatchIds(ids)));
        Assert.assertTrue(CollectionUtils.isNotEmpty(pgMapper.selectBatchIds(ids)));
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
        map2.put("\"group\"", 9);
        map2.put("\"pgInt\"", 9);
        Assert.assertTrue(CollectionUtils.isNotEmpty(pgMapper.selectByMap(map2)));
    }

    @Test
    public void d5_selectOne() {
        Assert.assertNotNull(commonMapper.selectOne(new QueryWrapper<CommonData>().lambda()
            .eq(CommonData::getId, 10L).eq(CommonData::getTestInt, 10)));
        Assert.assertNotNull(commonLogicMapper.selectOne(new QueryWrapper<CommonLogicData>().lambda()
            .eq(CommonLogicData::getId, 10L).eq(CommonLogicData::getTestInt, 10)));
        Assert.assertNotNull(pgMapper.selectOne(new QueryWrapper<PgData>().lambda()
            .eq(PgData::getId, 10L).eq(PgData::getGroup, 10).eq(PgData::getPgInt, 10)));
    }

    @Test
    public void d6_selectList() {
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonMapper.selectList(new QueryWrapper<CommonData>()
            .lambda().eq(CommonData::getTestInt, 10))));
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectList(new QueryWrapper<CommonLogicData>()
            .lambda().eq(CommonLogicData::getId, 10L).eq(CommonLogicData::getTestInt, 10))));
        Assert.assertTrue(CollectionUtils.isNotEmpty(pgMapper.selectList(new QueryWrapper<PgData>()
            .lambda().eq(PgData::getId, 10L).eq(PgData::getGroup, 10).eq(PgData::getPgInt, 10))));
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

        IPage<PgData> pgPage = new Page<>(1, 5);
        page.setSize(5).setCurrent(1);
        IPage<PgData> pgDataPage = pgMapper.selectPage(pgPage, null);
        Assert.assertSame(pgDataPage, pgPage);
        Assert.assertNotEquals(0L, pgDataPage.getTotal());
        Assert.assertNotEquals(0, pgDataPage.getRecords().size());
        Assert.assertTrue(CollectionUtils.isNotEmpty(pgDataPage.getRecords()));
        System.out.println(JSON.toJSONString(pgDataPage));
    }

    @Test
    public void d8_testApply() {
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonMapper.selectList(new QueryWrapper<CommonData>()
            .apply("test_int = 12"))));
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectList(new QueryWrapper<CommonLogicData>()
            .apply("test_int = 12"))));
        Assert.assertTrue(CollectionUtils.isNotEmpty(pgMapper.selectList(new QueryWrapper<PgData>()
            .apply("\"group\" = 12").apply("\"pgInt\" = 12"))));
    }
}
