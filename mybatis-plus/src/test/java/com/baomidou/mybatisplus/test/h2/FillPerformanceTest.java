package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.test.h2.fillperformance.model.PerformanceModel;
import com.baomidou.mybatisplus.test.h2.fillperformance.service.IPerformanceModelService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-fill-performance-h2.xml"})
class FillPerformanceTest {

    @Autowired
    private IPerformanceModelService performanceModelService;

    @Test
    void test(){
        List<PerformanceModel> list = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            list.add(new PerformanceModel());
        }
        System.out.println("-------------------------");
        long start = System.currentTimeMillis();
        performanceModelService.saveBatch(list);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        System.out.println("-------------------------");
    }
}
