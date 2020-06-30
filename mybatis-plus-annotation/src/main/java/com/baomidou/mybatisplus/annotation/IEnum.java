package com.baomidou.mybatisplus.annotation;

import java.io.Serializable;

/**
 * 自定义枚举接口
 *
 * @author hubin
 * @since 2017-10-11
 */
public interface IEnum<T extends Serializable> {

    /**
     * 枚举数据库存储值
     */
    T getValue();
}
