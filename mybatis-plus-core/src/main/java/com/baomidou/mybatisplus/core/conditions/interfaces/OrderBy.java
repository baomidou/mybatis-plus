package com.baomidou.mybatisplus.core.conditions.interfaces;

import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 排序条件封装
 * </p>
 *
 * @author Cat73
 * @since 2018-12-26
 */
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class OrderBy<This, R> implements Serializable {
    private final This self;
    private final R column;

    /**
     * 升序排序
     */
    public This asc() {
        ((Func) self).orderByAsc(column);
        return this.self;
    }

    /**
     * 降序排序
     */
    public This desc() {
        ((Func) self).orderByDesc(column);
        return this.self;
    }
}
