package com.baomidou.mybatisplus.test.h2.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义父类 SuperMapper
 *
 * @author hubin
 * @since 2017-06-26
 */
public interface SuperMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {

    /**
     * 这里注入自定义的公共方法
     */

    int alwaysUpdateSomeColumnById(@Param("et") T entity);

    int deleteByIdWithFill(T entity);

    int insertBatchSomeColumn(List<T> entityList);
}
