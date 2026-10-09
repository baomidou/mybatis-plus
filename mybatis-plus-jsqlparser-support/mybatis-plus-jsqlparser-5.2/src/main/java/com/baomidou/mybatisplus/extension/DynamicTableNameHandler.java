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
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.HashSet;
import java.util.Set;

/**
 * 动态表名解析处理
 * <p>1.无法保留sql注释(例如 select * from test; --这是个查询 处理完会变成 select * from test)</p>
 * <p>2.无法保留语句分隔符;(例如 select * from test; 处理完会变成 select * from test )</p>
 * <p>3.如果使用转义符包裹了表名需要自行处理</p>
 * <p>4.select * from dual (不处理这个,自行忽略)</p>
 *
 * @author nieqiurong
 * @since 3.5.11
 */
public class DynamicTableNameHandler extends TablesNamesFinder<Void> {

    private final String originSql;

    private final TableNameHandler tableNameHandler;

    private final Set<Table> set = new HashSet<>();

    public DynamicTableNameHandler(String originSql, TableNameHandler tableNameHandler) {
        this.originSql = originSql;
        this.tableNameHandler = tableNameHandler;
        init(false);
    }

    @Override
    public <S> Void visit(CreateIndex createIndex, S context) {
        return this.visit(createIndex.getTable(), context);
    }

    @Override
    public <S> Void visit(CreateView create, S context) {
        return this.visit(create.getSelect(), context);
    }

    @Override
    public <S> Void visit(Drop drop, S context) {
        if(StringUtils.isNotBlank(drop.getType())){
            String type = drop.getType().toUpperCase();
            if ("TABLE".equals(type)) {
                return super.visit(drop, context);
            }
        }
        return null;
    }

    @Override
    protected String extractTableName(Table table) {
        String originalTableName = table.getName();
        if (table.getASTNode() == null) {
            return originalTableName;
        }
        if (set.add(table)) {
            String tableName = tableNameHandler.dynamicTableName(originSql, originalTableName);
            if (StringUtils.isNotBlank(tableName)) {
                table.setName(tableName);
                return tableName;
            }
        }
        return originalTableName;
    }

}
