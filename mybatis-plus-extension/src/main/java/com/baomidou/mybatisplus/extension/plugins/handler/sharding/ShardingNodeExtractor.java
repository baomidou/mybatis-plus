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
package com.baomidou.mybatisplus.extension.plugins.handler.sharding;

import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import lombok.Getter;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengzhihong
 * @since 2021-01-14
 */
public class ShardingNodeExtractor extends TablesNamesFinder {

    @Getter
    private final List<ShardingNode<Table, ShardingNode<String, Integer>>> nodes;

    private Column currentColumn;

    public ShardingNodeExtractor(Statement statement) {
        this.nodes = new ArrayList<>();
        super.getTableList(statement);
    }


    @Override
    public void visit(Table table) {
        nodes.add(new ShardingNode<>(table, new ArrayList<>()));
    }


    @Override
    public void visit(Column column) {
        this.currentColumn = column;
        final ShardingNode<Table, ShardingNode<String, Integer>> tableNode =
                obtainTableNode(column);
        // SQL正确的前提下 tableNode一定不为null
        if (null == tableNode) {
            throw ExceptionUtils.mpe("please determine the alias on sql");
        }
        final ShardingNode<String, Integer> columnNode =
                tableNode.getList().stream().filter(i -> i.getNode().equals(column.getColumnName())).findFirst().orElse(null);
        if (null == columnNode) {
            tableNode.getList().add(new ShardingNode<>(column.getColumnName(), new ArrayList<>()));
        }
    }

    @Override
    public void visit(Insert insert) {
        visit(insert.getTable());
        if (insert.getColumns() != null && insert.getItemsList() != null && insert.getItemsList() instanceof ExpressionList) {
            final ExpressionList itemsList = (ExpressionList) insert.getItemsList();
            if (null != itemsList.getExpressions() && insert.getColumns().size() == itemsList.getExpressions().size()) {
                for (int i = 0; i < insert.getColumns().size(); i++) {
                    final Expression expression = itemsList.getExpressions().get(i);
                    if (!(expression instanceof JdbcParameter)) {
                        continue;
                    }
                    visit(insert.getColumns().get(i));
                    visit((JdbcParameter) expression);
                }
            }
        }
        if (insert.getSelect() != null) {
            visit(insert.getSelect());
        }
    }

    @Override
    public void visit(Update update) {
        visit(update.getTable());
        if (update.getStartJoins() != null) {
            for (Join join : update.getStartJoins()) {
                join.getRightItem().accept(this);
            }
        }
        /*if (update.getExpressions() != null) {
            for (Expression expression : update.getExpressions()) {
                expression.accept(this);
            }
        }*/

        if (update.getFromItem() != null) {
            update.getFromItem().accept(this);
        }

        if (update.getJoins() != null) {
            for (Join join : update.getJoins()) {
                join.getRightItem().accept(this);
            }
        }

        if (update.getWhere() != null) {
            update.getWhere().accept(this);
        }
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        final ShardingNode<Table, ShardingNode<String, Integer>> tableNode =
                obtainTableNode(this.currentColumn);
        if (null == tableNode) {
            throw ExceptionUtils.mpe("please determine the alias on sql");
        }
        final ShardingNode<String, Integer> columnNode =
                tableNode.getList().stream().filter(i -> i.getNode().equals(this.currentColumn.getColumnName())).findFirst().orElse(null);
        if (null == columnNode) {
            throw ExceptionUtils.mpe("please determine the alias on sql");
        }
        columnNode.getList().add(jdbcParameter.getIndex());
    }

    @Override
    public void visit(ExpressionList expressionList) {
        for (Expression expression : expressionList.getExpressions()) {
            expression.accept(this);
        }
    }

    private ShardingNode<Table, ShardingNode<String, Integer>> obtainTableNode(Column column) {
        return null == column || null == column.getTable() || null == column.getTable().getName() ? nodes.get(0) :
                nodes.stream().filter(i -> i.getNode().getAlias().getName().equals(column.getTable().getName())).findFirst().orElse(null);
    }


    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {

    }


    @Override
    public void visit(Parenthesis parenthesis) {
        parenthesis.getExpression().accept(this);
    }


    @Override
    public void visit(Addition addition) {
        visitBinaryExpression(addition);
    }

    @Override
    public void visit(Division division) {
        visitBinaryExpression(division);
    }


    @Override
    public void visit(Subtraction subtraction) {
        visitBinaryExpression(subtraction);
    }

    @Override
    public void visit(AndExpression andExpression) {
        visitBinaryExpression(andExpression);
    }

    @Override
    public void visit(OrExpression orExpression) {
        visitBinaryExpression(orExpression);
    }

    @Override
    public void visit(Between between) {
        between.getLeftExpression().accept(this);
        between.getBetweenExpressionStart().accept(this);
        between.getBetweenExpressionEnd().accept(this);
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        visitBinaryExpression(equalsTo);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        visitBinaryExpression(greaterThan);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        visitBinaryExpression(greaterThanEquals);
    }

    @Override
    public void visit(InExpression inExpression) {
        inExpression.getLeftExpression().accept(this);
        if (null != inExpression.getLeftItemsList()) {
            inExpression.getLeftItemsList().accept(this);
        }
        if (null != inExpression.getRightItemsList()) {
            inExpression.getRightItemsList().accept(this);
        }
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        visitBinaryExpression(likeExpression);
    }

    @Override
    public void visit(MinorThan minorThan) {
        visitBinaryExpression(minorThan);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        visitBinaryExpression(minorThanEquals);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        visitBinaryExpression(notEqualsTo);
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        existsExpression.getRightExpression().accept(this);
    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {
        allComparisonExpression.getSubSelect().getSelectBody().accept(this);
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        anyComparisonExpression.getSubSelect().getSelectBody().accept(this);
    }

    @Override
    public void visit(Concat concat) {
        visitBinaryExpression(concat);
    }

    @Override
    public void visit(Matches matches) {
        visitBinaryExpression(matches);
    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        visitBinaryExpression(bitwiseAnd);
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        visitBinaryExpression(bitwiseOr);
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        visitBinaryExpression(bitwiseXor);
    }

    @Override
    public void visit(CastExpression cast) {
    }

    @Override
    public void visit(SubSelect subSelect) {
        subSelect.getSelectBody().accept(this);
    }

    @Override
    public void visit(SubJoin subjoin) {
        subjoin.getLeft().accept(this);
        for (Join join : subjoin.getJoinList()) {
            join.getRightItem().accept(this);
        }
    }


    @Override
    public void visit(PlainSelect plainSelect) {
        plainSelect.getFromItem().accept(this);
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                join.getRightItem().accept(this);
            }
        }
        if (plainSelect.getWhere() != null) {
            plainSelect.getWhere().accept(this);
        }
    }
}
