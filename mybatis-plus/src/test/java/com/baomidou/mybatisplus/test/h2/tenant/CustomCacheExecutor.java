package com.baomidou.mybatisplus.test.h2.tenant;

import com.baomidou.mybatisplus.core.executor.MybatisCachingExecutor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.RowBounds;

import java.util.Objects;


/**
 * @author nieqiuqiu 2020/6/15
 */
public class CustomCacheExecutor extends MybatisCachingExecutor {

    private TenantHandler tenantHandler;

    public CustomCacheExecutor(Executor delegate, TenantHandler tenantHandler) {
        super(delegate);
        this.tenantHandler = tenantHandler;
    }

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
        if (ms.getSqlCommandType() == SqlCommandType.SELECT) {
            CacheKey cacheKey = super.createCacheKey(ms, parameterObject, rowBounds, boundSql);
            cacheKey.update(Objects.toString(tenantHandler.getTenantId(true)));
            return cacheKey;
        }
        return super.createCacheKey(ms, parameterObject, rowBounds, boundSql);
    }

    @Override
    protected CacheKey getCountCacheKey(MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject, RowBounds rowBounds) {
        CacheKey countCacheKey = super.getCountCacheKey(mappedStatement, boundSql, parameterObject, rowBounds);
        countCacheKey.update(Objects.toString(tenantHandler.getTenantId(true)));
        return countCacheKey;
    }

}
