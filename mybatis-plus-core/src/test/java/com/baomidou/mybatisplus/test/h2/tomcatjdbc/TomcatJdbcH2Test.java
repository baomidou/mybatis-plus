package com.baomidou.mybatisplus.test.h2.tomcatjdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.test.h2.base.H2Test;
import com.baomidou.mybatisplus.test.h2.config.DBHikaricpH2Config;
import com.baomidou.mybatisplus.test.h2.config.DBTomcatjdbcH2Config;
import com.baomidou.mybatisplus.test.h2.config.MybatisConfigMetaObjOptLockConfig;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserVersionAndLogicDeleteMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2UserVersionAndLogicDeleteEntity;

/**
 * <p>
 * Tomcatjdbc数据源测试
 * </p>
 *
 * @author yuxiaobin
 * @date 2018/1/3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DBTomcatjdbcH2Config.class, MybatisConfigMetaObjOptLockConfig.class})
public class TomcatJdbcH2Test extends H2Test {

    @BeforeClass
    public static void initDB() throws SQLException, IOException {
        @SuppressWarnings("resource")
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(DBHikaricpH2Config.class);
        context.register(MybatisConfigMetaObjOptLockConfig.class);
        context.refresh();
        DataSource ds = (DataSource) context.getBean("dataSource");
        try (Connection conn = ds.getConnection()) {
            initData(conn);
        }
    }

    @Autowired
    H2UserVersionAndLogicDeleteMapper userMapper;

    @Test
    public void testPerformanceInteceptor() {
        userMapper.selectList(new EntityWrapper<H2UserVersionAndLogicDeleteEntity>());
    }
}
