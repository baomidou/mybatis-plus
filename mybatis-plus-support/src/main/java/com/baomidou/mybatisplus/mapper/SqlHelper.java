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

import java.util.List;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;
import org.mybatis.spring.SqlSessionUtils;

/**
 * <p>
 * SQL 辅助类
 * </p>
 *
 * @author hubin
 * @Date 2016-11-06
 */
public class SqlHelper {

    private static final Log logger = LogFactory.getLog(SqlHelper.class);

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
        SqlSessionFactory sqlSessionFactory = GlobalConfigUtils.currentSessionFactory(clazz);
        return SqlSessionUtils.getSqlSession(sqlSessionFactory);
    }

    /**
     * <p>
     * 获取Session
     * </p>
     *
     * @param clazz      实体类
     * @return SqlSession
     */
    public static SqlSession sqlSession(Class<?> clazz) {
        return getSqlSession(clazz);
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
                logger.warn(String.format("Warn: execute Method There are  %s results.", size));
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
    public static Wrapper<?> fillWrapper(Page<?> page, Wrapper<?> wrapper) {
        if (null == page) {
            return wrapper;
        }
        // wrapper 不存创建一个 Condition
        if (isEmptyOfWrapper(wrapper)) {
            wrapper = Condition.create();
        }
        // 排序 fixed gitee issues/IHF7N
        if (page.isOpenSort() && page.isSearchCount()) {
            wrapper.orderAsc(page.getAscs());
            wrapper.orderDesc(page.getDescs());
        }
        // MAP 参数查询
        if (MapUtils.isNotEmpty(page.getCondition())) {
            wrapper.allEq(page.getCondition());
        }
        return wrapper;
    }

    /**
     * <p>
     * 判断Wrapper为空
     * </p>
     *
     * @param wrapper SQL包装对象
     * @return
     */
    public static boolean isEmptyOfWrapper(Wrapper<?> wrapper) {
        return null == wrapper || Condition.EMPTY == wrapper;
    }

    /**
     * <p>
     * 判断Wrapper不为空
     * </p>
     *
     * @param wrapper SQL包装对象
     * @return
     */
    public static boolean isNotEmptyOfWrapper(Wrapper<?> wrapper) {
        return !isEmptyOfWrapper(wrapper);
    }
}
