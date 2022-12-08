package com.baomidou.mybatisplus.generator.config.converts.select;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author hanchunlin
 * Created at 2020/6/11 19:43
 */
class SelectorTest {

    @Test
    void test() {
        assertEquals("1", asString(1));
        assertEquals("123", asString(4));
    }

    /**
     * 将数字转换为字符串
     *
     * @param i 数字
     * @return 返回对应的字符串
     */
    private String asString(int i) {
        Selector<Integer, String> selector = Selector.param(i);
        return selector.test(BranchBuilder.<Integer, String>of(ii -> ii == 1).then(p -> "1"))
            .test(BranchBuilder.<Integer, String>of(ii -> ii == 2).then(p -> "2"))
            .test(BranchBuilder.<Integer, String>of(ii -> ii == 3).then(p -> "3"))
            .or("123");
    }

}
