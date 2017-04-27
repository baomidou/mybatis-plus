/**
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
package com.baomidou.mybatisplus.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;

/**
 * <p>
 * 顶级 Service
 * </p>
 *
 * @author hubin
 * @Date 2016-04-20
 */
public interface IService<T> {

    /**
     * <p>
     * 插入一条记录（选择字段，策略插入）
     * </p>
     *
     * @param entity
     *            实体对象
     * @return boolean
     */
    boolean insert(T entity);

    /**
     * <p>
     * 插入一条记录（全部字段）
     * </p>
     *
     * @param entity
     *            实体对象
     * @return boolean
     */
    boolean insertAllColumn(T entity);

    /**
     * <p>
     * 插入（批量），该方法不适合 Oracle
     * </p>
     *
     * @param entityList
     *            实体对象列表
     * @return boolean
     */
    boolean insertBatch(List<T> entityList);

    /**
     * <p>
     * 插入（批量）
     * </p>
     *
     * @param entityList
     *            实体对象列表
     * @param batchSize
     *            插入批次数量
     * @return boolean
     */
    boolean insertBatch(List<T> entityList, int batchSize);

    /**
     * <p>
     * 批量修改插入
     * </p>
     *
     * @param entityList
     *            实体对象列表
     * @return boolean
     */
    boolean insertOrUpdateBatch(List<T> entityList);

    /**
     * <p>
     * 批量修改插入
     * </p>
     *
     * @param entityList
     *            实体对象列表
     * @param batchSize
     *
     * @return boolean
     */
    boolean insertOrUpdateBatch(List<T> entityList, int batchSize);

    /**
     * <p>
     * 根据 ID 删除
     * </p>
     *
     * @param id
     *            主键ID
     * @return boolean
     */
    boolean deleteById(Serializable id);

    /**
     * <p>
     * 根据 columnMap 条件，删除记录
     * </p>
     *
     * @param columnMap
     *            表字段 map 对象
     * @return boolean
     */
    boolean deleteByMap(Map<String, Object> columnMap);

    /**
     * <p>
     * 根据 entity 条件，删除记录
     * </p>
     *
     * @param wrapper
     *            实体包装类 {@link Wrapper}
     * @return boolean
     */
    boolean delete(Wrapper<T> wrapper);

    /**
     * <p>
     * 删除（根据ID 批量删除）
     * </p>
     *
     * @param idList
     *            主键ID列表
     * @return boolean
     */
    boolean deleteBatchIds(List<? extends Serializable> idList);

    /**
     * <p>
     * 根据 ID 选择修改
     * </p>
     *
     * @param entity
     *            实体对象
     * @return boolean
     */
    boolean updateById(T entity);

    /**
     * <p>
     * 根据 ID 修改全部字段
     * </p>
     *
     * @param entity
     *            实体对象
     * @return boolean
     */
    boolean updateAllColumnById(T entity);

    /**
     * <p>
     * 根据 whereEntity 条件，更新记录
     * </p>
     *
     * @param entity
     *            实体对象
     * @param wrapper
     *            实体包装类 {@link Wrapper}
     * @return boolean
     */
    boolean update(T entity, Wrapper<T> wrapper);

    /**
     * <p>
     * 根据ID 批量更新
     * </p>
     *
     * @param entityList
     *            实体对象列表
     * @return boolean
     */
    boolean updateBatchById(List<T> entityList);

    /**
     * <p>
     * 根据ID 批量更新
     * </p>
     *
     * @param entityList
     *            实体对象列表
     * @param batchSize
     *            更新批次数量
     * @return boolean
     */
    boolean updateBatchById(List<T> entityList, int batchSize);

    /**
     * <p>
     * TableId 注解存在更新记录，否插入一条记录
     * </p>
     *
     * @param entity
     *            实体对象
     * @return boolean
     */
    boolean insertOrUpdate(T entity);

    /**
     * <p>
     * 根据 ID 查询
     * </p>
     *
     * @param id
     *            主键ID
     * @return T
     */
    T selectById(Serializable id);

    /**
     * <p>
     * 查询（根据ID 批量查询）
     * </p>
     *
     * @param idList
     *            主键ID列表
     * @return List<T>
     */
    List<T> selectBatchIds(List<? extends Serializable> idList);

    /**
     * <p>
     * 查询（根据 columnMap 条件）
     * </p>
     *
     * @param columnMap
     *            表字段 map 对象
     * @return List<T>
     */
    List<T> selectByMap(Map<String, Object> columnMap);

    /**
     * <p>
     * 根据 Wrapper，查询一条记录
     * </p>
     *
     * @param wrapper
     *            实体对象
     * @return T
     */
    T selectOne(Wrapper<T> wrapper);

    /**
     * <p>
     * 根据 Wrapper，查询一条记录
     * </p>
     *
     * @param wrapper
     *            {@link Wrapper}
     * @return Map<String,Object>
     */
    Map<String, Object> selectMap(Wrapper<T> wrapper);

    /**
     * <p>
     * 根据 Wrapper，查询一条记录
     * </p>
     *
     * @param wrapper
     *            {@link Wrapper}
     * @return Object
     */
    Object selectObj(Wrapper<T> wrapper);

    /**
     * <p>
     * 根据 Wrapper 条件，查询总记录数
     * </p>
     *
     * @param wrapper
     *            实体对象
     * @return int
     */
    int selectCount(Wrapper<T> wrapper);

    /**
     * <p>
     * 查询列表
     * </p>
     *
     * @param wrapper
     *            实体包装类 {@link Wrapper}
     * @return
     */
    List<T> selectList(Wrapper<T> wrapper);

    /**
     * <p>
     * 翻页查询
     * </p>
     *
     * @param page
     *            翻页对象
     * @return
     */
    Page<T> selectPage(Page<T> page);

    /**
     * <p>
     * 查询列表
     * </p>
     *
     * @param wrapper
     *            {@link Wrapper}
     * @return
     */
    List<Map<String, Object>> selectMaps(Wrapper<T> wrapper);

    /**
     * <p>
     * 根据 Wrapper 条件，查询全部记录
     * </p>
     *
     * @param wrapper
     *            实体对象封装操作类（可以为 null）
     * @return List<Object>
     */
    List<Object> selectObjs(Wrapper<T> wrapper);

    /**
     * <p>
     * 翻页查询
     * </p>
     *
     * @param page
     *            翻页对象
     * @param wrapper
     *            {@link Wrapper}
     * @return
     */
    @SuppressWarnings("rawtypes")
    Page<Map<String, Object>> selectMapsPage(Page page, Wrapper<T> wrapper);

    /**
     * <p>
     * 翻页查询
     * </p>
     *
     * @param page
     *            翻页对象
     * @param wrapper
     *            实体包装类 {@link Wrapper}
     * @return
     */
    Page<T> selectPage(Page<T> page, Wrapper<T> wrapper);

}
