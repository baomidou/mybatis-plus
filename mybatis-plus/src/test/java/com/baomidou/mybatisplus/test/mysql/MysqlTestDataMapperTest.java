/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.mysql;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.base.BaseDbTest;
import com.baomidou.mybatisplus.test.base.entity.CommonLogicData;
import com.baomidou.mybatisplus.test.mysql.entity.MysqlData;
import com.baomidou.mybatisplus.test.mysql.mapper.MysqlDataMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Mybatis Plus mysql Junit Test
 *
 * @author hubin
 * @since 2018-06-05
 */
@ContextConfiguration(locations = {"classpath:mysql/spring-test-mysql.xml"})
class MysqlTestDataMapperTest extends BaseDbTest {

    @Resource
    private MysqlDataMapper mysqlMapper;

    @Override
    protected void insertForeach(Long id, int index, String str) {
        mysqlMapper.insert(new MysqlData().setOrder(index).setGroup(index).setId(id).setTestStr(str).setYaHoStr(str));
    }

    @Override
    protected void insertBatch(int size) {
        List<MysqlData> mysqlDataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String str = i + "条";
            mysqlDataList.add(new MysqlData().setOrder(i).setGroup(i).setTestStr(str).setYaHoStr(str));
        }
        assertEquals(size, mysqlMapper.insertBatchSomeColumn(mysqlDataList));
    }

    @Override
    protected void deleteById(long id) {
        assertEquals(success, mysqlMapper.deleteById(id));
    }

    @Override
    protected void deleteByMap_fail(long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("`order`", 5);
        assertEquals(fail, mysqlMapper.deleteByMap(map));
    }

    @Override
    protected void delete(long id) {
        assertEquals(success, mysqlMapper.delete(Wrappers.<MysqlData>lambdaQuery()
            .eq(MysqlData::getId, id)
            .eq(MysqlData::getOrder, 2)));
    }

    @Override
    protected void deleteBatchIds(List<Long> ids) {
        assertEquals(ids.size(), mysqlMapper.deleteBatchIds(ids));
    }

    @Override
    protected void updateById(long id) {
        assertEquals(success, mysqlMapper.updateById(new MysqlData().setId(id).setOrder(555)));
    }

    @Override
    protected void updateWithEntity(long id) {
        assertEquals(success, mysqlMapper.update(new MysqlData().setOrder(888),
            Wrappers.<MysqlData>lambdaUpdate().eq(MysqlData::getId, id)
                .eq(MysqlData::getOrder, 8)));
    }

    @Override
    protected void updateWithoutEntity(long id) {
        //todo
    }

    @Override
    protected void selectById(long id) {
        assertNotNull(mysqlMapper.selectById(id));
    }

    @Override
    protected void selectBatchIds(List<Long> ids) {
        List<MysqlData> mysqlData = mysqlMapper.selectBatchIds(ids);
        assertTrue(CollectionUtils.isNotEmpty(mysqlData));
        assertEquals(ids.size(), mysqlData.size());
    }

    @Override
    protected void selectByMap(long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("`order`", 9);
        assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectByMap(map)));
    }

    @Override
    protected void selectOne(long id) {
        assertNotNull(mysqlMapper.selectOne(Wrappers.<MysqlData>lambdaQuery()
            .eq(MysqlData::getId, id).eq(MysqlData::getOrder, 10)));
    }

    @Override
    protected void selectList(long id) {
        assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.<MysqlData>lambdaQuery()
            .eq(MysqlData::getId, id).eq(MysqlData::getOrder, 10))));
    }

    @Override
    protected void selectMaps() {
        List<Map<String, Object>> mysqlMaps = mysqlMapper.selectMaps(Wrappers.query());
        assertThat(mysqlMaps).isNotEmpty();
        assertThat(mysqlMaps.get(0)).isNotEmpty();

        Page<Map<String, Object>> mapPage = mysqlMapper.getMaps(new Page(1, 5));
        assertThat(mapPage).isNotNull();
        assertThat(mapPage.getRecords()).isNotEmpty();
        assertThat(mapPage.getRecords().get(0)).isNotEmpty();
    }

    //    @Test
