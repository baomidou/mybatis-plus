package com.baomidou.mybatisplus.extension.plugins.pagination.count;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author miemie
 * @since 2020-06-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsqlParserCountSqlParser implements CountSqlParser {

    private static final List<SelectItem> COUNT_SELECT_ITEM = Collections.singletonList(defaultCountSelectItem());
    private final Log logger = LogFactory.getLog(JsqlParserCountSqlParser.class);

    private boolean optimizeJoin = false;

    /**
     * 获取jsqlparser中count的SelectItem
     */
    private static SelectItem defaultCountSelectItem() {
        Function function = new Function();
        ExpressionList expressionList = new ExpressionList(Collections.singletonList(new LongValue(1)));
        function.setName("COUNT");
        function.setParameters(expressionList);
        return new SelectExpressionItem(function);
    }

    @Override
    public String parser(String originalSql) {
        if (logger.isDebugEnabled()) {
            logger.debug("JsqlParserCountOptimize sql=" + originalSql);
        }
        try {
            Select selectStatement = (Select) CCJSqlParserUtil.parse(originalSql);
            PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
            Distinct distinct = plainSelect.getDistinct();
            GroupByElement groupBy = plainSelect.getGroupBy();
            List<OrderByElement> orderBy = plainSelect.getOrderByElements();

            // 添加包含groupBy 不去除orderBy
            if (null == groupBy && CollectionUtils.isNotEmpty(orderBy)) {
                plainSelect.setOrderByElements(null);
            }
            //#95 Github, selectItems contains #{} ${}, which will be translated to ?, and it may be in a function: power(#{myInt},2)
            for (SelectItem item : plainSelect.getSelectItems()) {
                if (item.toString().contains(StringPool.QUESTION_MARK)) {
                    return defaultCount(selectStatement.toString());
                }
            }
            // 包含 distinct、groupBy不优化
            if (distinct != null || null != groupBy) {
                return defaultCount(selectStatement.toString());
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
            return defaultCount(selectStatement.toString());
        } catch (Throwable e) {
            // 无法优化使用原 SQL
            return defaultCount(originalSql);
        }
    }
}
