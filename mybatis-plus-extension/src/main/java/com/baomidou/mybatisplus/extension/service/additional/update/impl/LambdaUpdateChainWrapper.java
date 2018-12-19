package com.baomidou.mybatisplus.extension.service.additional.update.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.additional.AbstractChainWrapper;
import com.baomidou.mybatisplus.extension.service.additional.update.Updates;

/**
 * @author miemie
 * @since 2018-12-19
 */
@SuppressWarnings("serial")
public class LambdaUpdateChainWrapper<T> extends AbstractChainWrapper<T, SFunction<T, ?>, LambdaUpdateChainWrapper<T>, LambdaUpdateWrapper<T>>
    implements Updates<T>, Update<LambdaUpdateChainWrapper<T>, SFunction<T, ?>> {

    public LambdaUpdateChainWrapper(BaseMapper<T> baseMapper) {
        super(baseMapper);
        wrapperChildren = new LambdaUpdateWrapper<>();
    }

    @Override
    public LambdaUpdateChainWrapper<T> set(boolean condition, SFunction<T, ?> column, Object val) {
        wrapperChildren.set(condition, column, val);
        return typedThis;
    }

    @Override
    public LambdaUpdateChainWrapper<T> setSql(boolean condition, String sql) {
        wrapperChildren.setSql(condition, sql);
        return typedThis;
    }

    @Override
    public String getSqlSet() {
        return wrapperChildren.getSqlSet();
    }

    @Override
    public int update(T entity) {
        return baseMapper.update(entity, wrapperChildren);
    }
}
