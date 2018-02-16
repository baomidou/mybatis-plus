package com.baomidou.mybatisplys.extension.test.h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baomidou.mybatisplus.core.pagination.Page;
import com.baomidou.mybatisplys.extension.test.h2.base.H2Test;
import com.baomidou.mybatisplys.extension.test.h2.entity.mapper.H2UserMapper;
import com.baomidou.mybatisplys.extension.test.h2.entity.persistent.H2Addr;

/**
 * <p>
 * Mybatis Plus H2 Junit Test
 * </p>
 *
 * @author Caratacus
 * @date 2017/4/1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
public class H2UserAddrJoinTest extends H2Test {

    @Autowired
    private H2UserMapper userMapper;

    @BeforeClass
    public static void initDB() throws SQLException, IOException {
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:h2/spring-test-h2.xml");
        DataSource ds = (DataSource) context.getBean("dataSource");
        try (Connection conn = ds.getConnection()) {
            Statement stmt = conn.createStatement();
            stmt.execute(readFile("user.ddl.sql"));
            stmt.execute("truncate table h2user");
            stmt.execute(readFile("addr.ddl.sql"));
            stmt.execute("truncate table h2address");
            executeSql(stmt, "user.insert.sql");
            executeSql(stmt, "addr.insert.sql");
            conn.commit();
        }
    }

    @Test
    public void testJoinTableWithoutPagination() {
        List<H2Addr> addrList = userMapper.getAddrListByUserId(101L);
        Assert.assertEquals(5, addrList.size());
    }

    @Test
    public void testJoinTableWithPagination() {
        Page<H2Addr> page = new Page<H2Addr>(0, 3);
        List<H2Addr> addrList = userMapper.getAddrListByUserIdPage(101L, page);
        Assert.assertNotEquals("Should have pagination info", 0, page.getTotal());
        Assert.assertEquals(3, addrList.size());
    }

}
