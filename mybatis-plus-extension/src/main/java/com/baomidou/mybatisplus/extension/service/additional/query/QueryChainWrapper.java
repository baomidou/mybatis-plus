package com.baomidou.mybatisplus.extension.service.additional.query;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.extension.service.additional.ChainWrapper;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author miemie
 * @since 2018-12-19
 */
@SuppressWarnings("serial")
public class QueryChainWrapper<T> extends ChainWrapper<T, String, QueryChainWrapper<T>>
    implements Querys<T>, Query<QueryChainWrapper<T>, T, String> {

    private QueryWrapper<T> queryWrapper;
    private BaseMapper<T> baseMapper;

    public QueryChainWrapper(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
        queryWrapper = new QueryWrapper<>();
    }

    @Override
    protected AbstractWrapper getWrapper() {
        return queryWrapper;
    }

    @Override
    public List<T> list() {
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public T one() {
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public Integer count() {
        return baseMapper.selectCount(queryWrapper);
    }

    @Override
    public IPage<T> page(IPage<T> page) {
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public QueryChainWrapper<T> select(String... columns) {
        queryWrapper.select(columns);
        return typedThis;
    }

    @Override
    public QueryChainWrapper<T> select(Predicate<TableFieldInfo> predicate) {
        queryWrapper.select(predicate);
        return typedThis;
    }

    @Override
    public QueryChainWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        queryWrapper.select(entityClass, predicate);
        return typedThis;
    }

    @Override
    public String getSqlSelect() {
        return queryWrapper.getSqlSelect();
    }
}
