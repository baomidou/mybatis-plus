package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

/**
 * 重写 SqlSession
 *
 * @author NanCheung
 */
public class SqlSessionProxy implements InvocationHandler {

    public static final String METHOD_CLOSE = "close";

    private final SqlSession sqlSession;
    private final SqlSessionFactory sqlSessionFactory;

    public SqlSessionProxy(Class<?> clazz, SqlSessionFactory defaultSqlSessionFactory) {
        Assert.isTrue(clazz != null || defaultSqlSessionFactory != null, "Error: At least one of clazz and sqlSessionFactory is cannot be empty");
        this.sqlSessionFactory = getSqlSessionFactory(clazz, defaultSqlSessionFactory);
        this.sqlSession = SqlSessionUtils.getSqlSession(sqlSessionFactory);
    }

    public SqlSessionProxy(Class<?> clazz) {
        Assert.isTrue(clazz != null , "Error: Cannot be empty for clazz");
        this.sqlSessionFactory = getSqlSessionFactory(clazz, null);
        this.sqlSession = SqlSessionUtils.getSqlSession(sqlSessionFactory);
    }

    public SqlSession newInstance() {
        return (SqlSession) Proxy.newProxyInstance(SqlSession.class.getClassLoader(), new Class[]{SqlSession.class}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }

        // 重写close方法，以支持AutoCloseable
        if (METHOD_CLOSE.equals(method.getName())) {
            close();
            return null;
        }

        return method.invoke(sqlSession, args);
    }

    private void close() {
        SqlSessionUtils.closeSqlSession(sqlSession, sqlSessionFactory);
    }

    /**
     * 获取SqlSessionFactory
     */
    private SqlSessionFactory getSqlSessionFactory(Class<?> clazz, SqlSessionFactory defaultSqlSessionFactory) {
        return Optional.ofNullable(clazz).map(GlobalConfigUtils::currentSessionFactory).orElse(defaultSqlSessionFactory);
    }
}
