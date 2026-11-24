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
package com.baomidou.mybatisplus.extension;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.util.TablesNamesFinder;

/**
 * 处理本版本的动态表名 DDL 遍历。
 */
abstract class DynamicTableNameVisitor extends TablesNamesFinder<Void> {

    @Override
    public <S> Void visit(CreateIndex createIndex, S context) {
        return this.visit(createIndex.getTable(), context);
    }

    @Override
    public <S> Void visit(CreateView createView, S context) {
        return this.visit(createView.getSelect(), context);
    }

    @Override
    public <S> Void visit(Drop drop, S context) {
        if (StringUtils.isNotBlank(drop.getType())) {
            String type = drop.getType().toUpperCase();
            if ("TABLE".equals(type)) {
                return super.visit(drop, context);
            }
        }
        return null;
    }
}
