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

import java.util.HashSet;
import java.util.Set;

/**
 * 遍历 SQL 语法树，将原始 SQL 和表名交给 {@link TableNameHandler}。 回调返回非空白表名时，用返回值替换节点中的表名。
 * <ul>
 * <li>从语法树重新生成 SQL 时，普通注释和语句末尾的分号会被去掉。 例如，表名保持原样时，{@code SELECT * FROM orders; -- 查询订单} 会输出为
 * {@code SELECT * FROM orders}。</li>
 * <li>回调负责保留所需的表名定界符。例如，将 {@code `orders`} 改为 {@code `orders_01`} 时， 返回值需要带上两侧的反引号。</li>
 * <li>{@code select * from dual}（不做特殊处理，自行忽略）。</li>
 * </ul>
 *
 * @author nieqiurong
 * @since 3.5.11
 */
public class DynamicTableNameHandler extends DynamicTableNameVisitor {

    private final String originSql;

    private final TableNameHandler tableNameHandler;

    private final Set<Table> set = new HashSet<>();

    public DynamicTableNameHandler(String originSql, TableNameHandler tableNameHandler) {
        this.originSql = originSql;
        this.tableNameHandler = tableNameHandler;
        init(false);
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
