package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.test.h2.keygenerator.mapper.KeyGeneratorMapper;
import com.baomidou.mybatisplus.test.h2.keygenerator.model.KeyGeneratorModel;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-keygenerator-test-h2.xml"})
class H2KeyGeneratorTest {

    @Autowired
    private KeyGeneratorMapper keyGeneratorMapper;

    @Test
    void test(){
        KeyGeneratorModel keyGeneratorModel = new KeyGeneratorModel();
        keyGeneratorModel.setName("我举起了个栗子");
        keyGeneratorMapper.insert(keyGeneratorModel);
    }

}
