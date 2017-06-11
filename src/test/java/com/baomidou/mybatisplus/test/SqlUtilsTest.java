package com.baomidou.mybatisplus.test;

import org.junit.Assert;
import org.junit.Test;

import com.baomidou.mybatisplus.entity.CountOptimize;
import com.baomidou.mybatisplus.toolkit.SqlUtils;

/**
 * <p>
 * 测试SqlUtils工具类
 * </p>
 *
 * @author Caratacus
 * @Date 2016-11-3
 */
public class SqlUtilsTest {

    /**
     * 测试jsqlparser方式
     */
    @Test
    public void sqlCountOptimize1() {

        CountOptimize countOptimize = SqlUtils
                .getCountOptimize(
                        "select * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 order by (select 1 from dual)",
                        "jsqlparser", "mysql", true);
        String countsql = countOptimize.getCountSQL();
        boolean orderBy = countOptimize.isOrderBy();
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
        CountOptimize countOptimize = SqlUtils
                .getCountOptimize(
                        "select distinct * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 order by (select 1 from dual)",
                        "jsqlparser", "mysql", true);
        String countsql = countOptimize.getCountSQL();
        boolean orderBy = countOptimize.isOrderBy();
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
        CountOptimize countOptimize = SqlUtils
                .getCountOptimize(
                        "select * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 group by a.id order by (select 1 from dual)",
                        "jsqlparser", "mysql", true);
        String countsql = countOptimize.getCountSQL();
        boolean orderBy = countOptimize.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertFalse(orderBy);
        Assert.assertEquals(
                "SELECT COUNT(1) FROM ( SELECT * FROM user a LEFT JOIN (SELECT uuid FROM user2) b ON b.id = a.aid WHERE a = 1 GROUP BY a.id ORDER BY (SELECT 1 FROM dual) ) TOTAL",
                countsql);
    }

    /**
     * 测试default方式
     */
    @Test
    public void sqlCountOptimize4() {
        CountOptimize countOptimize = SqlUtils
                .getCountOptimize(
                        "select * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 group by a.id order by (select 1 from dual)",
                        "default", "mysql", false);
        String countsql = countOptimize.getCountSQL();
        boolean orderBy = countOptimize.isOrderBy();
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
        CountOptimize countOptimize = SqlUtils.getCountOptimize("select * from test where 1= 1 order by id ", "default", "mysql",
                true);
        String countsql = countOptimize.getCountSQL();
        boolean orderBy = countOptimize.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertFalse(orderBy);
        Assert.assertEquals("SELECT COUNT(1) from test where 1= 1 ", countsql);
    }

    /**
     * 测试default方式
     */
    @Test
    public void sqlCountOptimize6() {
        CountOptimize countOptimize = SqlUtils.getCountOptimize("select * from test where 1= 1 order by id ", "default", "mysql",
                false);
        String countsql = countOptimize.getCountSQL();
        boolean orderBy = countOptimize.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertFalse(orderBy);
        Assert.assertEquals("SELECT COUNT(1) FROM ( select * from test where 1= 1 order by id  ) TOTAL", countsql);
    }

    /**
     * 测试default方式
     */
    @Test
    public void sqlCountOptimize7() {
        CountOptimize countOptimize = SqlUtils.getCountOptimize("select * from test where 1= 1 ", "default", "mysql", false);
        String countsql = countOptimize.getCountSQL();
        boolean orderBy = countOptimize.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertTrue(orderBy);
        Assert.assertEquals("SELECT COUNT(1) FROM ( select * from test where 1= 1  ) TOTAL", countsql);
    }

    /**
     * 测试aliDruid方式
     */
    @Test
    public void sqlCountOptimize8() {
        CountOptimize countOptimize = SqlUtils.getCountOptimize("select * from test where 1= 1 order by id ", "aliDruid",
                "mysql", false);
        String countsql = countOptimize.getCountSQL();
        boolean orderBy = countOptimize.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertFalse(orderBy);
        Assert.assertEquals("SELECT COUNT(*)\n" + "FROM test\n" + "WHERE 1 = 1", countsql);
    }

