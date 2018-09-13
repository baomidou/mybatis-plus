package com.baomidou.mybatisplus.test.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author miemie
 * @since 2018-09-13
 */
public interface MyBaseMapper<T> extends BaseMapper<T> {

    int deleteByIdWithFill(T entity);
}
