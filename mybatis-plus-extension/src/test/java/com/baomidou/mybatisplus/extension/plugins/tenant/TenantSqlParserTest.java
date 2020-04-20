package com.baomidou.mybatisplus.extension.plugins.tenant;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.ValueListExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
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
                if (!where) {
                    return new LongValue(1);
                }
                ValueListExpression expression = new ValueListExpression();
                ExpressionList list = new ExpressionList(new LongValue(1), new LongValue(2));
                expression.setExpressionList(list);
                return expression;
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
            "select * from user where t_id in (1, 2)");
        select("select * from user u",
            "select * from user u where u.t_id in (1, 2)");
        select("select * from user where id in (select id from user)",
            "select * from user where id in (select id from user where t_id in (1, 2)) and t_id in (1, 2)");
        select("select * from user where id = 1 and id in (select id from user)",
            "select * from user where id = 1 and id in (select id from user where t_id in (1, 2)) and t_id in (1, 2)");
        select("select * from user where id = 1 or id in (select id from user)",
            "select * from user where (id = 1 or id in (select id from user where t_id in (1, 2))) and t_id in (1, 2)");

        update("update user set age = 1",
            "update user set age = 1 where t_id = 1");
    }

    private void select(String sql, String target) throws JSQLParserException {
        Statements statement = CCJSqlParserUtil.parseStatements(sql);
        Select select = (Select) statement.getStatements().get(0);
        parser.processSelectBody(select.getSelectBody());
        assertThat(select.toString().toLowerCase()).isEqualTo(target);
    }

    private void update(String sql, String target) throws JSQLParserException {
        Statements statement = CCJSqlParserUtil.parseStatements(sql);
        Update update = (Update) statement.getStatements().get(0);
        parser.processUpdate(update);
        assertThat(update.toString().toLowerCase()).isEqualTo(target);
    }
}
