package com.baomidou.mybatisplus.test.mysql;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.base.entity.LogicTestData;
import com.baomidou.mybatisplus.test.base.entity.TestData;
import com.baomidou.mybatisplus.test.base.mapper.LogicTestDataMapper;
import com.baomidou.mybatisplus.test.base.mapper.TestDataMapper;
import com.baomidou.mybatisplus.test.mysql.config.MysqlDb;
import com.baomidou.mybatisplus.test.mysql.service.ILogicTestDataService;

/**
 * <p>
 * Mybatis Plus mysql Junit Test
 * </p>
 *
 * @author hubin
 * @since 2018-06-05
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:mysql/spring-test-mysql.xml"})
public class MysqlTestDataMapperTest {

    @Resource
    private TestDataMapper mapper;
    @Resource
    private LogicTestDataMapper logicMapper;

    @Resource
    private ILogicTestDataService logicTestDataService;

    @BeforeClass
    public static void init() throws IOException, SQLException {
        MysqlDb.initMysqlData();
    }

    @Test
    public void insertForeach() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalDate nowDate = nowDateTime.toLocalDate();
        LocalTime nowTime = nowDateTime.toLocalTime();
        for (int i = 0; i < 20; i++) {
            mapper.insert(new TestData().setTestInt(i).setTestStr(String.format("第%s条数据", i))
                .setTestDouble(BigDecimal.valueOf(3.3).multiply(BigDecimal.valueOf(i)).doubleValue())
                .setTestBoolean((i + 3) % 2 == 0).setTestDate(nowDate)
                .setTestTime(nowTime).setTestDateTime(nowDateTime));
            logicMapper.insert(new LogicTestData().setTestInt(i).setTestStr(String.format("第%s条数据", i))
                .setTestDouble(BigDecimal.valueOf(3.3).multiply(BigDecimal.valueOf(i)).doubleValue())
                .setTestBoolean((i + 3) % 2 == 0).setTestDate(nowDate)
                .setTestTime(nowTime).setTestDateTime(nowDateTime));
        }
    }

    @Test
    public void deleteById() {
        mapper.deleteById(1014132604940615682L);
        logicMapper.deleteById(1014132604940615682L);
    }

    @Test
    public void deleteByMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1014361515785568258L);
        map.put("test_int", 5);
        mapper.deleteByMap(map);
        logicMapper.deleteByMap(map);
    }

    @Test
    public void delete() {
        mapper.delete(new QueryWrapper<TestData>().lambda()
            .eq(TestData::getId, 1014132604940615682L)
            .eq(TestData::getTestInt, 1));
        logicMapper.delete(new QueryWrapper<LogicTestData>().lambda()
            .eq(LogicTestData::getId, 1014132604940615682L)
            .eq(LogicTestData::getTestInt, 1));
    }

    @Test
    public void deleteBatchIds() {
        List<Long> ids = Arrays.asList(1014132604940615682L, 1014132604940615652L);
        mapper.deleteBatchIds(ids);
        logicMapper.deleteBatchIds(ids);
    }

    @Test
    public void updateTimeIssue() {
//        mapper.updateById(new TestData().setId(1014132604940615682L).setTestInt(1111111111));
        LogicTestData et = new LogicTestData()
            .setId(1019248035919613954L)
            .setTestInt(9991122)
            .setVersion(19);
        System.out.println("====1=====>>>" + JSON.toJSONString(et, true));
        boolean r = logicTestDataService.saveOrUpdate(et);
        System.out.println("====2-1==r==>>>" + r);
        System.out.println("====2-2=====>>>" + JSON.toJSONString(et, true));
    }

    @Test
    public void optimisticUpdateById() {
//        mapper.updateById(new TestData().setId(1014132604940615682L).setTestInt(1111111111));
        LogicTestData et = new LogicTestData()
            .setId(1019248035919613954L)
            .setTestInt(999)
            .setVersion(17);
        System.out.println("====1=====>>>" + JSON.toJSONString(et, true));
        int r = logicMapper.updateById(et);
        System.out.println("====2-1==r==>>>" + r);
        System.out.println("====2-2=====>>>" + JSON.toJSONString(et, true));
    }

    @Test
    public void updateById() {
        mapper.updateById(new TestData().setId(1014132604940615682L).setTestInt(1111111111));
        logicMapper.updateById(new LogicTestData().setId(1014132604940615682L).setTestInt(1111111111));
    }

    @Test
    public void update() {
        // type 1
        mapper.update(new TestData(), null);
        logicMapper.update(new LogicTestData(), null);
        // type 2
        mapper.update(new TestData(), new UpdateWrapper<TestData>()
            .set("test_int", 5));
        logicMapper.update(new LogicTestData(), new UpdateWrapper<LogicTestData>()
            .set("test_int", 5));
//        // type 3
        mapper.update(new TestData(), new UpdateWrapper<TestData>()
            .set("test_int", 5).eq("id", 1014361515554881538L));
        logicMapper.update(new LogicTestData(), new UpdateWrapper<LogicTestData>()
            .set("test_int", 5).eq("id", 1014361515554881538L));
//        // type 4
        mapper.update(new TestData(), new UpdateWrapper<TestData>()
            .eq("id", 1014361515554881538L));
        logicMapper.update(new LogicTestData(), new UpdateWrapper<LogicTestData>()
            .eq("id", 1014361515554881538L));
//        // type 5
        mapper.update(new TestData(), new UpdateWrapper<TestData>()
            .setEntity(new TestData().setTestInt(1)));
        logicMapper.update(new LogicTestData(), new UpdateWrapper<LogicTestData>()
            .setEntity(new LogicTestData().setTestInt(1)));
//        // type 6
        mapper.update(new TestData(), new UpdateWrapper<TestData>()
            .setEntity(new TestData().setTestInt(1))
            .eq("id", 1014361515554881538L));
        logicMapper.update(new LogicTestData(), new UpdateWrapper<LogicTestData>()
            .setEntity(new LogicTestData().setTestInt(1))
            .eq("id", 1014361515554881538L));
//        // type 7
        mapper.update(new TestData(), new UpdateWrapper<TestData>()
            .setEntity(new TestData().setTestInt(1))
            .set("test_int", 55555)
            .eq("id", 1014361515554881538L));
        logicMapper.update(new LogicTestData(), new UpdateWrapper<LogicTestData>()
            .setEntity(new LogicTestData().setTestInt(1))
            .set("test_int", 55555)
            .eq("id", 1014361515554881538L));
    }

    @Test
    public void selectById() {
        mapper.selectById(1L);
        logicMapper.selectById(1L);
    }

    @Test
    public void selectBatchIds() {
        List<Long> ids = Arrays.asList(1014132604940615682L, 1014132604940615652L);
        mapper.selectBatchIds(ids);
        logicMapper.selectBatchIds(ids);
    }

    @Test
    public void selectByMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1L);
        map.put("test_int", 1);
        mapper.selectByMap(map);
        logicMapper.selectByMap(map);
    }

    @Test
    public void selectOne() {
        mapper.selectOne(new QueryWrapper<TestData>().lambda()
            .eq(TestData::getId, 1L).eq(TestData::getTestInt, 1));
        logicMapper.selectOne(new QueryWrapper<LogicTestData>().lambda()
            .eq(LogicTestData::getId, 1L).eq(LogicTestData::getTestInt, 1));
    }

    @Test
    public void selectList() {
        mapper.selectList(new QueryWrapper<TestData>().lambda()
            .eq(TestData::getId, 1L).eq(TestData::getTestInt, 1));
        logicMapper.selectList(new QueryWrapper<LogicTestData>().lambda()
            .eq(LogicTestData::getId, 1L).eq(LogicTestData::getTestInt, 1));
        logicMapper.selectList(null);
    }

    @Test
    public void commonSelectList() {
        println(mapper.selectList(new QueryWrapper<TestData>()
            .eq("id", 1L)
            .like("test_str", 1)
            .between("test_double", 1L, 2L)));
    }

    @Test
    public void specialSelectList() {
        println(mapper.selectList(new QueryWrapper<TestData>().lambda()
            .nested(i -> i.eq(TestData::getId, 1L))
            .or(i -> i.between(TestData::getTestDouble, 1L, 2L))
            .or(i -> i.eq(TestData::getTestInt, 1)
                .or().eq(TestData::getTestDate, 1)
            )
            .eq(TestData::getTestBoolean, true)
            .eq(TestData::getTestDate, LocalDate.of(2008, 8, 8))
            .between(TestData::getTestDate, LocalDate.of(2008, 1, 1),
                LocalDate.of(2008, 12, 12))));
    }

    @Test
    public void selectPage() {
        IPage<TestData> page = new Page<>();
        page.setSize(5).setCurrent(1);
        IPage<TestData> dataPage = mapper.selectPage(page, null);
        Assert.assertSame(dataPage, page);
        System.out.println(String.format("total = {%s}", dataPage.getTotal()));
        System.out.println(String.format("data.size = {%s}", dataPage.getRecords().size()));
        println(page.getRecords());
        System.out.println(JSON.toJSONString(page));

        IPage<LogicTestData> logicPage = new Page<>();
        logicPage.setSize(5).setCurrent(1);
        IPage<LogicTestData> logicDataPage = logicMapper.selectPage(logicPage, null);
        Assert.assertSame(logicDataPage, logicPage);
        System.out.println(String.format("total = {%s}", logicDataPage.getTotal()));
        System.out.println(String.format("data.size = {%s}", logicDataPage.getRecords().size()));
        println(logicDataPage.getRecords());
        System.out.println(JSON.toJSONString(logicDataPage));

    }

    @Test
    public void testIn() {
        println(mapper.selectList(new QueryWrapper<TestData>()
//            .in("test_int", Arrays.asList(1, 2, 3))//ok
//                .notIn("test_int", Arrays.asList(1, 2, 3)//ok
//                .in("test_int", 1, 2, 3)//ok
//                .notIn("test_int", 1, 2, 3)//ok
                .inSql("test_int", "1,2,3")//ok
                .notInSql("test_int", "2,3")//ok
        ));
    }

    @Test
    public void testExists() {
        println(mapper.selectList(new QueryWrapper<TestData>()
            .exists("select * from test_data")//ok
            .or()
            .notExists("select * from test_data")//ok
        ));
        /* exists 连着用是可行的 */
    }

    @Test
    public void testApply() {
        println(mapper.selectList(new QueryWrapper<TestData>()
            .apply("test_int = 1")
        ));
    }

    private <T> void println(List<T> list) {
        list.forEach(System.out::println);
    }
}
