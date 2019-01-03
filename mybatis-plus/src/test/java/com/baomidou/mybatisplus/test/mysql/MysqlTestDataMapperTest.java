package com.baomidou.mybatisplus.test.mysql;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.additional.update.impl.LambdaUpdateChainWrapper;
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
import java.util.*;


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
    public void a1_insertForeach() {
        for (int i = 1; i < 20; i++) {
            Long id = (long) i;
            String str = String.format("第%s条数据", i);
            commonMapper.insert(new CommonData().setTestInt(i).setTestStr(str).setId(id)
                .setTestEnum(TestEnum.ONE));
            commonLogicMapper.insert(new CommonLogicData().setTestInt(i).setTestStr(str).setId(id));
            mysqlMapper.insert(new MysqlData().setOrder(i).setGroup(i).setId(id).setTestStr(str).setYaHoStr(str));
        }
    }

    @Test
    public void a2_insertBatch() {
        List<MysqlData> mysqlDataList = new ArrayList<>();
        List<CommonData> commonDataList = new ArrayList<>();
        List<CommonLogicData> commonLogicDataList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            String str = i + "条";
            mysqlDataList.add(new MysqlData().setOrder(i).setGroup(i).setTestStr(str).setYaHoStr(str));
            commonDataList.add(new CommonData().setTestInt(i).setTestEnum(TestEnum.TWO).setTestStr(str));
            commonLogicDataList.add(new CommonLogicData().setTestInt(i).setTestStr(str));
        }
        Assert.assertEquals(9, mysqlMapper.insertBatchSomeColumn(mysqlDataList));
        Assert.assertEquals(9, commonMapper.insertBatchSomeColumn(commonDataList));
        Assert.assertEquals(9, commonLogicMapper.insertBatchSomeColumn(commonLogicDataList));
    }

    @Test
    public void b1_deleteById() {
        long id = 1L;
        Assert.assertEquals(1, commonMapper.deleteById(id));
        Assert.assertEquals(1, commonLogicMapper.deleteById(id));
        Assert.assertEquals(1, mysqlMapper.deleteById(id));
    }

    @Test
    public void b2_deleteByMap() {
        long id = 2L;
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("test_int", 5);
        Assert.assertEquals(0, commonMapper.deleteByMap(map));
        Assert.assertEquals(0, commonLogicMapper.deleteByMap(map));
        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", id);
        map2.put("`order`", 5);
        Assert.assertEquals(0, mysqlMapper.deleteByMap(map2));
    }

    @Test
    public void b3_delete() {
        long id = 2L;
        Assert.assertEquals(1, commonMapper.delete(new QueryWrapper<CommonData>().lambda()
            .eq(CommonData::getId, id)
            .eq(CommonData::getTestInt, 2)));
        Assert.assertEquals(1, commonLogicMapper.delete(new QueryWrapper<CommonLogicData>().lambda()
            .eq(CommonLogicData::getId, id)
            .eq(CommonLogicData::getTestInt, 2)));
        Assert.assertEquals(1, mysqlMapper.delete(new QueryWrapper<MysqlData>().lambda()
            .eq(MysqlData::getId, id)
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
    public void b5_deleteByIdWithFill() {
        long id = 5L;
        // 真删
        Assert.assertEquals(1, commonMapper.deleteByIdWithFill(new CommonData().setId(id)));
        // 逻辑删除带填充
        Assert.assertEquals(1, commonLogicMapper.deleteByIdWithFill(new CommonLogicData().setId(id)));
        // 真删
        Assert.assertEquals(1, mysqlMapper.deleteByIdWithFill(new MysqlData().setId(id)));
    }

    @Test
    public void c1_updateById() {
        long id = 6L;
        Assert.assertEquals(1, commonMapper.updateById(new CommonData().setId(id).setTestInt(555).setVersion(0)));
        Assert.assertEquals(1, commonLogicMapper.updateById(new CommonLogicData().setId(id).setTestInt(555)));
        Assert.assertEquals(1, mysqlMapper.updateById(new MysqlData().setId(id).setOrder(555)));
    }

    @Test
    public void c2_optimisticUpdateById() {
        long id = 7L;
        Assert.assertEquals(1, commonMapper.updateById(new CommonData().setId(id).setTestInt(778)
            .setVersion(0)));
    }

    @Test
    public void c3_update() {
        long id = 8L;
        Assert.assertEquals(1, commonMapper.update(
            new CommonData().setTestInt(888).setVersion(0),
            new UpdateWrapper<CommonData>().lambda().eq(CommonData::getId, id)
                .eq(CommonData::getTestInt, 8)));
        Assert.assertEquals(1, commonLogicMapper.update(
            new CommonLogicData().setTestInt(888),
            new UpdateWrapper<CommonLogicData>().lambda().eq(CommonLogicData::getId, id)
                .eq(CommonLogicData::getTestInt, 8)));
        Assert.assertEquals(1, mysqlMapper.update(
            new MysqlData().setOrder(888),
            new UpdateWrapper<MysqlData>().lambda().eq(MysqlData::getId, id)
                .eq(MysqlData::getOrder, 8)));
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
        long id = 9L;
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("test_int", 9);
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonMapper.selectByMap(map)));
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectByMap(map)));
        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", id);
        map2.put("`order`", 9);
        Assert.assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectByMap(map2)));
    }

    @Test
    public void d5_selectOne() {
        long id = 10L;
        Assert.assertNotNull(commonMapper.selectOne(new QueryWrapper<CommonData>().lambda()
            .eq(CommonData::getId, id).eq(CommonData::getTestInt, 10)));
        Assert.assertNotNull(commonLogicMapper.selectOne(new QueryWrapper<CommonLogicData>().lambda()
            .eq(CommonLogicData::getId, id).eq(CommonLogicData::getTestInt, 10)));
        Assert.assertNotNull(mysqlMapper.selectOne(new QueryWrapper<MysqlData>().lambda()
            .eq(MysqlData::getId, id).eq(MysqlData::getOrder, 10)));
    }

    @Test
    public void d6_selectList() {
        long id = 10L;
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonMapper.selectList(new QueryWrapper<CommonData>()
            .lambda().eq(CommonData::getTestInt, 10))));
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectList(new QueryWrapper<CommonLogicData>()
            .lambda().eq(CommonLogicData::getId, id).eq(CommonLogicData::getTestInt, 10))));
        Assert.assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(new QueryWrapper<MysqlData>()
            .lambda().eq(MysqlData::getId, id).eq(MysqlData::getOrder, 10))));
    }

    @Test
    public void d7_1_selectListForNoLogic() {
        MysqlData data = new MysqlData().setOrder(1);
        // 1. 只有 entity
        Assert.assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.query(data))));
        // 2. 有 entity 也有 where 条件
        Assert.assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.lambdaQuery(data).eq(MysqlData::getGroup, 1))));
        // 3. 有 entity 也有 where 条件 也有 last 条件
        Assert.assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.lambdaQuery(data).eq(MysqlData::getGroup, 1).last("limit 1"))));
        // 4. 有 entity 也有 last 条件
        Assert.assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.query(data)
            .last("limit 1"))));
        // 5. 只有 order by 或者 last
        Assert.assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.<MysqlData>query()
            .lambda().orderByDesc(MysqlData::getOrder).last("limit 1"))));
        // 6. 什么都没有情况
        Assert.assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.emptyWrapper())));
        // 7. 只有 where 条件
        Assert.assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.lambdaQuery(new MysqlData()).eq(MysqlData::getGroup, 1))));
        // 8. 有 where 条件 也有 last 条件
        Assert.assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.lambdaQuery(new MysqlData()).eq(MysqlData::getGroup, 1).last("limit 1"))));
    }

    @Test
    public void d7_2_selectListForLogic() {
        // 1. 只有 entity
        CommonLogicData data = new CommonLogicData().setTestInt(11);
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectList(Wrappers.query(data))));
        // 2. 有 entity 也有 where 条件
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectList(Wrappers.lambdaQuery(data).eq(CommonLogicData::getId, 11))));
        // 3. 有 entity 也有 where 条件 也有 last 条件
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectList(Wrappers.lambdaQuery(data).eq(CommonLogicData::getId, 11).last("limit 1"))));
        // 4. 有 entity 也有 last 条件
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectList(Wrappers.query(data)
            .last("limit 1"))));
        // 5. 只有 order by 或者 last
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectList(Wrappers.<CommonLogicData>query()
            .lambda().orderByAsc(CommonLogicData::getTestInt).last("limit 1"))));
        // 6. 什么都没有情况
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectList(Wrappers.emptyWrapper())));
        // 7. 只有 where 条件
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectList(Wrappers.lambdaQuery(new CommonLogicData()).eq(CommonLogicData::getId, 11))));
        // 8. 有 where 条件 也有 last 条件
        Assert.assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectList(Wrappers.lambdaQuery(new CommonLogicData()).eq(CommonLogicData::getId, 11).last("limit 1"))));
    }

    @Test
    public void d7_selectPage() {
        Page<CommonData> page = new Page<>(1, 5);
        page.setDesc("c_time", "u_time");
        IPage<CommonData> dataPage = commonMapper.selectPage(page, null);
        Assert.assertSame(dataPage, page);
        Assert.assertNotEquals(null, dataPage.getTotal());
        Assert.assertNotEquals(0, dataPage.getRecords().size());
        Assert.assertTrue(CollectionUtils.isNotEmpty(dataPage.getRecords()));
        System.out.println(JSON.toJSONString(dataPage));
        System.out.println(JSON.toJSON(dataPage.convert(CommonData::getId)));

        Page<CommonLogicData> logicPage = new Page<>(1, 5);
        IPage<CommonLogicData> logicDataPage = commonLogicMapper.selectPage(logicPage, null);
        Assert.assertSame(logicDataPage, logicPage);
        Assert.assertNotEquals(null, logicDataPage.getTotal());
        Assert.assertNotEquals(0, logicDataPage.getRecords().size());
        Assert.assertTrue(CollectionUtils.isNotEmpty(logicDataPage.getRecords()));
        System.out.println(JSON.toJSONString(logicDataPage));

        Page<MysqlData> mysqlPage = new Page<>(1, 5);
        IPage<MysqlData> mysqlDataPage = mysqlMapper.selectPage(mysqlPage, null);
        Assert.assertSame(mysqlDataPage, mysqlPage);
        Assert.assertNotEquals(null, mysqlDataPage.getTotal());
        Assert.assertNotEquals(0, mysqlDataPage.getRecords().size());
        Assert.assertTrue(CollectionUtils.isNotEmpty(mysqlDataPage.getRecords()));
        System.out.println(JSON.toJSONString(mysqlDataPage));
    }

    @Test
    public void d7_arLambdaSelectPage() {
        Page<CommonData> page = new Page<>(1, 5);
        page.setDesc("c_time", "u_time");
        IPage<CommonData> dataPage = new CommonData().selectPage(page, new QueryWrapper<CommonData>().lambda());
        Assert.assertSame(dataPage, page);
        Assert.assertNotEquals(null, dataPage.getTotal());
        Assert.assertNotEquals(0, dataPage.getRecords().size());
        Assert.assertTrue(CollectionUtils.isNotEmpty(dataPage.getRecords()));
        System.out.println(JSON.toJSONString(dataPage));
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

    @Test
    public void d9_testSetSelect() {
        commonMapper.selectList(new QueryWrapper<>(new CommonData()).select(TableFieldInfo::isCharSequence));
        commonMapper.selectList(new QueryWrapper<>(new CommonData().setTestStr("")));
        commonMapper.selectList(new QueryWrapper<>(new CommonData().setTestStr("")).orderByAsc("test_int"));
        commonMapper.selectList(new QueryWrapper<>(new CommonData().setTestStr("").setTestInt(12)).orderByAsc("test_int"));

        mysqlMapper.selectList(Wrappers.query(new MysqlData().setTestStr("")));

        mysqlMapper.selectList(Wrappers.lambdaQuery(new MysqlData().setTestStr("")).orderByAsc(MysqlData::getGroup));
        mysqlMapper.selectList(Wrappers.lambdaQuery(new MysqlData().setTestStr("").setGroup(1)).orderByAsc(MysqlData::getGroup));
    }

    @Test
    public void d10_testDel1eq1Then() {
        // 有空对象,有 order by
        mysqlMapper.selectList(Wrappers.lambdaQuery(new MysqlData()).select(i -> true).orderByAsc(MysqlData::getId));
        commonMapper.selectList(Wrappers.lambdaQuery(new CommonData()).orderByAsc(CommonData::getCreateDatetime));
        commonLogicMapper.selectList(Wrappers.lambdaQuery(new CommonLogicData()).orderByAsc(CommonLogicData::getCreateDatetime));
        // 对象有值,有 order by
        mysqlMapper.selectList(Wrappers.lambdaQuery(new MysqlData().setOrder(12)).select(i -> true).orderByAsc(MysqlData::getId));
        commonMapper.selectList(Wrappers.lambdaQuery(new CommonData().setTestInt(12)).orderByAsc(CommonData::getCreateDatetime));
        commonLogicMapper.selectList(Wrappers.lambdaQuery(new CommonLogicData().setTestInt(12)).orderByAsc(CommonLogicData::getCreateDatetime));
    }

    @Test
    public void d11_testWrapperCustomSql() {
        // 1. 只有 order by 或者 last
        mysqlMapper.getAll(Wrappers.<MysqlData>lambdaQuery().orderByDesc(MysqlData::getOrder).last("limit 1"));
        // 2. 什么都没有情况
        mysqlMapper.getAll(Wrappers.emptyWrapper());
        // 3. 只有 where 条件
        mysqlMapper.getAll(Wrappers.lambdaQuery(new MysqlData()).eq(MysqlData::getGroup, 1));
        // 4. 有 where 条件 也有 last 条件
        mysqlMapper.getAll(Wrappers.lambdaQuery(new MysqlData()).eq(MysqlData::getGroup, 1).last("limit 1"));
    }

    @Test
    public void e_1testNestPage() {
        ArrayList<Object> list = new ArrayList<>();
        LambdaQueryWrapper<CommonData> wrapper = Wrappers.<CommonData>lambdaQuery()
            .isNotNull(CommonData::getId).and(i -> i.eq(CommonData::getId, 1)
                .or().in(CommonData::getTestInt, list));
        System.out.println(wrapper.getSqlSegment());
        System.out.println(wrapper.getSqlSegment());
        System.out.println(wrapper.getSqlSegment());
        System.out.println(wrapper.getSqlSegment());
        System.out.println(wrapper.getSqlSegment());
        commonMapper.selectList(wrapper);
//        commonMapper.selectPage(new Page<>(1, 10), wrapper);
    }

    @Test
    public void e_2testLambdaColumnCache() {
        mysqlMapper.selectList(Wrappers.<MysqlData>lambdaQuery().select(MysqlData::getId, MysqlData::getYaHoStr))
            .forEach(System.out::println);
    }

    @Test
    public void e_3testUpdateNotEntity() {
        mysqlMapper.update(null, Wrappers.<MysqlData>lambdaUpdate().set(MysqlData::getOrder, 1));
        commonLogicMapper.update(null, Wrappers.<CommonLogicData>lambdaUpdate().set(CommonLogicData::getTestInt, 1));
    }

    @Test
    public void e_4testChain() {
        new LambdaQueryChainWrapper<>(mysqlMapper).select(MysqlData::getId, MysqlData::getYaHoStr)
            .list().forEach(System.out::println);

        new LambdaUpdateChainWrapper<>(mysqlMapper).set(MysqlData::getYaHoStr, "123456").update();

        new LambdaQueryChainWrapper<>(mysqlMapper).select(MysqlData::getId, MysqlData::getYaHoStr)
            .list().forEach(System.out::println);
    }
}
