package com.baomidou.mybatisplus.extension.plugins.chain;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author miemie
 * @since 2020-06-16
 */
@SuppressWarnings({"rawtypes"})
public interface QiuQiu {

    /**
     * @param executor      Executor(可能是代理对象)
     * @param ms            MappedStatement
     * @param parameter     parameter
     * @param rowBounds     rowBounds
     * @param resultHandler resultHandler
     * @param boundSql      boundSql
     * @return 新的 boundSql
     */
    default boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        return true;
    }

    /**
     * @param executor      Executor(可能是代理对象)
     * @param ms            MappedStatement
     * @param parameter     parameter
     * @param rowBounds     rowBounds
     * @param resultHandler resultHandler
     * @param boundSql      boundSql
     */
    default void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        // do nothing
    }

    /**
     * @param executor  Executor(可能是代理对象)
     * @param ms        MappedStatement
     * @param parameter parameter
     */
    default void update(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
        // do nothing
    }

    /**
     *
     */
    default void prepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        // do nothing
    }
}
