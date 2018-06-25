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
package com.baomidou.mybatisplus.extension.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlHelper;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * IService 实现类（ 泛型：M 是 mapper 对象，T 是实体 ， PK 是主键泛型 ）
 * </p>
 *
 * @author hubin
 * @since 2018-06-23
 */
public class ServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {

    @Autowired
    protected M baseMapper;

    /**
     * <p>
     * 判断数据库操作是否成功
     * </p>
     * <p>
     * 注意！！ 该方法为 Integer 判断，不可传入 int 基本类型
     * </p>
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    protected static boolean retBool(Integer result) {
        return SqlHelper.retBool(result);
    }

    protected Class<T> currentModelClass() {
        return ReflectionKit.getSuperClassGenricType(getClass(), 1);
    }

    /**
     * <p>
     * 批量操作 SqlSession
     * </p>
     */
    protected SqlSession sqlSessionBatch() {
        return SqlHelper.sqlSessionBatch(currentModelClass());
    }

    /**
     * 获取SqlStatement
     *
     * @param sqlMethod
     * @return
     */
    protected String sqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.table(currentModelClass()).getSqlStatement(sqlMethod.getMethod());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insert(T entity) {
        return ServiceImpl.retBool(baseMapper.insert(entity));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertBatch(Collection<T> entityList) {
        return insertBatch(entityList, 30);
    }

    /**
     * 批量插入
     *
     * @param entityList
     * @param batchSize
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertBatch(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("Error: entityList must not be empty");
        }
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            int i = 0;
            String sqlStatement = sqlStatement(SqlMethod.INSERT_ONE);
            Iterator<T> iterator = entityList.iterator();
            while (iterator.hasNext()) {
                batchSqlSession.insert(sqlStatement, iterator.next());
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        } catch (Throwable e) {
            throw new MybatisPlusException("Error: Cannot execute insertBatch Method. Cause", e);
        }
        return true;
    }

    /**
     * <p>
     * TableId 注解存在更新记录，否插入一条记录
     * </p>
     *
     * @param entity 实体对象
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertOrUpdate(T entity) {
        if (null != entity) {
            Class<?> cls = entity.getClass();
            TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
            if (null != tableInfo && StringUtils.isNotEmpty(tableInfo.getKeyProperty())) {
                Object idVal = ReflectionKit.getMethodValue(cls, entity, tableInfo.getKeyProperty());
                if (StringUtils.checkValNull(idVal)) {
                    return insert(entity);
                } else {
                    /*
                     * 更新成功直接返回，失败执行插入逻辑
                     */
                    return updateById(entity) || insert(entity);
                }
            } else {
                throw new MybatisPlusException("Error:  Can not execute. Could not find @TableId.");
            }
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertOrUpdateBatch(Collection<T> entityList) {
        return insertOrUpdateBatch(entityList, 30);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertOrUpdateBatch(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("Error: entityList must not be empty");
        }
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            Iterator<T> iterator = entityList.iterator();
            while (iterator.hasNext()) {
                insertOrUpdate(iterator.next());
            }
            batchSqlSession.flushStatements();
        } catch (Throwable e) {
            throw new MybatisPlusException("Error: Cannot execute insertOrUpdateBatch Method. Cause", e);
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteById(Serializable id) {
        return SqlHelper.delBool(baseMapper.deleteById(id));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteByMap(Map<String, Object> columnMap) {
        if (ObjectUtils.isEmpty(columnMap)) {
            throw new MybatisPlusException("deleteByMap columnMap is empty.");
        }
        return SqlHelper.delBool(baseMapper.deleteByMap(columnMap));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(Wrapper<T> wrapper) {
        return SqlHelper.delBool(baseMapper.delete(wrapper));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatchIds(Collection<? extends Serializable> idList) {
        return SqlHelper.delBool(baseMapper.deleteBatchIds(idList));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(T entity) {
        return ServiceImpl.retBool(baseMapper.updateById(entity));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(T entity, Wrapper<T> wrapper) {
        return ServiceImpl.retBool(baseMapper.update(entity, wrapper));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateBatchById(Collection<T> entityList) {
        return updateBatchById(entityList, 30);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("Error: entityList must not be empty");
        }
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            int i = 0;
            String sqlStatement = sqlStatement(SqlMethod.UPDATE_BY_ID);
            Iterator<T> iterator = entityList.iterator();
            while (iterator.hasNext()) {
                MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                param.put("et", iterator.next());
                batchSqlSession.update(sqlStatement, param);
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        } catch (Throwable e) {
            throw new MybatisPlusException("Error: Cannot execute updateBatchById Method. Cause", e);
        }
        return true;
    }

    @Override
    public T selectById(Serializable id) {
        return baseMapper.selectById(id);
    }

    @Override
    public Collection<T> selectBatchIds(Collection<? extends Serializable> idList) {
        return baseMapper.selectBatchIds(idList);
    }

    @Override
    public Collection<T> selectByMap(Map<String, Object> columnMap) {
        return baseMapper.selectByMap(columnMap);
    }

    @Override
    public T selectOne(Wrapper<T> wrapper) {
        return SqlHelper.getObject(baseMapper.selectList(wrapper));
    }

    @Override
    public Map<String, Object> selectMap(Wrapper<T> wrapper) {
        return SqlHelper.getObject(baseMapper.selectMaps(wrapper));
    }

    @Override
    public Object selectObj(Wrapper<T> wrapper) {
        return SqlHelper.getObject(baseMapper.selectObjs(wrapper));
    }

    @Override
    public int selectCount(Wrapper<T> wrapper) {
        return SqlHelper.retCount(baseMapper.selectCount(wrapper));
    }

    @Override
    public List<T> selectList(Wrapper<T> wrapper) {
        return baseMapper.selectList(wrapper);
    }

    @Override
    public IPage<T> selectPage(IPage<T> page, Wrapper<T> wrapper) {
        wrapper = (Wrapper<T>) SqlHelper.fillWrapper(page, wrapper);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Map<String, Object>> selectMaps(Wrapper<T> wrapper) {
        return baseMapper.selectMaps(wrapper);
    }

    @Override
    public List<Object> selectObjs(Wrapper<T> wrapper) {
        return baseMapper.selectObjs(wrapper);
    }

    @Override
    public IPage<Map<String, Object>> selectMapsPage(IPage page, Wrapper<T> wrapper) {
        wrapper = (Wrapper<T>) SqlHelper.fillWrapper(page, wrapper);
        return baseMapper.selectMapsPage(page, wrapper);
    }
}
