package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2021-01-27
 */
class BaseWrapperTest {

    void logParams(AbstractWrapper<?, ?, ?> wrapper) {
        wrapper.getParamNameValuePairs().forEach((k, v) -> {
            System.out.println("key: '" + k + "'\t\tvalue: '" + v + StringPool.SINGLE_QUOTE);
            assertThat(k).startsWith(Constants.WRAPPER_PARAM);
        });
    }
}
