/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.SneakyThrows;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionHolder;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * SQL 辅助类
 *
 * @author hubin
 * @since 2016-11-06
 */
public final class SqlHelper {

    /**
     * 主要用于 service 和 ar
     */
    public static SqlSessionFactory FACTORY;

    /**
     * 批量操作 SqlSession
     *
     * @param clazz 实体类
     * @return SqlSession
     * @deprecated 3.5.4
     */
    @Deprecated
    public static SqlSession sqlSessionBatch(Class<?> clazz) {
        // TODO 暂时让能用先,但日志会显示Closing non transactional SqlSession,因为这个并没有绑定.
        return sqlSessionFactory(clazz).openSession(ExecutorType.BATCH);
    }

    /**
     * 获取SqlSessionFactory
     *
     * @param clazz 实体类
     * @return SqlSessionFactory
     * @since 3.3.0
     * @deprecated 3.5.3.2 尽量少用,后期取消此方法获取实例
     */
    @Deprecated
    public static SqlSessionFactory sqlSessionFactory(Class<?> clazz) {
        return GlobalConfigUtils.currentSessionFactory(clazz);
    }

    /**
     * 获取Session
     *
     * @param clazz 实体类
     * @return SqlSession
     * @deprecated 3.5.3.2 尽量少用,后期取消打开session方法
     */
    @Deprecated
    public static SqlSession sqlSession(Class<?> clazz) {
        return SqlSessionUtils.getSqlSession(GlobalConfigUtils.currentSessionFactory(clazz));
    }