    /**
     * 测试aliDruid方式
     */
    @Test
    public void sqlCountOptimize9() {
        CountOptimize countOptimize = SqlUtils.getCountOptimize("select * from test where 1= 1 ", "aliDruid", "mysql", false);
        String countsql = countOptimize.getCountSQL();
        boolean orderBy = countOptimize.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertTrue(orderBy);
        Assert.assertEquals("SELECT COUNT(*)\n" + "FROM test\n" + "WHERE 1 = 1", countsql);
    }

    /**
     * 测试aliDruid方式
     */
    @Test
    public void sqlCountOptimize10() {

        CountOptimize countOptimize = SqlUtils
                .getCountOptimize(
                        "select * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 order by (select 1 from dual)",
                        "aliDruid", "mysql", true);
        String countsql = countOptimize.getCountSQL();
        boolean orderBy = countOptimize.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertFalse(orderBy);
        Assert.assertEquals("SELECT COUNT(*)\n" + "FROM user a\n" + "\tLEFT JOIN (SELECT uuid\n" + "\t\tFROM user2\n"
                + "\t\t) b ON b.id = a.aid\n" + "WHERE a = 1", countsql);

    }

    /**
     * 测试aliDruid方式
     */
    @Test
    public void sqlCountOptimize11() {
        CountOptimize countOptimize = SqlUtils
                .getCountOptimize(
                        "select distinct * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 order by (select 1 from dual)",
                        "aliDruid", "mysql", true);
        String countsql = countOptimize.getCountSQL();
        boolean orderBy = countOptimize.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertFalse(orderBy);
        Assert.assertEquals("SELECT COUNT(DISTINCT *)\n" + "FROM user a\n" + "\tLEFT JOIN (SELECT uuid\n" + "\t\tFROM user2\n"
                + "\t\t) b ON b.id = a.aid\n" + "WHERE a = 1", countsql);
    }

    /**
     * 测试aliDruid方式
     */
    @Test
    public void sqlCountOptimize12() {
        CountOptimize countOptimize = SqlUtils
                .getCountOptimize(
                        "select * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 group by a.id order by (select 1 from dual)",
                        "aliDruid", "mysql", true);
        String countsql = countOptimize.getCountSQL();
        boolean orderBy = countOptimize.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertFalse(orderBy);
        Assert.assertEquals("SELECT COUNT(*)\n" + "FROM (SELECT *\n" + "\tFROM user a\n" + "\t\tLEFT JOIN (SELECT uuid\n"
                + "\t\t\tFROM user2\n" + "\t\t\t) b ON b.id = a.aid\n" + "\tWHERE a = 1\n" + "\tGROUP BY a.id\n"
                + "\t) ALIAS_COUNT", countsql);
    }

    /**
     * 测试aliDruid方式
     */
    @Test
    public void sqlCountOptimize13() {
        CountOptimize countOptimize = SqlUtils
                .getCountOptimize(
                        "select * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 group by a.id order by (select 1 from dual)",
                        "aliDruid", "mysql", false);
        String countsql = countOptimize.getCountSQL();
        boolean orderBy = countOptimize.isOrderBy();
        System.out.println(countsql);
        System.out.println(orderBy);
        Assert.assertFalse(orderBy);
        Assert.assertEquals("SELECT COUNT(*)\n" + "FROM (SELECT *\n" + "\tFROM user a\n" + "\t\tLEFT JOIN (SELECT uuid\n"
                + "\t\t\tFROM user2\n" + "\t\t\t) b ON b.id = a.aid\n" + "\tWHERE a = 1\n" + "\tGROUP BY a.id\n"
                + "\t) ALIAS_COUNT", countsql);
    }

}
