package com.baomidou.mybatisplus.test.mysql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.base.entity.TestData;
import com.baomidou.mybatisplus.test.base.mapper.TestDataMapper;

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
    protected TestDataMapper mapper;

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
        }
    }

    @Test
    public void updateByIdTest() {
        mapper.updateById(new TestData().setId(1014132604940615682L).setTestInt(1111111111));
    }

    @Test
    public void updateTest() {
        mapper.update(new TestData().setTestInt(222222222), new UpdateWrapper<TestData>()
            .set("test_str", "我佛慈悲2").eq("id",1014132605058056193L));//todo 发现一个bug
    }

    @Test
    public void selectById() {
        System.out.println(mapper.selectById(1L));
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
    public void update() {
        mapper.update(new TestData().setId(1L).setTestStr("123123"),
            new UpdateWrapper<TestData>().eq("id", 1L));
    }

    @Test
    public void selectByMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1L);
        map.put("test_int", 1);
        println(mapper.selectByMap(map));
    }

    @Test
    public void selectPage() {
        IPage<TestData> page = new Page<>();
        page.setSize(5).setCurrent(1);
        IPage<TestData> dataPage = mapper.selectPage(page, new QueryWrapper<TestData>().lambda()
            .eq(TestData::getTestInt, 1));
        Assert.assertSame(dataPage, page);
        System.out.println(dataPage.getTotal());
        System.out.println(dataPage.getRecords().size());
        println(page.getRecords());
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

    private void println(List<TestData> list) {
        list.forEach(System.out::println);
    }
}
