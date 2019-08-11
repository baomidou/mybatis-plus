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
package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.core.assist.ISqlRunner;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SqlRunner 执行 SQL
 *
 * @author Caratacus
 * @since 2016-12-11
 */
public class SqlRunner implements ISqlRunner {

    private Log log = LogFactory.getLog(SqlRunner.class);
    // 单例Query
    public static final SqlRunner DEFAULT = new SqlRunner();
    // 默认FACTORY
//    public static SqlSessionFactory FACTORY;
    private SqlSessionFactory sqlSessionFactory;

    private Class<?> clazz;

    public SqlRunner() {
        this.sqlSessionFactory = SqlHelper.FACTORY;
    }

    public SqlRunner(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * 获取默认的SqlQuery(适用于单库)
     *
     * @return ignore
     */
    public static SqlRunner db() {
        // 初始化的静态变量 还是有前后加载的问题 该判断只会执行一次
        if (DEFAULT.sqlSessionFactory == null) {
            DEFAULT.sqlSessionFactory = SqlHelper.FACTORY;
        }
        return DEFAULT;
    }

    /**
     * 根据当前class对象获取SqlQuery(适用于多库)
     *
     * @param clazz ignore
     * @return ignore
     */
    public static SqlRunner db(Class<?> clazz) {
        return new SqlRunner(clazz);
    }

    @Transactional
    @Override
    public boolean insert(String sql, Object... args) {
        SqlSession sqlSession = sqlSession();
        try {
            return SqlHelper.retBool(sqlSession.insert(INSERT, sqlMap(sql, args)));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    @Transactional
    @Override
    public boolean delete(String sql, Object... args) {
        SqlSession sqlSession = sqlSession();
        try {
            return SqlHelper.retBool(sqlSession.delete(DELETE, sqlMap(sql, args)));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * 获取sqlMap参数
     *
     * @param sql  指定参数的格式: {0}, {1}
     * @param args 仅支持String
     * @return ignore
     */
    private Map<String, String> sqlMap(String sql, Object... args) {
        Map<String, String> sqlMap = new HashMap<>();
        sqlMap.put(SQL, StringUtils.sqlArgsFill(sql, args));
        return sqlMap;
    }

    @Transactional
    @Override
    public boolean update(String sql, Object... args) {
        SqlSession sqlSession = sqlSession();
        try {
            return SqlHelper.retBool(sqlSession.update(UPDATE, sqlMap(sql, args)));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * 根据sql查询Map结果集
     * <p>SqlRunner.db().selectList("select * from tbl_user where name={0}", "Caratacus")</p>
     *
     * @param sql  sql语句，可添加参数，格式：{0},{1}
     * @param args 只接受String格式
     * @return ignore
     */
    @Override
    public List<Map<String, Object>> selectList(String sql, Object... args) {
        SqlSession sqlSession = sqlSession();
        try {
            return sqlSession.selectList(SELECT_LIST, sqlMap(sql, args));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * 根据sql查询一个字段值的结果集
     * <p>注意：该方法只会返回一个字段的值， 如果需要多字段，请参考{@code selectList()}</p>
     *
     * @param sql  sql语句，可添加参数，格式：{0},{1}
     * @param args 只接受String格式
     * @return ignore
     */
    @Override
    public List<Object> selectObjs(String sql, Object... args) {
        SqlSession sqlSession = sqlSession();
        try {
            return sqlSession.selectList(SELECT_OBJS, sqlMap(sql, args));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * 根据sql查询一个字段值的一条结果
     * <p>注意：该方法只会返回一个字段的值， 如果需要多字段，请参考{@code selectOne()}</p>
     *
     * @param sql  sql语句，可添加参数，格式：{0},{1}
     * @param args 只接受String格式
     * @return ignore
     */
    @Override
    public Object selectObj(String sql, Object... args) {
        return SqlHelper.getObject(log, selectObjs(sql, args));
    }

    @Override
    public int selectCount(String sql, Object... args) {
        SqlSession sqlSession = sqlSession();
        try {
            return SqlHelper.retCount(sqlSession.<Integer>selectOne(COUNT, sqlMap(sql, args)));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    @Override
    public Map<String, Object> selectOne(String sql, Object... args) {
        return SqlHelper.getObject(log, selectList(sql, args));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public IPage<Map<String, Object>> selectPage(IPage page, String sql, Object... args) {
        if (null == page) {
            return null;
        }
        // TODO 待完成
        //  page.setRecords(sqlSession().selectList(SELECT_LIST, sqlMap(sql, args), page));
        return page;
    }

    /**
     * 获取Session 默认自动提交
     */
    private SqlSession sqlSession() {
        return (clazz != null) ? SqlSessionUtils.getSqlSession(GlobalConfigUtils.currentSessionFactory(clazz)) : SqlSessionUtils.getSqlSession(sqlSessionFactory);
    }

    /**
     * 释放sqlSession
     *
     * @param sqlSession session
     */
    private void closeSqlSession(SqlSession sqlSession) {
        SqlSessionFactory sqlSessionFactory;
        if (clazz != null) {
            sqlSessionFactory = GlobalConfigUtils.currentSessionFactory(clazz);
        } else {
            sqlSessionFactory = DEFAULT.sqlSessionFactory;
        }
        SqlSessionUtils.closeSqlSession(sqlSession, sqlSessionFactory);
    }
}
