package com.baomidou.mybatisplus.test.sql;

import org.junit.Assert;
import org.junit.Test;

import com.baomidou.mybatisplus.plugins.pagination.optimize.JsqlParserCountOptimize;
import com.baomidou.mybatisplus.plugins.parser.SqlInfo;

/**
 * <p>
 * 测试SqlUtils工具类
 * </p>
 *
 * @author Caratacus
 * @since 2016-11-3
 */
public class SqlUtilsTest {

    public SqlInfo jsqlParserCountSqlInfo(String sql) {
        return new JsqlParserCountOptimize().optimizeSql(null, sql);
    }

    /**
     * 测试jsqlparser方式
     */
    @Test
    public void sqlCountOptimize1() {
        SqlInfo sqlInfo = jsqlParserCountSqlInfo(
                "select * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 order by (select 1 from dual)");
        String countsql = sqlInfo.getSql();
        boolean orderBy = sqlInfo.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertFalse(orderBy);
        Assert.assertEquals("SELECT COUNT(1) FROM user a LEFT JOIN (SELECT uuid FROM user2) b ON b.id = a.aid WHERE a = 1",
                countsql);

    }

    /**
     * 测试jsqlparser方式
     */
    @Test
    public void sqlCountOptimize2() {
        SqlInfo sqlInfo = jsqlParserCountSqlInfo(
                "select distinct * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 order by (select 1 from dual)"
        );
        String countsql = sqlInfo.getSql();
        boolean orderBy = sqlInfo.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertFalse(orderBy);
        Assert.assertEquals(
                "SELECT COUNT(1) FROM ( SELECT DISTINCT * FROM user a LEFT JOIN (SELECT uuid FROM user2) b ON b.id = a.aid WHERE a = 1 ) TOTAL",
                countsql);
    }

    /**
     * 测试jsqlparser方式
     */
    @Test
    public void sqlCountOptimize3() {
        SqlInfo sqlInfo = jsqlParserCountSqlInfo(
                "select * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 group by a.id order by (select 1 from dual)"
        );
        String countsql = sqlInfo.getSql();
        boolean orderBy = sqlInfo.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertTrue(orderBy);
        Assert.assertEquals(
                "SELECT COUNT(1) FROM ( SELECT * FROM user a LEFT JOIN (SELECT uuid FROM user2) b ON b.id = a.aid WHERE a = 1 GROUP BY a.id ORDER BY (SELECT 1 FROM dual) ) TOTAL",
                countsql);
    }


}
