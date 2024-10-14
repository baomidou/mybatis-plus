package com.baomidou.mybatisplus.extension.repository;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.baomidou.mybatisplus.core.toolkit.MybatisUtils;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AbstractRepository<M extends BaseMapper<T>, T>  implements IRepository<T> {

    protected final Log log = LogFactory.getLog(getClass());

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
     *  @see #getMapperClass()
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
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     * @return boolean
     */
    @Override
    public boolean saveOrUpdate(T entity) {
        return getBaseMapper().insertOrUpdate(entity);
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
     * 执行批量操作（默认批次提交数量{@link IRepository#DEFAULT_BATCH_SIZE}）
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

}
