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

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserAdapter;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.conditional.XorExpression;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.function.Predicate;

/**
 * 攻击 SQL 阻断解析器,防止全表更新与删除
 *
 * @author hubin
 * @since 3.4.0
 */
public class BlockAttackInnerInterceptor extends JsqlParserSupport implements InnerInterceptor {

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler handler = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = handler.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            if (InterceptorIgnoreHelper.willIgnoreBlockAttack(ms.getId())) {
                return;
            }
            BoundSql boundSql = handler.boundSql();
            parserMulti(boundSql.getSql(), null);
        }
    }

    @Override
    protected void processDelete(Delete delete, int index, String sql, Object obj) {
        this.checkWhere(delete.getTable(), delete.getWhere(), "Prohibition of full table deletion");
    }

    @Override
    protected void processUpdate(Update update, int index, String sql, Object obj) {
        this.checkWhere(update.getTable(), update.getWhere(), "Prohibition of table update operation");
    }

    /**
     * 检查 UPDATE 或 DELETE 的条件，使用目标表的别名识别逻辑删除过滤.
     */
    protected void checkWhere(Table table, Expression where, String ex) {
        Predicate<Expression> logicDeleteFilter = resolveLogicDeleteFilter(table);
        Assert.isFalse(Boolean.TRUE.equals(fullMatch(where, logicDeleteFilter)), ex);
    }

    /**
     * 判断 WHERE 是否恒真. 例如 {@code id = ? OR 1 = 1} 恒真，{@code id = ? AND 1 = 1} 仍取决于行数据. 返回 {@code null} 表示静态无法判定.
     */
    private Boolean fullMatch(Expression where, Predicate<Expression> logicDeleteFilter) {
        if (where == null || (logicDeleteFilter != null && logicDeleteFilter.test(where))) {
            return Boolean.TRUE;
        }

        if (where instanceof ComparisonOperator) {
            return matchConstantComparison((ComparisonOperator) where);
        } else if (where instanceof IsNullExpression) {
            IsNullExpression isNull = (IsNullExpression) where;
            boolean negated = isNull.isNot() || isNull.isUseNotNull();
            Expression operand = isNull.getLeftExpression();
            // NULL IS NULL 恒真，数字和布尔字面量的 IS NOT NULL 也恒真
            if (operand instanceof NullValue) {
                return !negated;
            }
            return toBoolean(operand) == null ? null : negated;
        } else if (where instanceof NotExpression) {
            // NOT (1 = 2) 恒真，NOT (id = ?) 仍无法静态判定
            Boolean result = fullMatch(((NotExpression) where).getExpression(), logicDeleteFilter);
            return result == null ? null : !result;
        } else if (where instanceof OrExpression) {
            OrExpression orExpression = (OrExpression) where;
            Boolean left = fullMatch(orExpression.getLeftExpression(), logicDeleteFilter);
            if (Boolean.TRUE.equals(left)) {
                return Boolean.TRUE;
            }
            Boolean right = fullMatch(orExpression.getRightExpression(), logicDeleteFilter);
            if (Boolean.TRUE.equals(right)) {
                return Boolean.TRUE;
            }
            return left == null || right == null ? null : Boolean.FALSE;
        } else if (where instanceof XorExpression) {
            // example: 1 xor 0,操作数按数据库真值语义解释,不能只看比较运算符
            Boolean left = fullMatch(((XorExpression) where).getLeftExpression(), logicDeleteFilter);
            Boolean right = fullMatch(((XorExpression) where).getRightExpression(), logicDeleteFilter);
            return left == null || right == null ? null : left ^ right;
        } else if (where instanceof AndExpression) {
            AndExpression andExpression = (AndExpression) where;
            Boolean left = fullMatch(andExpression.getLeftExpression(), logicDeleteFilter);
            if (Boolean.FALSE.equals(left)) {
                return Boolean.FALSE;
            }
            Boolean right = fullMatch(andExpression.getRightExpression(), logicDeleteFilter);
            if (Boolean.FALSE.equals(right)) {
                return Boolean.FALSE;
            }
            return left == null || right == null ? null : Boolean.TRUE;
        } else if (JsqlParserAdapter.isParenthesized(where)) {
            // example: (1 = 1)
            if (where instanceof ParenthesedExpressionList && ((ParenthesedExpressionList<?>) where).size() != 1) {
                return null;
            }
            return fullMatch(JsqlParserAdapter.unwrap(where), logicDeleteFilter);
        }
        // 直接求值单独的常量条件，例如 WHERE 1 或 WHERE TRUE
        return toBoolean(where);
    }

    /**
     * 提取常量的真值
     *
     * @param expression 表达式
     * @return 布尔或数值常量的真值，无法判定时返回 {@code null}
     */
    private Boolean toBoolean(Expression expression) {
        BigDecimal number = toExactNumber(expression);
        if (number == null) {
            return toBooleanLiteral(expression);
        }
        boolean nonZero = number.signum() != 0;
        double approximate = number.doubleValue();
        return Double.isFinite(approximate) && nonZero == (approximate != 0) ? nonZero : null;
    }

    private Boolean toBooleanLiteral(Expression expression) {
        // 4.9 和 5.0 把 TRUE/FALSE 解析成 Column，5.2 和 5.4 使用 BooleanValue
        String literal = expression.toString();
        if ("true".equalsIgnoreCase(literal)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(literal)) {
            return Boolean.FALSE;
        }
        return null;
    }

    /**
     * 比较精确数值、布尔字面量或相同的非空字符串字面量. 例如 {@code 1 <> 2} 和 {@code TRUE = TRUE} 恒真，{@code id <> ?} 和 {@code ? = ?} 需要运行时数据.
     *
     * @param comparison 比较表达式
     * @return 比较结果为真返回 {@code true},为假返回 {@code false},无法确定返回 {@code null}
     */
    private Boolean matchConstantComparison(ComparisonOperator comparison) {
        String operator = comparison.getStringExpression();
        if (StringUtils.isBlank(operator)) {
            return null;
        }
        Expression leftExpression = comparison.getLeftExpression();
        Expression rightExpression = comparison.getRightExpression();
        BigDecimal left = toExactNumber(leftExpression);
        BigDecimal right = toExactNumber(rightExpression);
        if (left != null && right != null) {
            Boolean result = compareConstant(operator, left.compareTo(right));
            BigDecimal sqliteLeft = toSqliteNumber(leftExpression, left);
            BigDecimal sqliteRight = toSqliteNumber(rightExpression, right);
            // 例如 0.10000000000000001 = 0.1 在精确十进制与浮点比较下可能得到不同结果
            return result != null && sqliteLeft != null && sqliteRight != null
                    && result.equals(compareConstant(operator, sqliteLeft.compareTo(sqliteRight))) ? result : null;
        }
        // 'A' = 'a' 取决于排序规则，'' = '' 在将空字符串视为 NULL 的数据库中也无法判为恒真
        if (leftExpression instanceof StringValue && rightExpression instanceof StringValue
                && !((StringValue) leftExpression).getValue().isEmpty()
                && leftExpression.toString().equals(rightExpression.toString())) {
            return compareConstant(operator, 0);
        }
        if ("=".equals(operator) || "<>".equals(operator) || "!=".equals(operator)) {
            Boolean leftBoolean = toBooleanLiteral(leftExpression);
            Boolean rightBoolean = toBooleanLiteral(rightExpression);
            if (leftBoolean != null && rightBoolean != null) {
                return compareConstant(operator, Boolean.compare(leftBoolean, rightBoolean));
            }
        }
        return null;
    }

    /**
     * 比较两个常量,运算符或常量类型不受支持时返回 {@code null}
     *
     * @param operator 运算符
     * @param result 两个常量的比较结果
     * @return 比较结果,无法判断返回 {@code null}
     */
    private Boolean compareConstant(String operator, int result) {
        switch (operator) {
            case "=" :
                return result == 0;
            case "!=" :
            case "<>" :
                return result != 0;
            case ">" :
                return result > 0;
            case ">=" :
                return result >= 0;
            case "<" :
                return result < 0;
            case "<=" :
                return result <= 0;
            default :
                return null;
        }
    }

    /**
     * 从字面量原文提取整数或定点小数,避免经过 {@code long} 或 {@code double} 转换.
     *
     * @param expression 表达式
     * @return 精确数值常量,依赖方言或无法求值时返回 {@code null}
     */
    private BigDecimal toExactNumber(Expression expression) {
        if (expression instanceof SignedExpression) {
            SignedExpression signed = (SignedExpression) expression;
            BigDecimal number = toExactNumber(signed.getExpression());
            if (number == null || (signed.getSign() != '+' && signed.getSign() != '-')) {
                return null;
            }
            return signed.getSign() == '-' ? number.negate() : number;
        }
        if (!(expression instanceof LongValue) && !(expression instanceof DoubleValue)) {
            return null;
        }
        String literal = expression.toString();
        // 科学计数法在部分方言中是近似值,无法按定点小数求值
        if (literal.indexOf('e') >= 0 || literal.indexOf('E') >= 0) {
            return null;
        }
        try {
            return new BigDecimal(literal);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private BigDecimal toSqliteNumber(Expression expression, BigDecimal number) {
        while (expression instanceof SignedExpression) {
            expression = ((SignedExpression) expression).getExpression();
        }
        if (expression instanceof LongValue && number.unscaledValue().bitLength() < Long.SIZE) {
            return number;
        }
        double approximate = number.doubleValue();
        // 精确保留二进制浮点数的值,以便与未舍入的整数比较.
        return Double.isFinite(approximate) ? new BigDecimal(approximate) : null;
    }

    private Predicate<Expression> resolveLogicDeleteFilter(Table table) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(getSchemaNameDotName(table));
        if (tableInfo == null || !tableInfo.isWithLogicDelete() || tableInfo.getLogicDeleteFieldInfo() == null) {
            return null;
        }
        TableFieldInfo field = tableInfo.getLogicDeleteFieldInfo();
        // 未删除值为 0 时，仅有 del_flag = 0 会拦截，追加 AND id = ? 后仍有业务过滤
        return expression -> isLogicDeleteFilter(expression, table, field);
    }

    private boolean isLogicDeleteFilter(Expression expression, Table table, TableFieldInfo field) {
        String value = field.getLogicNotDeleteValue();
        if (value == null || value.isEmpty()) {
            return false;
        }
        if ("null".equalsIgnoreCase(value)) {
            // 配置未删除值为 NULL 时，识别 del_flag IS NULL
            if (!(expression instanceof IsNullExpression)) {
                return false;
            }
            IsNullExpression isNull = (IsNullExpression) expression;
            return !isNull.isNot() && !isNull.isUseNotNull()
                    && isLogicDeleteColumn(isNull.getLeftExpression(), table, field.getColumn());
        }
        if (!(expression instanceof EqualsTo)) {
            return false;
        }
        EqualsTo equals = (EqualsTo) expression;
        Expression left = equals.getLeftExpression();
        Expression right = equals.getRightExpression();
        // 识别 del_flag = 0 和 0 = del_flag，del_flag = id 仍属于业务比较
        return (isLogicDeleteColumn(left, table, field.getColumn()) && isLogicNotDeletedValue(right, field))
                || (isLogicDeleteColumn(right, table, field.getColumn()) && isLogicNotDeletedValue(left, field));
    }

    private boolean isLogicNotDeletedValue(Expression expression, TableFieldInfo field) {
        String value = field.getLogicNotDeleteValue();
        if (expression instanceof StringValue) {
            StringValue literal = (StringValue) expression;
            return StringUtils.isBlank(literal.getPrefix()) && value.equals(literal.getValue());
        }
        Class<?> fieldType = field.getPropertyType();
        if ((fieldType == Boolean.class || fieldType == boolean.class)
                && ("false".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value))) {
            // JSqlParser 4.9 和 5.0 将布尔字面量解析为 Column
            return value.equalsIgnoreCase(expression.toString());
        }
        BigDecimal number = toExactNumber(expression);
        if (number == null || field.isCharSequence() || value.indexOf('e') >= 0 || value.indexOf('E') >= 0) {
            return false;
        }
        try {
            return number.compareTo(new BigDecimal(value)) == 0;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private boolean isLogicDeleteColumn(Expression expression, Table table, String name) {
        if (!(expression instanceof Column)) {
            return false;
        }
        Column column = (Column) expression;
        if (!sameIdentifier(name, column.getColumnName())) {
            return false;
        }
        Table qualifier = column.getTable();
        if (qualifier == null || qualifier.getName() == null) {
            return true;
        }
        if (table.getAlias() != null) {
            // UPDATE records r ... WHERE r.del_flag = 0 中的 r 必须是目标表别名
            return qualifier.getSchemaName() == null && sameIdentifier(table.getAlias().getName(), qualifier.getName());
        }
        return sameIdentifier(table.getName(), qualifier.getName()) && (qualifier.getSchemaName() == null
                || sameIdentifier(table.getSchemaName(), qualifier.getSchemaName()));
    }

    private boolean sameIdentifier(String left, String right) {
        if (left == null || right == null) {
            return false;
        }
        String leftName = unquoteIdentifier(left);
        String rightName = unquoteIdentifier(right);
        return leftName.length() == left.length() && rightName.length() == right.length()
                ? leftName.equalsIgnoreCase(rightName)
                : leftName.equals(rightName);
    }

    private String unquoteIdentifier(String name) {
        if (name.length() < 2) {
            return name;
        }
        char first = name.charAt(0);
        char last = name.charAt(name.length() - 1);
        if ((first == '"' && last == '"') || (first == '`' && last == '`') || (first == '[' && last == ']')) {
            return name.substring(1, name.length() - 1);
        }
        return name;
    }
}
