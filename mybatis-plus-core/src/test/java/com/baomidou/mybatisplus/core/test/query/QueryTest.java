package com.baomidou.mybatisplus.core.test.query;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class QueryTest {

    private void log(String message) {
        System.out.println(message);
    }

    @Test
    public void test() {
        String sql = new Query()
            .select("b.*")
            .where("b.age > 18", condition ->
                condition.and("b.type = 'rabid'")
                    .or(nested -> nested.apply("name='12'").and("age=1"))
                    .not().in("ads,2112,212")
                    .last("LIMIT 1")
            ).sqlSegment();

        log(sql);
        assertEquals("SELECT b.* WHERE b.age > 18 AND b.type = 'rabid' OR ( name='12' AND age=1 ) NOT IN ( ads,2112,212 ) LIMIT 1", sql);
    }

}
