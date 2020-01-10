/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.executor;

import com.baomidou.mybatisplus.core.metadata.PageList;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ParameterUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cache.TransactionalCacheManager;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * copy org.apache.ibatis.executor.CachingExecutor 主要修改了分页缓存逻辑
 *
 * @author nieqiuqiu
 */
public class MybatisCachingExecutor implements Executor {

    private final Executor delegate;
    private final TransactionalCacheManager tcm = new TransactionalCacheManager();

    public MybatisCachingExecutor(Executor delegate) {
        this.delegate = delegate;
        delegate.setExecutorWrapper(this);
    }

    @Override
    public Transaction getTransaction() {
        return delegate.getTransaction();
    }

    @Override
    public void close(boolean forceRollback) {
        try {
            //issues #499, #524 and #573
            if (forceRollback) {
                tcm.rollback();
            } else {
                tcm.commit();
            }
        } finally {
            delegate.close(forceRollback);
        }
    }

    @Override
    public boolean isClosed() {
        return delegate.isClosed();
    }

    @Override
    public int update(MappedStatement ms, Object parameterObject) throws SQLException {
        flushCacheIfRequired(ms);
        return delegate.update(ms, parameterObject);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        CacheKey key = createCacheKey(ms, parameterObject, rowBounds, boundSql);
        return query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
    }

    @Override
    public <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException {
        flushCacheIfRequired(ms);
        return delegate.queryCursor(ms, parameter, rowBounds);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> query(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql)
        throws SQLException {
        Cache cache = ms.getCache();
        Optional<IPage> pageOptional = ParameterUtils.findPage(parameterObject);
        if (cache != null) {
            flushCacheIfRequired(ms);
            if (ms.isUseCache() && resultHandler == null) {
                ensureNoOutParams(ms, boundSql);
                Object result = tcm.getObject(cache, key);
                if (result == null) {
                    if (pageOptional.isPresent()) {
                        IPage page = pageOptional.get();
                        CacheKey countCacheKey = null;
                        if (page.isSearchCount()) {
                            // 这里的执行sql为原select语句,标准一点的是需要将此转换为count语句当做缓存key的,留做当优化把.
                            countCacheKey = getCountCacheKey(ms, boundSql, parameterObject, RowBounds.DEFAULT);
                            // 复用count结果缓存,减少count查询.
                            Number count = (Number) tcm.getObject(cache, countCacheKey);
                            if (count != null) {
                                page.hitCount(true);
                                page.setTotal(count.longValue());
                            }
                        }
                        // 切勿将这提取至上方,如果先查的话,需要提前将boundSql拷贝一份
                        result = delegate.query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
                        List<E> records = (List<E>) result;
                        page.setRecords(records);
                        tcm.putObject(cache, key, records);
                        if (countCacheKey != null && !page.isHitCount()) {
                            tcm.putObject(cache, countCacheKey, page.getTotal());
                        }
                        return new PageList(records, page.getTotal());
                    } else {
                        result = delegate.query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
                        // issue #578 and #116
                        tcm.putObject(cache, key, result);
                        return (List<E>) result;
                    }
                } else {
                    if (pageOptional.isPresent()) {
                        IPage page = pageOptional.get();
                        if (page.isSearchCount()) {
                            CacheKey cacheKey = getCountCacheKey(ms, boundSql, parameterObject, RowBounds.DEFAULT);
                            Number count = (Number) tcm.getObject(cache, cacheKey);
                            if (count != null) {
                                page.hitCount(true);
                                return new PageList((List) result, count.longValue());
                            } else {
                                // 某些特殊情况,比如先不查count,缓存了list数据或者count缓存数据被淘汰(这几率比较小),就再查一次算了。
                                result = delegate.query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
                                List<E> records = (List<E>) result;
                                tcm.putObject(cache, cacheKey, page.getTotal());
                                return records;
                            }
                        }
                        return new PageList((List) result, 0L);
                    } else {
                        return (List<E>) result;
                    }
                }
            }
        }
        return delegate.query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
    }

    private MappedStatement buildCountMappedStatement(MappedStatement mappedStatement) {
        // 暂时补充点属性,留着后面分离count查询,这里暂时用到的也就只有id,所以不会影响后面的流程.
        return new MappedStatement.Builder(mappedStatement.getConfiguration(), mappedStatement.getId() + StringPool.DOT + "count", mappedStatement.getSqlSource(), SqlCommandType.SELECT)
            .useCache(true)
            .flushCacheRequired(false)
            .lang(mappedStatement.getLang())
            .resource(mappedStatement.getResource())
            .databaseId(mappedStatement.getDatabaseId())
            .cache(mappedStatement.getCache())
            .build();
    }

    private CacheKey getCountCacheKey(MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject, RowBounds rowBounds) {
        Configuration configuration = mappedStatement.getConfiguration();
//        BoundSql sourceSql = new BoundSql(mappedStatement.getConfiguration(), boundSql.getSql(), boundSql.getParameterMappings(), boundSql.getParameterObject());
        MappedStatement statement = buildCountMappedStatement(mappedStatement);
        CacheKey cacheKey = new CacheKey();
        cacheKey.update(statement.getId());
        cacheKey.update(rowBounds.getOffset());
        cacheKey.update(rowBounds.getLimit());
        cacheKey.update(boundSql.getSql());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        TypeHandlerRegistry typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        // mimic DefaultParameterHandler logic
        for (ParameterMapping parameterMapping : parameterMappings) {
            if (parameterMapping.getMode() != ParameterMode.OUT) {
                Object value;
                String propertyName = parameterMapping.getProperty();
                if (boundSql.hasAdditionalParameter(propertyName)) {
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (parameterObject == null) {
                    value = null;
                } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    value = parameterObject;
                } else {
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    value = metaObject.getValue(propertyName);
                }
                cacheKey.update(value);
            }
        }
        if (configuration.getEnvironment() != null) {
            // issue #176
            cacheKey.update(configuration.getEnvironment().getId());
        }
        return cacheKey;
    }

    @Override
    public List<BatchResult> flushStatements() throws SQLException {
        return delegate.flushStatements();
    }

    @Override
    public void commit(boolean required) throws SQLException {
        delegate.commit(required);
        tcm.commit();
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        try {
            delegate.rollback(required);
        } finally {
            if (required) {
                tcm.rollback();
            }
        }
    }

    private void ensureNoOutParams(MappedStatement ms, BoundSql boundSql) {
        if (ms.getStatementType() == StatementType.CALLABLE) {
            for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
                if (parameterMapping.getMode() != ParameterMode.IN) {
                    throw new ExecutorException("Caching stored procedures with OUT params is not supported.  Please configure useCache=false in " + ms.getId() + " statement.");
                }
            }
        }
    }

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
        return delegate.createCacheKey(ms, parameterObject, rowBounds, boundSql);
    }

    @Override
    public boolean isCached(MappedStatement ms, CacheKey key) {
        return delegate.isCached(ms, key);
    }

    @Override
    public void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType) {
        delegate.deferLoad(ms, resultObject, property, key, targetType);
    }

    @Override
    public void clearLocalCache() {
        delegate.clearLocalCache();
    }

    private void flushCacheIfRequired(MappedStatement ms) {
        Cache cache = ms.getCache();
        if (cache != null && ms.isFlushCacheRequired()) {
            tcm.clear(cache);
        }
    }

    @Override
    public void setExecutorWrapper(Executor executor) {
        throw new UnsupportedOperationException("This method should not be called");
    }

}
