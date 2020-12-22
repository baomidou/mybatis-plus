package com.baomidou.mybatisplus.test.mysql;

import com.baomidou.mybatisplus.extension.spring.ext.AnnotationTransactionAttributeSourceReplacer;
import com.baomidou.mybatisplus.test.mysql.service.ICommonDataService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.TestContextTransactionUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;

/**
 * @author dyu 2020/12/21 16:27
 */
@DirtiesContext
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:mysql/spring-test-mysql.xml"})
@TestPropertySource({"classpath:mysql/test_tm.properties"})
public class MysqlAssignTmTest {

    @Autowired
    private ICommonDataService commonDataService;

    @Test
    public void testAssignTm() {
        Assertions.assertTrue(commonDataService.saveOrUpdate(null));
        Assertions.assertTrue(commonDataService.saveOrUpdateBatch(null, 1));
    }
}
