package com.baomidou.mybatisplus.test.sql;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.plugins.parser.SqlInfo;
import com.baomidou.mybatisplus.plugins.parser.tenant.TenantSqlParser;
import com.baomidou.mybatisplus.plugins.parser.tenant.TenantHandler;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

/**
 * <p>
 * 租户 SQL 测试
 * </p>
 *
 * @author hubin
 * @since 2017-09-01
 */
public class TenantSqlTest {

    private TenantSqlParser tenantSqlParser;

    @Before
    public void setUp() throws Exception {
        tenantSqlParser = new TenantSqlParser();
        tenantSqlParser.setTenantHandler(new TenantHandler() {
            @Override
            public Expression getTenantId() {
                return new LongValue(1000L);
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
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

    // ----------------------------    insert 测试     ----------------------------
    @Test
    public void intsertFilter() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "INSERT INTO user (c1, c2, c3) VALUES (?, 'asd', 123)");
        Assert.assertEquals("INSERT INTO user (c1, c2, c3) VALUES (?, 'asd', 123)", sqlInfo.getSql());
    }

    @Test
    public void intsert() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "INSERT INTO role (c1, c2, c3) VALUES (?, 'asd', 123)");
        Assert.assertEquals("INSERT INTO role (c1, c2, c3, tenant_id) VALUES (?, 'asd', 123, 1000)", sqlInfo.getSql());
    }

    @Test
    public void intsertInto() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "insert into role (c1, c2, c3) SELECT * FROM bak");
        System.out.println(sqlInfo.getSql());
        Assert.assertEquals("INSERT INTO role (c1, c2, c3, tenant_id) SELECT *, tenant_id FROM bak WHERE tenant_id = 1000", sqlInfo.getSql());
    }

    // ----------------------------    delete 测试     ----------------------------
    @Test
    public void deleteFilter() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "DELETE FROM user t WHERE t.c1 = 9");
        Assert.assertEquals("DELETE FROM user t WHERE t.c1 = 9", sqlInfo.getSql());
    }

    @Test(expected = MybatisPlusException.class)
    public void deleteLimit() {
        tenantSqlParser.optimizeSql(null, "delete FROM role WHERE A.cod_table = 'YYY' LIMIT 3,4");
    }

    @Test
    public void deleteOrderBy() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "DELETE FROM tablename WHERE a = 1 AND b = 1 ORDER BY col");
        Assert.assertEquals("DELETE FROM tablename WHERE tenant_id = 1000 AND a = 1 AND b = 1 ORDER BY col", sqlInfo.getSql());
    }

    @Test
    public void deleteJoin() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "DELETE gc FROM guide_category AS gc LEFT JOIN guide AS g ON g.id_guide = gc.id_guide WHERE g.title IS NULL LIMIT 5");
        Assert.assertEquals("DELETE gc FROM guide_category AS gc LEFT JOIN guide AS g ON g.id_guide = gc.id_guide WHERE gc.tenant_id = 1000 LIMIT 5", sqlInfo.getSql());
    }

    // ----------------------------    update 测试     ----------------------------
    @Test
    public void updateFilter() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "UPDATE user set c1='as', c2=?, c3=565 Where o >= 3");
        Assert.assertEquals("UPDATE user SET c1 = 'as', c2 = ?, c3 = 565 WHERE o >= 3", sqlInfo.getSql());
    }

    @Test
    public void updateSet() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "UPDATE role set c1='as', c2=?, c3=565 Where o >= 3");
        Assert.assertEquals("UPDATE role SET c1 = 'as', c2 = ?, c3 = 565 WHERE tenant_id = 1000 AND o >= 3", sqlInfo.getSql());
    }

    @Test
    public void updateSetJoin() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "UPDATE role SET c1 = 5 FROM role LEFT JOIN user ON c1 = c2 Where o >= 3");
        Assert.assertEquals("UPDATE role SET c1 = 5 FROM role LEFT JOIN user ON c1 = c2 WHERE tenant_id = 1000 AND o >= 3", sqlInfo.getSql());
    }

    // ----------------------------    select 测试     ----------------------------
    @Test
    public void selectFilter() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "select * from user");
        Assert.assertEquals("SELECT * FROM user", sqlInfo.getSql());
    }

    @Test
    public void selectFilter1() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "select u.name,r.id FROM user u, role r");
        Assert.assertEquals("SELECT u.name, r.id FROM user u, role r", sqlInfo.getSql());
    }

    @Test
    public void selectChild() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "select aaa, bbb,(select ccc from user) as ccc from role");
        Assert.assertEquals("SELECT aaa, bbb, (SELECT ccc FROM user) AS ccc FROM role WHERE tenant_id = 1000", sqlInfo.getSql());
    }

    @Test
    public void selectChild1() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "select name from role where  id= (select rid from user where id=1 )");
        Assert.assertEquals("SELECT name FROM role WHERE tenant_id = 1000 AND id = (SELECT rid FROM user WHERE id = 1)", sqlInfo.getSql());
    }

    @Test
    public void selectJoin() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "SELECT u.aaa, r.bbb FROM role r left JOIN user u WHERE r.id=u.id");
        Assert.assertEquals("SELECT u.aaa, r.bbb FROM role r LEFT JOIN user u WHERE r.tenant_id = 1000 AND r.id = u.id", sqlInfo.getSql());
    }

    @Test
    public void selectJoin1() {
        SqlInfo sqlInfo = tenantSqlParser.optimizeSql(null, "SELECT u.aaa, r.bbb FROM role r LEFT JOIN user u ON r.id=u.id LEFT JOIN teacher t ON r.id=t.id ");
        Assert.assertEquals("SELECT u.aaa, r.bbb FROM role r LEFT JOIN user u ON r.id = u.id LEFT JOIN teacher t ON t.tenant_id = 1000 AND r.id = t.id WHERE r.tenant_id = 1000", sqlInfo.getSql());
    }
}
