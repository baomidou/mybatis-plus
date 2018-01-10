package com.baomidou.mybatisplus.test.h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.test.h2.base.H2Test;
import com.baomidou.mybatisplus.test.h2.config.DBConfig;
import com.baomidou.mybatisplus.test.h2.config.MybatisPlusConfigMapUnderline2Camel;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2User;
import com.baomidou.mybatisplus.test.h2.service.IH2UserService;

/**
 * <p>
 * 测试返回Map结果集，下划线自动转驼峰
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/12/19
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DBConfig.class, MybatisPlusConfigMapUnderline2Camel.class})
public class H2MapUnderline2CamelTest extends H2Test {

    @Autowired
    IH2UserService userService;

    @Autowired
    H2UserMapper h2UserMapper;

    @BeforeClass
    public static void init() throws SQLException, IOException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(DBConfig.class);
        context.refresh();
        DataSource ds = (DataSource) context.getBean("dataSource");
        try (Connection conn = ds.getConnection()) {
            initData(conn);
        }
    }

    @Test
    public void testMapUnderline2Camel() {
        List<Map<String, Object>> list = userService.selectMaps(new EntityWrapper<H2User>());
        for (Map<String, Object> map : list) {
            System.out.println(map);
            Object obj = map.get("TEST_DATE");
            if (obj != null) {
                System.out.println("TEST_DATE.class" + obj.getClass().getName());
            } else {
                obj = map.get("test_date");
                if (obj != null) {
                    System.out.println("test_date.class" + obj.getClass().getName());
                } else {
                    obj = map.get("testDate");
                    if (obj != null) {
                        System.out.println("testDate.class" + obj.getClass().getName());
                    } else {
                        obj = map.get("TESTDATE");
                        if (obj != null) {
                            System.out.println("TESTDATE.class" + obj.getClass().getName());
                        } else {
                            obj = map.get("testdate");
                            if (obj != null) {
                                System.out.println("testdate.class" + obj.getClass().getName());
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testMapUnderline2CamelMyMethods() {
        List<Map> list = userService.mySelectMaps();
        for (Map map : list) {
            System.out.println(JSONObject.toJSON(map));
            System.out.println("TESTID=" + map.get("TESTID"));
            System.out.println("testid=" + map.get("testid"));
            System.out.println("testId=" + map.get("testId"));
            System.out.println("TEST_ID=" + map.get("TEST_ID"));
            System.out.println("test_id=" + map.get("test_id"));
//            Assert.assertNotNull("test_id should be auto converted to testId", map.get("testId"));
//            Assert.assertNull("test_id should be auto converted to testId", map.get("TESTID"));
//            Assert.assertNull("test_id should be auto converted to testId", map.get("testid"));
//            Assert.assertNull("test_id should be auto converted to testId", map.get("TEST_ID"));
//            Assert.assertNull("test_id should be auto converted to testId", map.get("test_id"));
//            Assert.assertNotNull("test_type should be auto converted to testType", map.get("testType"));
        }
    }

    @Test
    public void testSelectSql4SelectMaps() {
        EntityWrapper<H2User> ew = new EntityWrapper<>();
        ew.setSqlSelect("test_id, test_type");
        List<Map<String, Object>> list = userService.selectMaps(ew);
        for (Map<String, Object> map : list) {
            System.out.println(map);
            Object obj = map.get("TEST_DATE");
            if (obj != null) {
                System.out.println("TEST_DATE.class" + obj.getClass().getName());
            } else {
                obj = map.get("test_date");
                if (obj != null) {
                    System.out.println("test_date.class" + obj.getClass().getName());
                } else {
                    obj = map.get("testDate");
                    if (obj != null) {
                        System.out.println("testDate.class" + obj.getClass().getName());
                    } else {
                        obj = map.get("TESTDATE");
                        if (obj != null) {
                            System.out.println("TESTDATE.class" + obj.getClass().getName());
                        } else {
                            obj = map.get("testdate");
                            if (obj != null) {
                                System.out.println("testdate.class" + obj.getClass().getName());
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testSelectSqlNotMapping() {
        EntityWrapper<H2User> ew = new EntityWrapper<>();
        ew.setSqlSelect("test_id, test_type");
        List<H2User> list = userService.selectList(ew);
        for (H2User u : list) {
            System.out.println(JSONObject.toJSON(u));
            Assert.assertNull("test_id is not null, but should not mapping to id", u.getId());
            Assert.assertNotNull("test_type should be convert to testType", u.getTestType());
        }
    }

    @Test
    public void testMpSelect() {
        List<H2User> list = userService.selectList(new EntityWrapper<H2User>());
        for (H2User u : list) {
            Assert.assertNotNull("id should not be null", u.getId());
            Assert.assertNotNull("test_type should not be null", u.getTestType());
        }
    }
}