    /**
     * 获取TableInfo
     *
     * @param clazz 对象类
     * @return TableInfo 对象表信息
     */
    public static TableInfo table(Class<?> clazz) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        Assert.notNull(tableInfo, "Error: Cannot execute table Method, ClassGenericType not found.");
        return tableInfo;
    }

    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    public static boolean retBool(Integer result) {
        return null != result && result >= 1;
    }

    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    public static boolean retBool(Long result) {
        return null != result && result >= 1;
    }

    /**
     * 批量操作是否成功
     *
     * @param result 批量操作结果集
     * @return 操作结果(批量行记录全满足成功的的情况下为true)
     * @since 3.5.8
     */
    public static boolean retBool(List<BatchResult> result) {
        return result != null && result.stream().flatMapToInt(r -> IntStream.of(r.getUpdateCounts())).allMatch(i -> i > 0 || i == Statement.SUCCESS_NO_INFO);
    }

    /**
     * 返回SelectCount执行结果
     *
     * @param result ignore
     * @return int
     */
    public static long retCount(Long result) {
        return (null == result) ? 0 : result;
    }

    /**
     * 从list中取第一条数据返回对应List中泛型的单个结果
     *
     * @param list ignore
     * @param <E>  ignore
     * @return ignore
     */
    public static <E> E getObject(Log log, List<E> list) {
        return getObject(() -> log, list);
    }

    /**
     * @since 3.4.3
     */
    public static <E> E getObject(Supplier<Log> supplier, List<E> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            int size = list.size();
            if (size > 1) {
                Log log = supplier.get();
                log.warn(String.format("Warn: execute Method There are  %s results.", size));
            }
            return list.get(0);
        }
        return null;
    }

    /**
     * 执行批量操作
     *
     * @param entityClass 实体
     * @param log         日志对象
     * @param consumer    consumer
     * @return 操作结果
     * @since 3.4.0
     */
    @Deprecated
    public static boolean executeBatch(Class<?> entityClass, Log log, Consumer<SqlSession> consumer) {
        return executeBatch(sqlSessionFactory(entityClass), log, consumer);
    }

    @SneakyThrows
    public static boolean executeBatch(SqlSessionFactory sqlSessionFactory, Log log, Consumer<SqlSession> consumer) {
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
            consumer.accept(sqlSession);
            //非事务情况下，强制commit。
            sqlSession.commit(!transaction);
            return true;
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
            sqlSession.close();
        }
    }

    /**
     * 执行批量操作
     *
     * @param entityClass 实体类
     * @param log         日志对象
     * @param list        数据集合
     * @param batchSize   批次大小
     * @param consumer    consumer
     * @param <E>         T
     * @return 操作结果
     * @since 3.4.0
     * @deprecated {@link #executeBatch(SqlSessionFactory, Log, Collection, int, BiConsumer)}
     */
    @Deprecated
    public static <E> boolean executeBatch(Class<?> entityClass, Log log, Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        return executeBatch(sqlSessionFactory(entityClass), log, list, batchSize, consumer);
    }

    public static <E> boolean executeBatch(SqlSessionFactory sqlSessionFactory, Log log, Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        Assert.isFalse(batchSize < 1, "batchSize must not be less than one");
        return !CollectionUtils.isEmpty(list) && executeBatch(sqlSessionFactory, log, sqlSession -> {
            int size = list.size();
            int idxLimit = Math.min(batchSize, size);
            int i = 1;
            for (E element : list) {
                consumer.accept(sqlSession, element);
                if (i == idxLimit) {
                    sqlSession.flushStatements();
                    idxLimit = Math.min(idxLimit + batchSize, size);
                }
                i++;
            }
        });
    }

    /**
     * 批量更新或保存
     *
     * @param entityClass 实体
     * @param log         日志对象
     * @param list        数据集合
     * @param batchSize   批次大小
     * @param predicate   predicate(新增条件) notNull
     * @param consumer    consumer（更新处理） notNull
     * @param <E>         E
     * @return 操作结果
     * @since 3.4.0
     * @deprecated 3.5.4 {@link #saveOrUpdateBatch(SqlSessionFactory, Class, Log, Collection, int, BiPredicate, BiConsumer)}
     */
    @Deprecated
    public static <E> boolean saveOrUpdateBatch(Class<?> entityClass, Class<?> mapper, Log log, Collection<E> list, int batchSize, BiPredicate<SqlSession, E> predicate, BiConsumer<SqlSession, E> consumer) {
        return saveOrUpdateBatch(sqlSessionFactory(entityClass), mapper, log, list, batchSize, predicate, consumer);
    }

    /**
     * 批量更新或保存
     *
     * @param sqlSessionFactory SqlSessionFactory
     * @param log               日志对象
     * @param list              数据集合
     * @param batchSize         批次大小
     * @param predicate         predicate(新增条件) notNull
     * @param consumer          consumer（更新处理） notNull
     * @param <E>               E
     * @return 操作结果
     * @since 3.5.4
     */
    public static <E> boolean saveOrUpdateBatch(SqlSessionFactory sqlSessionFactory, Class<?> mapper, Log log, Collection<E> list, int batchSize, BiPredicate<SqlSession, E> predicate, BiConsumer<SqlSession, E> consumer) {
        String sqlStatement = getSqlStatement(mapper, SqlMethod.INSERT_ONE);
        return executeBatch(sqlSessionFactory, log, list, batchSize, (sqlSession, entity) -> {
            if (predicate.test(sqlSession, entity)) {
                sqlSession.insert(sqlStatement, entity);
            } else {
                consumer.accept(sqlSession, entity);
            }
        });
    }

    /**
     * 获取mapperStatementId
     *
     * @param sqlMethod 方法名
     * @return 命名id
     * @since 3.4.0
     */
    public static String getSqlStatement(Class<?> mapper, SqlMethod sqlMethod) {
        return mapper.getName() + StringPool.DOT + sqlMethod.getMethod();
    }

    /**
     * 通过entityClass获取Mapper，记得要释放连接
     * 例： {@code
     * SqlSession sqlSession = SqlHelper.sqlSession(entityClass);
     * try {
     *     BaseMapper<User> userMapper = getMapper(User.class, sqlSession);
     * } finally {
     *     sqlSession.close();
     * }
     * }
     *
     * @param entityClass 实体
     * @param <T>         实体类型
     * @param <M>         Mapper类型
     * @return Mapper
     */
    @SuppressWarnings("unchecked")
    public static <T,M extends BaseMapper<T>> M getMapper(Class<T> entityClass, SqlSession sqlSession) {
        Assert.notNull(entityClass, "entityClass can't be null!");
        TableInfo tableInfo = Optional.ofNullable(TableInfoHelper.getTableInfo(entityClass)).orElseThrow(() -> ExceptionUtils.mpe("Can not find TableInfo from Class: \"%s\".", entityClass.getName()));
        Class<?> mapperClass = ClassUtils.toClassConfident(tableInfo.getCurrentNamespace());
        return (M) tableInfo.getConfiguration().getMapper(mapperClass, sqlSession);
    }

    /**
     * 通过entityClass获取BaseMapper，再传入lambda使用该mapper，本方法自动释放链接
     *
     * @param entityClass 实体类
     * @param sFunction   lambda操作，例如 {@code m->m.selectList(wrapper)}
     * @param <T>         实体类的类型
     * @param <R>         返回值类型
     * @param <M>         Mapper类型
     * @return 返回lambda执行结果
     */
    public static <T, R,M extends BaseMapper<T>> R execute(Class<T> entityClass, SFunction<M, R> sFunction) {
        SqlSession sqlSession = SqlHelper.sqlSession(entityClass);
        try {
            return sFunction.apply(SqlHelper.getMapper(entityClass, sqlSession));
        } finally {
            SqlSessionUtils.closeSqlSession(sqlSession, GlobalConfigUtils.currentSessionFactory(entityClass));
        }
    }
}
