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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

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
     * 插入（批量），该方法不适合 Oracle
     * </p>
     *
     * @param entityList 实体对象集合
     */
    boolean saveBatch(Collection<T> entityList);

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
    boolean saveOrUpdateBatch(Collection<T> entityList);

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
    boolean updateBatchById(Collection<T> entityList);

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
    T getOne(Wrapper<T> queryWrapper);

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
     */
    Object getObj(Wrapper<T> queryWrapper);

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
     * 查询列表
     * </p>
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    List<T> list(Wrapper<T> queryWrapper);

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
     * 查询列表
     * </p>
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    List<Map<String, Object>> listMaps(Wrapper<T> queryWrapper);

    /**
     * <p>
     * 根据 Wrapper 条件，查询全部记录
     * </p>
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    List<Object> listObjs(Wrapper<T> queryWrapper);

    /**
     * <p>
     * 翻页查询
     * </p>
     *
     * @param page         翻页对象
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    IPage<Map<String, Object>> pageMaps(IPage<T> page, Wrapper<T> queryWrapper);
}
