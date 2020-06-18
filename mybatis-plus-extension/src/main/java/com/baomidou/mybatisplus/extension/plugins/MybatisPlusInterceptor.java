package com.baomidou.mybatisplus.extension.plugins;

import com.baomidou.mybatisplus.extension.plugins.chain.BeforeQuery;
import com.baomidou.mybatisplus.extension.plugins.chain.PageBeforeQuery;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author miemie
 * @since 2020-06-16
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Intercepts(
    {
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
    }
)
public class MybatisPlusInterceptor implements Interceptor {

    private final List<BeforeQuery> beforeQueries = Collections.singletonList(new PageBeforeQuery());

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
        Executor executor = (Executor) invocation.getTarget();
        BoundSql boundSql;
        if (args.length == 4) {
            boundSql = ms.getBoundSql(parameter);
        } else {
            // 几乎不可能走进这里面
            boundSql = (BoundSql) args[5];
        }
        for (BeforeQuery query : beforeQueries) {
            if (!query.canChange(executor, ms, parameter, rowBounds, resultHandler, boundSql)) {
                return Collections.emptyList();
            }
            boundSql = query.change(executor, ms, parameter, rowBounds, resultHandler, boundSql);
        }
        CacheKey cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
        return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