//    void b5_deleteByIdWithFill() {
//        long id = 5L;
//        // 真删
//        Assertions.assertEquals(1, commonMapper.deleteByIdWithFill(new CommonData().setId(id)));
//        // 逻辑删除带填充
//        Assertions.assertEquals(1, commonLogicDataMapper.deleteByIdWithFill(new CommonLogicData().setId(id)));
//        // 真删
//        Assertions.assertEquals(1, mysqlMapper.deleteByIdWithFill(new MysqlData().setId(id)));
//    }
//
//    @Test
//    void c2_optimisticUpdateById() {
//        long id = 7L;
//        Assertions.assertEquals(1, commonMapper.updateById(new CommonData().setId(id).setTestInt(778)
//            .setVersion(0)));
//    }
//
//    @Test
//    void d1_getAllNoTenant() {
//        commonMapper.getAllNoTenant();
//    }
//
    @Test
    void b01_selectListForNoLogic() {
        MysqlData data = new MysqlData().setOrder(1);
        // 1. 只有 entity
        assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.query(data))));
        // 2. 有 entity 也有 where 条件
        assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.lambdaQuery(data).eq(MysqlData::getGroup, 1))));
        // 3. 有 entity 也有 where 条件 也有 last 条件
        assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.lambdaQuery(data).eq(MysqlData::getGroup, 1).last("limit 1"))));
        // 4. 有 entity 也有 last 条件
        assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.query(data)
            .last("limit 1"))));
        // 5. 只有 order by 或者 last
        assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.<MysqlData>query()
            .lambda().orderByDesc(MysqlData::getOrder).last("limit 1"))));
        // 6. 什么都没有情况
        assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.emptyWrapper())));
        // 7. 只有 where 条件
        assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.lambdaQuery(new MysqlData()).eq(MysqlData::getGroup, 1))));
        // 8. 有 where 条件 也有 last 条件
        assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(Wrappers.lambdaQuery(new MysqlData()).eq(MysqlData::getGroup, 1).last("limit 1"))));
    }

    @Test
    void b02_selectListForLogic() {
        // 1. 只有 entity
        CommonLogicData data = new CommonLogicData().setTestInt(11);
        assertTrue(CollectionUtils.isNotEmpty(commonLogicDataMapper.selectList(Wrappers.query(data))));
        // 2. 有 entity 也有 where 条件
        assertTrue(CollectionUtils.isNotEmpty(commonLogicDataMapper.selectList(Wrappers.lambdaQuery(data).eq(CommonLogicData::getId, 11))));
        // 3. 有 entity 也有 where 条件 也有 last 条件
        assertTrue(CollectionUtils.isNotEmpty(commonLogicDataMapper.selectList(Wrappers.lambdaQuery(data).eq(CommonLogicData::getId, 11).last("limit 1"))));
        // 4. 有 entity 也有 last 条件
        assertTrue(CollectionUtils.isNotEmpty(commonLogicDataMapper.selectList(Wrappers.query(data)
            .last("limit 1"))));
        // 5. 只有 order by 或者 last
        assertTrue(CollectionUtils.isNotEmpty(commonLogicDataMapper.selectList(Wrappers.<CommonLogicData>query()
            .lambda().orderByAsc(CommonLogicData::getTestInt).last("limit 1"))));
        // 6. 什么都没有情况
        assertTrue(CollectionUtils.isNotEmpty(commonLogicDataMapper.selectList(Wrappers.emptyWrapper())));
        // 7. 只有 where 条件
        assertTrue(CollectionUtils.isNotEmpty(commonLogicDataMapper.selectList(Wrappers.lambdaQuery(new CommonLogicData()).eq(CommonLogicData::getId, 11))));
        // 8. 有 where 条件 也有 last 条件
        assertTrue(CollectionUtils.isNotEmpty(commonLogicDataMapper.selectList(Wrappers.lambdaQuery(new CommonLogicData()).eq(CommonLogicData::getId, 11).last("limit 1"))));
    }

    @Test
    void b03_getRandomOne() {
        Map<String, Object> randomOne = mysqlMapper.getRandomOne(null, null);
        assertThat(randomOne).isNotEmpty();
    }

    @Override
    protected void selectPage() {
        Page<MysqlData> mysqlPage = new Page<>(1, 5);
        IPage<MysqlData> mysqlDataPage = mysqlMapper.selectPage(mysqlPage, null);
        assertSame(mysqlDataPage, mysqlPage);
        assertNotEquals(0, mysqlDataPage.getRecords().size());
        assertTrue(CollectionUtils.isNotEmpty(mysqlDataPage.getRecords()));
        System.out.println(JSON.toJSONString(mysqlDataPage));
    }
