/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.spring.toolkit;

import com.baomidou.mybatisplus.core.assist.AbstractSqlRunner;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SqlRunner 执行 SQL
 * <p>
 * 自3.5.12开始,(当传入的参数是单参数时,支持使用Map,Array,List,JavaBean)
 * <li>当参数为 Map 时可通过{key}进行属性访问
 * <li>当参数为 JavaBean 时可通过{property}进行属性访问
 * <li>当参数为 List 时直接访问索引 {0} </li>
 * <li>当参数为 Array 时直接访问索引 {0} </li>
 * </p>
 *
 * @author Caratacus, nieqiurong
 * @since 2016-12-11
 */
public class SqlRunner extends AbstractSqlRunner {

    /**
     * 日志对象
     */
    private static final Log LOG = LogFactory.getLog(SqlRunner.class);

    /**
     * 默认实例 (使用{@link SqlHelper#FACTORY}进行会话操作)
     */
    public static final SqlRunner DEFAULT = new SqlRunner();

    /**
     * 实体类 (需要被扫描注入的实体对象,当未指定时,将使用{@link SqlHelper#FACTORY}进行会话操作)
     *
     * @see com.baomidou.mybatisplus.core.metadata.TableInfoHelper#initTableInfo(MapperBuilderAssistant, Class)
     */
    private Class<?> clazz;

    /**
     * 默认构造,使用{@link SqlHelper#FACTORY}进行会话操作
     */
    public SqlRunner() {
    }

    /**
     * 通过实体类构造
     *
     * @param clazz 实体类
     */
    public SqlRunner(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * 获取默认的SqlQuery(适用于单库)
     *
     * @return this
     */
    public static SqlRunner db() {
        return DEFAULT;
    }

    /**
     * 根据当前class对象获取SqlQuery(适用于多库)
     *
     * @param clazz 实体类
     * @return this
     */
    public static SqlRunner db(Class<?> clazz) {
        return new SqlRunner(clazz);
    }

    /**
     * 执行插入语句
     *
     * @param sql  指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @return 插入结果
     */
    @Override
    @Transactional
    public boolean insert(String sql, Object... args) {
        SqlSession sqlSession = sqlSession();
        try {
            return SqlHelper.retBool(sqlSession.insert(INSERT, sqlMap(sql, args)));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * 执行删除语句
     *
     * @param sql  指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @return 删除结果
     */
    @Override
    @Transactional
    public boolean delete(String sql, Object... args) {
        SqlSession sqlSession = sqlSession();
        try {
            return SqlHelper.retBool(sqlSession.delete(DELETE, sqlMap(sql, args)));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * 执行更新语句
     *
     * @param sql  指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @return 更新结果
     */
    @Override
    @Transactional
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
     * @param sql  sql语句，可添加参数，指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数列表
     * @return 结果集
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
     * <p>注意：该方法只会返回一个字段的值， 如果需要多字段，请参考{@link #selectList(String, Object...)}</p>
     *
     * @param sql  sql语句，可添加参数，指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @return 结果集
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
     * <p>注意：该方法只会返回一个字段的值， 如果需要多字段，请参考{@link #selectOne(String, Object...)}</p>
     *
     * @param sql  sql语句，可添加参数，指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @return 结果
     */
    @Override
    public Object selectObj(String sql, Object... args) {
        return SqlHelper.getObject(LOG, selectObjs(sql, args));
    }

    /**
     * 查询总数
     *
     * @param sql  sql语句，可添加参数，指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @return 总记录数
     */
    @Override
    public long selectCount(String sql, Object... args) {
        SqlSession sqlSession = sqlSession();
        try {
            return SqlHelper.retCount(sqlSession.<Long>selectOne(COUNT, sqlMap(sql, args)));
        } finally {
            closeSqlSession(sqlSession);
        }
    }

    /**
     * 获取单条记录
     *
     * @param sql  sql语句，可添加参数，指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @return 单行结果集 (当执行语句返回多条记录时,只会选取第一条记录)
     */
    @Override
    public Map<String, Object> selectOne(String sql, Object... args) {
        return SqlHelper.getObject(LOG, selectList(sql, args));
    }

    /**
     * 分页查询
     *
     * @param page 分页对象
     * @param sql  sql语句，可添加参数，指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @param <E>  E
     * @return 分页数据
     */
    @Override
    public <E extends IPage<Map<String, Object>>> E selectPage(E page, String sql, Object... args) {
        if (null == page) {
            return null;
        }
        SqlSession sqlSession = sqlSession();
        try {
            page.setRecords(sqlSession.selectList(SELECT_LIST, sqlMap(sql, page, args)));
        } finally {
            closeSqlSession(sqlSession);
        }
        return page;
    }

    /**
     * 获取Session 默认自动提交
     */
    private SqlSession sqlSession() {
        return SqlSessionUtils.getSqlSession(getSqlSessionFactory());
    }

    /**
     * 释放sqlSession
     *
     * @param sqlSession session
     */
    private void closeSqlSession(SqlSession sqlSession) {
        SqlSessionUtils.closeSqlSession(sqlSession, getSqlSessionFactory());
    }

    /**
     * 获取SqlSessionFactory
     */
    private SqlSessionFactory getSqlSessionFactory() {
        return Optional.ofNullable(clazz).map(GlobalConfigUtils::currentSessionFactory).orElse(SqlHelper.FACTORY);
    }

    /**
     * @deprecated 3.5.3.2
     */
    @Deprecated
    public void close() {

    }

}
