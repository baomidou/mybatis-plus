package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.Data;

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

    @Data
    protected static class Entity {
        private Integer id;

        @TableField("username")
        private String name;

        private Integer roleId;
    }
}
