package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;

/**
 * @author liujiawei
 * @date 2020/6/8
 */
public class TestIdentifierGenerator implements IdentifierGenerator {
    @Override
    public Number nextId(Object entity) {
        return 777L;
    }
}
