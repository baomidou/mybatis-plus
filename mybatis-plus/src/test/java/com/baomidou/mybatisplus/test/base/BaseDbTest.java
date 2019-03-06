package com.baomidou.mybatisplus.test.base;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.test.base.entity.CommonData;
import com.baomidou.mybatisplus.test.base.entity.CommonLogicData;
import com.baomidou.mybatisplus.test.base.entity.ResultMapEntity;
import com.baomidou.mybatisplus.test.base.enums.TestEnum;
import com.baomidou.mybatisplus.test.base.mapper.commons.CommonDataMapper;
import com.baomidou.mybatisplus.test.base.mapper.commons.CommonLogicDataMapper;
import com.baomidou.mybatisplus.test.base.mapper.commons.ResultMapEntityMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.*;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
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
    @Resource
    protected CommonDataMapper commonMapper;
    @Resource
    protected CommonLogicDataMapper commonLogicMapper;
    @Resource
    protected ResultMapEntityMapper resultMapEntityMapper;

    @Test
    void a01_insertForeach() {
        for (int i = 1; i < 20; i++) {
            Long id = (long) i;
            String str = String.format("第%s条数据", i);
            commonMapper.insert(new CommonData().setTestInt(i).setTestStr(str).setId(id)
                .setTestEnum(TestEnum.ONE));
            commonLogicMapper.insert(new CommonLogicData().setTestInt(i).setTestStr(str).setId(id));
            resultMapEntityMapper.insert(new ResultMapEntity().setId(id).setList(list).setMap(map));
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
        assertEquals(size, commonMapper.insertBatchSomeColumn(commonDataList));
        assertEquals(size, commonLogicMapper.insertBatchSomeColumn(commonLogicDataList));
        this.insertBatch(size);
    }

    protected abstract void insertBatch(int size);

    @Test
    void a03_deleteById() {
        long id = 1L;
        assertEquals(success, commonMapper.deleteById(id));
        assertEquals(success, commonLogicMapper.deleteById(id));
        this.deleteById(id);
    }

    protected abstract void deleteById(long id);

    @Test
    void a04_deleteByMap() {
        long id = 2L;
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("test_int", 5);
        assertEquals(fail, commonMapper.deleteByMap(map));
        assertEquals(fail, commonLogicMapper.deleteByMap(map));
        this.deleteByMap_fail(id);
    }

    protected abstract void deleteByMap_fail(long id);

    @Test
    void a05_delete() {
        long id = 2L;
        assertEquals(success, commonMapper.delete(Wrappers.<CommonData>lambdaQuery()
            .eq(CommonData::getId, id)
            .eq(CommonData::getTestInt, 2)));
        assertEquals(success, commonLogicMapper.delete(Wrappers.<CommonLogicData>lambdaQuery()
            .eq(CommonLogicData::getId, id)
            .eq(CommonLogicData::getTestInt, 2)));
        this.delete(id);
    }

    protected abstract void delete(long id);

    @Test
    void a06_deleteBatchIds() {
        List<Long> ids = Arrays.asList(3L, 4L);
        assertEquals(ids.size(), commonMapper.deleteBatchIds(ids));
        assertEquals(ids.size(), commonLogicMapper.deleteBatchIds(ids));
        this.deleteBatchIds(ids);
    }

    protected abstract void deleteBatchIds(List<Long> ids);

    //    @Test
//    void b5_deleteByIdWithFill() {
//        long id = 5L;
//        // 真删
//        Assertions.assertEquals(1, commonMapper.deleteByIdWithFill(new CommonData().setId(id)));
//        // 逻辑删除带填充
//        Assertions.assertEquals(1, commonLogicMapper.deleteByIdWithFill(new CommonLogicData().setId(id)));
//        // 真删
//        Assertions.assertEquals(1, mysqlMapper.deleteByIdWithFill(new MysqlData().setId(id)));
//    }

    @Test
    void a07_updateById() {
        long id = 6L;
        assertEquals(success, commonMapper.updateById(new CommonData().setId(id).setTestInt(555).setVersion(0)
            .setTestEnum(TestEnum.TWO)));
        assertEquals(success, commonLogicMapper.updateById(new CommonLogicData().setId(id).setTestInt(555)));
        assertEquals(success, resultMapEntityMapper.updateById(new ResultMapEntity().setId(id).setList(list).setMap(map)));
        this.updateById(id);
    }

    protected abstract void updateById(long id);

    @Test
    void a08_updateWithEntity() {
        long id = 8L;
        assertEquals(success, commonMapper.update(new CommonData().setTestInt(888).setVersion(0),
            Wrappers.<CommonData>lambdaUpdate().eq(CommonData::getId, id).eq(CommonData::getTestInt, 8)));
        assertEquals(success, commonLogicMapper.update(new CommonLogicData().setTestInt(888),
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
        assertNotNull(commonMapper.selectById(id).getTestEnum());
        // todo
        assertTrue(commonMapper.getById(id).isPresent());
        assertNotNull(commonLogicMapper.selectById(id));
        ResultMapEntity resultMapEntity = resultMapEntityMapper.selectById(id);
        assertNotNull(resultMapEntity);
        assertTrue(CollectionUtils.isNotEmpty(resultMapEntity.getMap()));
        assertTrue(CollectionUtils.isNotEmpty(resultMapEntity.getList()));
        this.selectById(id);
    }

    protected abstract void selectById(long id);

    @Test
    void a11_selectBatchIds() {
        List<Long> ids = Arrays.asList(7L, 8L);

        List<CommonData> commonData = commonMapper.selectBatchIds(ids);
        assertTrue(CollectionUtils.isNotEmpty(commonData));
        assertEquals(ids.size(), commonData.size());

        List<CommonLogicData> commonLogicData = commonLogicMapper.selectBatchIds(ids);
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
        assertTrue(CollectionUtils.isNotEmpty(commonMapper.selectByMap(map)));
        assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectByMap(map)));
        this.selectByMap(id);
    }

    protected abstract void selectByMap(long id);

    @Test
    void a13_selectOne() {
        long id = 10L;
        assertNotNull(commonMapper.selectOne(Wrappers.<CommonData>lambdaQuery()
            .eq(CommonData::getId, id).eq(CommonData::getTestInt, 10)));
        assertNotNull(commonLogicMapper.selectOne(Wrappers.<CommonLogicData>lambdaQuery()
            .eq(CommonLogicData::getId, id).eq(CommonLogicData::getTestInt, 10)));
        this.selectOne(id);
    }

    protected abstract void selectOne(long id);

    @Test
    void a14_selectList() {
        long id = 10L;
        assertTrue(CollectionUtils.isNotEmpty(commonMapper.selectList(Wrappers.<CommonData>lambdaQuery()
            .eq(CommonData::getTestInt, 10))));
        assertTrue(CollectionUtils.isNotEmpty(commonLogicMapper.selectList(Wrappers.<CommonLogicData>lambdaQuery()
            .eq(CommonLogicData::getId, id).eq(CommonLogicData::getTestInt, 10))));
        this.selectList(id);
    }

    protected abstract void selectList(long id);
}
