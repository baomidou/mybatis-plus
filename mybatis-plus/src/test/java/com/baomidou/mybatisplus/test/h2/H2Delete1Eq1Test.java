package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.test.h2.entity.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2StudentMapper;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2Student;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2User;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

/**
 * Mybatis Plus H2 Junit Test
 *
 * @author hubin
 * @since 2018-06-05
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
class H2Delete1Eq1Test extends BaseTest {

    @Resource
    protected H2UserMapper logicDeleteMapper;
    @Resource
    protected H2StudentMapper defaultMapper;

    @Test
    @Order(1)
    void crudTest() {
        for (int i = 0; i < 10; i++) {
            logicDeleteMapper.insert(new H2User("mp" + i, AgeEnum.ONE));
        }
        log(logicDeleteMapper.selectList(new QueryWrapper<H2User>().orderByAsc("`desc`")));
        log(logicDeleteMapper.selectOne(new QueryWrapper<H2User>().last("limit 1")));

        H2User h2User = new H2User();
        h2User.setDesc("1");
        h2User.setName("2");
        log(logicDeleteMapper.selectList(new QueryWrapper<>(h2User).orderByAsc("name")));

        for (long i = 0; i < 10L; i++) {
            defaultMapper.insert(new H2Student(i, "Tom长大了", 1));
        }
        log(logicDeleteMapper.selectList(new QueryWrapper<>(h2User).eq("name","2").orderByAsc("name")));
        log(defaultMapper.selectList(new QueryWrapper<H2Student>().orderByAsc("id")));
        log(defaultMapper.selectOne(new QueryWrapper<H2Student>().last("limit 1")));

        H2Student h2Student = new H2Student();
        h2Student.setId(1L);
        h2Student.setAge(2);
        log(defaultMapper.selectList(new QueryWrapper<>(h2Student).orderByAsc("id")));
    }

    @Test
    @Order(Integer.MAX_VALUE)
    void delete() {
        logicDeleteMapper.delete(new QueryWrapper<>());
        defaultMapper.delete(new QueryWrapper<>());
    }
}
