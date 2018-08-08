package com.baomidou.mybatisplus.test;

import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlTest {

    @Test
    public void test() {
        Stream.of(1, 2, 3, 4, 5).collect(Collectors.toList()).toArray();
    }
}
