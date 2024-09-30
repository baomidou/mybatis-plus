package com.baomidou.mybatisplus.core.override;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.Method;

public class SolonMybatisMapperProxy<T> extends MybatisMapperProxy<T> {

    private final SqlSessionFactory factory;
    private final Class<T> mapperInterface;
    private final SqlSession sqlSession;

    public SolonMybatisMapperProxy(SqlSessionFactory sqlSessionFactory, SqlSession sqlSession, Class<T> mapperInterface) {
        super(null, mapperInterface, null);
        this.factory = sqlSessionFactory;
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public SqlSession getSqlSession() {
        return sqlSession;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try (SqlSession session = factory.openSession(true)) {
            Object mapper = session.getMapper(mapperInterface);
            return method.invoke(mapper, args);
        }
    }
}
