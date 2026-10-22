/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.plugins;

import com.baomidou.mybatisplus.core.handlers.SelectOneResultHandler;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * Keeps the empty L1 result of the internal selectOne handler separate from ordinary list results.
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})})
public final class SelectOneCacheInterceptor implements Interceptor {

    private static final Object CACHE_KEY_MARKER = new Object();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        if (!(args[3] instanceof SelectOneResultHandler)) {
            return invocation.proceed();
        }
        Executor executor = (Executor) invocation.getTarget();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        BoundSql boundSql = args.length == 6 ? (BoundSql) args[5] : ms.getBoundSql(parameter);
        CacheKey cacheKey = args.length == 6
                ? ((CacheKey) args[4]).clone()
                : executor.createCacheKey(ms, parameter, rowBounds, boundSql);
        cacheKey.update(CACHE_KEY_MARKER);
        return executor.query(ms, parameter, rowBounds, (ResultHandler<?>) args[3], cacheKey, boundSql);
    }
}
