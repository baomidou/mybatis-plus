package com.baomidou.mybatisplus.extension.service.additional.query;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.additional.ChainWrapper;

import java.util.List;

/**
 * @author miemie
 * @since 2018-12-19
 */
public interface Querys<T> extends ChainWrapper<T> {

    default List<T> list() {
        return getBaseMapper().selectList(getWrapper());
    }

    default T one() {
        return getBaseMapper().selectOne(getWrapper());
    }

    default Integer count() {
        return getBaseMapper().selectCount(getWrapper());
    }

    default IPage<T> page(IPage<T> page) {
        return getBaseMapper().selectPage(page, getWrapper());
    }
}
