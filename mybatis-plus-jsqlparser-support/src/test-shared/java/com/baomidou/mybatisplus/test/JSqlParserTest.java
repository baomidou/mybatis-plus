package com.baomidou.mybatisplus.test;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Properties;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SQL 解析测试
 */
@Tag("unit")
class JSqlParserTest {

    @Test
    void parser() throws Exception {
        Select select = (Select) CCJSqlParserUtil
                .parse("SELECT a,b,c FROM tableName t WHERE t.col = 9 and b=c LIMIT 3, ?");

        PlainSelect ps = (PlainSelect) select;

        assertThat(ps.getWhere().toString()).isEqualTo("t.col = 9 AND b = c");
        assertThat(ps.getSelectItems().get(1).toString()).isEqualTo("b");
        AndExpression e = (AndExpression) ps.getWhere();
        assertThat(e.getLeftExpression().toString()).isEqualTo("t.col = 9");
    }

    @Test
    void shouldParseDecrementInUpdate() throws JSQLParserException {
        // 如果连一起 SqlParser 将无法解析 , 还有种处理方式就自减为负数的时候 转为 自增.
        var parse1 = CCJSqlParserUtil.parse("UPDATE test SET a = a --110");
        assertThat(parse1.toString()).isEqualTo("UPDATE test SET a = a");
        var parse2 = CCJSqlParserUtil.parse("UPDATE test SET a = a - -110");
        assertThat(parse2.toString()).isEqualTo("UPDATE test SET a = a - -110");
    }

    @Test
    void shouldParseIncrementInUpdate() throws JSQLParserException {
        var parse1 = CCJSqlParserUtil.parse("UPDATE test SET a = a +-110");
        assertThat(parse1.toString()).isEqualTo("UPDATE test SET a = a + -110");
        var parse2 = CCJSqlParserUtil.parse("UPDATE test SET a = a + -110");
        assertThat(parse2.toString()).isEqualTo("UPDATE test SET a = a + -110");
    }

    @Test
    void notLikeParser() throws Exception {
        final String targetSql = "SELECT * FROM tableName WHERE id NOT LIKE ?";
        Select select = (Select) CCJSqlParserUtil.parse(targetSql);
        assertThat(select.toString()).isEqualTo(targetSql);
    }

    @Test
    void updateWhereParser() throws Exception {
        Update update = (Update) CCJSqlParserUtil
                .parse("Update tableName t SET t.a=(select c from tn where tn.id=t.id),b=2,c=3 ");
        assertThat(update.getWhere()).isNull();
    }

    @Test
    void deleteWhereParser() throws Exception {
        Delete delete = (Delete) CCJSqlParserUtil.parse("delete from tableName t");
        assertThat(delete.getWhere()).isNull();
    }

    @ParameterizedTest
    @MethodSource("selectForUpdateCases")
    void shouldParseSelectForUpdate(String sql, String expectedSql) throws JSQLParserException {
        assertThat(CCJSqlParserUtil.parse(sql).toString()).isEqualTo(expectedSql);
    }

    private static Stream<Arguments> selectForUpdateCases() throws IOException {
        Properties parser = new Properties();
        try (var metadata = CCJSqlParserUtil.class
                .getResourceAsStream("/META-INF/maven/com.github.jsqlparser/jsqlparser/pom.properties")) {
            parser.load(metadata);
        }
        String version = parser.getProperty("version");
        String earlyLock = " FOR UPDATE ORDER BY submit_time ASC LIMIT 10";
        String lateLock = " ORDER BY submit_time ASC LIMIT 10 FOR UPDATE";
        String select = "SELECT * FROM sys_sms_send_record WHERE check_status = 0";
        // 5.0 和 5.2 覆盖旧写法，4.9 将锁子句输出在 ORDER BY 之前。
        String input = "5.0".equals(version) || "5.2".equals(version) ? earlyLock : lateLock;
        String expected = "4.9".equals(version) ? earlyLock : lateLock;
        return Stream.of(
                Arguments.of(
                        "select * from t_demo where a = 1 for update", "SELECT * FROM t_demo WHERE a = 1 FOR UPDATE"),
                Arguments.of(select + input, select + expected));
    }

}
