package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.test.h2.idgenerator.mapper.*;
import com.baomidou.mybatisplus.test.h2.idgenerator.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-id-generator-h2.xml"})
class IdentifierGeneratorTest {

    @Autowired
    private LongIdGeneratorMapper longIdGeneratorMapper;

    @Autowired
    private IntegerIdGeneratorMapper integerIdGeneratorMapper;

    @Autowired
    private StringIdGeneratorMapper stringIdGeneratorMapper;

    @Autowired
    private LongStringIdGeneratorMapper longStringIdGeneratorMapper;

    @Autowired
    private IntegerStringIdGeneratorMapper integerStringIdGeneratorMapper;

    @Test
    void test() {
        LongIdGeneratorModel longIdGeneratorModel1 = new LongIdGeneratorModel("旺仔");
        longIdGeneratorMapper.insert(longIdGeneratorModel1);
        Assertions.assertEquals(666L, longIdGeneratorModel1.getId());
        LongIdGeneratorModel longIdGeneratorModel2 = new LongIdGeneratorModel("靓仔");
        longIdGeneratorMapper.insert(longIdGeneratorModel2);
        Assertions.assertEquals(777L, longIdGeneratorModel2.getId());

        IntegerIdGeneratorModel integerIdGeneratorModel1 = new IntegerIdGeneratorModel("旺仔");
        integerIdGeneratorMapper.insert(integerIdGeneratorModel1);
        Assertions.assertEquals(666, integerIdGeneratorModel1.getId());
        IntegerIdGeneratorModel integerIdGeneratorModel2 = new IntegerIdGeneratorModel("靓仔");
        integerIdGeneratorMapper.insert(integerIdGeneratorModel2);
        Assertions.assertEquals(777, integerIdGeneratorModel2.getId());

        StringIdGeneratorModel stringIdGeneratorModel1 = new StringIdGeneratorModel("旺仔");
        stringIdGeneratorMapper.insert(stringIdGeneratorModel1);
        Assertions.assertEquals("66666666666", stringIdGeneratorModel1.getId());
        StringIdGeneratorModel stringIdGeneratorModel2 = new StringIdGeneratorModel("靓仔");
        stringIdGeneratorMapper.insert(stringIdGeneratorModel2);
        Assertions.assertEquals("77777777777", stringIdGeneratorModel2.getId());

        LongStringIdGeneratorModel longStringIdGeneratorModel1 = new LongStringIdGeneratorModel("旺仔");
        longStringIdGeneratorMapper.insert(longStringIdGeneratorModel1);
        Assertions.assertEquals("666", longStringIdGeneratorModel1.getId());
        LongStringIdGeneratorModel longStringIdGeneratorModel2 = new LongStringIdGeneratorModel("靓仔");
        longStringIdGeneratorMapper.insert(longStringIdGeneratorModel2);
        Assertions.assertEquals("777", longStringIdGeneratorModel2.getId());


        IntegerStringIdGeneratorModel integerStringIdGeneratorModel1 = new IntegerStringIdGeneratorModel("旺仔");
        integerStringIdGeneratorMapper.insert(integerStringIdGeneratorModel1);
        Assertions.assertEquals("666", integerStringIdGeneratorModel1.getId());
        IntegerStringIdGeneratorModel integerStringIdGeneratorModel2 = new IntegerStringIdGeneratorModel("靓仔");
        integerStringIdGeneratorMapper.insert(integerStringIdGeneratorModel2);
        Assertions.assertEquals("777", integerStringIdGeneratorModel2.getId());
    }
}
