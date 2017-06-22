package com.baomidou.mybatisplus.test;

import org.junit.Assert;
import org.junit.Test;

import com.baomidou.mybatisplus.plugins.pagination.optimize.AliDruidCountOptimize;
import com.baomidou.mybatisplus.plugins.pagination.optimize.DefaultCountOptimize;
import com.baomidou.mybatisplus.plugins.pagination.optimize.JsqlParserCountOptimize;
import com.baomidou.mybatisplus.parser.SqlInfo;

/**
 * <p>
 * 测试SqlUtils工具类
 * </p>
 *
 * @author Caratacus
 * @Date 2016-11-3
 */
public class SqlUtilsTest {

    public SqlInfo jsqlParserCountSqlInfo(String sql) {
        return new JsqlParserCountOptimize().optimizeSql(sql, "mysql");
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


    public SqlInfo defaultCountSqlInfo(String sql) {
        return new DefaultCountOptimize().optimizeSql(sql, "mysql");
    }

    /**
     * 测试default方式
     */
    @Test
    public void sqlCountOptimize4() {
        SqlInfo sqlInfo = defaultCountSqlInfo(
                "select * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 group by a.id order by (select 1 from dual)");
        String countsql = sqlInfo.getSql();
        boolean orderBy = sqlInfo.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertFalse(orderBy);
        Assert.assertEquals(
                "SELECT COUNT(1) FROM ( select * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 group by a.id order by (select 1 from dual) ) TOTAL",
                countsql);
    }

    /**
     * 测试default方式
     */
    @Test
    public void sqlCountOptimize5() {
        SqlInfo sqlInfo = defaultCountSqlInfo("select * from test where 1= 1 order by id ");
        String countsql = sqlInfo.getSql();
        boolean orderBy = sqlInfo.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertFalse(orderBy);
        Assert.assertEquals("SELECT COUNT(1) from test where 1= 1 ", countsql);
    }

    /**
     * 测试default方式
     */
    @Test
    public void sqlCountOptimize7() {
        SqlInfo sqlInfo = defaultCountSqlInfo("select * from test where 1= 1 ");
        String countsql = sqlInfo.getSql();
        boolean orderBy = sqlInfo.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertTrue(orderBy);
        Assert.assertEquals("SELECT COUNT(1) from test where 1= 1 ", countsql);
    }


    public SqlInfo aliDruidCountSqlInfo(String sql) {
        return new AliDruidCountOptimize().optimizeSql(sql, "mysql");
    }

    /**
     * 测试aliDruid方式
     */
    @Test
    public void sqlCountOptimize8() {
        SqlInfo sqlInfo = aliDruidCountSqlInfo("select * from test where 1= 1 order by id ");
        String countsql = sqlInfo.getSql();
        boolean orderBy = sqlInfo.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertTrue(orderBy);
        Assert.assertEquals("SELECT COUNT(*)\n" + "FROM test\n" + "WHERE 1 = 1", countsql);
    }

    /**
     * 测试aliDruid方式
     */
    @Test
    public void sqlCountOptimize9() {
        SqlInfo sqlInfo = aliDruidCountSqlInfo("select * from test where 1= 1 ");
        String countsql = sqlInfo.getSql();
        boolean orderBy = sqlInfo.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertFalse(orderBy);
        Assert.assertEquals("SELECT COUNT(*)\n" + "FROM test\n" + "WHERE 1 = 1", countsql);
    }

    /**
     * 测试aliDruid方式
     */
    @Test
    public void sqlCountOptimize10() {
        SqlInfo sqlInfo = aliDruidCountSqlInfo("select * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 order by (select 1 from dual)");

        String countsql = sqlInfo.getSql();
        boolean orderBy = sqlInfo.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertTrue(orderBy);
        Assert.assertEquals("SELECT COUNT(*)\n" + "FROM user a\n" + "\tLEFT JOIN (SELECT uuid\n" + "\t\tFROM user2\n"
                + "\t\t) b ON b.id = a.aid\n" + "WHERE a = 1", countsql);

    }

    /**
     * 测试aliDruid方式
     */
    @Test
    public void sqlCountOptimize11() {
        SqlInfo sqlInfo = aliDruidCountSqlInfo("select distinct * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 order by (select 1 from dual)");
        String countsql = sqlInfo.getSql();
        boolean orderBy = sqlInfo.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertTrue(orderBy);
        Assert.assertEquals("SELECT COUNT(DISTINCT *)\n" + "FROM user a\n" + "\tLEFT JOIN (SELECT uuid\n" + "\t\tFROM user2\n"
                + "\t\t) b ON b.id = a.aid\n" + "WHERE a = 1", countsql);
    }

    /**
     * 测试aliDruid方式
     */
    @Test
    public void sqlCountOptimize12() {
        SqlInfo sqlInfo = aliDruidCountSqlInfo("select * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 group by a.id order by (select 1 from dual)");
        String countsql = sqlInfo.getSql();
        boolean orderBy = sqlInfo.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertTrue(orderBy);
        Assert.assertEquals("SELECT COUNT(*)\n" + "FROM (SELECT *\n" + "\tFROM user a\n" + "\t\tLEFT JOIN (SELECT uuid\n"
                + "\t\t\tFROM user2\n" + "\t\t\t) b ON b.id = a.aid\n" + "\tWHERE a = 1\n" + "\tGROUP BY a.id\n"
                + "\t) ALIAS_COUNT", countsql);
    }

}
