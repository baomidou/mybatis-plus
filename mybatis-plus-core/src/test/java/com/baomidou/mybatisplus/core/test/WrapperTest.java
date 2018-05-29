package com.baomidou.mybatisplus.core.test;

import org.junit.Test;

import com.baomidou.mybatisplus.core.conditions.EntityWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;

public class WrapperTest {

    private void log(String message) {
        System.out.println(message);
    }

    @Test
    public void test() {
        Wrapper wrapper = new EntityWrapper<User>().stream().eq(User::getName, 123)
            .and().eq(User::getId, 1);
        log(wrapper.getSqlSegment());

        wrapper = new EntityWrapper<User>().eq("name", 123)
            .and().eq("id", 1);
        log(wrapper.getSqlSegment());
    }

//    public void test() {
//        String sql = new QueryWrapper()
//            .where("b.age > 18", condition ->
//                condition.and("b.type = 'rabid'")
//                    .or(nested -> nested.apply("name='12'").and("age=1"))
//                    .notIn("ads,2112,212")
//                    .last("LIMIT 1")
//            ).sqlSegment();
//
//        log(sql);
//        assertEquals("WHERE b.age > 18 AND b.type = 'rabid' OR ( name='12' AND age=1 ) NOT IN ( ads,2112,212 ) LIMIT 1", sql);
//    }

}
