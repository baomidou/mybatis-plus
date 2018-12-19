package com.baomidou.mybatisplus.extension.service.additional.query.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.additional.AbstractChainWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.Querys;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author miemie
 * @since 2018-12-19
 */
@SuppressWarnings("serial")
public class LambdaQueryChainWrapper<T> extends AbstractChainWrapper<T, SFunction<T, ?>, LambdaQueryChainWrapper<T>, LambdaQueryWrapper<T>>
    implements Querys<T>, Query<LambdaQueryChainWrapper<T>, T, SFunction<T, ?>> {

    public LambdaQueryChainWrapper(BaseMapper<T> baseMapper) {
        super(baseMapper);
        wrapperChildren = new LambdaQueryWrapper<>();
    }

    @SafeVarargs
    @Override
    public final LambdaQueryChainWrapper<T> select(SFunction<T, ?>... columns) {
        wrapperChildren.select(columns);
        return typedThis;
    }

    @Override
    public LambdaQueryChainWrapper<T> select(Predicate<TableFieldInfo> predicate) {
        wrapperChildren.select(predicate);
        return typedThis;
    }

    @Override
    public LambdaQueryChainWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        wrapperChildren.select(entityClass, predicate);
        return typedThis;
    }

    @Override
    public String getSqlSelect() {
        return wrapperChildren.getSqlSelect();
    }

    @Override
    public List<T> list() {
        return baseMapper.selectList(wrapperChildren);
    }

    @Override
    public T one() {
        return baseMapper.selectOne(wrapperChildren);
    }

    @Override
    public Integer count() {
        return baseMapper.selectCount(wrapperChildren);
    }

    @Override
    public IPage<T> page(IPage<T> page) {
        return baseMapper.selectPage(page, wrapperChildren);
    }
}
