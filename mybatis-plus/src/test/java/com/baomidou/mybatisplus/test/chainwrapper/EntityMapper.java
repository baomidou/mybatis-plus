package com.baomidou.mybatisplus.test.chainwrapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;

/**
 * @author miemie
 * @since 2020-06-23
 */
public interface EntityMapper extends BaseMapper<Entity> {

    default QueryChainWrapper<Entity> queryChain() {
        return ChainWrappers.queryChain(this);
    }
}
