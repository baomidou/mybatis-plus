/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
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
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ActiveRecord 模式 CRUD
 * <p>
 * 必须存在对应的原始mapper并继承baseMapper并且可以使用的前提下
 * 才能使用此 AR 模式 !!!
 * </p>
 *
 * @param <T>
 * @author hubin
 * @since 2016-11-06
 */
public abstract class Model<T extends Model<?>> implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient Log log = LogFactory.getLog(getClass());

    /**
     * 插入（字段选择插入）
     */
    public boolean insert() {
        SqlSession sqlSession = sqlSession();
        try {
            return SqlHelper.retBool(sqlSession.insert(sqlStatement(SqlMethod.INSERT_ONE), this));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * 插入 OR 更新
     */
    public boolean insertOrUpdate() {
        return StringUtils.checkValNull(pkVal()) || Objects.isNull(selectById(pkVal())) ? insert() : updateById();
    }

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    public boolean deleteById(Serializable id) {
        SqlSession sqlSession = sqlSession();
        try {
            return SqlHelper.retBool(sqlSession.delete(sqlStatement(SqlMethod.DELETE_BY_ID), id));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * 根据主键删除
     */
    public boolean deleteById() {
        Assert.isFalse(StringUtils.checkValNull(pkVal()), "deleteById primaryKey is null.");
        return deleteById(pkVal());
    }

    /**
     * 删除记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    public boolean delete(Wrapper<T> queryWrapper) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(Constants.WRAPPER, queryWrapper);
        SqlSession sqlSession = sqlSession();
        try {
            return SqlHelper.retBool(sqlSession.delete(sqlStatement(SqlMethod.DELETE), map));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * 更新（字段选择更新）
     */
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
     * 执行 SQL 更新
     *
     * @param updateWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     */
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
     * 查询所有
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
     * 根据 ID 查询
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
     * 根据主键查询
     */
    public T selectById() {
        Assert.isFalse(StringUtils.checkValNull(pkVal()), "selectById primaryKey is null.");
        return selectById(pkVal());
    }

    /**
     * 查询总记录数
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
     * 查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    public T selectOne(Wrapper<T> queryWrapper) {
        return SqlHelper.getObject(log, selectList(queryWrapper));
    }

    /**
     * 翻页查询
     *
     * @param page         翻页查询条件
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    public <E extends IPage<T>> E selectPage(E page, Wrapper<T> queryWrapper) {
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
     * 查询总数
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
     * 执行 SQL
     */
    public SqlRunner sql() {
        return new SqlRunner(getClass());
    }

    /**
     * 获取Session 默认自动提交
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
