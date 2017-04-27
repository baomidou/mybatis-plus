/**
 * Copyright 2010-2016 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus;

import static java.lang.reflect.Proxy.newProxyInstance;
import static org.springframework.util.Assert.notNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;

/**
 * Copy SqlSessionTemplate
 *
 * @see org.mybatis.spring.SqlSessionTemplate
 */
public class MybatisSqlSessionTemplate implements SqlSession, DisposableBean {

    private final SqlSessionFactory sqlSessionFactory;

    private final ExecutorType executorType;

    private final SqlSession sqlSessionProxy;

    private final PersistenceExceptionTranslator exceptionTranslator;

    /**
     * Constructs a Spring managed SqlSession with the {@code SqlSessionFactory}
     * provided as an argument.
     *
     * @param sqlSessionFactory
     */
    public MybatisSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        this(sqlSessionFactory, sqlSessionFactory.getConfiguration().getDefaultExecutorType());
    }

    /**
     * Constructs a Spring managed SqlSession with the {@code SqlSessionFactory}
     * provided as an argument and the given {@code ExecutorType}
     * {@code ExecutorType} cannot be changed once the
     * {@code SqlSessionTemplate} is constructed.
     *
     * @param sqlSessionFactory
     * @param executorType
     */
    public MybatisSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
        this(sqlSessionFactory, executorType, new MyBatisExceptionTranslator(sqlSessionFactory.getConfiguration()
                .getEnvironment().getDataSource(), true));
    }

    /**
     * Constructs a Spring managed {@code SqlSession} with the given
     * {@code SqlSessionFactory} and {@code ExecutorType}. A custom
     * {@code SQLExceptionTranslator} can be provided as an argument so any
     * {@code PersistenceException} thrown by MyBatis can be custom translated
     * to a {@code RuntimeException} The {@code SQLExceptionTranslator} can also
     * be null and thus no exception translation will be done and MyBatis
     * exceptions will be thrown
     *
     * @param sqlSessionFactory
     * @param executorType
     * @param exceptionTranslator
     */
    public MybatisSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType,
                                     PersistenceExceptionTranslator exceptionTranslator) {

        notNull(sqlSessionFactory, "Property 'sqlSessionFactory' is required");
        notNull(executorType, "Property 'executorType' is required");

        this.sqlSessionFactory = sqlSessionFactory;
        this.executorType = executorType;
        this.exceptionTranslator = exceptionTranslator;
        this.sqlSessionProxy = (SqlSession) newProxyInstance(SqlSessionFactory.class.getClassLoader(),
                new Class[]{SqlSession.class}, new SqlSessionInterceptor());
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return this.sqlSessionFactory;
    }

    public ExecutorType getExecutorType() {
        return this.executorType;
    }

    public PersistenceExceptionTranslator getPersistenceExceptionTranslator() {
        return this.exceptionTranslator;
    }

    /**
     * {@inheritDoc}
     */

    public <T> T selectOne(String statement) {
        return this.sqlSessionProxy.<T>selectOne(statement);
    }

    /**
     * {@inheritDoc}
     */

    public <T> T selectOne(String statement, Object parameter) {
        return this.sqlSessionProxy.<T>selectOne(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */

    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
        return this.sqlSessionProxy.<K, V>selectMap(statement, mapKey);
    }

    /**
     * {@inheritDoc}
     */

    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
        return this.sqlSessionProxy.<K, V>selectMap(statement, parameter, mapKey);
    }

    /**
     * {@inheritDoc}
     */

    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
        return this.sqlSessionProxy.<K, V>selectMap(statement, parameter, mapKey, rowBounds);
    }

    /**
     * {@inheritDoc}
     */

    public <T> Cursor<T> selectCursor(String statement) {
        return this.sqlSessionProxy.selectCursor(statement);
    }

    /**
     * {@inheritDoc}
     */

    public <T> Cursor<T> selectCursor(String statement, Object parameter) {
        return this.sqlSessionProxy.selectCursor(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */

    public <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds) {
        return this.sqlSessionProxy.selectCursor(statement, parameter, rowBounds);
    }

    /**
     * {@inheritDoc}
     */

    public <E> List<E> selectList(String statement) {
        return this.sqlSessionProxy.<E>selectList(statement);
    }

    /**
     * {@inheritDoc}
     */

    public <E> List<E> selectList(String statement, Object parameter) {
        return this.sqlSessionProxy.<E>selectList(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */

    public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
        return this.sqlSessionProxy.<E>selectList(statement, parameter, rowBounds);
    }

    /**
     * {@inheritDoc}
     */

    @SuppressWarnings("rawtypes")
    public void select(String statement, ResultHandler handler) {
        this.sqlSessionProxy.select(statement, handler);
    }

    /**
     * {@inheritDoc}
     */

    @SuppressWarnings("rawtypes")
    public void select(String statement, Object parameter, ResultHandler handler) {
        this.sqlSessionProxy.select(statement, parameter, handler);
    }

    /**
     * {@inheritDoc}
     */

    @SuppressWarnings("rawtypes")
    public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
        this.sqlSessionProxy.select(statement, parameter, rowBounds, handler);
    }

    /**
     * {@inheritDoc}
     */

    public int insert(String statement) {
        return this.sqlSessionProxy.insert(statement);
    }

    /**
     * {@inheritDoc}
     */

    public int insert(String statement, Object parameter) {
        return this.sqlSessionProxy.insert(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */

    public int update(String statement) {
        return this.sqlSessionProxy.update(statement);
    }

    /**
     * {@inheritDoc}
     */

    public int update(String statement, Object parameter) {
        return this.sqlSessionProxy.update(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */

    public int delete(String statement) {
        return this.sqlSessionProxy.delete(statement);
    }

    /**
     * {@inheritDoc}
     */

    public int delete(String statement, Object parameter) {
        return this.sqlSessionProxy.delete(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */

    public <T> T getMapper(Class<T> type) {
        return getConfiguration().getMapper(type, this);
    }

    /**
     * {@inheritDoc}
     */

    public void commit() {
        throw new UnsupportedOperationException("Manual commit is not allowed over a Spring managed SqlSession");
    }

    /**
     * {@inheritDoc}
     */

    public void commit(boolean force) {
        throw new UnsupportedOperationException("Manual commit is not allowed over a Spring managed SqlSession");
    }

    /**
     * {@inheritDoc}
     */

    public void rollback() {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a Spring managed SqlSession");
    }

    /**
     * {@inheritDoc}
     */

    public void rollback(boolean force) {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a Spring managed SqlSession");
    }

    /**
     * {@inheritDoc}
     */

    public void close() {
        throw new UnsupportedOperationException("Manual close is not allowed over a Spring managed SqlSession");
    }

    /**
     * {@inheritDoc}
     */

    public void clearCache() {
        this.sqlSessionProxy.clearCache();
    }

    /**
     * {@inheritDoc}
     */

    public Configuration getConfiguration() {
        return this.sqlSessionFactory.getConfiguration();
    }

    /**
     * {@inheritDoc}
     */

    public Connection getConnection() {
        return this.sqlSessionProxy.getConnection();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.2
     */

    public List<BatchResult> flushStatements() {
        return this.sqlSessionProxy.flushStatements();
    }

    /**
     * Allow gently dispose bean:
     * <p>
     * <p>
     * <pre>
     * {@code
     *
     * <bean id="sqlSession" class="org.mybatis.spring.SqlSessionTemplate">
     *  <constructor-arg index="0" ref="sqlSessionFactory" />
     * </bean>
     * }
     * </pre>
     * <p>
     * The implementation of {@link DisposableBean} forces spring context to use
     * {@link DisposableBean#destroy()} method instead of
     * {@link MybatisSqlSessionTemplate#close()} to shutdown gently.
     *
     * @see MybatisSqlSessionTemplate#close()
     * @see org.springframework.beans.factory.support.DisposableBeanAdapter#inferDestroyMethodIfNecessary
     * @see org.springframework.beans.factory.support.DisposableBeanAdapter#CLOSE_METHOD_NAME
     */

    public void destroy() throws Exception {
        // This method forces spring disposer to avoid call of
        // SqlSessionTemplate.close() which gives UnsupportedOperationException
    }

    /**
     * Proxy needed to route MyBatis method calls to the proper SqlSession got
     * from Spring's Transaction Manager It also unwraps exceptions thrown by
     * {@code Method#invoke(Object, Object...)} to pass a
     * {@code PersistenceException} to the
     * {@code PersistenceExceptionTranslator}.
     */
    private class SqlSessionInterceptor implements InvocationHandler {

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            SqlSession sqlSession = MybatisSqlSessionTemplate.this.sqlSessionFactory
                    .openSession(MybatisSqlSessionTemplate.this.executorType);
            try {
                Object result = method.invoke(sqlSession, args);
                sqlSession.commit(true);
                return result;
            } catch (Throwable t) {
                throw new MybatisPlusException(t);
            } finally {
                if (sqlSession != null) {
                    sqlSession.close();
                }
            }
        }
    }
}