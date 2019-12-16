package com.baomidou.mybatisplus.test.h2.tenant;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.h2.tenant.mapper.StudentMapper;
import com.baomidou.mybatisplus.test.h2.tenant.model.Student;
import org.junit.jupiter.api.Assertions;
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

//    @Test
    void testSimple(){
        TenantConfig.TENANT_ID = 2L;
        Student student1 = studentMapper.selectById(1L);
        Assertions.assertNull(student1);
        student1 = studentMapper.selectById(1L);
        Assertions.assertNull(student1);
        TenantConfig.TENANT_ID = 1L;
        Student student2 = studentMapper.selectById(1L);
        Assertions.assertNotNull(student2);
        student2 = studentMapper.selectById(1L);
        Assertions.assertNotNull(student2);
    }

//    @Test
    void testPage(){
        TenantConfig.TENANT_ID = 2L;
        Page<Student> page1 = studentMapper.selectPage(new Page<>(0, 10), new QueryWrapper<>());
        Assertions.assertEquals(page1.getTotal(),1);
        page1 = studentMapper.selectPage(new Page<>(0, 10), new QueryWrapper<>());
        Assertions.assertEquals(page1.getTotal(),1);
        TenantConfig.TENANT_ID = 3L;
        Page<Student> page2 = studentMapper.selectPage(new Page<>(0,10), new QueryWrapper<>());
        Assertions.assertEquals(page2.getTotal(),0);
        page2 = studentMapper.selectPage(new Page<>(0,10), new QueryWrapper<>());
        Assertions.assertEquals(page2.getTotal(),0);
    }

}
