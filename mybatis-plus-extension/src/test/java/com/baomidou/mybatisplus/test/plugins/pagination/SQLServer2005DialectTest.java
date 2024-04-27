package com.baomidou.mybatisplus.test.plugins.pagination;

import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.SQLServer2005Dialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author nieqiurong
 */
public class SQLServer2005DialectTest {

    private final SQLServer2005Dialect sqlServer2005Dialect = new SQLServer2005Dialect();

    @Test
    void test() {
        Assertions.assertEquals(sqlServer2005Dialect.buildPaginationSql("select distinct *,(select 1) from test", 1, 10).getDialectSql(),
            "WITH selectTemp AS (SELECT DISTINCT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(select 1) from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 11 ORDER BY __row_number__");
        Assertions.assertEquals(sqlServer2005Dialect.buildPaginationSql(" select distinct *,(select 1) from test", 1, 10).getDialectSql(),
            "WITH selectTemp AS (SELECT DISTINCT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(select 1) from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 11 ORDER BY __row_number__");

        Assertions.assertEquals(sqlServer2005Dialect.buildPaginationSql("select *,(select 1) from test", 1, 10).getDialectSql(),
            "WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(select 1) from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 11 ORDER BY __row_number__");
        Assertions.assertEquals(sqlServer2005Dialect.buildPaginationSql(" select *,(select 1) from test", 1, 10).getDialectSql(),
            "WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(select 1) from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 11 ORDER BY __row_number__");

        Assertions.assertEquals(sqlServer2005Dialect.buildPaginationSql("select   distinct   *,(select 1) from test", 1, 10).getDialectSql(),
            "WITH selectTemp AS (SELECT DISTINCT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(select 1) from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 11 ORDER BY __row_number__");
        Assertions.assertEquals(sqlServer2005Dialect.buildPaginationSql(" select   distinct *,(select 1) from test", 1, 10).getDialectSql(),
            "WITH selectTemp AS (SELECT DISTINCT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(select 1) from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 11 ORDER BY __row_number__");

        Assertions.assertEquals(sqlServer2005Dialect.buildPaginationSql("select   *,(select 1) from test", 1, 10).getDialectSql(),
            "WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(select 1) from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 11 ORDER BY __row_number__");
        Assertions.assertEquals(sqlServer2005Dialect.buildPaginationSql(" select   *,(select 1) from test", 1, 10).getDialectSql(),
            "WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(select 1) from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 11 ORDER BY __row_number__");

        Assertions.assertEquals(sqlServer2005Dialect.buildPaginationSql("select * from test", 1, 10).getDialectSql(),
            "WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  * from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 11 ORDER BY __row_number__");
        Assertions.assertEquals(sqlServer2005Dialect.buildPaginationSql(" select * from test", 1, 10).getDialectSql(),
            "WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  * from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 11 ORDER BY __row_number__");

        Assertions.assertEquals(sqlServer2005Dialect.buildPaginationSql("SELECT DISTINCT *,(SELECT 1) FROM TEST", 1, 10).getDialectSql(),
            "WITH selectTemp AS (SELECT DISTINCT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(SELECT 1) FROM TEST) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 11 ORDER BY __row_number__");
        Assertions.assertEquals(sqlServer2005Dialect.buildPaginationSql(" SELECT DISTINCT *,(SELECT 1) FROM TEST", 1, 10).getDialectSql(),
            "WITH selectTemp AS (SELECT DISTINCT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(SELECT 1) FROM TEST) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 11 ORDER BY __row_number__");

        Assertions.assertEquals(sqlServer2005Dialect.buildPaginationSql("SELECT *,(SELECT 1) FROM TEST", 1, 10).getDialectSql(),
            "WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(SELECT 1) FROM TEST) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 11 ORDER BY __row_number__");
        Assertions.assertEquals(sqlServer2005Dialect.buildPaginationSql(" SELECT *,(SELECT 1) FROM TEST", 1, 10).getDialectSql(),
            "WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(SELECT 1) FROM TEST) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 11 ORDER BY __row_number__");

        Assertions.assertEquals(sqlServer2005Dialect.buildPaginationSql("SELECT * FROM TEST", 1, 10).getDialectSql(),
            "WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  * FROM TEST) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 11 ORDER BY __row_number__");
        Assertions.assertEquals(sqlServer2005Dialect.buildPaginationSql(" SELECT * FROM TEST", 1, 10).getDialectSql(),
            "WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  * FROM TEST) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 2 AND 11 ORDER BY __row_number__");

    }

}
