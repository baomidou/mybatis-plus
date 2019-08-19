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
package com.baomidou.mybatisplus.test.base;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.base.entity.CommonData;
import com.baomidou.mybatisplus.test.base.entity.CommonLogicData;
import com.baomidou.mybatisplus.test.base.entity.ResultMapEntity;
import com.baomidou.mybatisplus.test.base.enums.TestEnum;
import com.baomidou.mybatisplus.test.base.mapper.children.CommonDataChildrenMapper;
import com.baomidou.mybatisplus.test.base.mapper.children.CommonLogicDataChildrenMapper;
import com.baomidou.mybatisplus.test.base.mapper.commons.CommonDataCopyMapper;
import com.baomidou.mybatisplus.test.base.mapper.commons.CommonDataMapper;
import com.baomidou.mybatisplus.test.base.mapper.commons.CommonLogicDataMapper;
import com.baomidou.mybatisplus.test.base.mapper.commons.ResultMapEntityMapper;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.*;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author miemie
 * @since 2019-03-06
 */
@DirtiesContext
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@ExtendWith(SpringExtension.class)
public abstract class BaseDbTest {
    protected final int success = 1;
    protected final int fail = 0;

    protected final List<String> list = Arrays.asList("1", "2", "3");
    protected final Map<String, Object> map = list.parallelStream().collect(toMap(identity(), identity()));
    @Resource(name = "commonDataMapper")
    protected CommonDataMapper commonDataMapper;
    @Resource(name = "commonDataChildrenMapper")
    protected CommonDataChildrenMapper commonDataChildrenMapper;
    @Resource(name = "commonLogicDataMapper")
    protected CommonLogicDataMapper commonLogicDataMapper;
    @Resource(name = "commonLogicDataChildrenMapper")
    protected CommonLogicDataChildrenMapper commonLogicDataChildrenMapper;
    @Resource
    protected CommonDataCopyMapper commonDataCopyMapper;
    @Resource
    protected ResultMapEntityMapper resultMapEntityMapper;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    void a00() {
        Configuration configuration = sqlSessionFactory.getConfiguration();
        assertThat(configuration).isInstanceOf(MybatisConfiguration.class);
        MappedStatement mappedStatement = configuration.getMappedStatement("com.baomidou.mybatisplus.test.mysql.mapper.MysqlDataMapper.getRandomOne");
        assertThat(mappedStatement).isNotNull();
    }

    @Test
    void a01_insertForeach() {
        for (int i = 1; i < 20; i++) {
            Long id = (long) i;
            String str = String.format("第%s条数据", i);
            commonDataMapper.insert(new CommonData().setTestInt(i).setTestStr(str).setId(id)
                .setTestEnum(TestEnum.ONE));
            commonLogicDataMapper.insert(new CommonLogicData().setTestInt(i).setTestStr(str).setId(id));
            resultMapEntityMapper.insert(new ResultMapEntity().setId(id).setList(list).setMap(map).setMapp(map));
            this.insertForeach(id, i, str);
        }
    }

    protected abstract void insertForeach(Long id, int index, String str);

