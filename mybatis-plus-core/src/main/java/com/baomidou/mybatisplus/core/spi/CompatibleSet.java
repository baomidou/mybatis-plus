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

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.InputStream;
import java.util.function.Consumer;

/**
 * Web 开发平台待兼容方法集接口类
 */
public interface CompatibleSet {

    SqlSession getSqlSession(SqlSessionFactory sessionFactory);

    void closeSqlSession(SqlSession session, SqlSessionFactory sessionFactory);

    boolean executeBatch(SqlSessionFactory sqlSessionFactory, Log log, Consumer<SqlSession> consumer);

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
     * @param context 容器上下文
     * @since 3.5.13
     */
    default void setContext(Object context) {

    }

}
