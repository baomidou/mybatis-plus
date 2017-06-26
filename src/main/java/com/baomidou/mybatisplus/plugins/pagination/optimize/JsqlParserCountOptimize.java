/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.plugins.pagination.optimize;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.parser.AbstractSqlParser;
import com.baomidou.mybatisplus.parser.SqlInfo;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.SqlUtils;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

/**
 * <p>
 * JsqlParser Count Optimize
 * </p>
 *
 * @author hubin
 * @since 2017-06-20
 */
public class JsqlParserCountOptimize extends AbstractSqlParser {

    private static final List<SelectItem> countSelectItem = countSelectItem();

    @Override
    public SqlInfo optimizeSql(String sql) {
        if (logger.isDebugEnabled()) {
            logger.debug(" JsqlParserCountOptimize sql=" + sql);
        }
        SqlInfo sqlInfo = SqlInfo.newInstance();
        try {
            Select selectStatement = (Select) CCJSqlParserUtil.parse(sql);
            PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
            Distinct distinct = plainSelect.getDistinct();
            List<Expression> groupBy = plainSelect.getGroupByColumnReferences();
            List<OrderByElement> orderBy = plainSelect.getOrderByElements();

            // 添加包含groupBy 不去除orderBy
            if (CollectionUtils.isEmpty(groupBy) && CollectionUtils.isNotEmpty(orderBy)) {
                plainSelect.setOrderByElements(null);
                sqlInfo.setOrderBy(false);
            }

            // 包含 distinct、groupBy不优化
            if (distinct != null || CollectionUtils.isNotEmpty(groupBy)) {
                sqlInfo.setSql(String.format(SqlUtils.SQL_BASE_COUNT, selectStatement.toString()));
                return sqlInfo;
            }
            // 优化 SQL
            plainSelect.setSelectItems(countSelectItem);
            sqlInfo.setSql(selectStatement.toString());
            return sqlInfo;
        } catch (Throwable e) {
            // 无法优化使用原 SQL
            sqlInfo.setSql(String.format(SqlUtils.SQL_BASE_COUNT, sql));
            return sqlInfo;
        }
    }


    /**
     * <p>
     * 获取jsqlparser中count的SelectItem
     * </p>
     *
     * @return
     */
    private static List<SelectItem> countSelectItem() {
        Function function = new Function();
        function.setName("COUNT");
        List<Expression> expressions = new ArrayList<>();
        LongValue longValue = new LongValue(1);
        ExpressionList expressionList = new ExpressionList();
        expressions.add(longValue);
        expressionList.setExpressions(expressions);
        function.setParameters(expressionList);
        List<SelectItem> selectItems = new ArrayList<>();
        SelectExpressionItem selectExpressionItem = new SelectExpressionItem(function);
        selectItems.add(selectExpressionItem);
        return selectItems;
    }
}
