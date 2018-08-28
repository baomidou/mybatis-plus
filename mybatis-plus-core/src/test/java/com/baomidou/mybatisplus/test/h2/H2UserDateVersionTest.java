package com.baomidou.mybatisplus.test.h2;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.test.h2.base.H2Test;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserDateVersionMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2UserDateVersion;

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
public class H2UserDateVersionTest extends H2Test {

    @Autowired
    private H2UserDateVersionMapper userMapper;

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
    public void testUpdateByIdNoDateVersion() {
        Long id = 991L;
        H2UserDateVersion user = new H2UserDateVersion();
        user.setId(id);
        user.setName("991");
        user.setAge(91);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        userMapper.insertAllColumn(user);

        H2UserDateVersion userDB = userMapper.selectById(id);
        Assert.assertEquals(null, userDB.getTestDate());

        userDB.setName("991");
        userMapper.updateById(userDB);

        userDB = userMapper.selectById(id);
        Assert.assertEquals("991", userDB.getName());
        Assert.assertEquals(null, userDB.getTestDate());
    }


    @Test
    public void testUpdateByEntityWrapperNoDateVersion() {
        Long id = 992L;
        H2UserDateVersion user = new H2UserDateVersion();
        user.setId(id);
        user.setName("992");
        user.setAge(92);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        userMapper.insertAllColumn(user);

        H2UserDateVersion userDB = userMapper.selectById(id);

        H2UserDateVersion updUser = new H2UserDateVersion();
        updUser.setName("999");

        userMapper.update(updUser, new EntityWrapper<>(userDB));

        userDB = userMapper.selectById(id);
        Assert.assertEquals("999", userDB.getName());
    }

    @Test
    public void testUpdateByIdWithDateVersion() {
        Long id = 994L;
        H2UserDateVersion user = new H2UserDateVersion();
        user.setId(id);
        user.setName("994");
        user.setAge(91);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        user.setTestDate(cal.getTime());
        userMapper.insertAllColumn(user);

        System.out.println("before update: testDate=" + user.getTestDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        H2UserDateVersion userDB = userMapper.selectById(id);

        Assert.assertNotNull(userDB.getTestDate());
        String originalDateVersionStr = sdf.format(cal.getTime());
        Assert.assertEquals(originalDateVersionStr, sdf.format(userDB.getTestDate()));

        userDB.setName("991");
        userMapper.updateById(userDB);
        userDB = userMapper.selectById(id);
        Assert.assertEquals("991", userDB.getName());
        Date versionDate = userDB.getTestDate();
        System.out.println("after update: testDate=" + versionDate);
        String versionDateStr = sdf.format(versionDate);
        Assert.assertEquals("@version field testDate should be updated to current sysdate", sdf.format(new Date()), versionDateStr);

        Assert.assertNotEquals("@version field testDate should be updated to current sysdate", originalDateVersionStr, versionDateStr);

    }

    @Test
    public void testUpdateByEntityWrapperWithDateVersion() {
        Long id = 993L;
        H2UserDateVersion user = new H2UserDateVersion();
        user.setId(id);
        user.setName("992");
        user.setAge(92);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);

        Date oldDateVersion = cal.getTime();
        user.setTestDate(oldDateVersion);
        userMapper.insertAllColumn(user);

        H2UserDateVersion userDB = userMapper.selectById(id);

        H2UserDateVersion updUser = new H2UserDateVersion();
        updUser.setName("999");
        updUser.setTestDate(oldDateVersion);
        userMapper.update(updUser, new EntityWrapper<H2UserDateVersion>());

        System.out.println("before update: testDate=" + userDB.getTestDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");

        userDB = userMapper.selectById(id);
        Assert.assertEquals("999", userDB.getName());

        Date versionDate = userDB.getTestDate();
        System.out.println("after update: testDate=" + versionDate);
        String versionDateStr = sdf.format(versionDate);
        Assert.assertEquals("@version field testDate should be updated to current sysdate", sdf.format(new Date()), versionDateStr);
    }


}
