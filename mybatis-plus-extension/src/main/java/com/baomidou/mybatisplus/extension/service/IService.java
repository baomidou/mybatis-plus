/*
 * Copyright (c) 2011-2016, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.additional.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * <p>
 * 顶级 Service
 * </p>
 *
 * @author hubin
 * @since 2018-06-23
 */
public interface IService<T> {

    /**
     * <p>
     * 插入一条记录（选择字段，策略插入）
     * </p>
     *
     * @param entity 实体对象
     */
    boolean save(T entity);

    /**
     * <p>
     * 插入（批量）
     * </p>
     *
     * @param entityList 实体对象集合
     */
    default boolean saveBatch(Collection<T> entityList) {
        return saveBatch(entityList, 1000);
    }

    /**
     * <p>
     * 插入（批量）
     * </p>
     *
     * @param entityList 实体对象集合
     * @param batchSize  插入批次数量
     */
    boolean saveBatch(Collection<T> entityList, int batchSize);

    /**
     * <p>
     * 批量修改插入
     * </p>
     *
     * @param entityList 实体对象集合
     */
    default boolean saveOrUpdateBatch(Collection<T> entityList) {
        return saveOrUpdateBatch(entityList, 1000);
    }

    /**
     * <p>
     * 批量修改插入
     * </p>
     *
     * @param entityList 实体对象集合
     * @param batchSize  每次的数量
     */
    boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize);

    /**
     * <p>
     * 根据 ID 删除
     * </p>
     *
     * @param id 主键ID
     */
    boolean removeById(Serializable id);

    /**
     * <p>
     * 根据 columnMap 条件，删除记录
     * </p>
     *
     * @param columnMap 表字段 map 对象
     */
    boolean removeByMap(Map<String, Object> columnMap);

    /**
     * <p>
     * 根据 entity 条件，删除记录
     * </p>
     *
     * @param queryWrapper 实体包装类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    boolean remove(Wrapper<T> queryWrapper);

    /**
     * <p>
     * 删除（根据ID 批量删除）
     * </p>
     *
     * @param idList 主键ID列表
     */
    boolean removeByIds(Collection<? extends Serializable> idList);

    /**
     * <p>
     * 根据 ID 选择修改
     * </p>
     *
     * @param entity 实体对象
     */
    boolean updateById(T entity);

    /**
     * <p>
     * 根据 whereEntity 条件，更新记录
     * </p>
     *
     * @param entity        实体对象
     * @param updateWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper}
     */
    boolean update(T entity, Wrapper<T> updateWrapper);

    /**
     * <p>
     * 根据ID 批量更新
     * </p>
     *
     * @param entityList 实体对象集合
     */
    default boolean updateBatchById(Collection<T> entityList) {
        return updateBatchById(entityList, 1000);
    }

    /**
     * <p>
     * 根据ID 批量更新
     * </p>
     *
     * @param entityList 实体对象集合
     * @param batchSize  更新批次数量
     */
    boolean updateBatchById(Collection<T> entityList, int batchSize);

    /**
     * <p>
     * TableId 注解存在更新记录，否插入一条记录
     * </p>
     *
     * @param entity 实体对象
     */
    boolean saveOrUpdate(T entity);

    /**
     * <p>
     * 根据 ID 查询
     * </p>
     *
     * @param id 主键ID
     */
    T getById(Serializable id);

    /**
     * <p>
     * 查询（根据ID 批量查询）
     * </p>
     *
     * @param idList 主键ID列表
     */
    Collection<T> listByIds(Collection<? extends Serializable> idList);

    /**
     * <p>
     * 查询（根据 columnMap 条件）
     * </p>
     *
     * @param columnMap 表字段 map 对象
     */
    Collection<T> listByMap(Map<String, Object> columnMap);

    /**
     * <p>
     * 根据 Wrapper，查询一条记录
     * </p>
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    default T getOne(Wrapper<T> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    /**
     * <p>
     * 根据 Wrapper，查询一条记录
     * </p>
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     * @param throwEx      有多个 result 是否抛出异常
     */
    T getOne(Wrapper<T> queryWrapper, boolean throwEx);

    /**
     * <p>
     * 根据 Wrapper，查询一条记录
     * </p>
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    Map<String, Object> getMap(Wrapper<T> queryWrapper);

    /**
     * <p>
     * 根据 Wrapper，查询一条记录
     * </p>
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     * @param mapper       转换函数
     */
    default <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return SqlHelper.getObject(listObjs(queryWrapper, mapper));
    }

    /**
     * <p>
     * 根据 Wrapper 条件，查询总记录数
     * </p>
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    int count(Wrapper<T> queryWrapper);

    /**
     * <p>
     * 查询总记录数
     * </p>
     *
     * @see Wrappers#emptyWrapper()
     */
    default int count() {
        return count(Wrappers.emptyWrapper());
    }

    /**
     * <p>
     * 查询列表
     * </p>
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    List<T> list(Wrapper<T> queryWrapper);

    /**
     * <p>
     * 查询所有
     * </p>
     *
     * @see Wrappers#emptyWrapper()
     */
    default List<T> list() {
        return list(Wrappers.emptyWrapper());
    }

    /**
     * <p>
     * 翻页查询
     * </p>
     *
     * @param page         翻页对象
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    IPage<T> page(IPage<T> page, Wrapper<T> queryWrapper);

    /**
     * <p>
     * 无条件翻页查询
     * </p>
     *
     * @param page 翻页对象
     * @see Wrappers#emptyWrapper()
     */
    default IPage<T> page(IPage<T> page) {
        return page(page, Wrappers.emptyWrapper());
    }

    /**
     * <p>
     * 查询列表
     * </p>
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    List<Map<String, Object>> listMaps(Wrapper<T> queryWrapper);

    /**
     * <p>
     * 查询所有列表
     * </p>
     *
     * @see Wrappers#emptyWrapper()
     */
    default List<Map<String, Object>> listMaps() {
        return listMaps(Wrappers.emptyWrapper());
    }

    /**
     * <p>
     * 查询全部记录
     * </p>
     */
    default List<Object> listObjs() {
        return listObjs(Function.identity());
    }

    /**
     * <p>
     * 查询全部记录
     * </p>
     *
     * @param mapper 转换函数
     */
    default <V> List<V> listObjs(Function<? super Object, V> mapper) {
        return listObjs(Wrappers.emptyWrapper(), mapper);
    }

    /**
     * <p>
     * 根据 Wrapper 条件，查询全部记录
     * </p>
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    default List<Object> listObjs(Wrapper<T> queryWrapper) {
        return listObjs(Wrappers.emptyWrapper(), Function.identity());
    }

    /**
     * <p>
     * 根据 Wrapper 条件，查询全部记录
     * </p>
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     * @param mapper       转换函数
     */
    <V> List<V> listObjs(Wrapper<T> queryWrapper, Function<? super Object, V> mapper);

    /**
     * <p>
     * 翻页查询
     * </p>
     *
     * @param page         翻页对象
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    IPage<Map<String, Object>> pageMaps(IPage<T> page, Wrapper<T> queryWrapper);

    /**
     * <p>
     * 无条件翻页查询
     * </p>
     *
     * @param page 翻页对象
     * @see Wrappers#emptyWrapper()
     */
    default IPage<Map<String, Object>> pageMaps(IPage<T> page) {
        return pageMaps(page, Wrappers.emptyWrapper());
    }

    BaseMapper<T> getBaseMapper();

    default QueryChainWrapper<T> query() {
        return new QueryChainWrapper<>(getBaseMapper());
    }

    default InnerLambdaQuery<T> lambdaQuery() {
        return new InnerLambdaQuery<>(this);
    }

    class InnerLambdaQuery<T> extends LambdaQueryWrapper<T> {

        private IService<T> iService;

        private InnerLambdaQuery() {
        }

        protected InnerLambdaQuery(IService iService) {
            this.iService = iService;
        }


        /**
         * <p>
         * 根据 entity 条件，删除记录
         * </p>
         */
        public boolean remove() {
            return iService.remove(this);
        }


        /**
         * <p>
         * 根据 Wrapper，查询一条记录
         * </p>
         */
        public T getOne() {
            return getOne(false);
        }

        /**
         * <p>
         * 根据 Wrapper，查询一条记录
         * </p>
         *
         * @param throwEx 有多个 result 是否抛出异常
         */
        public T getOne(boolean throwEx) {
            return iService.getOne(this, throwEx);
        }

        /**
         * <p>
         * 根据 Wrapper，查询一条记录
         * </p>
         */
        public Map<String, Object> getMap() {
            return iService.getMap(this);
        }


        /**
         * <p>
         * 根据 Wrapper 条件，查询总记录数
         * </p>
         */
        public int count() {
            return iService.count(this);
        }

        /**
         * <p>
         * 查询列表
         * </p>
         */
        public List<T> list() {
            return iService.list(this);
        }

        /**
         * <p>
         * 翻页查询
         * </p>
         *
         * @param page 翻页对象
         */
        public IPage<T> page(IPage<T> page) {
            return iService.page(page, this);
        }

        /**
         * <p>
         * 查询列表
         * </p>
         */
        public List<Map<String, Object>> listMaps() {
            return iService.listMaps(this);
        }

        /**
         * <p>
         * 查询全部记录
         * </p>
         *
         * @param mapper 转换函数
         */
        public <V> List<V> listObjs(Function<? super Object, V> mapper) {
            return iService.listObjs(this, mapper);
        }

        /**
         * <p>
         * 根据 Wrapper 条件，查询全部记录
         * </p>
         */
        public List<Object> listObjs() {
            return listObjs(Function.identity());
        }

        /**
         * <p>
         * 翻页查询
         * </p>
         *
         * @param page 翻页对象
         */
        public IPage<Map<String, Object>> pageMaps(IPage<T> page) {
            return iService.pageMaps(page, this);
        }

        @Override
        public InnerLambdaQuery<T> setEntity(T entity) {
            super.setEntity(entity);
            return this;
        }


        @Override
        public <V> InnerLambdaQuery<T> allEq(Map<SFunction<T, ?>, V> params) {
            super.allEq(params);
            return this;
        }

        @Override
        public <V> InnerLambdaQuery<T> allEq(Map<SFunction<T, ?>, V> params, boolean null2IsNull) {
            super.allEq(params, null2IsNull);
            return this;
        }

        @Override
        public <V> InnerLambdaQuery<T> allEq(BiPredicate<SFunction<T, ?>, V> filter, Map<SFunction<T, ?>, V> params) {
            super.allEq(filter, params);
            return this;
        }

        @Override
        public <V> InnerLambdaQuery<T> allEq(BiPredicate<SFunction<T, ?>, V> filter, Map<SFunction<T, ?>, V> params, boolean null2IsNull) {
            super.allEq(filter, params, null2IsNull);
            return this;
        }

        @Override
        public <V> InnerLambdaQuery<T> allEq(boolean condition, Map<SFunction<T, ?>, V> params, boolean null2IsNull) {
            super.allEq(condition, params, null2IsNull);
            return this;
        }

        @Override
        public <V> InnerLambdaQuery<T> allEq(boolean condition, BiPredicate<SFunction<T, ?>, V> filter, Map<SFunction<T, ?>, V> params, boolean null2IsNull) {
            super.allEq(condition, filter, params, null2IsNull);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> eq(SFunction<T, ?> column, Object val) {
            super.eq(column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> ne(SFunction<T, ?> column, Object val) {
            super.ne(column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> gt(SFunction<T, ?> column, Object val) {
            super.gt(column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> ge(SFunction<T, ?> column, Object val) {
            super.ge(column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> lt(SFunction<T, ?> column, Object val) {
            super.lt(column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> le(SFunction<T, ?> column, Object val) {
            super.le(column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> between(SFunction<T, ?> column, Object val1, Object val2) {
            super.between(column, val1, val2);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> notBetween(SFunction<T, ?> column, Object val1, Object val2) {
            super.notBetween(column, val1, val2);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> like(SFunction<T, ?> column, Object val) {
            super.like(column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> notLike(SFunction<T, ?> column, Object val) {
            super.notLike(column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> likeLeft(SFunction<T, ?> column, Object val) {
            super.likeLeft(column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> likeRight(SFunction<T, ?> column, Object val) {
            super.likeRight(column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
            super.eq(condition, column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> ne(boolean condition, SFunction<T, ?> column, Object val) {
            super.ne(condition, column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> gt(boolean condition, SFunction<T, ?> column, Object val) {
            super.gt(condition, column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> ge(boolean condition, SFunction<T, ?> column, Object val) {
            super.ge(condition, column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> lt(boolean condition, SFunction<T, ?> column, Object val) {
            super.lt(condition, column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> le(boolean condition, SFunction<T, ?> column, Object val) {
            super.le(condition, column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> like(boolean condition, SFunction<T, ?> column, Object val) {
            super.like(condition, column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> notLike(boolean condition, SFunction<T, ?> column, Object val) {
            super.notLike(condition, column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> likeLeft(boolean condition, SFunction<T, ?> column, Object val) {
            super.likeLeft(condition, column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> likeRight(boolean condition, SFunction<T, ?> column, Object val) {
            super.likeRight(condition, column, val);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> between(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
            super.between(condition, column, val1, val2);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> notBetween(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
            super.notBetween(condition, column, val1, val2);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> and(boolean condition, Function<LambdaQueryWrapper<T>, LambdaQueryWrapper<T>> func) {
            super.and(condition, func);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> or(boolean condition, Function<LambdaQueryWrapper<T>, LambdaQueryWrapper<T>> func) {
            super.or(condition, func);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> nested(boolean condition, Function<LambdaQueryWrapper<T>, LambdaQueryWrapper<T>> func) {
            super.nested(condition, func);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> or(boolean condition) {
            super.or(condition);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> apply(boolean condition, String applySql, Object... value) {
            super.apply(condition, applySql, value);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> last(boolean condition, String lastSql) {
            super.last(condition, lastSql);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> exists(boolean condition, String existsSql) {
            super.exists(condition, existsSql);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> isNotNull(SFunction<T, ?> column) {
            super.isNotNull(column);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> in(SFunction<T, ?> column, Collection<?> coll) {
            super.in(column, coll);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> inOrThrow(SFunction<T, ?> column, Collection<?> value) {
            super.inOrThrow(column, value);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> inOrThrow(boolean condition, SFunction<T, ?> column, Collection<?> coll) {
            super.inOrThrow(condition, column, coll);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> inOrThrow(SFunction<T, ?> column, Object... values) {
            super.inOrThrow(column, values);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> inOrThrow(boolean condition, SFunction<T, ?> column, Object... values) {
            super.inOrThrow(condition, column, values);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> in(SFunction<T, ?> column, Object... values) {
            super.in(column, values);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> in(boolean condition, SFunction<T, ?> column, Object... values) {
            super.in(condition, column, values);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> notIn(SFunction<T, ?> column, Collection<?> coll) {
            super.notIn(column, coll);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> notInOrThrow(SFunction<T, ?> column, Collection<?> value) {
            super.notInOrThrow(column, value);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> notInOrThrow(boolean condition, SFunction<T, ?> column, Collection<?> coll) {
            super.notInOrThrow(condition, column, coll);
            return this;
        }
        @Override
        public InnerLambdaQuery<T> notInOrThrow(SFunction<T, ?> column, Object... values) {
            super.notInOrThrow(column, values);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> notInOrThrow(boolean condition, SFunction<T, ?> column, Object... values) {
            super.notInOrThrow(condition, column, values);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> notIn(SFunction<T, ?> column, Object... value) {
            super.notIn(column, value);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> notIn(boolean condition, SFunction<T, ?> column, Object... values) {
            super.notIn(condition, column, values);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> orderByAsc(SFunction<T, ?>... columns) {
            super.orderByAsc(columns);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> orderByAsc(boolean condition, SFunction<T, ?>... columns) {
            super.orderByAsc(condition, columns);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> orderByDesc(SFunction<T, ?>... columns) {
            super.orderByDesc(columns);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> orderByDesc(boolean condition, SFunction<T, ?>... columns) {
            super.orderByDesc(condition, columns);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> having(String sqlHaving, Object... params) {
            super.having(sqlHaving, params);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> or() {
            super.or();
            return this;
        }

        @Override
        public InnerLambdaQuery<T> apply(String applySql, Object... value) {
            super.apply(applySql, value);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> last(String lastSql) {
            super.last(lastSql);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> exists(String existsSql) {
            super.exists(existsSql);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> notExists(String notExistsSql) {
            super.notExists(notExistsSql);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> and(Function<LambdaQueryWrapper<T>, LambdaQueryWrapper<T>> func) {
            super.and(func);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> or(Function<LambdaQueryWrapper<T>, LambdaQueryWrapper<T>> func) {
            super.or(func);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> nested(Function<LambdaQueryWrapper<T>, LambdaQueryWrapper<T>> func) {
            super.nested(func);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> notExists(boolean condition, String notExistsSql) {
            super.notExists(condition, notExistsSql);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> isNull(boolean condition, SFunction<T, ?> column) {
            super.isNull(condition, column);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> isNull(SFunction<T, ?> column) {
            super.isNull(column);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> isNotNull(boolean condition, SFunction<T, ?> column) {
            super.isNotNull(condition, column);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> in(boolean condition, SFunction<T, ?> column, Collection<?> coll) {
            super.in(condition, column, coll);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> notIn(boolean condition, SFunction<T, ?> column, Collection<?> coll) {
            super.notIn(condition, column, coll);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> inSql(SFunction<T, ?> column, String inValue) {
            super.inSql(column, inValue);
            return this;
        }


        @Override
        public InnerLambdaQuery<T> inSql(boolean condition, SFunction<T, ?> column, String inValue) {
            super.inSql(condition, column, inValue);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> notInSql(SFunction<T, ?> column, String inValue) {
            super.notInSql(column, inValue);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> notInSql(boolean condition, SFunction<T, ?> column, String inValue) {
            super.notInSql(condition, column, inValue);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> groupBy(SFunction<T, ?>... columns) {
            super.groupBy(columns);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> groupBy(boolean condition, SFunction<T, ?>... columns) {
            super.groupBy(condition, columns);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> orderBy(boolean condition, boolean isAsc, SFunction<T, ?>... columns) {
            super.orderBy(condition, isAsc, columns);
            return this;
        }

        @Override
        public InnerLambdaQuery<T> having(boolean condition, String sqlHaving, Object... params) {
            super.having(condition, sqlHaving, params);
            return this;
        }
    }
}
