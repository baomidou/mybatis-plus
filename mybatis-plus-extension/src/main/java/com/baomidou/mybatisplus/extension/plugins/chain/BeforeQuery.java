package com.baomidou.mybatisplus.extension.plugins.chain;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;

/**
 * @author miemie
 * @since 2020-06-16
 */
public interface BeforeQuery {

    /**
     * 拦截 Executor.query 执行前对执行sql进行处理
     *
     * @param executor      Executor(可能是代理对象)
     * @param ms            MappedStatement
     * @param parameter     parameter
     * @param rowBounds     rowBounds
     * @param resultHandler resultHandler
     * @param boundSql      boundSql
     * @return 新的 boundSql
     */
    default boolean canChange(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        return true;
    }

    /**
     * 拦截 Executor.query 执行前对执行sql进行处理
     *
     * @param executor      Executor(可能是代理对象)
     * @param ms            MappedStatement
     * @param parameter     parameter
     * @param rowBounds     rowBounds
     * @param resultHandler resultHandler
     * @param boundSql      boundSql
     * @return 新的 boundSql
     */
    BoundSql change(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException;
}
