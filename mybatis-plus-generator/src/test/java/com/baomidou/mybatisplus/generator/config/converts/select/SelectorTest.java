package com.baomidou.mybatisplus.generator.config.converts.select;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hanchunlin <br>
 *         Created at 2020/6/11 19:43
 */
@Tag("unit")
class SelectorTest {

    @Test
    void test() {
        assertThat(asString(1)).isEqualTo("1");
        assertThat(asString(4)).isEqualTo("123");
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
                .test(BranchBuilder.<Integer, String>of(ii -> ii == 3).then(p -> "3")).or("123");
    }

}
