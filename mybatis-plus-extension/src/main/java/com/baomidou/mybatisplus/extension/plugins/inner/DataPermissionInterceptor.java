/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.update.Update;

/**
 * 数据权限处理器
 *
 * @author hubin
 * @since 3.5.2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings({"rawtypes"})
public class DataPermissionInterceptor extends BaseMultiTableInnerInterceptor implements InnerInterceptor {

    private DataPermissionHandler dataPermissionHandler;

    @SuppressWarnings("RedundantThrows")
    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (InterceptorIgnoreHelper.willIgnoreDataPermission(ms.getId())) {
            return;
        }
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        mpBs.sql(parserSingle(mpBs.sql(), ms.getId()));
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            if (InterceptorIgnoreHelper.willIgnoreDataPermission(ms.getId())) {
                return;
            }
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            mpBs.sql(parserMulti(mpBs.sql(), ms.getId()));
        }
    }

    @Override
    protected void processSelect(Select select, int index, String sql, Object obj) {
        if (dataPermissionHandler == null) {
            return;
        }
        if (dataPermissionHandler instanceof MultiDataPermissionHandler) {
            // 参照 com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor.processSelect 做的修改
            final String whereSegment = (String) obj;
            processSelectBody(select, whereSegment);
            List<WithItem> withItemsList = select.getWithItemsList();
            if (!CollectionUtils.isEmpty(withItemsList)) {
                withItemsList.forEach(withItem -> processSelectBody(withItem, whereSegment));
            }
        } else {
            // 兼容原来的旧版 DataPermissionHandler 场景
            if (select instanceof PlainSelect) {
                this.setWhere((PlainSelect) select, (String) obj);
            } else if (select instanceof SetOperationList) {
                SetOperationList setOperationList = (SetOperationList) select;
                List<Select> selectBodyList = setOperationList.getSelects();
                selectBodyList.forEach(s -> this.setWhere((PlainSelect) s, (String) obj));
            }
        }
    }

    /**
     * 设置 where 条件
     *
     * @param plainSelect  查询对象
     * @param whereSegment 查询条件片段
     */
    protected void setWhere(PlainSelect plainSelect, String whereSegment) {
        if (dataPermissionHandler == null) {
            return;
        }
        // 兼容旧版的数据权限处理
        final Expression sqlSegment = dataPermissionHandler.getSqlSegment(plainSelect.getWhere(), whereSegment);
        if (null != sqlSegment) {
            plainSelect.setWhere(sqlSegment);
        }
    }

    /**
     * update 语句处理
     */
    @Override
    protected void processUpdate(Update update, int index, String sql, Object obj) {
        final Expression sqlSegment = getUpdateOrDeleteExpression(update.getTable(), update.getWhere(), (String) obj);
        if (null != sqlSegment) {
            update.setWhere(sqlSegment);
        }
    }

    /**
     * delete 语句处理
     */
    @Override
    protected void processDelete(Delete delete, int index, String sql, Object obj) {
        final Expression sqlSegment = getUpdateOrDeleteExpression(delete.getTable(), delete.getWhere(), (String) obj);
        if (null != sqlSegment) {
            delete.setWhere(sqlSegment);
        }
    }

    protected Expression getUpdateOrDeleteExpression(final Table table, final Expression where, final String whereSegment) {
        if (dataPermissionHandler == null) {
            return null;
        }
        if (dataPermissionHandler instanceof MultiDataPermissionHandler) {
            return andExpression(table, where, whereSegment);
        } else {
            // 兼容旧版的数据权限处理
            return dataPermissionHandler.getSqlSegment(where, whereSegment);
        }
    }

    @Override
    public Expression buildTableExpression(final Table table, final Expression where, final String whereSegment) {
        if (dataPermissionHandler == null) {
            return null;
        }
        // 只有新版数据权限处理器才会执行到这里
        final MultiDataPermissionHandler handler = (MultiDataPermissionHandler) dataPermissionHandler;
        return handler.getSqlSegment(table, where, whereSegment);
    }
}
