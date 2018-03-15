package com.baomidou.mybatisplus.extension.test.h2;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
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

import com.baomidou.mybatisplus.extension.test.h2.base.AbstractH2UserTest;
import com.baomidou.mybatisplus.extension.test.h2.entity.persistent.H2UserLogicDeleteWithAR;
import com.baomidou.mybatisplus.extension.test.h2.entity.persistent.H2UserVersionIntWithAR;
import com.baomidou.mybatisplus.extension.test.h2.service.IH2UserLogicDeleteService;
import com.baomidou.mybatisplus.extension.test.h2.service.IH2UserVersionIntWithARService;

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
public class H2UserVersionIntWithARTest extends AbstractH2UserTest {

    @BeforeClass
    public static void initDB() throws SQLException, IOException {
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:h2/spring-test-h2.xml");
        DataSource ds = (DataSource) context.getBean("dataSource");
        try (Connection conn = ds.getConnection()) {
            initData(conn);
        }
    }

    @Autowired
    IH2UserVersionIntWithARService arService;

    @Autowired
    private IH2UserLogicDeleteService logicDeleteService;

    @Test
    public void testOptLock4AR() {
        H2UserVersionIntWithAR user = new H2UserVersionIntWithAR();
        user.setName("ARTest").setAge(18).setPrice(BigDecimal.TEN).setTestDate(new Date()).setVersion(1);
        user.insert();
        Long id = user.getId();
        H2UserVersionIntWithAR userDB = user.selectById(id);
        Assert.assertNotNull("should insert success", userDB);
        Assert.assertNotNull("autofill should work when do insert", userDB.getTestType());
        Assert.assertNull("lastUpdateDt should be null", userDB.getLastUpdatedDt());

        Assert.assertEquals("init version=1", 1, userDB.getVersion().intValue());
        String updateName = "ARTestUpdate";
        user.setName(updateName).updateById();

        userDB = user.selectById(id);
        Assert.assertNotNull(userDB);
        Assert.assertEquals("version should updated", 2, userDB.getVersion().intValue());
        Assert.assertNotNull("autofill should work when do update", userDB.getLastUpdatedDt());
    }


    @Test
    public void testLogicDelete() {
        H2UserLogicDeleteWithAR user = new H2UserLogicDeleteWithAR();
        user.setName("ARLogicDelete").setAge(1).setVersion(1).setTestDate(new Date()).insert();
        Long id = user.getId();
        user = user.selectById(id);
        Assert.assertNotNull("should have record for id=" + id, user);
        /**
         * 测试service的逻辑删除
         */
        logicDeleteService.deleteById(id);
        Assert.assertNull("should logic deleted", logicDeleteService.selectById(id));
        Assert.assertNotNull("should logic deleted, not real delete", arService.selectByIdWithoutLogicDeleteLimit(id));
        arService.updateLogicDeletedRecord(id);
        /**
         * 测试AR的逻辑删除
         */
        user.deleteById();
        Assert.assertNull("should be logic deleted for id=" + id, user.selectById(id));
        Assert.assertNotNull("record is logic deleted, should not null", arService.selectByIdWithoutLogicDeleteLimit(id));
        arService.updateLogicDeletedRecord(id);
    }

}
