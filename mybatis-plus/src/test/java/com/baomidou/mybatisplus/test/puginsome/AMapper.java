package com.baomidou.mybatisplus.test.puginsome;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author miemie
 * @since 2022-05-11
 */
public interface AMapper extends BaseMapper<A> {

    List<A> list();
}
