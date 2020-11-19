package com.baomidou.mybatisplus.extension.plugins.inner;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-11-19
 */
class ReplaceSelectBodyInnerInterceptorTest {

    private final ReplaceSelectBodyInnerInterceptor innerInterceptor = new ReplaceSelectBodyInnerInterceptor();

    @Test
    void find() {
        List<String> list = innerInterceptor.find("select {@table},{@table:t},{@table:t:r} from table");
        assertThat(list).contains("{@table}", "{@table:t}", "{@table:t:r}");
    }
}
