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
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionUtils;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;

/**
 * <p>
 * SQL 辅助类
 * </p>
 *
 * @author hubin
 * @since 2016-11-06
 */
public final class SqlHelper {

    private static final Log logger = LogFactory.getLog(SqlHelper.class);
    public static SqlSessionFactory FACTORY;


    /**
     * <p>
     * 批量操作 SqlSession
     * </p>
     *
     * @param clazz 实体类
     * @return SqlSession
     */
    public static SqlSession sqlSessionBatch(Class<?> clazz) {
        //todo 暂时让能用先,但日志会显示Closing non transactional SqlSession,因为这个并没有绑定.
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
        return SqlSessionUtils.getSqlSession(GlobalConfigUtils.currentSessionFactory(clazz));
    }

    /**
     * <p>
     * 获取Session
     * </p>
     *
     * @param clazz 实体类
     * @return SqlSession
     */
    public static SqlSession sqlSession(Class<?> clazz) {
        return SqlHelper.getSqlSession(clazz);
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
        Assert.notNull(tableInfo, "Error: Cannot execute table Method, ClassGenricType not found .");
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
                logger.warn(String.format("Warn: execute Method There are  %s results.", size));
            }
            return list.get(0);
        }
        return null;
    }
}