//
//    @Test
//    void d8_testApply() {
//        Assertions.assertTrue(CollectionUtils.isNotEmpty(commonMapper.selectList(new QueryWrapper<CommonData>()
//            .apply("test_int = 12"))));
//        Assertions.assertTrue(CollectionUtils.isNotEmpty(commonLogicDataMapper.selectList(new QueryWrapper<CommonLogicData>()
//            .apply("test_int = 12"))));
//        Assertions.assertTrue(CollectionUtils.isNotEmpty(mysqlMapper.selectList(new QueryWrapper<MysqlData>()
//            .apply("`order` = 12"))));
//    }
//
//    @Test
//    void d9_testSetSelect() {
//        commonMapper.selectList(new QueryWrapper<>(new CommonData()).select(TableFieldInfo::isCharSequence));
//        commonMapper.selectList(new QueryWrapper<>(new CommonData().setTestStr("")));
//        commonMapper.selectList(new QueryWrapper<>(new CommonData().setTestStr("")).orderByAsc("test_int"));
//        commonMapper.selectList(new QueryWrapper<>(new CommonData().setTestStr("").setTestInt(12)).orderByAsc("test_int"));
//
//        mysqlMapper.selectList(Wrappers.query(new MysqlData().setTestStr("")));
//
//        mysqlMapper.selectList(Wrappers.lambdaQuery(new MysqlData().setTestStr("")).orderByAsc(MysqlData::getGroup));
//        mysqlMapper.selectList(Wrappers.lambdaQuery(new MysqlData().setTestStr("").setGroup(1)).orderByAsc(MysqlData::getGroup));
//    }
//
//    @Test
//    void d10_testDel1eq1Then() {
//        // 有空对象,有 order by
//        mysqlMapper.selectList(Wrappers.lambdaQuery(new MysqlData()).select(i -> true).orderByAsc(MysqlData::getId));
//        commonMapper.selectList(Wrappers.lambdaQuery(new CommonData()).orderByAsc(CommonData::getCreateDatetime));
//        commonLogicDataMapper.selectList(Wrappers.lambdaQuery(new CommonLogicData()).orderByAsc(CommonLogicData::getCreateDatetime));
//        // 对象有值,有 order by
//        mysqlMapper.selectList(Wrappers.lambdaQuery(new MysqlData().setOrder(12)).select(i -> true).orderByAsc(MysqlData::getId));
//        commonMapper.selectList(Wrappers.lambdaQuery(new CommonData().setTestInt(12)).orderByAsc(CommonData::getCreateDatetime));
//        commonLogicDataMapper.selectList(Wrappers.lambdaQuery(new CommonLogicData().setTestInt(12)).orderByAsc(CommonLogicData::getCreateDatetime));
//    }
//
//    @Test
//    void d11_testWrapperCustomSql() {
//        // 1. 只有 order by 或者 last
//        mysqlMapper.getAll(Wrappers.<MysqlData>lambdaQuery().orderByDesc(MysqlData::getOrder).last("limit 1"));
//        // 2. 什么都没有情况
//        mysqlMapper.getAll(Wrappers.emptyWrapper());
//        // 3. 只有 where 条件
//        mysqlMapper.getAll(Wrappers.lambdaQuery(new MysqlData()).eq(MysqlData::getGroup, 1));
//        // 4. 有 where 条件 也有 last 条件
//        mysqlMapper.getAll(Wrappers.lambdaQuery(new MysqlData()).eq(MysqlData::getGroup, 1).last("limit 1"));
//    }
//
//    @Test
//    void e_1testNest() {
//        ArrayList<Object> list = new ArrayList<>();
//        list.add(1);
//        LambdaQueryWrapper<CommonData> wrapper = Wrappers.<CommonData>lambdaQuery()
//            .isNotNull(CommonData::getId).and(i -> i.eq(CommonData::getId, 1)
//                .or().in(CommonData::getTestInt, list));
//        System.out.println(wrapper.getSqlSegment());
//        System.out.println(wrapper.getSqlSegment());
//        System.out.println(wrapper.getSqlSegment());
//        System.out.println(wrapper.getSqlSegment());
//        System.out.println(wrapper.getSqlSegment());
//        commonMapper.selectList(wrapper);
//    }
//
//    @Test
//    void e_2testLambdaColumnCache() {
//        mysqlMapper.selectList(Wrappers.<MysqlData>lambdaQuery().select(MysqlData::getId, MysqlData::getYaHoStr))
//            .forEach(System.out::println);
//    }
//
//    @Test
//    void e_3testUpdateNotEntity() {
//        mysqlMapper.update(null, Wrappers.<MysqlData>lambdaUpdate().set(MysqlData::getOrder, 1));
//        commonLogicDataMapper.update(null, Wrappers.<CommonLogicData>lambdaUpdate().set(CommonLogicData::getTestInt, 1));
//    }
//
//    @Test
//    void e_4testChainQuery() {
//        new LambdaQueryChainWrapper<>(mysqlMapper).select(MysqlData::getId, MysqlData::getYaHoStr)
//            .list().forEach(System.out::println);
//
//        new LambdaQueryChainWrapper<>(mysqlMapper).select(MysqlData::getId, MysqlData::getYaHoStr)
//            .eq(MysqlData::getId, 19).one();
//
//        new LambdaQueryChainWrapper<>(mysqlMapper).count();
//
//        new LambdaQueryChainWrapper<>(mysqlMapper).select(MysqlData::getId, MysqlData::getYaHoStr)
//            .page(new Page<>(1, 2));
//    }
//
//    @Test
//    void e_5testChainUpdate() {
//        new LambdaUpdateChainWrapper<>(mysqlMapper).set(MysqlData::getYaHoStr, "123456").update();
//
//        new LambdaUpdateChainWrapper<>(mysqlMapper).eq(MysqlData::getYaHoStr, "111").remove();
//    }
//
//    @Test
//    void e_6getByWrapper() {
//        commonMapper.getByWrapper(Wrappers.<CommonData>lambdaQuery().isNotNull(CommonData::getId));
//        commonLogicDataMapper.getByWrapper(Wrappers.<CommonLogicData>lambdaQuery().isNotNull(CommonLogicData::getId));
//    }
}
