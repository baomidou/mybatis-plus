package com.baomidou.mybatisplus.test.sql;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.baomidou.mybatisplus.plugins.parser.SqlInfo;
import com.baomidou.mybatisplus.plugins.parser.logicdelete.LogicDeleteHandler;
import com.baomidou.mybatisplus.plugins.parser.logicdelete.LogicDeleteSqlParser;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;

/**
 * <p>
 * 逻辑删除 全SQL 测试
 * </p>
 *
 * @author willenfoo
 * @since 2018-03-09
 */
public class LogicDeleteAllSqlTest {

    private LogicDeleteSqlParser tenantSqlParser;

    @Before
    public void setUp() throws Exception {
        tenantSqlParser = new LogicDeleteSqlParser();
        tenantSqlParser.setLogicDeleteHandler(new LogicDeleteHandler() {

            @Override
            public Expression getValue(String tableName) {
                return new StringValue("N");
            }

            @Override
            public String getColumn(String tableName) {
                return "delete_flag";
            }

            @Override
            public boolean doTableFilter(String tableName) {
                return false;
            }
        });
    }

    // ----------------------------    update 测试     ----------------------------
    @Test
    public void updateFilter() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "UPDATE user set c1='as', c2=?, c3=565 Where o >= 3");
        Assert.assertEquals("UPDATE user SET c1 = 'as', c2 = ?, c3 = 565 WHERE o >= 3 AND delete_flag = 'N'", sqlInfo.getSql());
    }

    @Test
    public void updateSet() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "UPDATE role set c1='as', c2=?, c3=565 Where o >= 3");
        Assert.assertEquals("UPDATE role SET c1 = 'as', c2 = ?, c3 = 565 WHERE o >= 3 AND delete_flag = 'N'", sqlInfo.getSql());
    }

    @Test
    public void updateSetJoin() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "UPDATE role r SET r.c1 = 5 FROM role LEFT JOIN user u ON r.c1 = u.c2 Where o >= 3");
        Assert.assertEquals("UPDATE role r SET r.c1 = 5 FROM role LEFT JOIN user u ON r.c1 = u.c2 AND u.delete_flag = 'N' WHERE o >= 3 AND delete_flag = 'N'", sqlInfo.getSql());
    }

    // ----------------------------    select 测试     ----------------------------
    @Test
    public void selectFilter() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "select * from user");
        Assert.assertEquals("SELECT * FROM user WHERE delete_flag = 'N'", sqlInfo.getSql());
    }

    @Test
    public void selectFilter1() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "select u.name,r.id FROM user u, role r");
        Assert.assertEquals("SELECT u.name, r.id FROM user u, role r WHERE u.delete_flag = 'N'", sqlInfo.getSql());
    }

    @Test
    public void selectChild() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "select aaa, bbb,(select ccc from user) as ccc from role");
        Assert.assertEquals("SELECT aaa, bbb, (SELECT ccc FROM user) AS ccc FROM role WHERE delete_flag = 'N'", sqlInfo.getSql());
    }

    @Test
    public void selectChild1() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "select name from role where  id= (select rid from user where id=1 )");
        Assert.assertEquals("SELECT name FROM role WHERE id = (SELECT rid FROM user WHERE id = 1 AND delete_flag = 'N') AND delete_flag = 'N'", sqlInfo.getSql());
    }

    @Test
    public void selectJoin() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "SELECT u.aaa, r.bbb FROM role r left JOIN user u WHERE r.id=u.id");
        Assert.assertEquals("SELECT u.aaa, r.bbb FROM role r LEFT JOIN user u ON u.delete_flag = 'N' WHERE r.id = u.id AND r.delete_flag = 'N'", sqlInfo.getSql());
    }

    @Test
    public void selectJoin1() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "SELECT u.aaa, r.bbb FROM role r LEFT JOIN user u ON r.id=u.id LEFT JOIN teacher t ON r.id=t.id ");
        Assert.assertEquals("SELECT u.aaa, r.bbb FROM role r LEFT JOIN user u ON r.id = u.id AND u.delete_flag = 'N' LEFT JOIN teacher t ON r.id = t.id AND t.delete_flag = 'N' WHERE r.delete_flag = 'N'", sqlInfo.getSql());
    }
}
