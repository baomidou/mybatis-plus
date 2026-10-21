package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-fill-performance-h2.xml"})
class FillPerformanceTest {

    private static final int BATCH_SIZE = 5000;

    @Autowired
    private IPerformanceModelService performanceModelService;

    /**
     * 批量插入 5000 行只是冒烟,不断言耗时;真正钉住的是每一行都走完了自动填充,且没填到不该填的列。
     */
    @Test
    void test() {
        // 表只有本用例在用,先清一次,后面才能用绝对条数断言
        performanceModelService.remove(new QueryWrapper<>());
        assertThat(performanceModelService.count()).isZero();

        List<PerformanceModel> list = new ArrayList<>();
        for (int i = 0; i < BATCH_SIZE; i++) {
            list.add(new PerformanceModel());
        }
        assertThat(performanceModelService.saveBatch(list)).isTrue();

        assertThat(performanceModelService.count()).isEqualTo((long) BATCH_SIZE);
        assertThat(performanceModelService.count(new QueryWrapper<PerformanceModel>().isNotNull("id")))
                .as("id 列没有自增,主键得靠 MP 生成").isEqualTo((long) BATCH_SIZE);
        // insertFill 用 setFieldValByName 填 c..m 这十列
        assertThat(performanceModelService.count(new QueryWrapper<PerformanceModel>().eq("c", "1234567890")))
                .isEqualTo((long) BATCH_SIZE);
        assertThat(performanceModelService.count(new QueryWrapper<PerformanceModel>().eq("m", "1234567890")))
                .isEqualTo((long) BATCH_SIZE);
        // a/b/k/n 不参与填充,不该被顺手写上值
        assertThat(performanceModelService.count(new QueryWrapper<PerformanceModel>().isNull("k")))
                .isEqualTo((long) BATCH_SIZE);
        assertThat(performanceModelService.count(new QueryWrapper<PerformanceModel>().isNull("a")))
                .isEqualTo((long) BATCH_SIZE);
    }
}
