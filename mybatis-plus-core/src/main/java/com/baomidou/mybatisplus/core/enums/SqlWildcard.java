package com.baomidou.mybatisplus.core.enums;

import com.baomidou.mybatisplus.core.toolkit.StringPool;

/**
 * 通配符
 */
public enum SqlWildcard {
    /**
     * 百分号
     */
    PERCENT(StringPool.PERCENT),
    /**
     * 下划线
     */
    UNDERSCORE(StringPool.UNDERSCORE);

    private final String wildcard;

    SqlWildcard(String wildcard) {
        this.wildcard = wildcard;
    }

    public String getWildcard() {
        return wildcard;
    }
}
