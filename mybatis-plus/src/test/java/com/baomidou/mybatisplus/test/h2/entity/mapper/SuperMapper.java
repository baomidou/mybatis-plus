package com.baomidou.mybatisplus.test.h2.entity.mapper;

/**
 * 自定义父类 SuperMapper
 *
 * @author hubin
 * @since 2017-06-26
 */
public interface SuperMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {

    // 这里可以写 mapper 层公共方法、 注意！！ 多泛型的时候请将泛型T放在第一位.
}
