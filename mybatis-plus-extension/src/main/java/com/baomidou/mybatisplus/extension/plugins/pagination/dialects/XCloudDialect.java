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
package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * XCloud 数据库分页语句组装实现
 *
 * @author huyang
 * @since 2022-04-13
 */
public class XCloudDialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        StringBuilder sql = new StringBuilder(originalSql).append(" LIMIT ");
        if (offset != 0L) {
            sql.append(" (" + FIRST_MARK + "," + SECOND_MARK + ") ");
            return new DialectModel(sql.toString(), offset + 1, offset + limit).setConsumerChain();
        } else {
            sql.append(FIRST_MARK);
            return new DialectModel(sql.toString(), limit).setConsumer(true);
        }
    }
}