    @Test
    void a02_insertBatch() {
        int size = 9;
        List<CommonData> commonDataList = new ArrayList<>();
        List<CommonLogicData> commonLogicDataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String str = i + "条";
            commonDataList.add(new CommonData().setTestInt(i).setTestEnum(TestEnum.TWO).setTestStr(str));
            commonLogicDataList.add(new CommonLogicData().setTestInt(i).setTestStr(str));
        }
        assertEquals(size, commonDataMapper.insertBatchSomeColumn(commonDataList));
        assertEquals(size, commonLogicDataMapper.insertBatchSomeColumn(commonLogicDataList));
        this.insertBatch(size);
    }

    protected abstract void insertBatch(int size);

    @Test
    void a03_deleteById() {
        long id = 1L;
        assertEquals(success, commonDataMapper.deleteById(id));
        assertEquals(success, commonLogicDataMapper.deleteById(id));
        this.deleteById(id);
    }

    protected abstract void deleteById(long id);

    @Test
    void a04_deleteByMap() {
        long id = 2L;
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("test_int", 5);
        assertEquals(fail, commonDataMapper.deleteByMap(map));
        assertEquals(fail, commonLogicDataMapper.deleteByMap(map));
        this.deleteByMap_fail(id);
    }

    protected abstract void deleteByMap_fail(long id);

    @Test
    void a05_delete() {
        long id = 2L;
        assertEquals(success, commonDataMapper.delete(Wrappers.<CommonData>lambdaQuery()
            .eq(CommonData::getId, id)
            .eq(CommonData::getTestInt, 2)));
        assertEquals(success, commonLogicDataMapper.delete(Wrappers.<CommonLogicData>lambdaQuery()
            .eq(CommonLogicData::getId, id)
            .eq(CommonLogicData::getTestInt, 2)));
        this.delete(id);
    }

    protected abstract void delete(long id);

    @Test
    void a06_deleteBatchIds() {
        List<Long> ids = Arrays.asList(3L, 4L);
        assertEquals(ids.size(), commonDataMapper.deleteBatchIds(ids));
        assertEquals(ids.size(), commonLogicDataMapper.deleteBatchIds(ids));
        this.deleteBatchIds(ids);
    }

    protected abstract void deleteBatchIds(List<Long> ids);

    //    @Test
