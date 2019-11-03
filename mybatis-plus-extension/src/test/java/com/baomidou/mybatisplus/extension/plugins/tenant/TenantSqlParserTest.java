package com.baomidou.mybatisplus.extension.plugins.tenant;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2019-11-02
 */

public class TenantSqlParserTest {

    private final TenantSqlParser parser = new TenantSqlParser()
        .setTenantHandler(new TenantHandler() {
            @Override
            public Expression getTenantId(boolean where) {
                return new LongValue(1);
            }

            @Override
            public String getTenantIdColumn() {
                return "t_id";
            }

            @Override
            public boolean doTableFilter(String tableName) {
                return false;
            }
        });

    @Test
    public void processSelectBody() throws JSQLParserException {
        select("select * from user",
            "select * from user where user.t_id = 1");
        select("select * from user where id in (select id from user)",
            "select * from user where id in (select id from user where user.t_id = 1) and user.t_id = 1");
        select("select * from user where id = 1 and id in (select id from user)",
            "select * from user where id = 1 and id in (select id from user where user.t_id = 1) and user.t_id = 1");
        select("select * from user where id = 1 or id in (select id from user)",
            "select * from user where (id = 1 or id in (select id from user where user.t_id = 1)) and user.t_id = 1");
    }

    private void select(String sql, String target) throws JSQLParserException {
        Statements statement = CCJSqlParserUtil.parseStatements(sql);
        Select select = (Select) statement.getStatements().get(0);
        parser.processSelectBody(select.getSelectBody());
        assertThat(select.toString().toLowerCase()).isEqualTo(target);
    }
}
