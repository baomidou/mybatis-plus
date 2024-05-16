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
package com.baomidou.mybatisplus.extension.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * IService 实现类（ 泛型：M 是 mapper 对象，T 是实体 ）
 *
 * @author hubin
 * @since 2018-06-23
 */
@SuppressWarnings("unchecked")
public abstract class ServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {

    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
    protected M baseMapper;

    @Override
    public M getBaseMapper() {
        Assert.notNull(this.baseMapper, "baseMapper can not be null");
        return this.baseMapper;
    }

    /**
     * @see #getEntityClass()
     */
    private Class<T> entityClass;

    @Override
    public Class<T> getEntityClass() {
        if(this.entityClass == null) {
            this.entityClass = (Class<T>) GenericTypeUtils.resolveTypeArguments(this.getMapperClass(), BaseMapper.class)[0];
        }
        return this.entityClass;
    }

    /**
     *  @see #currentMapperClass()
     */
    private Class<M> mapperClass;

    private volatile SqlSessionFactory sqlSessionFactory;

    protected SqlSessionFactory getSqlSessionFactory() {
        if (this.sqlSessionFactory == null) {
            MybatisMapperProxy<?> mybatisMapperProxy = MybatisUtils.getMybatisMapperProxy(this.getBaseMapper());
            this.sqlSessionFactory = MybatisUtils.getSqlSessionFactory(mybatisMapperProxy);
        }
        return this.sqlSessionFactory;
    }

    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     * @deprecated 3.3.1
     */
    @Deprecated
    protected boolean retBool(Integer result) {
        return SqlHelper.retBool(result);
    }

    /**
     * @return baseMapper 真实类型
     * @deprecated 3.5.7 {@link #getMapperClass()}
     */
    @Deprecated
    protected Class<M> currentMapperClass() {
        return this.getMapperClass();
    }

    /**
     * @return baseMapper 真实类型
     * @since 3.5.7
     */
    public Class<M> getMapperClass() {
        if (this.mapperClass == null) {
            MybatisMapperProxy<?> mybatisMapperProxy = MybatisUtils.getMybatisMapperProxy(this.getBaseMapper());
            this.mapperClass = (Class<M>) mybatisMapperProxy.getMapperInterface();
        }
        return this.mapperClass;
    }

    /**
     * @return 实体类型
     * @deprecated 3.5.7 {@link #getEntityClass()}
     */
    @Deprecated
    protected Class<T> currentModelClass() {
        return getEntityClass();
    }


    /**
     * 批量操作 SqlSession
     *
     * @deprecated 3.3.0
     */
    @Deprecated
    protected SqlSession sqlSessionBatch() {
        return getSqlSessionFactory().openSession(ExecutorType.BATCH);
    }

    /**
     * 释放sqlSession
     *
     * @param sqlSession session
     * @deprecated 3.3.0
     */
    @Deprecated
    protected void closeSqlSession(SqlSession sqlSession) {
        SqlSessionUtils.closeSqlSession(sqlSession, getSqlSessionFactory());
    }

    /**
     * 获取 SqlStatement
     *
     * @param sqlMethod ignore
     * @return ignore
     * @see #getSqlStatement(SqlMethod)
     * @deprecated 3.4.0
     */
    @Deprecated
    protected String sqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.table(getEntityClass()).getSqlStatement(sqlMethod.getMethod());
    }

    /**
     * 批量插入
     *
     * @param entityList ignore
     * @param batchSize  ignore
     * @return ignore
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
    }

    /**
     * 获取mapperStatementId
     *
     * @param sqlMethod 方法名
     * @return 命名id
     * @since 3.4.0
     */
    protected String getSqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.getSqlStatement(this.currentMapperClass(), sqlMethod);
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     * @return boolean
     */
    @Override
    public boolean saveOrUpdate(T entity) {
        return getBaseMapper().saveOrUpdate(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        return SqlHelper.saveOrUpdateBatch(getSqlSessionFactory(), this.currentMapperClass(), this.log, entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal)
                || CollectionUtils.isEmpty(sqlSession.selectList(getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(sqlStatement, param);
        });
    }

    @Override
    public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
        return getBaseMapper().selectOne(queryWrapper, throwEx);
    }

    @Override
    public Optional<T> getOneOpt(Wrapper<T> queryWrapper, boolean throwEx) {
        return Optional.ofNullable(getBaseMapper().selectOne(queryWrapper, throwEx));
    }

    @Override
    public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
        return SqlHelper.getObject(log, getBaseMapper().selectMaps(queryWrapper));
    }

    @Override
    public <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return SqlHelper.getObject(log, listObjs(queryWrapper, mapper));
    }

    /**
     * 执行批量操作
     *
     * @param consumer consumer
     * @since 3.3.0
     * @deprecated 3.3.1 后面我打算移除掉 {@link #executeBatch(Collection, int, BiConsumer)} }.
     */
    @Deprecated
    protected boolean executeBatch(Consumer<SqlSession> consumer) {
        return SqlHelper.executeBatch(getSqlSessionFactory(), this.log, consumer);
    }

    /**
     * 执行批量操作
     *
     * @param list      数据集合
     * @param batchSize 批量大小
     * @param consumer  执行方法
     * @param <E>       泛型
     * @return 操作结果
     * @since 3.3.1
     */
    protected <E> boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        return SqlHelper.executeBatch(getSqlSessionFactory(), this.log, list, batchSize, consumer);
    }

    /**
     * 执行批量操作（默认批次提交数量{@link IService#DEFAULT_BATCH_SIZE}）
     *
     * @param list     数据集合
     * @param consumer 执行方法
     * @param <E>      泛型
     * @return 操作结果
     * @since 3.3.1
     */
    protected <E> boolean executeBatch(Collection<E> list, BiConsumer<SqlSession, E> consumer) {
        return executeBatch(list, DEFAULT_BATCH_SIZE, consumer);
    }

    @Override
    public boolean removeById(Serializable id, boolean useFill) {
        return SqlHelper.retBool(getBaseMapper().deleteById(id, useFill));
    }

    @Override
    public boolean removeBatchByIds(Collection<?> list, int batchSize) {
        return SqlHelper.retBool(getBaseMapper().deleteByIds(list));
    }

    @Override
    public boolean removeBatchByIds(Collection<?> list, int batchSize, boolean useFill) {
        return SqlHelper.retBool(getBaseMapper().deleteByIds(list, useFill));
    }

}
