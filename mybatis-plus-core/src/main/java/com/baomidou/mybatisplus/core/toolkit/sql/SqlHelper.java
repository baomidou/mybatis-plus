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
package com.baomidou.mybatisplus.core.toolkit.sql;

import java.util.List;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;

/**
 * <p>
 * SQL 辅助类
 * </p>
 *
 * @author hubin
 * @since 2016-11-06
 */
public class SqlHelper {

    private static final Log logger = LogFactory.getLog(SqlHelper.class);
    public static SqlSessionFactory FACTORY;


    /**
     * <p>
     * 获取Session 默认自动提交
     * </p>
     * <p>
     * 特别说明:这里获取SqlSession时这里虽然设置了自动提交但是如果事务托管了的话 是不起作用的 切记!!
     * </p>
     *
     * @return SqlSession
     */
    public static SqlSession sqlSession(Class<?> clazz) {
        return SqlHelper.sqlSession(clazz, true);
    }

    /**
     * <p>
     * 批量操作 SqlSession
     * </p>
     *
     * @param clazz 实体类
     * @return SqlSession
     */
    public static SqlSession sqlSessionBatch(Class<?> clazz) {
        return GlobalConfigUtils.currentSessionFactory(clazz).openSession(ExecutorType.BATCH);
    }

    /**
     * <p>
     * 获取sqlSession
     * </p>
     *
     * @param clazz 对象类
     * @return
     */
    private static SqlSession getSqlSession(Class<?> clazz) {
        SqlSession session = null;
        try {
            SqlSessionFactory sqlSessionFactory = GlobalConfigUtils.currentSessionFactory(clazz);
            Configuration configuration = sqlSessionFactory.getConfiguration();
            session = GlobalConfigUtils.getGlobalConfig(configuration).getSqlSession();
        } catch (Exception e) {
            // ignored
        }
        return session;
    }

    /**
     * <p>
     * 获取Session
     * </p>
     *
     * @param clazz      实体类
     * @param autoCommit true自动提交false则相反
     * @return SqlSession
     */
    public static SqlSession sqlSession(Class<?> clazz, boolean autoCommit) {
        SqlSession sqlSession = SqlHelper.getSqlSession(clazz);
        return (sqlSession != null) ? sqlSession : GlobalConfigUtils.currentSessionFactory(clazz).openSession(autoCommit);
    }

    /**
     * <p>
     * 获取TableInfo
     * </p>
     *
     * @param clazz 对象类
     * @return TableInfo 对象表信息
     */
    public static TableInfo table(Class<?> clazz) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        if (null == tableInfo) {
            throw new MybatisPlusException("Error: Cannot execute table Method, ClassGenricType not found .");
        }
        return tableInfo;
    }

    /**
     * <p>
     * 判断数据库操作是否成功
     * </p>
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    public static boolean retBool(Integer result) {
        return null != result && result >= 1;
    }

    /**
     * <p>
     * 删除不存在的逻辑上属于成功
     * </p>
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    public static boolean delBool(Integer result) {
        return null != result && result >= 0;
    }

    /**
     * <p>
     * 返回SelectCount执行结果
     * </p>
     *
     * @param result
     * @return int
     */
    public static int retCount(Integer result) {
        return (null == result) ? 0 : result;
    }

    /**
     * <p>
     * 从list中取第一条数据返回对应List中泛型的单个结果
     * </p>
     *
     * @param list
     * @param <E>
     * @return
     */
    public static <E> E getObject(List<E> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            int size = list.size();
            if (size > 1) {
                SqlHelper.logger.warn(String.format("Warn: execute Method There are  %s results.", size));
            }
            return list.get(0);
        }
        return null;
    }

    /**
     * <p>
     * 填充Wrapper
     * </p>
     *
     * @param page    分页对象
     * @param wrapper SQL包装对象
     */
    public static Wrapper<?> fillWrapper(IPage<?> page, Wrapper<?> wrapper) {
        if (null == page) {
            return wrapper;
        }
        if (ArrayUtils.isEmpty(page.ascs())
            && ArrayUtils.isEmpty(page.descs())
            && ObjectUtils.isEmpty(page.condition())) {
            return wrapper;
        }
        QueryWrapper qw;
        if (null == wrapper) {
            qw = new QueryWrapper<>();
        } else {
            qw = (QueryWrapper) wrapper;
        }
        // 排序
        if (ArrayUtils.isNotEmpty(page.ascs())) {
            qw.orderByAsc(page.ascs());
        }
        if (ArrayUtils.isNotEmpty(page.descs())) {
            qw.orderByDesc(page.descs());
        }
        // MAP 参数查询
        if (ObjectUtils.isNotEmpty(page.condition())) {
            qw.allEq(page.condition());
        }
        return qw;
    }
}
