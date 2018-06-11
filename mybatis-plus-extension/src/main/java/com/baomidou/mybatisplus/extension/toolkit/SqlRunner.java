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
package com.baomidou.mybatisplus.extension.toolkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.assist.ISqlRunner;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlHelper;

/**
 * <p>
 * SqlRunner 执行 SQL
 * </p>
 *
 * @author Caratacus
 * @since 2016-12-11
 */
public class SqlRunner implements ISqlRunner {

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
     * <p>
     * 获取默认的SqlQuery(适用于单库)
     * </p>
     *
     * @return
     */
    public static SqlRunner db() {
        // 初始化的静态变量 还是有前后加载的问题 该判断只会执行一次
        if (DEFAULT.sqlSessionFactory == null) {
            DEFAULT.sqlSessionFactory = SqlHelper.FACTORY;
        }
        return DEFAULT;
    }

    /**
     * <p>
     * 根据当前class对象获取SqlQuery(适用于多库)
     * </p>
     *
     * @param clazz
     * @return
     */
    public static SqlRunner db(Class<?> clazz) {
        return new SqlRunner(clazz);
    }

    @Transactional
    @Override
    public boolean insert(String sql, Object... args) {
        return SqlHelper.retBool(sqlSession().insert(INSERT, sqlMap(sql, args)));
    }

    @Transactional
    @Override
    public boolean delete(String sql, Object... args) {
        return SqlHelper.retBool(sqlSession().delete(DELETE, sqlMap(sql, args)));
    }

    /**
     * 获取sqlMap参数
     *
     * @param sql  指定参数的格式: {0}, {1}
     * @param args 仅支持String
     * @return
     */
    private Map<String, String> sqlMap(String sql, Object... args) {
        Map<String, String> sqlMap = new HashMap<>();
        sqlMap.put(SQL, StringUtils.sqlArgsFill(sql, args));
        return sqlMap;
    }

    @Transactional
    @Override
    public boolean update(String sql, Object... args) {
        return SqlHelper.retBool(sqlSession().update(UPDATE, sqlMap(sql, args)));
    }

    /**
     * 根据sql查询Map结果集
     * SqlRunner.db().selectList("select * from tbl_user where name={0}", "Caratacus")
     *
     * @param sql  sql语句，可添加参数，格式：{0},{1}
     * @param args 只接受String格式
     * @return
     */
    @Override
    public List<Map<String, Object>> selectList(String sql, Object... args) {
        return sqlSession().selectList(SELECT_LIST, sqlMap(sql, args));
    }

    /**
     * 根据sql查询一个字段值的结果集
     * 注意：该方法只会返回一个字段的值， 如果需要多字段，请参考{@code selectList()}
     *
     * @param sql  sql语句，可添加参数，格式：{0},{1}
     * @param args 只接受String格式
     * @return
     */
    @Override
    public List<Object> selectObjs(String sql, Object... args) {
        return sqlSession().selectList(SELECT_OBJS, sqlMap(sql, args));
    }

    /**
     * 根据sql查询一个字段值的一条结果
     * 注意：该方法只会返回一个字段的值， 如果需要多字段，请参考{@code selectOne()}
     *
     * @param sql  sql语句，可添加参数，格式：{0},{1}
     * @param args 只接受String格式
     * @return
     */
    @Override
    public Object selectObj(String sql, Object... args) {
        return SqlHelper.getObject(selectObjs(sql, args));
    }

    @Override
    public int selectCount(String sql, Object... args) {
        return SqlHelper.retCount(sqlSession().<Integer>selectOne(COUNT, sqlMap(sql, args)));
    }

    @Override
    public Map<String, Object> selectOne(String sql, Object... args) {
        return SqlHelper.getObject(selectList(sql, args));
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
     * <p>
     * 获取Session 默认自动提交
     * <p/>
     */
    private SqlSession sqlSession() {
        return (clazz != null) ? SqlHelper.sqlSession(clazz) : GlobalConfigUtils.getSqlSession(SqlHelper.FACTORY.getConfiguration());
    }

}
