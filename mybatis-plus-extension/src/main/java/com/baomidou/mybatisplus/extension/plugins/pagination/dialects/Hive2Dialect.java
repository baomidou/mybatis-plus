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
package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * DB2 数据库分页方言
 *
 * @author hubin
 * @since 2016-11-10
 */
public class Hive2Dialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        long firstParam = offset + 1;
        long secondParam = limit;
        /**
         * select * from ( select t.*,ROW_NUMBER() OVER(ORDER BY INNER_NO) AS row_num FROM ("
         * +sqlcmd+") t ) a offset "+startNo+" rows fetch next "+fetchCount+" rows only
         */
        String sql = "SELECT a.* FROM (SELECT TMP_PAGE.*,ROW_NUMBER() OVER() AS ROW_ID FROM ( "
            + originalSql +
            " ) TMP_PAGE) a OFFSET "
            + firstParam
            + " ROWS FETCH NEXT "
            + secondParam + " ROWS ONLY";
        return new DialectModel(sql);
    }
}
