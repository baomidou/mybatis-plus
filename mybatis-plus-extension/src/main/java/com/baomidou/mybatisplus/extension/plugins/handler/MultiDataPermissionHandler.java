/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.plugins.handler;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;

/**
 * 支持多表的数据权限处理器
 *
 * @author houkunlin
 * @since 3.5.2 +
 */
public interface MultiDataPermissionHandler extends DataPermissionHandler {
    /**
     * 为兼容旧版数据权限处理器，继承了 {@link DataPermissionHandler} 但是新的多表数据权限处理又不会调用此方法，因此标记过时
     *
     * @param where             待执行 SQL Where 条件表达式
     * @param mappedStatementId Mybatis MappedStatement Id 根据该参数可以判断具体执行方法
     * @return JSqlParser 条件表达式
     * @deprecated 新的多表数据权限处理不会调用此方法，因此标记过时
     */
    @Deprecated
    @Override
    default Expression getSqlSegment(Expression where, String mappedStatementId) {
        return where;
    }

    /**
     * 获取数据权限 SQL 片段。
     * <p>旧的 {@link MultiDataPermissionHandler#getSqlSegment(Expression, String)} 方法第一个参数包含所有的 where 条件信息，如果 return 了 null 会覆盖原有的 where 数据，</p>
     * <p>新版的 {@link MultiDataPermissionHandler#getSqlSegment(Table, Expression, String)} 方法不能覆盖原有的 where 数据，如果 return 了 null 则表示不追加任何 where 条件</p>
     *
     * @param table             所执行的数据库表信息，可以通过此参数获取表名和表别名
     * @param where             原有的 where 条件信息
     * @param mappedStatementId Mybatis MappedStatement Id 根据该参数可以判断具体执行方法
     * @return JSqlParser 条件表达式，返回的条件表达式会拼接在原有的表达式后面（不会覆盖原有的表达式）
     */
    Expression getSqlSegment(final Table table, final Expression where, final String mappedStatementId);
}
