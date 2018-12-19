package com.baomidou.mybatisplus.extension.service.additional;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author miemie
 * @since 2018-12-19
 */
public interface ChainWrapper<T> {

    BaseMapper<T> getBaseMapper();

    Wrapper<T> getWrapper();
}
