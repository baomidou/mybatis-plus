package com.baomidou.mybatisplus.test;

import org.junit.Test;

public class SqlTest {

    @Test
    public void test(){
        System.out.println(String.format("%s LIKE CONCAT('%%',#{%s},'%%')", "column", "value"));
    }
}
