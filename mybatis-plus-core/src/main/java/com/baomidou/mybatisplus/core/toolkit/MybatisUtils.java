package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.handlers.IJsonTypeHandler;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
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
     *
     * @param mybatisMapperProxy {@link MybatisMapperProxy}
     * @return SqlSessionFactory
     * @since 3.5.7
     */
    public static SqlSessionFactory getSqlSessionFactory(MybatisMapperProxy<?> mybatisMapperProxy) {
        SqlSession sqlSession = mybatisMapperProxy.getSqlSession();
        if (sqlSession instanceof DefaultSqlSession) {
            // TODO 原生mybatis下只能这样了.
            return GlobalConfigUtils.getGlobalConfig(mybatisMapperProxy.getSqlSession().getConfiguration()).getSqlSessionFactory();
        }
        Field declaredField;
        try {
            declaredField = sqlSession.getClass().getDeclaredField("sqlSessionFactory");
            declaredField.setAccessible(true);
            return (SqlSessionFactory) declaredField.get(sqlSession);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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

}
