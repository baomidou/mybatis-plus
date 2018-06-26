package com.baomidou.mybatisplus.core.test;

import org.junit.Test;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

public class WrapperTest {

    private void log(String message) {
        System.out.println(message);
    }

    private void logSqlSegment(String explain, ISqlSegment sqlSegment) {
        System.out.println(String.format(" ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓   ->(%s)<-   ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓", explain));
        System.out.println(sqlSegment.getSqlSegment());
    }

    @Test
    public void test() {
        Wrapper<User> wrapper = new QueryWrapper<User>().lambda().eq(User::getName, 123)
            .or(c -> c.eq(User::getRoleId, 1).eq(User::getId, 2))
            .and().eq(User::getId, 1);
        log(wrapper.getSqlSegment());

    }

    @Test
    public void test1() {
        QueryWrapper<User> ew = new QueryWrapper<User>()
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

    @Test
    public void testQueryWrapper() {
        logSqlSegment("去除第一个 and,以及自动拼接 and,以及手动拼接 and", new QueryWrapper<User>().and()
            .ge("age", 3).and().ge("age", 3).ge("age", 3));

        logSqlSegment("去除第一个 or,以及自动拼接 and,以及手动拼接 or", new QueryWrapper<User>().or()
            .ge("age", 3).or().ge("age", 3).ge("age", 3));

        logSqlSegment("多个 and 相连接,去除多余的 and", new QueryWrapper<User>()
            .ge("age", 3).and().and().and().ge("age", 3).and().and().ge("age", 3));

        logSqlSegment("多个 or 相连接,去除多余的 or", new QueryWrapper<User>()
            .ge("age", 3).or().or().or().ge("age", 3).or().or().ge("age", 3));

        logSqlSegment("多个 and或者 or 相连接,取最后一个", new QueryWrapper<User>()
            .ge("age", 3).and().or().and().ge("age", 3).and().or().ge("age", 3));

        logSqlSegment("嵌套测试,套内外自动拼接 and,去除套内第一个 and", new QueryWrapper<User>()
            .eq("id", 11).and(i -> i.and().eq("id", 1)).eq("id", 1));

        logSqlSegment("嵌套测试,套内外手动拼接 or,去除套内第一个 or", new QueryWrapper<User>()
            .eq("id", 11).or(i -> i.or().eq("id", 1)).or().eq("id", 1));
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
