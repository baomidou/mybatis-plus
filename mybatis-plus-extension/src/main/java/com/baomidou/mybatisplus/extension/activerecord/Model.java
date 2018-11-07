/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.activerecord;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlHelper;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ActiveRecord 模式 CRUD
 * <p>
 * 必须存在对应的原始mapper并继承baseMapper并且可以使用的前提下
 * 才能使用此 AR 模式 !!!
 *
 * @param <T>
 * @author hubin
 * @since 2016-11-06
 */
public abstract class Model<T extends Model> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * <p>
     * 插入（字段选择插入）
     * </p>
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean insert() {
        SqlSession sqlSession = sqlSession();
        try {
            return SqlHelper.retBool(sqlSession.insert(sqlStatement(SqlMethod.INSERT_ONE), this));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * <p>
     * 插入 OR 更新
     * </p>
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean insertOrUpdate() {
        if (StringUtils.checkValNull(pkVal())) {
            // insert
            return insert();
        } else {
            /*
             * 更新成功直接返回，失败执行插入逻辑
             */
            return updateById() || insert();
        }
    }

    /**
     * <p>
     * 根据 ID 删除
     * </p>
     *
     * @param id 主键ID
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Serializable id) {
        SqlSession sqlSession = sqlSession();
        try {
            return SqlHelper.delBool(sqlSession.delete(sqlStatement(SqlMethod.DELETE_BY_ID), id));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * <p>
     * 根据主键删除
     * </p>
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById() {
        Assert.isFalse(StringUtils.checkValNull(pkVal()), "deleteById primaryKey is null.");
        return deleteById(pkVal());
    }

    /**
     * <p>
     * 删除记录
     * </p>
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Wrapper<T> queryWrapper) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(Constants.WRAPPER, queryWrapper);
        SqlSession sqlSession = sqlSession();
        try {
            return SqlHelper.delBool(sqlSession.delete(sqlStatement(SqlMethod.DELETE), map));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * <p>
     * 更新（字段选择更新）
     * </p>
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById() {
        Assert.isFalse(StringUtils.checkValNull(pkVal()), "updateById primaryKey is null.");
        // updateById
        Map<String, Object> map = new HashMap<>(1);
        map.put(Constants.ENTITY, this);
        SqlSession sqlSession = sqlSession();
        try {
            return SqlHelper.retBool(sqlSession.update(sqlStatement(SqlMethod.UPDATE_BY_ID), map));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * <p>
     * 执行 SQL 更新
     * </p>
     *
     * @param updateWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Wrapper<T> updateWrapper) {
        Map<String, Object> map = new HashMap<>(2);
        map.put(Constants.ENTITY, this);
        map.put(Constants.WRAPPER, updateWrapper);
        // update
        SqlSession sqlSession = sqlSession();
        try {
            return SqlHelper.retBool(sqlSession.update(sqlStatement(SqlMethod.UPDATE), map));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * <p>
     * 查询所有
     * </p>
     */
    public List<T> selectAll() {
        SqlSession sqlSession = sqlSession();
        try {
            return sqlSession.selectList(sqlStatement(SqlMethod.SELECT_LIST));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * <p>
     * 根据 ID 查询
     * </p>
     *
     * @param id 主键ID
     */
    public T selectById(Serializable id) {
        SqlSession sqlSession = sqlSession();
        try {
            return sqlSession.selectOne(sqlStatement(SqlMethod.SELECT_BY_ID), id);
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * <p>
     * 根据主键查询
     * </p>
     */
    public T selectById() {
        Assert.isFalse(StringUtils.checkValNull(pkVal()), "selectById primaryKey is null.");
        return selectById(pkVal());
    }

    /**
     * <p>
     * 查询总记录数
     * </p>
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */

    public List<T> selectList(Wrapper<T> queryWrapper) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(Constants.WRAPPER, queryWrapper);
        SqlSession sqlSession = sqlSession();
        try {
            return sqlSession.selectList(sqlStatement(SqlMethod.SELECT_LIST), map);
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * <p>
     * 查询一条记录
     * </p>
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    public T selectOne(Wrapper<T> queryWrapper) {
        return SqlHelper.getObject(selectList(queryWrapper));
    }

    /**
     * <p>
     * 翻页查询
     * </p>
     *
     * @param page         翻页查询条件
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    public IPage<T> selectPage(IPage<T> page, Wrapper<T> queryWrapper) {
        Map<String, Object> map = new HashMap<>(2);
        map.put(Constants.WRAPPER, queryWrapper);
        map.put("page", page);
        SqlSession sqlSession = sqlSession();
        try {
            page.setRecords(sqlSession.selectList(sqlStatement(SqlMethod.SELECT_PAGE), map));
        } finally {
            closeSqlSession(sqlSession);
        }
        return page;
    }

    /**
     * <p>
     * 查询总数
     * </p>
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    public Integer selectCount(Wrapper<T> queryWrapper) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(Constants.WRAPPER, queryWrapper);
        SqlSession sqlSession = sqlSession();
        try {
            return SqlHelper.retCount(sqlSession.<Integer>selectOne(sqlStatement(SqlMethod.SELECT_COUNT), map));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * <p>
     * 执行 SQL
     * </p>
     */
    public SqlRunner sql() {
        return new SqlRunner(getClass());
    }

    /**
     * <p>
     * 获取Session 默认自动提交
     * <p/>
     */
    protected SqlSession sqlSession() {
        return SqlHelper.sqlSession(getClass());
    }

    /**
     * 获取SqlStatement
     *
     * @param sqlMethod sqlMethod
     */
    protected String sqlStatement(SqlMethod sqlMethod) {
        return sqlStatement(sqlMethod.getMethod());
    }

    /**
     * 获取SqlStatement
     *
     * @param sqlMethod sqlMethod
     */
    protected String sqlStatement(String sqlMethod) {
        return SqlHelper.table(getClass()).getSqlStatement(sqlMethod);
    }

    /**
     * 主键值
     */
    protected Serializable pkVal() {
        return (Serializable) ReflectionKit.getMethodValue(this, TableInfoHelper.getTableInfo(getClass()).getKeyProperty());
    }

    /**
     * 释放sqlSession
     *
     * @param sqlSession session
     */
    protected void closeSqlSession(SqlSession sqlSession) {
        SqlSessionUtils.closeSqlSession(sqlSession, GlobalConfigUtils.currentSessionFactory(getClass()));
    }
}
