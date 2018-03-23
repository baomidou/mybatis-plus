package com.baomidou.mybatisplus.extension.test.h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baomidou.mybatisplus.extension.test.h2.base.H2Test;
import com.baomidou.mybatisplus.extension.test.h2.config.ServiceConfig;
import com.baomidou.mybatisplus.extension.test.h2.entity.mapper.H2PersonLogicDeleteMapper;
import com.baomidou.mybatisplus.extension.test.h2.entity.persistent.H2PersonLogicDelete;

/**
 * <p>
 * 逻辑删除 - 自定义sql也带上逻辑删除标识
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/6/15
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceConfig.class})
public class H2LogicDeleteSqlParserTest extends H2Test {

    @BeforeClass
    public static void init() throws SQLException, IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:h2/spring-test-h2-mvc-camel.xml");
        DataSource ds = (DataSource) context.getBean("dataSource");
        try (Connection conn = ds.getConnection()) {
            initData(conn, "person.ddl.sql", "person.insert.sql", "h2person");
            initData(conn, "addr.ddl.sql", "addr.insert.sql", "h2address");
        }
    }

    @Autowired
    private H2PersonLogicDeleteMapper personMapper;

    @Test
    public void testInsert() {
        H2PersonLogicDelete person = new H2PersonLogicDelete();
        person.setName("abc").setTestType(1);
        personMapper.insert(person);
        Long id = person.getId();
        person = personMapper.selectById(id);
        System.out.println(person);
    }


    @Test
    public void testMySelect(){
        personMapper.getAddrListByUserId(1L);
        //TODO: 然并无
    }

}
