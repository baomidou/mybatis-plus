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
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
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
     * Key stored in {@link BoundSql#setAdditionalParameter} to mark that this interceptor has
     * already rewritten the SQL in {@link #beforeQuery}.  The flag prevents a second rewrite in
     * {@link #beforePrepare} for the same main-query statement while still allowing
     * {@link #beforePrepare} to rewrite nested-select statements (used by {@code @Many} /
     * {@code @One}) that bypass the {@code Executor} interceptor chain.
     */
    private static final String DYNAMIC_TABLE_NAME_ALREADY_PARSED = "_MP_DYNAMIC_TABLE_NAME_ALREADY_PARSED";

    /**
     * 回调处理
     */
    private Runnable hook;

    /**
     * 表名处理器，是否 处理表名的情况都在该处理器中自行判断
     */
    private TableNameHandler tableNameHandler = (sql, tableName) -> sql;

    /**
     * 默认构建
     *
     * @see #DynamicTableNameInnerInterceptor(TableNameHandler)
     * @deprecated 3.5.11
     */
    @Deprecated
    public DynamicTableNameInnerInterceptor() {
    }

    public DynamicTableNameInnerInterceptor(TableNameHandler tableNameHandler) {
        this.tableNameHandler = tableNameHandler;
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) return;
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        mpBs.sql(this.changeTable(mpBs.sql()));
        // Mark the BoundSql so that beforePrepare can detect it was already processed
        // and skip the rewrite, avoiding double processing for the same query.
        boundSql.setAdditionalParameter(DYNAMIC_TABLE_NAME_ALREADY_PARSED, Boolean.TRUE);
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.SELECT) {
            // Nested selects from @Many / @One bypass the Executor interceptor chain and therefore
            // never reach beforeQuery.  Handle them here so the table-name replacement is applied.
            // Skip if beforeQuery already rewrote the SQL for the same BoundSql instance.
            if (InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
                return;
            }
            BoundSql boundSql = mpSh.boundSql();
            if (!boundSql.hasAdditionalParameter(DYNAMIC_TABLE_NAME_ALREADY_PARSED)) {
                PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
                mpBs.sql(this.changeTable(mpBs.sql()));
            }
        } else if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            if (InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
                return;
            }
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            mpBs.sql(this.changeTable(mpBs.sql()));
        }
    }

    public String changeTable(String sql) {
        try {
            return processTableName(sql);
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
     * @return 处理完的sql
     * @since 3.5.11
     */
    protected String processTableName(String sql) {
        TableNameParser parser = new TableNameParser(sql);
        List<TableNameParser.SqlToken> names = new ArrayList<>();
        parser.accept(names::add);
        StringBuilder builder = new StringBuilder();
        int last = 0;
        for (TableNameParser.SqlToken name : names) {
            int start = name.getStart();
            if (start != last) {
                builder.append(sql, last, start);
                builder.append(tableNameHandler.dynamicTableName(sql, name.getValue()));
            }
            last = name.getEnd();
        }
        if (last != sql.length()) {
            builder.append(sql.substring(last));
        }
        return builder.toString();
    }

}
