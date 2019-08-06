/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.plugins.pagination.optimize;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.core.parser.SqlInfo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.toolkit.SqlParserUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.MetaObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JsqlParser Count Optimize
 *
 * @author hubin
 * @since 2017-06-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsqlParserCountOptimize implements ISqlParser {
    private static final List<SelectItem> COUNT_SELECT_ITEM = countSelectItem();
    private final Log logger = LogFactory.getLog(JsqlParserCountOptimize.class);

    private boolean optimizeJoin = false;

    /**
     * 获取jsqlparser中count的SelectItem
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

    @Override
    public SqlInfo parser(MetaObject metaObject, String sql) {
        if (logger.isDebugEnabled()) {
            logger.debug("JsqlParserCountOptimize sql=" + sql);
        }
        SqlInfo sqlInfo = SqlInfo.newInstance();
        try {
            Select selectStatement = (Select) CCJSqlParserUtil.parse(sql);
            PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
            Distinct distinct = plainSelect.getDistinct();
            GroupByElement groupBy = plainSelect.getGroupBy();
            List<OrderByElement> orderBy = plainSelect.getOrderByElements();

            // 添加包含groupBy 不去除orderBy
            if (null == groupBy && CollectionUtils.isNotEmpty(orderBy)) {
                plainSelect.setOrderByElements(null);
                sqlInfo.setOrderBy(false);
            }
            //#95 Github, selectItems contains #{} ${}, which will be translated to ?, and it may be in a function: power(#{myInt},2)
            for (SelectItem item : plainSelect.getSelectItems()) {
                if (item.toString().contains(StringPool.QUESTION_MARK)) {
                    return sqlInfo.setSql(SqlParserUtils.getOriginalCountSql(selectStatement.toString()));
                }
            }
            // 包含 distinct、groupBy不优化
            if (distinct != null || null != groupBy) {
                return sqlInfo.setSql(SqlParserUtils.getOriginalCountSql(selectStatement.toString()));
            }
            // 包含 join 连表,进行判断是否移除 join 连表
            List<Join> joins = plainSelect.getJoins();
            if (optimizeJoin && CollectionUtils.isNotEmpty(joins)) {
                boolean canRemoveJoin = true;
                String whereS = Optional.ofNullable(plainSelect.getWhere()).map(Expression::toString).orElse(StringPool.EMPTY);
                for (Join join : joins) {
                    if (!join.isLeft()) {
                        canRemoveJoin = false;
                        break;
                    }
                    Table table = (Table) join.getRightItem();
                    String str = Optional.ofNullable(table.getAlias()).map(Alias::getName).orElse(table.getName()) + StringPool.DOT;
                    String onExpressionS = join.getOnExpression().toString();
                    /* 如果 join 里包含 ?(代表有入参) 或者 where 条件里包含使用 join 的表的字段作条件,就不移除 join */
                    if (onExpressionS.contains(StringPool.QUESTION_MARK) || whereS.contains(str)) {
                        canRemoveJoin = false;
                        break;
                    }
                }
                if (canRemoveJoin) {
                    plainSelect.setJoins(null);
                }
            }
            // 优化 SQL
            plainSelect.setSelectItems(COUNT_SELECT_ITEM);
            return sqlInfo.setSql(selectStatement.toString());
        } catch (Throwable e) {
            // 无法优化使用原 SQL
            return sqlInfo.setSql(SqlParserUtils.getOriginalCountSql(sql));
        }
    }
}
