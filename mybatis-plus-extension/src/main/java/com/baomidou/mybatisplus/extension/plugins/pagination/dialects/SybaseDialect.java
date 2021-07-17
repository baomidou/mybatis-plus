/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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
 * sybase 数据库分页方言
 *
 * @author sdsyjh
 * @since 2020-07-30
 */
public class SybaseDialect implements IDialect {

    private final boolean hasTop; // sybase12.5.4以前，不支持select top

    public SybaseDialect() {
        this(false);
    }

    public SybaseDialect(boolean hasTop) {
        this.hasTop = hasTop;
    }

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        int index = originalSql.indexOf("FROM");
        String sql = "select";
        if (hasTop) {
            sql += " top " + (offset + limit);
        }
        sql += " rownum=identity(12)," + originalSql.substring(6, index) + " into #t " + originalSql.substring(index);
        sql += " select * from #t where rownum > ? and rownum <= ? ";
        sql += "drop table #t ";
        return new DialectModel(sql, offset, offset + limit).setConsumerChain();
    }
}
