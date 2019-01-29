package com.baomidou.mybatisplus.core.test;

import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SQL 解析测试
 */
class JSqlParserTest {


    @Test
    void parser() throws Exception {
        Select select = (Select) CCJSqlParserUtil.parse("SELECT a,b,c FROM tableName t WHERE t.col = 9 and b=c LIMIT 3, ?");

        PlainSelect ps = (PlainSelect) select.getSelectBody();

        System.out.println(ps.getWhere().toString());
        System.out.println(ps.getSelectItems().get(1).toString());

        AndExpression e = (AndExpression) ps.getWhere();
        System.out.println(e.getLeftExpression());
    }


    @Test
    void updateWhereParser() throws Exception {
        Update update = (Update) CCJSqlParserUtil.parse("Update tableName t SET t.a=(select c from tn where tn.id=t.id),b=2,c=3 ");
        Assertions.assertNull(update.getWhere());
    }


    @Test
    void deleteWhereParser() throws Exception {
        Delete delete = (Delete) CCJSqlParserUtil.parse("delete from tableName t");
        Assertions.assertNull(delete.getWhere());
    }
}
