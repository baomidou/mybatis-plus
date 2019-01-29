package com.baomidou.mybatisplus.core.toolkit.condition;

import lombok.Getter;
import org.junit.jupiter.api.Test;

class WrapperTest {

    @Test
    void name() {
        Wrapper<User> wrapper = new WrapperImpl<>();
        wrapper.eq("id", 123);
        System.out.println(wrapper.getSqlSeq());
    }

    @Getter
    private class User {

        private int id;
    }
}
