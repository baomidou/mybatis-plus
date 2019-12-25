package com.baomidou.mybatisplus.test.h2.tenant;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.h2.tenant.model.Student;
import com.baomidou.mybatisplus.test.h2.tenant.service.IStudentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

/**
 * @author nieqiuqiu 2019/12/8
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-tenant-h2.xml"})
class TenantTest {

    @Autowired
    private IStudentService studentService;

    //    @Test
    void testSimple() {
        TenantConfig.TENANT_ID = 2L;
        Student student1 = studentService.getById(1L);
        Assertions.assertNull(student1);
        student1 = studentService.getById(1L);
        Assertions.assertNull(student1);
        TenantConfig.TENANT_ID = 1L;
        Student student2 = studentService.getById(1L);
        Assertions.assertNotNull(student2);
        student2 = studentService.getById(1L);
        Assertions.assertNotNull(student2);
    }

    //    @Test
    void testPage() {
        TenantConfig.TENANT_ID = 2L;
        Page<Student> page1 = studentService.page(new Page<>(0, 10), new QueryWrapper<>());
        Assertions.assertEquals(page1.getTotal(), 1);
        page1 = studentService.page(new Page<>(0, 10), new QueryWrapper<>());
        Assertions.assertEquals(page1.getTotal(), 1);
        TenantConfig.TENANT_ID = 3L;
        Page<Student> page2 = studentService.page(new Page<>(0, 10), new QueryWrapper<>());
        Assertions.assertEquals(page2.getTotal(), 0);
        page2 = studentService.page(new Page<>(0, 10), new QueryWrapper<>());
        Assertions.assertEquals(page2.getTotal(), 0);
    }

    @Test
    void testBatch() {
        TenantConfig.TENANT_ID = 1L;
        List<Student> list = studentService.list();
        list.forEach(student -> student.setName("小红"));
        studentService.updateBatchById(list);
        studentService.list().forEach(student -> Assertions.assertEquals("小红", student.getName()));
    }
}
