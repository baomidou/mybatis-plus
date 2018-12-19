package com.baomidou.mybatisplus.extension.service.additional.query;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @author miemie
 * @since 2018-12-19
 */
public interface Querys<T> {

    List<T> list();

    T one();

    Integer count();

    IPage<T> page(IPage<T> page);
}
