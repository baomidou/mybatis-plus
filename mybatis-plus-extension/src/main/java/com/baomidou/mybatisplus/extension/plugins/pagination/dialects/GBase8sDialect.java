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
 * GBase 8s V8.8 数据库分页语句组装实现
 * 通用分页版本
 *
 * @author liaojinqing
 * @since 2021-07-20
 */
public class GBase8sDialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        StringBuilder sql = new StringBuilder(originalSql)
            .insert(6, " SKIP " + offset + " FIRST " + limit);
        return new DialectModel(sql.toString());
    }
}
