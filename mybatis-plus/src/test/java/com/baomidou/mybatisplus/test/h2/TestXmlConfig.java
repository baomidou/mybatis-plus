package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.service.IH2UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * 测试XML配置
 * @author nieqiurong 2018/8/14 13:30.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-xml-h2.xml"})
class TestXmlConfig {

    @Autowired
    protected IH2UserService userService;

    @Test
    void test() {
        H2User user = userService.getById(101L);
        Assertions.assertNotNull(user);
    }

}
