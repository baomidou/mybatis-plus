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
package com.baomidou.mybatisplus.plugins.tenancy;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.parser.AbstractSqlParser;
import com.baomidou.mybatisplus.parser.SqlInfo;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.update.Update;

/**
 * <p>
 * 租户 SQL 解析
 * </p>
 *
 * @author hubin
 * @since 2017-06-20
 */
public class TenancySqlParser extends AbstractSqlParser {

    private TenantInfo tenantInfo;

    @Override
    public SqlInfo optimizeSql(String sql) {
        //logger.debug("old sql:{}", sql);
        Statement stmt = null;
        try {
            stmt = CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            //logger.debug("解析", e);
            //logger.error("解析sql[{}]失败\n原因:{}", sql, e.getMessage());
            //如果解析失败不进行任何处理防止业务中断
            return null;
        }
        if (stmt instanceof Insert) {
            processInsert((Insert) stmt);
        } else if (stmt instanceof Select) {
            processSelectBody(((Select) stmt).getSelectBody());
        } else if (stmt instanceof Update) {
            processUpdate((Update) stmt);
        }
        //logger.debug("new sql:{}", stmt);
        SqlInfo sqlInfo = SqlInfo.newInstance();
        sqlInfo.setSql(stmt.toString());
        return sqlInfo;
    }

    /**
     * <p>
     * select 语句处理
     * </p>
     */
    public void processSelectBody(SelectBody selectBody) {
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
     * insert 语句处理
     * </p>
     */
    public void processInsert(Insert insert) {
        if (doTableFilter(
                insert.getTable().getName()
        )) {
            insert.getColumns().add(new Column(this.tenantInfo.getTenantIdColumn()));
            if (insert.getSelect() != null) {
                processPlainSelect((PlainSelect) insert.getSelect().getSelectBody(), true);
            } else if (insert.getItemsList() != null) {
                ((ExpressionList) insert.getItemsList()).getExpressions().add(new StringValue("," + this.tenantInfo.getTenantId() + ","));
            } else {
                //
                throw new RuntimeException("无法处理的 sql");
            }
        }
    }

    /**
     * <p>
     * update 语句处理
     * </p>
     */
    public void processUpdate(Update update) {
        //获得where条件表达式
        Expression where = update.getWhere();
        EqualsTo equalsTo = new EqualsTo();
        if (where instanceof BinaryExpression) {
            equalsTo.setLeftExpression(new Column(this.tenantInfo.getTenantIdColumn()));
            equalsTo.setRightExpression(new StringValue("," + tenantInfo.getTenantId() + ","));
            AndExpression andExpression = new AndExpression(equalsTo, where);
            update.setWhere(andExpression);
        } else {
            equalsTo.setLeftExpression(new Column(this.tenantInfo.getTenantIdColumn()));
            equalsTo.setRightExpression(new StringValue("," + tenantInfo.getTenantId() + ","));
            update.setWhere(equalsTo);
        }
    }


    /**
     * <p>
     * delete 语句处理
     * </p>
     */
    public void processDelete(Delete delete) {

    }

    /**
     * 处理PlainSelect
     */
    public void processPlainSelect(PlainSelect plainSelect) {
        processPlainSelect(plainSelect, false);
    }

    /**
     * 处理PlainSelect
     *
     * @param plainSelect
     * @param addColumn   是否添加租户列,insert into select语句中需要
     */

    public void processPlainSelect(PlainSelect plainSelect, boolean addColumn) {
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            Table fromTable = (Table) fromItem;
            if (doTableFilter(fromTable.getName())) {
                plainSelect.setWhere(builderExpression(plainSelect.getWhere(), fromTable));
                if (addColumn)
                    plainSelect.getSelectItems().add(new SelectExpressionItem(new Column("'" + this.tenantInfo.getTenantId() + "'")));
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
     *
     * @param fromItem
     */
    public void processFromItem(FromItem fromItem) {
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
     *
     * @param join
     */
    public void processJoin(Join join) {
        if (join.getRightItem() instanceof Table) {
            Table fromTable = (Table) join.getRightItem();
            if (doTableFilter(fromTable.getName())) {
                join.setOnExpression(builderExpression(join.getOnExpression(), fromTable));
            }

        }
    }


    /**
     * 处理条件
     * TODO 未解决sql注入问题(考虑替换StringValue为LongValue),因为线上数据库租户字段为int暂时不存在注入问题
     *
     * @param expression
     * @param table
     * @return
     */
    public Expression builderExpression(Expression expression, Table table) {
        Expression tenantExpression = null;
        String[] tenantIds = this.tenantInfo.getTenantId().split(",");
        //当传入table时,字段前加上别名或者table名
        //别名优先使用
        StringBuilder tenantIdColumnName = new StringBuilder();
        if (table != null) {
            tenantIdColumnName.append(table.getAlias() != null ? table.getAlias().getName() : table.getName());
            tenantIdColumnName.append(".");
        }
        tenantIdColumnName.append(this.tenantInfo.getTenantIdColumn());
        //生成字段名
        Column tenantColumn = new Column(tenantIdColumnName.toString());

        if (tenantIds.length == 1) {
            EqualsTo equalsTo = new EqualsTo();
            tenantExpression = equalsTo;
            equalsTo.setLeftExpression(tenantColumn);
            equalsTo.setRightExpression(new StringValue("'" + tenantIds[0] + "'"));
        } else {
            //多租户身份
            InExpression inExpression = new InExpression();
            tenantExpression = inExpression;
            inExpression.setLeftExpression(tenantColumn);
            List<Expression> valueList = new ArrayList<>();
            for (String tid : tenantIds) {
                valueList.add(new StringValue("'" + tid + "'"));
            }
            inExpression.setRightItemsList(new ExpressionList(valueList));
        }

        //加入判断防止条件为空时生成 "and null" 导致查询结果为空
        if (expression == null) {
            return tenantExpression;
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
            return new AndExpression(tenantExpression, expression);
        }

    }

    private boolean doTableFilter(String table) {
        return true;
    }
}
