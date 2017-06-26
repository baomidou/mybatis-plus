package com.baomidou.mybatisplus.test.h2;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.test.h2.config.ServiceConfig;
import com.baomidou.mybatisplus.test.h2.config.ServiceSequenceConfig;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2UserSequence;
import com.baomidou.mybatisplus.test.h2.service.IH2UserSequenceService;

/**
 * <p>
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/6/26
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceSequenceConfig.class})
public class H2UserSequenceTest extends H2Test{

    @Autowired
    IH2UserSequenceService userService;

    @BeforeClass
    public static void init() throws SQLException, IOException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(ServiceConfig.class);
        context.refresh();
        DataSource ds = (DataSource) context.getBean("dataSource");
        try (Connection conn = ds.getConnection()) {
            String createTableSql = readFile("user.ddl.sql");
            Statement stmt = conn.createStatement();
            stmt.execute(createTableSql);
            stmt.execute("truncate table h2user");
            executeSql(stmt, "user.insert.sql");
            stmt.execute("CREATE SEQUENCE SEQ_TEST");
            conn.commit();
        }
    }


    @Test
    public void testInsert(){
        List<H2UserSequence> list = userService.selectList(new EntityWrapper<H2UserSequence>());
        for(H2UserSequence u:list){
            System.out.println(u);
        }
        H2UserSequence user = new H2UserSequence();
        user.setAge(1);
        user.setPrice(new BigDecimal("9.99"));
        userService.insert(user);

        list = userService.selectList(new EntityWrapper<H2UserSequence>());
        for(H2UserSequence u:list){
            System.out.println(u);
        }

        Assert.assertNotNull(user.getId());
        user.setDesc("Caratacus");
        userService.insertOrUpdate(user);
        H2UserSequence userFromDB = userService.selectById(user.getId());
        Assert.assertEquals("Caratacus", userFromDB.getDesc());
    }

    @Test
    public void testInsertBatch(){
        List<H2UserSequence> list= new ArrayList<>();
        for(int i=0;i<5;++i) {
            list.add(new H2UserSequence("name"+i, 1));
        }
        userService.insertBatch(list);
        for(H2UserSequence u:list){
            System.out.println("id="+u.getId());
            Assert.assertNotNull(u.getId());
        }

    }

}
