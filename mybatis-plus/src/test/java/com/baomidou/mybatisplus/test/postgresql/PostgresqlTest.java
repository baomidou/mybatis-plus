package com.baomidou.mybatisplus.test.postgresql;

import com.baomidou.mybatisplus.test.postgresql.entity.Pgtable;
import com.baomidou.mybatisplus.test.postgresql.mapper.PgtableMappper;
import com.baomidou.mybatisplus.test.postgresql.service.IPgtableService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:postgresql/spring-test-postgresql.xml"})
public class PostgresqlTest {
    @Autowired
    private PgtableMappper mapper;
    @Autowired
    private IPgtableService pgtableService;

    @Test
    void test() {
        Pgtable pgtable = Pgtable.builder().compName("test").age(10).build();
        Assertions.assertTrue(mapper.insert(pgtable) > 0);
        System.out.println(pgtable.getId());

        List<Pgtable> pgtableList = mapper.selectList(null);
        Assertions.assertNotNull(pgtableList);
        Assertions.assertTrue(pgtableList.size() > 0);
    }

    @Test
    void testTask() throws InterruptedException {
        // 测试线程
        pgtableService.testTask();
        // 主线程休眠
        Thread.sleep(10000);
    }
}
