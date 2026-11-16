package com.baomidou.mybatisplus.test.extension.parser.cache;

import com.baomidou.mybatisplus.extension.parser.cache.ForySerialCaffeineJsqlParseCache;
import com.baomidou.mybatisplus.extension.parser.cache.FurySerialCaffeineJsqlParseCache;
import com.baomidou.mybatisplus.extension.parser.cache.JsqlParseCache;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class ParserCacheSerializationTest {

    @Test
    void roundTripsStatementWithFory() throws JSQLParserException {
        assertRoundTrip(new ForySerialCaffeineJsqlParseCache(builder -> {
        }));
    }

    @Test
    @SuppressWarnings("deprecation")
    void roundTripsStatementWithFury() throws JSQLParserException {
        assertRoundTrip(new FurySerialCaffeineJsqlParseCache(builder -> {
        }));
    }

    private void assertRoundTrip(JsqlParseCache cache) throws JSQLParserException {
        String sql = "SELECT t.id FROM t LEFT JOIN u ON t.id = u.id WHERE (t.id = ? OR u.id = ?)";
        Statement statement = CCJSqlParserUtil.parse(sql);
        cache.putStatement(sql, statement);
        Statement restored = cache.getStatement(sql);
        assertThat(restored).isExactlyInstanceOf(statement.getClass()).isNotSameAs(statement)
                .hasToString(statement.toString());
    }
}
