package com.baomidou.mybatisplus.test.base.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

/**
 * @author miemie
 * @since 2018-08-19
 */
@Getter
public enum TestEnum implements IEnum<Integer> {
    ONE(1, "一"),
    TWO(2, "二");

    private int code;
    private String val;

    TestEnum(int code, String val) {
        this.code = code;
        this.val = val;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
