package com.baomidou.mybatisplus.extension.toolkit;


import org.junit.jupiter.api.Test;

/**
 * @author miemie
 * @since 2019-02-22
 */
class SqlParserUtilsTest {

    @Test
    void getOptimizeCountSql() {
        System.out.println(SqlParserUtils.getOptimizeCountSql(true, null,
            "SELECT id,name,age,email,merge FROM user").getSql());
    }
}
