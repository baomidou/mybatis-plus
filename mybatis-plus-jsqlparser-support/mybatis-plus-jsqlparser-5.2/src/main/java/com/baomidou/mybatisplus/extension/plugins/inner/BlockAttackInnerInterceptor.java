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

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.conditional.XorExpression;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import java.sql.Connection;

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

    protected void checkWhere(Table table, Expression where, String ex) {
        // fixed https://github.com/baomidou/mybatis-plus/issues/7081
        this.checkWhere(getSchemaNameDotName(table), where, ex);
    }

    protected void checkWhere(String tableName, Expression where, String ex) {
        Assert.isFalse(Boolean.TRUE.equals(this.fullMatch(where, this.getTableLogicField(tableName))), ex);
    }

    private Boolean fullMatch(Expression where, String logicField) {
        if (where == null) {
            return Boolean.TRUE;
        }
        if (StringUtils.isNotBlank(logicField)) {

            if (where instanceof BinaryExpression) {
                BinaryExpression binaryExpression = (BinaryExpression) where;
                if (StringUtils.equals(binaryExpression.getLeftExpression().toString(), logicField)
                        || StringUtils.equals(binaryExpression.getRightExpression().toString(), logicField)) {
                    return Boolean.TRUE;
                }
            }

            if (where instanceof IsNullExpression) {
                IsNullExpression binaryExpression = (IsNullExpression) where;
                if (StringUtils.equals(binaryExpression.getLeftExpression().toString(), logicField)) {
                    return Boolean.TRUE;
                }
            }
        }

        if (where instanceof ComparisonOperator) {
            // 直接比较两个常量,提前按数据库语义求出结果,避免常量恒等式换一种写法绕过校验
            Boolean result = matchConstantComparison((ComparisonOperator) where);
            if (result != null) {
                return result;
            }
            if (where instanceof EqualsTo) {
                // example: 1=1
                EqualsTo equalsTo = (EqualsTo) where;
                return StringUtils
                        .equals(equalsTo.getLeftExpression().toString(), equalsTo.getRightExpression().toString());
            } else if (where instanceof NotEqualsTo) {
                // example: 1 != 2
                NotEqualsTo notEqualsTo = (NotEqualsTo) where;
                return !StringUtils.equals(
                        notEqualsTo.getLeftExpression().toString(), notEqualsTo.getRightExpression().toString());
            }
        } else if (where instanceof NotExpression) {
            // example: not 1 = 2
            Boolean result = fullMatch(((NotExpression) where).getExpression(), logicField);
            return result == null ? null : !result;
        } else if (where instanceof OrExpression) {

            OrExpression orExpression = (OrExpression) where;
            Boolean left = fullMatch(orExpression.getLeftExpression(), logicField);
            if (Boolean.TRUE.equals(left)) {
                return Boolean.TRUE;
            }
            Boolean right = fullMatch(orExpression.getRightExpression(), logicField);
            if (Boolean.TRUE.equals(right)) {
                return Boolean.TRUE;
            }
            return left == null || right == null ? null : Boolean.FALSE;
        } else if (where instanceof XorExpression) {
            // example: 1 xor 0,操作数按数据库真值语义解释,不能只看比较运算符
            Boolean left = fullMatch(((XorExpression) where).getLeftExpression(), logicField);
            Boolean right = fullMatch(((XorExpression) where).getRightExpression(), logicField);
            return left == null || right == null ? null : left ^ right;
        } else if (where instanceof AndExpression) {

            AndExpression andExpression = (AndExpression) where;
            Boolean left = fullMatch(andExpression.getLeftExpression(), logicField);
            if (Boolean.FALSE.equals(left)) {
                return Boolean.FALSE;
            }
            Boolean right = fullMatch(andExpression.getRightExpression(), logicField);
            if (Boolean.FALSE.equals(right)) {
                return Boolean.FALSE;
            }
            return left == null || right == null ? null : Boolean.TRUE;
        } else if (where instanceof ParenthesedExpressionList) {
            // example: (1 = 1)
            ParenthesedExpressionList<Expression> parenthesis = (ParenthesedExpressionList<Expression>) where;
            return parenthesis.size() == 1 ? fullMatch(parenthesis.get(0), logicField) : null;
        }

        // 条件本身是数值常量时按真值语义判断,例如 where 1
        return toBoolean(where);
    }

    /**
     * 提取常量的真值
     *
     * @param expression 表达式
     * @return 非零常量为 {@code true},零常量为 {@code false},不是常量返回 {@code null}
     */
    private Boolean toBoolean(Expression expression) {
        if (expression instanceof LongValue) {
            return ((LongValue) expression).getValue() != 0;
        }
        if (expression instanceof DoubleValue) {
            return ((DoubleValue) expression).getValue() != 0;
        }
        return null;
    }

    /**
     * 按数据库的真值语义计算两个常量之间的比较结果
     * <p>
     * 常量恒等式的写法不止 {@code 1=1} 一种,{@code 1<=1}、{@code 1<>2}、{@code 2>1}、{@code 'a'='a'} 同样匹配全部数据. 无法求值时返回
     * {@code null},交由调用方继续按原有语法规则判断.
     *
     * @param comparison 比较表达式
     * @return 比较结果为真返回 {@code true},为假返回 {@code false},无法确定返回 {@code null}
     */
    private Boolean matchConstantComparison(ComparisonOperator comparison) {
        String operator = comparison.getStringExpression();
        if (StringUtils.isBlank(operator) || comparison.getLeftExpression() == null
                || comparison.getRightExpression() == null) {
            return null;
        }
        Number left = toNumber(comparison.getLeftExpression());
        Number right = toNumber(comparison.getRightExpression());
        if (left == null || right == null) {
            String leftText = toText(comparison.getLeftExpression());
            String rightText = toText(comparison.getRightExpression());
            if (leftText == null || rightText == null) {
                return null;
            }
            return compareConstant(operator, leftText, rightText);
        }
        return compareConstant(operator, left, right);
    }

    /**
     * 比较两个常量,运算符或常量类型不受支持时返回 {@code null}
     *
     * @param operator 运算符
     * @param left 左常量
     * @param right 右常量
     * @return 比较结果,无法判断返回 {@code null}
     */
    private Boolean compareConstant(String operator, Object left, Object right) {
        int result;
        if (left instanceof Number && right instanceof Number) {
            result = Double.compare(((Number) left).doubleValue(), ((Number) right).doubleValue());
        } else if (left instanceof String && right instanceof String) {
            result = ((String) left).compareTo((String) right);
        } else {
            return null;
        }
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
     * 提取表达式的数值常量
     *
     * @param expression 表达式
     * @return 数值常量,不是数值常量返回 {@code null}
     */
    private Number toNumber(Expression expression) {
        if (expression instanceof LongValue) {
            return ((LongValue) expression).getValue();
        }
        if (expression instanceof DoubleValue) {
            return ((DoubleValue) expression).getValue();
        }
        return null;
    }

    /**
     * 提取表达式的字符串常量
     *
     * @param expression 表达式
     * @return 字符串常量,不是字符串常量返回 {@code null}
     */
    private String toText(Expression expression) {
        if (expression instanceof StringValue) {
            return ((StringValue) expression).getValue();
        }
        return null;
    }

    /**
     * 获取表名中的逻辑删除字段
     *
     * @param tableName 表名
     * @return 逻辑删除字段
     */
    private String getTableLogicField(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return StringPool.EMPTY;
        }

        TableInfo tableInfo = TableInfoHelper.getTableInfo(tableName);
        if (tableInfo == null || !tableInfo.isWithLogicDelete() || tableInfo.getLogicDeleteFieldInfo() == null) {
            return StringPool.EMPTY;
        }
        return tableInfo.getLogicDeleteFieldInfo().getColumn();
    }
}
