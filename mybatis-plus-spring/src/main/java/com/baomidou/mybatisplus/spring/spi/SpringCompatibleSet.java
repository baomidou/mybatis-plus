/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.spring.spi;

import com.baomidou.mybatisplus.core.spi.CompatibleSet;
import com.baomidou.mybatisplus.core.toolkit.AopUtils;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.MybatisUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.SneakyThrows;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionHolder;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.InputStream;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Function;

/**
 * spring 兼容方法集接口实现类
 */
public class SpringCompatibleSet implements CompatibleSet {

    private static final Log LOG = LogFactory.getLog(SpringCompatibleSet.class);

    public static volatile ApplicationContext applicationContext;

    @Override
    public SqlSession getSqlSession(SqlSessionFactory sessionFactory) {
        return SqlSessionUtils.getSqlSession(sessionFactory);
    }

    @Override
    public void closeSqlSession(SqlSession sqlSession, SqlSessionFactory sqlSessionFactory) {
        SqlSessionUtils.closeSqlSession(sqlSession, sqlSessionFactory);
    }

    @SneakyThrows
    @Override
    public boolean executeBatch(SqlSessionFactory sqlSessionFactory, Log log, Function<SqlSession, Integer> execFunc) {
        SqlSessionHolder sqlSessionHolder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(sqlSessionFactory);
        boolean transaction = TransactionSynchronizationManager.isSynchronizationActive();
        if (sqlSessionHolder != null) {
            SqlSession sqlSession = sqlSessionHolder.getSqlSession();
            //原生无法支持执行器切换，当存在批量操作时，会嵌套两个session的，优先commit上一个session
            //按道理来说，这里的值应该一直为false。
            sqlSession.commit(!transaction);
        }
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        if (!transaction) {
            log.warn("SqlSession [" + sqlSession + "] Transaction not enabled");
        }
        try {
            Integer resultRow = execFunc.apply(sqlSession);
            //非事务情况下，强制commit。
            sqlSession.commit(!transaction);
            return SqlHelper.retBool(resultRow);
        } catch (Throwable t) {
            sqlSession.rollback();
            Throwable unwrapped = ExceptionUtil.unwrapThrowable(t);
            if (unwrapped instanceof PersistenceException) {
                MyBatisExceptionTranslator myBatisExceptionTranslator
                    = new MyBatisExceptionTranslator(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), true);
                Throwable throwable = myBatisExceptionTranslator.translateExceptionIfPossible((PersistenceException) unwrapped);
                if (throwable != null) {
                    throw throwable;
                }
            }
            throw ExceptionUtils.mpe(unwrapped);
        } finally {
            if (!transaction) {
                sqlSession.close();
            }
        }
    }

    @Override
    public void clearMapperCache(SqlSession sqlSession, String statementId) {
        if (sqlSession instanceof SqlSessionTemplate) {
            SqlSessionFactory sqlSessionFactory = MybatisUtils.getSqlSessionFactory(sqlSession);
            clearCacheOnRollback(sqlSessionFactory, statementId);
            SqlSessionHolder holder =
                (SqlSessionHolder) TransactionSynchronizationManager.getResource(sqlSessionFactory);
            if (holder != null) {
                MybatisUtils.clearMapperCache(holder.getSqlSession(), statementId);
            }
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            clearCacheOnRollback(MybatisUtils.getSqlSessionFactory(sqlSession), statementId);
        }
        MybatisUtils.clearMapperCache(sqlSession, statementId);
    }

    private void clearCacheOnRollback(SqlSessionFactory sqlSessionFactory, String statementId) {
        Cache cache = sqlSessionFactory.getConfiguration().getMappedStatement(statementId).getCache();
        if (cache == null || !TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
            if (synchronization instanceof CacheCleanupSynchronization) {
                ((CacheCleanupSynchronization) synchronization).add(cache);
                return;
            }
        }
        // MyBatis-Spring 可能在 JDBC 回滚前关闭会话并发布二级缓存，因此回滚完成后需再次清理。
        TransactionSynchronizationManager.registerSynchronization(new CacheCleanupSynchronization(cache));
    }

    private static class CacheCleanupSynchronization implements TransactionSynchronization {

        private final Set<Cache> caches = Collections.newSetFromMap(new IdentityHashMap<>());

        private CacheCleanupSynchronization(Cache cache) {
            add(cache);
        }

        private void add(Cache cache) {
            caches.add(cache);
        }

        @Override
        public void afterCompletion(int status) {
            if (status != TransactionSynchronization.STATUS_COMMITTED) {
                caches.forEach(Cache::clear);
            }
        }
    }

    @Override
    public InputStream getInputStream(String path) throws Exception {
        return new ClassPathResource(path).getInputStream();
    }

    @Override
    public <T> T getBean(Class<T> clz) {
        if (applicationContext != null) {
            ObjectProvider<T> provider = applicationContext.getBeanProvider(clz);
            return provider.getIfAvailable();
        }
        LOG.warn("The applicationContext property is empty. Please initialize it via the static field of applicationContext in SpringContextHolder or by calling the setApplicationContext method of MybatisSqlSessionFactoryBean.");
        return null;
    }

    @Override
    public Object getProxyTargetObject(Object mapper) {
        Object result = mapper;
        if (AopUtils.isLoadSpringAop()) {
            while (org.springframework.aop.support.AopUtils.isAopProxy(result)) {
                result = AopProxyUtils.getSingletonTarget(result);
            }
        }
        return result;
    }

    @Override
    public void setContext(Object context) {
        applicationContext = (ApplicationContext) context;
    }

}
