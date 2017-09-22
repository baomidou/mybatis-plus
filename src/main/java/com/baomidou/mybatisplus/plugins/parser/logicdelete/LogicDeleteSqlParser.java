/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.plugins.parser.logicdelete;

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.plugins.parser.AbstractJsqlParser;
import com.baomidou.mybatisplus.plugins.parser.SqlInfo;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;

import java.util.List;

/**
 * <p>
 * 租户 SQL 解析
 * </p>
 *
 * @author hubin
 * @since 2017-09-01
 */
public class LogicDeleteSqlParser extends AbstractJsqlParser {

    private LogicDeleteHandler logicDeleteHandler;

    @Override
    public SqlInfo processParser(Statement statement) {
        if (statement instanceof Select) {
            this.processSelectBody(((Select) statement).getSelectBody());
        } else if (statement instanceof Update) {
            this.processUpdate((Update) statement);
        }
        logger.debug("parser sql: " + statement.toString());
        return SqlInfo.newInstance().setSql(statement.toString());
    }

    /**
     * select 语句处理
     */
    protected void processSelectBody(SelectBody selectBody) {
        if (selectBody instanceof PlainSelect) {
            processPlainSelect((PlainSelect) selectBody);
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            if (withItem.getSelectBody() != null) {
                processSelectBody(withItem.getSelectBody());
            }
        } else {
            SetOperationList operationList = (SetOperationList) selectBody;
            if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
                List<SelectBody> plainSelects = operationList.getSelects();
                for (SelectBody plainSelect : plainSelects) {
                    processSelectBody(plainSelect);
                }
            }
        }
    }

    /**
     * <p>
     * update 语句处理
     * </p>
     */
    protected void processUpdate(Update update) {
        List<Table> tableList = update.getTables();
        if (null == tableList || tableList.size() >= 2) {
            throw new MybatisPlusException("Failed to process multiple-table update, please exclude the statementId");
        }
        Table table = tableList.get(0);
        if (this.logicDeleteHandler.doTableFilter(table.getName())) {
            // 过滤退出执行
            return;
        }
        update.setWhere(this.andExpression(table, update.getWhere()));
    }

    /**
     * <p>
     * delete update 语句 where 处理
     * </p>
     */
    protected BinaryExpression andExpression(Table table, Expression where) {
        String tableName = table.getName();
        //获得where条件表达式
        EqualsTo equalsTo = new EqualsTo();
        if (where instanceof BinaryExpression) {
            equalsTo.setLeftExpression(new Column(this.logicDeleteHandler.getColumn(tableName)));
            equalsTo.setRightExpression(logicDeleteHandler.getValue(tableName));
            return new AndExpression(where, equalsTo);
        }
        equalsTo.setLeftExpression(this.getAliasColumn(table));
        equalsTo.setRightExpression(logicDeleteHandler.getValue(tableName));
        return equalsTo;
    }

    /**
     * <p>
     * 处理 PlainSelect
     * </p>
     */
    protected void processPlainSelect(PlainSelect plainSelect) {
        processPlainSelect(plainSelect, false);
    }

    /**
     * <p>
     * 处理 PlainSelect
     * </p>
     *
     * @param plainSelect
     * @param addColumn   是否添加租户列,insert into select语句中需要
     */
    protected void processPlainSelect(PlainSelect plainSelect, boolean addColumn) {
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            Table fromTable = (Table) fromItem;
            if (this.logicDeleteHandler.doTableFilter(fromTable.getName())) {
                // 过滤退出执行
                return;
            }
            plainSelect.setWhere(builderExpression(plainSelect.getWhere(), fromTable));
            if (addColumn) {
                plainSelect.getSelectItems().add(new SelectExpressionItem(new Column(this.logicDeleteHandler.getColumn(fromTable.getName()))));
            }
        } else {
            processFromItem(fromItem);
        }
        List<Join> joins = plainSelect.getJoins();
        if (joins != null && joins.size() > 0) {
            for (Join join : joins) {
                processJoin(join);
                processFromItem(join.getRightItem());
            }
        }
    }

    /**
     * 处理子查询等
     */
    protected void processFromItem(FromItem fromItem) {
        if (fromItem instanceof SubJoin) {
            SubJoin subJoin = (SubJoin) fromItem;
            if (subJoin.getJoin() != null) {
                processJoin(subJoin.getJoin());
            }
            if (subJoin.getLeft() != null) {
                processFromItem(subJoin.getLeft());
            }
        } else if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) fromItem;
            if (subSelect.getSelectBody() != null) {
                processSelectBody(subSelect.getSelectBody());
            }
        } else if (fromItem instanceof ValuesList) {
            logger.debug("Perform a subquery, if you do not give us feedback");
        } else if (fromItem instanceof LateralSubSelect) {
            LateralSubSelect lateralSubSelect = (LateralSubSelect) fromItem;
            if (lateralSubSelect.getSubSelect() != null) {
                SubSelect subSelect = lateralSubSelect.getSubSelect();
                if (subSelect.getSelectBody() != null) {
                    processSelectBody(subSelect.getSelectBody());
                }
            }
        }
    }

    /**
     * 处理联接语句
     */
    protected void processJoin(Join join) {
        if (join.getRightItem() instanceof Table) {
            Table fromTable = (Table) join.getRightItem();
            if (this.logicDeleteHandler.doTableFilter(fromTable.getName())) {
                // 过滤退出执行
                return;
            }
            join.setOnExpression(builderExpression(join.getOnExpression(), fromTable));
        }
    }

    /**
     * 处理条件
     */
    protected Expression builderExpression(Expression expression, Table table) {
        //生成字段名
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(this.getAliasColumn(table));
        equalsTo.setRightExpression(logicDeleteHandler.getValue(table.getName()));
        //加入判断防止条件为空时生成 "and null" 导致查询结果为空
        if (expression == null) {
            return equalsTo;
        } else {
            if (expression instanceof BinaryExpression) {
                BinaryExpression binaryExpression = (BinaryExpression) expression;
                if (binaryExpression.getLeftExpression() instanceof FromItem) {
                    processFromItem((FromItem) binaryExpression.getLeftExpression());
                }
                if (binaryExpression.getRightExpression() instanceof FromItem) {
                    processFromItem((FromItem) binaryExpression.getRightExpression());
                }
            }
            return new AndExpression(expression, equalsTo);
        }
    }

    /**
     * <p>
     * 字段是否添加别名设置
     * </p>
     *
     * @param table 表对象
     * @return 字段
     */
    protected Column getAliasColumn(Table table) {
        String tableName = table.getName();
        if (null == table.getAlias()) {
            return new Column(this.logicDeleteHandler.getColumn(tableName));
        }
        StringBuilder column = new StringBuilder();
        column.append(table.getAlias().getName());
        column.append(".");
        column.append(this.logicDeleteHandler.getColumn(tableName));
        return new Column(column.toString());
    }

    public LogicDeleteHandler getLogicDeleteHandler() {
        return logicDeleteHandler;
    }

    public void setLogicDeleteHandler(LogicDeleteHandler logicDeleteHandler) {
        this.logicDeleteHandler = logicDeleteHandler;
    }
}
