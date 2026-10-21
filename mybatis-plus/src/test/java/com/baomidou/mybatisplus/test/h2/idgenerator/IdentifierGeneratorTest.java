package com.baomidou.mybatisplus.test.h2.idgenerator;

import com.baomidou.mybatisplus.test.h2.idgenerator.mapper.*;
import com.baomidou.mybatisplus.test.h2.idgenerator.model.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Autowired
    private BigDecimalIdGeneratorMapper bigDecimalIdGeneratorMapper;

    @Autowired
    private BigIntegerIdGeneratorMapper bigIntegerIdGeneratorMapper;

    @Test
    void test() {
        LongIdGeneratorModel longIdGeneratorModel1 = new LongIdGeneratorModel("旺仔");
        longIdGeneratorMapper.insert(longIdGeneratorModel1);
        assertThat(longIdGeneratorModel1.getId()).isEqualTo(666L);
        LongIdGeneratorModel longIdGeneratorModel2 = new LongIdGeneratorModel("靓仔");
        longIdGeneratorMapper.insert(longIdGeneratorModel2);
        assertThat(longIdGeneratorModel2.getId()).isEqualTo(777L);

        IntegerIdGeneratorModel integerIdGeneratorModel1 = new IntegerIdGeneratorModel("旺仔");
        integerIdGeneratorMapper.insert(integerIdGeneratorModel1);
        assertThat(integerIdGeneratorModel1.getId()).isEqualTo(666);
        IntegerIdGeneratorModel integerIdGeneratorModel2 = new IntegerIdGeneratorModel("靓仔");
        integerIdGeneratorMapper.insert(integerIdGeneratorModel2);
        assertThat(integerIdGeneratorModel2.getId()).isEqualTo(777);

        StringIdGeneratorModel stringIdGeneratorModel1 = new StringIdGeneratorModel("旺仔");
        stringIdGeneratorMapper.insert(stringIdGeneratorModel1);
        assertThat(stringIdGeneratorModel1.getId()).isEqualTo("66666666666");
        StringIdGeneratorModel stringIdGeneratorModel2 = new StringIdGeneratorModel("靓仔");
        stringIdGeneratorMapper.insert(stringIdGeneratorModel2);
        assertThat(stringIdGeneratorModel2.getId()).isEqualTo("77777777777");

        LongStringIdGeneratorModel longStringIdGeneratorModel1 = new LongStringIdGeneratorModel("旺仔");
        longStringIdGeneratorMapper.insert(longStringIdGeneratorModel1);
        assertThat(longStringIdGeneratorModel1.getId()).isEqualTo("666");
        LongStringIdGeneratorModel longStringIdGeneratorModel2 = new LongStringIdGeneratorModel("靓仔");
        longStringIdGeneratorMapper.insert(longStringIdGeneratorModel2);
        assertThat(longStringIdGeneratorModel2.getId()).isEqualTo("777");

        IntegerStringIdGeneratorModel integerStringIdGeneratorModel1 = new IntegerStringIdGeneratorModel("旺仔");
        integerStringIdGeneratorMapper.insert(integerStringIdGeneratorModel1);
        assertThat(integerStringIdGeneratorModel1.getId()).isEqualTo("666");
        IntegerStringIdGeneratorModel integerStringIdGeneratorModel2 = new IntegerStringIdGeneratorModel("靓仔");
        integerStringIdGeneratorMapper.insert(integerStringIdGeneratorModel2);
        assertThat(integerStringIdGeneratorModel2.getId()).isEqualTo("777");

        BigDecimalIdGeneratorModel bigDecimalIdGeneratorModel = new BigDecimalIdGeneratorModel("测试");
        bigDecimalIdGeneratorMapper.insert(bigDecimalIdGeneratorModel);
        bigDecimalIdGeneratorMapper.deleteById(bigDecimalIdGeneratorModel);

        BigIntegerIdGeneratorModel bigIntegerIdGeneratorModel = new BigIntegerIdGeneratorModel("测试");
        bigIntegerIdGeneratorMapper.insert(bigIntegerIdGeneratorModel);
        bigIntegerIdGeneratorMapper.deleteById(bigIntegerIdGeneratorModel);
    }
}
