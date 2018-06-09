package com.baomidou.mybatisplus.core.override;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.apache.ibatis.lang.UsesJava7;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.SqlSession;

/**
 * 替换掉引用
 * @author ming
 * @since 2018/6/9
 */
public class PageMapperProxy<T> implements InvocationHandler, Serializable {

    private static final long serialVersionUID = -6424540398559729838L;
    private final SqlSession sqlSession;
    private final Class<T> mapperInterface;
    private final Map<Method, PageMapperMethod> methodCache;

    public PageMapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, PageMapperMethod> methodCache) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
        this.methodCache = methodCache;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else if (isDefaultMethod(method)) {
                return invokeDefaultMethod(proxy, method, args);
            }
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
        final PageMapperMethod mapperMethod = cachedMapperMethod(method);
        return mapperMethod.execute(sqlSession, args);
    }

    private PageMapperMethod cachedMapperMethod(Method method) {
        PageMapperMethod mapperMethod = methodCache.get(method);
        if (mapperMethod == null) {
            mapperMethod = new PageMapperMethod(mapperInterface, method, sqlSession.getConfiguration());
            methodCache.put(method, mapperMethod);
        }
        return mapperMethod;
    }

    @UsesJava7
    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args)
        throws Throwable {
        final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
            .getDeclaredConstructor(Class.class, int.class);
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        final Class<?> declaringClass = method.getDeclaringClass();
        return constructor
            .newInstance(declaringClass,
                MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
                    | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC)
            .unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
    }

    /**
     * Backport of java.lang.reflect.Method#isDefault()
     */
    private boolean isDefaultMethod(Method method) {
        return (method.getModifiers()
            & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC
            && method.getDeclaringClass().isInterface();
    }
}
