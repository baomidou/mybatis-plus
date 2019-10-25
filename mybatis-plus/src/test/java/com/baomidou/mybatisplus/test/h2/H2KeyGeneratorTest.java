package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.test.h2.keygenerator.mapper.ExtendKeyGeneratorMapper;
import com.baomidou.mybatisplus.test.h2.keygenerator.mapper.KeyGeneratorMapper;
import com.baomidou.mybatisplus.test.h2.keygenerator.mapper.LongKeyGeneratorMapper;
import com.baomidou.mybatisplus.test.h2.keygenerator.mapper.StringKeyGeneratorMapper;
import com.baomidou.mybatisplus.test.h2.keygenerator.model.ExtendKeyGeneratorModel;
import com.baomidou.mybatisplus.test.h2.keygenerator.model.KeyGeneratorModel;
import com.baomidou.mybatisplus.test.h2.keygenerator.model.LongKeyGeneratorModel;
import com.baomidou.mybatisplus.test.h2.keygenerator.model.StringKeyGeneratorModel;
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
@ContextConfiguration(locations = {"classpath:h2/spring-keygenerator-test-h2.xml"})
class H2KeyGeneratorTest {

    @Autowired
    private KeyGeneratorMapper keyGeneratorMapper;

    @Autowired
    private LongKeyGeneratorMapper longKeyGeneratorMapper;

    @Autowired
    private StringKeyGeneratorMapper stringKeyGeneratorMapper;

    @Autowired
    private ExtendKeyGeneratorMapper extendKeyGeneratorMapper;

    @Test
    void test(){
        KeyGeneratorModel keyGeneratorModel = new KeyGeneratorModel();
        keyGeneratorModel.setName("我举起了咩咩");
        keyGeneratorMapper.insert(keyGeneratorModel);
        Assertions.assertNotNull(keyGeneratorModel.getUid());

        LongKeyGeneratorModel longKeyGeneratorModel = new LongKeyGeneratorModel();
        longKeyGeneratorModel.setName("我举起了个栗子");
        longKeyGeneratorMapper.insert(longKeyGeneratorModel);
        Assertions.assertNotNull(longKeyGeneratorModel.getId());

        StringKeyGeneratorModel stringKeyGeneratorModel = new StringKeyGeneratorModel();
        stringKeyGeneratorModel.setName("我举起了个锤子");
        stringKeyGeneratorMapper.insert(stringKeyGeneratorModel);
        Assertions.assertNotNull(stringKeyGeneratorModel.getId());

        ExtendKeyGeneratorModel extendKeyGeneratorModel = new ExtendKeyGeneratorModel();
        extendKeyGeneratorModel.setName("我举起了句号");
        extendKeyGeneratorMapper.insert(extendKeyGeneratorModel);
        Assertions.assertNotNull(extendKeyGeneratorModel.getUid());
    }

}
