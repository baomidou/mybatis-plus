package com.baomidou.mybatisplus.test.sql;

import org.junit.Before;
import org.junit.Test;

import com.baomidou.mybatisplus.plugins.parser.SqlInfo;
import com.baomidou.mybatisplus.plugins.tenancy.TenancySqlParser;
import com.baomidou.mybatisplus.plugins.tenancy.TenantHandler;

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
            public String getTenantId() {
                return "1000";
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }
        });
    }

    @Test
    public void test1() {
        SqlInfo sqlInfo = tenancySqlParser.optimizeSql(null, "select * from user");
        if (null != sqlInfo) {
            System.err.println(sqlInfo.getSql());
        }
    }
}
