package com.baomidou.mybatisplus.test.extension.parser.cache;

import com.baomidou.mybatisplus.extension.parser.cache.ForyFactory;
import com.baomidou.mybatisplus.extension.parser.cache.FstFactory;
import com.baomidou.mybatisplus.extension.parser.cache.FuryFactory;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2026-10-09
 */
@Tag("unit")
class SerialFactoryTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("sqls")
    void shouldRoundTripStatementWithFst(String sql) throws JSQLParserException {
        FstFactory factory = new FstFactory();
        assertRoundTrip(sql, obj -> factory.asObject(factory.asByteArray(obj)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("sqls")
    void shouldRoundTripStatementWithFory(String sql) throws JSQLParserException {
        ForyFactory factory = new ForyFactory();
        assertRoundTrip(sql, obj -> factory.deserialize(factory.serialize(obj)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("sqls")
    @SuppressWarnings("deprecation")
    void shouldRoundTripStatementWithFury(String sql) throws JSQLParserException {
        FuryFactory factory = new FuryFactory();
        assertRoundTrip(sql, obj -> factory.deserialize(factory.serialize(obj)));
    }

    static Stream<String> sqls() {
        return Stream.of(
                "SELECT TRUE FROM t", "SELECT sum(id) OVER (PARTITION BY name) FROM t", "DELETE FROM t WHERE id = 1");
    }

    @FunctionalInterface
    private interface RoundTrip {

        Object roundTrip(Object obj);
    }

    private void assertRoundTrip(String sql, RoundTrip roundTrip) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        Object restored = roundTrip.roundTrip(statement);
        assertThat(restored.getClass()).isEqualTo(statement.getClass());
        assertThat(restored.toString()).isEqualTo(statement.toString());
    }
}
