package com.baomidou.mybatisplus.test.h2.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * <p></p>
 *
 * @author yuxiaobin
 * @date 2018/8/30
 */
public enum GradeEnum {

    PRIMARY(1, "小学"),
    SECONDARY(2, "中学"),
    HIGH(3, "高中");

    GradeEnum(int code, String descp) {
        this.code = code;
        this.descp = descp;
    }

    @EnumValue
    private final int code;

    private final String descp;

    public int getCode() {
        return code;
    }

    public String getDescp() {
        return descp;
    }
}
