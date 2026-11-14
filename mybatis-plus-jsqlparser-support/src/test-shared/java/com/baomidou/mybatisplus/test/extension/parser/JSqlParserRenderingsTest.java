package com.baomidou.mybatisplus.test.extension.parser;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 每个 JSqlParser 版本模块都会把这份源编进自己的测试类路径：登记的版本画像必须与当前依赖的实际行为逐项吻合。 升级 JSqlParser 后若画像没跟上，这里先红，而不是让共享测试悄悄跑到错误的期望分支上。
 */
@Tag("unit")
class JSqlParserRenderingsTest {

    @Test
    void registeredProfileMatchesTheRenderingOnTheClasspath() throws JSQLParserException {
        assertThat(JSqlParserRenderings.registeredVersions()).contains(JSqlParserRenderings.currentVersion());
        assertThat(rendered("select 1 union all select 2")).contains(JSqlParserRenderings.UNION_ALL);
        // 探针必须带 FROM：4.9 对无 FROM 的 SELECT 会直接丢弃 FOR UPDATE，探不出位置差异
        assertThat(
                rendered("select 1 from t where a = 1 order by x asc limit 10 for update")
                        .contains("FOR UPDATE ORDER BY"))
                .isEqualTo(JSqlParserRenderings.FOR_UPDATE_BEFORE_ORDER_BY);
    }

    @Test
    void registeredProfileMatchesTheParsingCapabilitiesOnTheClasspath() {
        assertParsing("drop procedure procedureName", JSqlParserRenderings.SUPPORTS_DROP_PROCEDURE);
        assertParsing(
                "update /*+ PARALLEL OPT_PARAM('parallel_min_percent','0') */ eligible ec set ec.END_DATE = "
                        + "ec.END_DATE + INTERVAL '0 0:0:0.999' DAY TO SECOND",
                JSqlParserRenderings.SUPPORTS_ORACLE_HINTED_UPDATE);
        assertParsing(
                "ALTER TABLE table1 ADD FULLTEXT INDEX `a`(`a`)", JSqlParserRenderings.SUPPORTS_FULLTEXT_INDEX_DDL);
        assertParsing(
                "SELECT COUNT(*) AS 订单总数, SUM(o.order_total) AS 总销售额 FROM orders o JOIN customers c "
                        + "ON o.customer_id = c.customer_id WHERE c.region = 'North America' GROUP BY o.customer_id "
                        + "HAVING COUNT(*) > 10;\nORDER BY total_sales DESC;\n",
                JSqlParserRenderings.PARSES_TEXT_WITH_TRAILING_STATEMENTS);
    }

    private static String rendered(String sql) throws JSQLParserException {
        return CCJSqlParserUtil.parse(sql).toString();
    }

    private static void assertParsing(String sql, boolean supported) {
        if (supported) {
            assertThatCode(() -> CCJSqlParserUtil.parse(sql)).doesNotThrowAnyException();
        } else {
            assertThatThrownBy(() -> CCJSqlParserUtil.parse(sql)).isInstanceOf(JSQLParserException.class);
        }
    }
}
