package com.baomidou.mybatisplus.test.plugins.pagination;

import com.baomidou.mybatisplus.core.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.plugins.pagination.dialects.SQLServer2005Dialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author nieqiurong
 */
public class SQLServer2005DialectTest {

    private final SQLServer2005Dialect sqlServer2005Dialect = new SQLServer2005Dialect();

    private final Page<?> page = Page.of(1, 10);

    @Test
    void test() {
        Assertions.assertEquals("WITH selectTemp AS (SELECT DISTINCT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(select 1) from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 1 AND 10 ORDER BY __row_number__",
            sqlServer2005Dialect.buildPaginationSql("select distinct *,(select 1) from test", page).getDialectSql());
        Assertions.assertEquals("WITH selectTemp AS (SELECT DISTINCT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(select 1) from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 1 AND 10 ORDER BY __row_number__",
            sqlServer2005Dialect.buildPaginationSql(" select distinct *,(select 1) from test", page).getDialectSql());

        Assertions.assertEquals("WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(select 1) from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 1 AND 10 ORDER BY __row_number__",
            sqlServer2005Dialect.buildPaginationSql("select *,(select 1) from test", page).getDialectSql());
        Assertions.assertEquals("WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(select 1) from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 1 AND 10 ORDER BY __row_number__",
            sqlServer2005Dialect.buildPaginationSql(" select *,(select 1) from test", page).getDialectSql());

        Assertions.assertEquals("WITH selectTemp AS (SELECT DISTINCT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(select 1) from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 1 AND 10 ORDER BY __row_number__",
            sqlServer2005Dialect.buildPaginationSql("select   distinct   *,(select 1) from test", page).getDialectSql());
        Assertions.assertEquals("WITH selectTemp AS (SELECT DISTINCT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(select 1) from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 1 AND 10 ORDER BY __row_number__",
            sqlServer2005Dialect.buildPaginationSql(" select   distinct *,(select 1) from test", page).getDialectSql());

        Assertions.assertEquals("WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(select 1) from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 1 AND 10 ORDER BY __row_number__",
            sqlServer2005Dialect.buildPaginationSql("select   *,(select 1) from test", page).getDialectSql());
        Assertions.assertEquals("WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(select 1) from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 1 AND 10 ORDER BY __row_number__",
            sqlServer2005Dialect.buildPaginationSql(" select   *,(select 1) from test", page).getDialectSql());

        Assertions.assertEquals("WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  * from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 1 AND 10 ORDER BY __row_number__",
            sqlServer2005Dialect.buildPaginationSql("select * from test", page).getDialectSql());
        Assertions.assertEquals("WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  * from test) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 1 AND 10 ORDER BY __row_number__",
            sqlServer2005Dialect.buildPaginationSql(" select * from test", page).getDialectSql());

        Assertions.assertEquals("WITH selectTemp AS (SELECT DISTINCT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(SELECT 1) FROM TEST) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 1 AND 10 ORDER BY __row_number__",
            sqlServer2005Dialect.buildPaginationSql("SELECT DISTINCT *,(SELECT 1) FROM TEST", page).getDialectSql());
        Assertions.assertEquals("WITH selectTemp AS (SELECT DISTINCT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(SELECT 1) FROM TEST) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 1 AND 10 ORDER BY __row_number__",
            sqlServer2005Dialect.buildPaginationSql(" SELECT DISTINCT *,(SELECT 1) FROM TEST", page).getDialectSql());

        Assertions.assertEquals("WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(SELECT 1) FROM TEST) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 1 AND 10 ORDER BY __row_number__",
            sqlServer2005Dialect.buildPaginationSql("SELECT *,(SELECT 1) FROM TEST", page).getDialectSql());
        Assertions.assertEquals("WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  *,(SELECT 1) FROM TEST) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 1 AND 10 ORDER BY __row_number__",
            sqlServer2005Dialect.buildPaginationSql(" SELECT *,(SELECT 1) FROM TEST", page).getDialectSql());

        Assertions.assertEquals("WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  * FROM TEST) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 1 AND 10 ORDER BY __row_number__",
            sqlServer2005Dialect.buildPaginationSql("SELECT * FROM TEST", page).getDialectSql());
        Assertions.assertEquals("WITH selectTemp AS (SELECT TOP 100 PERCENT  ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __row_number__,  * FROM TEST) SELECT * FROM selectTemp WHERE __row_number__ BETWEEN 1 AND 10 ORDER BY __row_number__",
            sqlServer2005Dialect.buildPaginationSql(" SELECT * FROM TEST", page).getDialectSql());

    }

}
