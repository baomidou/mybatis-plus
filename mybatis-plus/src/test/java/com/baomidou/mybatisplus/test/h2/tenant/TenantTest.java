package com.baomidou.mybatisplus.test.h2.tenant;

import com.baomidou.mybatisplus.test.h2.tenant.mapper.StudentMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author nieqiuqiu 2019/12/8
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-tenant-h2.xml"})
class TenantTest {

    @Autowired
    private StudentMapper studentMapper;

    @Test
    void test(){
        TenantConfig.TENANT_ID = 2L;
        studentMapper.selectById(1L);
        TenantConfig.TENANT_ID = 1L;
        studentMapper.selectById(1L);
    }

}
