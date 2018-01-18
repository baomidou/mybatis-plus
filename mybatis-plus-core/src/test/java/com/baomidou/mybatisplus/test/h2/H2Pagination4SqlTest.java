package com.baomidou.mybatisplus.test.h2;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.test.h2.base.H2Test;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2Addr;

/**
 * <p>
 * 这个方法跟{@link H2UserAddrJoinTest} 一样，
 * 只是起名为了方便查询自定义分页查询
 * </p>
 *
 * @author Caratacus
 * @date 2017/4/1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
public class H2Pagination4SqlTest extends H2Test {

    @Autowired
    private H2UserMapper userMapper;

    @BeforeClass
    public static void initDB() throws SQLException, IOException {
        H2UserAddrJoinTest.initDB();
    }

    @Test
    public void testJoinTableWithoutPagination() {
        List<H2Addr> addrList = userMapper.getAddrListByUserId(101L);
        Assert.assertEquals(5, addrList.size());
    }

    @Test
    public void testJoinTableWithPagination() {
        this.testOptimizeCountSqlPagination(false);
        this.testOptimizeCountSqlPagination(true);
    }

    private void testOptimizeCountSqlPagination(boolean optimizeCountSql) {
        Page<H2Addr> page = new Page<H2Addr>(0, 3);
        page.setOptimizeCountSql(optimizeCountSql);
        List<H2Addr> addrList = userMapper.getAddrListByUserIdPage(101L, page);
        Assert.assertNotEquals("Should have pagination info", 0, page.getTotal());
        Assert.assertEquals(3, addrList.size());
    }

}
