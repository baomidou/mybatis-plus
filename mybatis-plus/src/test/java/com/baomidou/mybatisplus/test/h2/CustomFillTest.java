package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.test.h2.customfill.mapper.TestModelMapper;
import com.baomidou.mybatisplus.test.h2.customfill.model.TestModel;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-custom-fill-test-h2.xml"})
public class CustomFillTest {

    @Autowired
    private TestModelMapper testModelMapper;

    @BeforeAll
    public static void before(){
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), TestModel.class);
    }

    @Test
    public void testInsert() {

        TestModel testModel = new TestModel();
        testModelMapper.insert(testModel);
        Assertions.assertNotNull(testModel.getA());
        Assertions.assertNotNull(testModel.getB());
    }
}
