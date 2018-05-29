package com.baomidou.mybatisplus.core.toolkit.condition;

import org.junit.Test;

import lombok.Getter;

public class WrapperTest {

    @Test
    public void name() {
        Wrapper<User> wrapper = new WrapperImpl<>();
        wrapper.eq("id", 123);
        System.out.println(wrapper.getSqlSeq());
    }

    @Getter
    private class User {

        private int id;
    }
}
