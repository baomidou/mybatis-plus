package com.baomidou.mybatisplus.test.h2;

import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
public class BaseTest {

    protected static final String NQQ = "聂秋秋";

    protected void log(Object object) {
        System.out.println(object);
    }

}
