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

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionHolder;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

/**
 * SQL 辅助类
 *
 * @author hubin
 * @since 2016-11-06
 */
public final class SqlHelper {

    /**
     * 主要用于 service 和 ar
     */
    public static SqlSessionFactory FACTORY;

    /**
     * 批量操作 SqlSession
     *
     * @param clazz 实体类
     * @return SqlSession
     */
    public static SqlSession sqlSessionBatch(Class<?> clazz) {
        // TODO 暂时让能用先,但日志会显示Closing non transactional SqlSession,因为这个并没有绑定.
        return sqlSessionFactory(clazz).openSession(ExecutorType.BATCH);
    }

    /**
     * 获取SqlSessionFactory
     *
     * @param clazz 实体类
     * @return SqlSessionFactory
     * @since 3.3.0
     */
    public static SqlSessionFactory sqlSessionFactory(Class<?> clazz) {
        return GlobalConfigUtils.currentSessionFactory(clazz);
    }

    /**
     * 获取Session
     *
     * @param clazz 实体类
     * @return SqlSession
     */
    public static SqlSession sqlSession(Class<?> clazz) {
        return SqlSessionUtils.getSqlSession(GlobalConfigUtils.currentSessionFactory(clazz));
    }

    /**
     * 获取TableInfo
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
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    public static boolean retBool(Integer result) {
        return null != result && result >= 1;
    }

    /**
     * 返回SelectCount执行结果
     *
     * @param result ignore
     * @return int
     */
    public static int retCount(Integer result) {
        return (null == result) ? 0 : result;
    }

    /**
     * 从list中取第一条数据返回对应List中泛型的单个结果
     *
     * @param list ignore
     * @param <E>  ignore
     * @return ignore
     */
    public static <E> E getObject(Log log, List<E> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            int size = list.size();
            if (size > 1) {
                log.warn(String.format("Warn: execute Method There are  %s results.", size));
            }
            return list.get(0);
        }
        return null;
    }

    /**
     * 清理缓存.
     * 批量插入因为无法重用sqlSession，只能新开启一个sqlSession
     *
     * @param clazz 实体类
     * @deprecated 3.3.1
     */
    @Deprecated
    public static void clearCache(Class<?> clazz) {
        SqlSessionFactory sqlSessionFactory = GlobalConfigUtils.currentSessionFactory(clazz);
        SqlSessionHolder sqlSessionHolder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(sqlSessionFactory);
        if (sqlSessionHolder != null) {
            SqlSession sqlSession = sqlSessionHolder.getSqlSession();
            sqlSession.clearCache();
        }
    }
}
