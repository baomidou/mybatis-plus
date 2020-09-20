package com.baomidou.mybatisplus.test.h2.issues.genericid;

import com.baomidou.mybatisplus.test.h2.issues.genericid.entity.LongEntity;
import com.baomidou.mybatisplus.test.h2.issues.genericid.entity.StringEntity;
import com.baomidou.mybatisplus.test.h2.issues.genericid.mapper.LongEntityMapper;
import com.baomidou.mybatisplus.test.h2.issues.genericid.mapper.StringEntityMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * 泛型主键问题
 * https://gitee.com/baomidou/mybatis-plus/issues/I171CQ
 *
 * @author nieqiuqiu
 * @date 2019-12-20
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:issues/genericid/spring.xml"})
class GenericIdTest {

    @Autowired
    private LongEntityMapper longEntityMapper;

    @Autowired
    private StringEntityMapper stringEntityMapper;

    @Test
    void test() {
        longEntityMapper.insert(new LongEntity("testLong"));
        stringEntityMapper.insert(new StringEntity("testString"));
    }

}
