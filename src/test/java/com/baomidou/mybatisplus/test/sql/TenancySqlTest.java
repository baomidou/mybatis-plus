package com.baomidou.mybatisplus.test.sql;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.baomidou.mybatisplus.plugins.parser.SqlInfo;
import com.baomidou.mybatisplus.plugins.tenancy.TenancySqlParser;
import com.baomidou.mybatisplus.plugins.tenancy.TenantHandler;

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
public class TenancySqlTest {

    private TenancySqlParser tenancySqlParser;

    @Before
    public void setUp() throws Exception {
        tenancySqlParser = new TenancySqlParser();
        tenancySqlParser.setTenantHandler(new TenantHandler() {
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

    @Test
    public void filter() {
        SqlInfo sqlInfo = tenancySqlParser.optimizeSql(null, "select * from user");
        Assert.assertEquals("SELECT * FROM user", sqlInfo.getSql());
    }

    @Test
    public void test() {
        SqlInfo sqlInfo = tenancySqlParser.optimizeSql(null, "select aaa, bbb,(select ccc from role) as ccc from user");
        Assert.assertEquals("SELECT aaa, bbb, (SELECT ccc FROM role) AS ccc FROM user WHERE user.tenant_id = 1000", sqlInfo.getSql());
    }

    @Test
    public void join() {
        SqlInfo sqlInfo = tenancySqlParser.optimizeSql(null, "SELECT u.aaa, r.bbb FROM role r left JOIN user u WHERE r.id=u.id");
        Assert.assertEquals("SELECT u.aaa, r.bbb FROM role r LEFT JOIN user u WHERE r.tenant_id = 1000 AND r.id = u.id", sqlInfo.getSql());
    }
}
