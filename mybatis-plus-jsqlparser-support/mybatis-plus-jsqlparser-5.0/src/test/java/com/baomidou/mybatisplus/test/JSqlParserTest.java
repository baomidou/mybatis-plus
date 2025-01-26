package com.baomidou.mybatisplus.test;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SQL 解析测试
 */
class JSqlParserTest {

    @Test
    void parser() throws Exception {
        Select select = (Select) CCJSqlParserUtil.parse("SELECT a,b,c FROM tableName t WHERE t.col = 9 and b=c LIMIT 3, ?");

        PlainSelect ps = (PlainSelect) select;

        System.out.println(ps.getWhere().toString());
        System.out.println(ps.getSelectItems().get(1).toString());

        AndExpression e = (AndExpression) ps.getWhere();
        System.out.println(e.getLeftExpression());
    }

    @Test
    void testDecr() throws JSQLParserException {
        // 如果连一起 SqlParser 将无法解析 , 还有种处理方式就自减为负数的时候 转为 自增.
        var parse1 = CCJSqlParserUtil.parse("UPDATE test SET a = a --110");
        Assertions.assertEquals("UPDATE test SET a = a", parse1.toString());
        var parse2 = CCJSqlParserUtil.parse("UPDATE test SET a = a - -110");
        Assertions.assertEquals("UPDATE test SET a = a - -110", parse2.toString());
    }

    @Test
    void testIncr() throws JSQLParserException {
        var parse1 = CCJSqlParserUtil.parse("UPDATE test SET a = a +-110");
        Assertions.assertEquals("UPDATE test SET a = a + -110", parse1.toString());
        var parse2 = CCJSqlParserUtil.parse("UPDATE test SET a = a + -110");
        Assertions.assertEquals("UPDATE test SET a = a + -110", parse2.toString());
    }

    @Test
    void notLikeParser() throws Exception {
        final String targetSql = "SELECT * FROM tableName WHERE id NOT LIKE ?";
        Select select = (Select) CCJSqlParserUtil.parse(targetSql);
        assertThat(select.toString()).isEqualTo(targetSql);
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

    @Test
    void testSelectForUpdate() throws Exception {
        Assertions.assertEquals("SELECT * FROM t_demo WHERE a = 1 FOR UPDATE",
            CCJSqlParserUtil.parse("select * from t_demo where a = 1 for update").toString());
        Assertions.assertEquals("SELECT * FROM sys_sms_send_record WHERE check_status = 0 ORDER BY submit_time ASC LIMIT 10 FOR UPDATE",
            CCJSqlParserUtil.parse("select * from sys_sms_send_record where check_status = 0 for update order by submit_time asc limit 10").toString());
    }

}
