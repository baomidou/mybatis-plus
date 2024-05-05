package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.entity.H2UserLogicDelete;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.mapper.H2UserLogicDeleteMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

/**
 * Mybatis Plus H2 Junit Test
 *
 * @author hubin
 * @since 2018-06-05
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-logic-delete-h2.xml"})
class H2LogicDeleteTest extends BaseTest {

    @Autowired
    protected H2UserLogicDeleteMapper logicDeleteMapper;

    @Test
    @Order(1)
    void crudTest() {
        String name = "LogicDelete4Date";
        H2UserLogicDelete user = new H2UserLogicDelete();
        user.setAge(AgeEnum.ONE);
        user.setName(name);
        logicDeleteMapper.insert(user);
        Assertions.assertNotNull(user.getTestId(), "id should not be null");
        Assertions.assertNull(user.getLastUpdatedDt());

        logicDeleteMapper.deleteById(user.getTestId());

        List<H2User> userList = queryByName(name);
        Assertions.assertTrue(userList!=null && !userList.isEmpty());
        Assertions.assertNotNull(userList.get(0).getLastUpdatedDt(),"lastUpdateDt should not be null after logic delete");

    }

}
