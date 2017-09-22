package com.baomidou.mybatisplus.test.sql;

import com.baomidou.mybatisplus.plugins.parser.SqlInfo;
import com.baomidou.mybatisplus.plugins.parser.logicdelete.LogicDeleteHandler;
import com.baomidou.mybatisplus.plugins.parser.logicdelete.LogicDeleteSqlParser;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * 租户 SQL 测试
 * </p>
 *
 * @author hubin
 * @since 2017-09-01
 */
public class LogicDeleteSqlTest {

    private LogicDeleteSqlParser logicDeleteSqlParser;

    @Before
    public void setUp() throws Exception {
        logicDeleteSqlParser = new LogicDeleteSqlParser();
        logicDeleteSqlParser.setLogicDeleteHandler(new LogicDeleteHandler() {
            @Override
            public Expression getValue(String tableName) {
                return new LongValue(1L);
            }

            @Override
            public String getColumn(String tableName) {
                return "delete_flag";
            }

            @Override
            public boolean doTableFilter(String tableName) {
                if ("user".equals(tableName)) {
                    return true;
                }
                return false;
            }
        });
    }

    // ----------------------------    update 测试     ----------------------------
    @Test
    public void updateFilter() {
        SqlInfo sqlInfo = logicDeleteSqlParser.optimizeSql(null, "UPDATE user set c1='as', c2=?, c3=565 Where o >= 3");
        Assert.assertEquals("UPDATE user SET c1 = 'as', c2 = ?, c3 = 565 WHERE o >= 3", sqlInfo.getSql());
    }

    @Test
    public void updateSet() {
        SqlInfo sqlInfo = logicDeleteSqlParser.optimizeSql(null, "UPDATE role set c1='as', c2=?, c3=565 Where o >= 3");
        Assert.assertEquals("UPDATE role SET c1 = 'as', c2 = ?, c3 = 565 WHERE o >= 3 AND delete_flag = 1", sqlInfo.getSql());
    }

    @Test
    public void updateSetJoin() {
        SqlInfo sqlInfo = logicDeleteSqlParser.optimizeSql(null, "UPDATE role SET c1 = 5 FROM role LEFT JOIN user ON c1 = c2 Where o >= 3");
        Assert.assertEquals("UPDATE role SET c1 = 5 FROM role LEFT JOIN user ON c1 = c2 WHERE o >= 3 AND delete_flag = 1", sqlInfo.getSql());
    }

    // ----------------------------    select 测试     ----------------------------
    @Test
    public void selectFilter() {
        SqlInfo sqlInfo = logicDeleteSqlParser.optimizeSql(null, "select * from user");
        Assert.assertEquals("SELECT * FROM user", sqlInfo.getSql());
    }

    @Test
    public void selectFilter1() {
        SqlInfo sqlInfo = logicDeleteSqlParser.optimizeSql(null, "select u.name,r.id FROM user u, role r");
        Assert.assertEquals("SELECT u.name, r.id FROM user u, role r", sqlInfo.getSql());
    }

    @Test
    public void selectChild() {
        SqlInfo sqlInfo = logicDeleteSqlParser.optimizeSql(null, "select aaa, bbb,(select ccc from user) as ccc from role");
        Assert.assertEquals("SELECT aaa, bbb, (SELECT ccc FROM user) AS ccc FROM role WHERE delete_flag = 1", sqlInfo.getSql());
    }

    @Test
    public void selectChild1() {
        SqlInfo sqlInfo = logicDeleteSqlParser.optimizeSql(null, "SELECT name FROM role WHERE id = (SELECT rid FROM user WHERE id = 1)");
        System.out.println(sqlInfo.getSql());
        Assert.assertEquals("SELECT name FROM role WHERE id = (SELECT rid FROM user WHERE id = 1) AND delete_flag = 1", sqlInfo.getSql());
    }

    @Test
    public void selectJoin() {
        SqlInfo sqlInfo = logicDeleteSqlParser.optimizeSql(null, "SELECT u.aaa, r.bbb FROM role r left JOIN user u WHERE r.id=u.id");
        Assert.assertEquals("SELECT u.aaa, r.bbb FROM role r LEFT JOIN user u WHERE r.id = u.id AND r.delete_flag = 1", sqlInfo.getSql());
    }

    @Test
    public void selectJoin1() {
        SqlInfo sqlInfo = logicDeleteSqlParser.optimizeSql(null, "SELECT u.aaa, r.bbb FROM role r LEFT JOIN user u ON r.id=u.id LEFT JOIN teacher t ON r.id=t.id ");
        Assert.assertEquals("SELECT u.aaa, r.bbb FROM role r LEFT JOIN user u ON r.id = u.id LEFT JOIN teacher t ON r.id = t.id AND t.delete_flag = 1 WHERE r.delete_flag = 1", sqlInfo.getSql());
    }
}
