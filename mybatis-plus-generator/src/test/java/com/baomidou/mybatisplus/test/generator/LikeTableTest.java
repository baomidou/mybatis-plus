package com.baomidou.mybatisplus.test.generator;

import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.baomidou.mybatisplus.generator.config.po.LikeTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LikeTableTest {

    @Test
    void test() {
        Assertions.assertEquals("%test%", new LikeTable("test").toString());
        Assertions.assertEquals("%test%", new LikeTable("test").getValue());
        Assertions.assertEquals("%test", new LikeTable("test", SqlLike.LEFT).toString());
        Assertions.assertEquals("%test", new LikeTable("test", SqlLike.LEFT).getValue());
        Assertions.assertEquals("test%", new LikeTable("test", SqlLike.RIGHT).toString());
        Assertions.assertEquals("test%", new LikeTable("test", SqlLike.RIGHT).getValue());
    }

}
