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
        List<TestData> list = testDataMapper.selectList(new QueryWrapper<TestData>()
            .eq("id", 1L)
            .like("test_str", 1));
        list.forEach(System.out::println);
    }

    @Test
    public void specialSelectList() {
        testDataMapper.selectList(new QueryWrapper<TestData>().lambda()
            .and());
    }
}
