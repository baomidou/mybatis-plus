package com.baomidou.mybatisplus.test.phoenix;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author: fly
 * Created date: 2019/12/21 16:35
 */
public interface PhoenixBaseMapper<T> extends BaseMapper<T> {

    int upsert(T entity);

    @Override
    default int insert(T entity) {
        return upsert(entity);
    }

}
