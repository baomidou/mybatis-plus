package com.baomidou.mybatisplus.test.h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
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
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2CamelUnderlineMixMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2Horse;

/**
 * <p>
 * Mybatis Plus H2 Junit Test
 * </p>
 *
 * @author Caratacus
 * @date 2017/4/1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2-mvc.xml"})
public class H2HorseCamelUnderMixTest extends AbstractH2UserTest {

    @Autowired
    H2CamelUnderlineMixMapper camelUnderlineMixMapper;

    @BeforeClass
    public static void initDB() throws SQLException, IOException {
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:h2/spring-test-h2-mvc.xml");
        DataSource ds = (DataSource) context.getBean("dataSource");
        try (Connection conn = ds.getConnection()) {
            initData(conn, "horse.ddl.sql", "horse.insert.sql", "h2horse");
        }
    }

    @Test
    public void testInsert() {
        H2Horse horse = new H2Horse();
        horse.setTestDate(new Date()).setTestType(3);
        horse.setName("horse");
        camelUnderlineMixMapper.insert(horse);
        List<H2Horse> list = camelUnderlineMixMapper.selectList(new EntityWrapper<H2Horse>());
        for(H2Horse horse1: list){
            System.out.println(horse1);
        }
    }

}
