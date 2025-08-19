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
package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.TableNameParser;
import com.baomidou.mybatisplus.extension.plugins.handler.QueryParameterWrapper;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandlerFactory;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态表名
 *
 * @author jobob
 * @since 3.4.0
 */
@Getter
@Setter
@SuppressWarnings({"rawtypes"})
public class DynamicTableNameInnerInterceptor implements InnerInterceptor {

    /**
     * 回调处理
     */
    private Runnable hook;

    /**
     * 表名处理器，是否 处理表名的情况都在该处理器中自行判断
     */
    @Deprecated
    private TableNameHandler tableNameHandler = (sql, tableName) -> sql;
    /**
     * 表名处理器Factory，在实现中决定是静态处理, 还是每个handler都携带捕获到的BoundSql中的参数
     */
    private TableNameHandlerFactory tableNameHandlerFactory = null;
    /**
     * 默认构建
     *
     * @see #DynamicTableNameInnerInterceptor(TableNameHandler)
     * @deprecated 3.5.11
     */
    @Deprecated
    public DynamicTableNameInnerInterceptor() {
    }

    @Deprecated
    public DynamicTableNameInnerInterceptor(TableNameHandler tableNameHandler) {
        this.tableNameHandler = tableNameHandler;
    }

    public DynamicTableNameInnerInterceptor(TableNameHandlerFactory tableNameHandlerFactory) {
        this.tableNameHandlerFactory = tableNameHandlerFactory;
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) return;
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        //兼容直接使用类内静态tableNameHandler
        TableNameHandler handler = tableNameHandlerFactory != null ?
            tableNameHandlerFactory.createContextTableNameHandler(QueryParameterWrapper.createWrapper(mpBs))
            : this.tableNameHandler;
        mpBs.sql(this.changeTable(mpBs.sql(), handler));
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            if (InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
                return;
            }
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            //兼容直接使用类内静态tableNameHandler
            TableNameHandler handler = tableNameHandlerFactory != null ?
                tableNameHandlerFactory.createContextTableNameHandler(QueryParameterWrapper.createWrapper(mpBs))
                : this.tableNameHandler;
            mpBs.sql(this.changeTable(mpBs.sql(), handler));
        }
    }

    public String changeTable(String sql, TableNameHandler handler) {
        try {
            return processTableName(sql, handler);
        } finally {
            if (hook != null) {
                hook.run();
            }
        }
    }

    /**
     * 处理表名解析替换
     *
     * @param sql 原始sql
     * @param handler 表名处理器
     * @return 处理完的sql
     * @since 3.5.11
     */
    protected String processTableName(String sql, TableNameHandler handler) {
        TableNameParser parser = new TableNameParser(sql);
        List<TableNameParser.SqlToken> names = new ArrayList<>();
        parser.accept(names::add);
        StringBuilder builder = new StringBuilder();
        int last = 0;
        for (TableNameParser.SqlToken name : names) {
            int start = name.getStart();
            if (start != last) {
                builder.append(sql, last, start);
                builder.append(handler.dynamicTableName(sql, name.getValue()));
            }
            last = name.getEnd();
        }
        if (last != sql.length()) {
            builder.append(sql.substring(last));
        }
        return builder.toString();
    }

}
