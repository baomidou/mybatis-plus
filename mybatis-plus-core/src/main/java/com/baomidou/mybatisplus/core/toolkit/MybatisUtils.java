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
package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.handlers.IJsonTypeHandler;
import com.baomidou.mybatisplus.core.metadata.MapperProxyMetadata;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import io.quarkus.arc.ClientProxy;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.aop.framework.AopProxyUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @author nieqiurong
 * @since 3.5.6
 */
@Slf4j
@UtilityClass
public class MybatisUtils {

    /**
     * 实例化Json类型处理器
     * <p>
     * 1.子类需要包含构造(Class,Field)
     * 2.如果无上述构造或者无属性字段,则使用默认构造(Class)进行实例化
     * </p>
     *
     * @param typeHandler   类型处理器 {@link IJsonTypeHandler}
     * @param javaTypeClass java类型信息
     * @param field         属性字段
     * @return 实例化类型处理器
     */
    public static TypeHandler<?> newJsonTypeHandler(Class<? extends TypeHandler<?>> typeHandler, Class<?> javaTypeClass, Field field) {
        TypeHandler<?> result = null;
        if (IJsonTypeHandler.class.isAssignableFrom(typeHandler)) {
            if (field != null) {
                try {
                    result = typeHandler.getConstructor(Class.class, Field.class).newInstance(javaTypeClass, field);
                } catch (ReflectiveOperationException e) {
                    // ignore
                }
            }
            if (result == null) {
                try {
                    result = typeHandler.getConstructor(Class.class).newInstance(javaTypeClass);
                } catch (ReflectiveOperationException ex) {
                    throw new TypeException("Failed invoking constructor for handler " + typeHandler, ex);
                }
            }
        }
        return result;
    }

    /**
     * 获取SqlSessionFactory
     * <p>当自定义实现{@link SqlSession}时,请实现对{@link SqlSessionFactory}的访问 (spring的方式)</p>
     * <p>当无法获得{@link SqlSessionFactory}时,需要将{@link SqlSessionFactory}绑定至上下文对象中(原生mybatis访问方式)</p>
     *
     * @param mybatisMapperProxy {@link MybatisMapperProxy}
     * @return SqlSessionFactory
     * @see DefaultSqlSession
     * @see GlobalConfigUtils#getGlobalConfig(Configuration)
     * @see GlobalConfigUtils#setGlobalConfig(Configuration, GlobalConfig)
     * @since 3.5.7
     */
    public static SqlSessionFactory getSqlSessionFactory(MybatisMapperProxy<?> mybatisMapperProxy) {
        SqlSession sqlSession = mybatisMapperProxy.getSqlSession();
        return getSqlSessionFactory(sqlSession);
    }

    /**
     * 获取sqlSession中的SqlSessionFactory
     *
     * @param sqlSession sqlSession会话
     * @return SqlSessionFactory
     * @since 3.5.12
     */
    public static SqlSessionFactory getSqlSessionFactory(SqlSession sqlSession) {
        MetaObject metaObject = SystemMetaObject.forObject(sqlSession);
        String property = "sqlSessionFactory";
        if (metaObject.hasGetter(property)) {
            return (SqlSessionFactory) metaObject.getValue(property);
        }
        SqlSessionFactory sqlSessionFactory = GlobalConfigUtils.getGlobalConfig(sqlSession.getConfiguration()).getSqlSessionFactory();
        Assert.isTrue(sqlSessionFactory != null, "Please implement access to the sqlSessionFactory property or bind sqlSessionFactory to global access.");
        return sqlSessionFactory;
    }

    /**
     * 获取代理实现
     *
     * @param mapper mapper类
     * @return 代理实现
     * @since 3.5.7
     */
    public static MybatisMapperProxy<?> getMybatisMapperProxy(Object mapper) {
        if (mapper instanceof MybatisMapperProxy) {
            // fast return
            return (MybatisMapperProxy<?>) mapper;
        }
        Object result = mapper;
        //Quarkus's proxy detection
        if (result instanceof ClientProxy) {
            result = ((ClientProxy) result).arc_contextualInstance();
        }
        if (AopUtils.isLoadSpringAop()) {
            while (org.springframework.aop.support.AopUtils.isAopProxy(result)) {
                result = AopProxyUtils.getSingletonTarget(result);
            }
        }
        if (result != null) {
            while (Proxy.isProxyClass(result.getClass())) {
                result = Proxy.getInvocationHandler(result);
            }
        }
        if (result instanceof MybatisMapperProxy) {
            return (MybatisMapperProxy<?>) result;
        }
        throw new MybatisPlusException("Unable to get MybatisMapperProxy : " + mapper);
    }

    /**
     * 提取MapperProxy
     *
     * @param mapper Mapper对象
     * @return 真实Mapper对象(去除动态代理增强)
     * @since 3.5.12
     */
    public static Object extractMapperProxy(Object mapper) {
        if (mapper instanceof MybatisMapperProxy) {
            // fast return
            return mapper;
        }
        Object result = mapper;
        //Quarkus's proxy detection
        if (result instanceof ClientProxy) {
            result = ((ClientProxy) result).arc_contextualInstance();
        }
        if (AopUtils.isLoadSpringAop()) {
            while (org.springframework.aop.support.AopUtils.isAopProxy(result)) {
                result = AopProxyUtils.getSingletonTarget(result);
            }
        }
        if (result != null) {
            while (Proxy.isProxyClass(result.getClass())) {
                result = Proxy.getInvocationHandler(result);
            }
        }
        return result;
    }


    /**
     * 获取MapperProxy元数据信息
     *
     * @param mapper Mapper对象
     * @return 代理属性
     * @since 3.5.12
     */
    public static MapperProxyMetadata getMapperProxy(Object mapper) {
        Object mapperProxy = extractMapperProxy(mapper);
        MetaObject metaObject = SystemMetaObject.forObject(mapperProxy);
        return new MapperProxyMetadata(metaObject);
    }

}
