package com.baomidou.mybatisplus.core.test;

import org.junit.Assert;
import org.junit.Test;

import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

/**
 * SQL 解析测试
 */
public class JSqlParserTest {


    @Test
    public void parser() throws Exception {
        Select select = (Select) CCJSqlParserUtil.parse("SELECT a,b,c FROM tableName t WHERE t.col = 9 and b=c LIMIT 3, ?");

        PlainSelect ps = (PlainSelect) select.getSelectBody();

        System.out.println(ps.getWhere().toString());
        System.out.println(ps.getSelectItems().get(1).toString());

        AndExpression e = (AndExpression) ps.getWhere();
        System.out.println(e.getLeftExpression());
    }


    @Test
    public void updateWhereParser() throws Exception {
        Update update = (Update) CCJSqlParserUtil.parse("Update tableName t SET t.a=(select c from tn where tn.id=t.id),b=2,c=3 ");
        Assert.assertTrue(null == update.getWhere());
    }


    @Test
    public void deleteWhereParser() throws Exception {
        Delete delete = (Delete) CCJSqlParserUtil.parse("delete from tableName t");
        Assert.assertTrue(null == delete.getWhere());
    }
}
