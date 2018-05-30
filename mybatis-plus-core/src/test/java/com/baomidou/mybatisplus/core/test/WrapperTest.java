package com.baomidou.mybatisplus.core.test;

import com.baomidou.mybatisplus.core.conditions.where.StrEntityWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.junit.Test;

public class WrapperTest {

    private void log(String message) {
        System.out.println(message);
    }

    @Test
    public void test() {
        Wrapper wrapper = new StrEntityWrapper<User>().stream().eq(User::getName, 123)
            .and().eq(User::getId, 1);
        log(wrapper.getSqlSegment());

        wrapper = new StrEntityWrapper<User>().eq("name", 123)
            .and().eq("id", 1);
        log(wrapper.getSqlSegment());
    }

    @Test
    public void test1() {
        StrEntityWrapper<User> ew = new StrEntityWrapper<User>()
            .eq("xxx", 123)
            .and(i -> i.eq("andx", 65444).and().le("ande", 66666))
            .and().ne("xxx", 222);
        log(ew.getSqlSegment());
        ew.getParamNameValuePairs().forEach((k, v) -> System.out.println("key = " + k + " ; value = " + v));
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
