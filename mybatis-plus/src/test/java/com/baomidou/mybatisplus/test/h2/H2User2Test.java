package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.test.h2.mapper.H2User2Mapper;
import org.apache.ibatis.executor.BatchResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
class H2User2Test extends BaseTest {
    @Autowired
    private H2User2Mapper h2User2Mapper;

    @Test
    @Order(1)
    void testLambdaTypeHandler() {
        List<H2User2> h2User2List = h2User2Mapper.selectList(Wrappers.<H2User2>lambdaQuery().enableAutoMapping()
            .eq(H2User2::getName, "{\"id\":101,\"name\":\"Tomcat\"}"));
        Assertions.assertEquals(1, h2User2List.size());
        Assertions.assertEquals("Tomcat OK", h2User2List.get(0).getName());
    }

    @Test
    @Order(2)
    void testCollectionUpdateById() {
        List<H2User2> h2User2List = new ArrayList<>();
        h2User2List.add(new H2User2(101L, "{\"id\":101,\"name\":\"hi 101\"}"));
        h2User2List.add(new H2User2(102L, "{\"id\":101,\"name\":\"hi 102\"}"));
        List<BatchResult> brList = h2User2Mapper.updateById(h2User2List);
        Assertions.assertEquals(2, brList.get(0).getUpdateCounts().length);
        Assertions.assertEquals("hi 101 OK", h2User2Mapper.selectById(101L).getName());
    }
}
