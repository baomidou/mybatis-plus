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

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.statement.select.*;

import java.util.function.Consumer;

/**
 * 编译期 AST 适配：保留本模块 JSqlParser 版本的节点和访问器语义。
 */
final class JsqlParserAdapter {

    private JsqlParserAdapter() {
    }

    static Expression wrap(Expression expression) {
        return new ParenthesedExpressionList<>(expression);
    }

    static boolean isParenthesized(Expression expression) {
        return expression instanceof ParenthesedExpressionList;
    }

    static Expression unwrap(Expression expression) {
        return ((ParenthesedExpressionList<?>) expression).get(0);
    }

    static FromItem getJoinFromItem(Join join) {
        return join.getRightItem();
    }

    static void processWithItems(Select select, Consumer<Select> consumer) {
        if (select.getWithItemsList() != null) {
            select.getWithItemsList().forEach(withItem -> consumer.accept(withItem));
        }
    }

    static void visitExpression(Expression expression, Consumer<Select> selectConsumer,
            Consumer<Function> functionConsumer) {
        expression.accept(new ExpressionVisitorAdapter<Void>() {

            @Override
            public <S> Void visit(ParenthesedSelect select, S context) {
                selectConsumer.accept(select);
                return null;
            }

            @Override
            public <S> Void visit(Select select, S context) {
                selectConsumer.accept(select);
                return null;
            }

            @Override
            public <S> Void visit(Function function, S context) {
                functionConsumer.accept(function);
                return null;
            }
        }, null);
    }
}
