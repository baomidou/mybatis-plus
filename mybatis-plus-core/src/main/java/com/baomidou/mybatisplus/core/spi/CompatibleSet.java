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
package com.baomidou.mybatisplus.core.spi;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.InputStream;
import java.util.function.Function;

/**
 * Web 开发平台待兼容方法集接口类
 */
public interface CompatibleSet {

    /**
     * 获取实体类关联的配置中绑定的会话工厂。
     *
     * @param entity 实体类
     * @return 已绑定的会话工厂，未绑定时返回 null
     */
    default SqlSessionFactory getSqlSessionFactory(Class<?> entity) {
        return GlobalConfigUtils.getGlobalConfig(getConfiguration(entity)).getSqlSessionFactory();
    }

    /**
     * 获取实体类当前缓存的表元数据所关联的配置。表元数据尚未初始化时抛出异常。
     *
     * @param entity 实体类
     * @return 表元数据关联的配置
     */
    default Configuration getConfiguration(Class<?> entity) {
        Assert.notNull(entity, "Class must not be null");
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entity);
        Assert.notNull(tableInfo, ClassUtils.getUserClass(entity).getName() + " Not Found TableInfoCache.");
        return tableInfo.getConfiguration();
    }

    /**
     * 通过实体类关联的会话工厂获取会话，沿用兼容实现的会话管理方式。
     *
     * @param entity 实体类
     * @return SQL 会话
     */
    default SqlSession getSqlSession(Class<?> entity) {
        return getSqlSession(getSqlSessionFactory(entity));
    }

    default SqlSession getSqlSession(SqlSessionFactory sessionFactory) {
        return sessionFactory.openSession();
    }

    /**
     * 在框架管理的操作成功后提交独立会话。由外部事务管理器管理的会话交给事务管理器提交。
     *
     * @param session 本次操作使用的会话
     * @param sessionFactory 会话所属的工厂
     */
    default void commitSqlSession(SqlSession session, SqlSessionFactory sessionFactory) {
        session.commit();
    }

    default void closeSqlSession(SqlSession session, SqlSessionFactory sessionFactory) {
        if (session != null) {
            session.close();
        }
    }

    default boolean executeBatch(SqlSessionFactory sqlSessionFactory, Log log, Function<SqlSession, Integer> execFunc) {
        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            Integer affected = execFunc.apply(session);
            session.commit();
            return affected != null && affected > 0;
        }
    }

    /**
     * @deprecated 3.5.12 无需实现
     */
    @Deprecated
    default InputStream getInputStream(String path) throws Exception {
        return null;
    }

    /**
     * 获取容器bean实例
     *
     * @param clz 类型
     * @return bean实例 (当无实例时返回null)
     * @since 3.5.12
     */
    default <T> T getBean(Class<T> clz) {
        return null;
    }

    /**
     * 获取真实被代理的对象 (如果没有被代理,请返回原始对象)
     *
     * @param mapper Mapper对象
     * @return 真实对象
     * @since 3.5.12
     */
    default Object getProxyTargetObject(Object mapper) {
        return null;
    }

    /**
     * 传递上下文对象
     * 
     * @param context 容器上下文
     * @since 3.5.13
     */
    default void setContext(Object context) {

    }

    /**
     * 返回值越大，SPI 实现的优先级越高。
     */
    default int order() {
        return 0;
    }

}
