package com.baomidou.mybatisplus.core.enums;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import lombok.AllArgsConstructor;

/**
 * @Author Anson <br>
 * Created on 2020/11/18 16:09<br>
 * 名称：OrderByInjectKeyword.java <br>
 * 描述：防止 ORDER BY 字段注入，特殊字符枚举<br>
 */
@AllArgsConstructor
public enum OrderByInjectKeyword implements ISqlSegment {
    /**
     * 等号
     */
    EQUALS("="),
    /**
     * 空格
     */
    EMPTY(" "),
    /**
     * 左括号
     */
    LEFT_BRACKET("("),
    /**
     * 右括号
     */
    RIGHT_BRACKET(")"),
    /**
     * 分号
     */
    SEMICOLON(";");

    private final String keyword;

    @Override
    public String getSqlSegment() {
        return keyword;
    }
}
