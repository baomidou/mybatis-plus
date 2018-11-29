package com.baomidou.mybatisplus.test.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author miemie
 * @since 2018-09-13
 */
public interface MyBaseMapper<T> extends BaseMapper<T> {

    int deleteByIdWithFill(T entity);

    int insertBatchSomeColumn(List<T> entityList);
}
