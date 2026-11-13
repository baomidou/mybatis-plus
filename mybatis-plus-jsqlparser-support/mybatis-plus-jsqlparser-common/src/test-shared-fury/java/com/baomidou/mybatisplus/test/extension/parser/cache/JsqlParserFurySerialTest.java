package com.baomidou.mybatisplus.test.extension.parser.cache;

import com.baomidou.mybatisplus.extension.parser.cache.FuryFactory;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.apache.fury.logging.LoggerFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * fury-core 只进了 base 与 5.2 两个版本模块的依赖，这条用例单独放 test-shared-fury，随这两处类路径编译运行。 只验序列化往返的正确性：耗时断言依赖系统时钟精度，也不该放在 unit 层循环跑上千次。
 *
 * @author miemie
 * @since 2023-08-03
 */
@Tag("unit")
class JsqlParserFurySerialTest {

    private static final String SQL = "SELECT * FROM entity e " + "LEFT JOIN entity1 e1 "
            + "LEFT JOIN entity2 e2 ON e2.id = e1.id " + "ON e1.id = e.id " + "WHERE (e.id = ? OR e.NAME = ?)";

    @Test
    void shouldSerializeAndDeserializeSqlStatementWithFury() throws JSQLParserException {
        LoggerFactory.disableLogging();
        Statement statement = CCJSqlParserUtil.parse(SQL);
        FuryFactory factory = FuryFactory.getFuryFactory();
        byte[] serial = factory.serialize(statement);
        assertThat(serial).as("Fury 序列化结果不能为空").isNotEmpty();
        assertThat(factory.deserialize(serial)).isInstanceOf(Statement.class).hasToString(statement.toString());
    }
}
