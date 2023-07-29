/*
 * Copyright (c) 2011-2022, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * Tdengine 数据库分页语句组装实现
 * 带有group by时使用 SLIMIT，如果存在嵌套查询时则可能有问题
 *
 * @author aispringli
 * @since 2023-04-20
 */
public class TdengineDialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        StringBuilder sql = new StringBuilder(originalSql);
        if (originalSql.contains(SqlKeyword.GROUP_BY.getSqlSegment())) {
            sql.append(" SLIMIT ");
        } else {
            sql.append(" LIMIT ");
        }
        sql.append(FIRST_MARK);
        if (offset != 0L) {
            sql.append(" OFFSET ").append(SECOND_MARK);
            return new DialectModel(sql.toString(), limit, offset).setConsumerChain();
        } else {
            return new DialectModel(sql.toString(), limit).setConsumer(true);
        }
    }
}