//    void b5_deleteByIdWithFill() {
//        long id = 5L;
//        // 真删
//        Assertions.assertEquals(1, commonDataMapper.deleteByIdWithFill(new CommonData().setId(id)));
//        // 逻辑删除带填充
//        Assertions.assertEquals(1, commonLogicDataMapper.deleteByIdWithFill(new CommonLogicData().setId(id)));
//        // 真删
//        Assertions.assertEquals(1, mysqlMapper.deleteByIdWithFill(new MysqlData().setId(id)));
//    }

    @Test
    void a07_updateById() {
        long id = 6L;
        assertEquals(success, commonDataMapper.updateById(new CommonData().setId(id).setTestInt(555).setVersion(0)
            .setTestEnum(TestEnum.TWO)));
        assertEquals(success, commonLogicDataMapper.updateById(new CommonLogicData().setId(id).setTestInt(555)));
        assertEquals(success, resultMapEntityMapper.updateById(new ResultMapEntity().setId(id).setList(list).setMap(map)));
        this.updateById(id);
    }

    protected abstract void updateById(long id);

    @Test
    void a08_updateWithEntity() {
        long id = 8L;
        assertEquals(success, commonDataMapper.update(new CommonData().setTestInt(888).setVersion(0),
            Wrappers.<CommonData>lambdaUpdate().eq(CommonData::getId, id).eq(CommonData::getTestInt, 8)));
        assertEquals(success, commonLogicDataMapper.update(new CommonLogicData().setTestInt(888),
            Wrappers.<CommonLogicData>lambdaUpdate().eq(CommonLogicData::getId, id)
                .eq(CommonLogicData::getTestInt, 8)));
        this.updateWithEntity(id);
    }

    protected abstract void updateWithEntity(long id);

    @Test
    void a09_updateWithoutEntity() {
        long id = 9L;
        // todo
        this.updateWithoutEntity(id);
    }

    protected abstract void updateWithoutEntity(long id);

    @Test
    void a10_selectById() {
        long id = 6L;
        assertNotNull(commonDataMapper.selectById(id).getTestEnum());
        // todo
        assertTrue(commonDataMapper.getById(id).isPresent());
        assertThat(commonDataCopyMapper.selectById(id).isPresent()).isTrue();
        assertNotNull(commonLogicDataMapper.selectById(id));
        ResultMapEntity resultMapEntity = resultMapEntityMapper.selectById(id);
        assertNotNull(resultMapEntity);
        assertTrue(CollectionUtils.isNotEmpty(resultMapEntity.getMap()));
        assertTrue(CollectionUtils.isNotEmpty(resultMapEntity.getMapp()));
        assertTrue(CollectionUtils.isNotEmpty(resultMapEntity.getList()));
        this.selectById(id);
    }

    protected abstract void selectById(long id);

    @Test
    void a11_selectBatchIds() {
        List<Long> ids = Arrays.asList(7L, 8L);

        List<CommonData> commonData = commonDataMapper.selectBatchIds(ids);
        assertTrue(CollectionUtils.isNotEmpty(commonData));
        assertEquals(ids.size(), commonData.size());

        List<CommonLogicData> commonLogicData = commonLogicDataMapper.selectBatchIds(ids);
        assertTrue(CollectionUtils.isNotEmpty(commonLogicData));
        assertEquals(ids.size(), commonLogicData.size());

        List<ResultMapEntity> resultMapEntities = resultMapEntityMapper.selectBatchIds(ids);
        assertTrue(CollectionUtils.isNotEmpty(resultMapEntities));
        assertEquals(ids.size(), resultMapEntities.size());

        this.selectBatchIds(ids);
    }

    protected abstract void selectBatchIds(List<Long> ids);

    @Test
    void a12_selectByMap() {
        long id = 9L;
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("test_int", 9);
        assertTrue(CollectionUtils.isNotEmpty(commonDataMapper.selectByMap(map)));
        assertTrue(CollectionUtils.isNotEmpty(commonLogicDataMapper.selectByMap(map)));
        this.selectByMap(id);
    }

    protected abstract void selectByMap(long id);

    @Test
    void a13_selectOne() {
        long id = 10L;
        assertNotNull(commonDataMapper.selectOne(Wrappers.<CommonData>lambdaQuery()
            .eq(CommonData::getId, id).eq(CommonData::getTestInt, 10)));
        assertNotNull(commonLogicDataMapper.selectOne(Wrappers.<CommonLogicData>lambdaQuery()
            .eq(CommonLogicData::getId, id).eq(CommonLogicData::getTestInt, 10)));
        this.selectOne(id);
    }

    protected abstract void selectOne(long id);

    @Test
    void a14_selectList() {
        long id = 10L;
        List<CommonData> commonData = commonDataMapper.selectList(Wrappers.<CommonData>lambdaQuery()
            .eq(CommonData::getTestInt, 10));
        assertThat(commonData).isNotEmpty();
        assertThat(commonData.get(0)).isNotNull();

        List<CommonLogicData> commonLogicData = commonLogicDataMapper.selectList(Wrappers.<CommonLogicData>lambdaQuery()
            .eq(CommonLogicData::getId, id).eq(CommonLogicData::getTestInt, 10));
        assertThat(commonLogicData).isNotEmpty();
        assertThat(commonLogicData.get(0)).isNotNull();

        List<ResultMapEntity> resultMapEntities = resultMapEntityMapper.selectList(Wrappers.<ResultMapEntity>lambdaQuery()
            .eq(ResultMapEntity::getId, id));
        assertThat(resultMapEntities).isNotEmpty();
        assertThat(resultMapEntities.get(0)).isNotNull();
        assertThat(resultMapEntities.get(0).getList()).isNotEmpty();
        assertThat(resultMapEntities.get(0).getMap()).isNotEmpty();
        assertThat(resultMapEntities.get(0).getMapp()).isNotEmpty();

        this.selectList(id);
    }

    protected abstract void selectList(long id);

    @Test
    void a15_selectMaps() {
        List<Map<String, Object>> commonMaps = commonDataMapper.selectMaps(Wrappers.query());
        assertThat(commonMaps).isNotEmpty();
        assertThat(commonMaps.get(0)).isNotEmpty();

        List<Map<String, Object>> commonLogicMaps = commonLogicDataMapper.selectMaps(Wrappers.query());
        assertThat(commonLogicMaps).isNotEmpty();
        assertThat(commonLogicMaps.get(0)).isNotEmpty();

        List<Map<String, Object>> resultMapEntityMaps = resultMapEntityMapper.selectMaps(Wrappers.query());
        assertThat(resultMapEntityMaps).isNotEmpty();
        assertThat(resultMapEntityMaps.get(0)).isNotEmpty();

        this.selectMaps();
    }

    protected abstract void selectMaps();

    @Test
    void a16_selectPage() {
        Page<CommonData> page = new Page<>(1, 5);
        page.addOrder(OrderItem.descs("c_time", "u_time"));
        IPage<CommonData> dataPage = commonDataMapper.selectPage(page, null);
        assertSame(dataPage, page);
        assertNotEquals(0, dataPage.getRecords().size());
        assertTrue(CollectionUtils.isNotEmpty(dataPage.getRecords()));
        System.out.println(JSON.toJSONString(dataPage));
        System.out.println(JSON.toJSON(dataPage.convert(CommonData::getId)));


        Page<CommonLogicData> logicPage = new Page<>(1, 5);
        IPage<CommonLogicData> logicDataPage = commonLogicDataMapper.selectPage(logicPage, null);
        assertSame(logicDataPage, logicPage);
        assertNotEquals(0, logicDataPage.getRecords().size());
        assertTrue(CollectionUtils.isNotEmpty(logicDataPage.getRecords()));
        System.out.println(JSON.toJSONString(logicDataPage));


        Page<CommonData> commonDataPage = new Page<>(1, 5);
        commonDataPage.addOrder(OrderItem.descs("c_time", "u_time"));
        IPage<CommonData> commonDataDataPage = commonDataMapper.myPage(commonDataPage);
        assertSame(commonDataDataPage, commonDataPage);
        assertNotEquals(0, commonDataDataPage.getRecords().size());
        assertTrue(CollectionUtils.isNotEmpty(commonDataDataPage.getRecords()));
        System.out.println(JSON.toJSONString(commonDataDataPage));
        System.out.println(JSON.toJSON(commonDataDataPage.convert(CommonData::getId)));


        Page<ResultMapEntity> resultMapEntityPage = new Page<>(1, 5);
        IPage<ResultMapEntity> resultMapEntityDataPage = resultMapEntityMapper.selectPage(resultMapEntityPage, null);
        assertSame(resultMapEntityDataPage, resultMapEntityPage);
        assertNotEquals(0, resultMapEntityDataPage.getRecords().size());
        assertTrue(CollectionUtils.isNotEmpty(resultMapEntityDataPage.getRecords()));
        System.out.println(JSON.toJSONString(resultMapEntityDataPage));

        this.selectPage();
    }

    protected abstract void selectPage();

    @Test
    void a17_fatherAndChildren() {
        long id = 6;
        assertThat(commonDataMapper.getById(id).isPresent()).isTrue();
        assertThat(commonDataChildrenMapper.getByIdChildren(id).isPresent()).isTrue();
        assertNotNull(commonLogicDataMapper.selectById(id));
        assertNotNull(commonLogicDataChildrenMapper.selectById(id));
        commonLogicDataMapper.getByWrapper(Wrappers.lambdaQuery());
        commonLogicDataChildrenMapper.getByWrapper(Wrappers.lambdaQuery());
    }

    @Test
    void a18_groupByOrderBy() {
        LambdaQueryWrapper<CommonData> wrapper = Wrappers.<CommonData>lambdaQuery().groupBy(true, CommonData::getCreateDatetime);
        LambdaQueryWrapper<CommonData> wrapper2 = Wrappers.<CommonData>lambdaQuery().orderByAsc(CommonData::getCreateDatetime);
        System.out.println(wrapper.getSqlSegment());
        System.out.println(wrapper2.getSqlSegment());
//        List<CommonData> commonData = commonDataMapper.selectList(wrapper);
//        assertThat(commonData).isNotEmpty();
    }
}
