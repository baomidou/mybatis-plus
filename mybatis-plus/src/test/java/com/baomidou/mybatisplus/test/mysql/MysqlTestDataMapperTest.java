package com.baomidou.mybatisplus.test.mysql;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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
    protected TestDataMapper testDataMapper;

    @Test
    public void selectById() {
        System.out.println(testDataMapper.selectById(1L));
    }

    @Test
    public void commonSelectList() {
        println(testDataMapper.selectList(new QueryWrapper<TestData>()
            .eq("id", 1L)
            .like("test_str", 1)
            .between("test_double", 1L, 2L)));
    }

    @Test
    public void specialSelectList() {
        println(testDataMapper.selectList(new QueryWrapper<TestData>().lambda()
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
        testDataMapper.update(new TestData().setId(1L).setTestStr("123123"),
            new UpdateWrapper<TestData>().eq("id", 1L));
    }

    @Test
    public void selectByMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1L);
        map.put("test_int", 1);
        println(testDataMapper.selectByMap(map));
    }

    @Test
    public void selectPage() {
        IPage<TestData> page = new Page<>();
        page.setSize(5).setCurrent(1);
        IPage<TestData> dataPage = testDataMapper.selectPage(page, new QueryWrapper<TestData>().lambda()
            .eq(TestData::getTestInt, 1));
        System.out.println(dataPage.getTotal());
        System.out.println(dataPage.getRecords().size());
        println(page.getRecords());
    }

    private void println(List<TestData> list) {
        list.forEach(System.out::println);
    }
}
