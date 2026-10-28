package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.test.h2.keygenerator.mapper.*;
import com.baomidou.mybatisplus.test.h2.keygenerator.model.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-keygenerator-h2.xml"})
@Tag("integration")
class H2KeyGeneratorTest {

    @Autowired
    private KeyGeneratorMapper keyGeneratorMapper;

    @Autowired
    private LongKeyGeneratorMapper longKeyGeneratorMapper;

    @Autowired
    private StringKeyGeneratorMapper stringKeyGeneratorMapper;

    @Autowired
    private ExtendKeyGeneratorMapper extendKeyGeneratorMapper;

    @Autowired
    private IntegerKeyGeneratorMapper integerKeyGeneratorMapper;

    @Test
    void shouldGenerateKeysAcrossAllIdTypes() {
        KeyGeneratorModel keyGeneratorModel = new KeyGeneratorModel();
        keyGeneratorModel.setName("我举起了咩咩");
        keyGeneratorMapper.insert(keyGeneratorModel);
        assertThat(keyGeneratorModel.getUid()).isNotNull();
        assertThat(keyGeneratorModel.getUid()).isEqualTo(1L);

        LongKeyGeneratorModel longKeyGeneratorModel = new LongKeyGeneratorModel();
        longKeyGeneratorModel.setName("我举起了个栗子");
        longKeyGeneratorMapper.insert(longKeyGeneratorModel);
        assertThat(longKeyGeneratorModel.getId()).isNotNull();
        assertThat(longKeyGeneratorModel.getId()).isEqualTo(2L);

        StringKeyGeneratorModel stringKeyGeneratorModel = new StringKeyGeneratorModel();
        stringKeyGeneratorModel.setName("我举起了个锤子");
        stringKeyGeneratorMapper.insert(stringKeyGeneratorModel);
        assertThat(stringKeyGeneratorModel.getId()).isNotNull();
        assertThat(stringKeyGeneratorModel.getId()).isEqualTo("3");

        ExtendKeyGeneratorModel extendKeyGeneratorModel = new ExtendKeyGeneratorModel();
        extendKeyGeneratorModel.setName("我举起了句号");
        extendKeyGeneratorMapper.insert(extendKeyGeneratorModel);
        assertThat(extendKeyGeneratorModel.getUid()).isNotNull();
        assertThat(extendKeyGeneratorModel.getUid()).isEqualTo(4L);

        //这个受限数据库，如果返回是long的话,那就救不了.
        IntegerKeyGeneratorModel integerKeyGeneratorModel = new IntegerKeyGeneratorModel();
        integerKeyGeneratorModel.setName("我举起了K神");
        integerKeyGeneratorMapper.insert(integerKeyGeneratorModel);
        assertThat(integerKeyGeneratorModel.getUid()).isNotNull();
        assertThat(integerKeyGeneratorModel.getUid()).isEqualTo(5);
    }

}
