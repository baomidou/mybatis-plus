package com.baomidou.mybatisplus.test.oracle;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.test.oracle.config.OracleDBConfig;
import com.baomidou.mybatisplus.test.oracle.config.OracleMybatisPlusConfig;
import com.baomidou.mybatisplus.test.oracle.entity.TestSequser;
import com.baomidou.mybatisplus.test.oracle.mapper.TestSequserMapper;

/**
 * <p>
 * oracle user test for spring
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/6/14
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {OracleDBConfig.class, OracleMybatisPlusConfig.class})
public class OracleUserTest {

    @Autowired
    TestSequserMapper sequserMapper;

    @Test
    public void testSelectListMp() {
        List<TestSequser> list = sequserMapper.selectList(new EntityWrapper<TestSequser>());
        for (TestSequser u : list) {
            System.out.println(u);
        }
    }

    @Test
    public void testSelectListNative() {
        List<TestSequser> list = sequserMapper.getList();
        for (TestSequser u : list) {
            System.out.println(u);
        }
    }

    @Test
    public void testInsert() {
        TestSequser user = new TestSequser();
        user.setName("seqtest0627");
        user.setAge(12);
        user.setTestType(1);
        sequserMapper.insert(user);
        Long id = user.getId();
        Assert.assertNotNull(id);
    }

    @Test
    public void testPagination() {
        Page<TestSequser> page = new Page<>(1, 10);
        EntityWrapper<TestSequser> ew = new EntityWrapper<>();
        ew.orderBy("TEST_ID");
        List<TestSequser> list = sequserMapper.selectPage(page, ew);
        System.out.println("Id in DB: 1,2,3,4,5,6,7,8,9,10");
        Assert.assertEquals("Id should start from 1", 1, list.get(0).getId().longValue());
        Assert.assertEquals("Id should end with 10", 10, list.get(9).getId().longValue());
    }

}
