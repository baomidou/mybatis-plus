package com.baomidou.mybatisplus.extension.service.additional.update;

import com.baomidou.mybatisplus.extension.service.additional.ChainWrapper;

/**
 * @author miemie
 * @since 2018-12-19
 */
public interface ChainUpdate<T> extends ChainWrapper<T> {

    default int update(T entity) {
        return getBaseMapper().update(entity, getWrapper());
    }
}
