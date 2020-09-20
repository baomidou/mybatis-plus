package com.baomidou.mybatisplus.test.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author miemie
 * @since 2020-06-23
 */
@Getter
@AllArgsConstructor
public enum EnumStr {
    ONE("1"),
    TWO("2");

    @EnumValue
    private final String value;
}
