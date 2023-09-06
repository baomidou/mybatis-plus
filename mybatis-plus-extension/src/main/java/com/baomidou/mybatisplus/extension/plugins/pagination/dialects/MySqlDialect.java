/*
 * Copyright (c) 2011-2023, baomidou (jobob@qq.com).
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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * MYSQL 数据库分页语句组装实现
 *
 * @author hubin
 * @since 2016-01-23
 */
public class MySqlDialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, IPage<?> page) {
        long offset = page.offset();
        // 计算实际count值
        long limit = page.getSize();
        // 此次分页查询是否进行总数的查询，为否时total恒为0无法计算出真实count
        if (page.searchCount()) {
            long leftCount = page.getTotal() - (page.getCurrent() - 1) * limit;
            // 正常来说leftCount总是>=0, 这里避免异常情况发现小于0时直接设置为0
            if (leftCount < 0) {
                limit = 0;
            } else {
                limit = Math.min(leftCount, limit);
            }
        }
        StringBuilder sql = new StringBuilder(originalSql).append(" LIMIT ").append(FIRST_MARK);
        if (offset != 0L) {
            sql.append(StringPool.COMMA).append(SECOND_MARK);
            return new DialectModel(sql.toString(), offset, limit).setConsumerChain();
        } else {
            return new DialectModel(sql.toString(), limit).setConsumer(true);
        }
    }
}
