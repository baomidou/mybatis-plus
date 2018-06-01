package com.baomidou.mybatisplus.core.test;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.select.EntityWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import org.junit.Test;

public class WrapperTest {

    private void log(String message) {
        System.out.println(message);
    }

    @Test
    public void test() {
        Wrapper<User> wrapper = new EntityWrapper<User>().stream().eq(User::getName, 123)
            .or(c -> c.eq(User::getRoleId, 1).eq(User::getId, 2))
            .and().eq(User::getId, 1);
        log(wrapper.getSqlSegment());

    }

    @Test
    public void test1() {
        EntityWrapper<User> ew = new EntityWrapper<User>()
            .eq("xxx", 123)
            .and(i -> i.eq("andx", 65444).and().le("ande", 66666))
            .and().ne("xxx", 222);
        log(ew.getSqlSegment());
        ew.getParamNameValuePairs().forEach((k, v) -> System.out.println("key = " + k + " ; value = " + v));
    }

    @Test
    public void test2() {
        UpdateWrapper<User> ew = new UpdateWrapper<User>()
            .set("name", "三毛").set("id", 1)
            .eq("xxx", 123)
            .and(i -> i.eq("andx", 65444).and().le("ande", 66666))
            .and().ne("xxx", 222);
        log(ew.getSqlSet());
        log(ew.getSqlSegment());
    }

    @Test
    public void test3() {
        UpdateWrapper<User> ew = new UpdateWrapper<User>()
           .setSql("abc=1,def=2").eq("id", 1).and().ge("age", 3);
        log(ew.getSqlSet());
        log(ew.getSqlSegment());
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
