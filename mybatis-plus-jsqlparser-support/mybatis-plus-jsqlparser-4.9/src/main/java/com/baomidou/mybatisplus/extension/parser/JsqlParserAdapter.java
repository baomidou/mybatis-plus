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
package com.baomidou.mybatisplus.extension.parser;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.statement.select.*;

import java.util.List;
import java.util.function.Consumer;

/**
 * 编译期 AST 适配：保留本模块 JSqlParser 版本的节点和访问器语义。
 */
public final class JsqlParserAdapter {

    private JsqlParserAdapter() {
    }

    public static Expression wrap(Expression expression) {
        return new Parenthesis(expression);
    }

    public static boolean isParenthesized(Expression expression) {
        return expression instanceof Parenthesis;
    }

    public static Expression unwrap(Expression expression) {
        return ((Parenthesis) expression).getExpression();
    }

    public static FromItem getJoinFromItem(Join join) {
        return join.getFromItem();
    }

    public static void processWithItems(Select select, Consumer<Select> consumer) {
        if (select.getWithItemsList() != null) {
            select.getWithItemsList().forEach(withItem -> consumer.accept(withItem));
        }
    }

    /**
     * 遍历 WITH 列表中的 SELECT 定义。
     */
    public static void processWithItemsList(List<WithItem> withItems, Consumer<Select> consumer) {
        if (withItems != null) {
            withItems.forEach(consumer);
        }
    }

    public static void visitExpression(Expression expression, Consumer<Select> selectConsumer,
            Consumer<Function> functionConsumer) {
        expression.accept(new ExpressionVisitorAdapter() {

            @Override
            public void visit(ParenthesedSelect select) {
                selectConsumer.accept(select);
            }

            @Override
            public void visit(Select select) {
                selectConsumer.accept(select);
            }

            @Override
            public void visit(Function function) {
                functionConsumer.accept(function);
            }
        });
    }
}
