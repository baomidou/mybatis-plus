package com.baomidou.mybatisplus.test.mysql;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.test.base.entity.TestData;
import com.baomidou.mybatisplus.test.base.mapper.TestDataMapper;
import com.baomidou.mybatisplus.test.h2.BaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * Mybatis Plus H2 Junit Test
 * </p>
 *
 * @author hubin
 * @since 2018-06-05
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:mysql/spring-test-mysql.xml"})
public class MysqlTestDataMapperTest extends BaseTest {

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
            .and()
            .like("test_str", 1)));
    }

    @Test
    public void specialSelectList() {
        println(testDataMapper.selectList(new QueryWrapper<TestData>().lambda()
            .eq(TestData::getId, 1L)
            .and()
            .eq(TestData::getTestInt, 1)));
    }

    private void println(List<TestData> list) {
        list.forEach(System.out::println);
    }
}
