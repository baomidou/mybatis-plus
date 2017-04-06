/**
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
package com.baomidou.mybatisplus.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * SqlRunner 执行 SQL
 * </p>
 *
 * @author Caratacus
 * @Date 2016-12-11
 */
public class SqlRunner {

    public static final String INSERT = "com.baomidou.mybatisplus.mapper.SqlRunner.Insert";
    public static final String DELETE = "com.baomidou.mybatisplus.mapper.SqlRunner.Delete";
    public static final String UPDATE = "com.baomidou.mybatisplus.mapper.SqlRunner.Update";
    public static final String SELECT_LIST = "com.baomidou.mybatisplus.mapper.SqlRunner.SelectList";
    public static final String SELECT_OBJS = "com.baomidou.mybatisplus.mapper.SqlRunner.SelectObjs";
    public static final String COUNT = "com.baomidou.mybatisplus.mapper.SqlRunner.Count";
    public static final String SQLScript = "${sql}";
    public static final String SQL = "sql";
    // 单例Query
    public static final SqlRunner DEFAULT = new SqlRunner();
    // 默认FACTORY
    public static SqlSessionFactory FACTORY;
    private SqlSessionFactory sqlSessionFactory;

    private Class<?> clazz;

    public SqlRunner() {
        this.sqlSessionFactory = FACTORY;
    }

    public SqlRunner(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * 获取默认的SqlQuery(适用于单库)
     *
     * @return
     */
    public static SqlRunner db() {
        // 初始化的静态变量 还是有前后加载的问题 该判断只会执行一次
        if (DEFAULT.sqlSessionFactory == null) {
            DEFAULT.sqlSessionFactory = FACTORY;
        }
        return DEFAULT;
    }

    /**
     * 根据当前class对象获取SqlQuery(适用于多库)
     *
     * @param clazz
     * @return
     */
    public static SqlRunner db(Class<?> clazz) {
        return new SqlRunner(clazz);
    }

    @Transactional
    public boolean insert(String sql, Object... args) {
        return SqlHelper.retBool(sqlSession().insert(INSERT, sqlMap(sql, args)));
    }

    @Transactional
    public boolean delete(String sql, Object... args) {
        return SqlHelper.retBool(sqlSession().delete(DELETE, sqlMap(sql, args)));
    }

    /**
     * 获取sqlMap参数
     *
     * @param sql
     * @param args
     * @return
     */
    private Map<String, String> sqlMap(String sql, Object... args) {
        Map<String, String> sqlMap = new HashMap<>();
        sqlMap.put(SQL, StringUtils.sqlArgsFill(sql, args));
        return sqlMap;
    }

    @Transactional
    public boolean update(String sql, Object... args) {
        return SqlHelper.retBool(sqlSession().update(UPDATE, sqlMap(sql, args)));
    }

    public List<Map<String, Object>> selectList(String sql, Object... args) {
        return sqlSession().selectList(SELECT_LIST, sqlMap(sql, args));
    }

    public List<Object> selectObjs(String sql, Object... args) {
        return sqlSession().selectList(SELECT_OBJS, sqlMap(sql, args));
    }

    public Object selectObj(String sql, Object... args) {
        return SqlHelper.getObject(selectObjs(sql, args));
    }

    public int selectCount(String sql, Object... args) {
        return SqlHelper.retCount(sqlSession().<Integer>selectOne(COUNT, sqlMap(sql, args)));
    }

    public Map<String, Object> selectOne(String sql, Object... args) {
        return SqlHelper.getObject(selectList(sql, args));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Page<Map<String, Object>> selectPage(Page page, String sql, Object... args) {
        if (null == page) {
            return null;
        }
        page.setRecords(sqlSession().selectList(SELECT_LIST, sqlMap(sql, args), page));
        return page;
    }

    /**
     * <p>
     * 获取Session 默认自动提交
     * <p/>
     */
    private SqlSession sqlSession() {
        return (clazz != null) ? SqlHelper.sqlSession(clazz) : GlobalConfiguration.getSqlSession(FACTORY.getConfiguration());
    }

}
