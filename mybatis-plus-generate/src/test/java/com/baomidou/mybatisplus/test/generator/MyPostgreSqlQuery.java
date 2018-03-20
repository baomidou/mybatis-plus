package com.baomidou.mybatisplus.test.generator;

import com.baomidou.mybatisplus.generator.config.querys.PostgreSqlQuery;

public class MyPostgreSqlQuery extends PostgreSqlQuery {


    @Override
    public String tableFieldsSql() {
        // 固定 abc  def 内容，实际可以查询字段大小等信息
        return "SELECT 1 AS abc, 2 AS def, A.attname AS name, format_type(A.atttypid, A.atttypmod) AS type,col_description(A.attrelid, A.attnum) AS comment, (CASE C.contype WHEN 'p' THEN 'PRI' ELSE '' END) AS key " +
            "FROM pg_attribute A LEFT JOIN pg_constraint C ON A.attnum = C.conkey[1] AND A.attrelid = C.conrelid " +
            "WHERE  A.attrelid = '%s.%s'::regclass AND A.attnum > 0 AND NOT A.attisdropped ORDER  BY A.attnum";
    }


    @Override
    public String[] fieldCustom() {
        // 返回自定义查询字段
        return new String[]{"abc", "def"};
    }
}
