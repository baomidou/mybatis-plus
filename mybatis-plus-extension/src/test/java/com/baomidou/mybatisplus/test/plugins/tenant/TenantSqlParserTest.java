package com.baomidou.mybatisplus.test.plugins.tenant;

import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.ValueListExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
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
            public Expression getTenantId(boolean select) {
                if (!select) {
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

        select("select * from user group by date(date_time)",
            "select * from user where t_id in (1, 2) group by date(date_time)");

        update("update user set age = 1",
            "update user set age = 1 where t_id = 1");

        insert("insert into user (id, age) values (?, ?)",
            "insert into user (id, age, t_id) values (?, ?, 1)");
    }

    private void select(String sql, String target) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        Select select = (Select) statement;
        parser.processSelectBody(select.getSelectBody());
        assertThat(select.toString().toLowerCase()).isEqualTo(target);
    }

    private void update(String sql, String target) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        Update update = (Update) statement;
        parser.processUpdate(update);
        assertThat(update.toString().toLowerCase()).isEqualTo(target);
    }

    private void insert(String sql, String target) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        Insert insert = (Insert) statement;
        parser.processInsert(insert);
        assertThat(insert.toString().toLowerCase()).isEqualTo(target);
    }
}
