/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.plugins.inner;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author <a href="mailto:xyz327@outlook.com">xizhou</a>
 * @since 2021/6/2 11:04 上午
 */
@SuppressWarnings({"rawtypes"})
public interface PostInnerInterceptor extends InnerInterceptor {

    /**
     * {@link this#beforeQuery(Executor, MappedStatement, Object, RowBounds, ResultHandler, BoundSql)} 整体完成之后的处理
     *
     * @param executor
     * @param ms
     * @param parameter
     * @param rowBounds
     * @param resultHandler
     * @param boundSql
     * @throws SQLException
     */
    default void afterQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
    }

    /**
     * {@link this#beforeUpdate(Executor, MappedStatement, Object)} 整体完成之后的处理
     *
     * @param executor
     * @param ms
     * @param parameter
     */
    default void afterUpdate(Executor executor, MappedStatement ms, Object parameter) {
    }

    /**
     * {@link this#beforeGetBoundSql(StatementHandler)} 整体完成之后的处理
     *
     * @param sh
     */
    default void afterGetBoundSql(StatementHandler sh) {
    }

    /**
     * {@link this#beforePrepare(StatementHandler, Connection, Integer)} 整体完成之后的处理
     *
     * @param sh
     * @param connections
     * @param transactionTimeout
     */
    default void afterPrepare(StatementHandler sh, Connection connections, Integer transactionTimeout) {
    }

    /**
     * 整个拦截执行完成之后的处理
     */
    default void afterCompletion() {
    }
}
