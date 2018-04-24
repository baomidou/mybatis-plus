package com.baomidou.mybatisplus.test.h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.test.h2.base.AbstractH2UserTest;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserUnderlineMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2UserUnderline;

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
public class H2UserUnderlineNamingTest extends AbstractH2UserTest {

    @Autowired
    H2UserUnderlineMapper userUnderlineMapper;

    @BeforeClass
    public static void initDB() throws SQLException, IOException {
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:h2/spring-test-h2.xml");
        DataSource ds = (DataSource) context.getBean("dataSource");
        try (Connection conn = ds.getConnection()) {
            initData(conn);
        }
    }

    @Test
    public void testUnderLineNaming() {
        H2UserUnderline user = new H2UserUnderline();
        user.setName("underlineName").setAge(12).setTest_type(1).setVersion(1);
        userUnderlineMapper.insert(user);

        List<H2UserUnderline> list = userUnderlineMapper.selectList(new EntityWrapper<H2UserUnderline>());
        for (H2UserUnderline u : list) {
            System.out.println(u);
        }
    }

}
