/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.generator;

import com.baomidou.mybatisplus.generator.config.querys.KingbaseESQuery;

public class MyKingbaseESQuery extends KingbaseESQuery {


    @Override
    public String tableFieldsSql() {
        // 固定 abc  def 内容，实际可以查询字段大小等信息
        return "SELECT 1 AS abc, 2 AS def, A.attname AS name, format_type(A.atttypid, A.atttypmod) AS type,col_description(A.attrelid, A.attnum) AS comment, (CASE C.contype WHEN 'p' THEN 'PRI' ELSE '' END) AS key " +
            "FROM sys_attribute A LEFT JOIN sys_constraint C ON A.attnum = C.conkey[1] AND A.attrelid = C.conrelid " +
            "WHERE  A.attrelid = '%s.%s'::regclass AND A.attnum > 0 AND NOT A.attisdropped ORDER  BY A.attnum";
    }


    @Override
    public String[] fieldCustom() {
        // 返回自定义查询字段
        return new String[]{"abc", "def"};
    }
}
