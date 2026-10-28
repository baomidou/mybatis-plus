package com.baomidou.mybatisplus.test.extension.parser;

import com.baomidou.mybatisplus.extension.parser.cache.FstFactory;
import com.baomidou.mybatisplus.extension.parser.cache.FuryFactory;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.apache.fury.logging.LoggerFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.util.SerializationUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2023-08-03
 */
@Tag("unit")
class JsqlParserSimpleSerialTest {

    private final static int len = 1000;
    private final static String sql = "SELECT * FROM entity e " + "LEFT JOIN entity1 e1 "
            + "LEFT JOIN entity2 e2 ON e2.id = e1.id " + "ON e1.id = e.id " + "WHERE (e.id = ? OR e.NAME = ?)";

    @Test
    void shouldSerializeAndDeserializeSqlStatementWithJdkFstAndFury() throws JSQLParserException {
        noSerial();
        jdkSerial();
        fstSerial();
        furySerial();
    }

    void noSerial() throws JSQLParserException {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            CCJSqlParserUtil.parse(sql);
        }
        long endTime = System.currentTimeMillis();
        long e1 = endTime - startTime;
        assertThat(e1).isPositive();
    }

    void jdkSerial() throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        String target = statement.toString();
        byte[] serial = null;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            serial = SerializationUtils.serialize(statement);
        }
        long endTime = System.currentTimeMillis();
        long et = endTime - startTime;
        assertThat(et).isPositive();

        startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            statement = (Statement) SerializationUtils.deserialize(serial);
        }
        endTime = System.currentTimeMillis();
        et = endTime - startTime;
        assertThat(statement).isNotNull();
        assertThat(statement.toString()).isEqualTo(target);
    }

    void fstSerial() throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        String target = statement.toString();
        FstFactory factory = FstFactory.getDefaultFactory();
        byte[] serial = null;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            serial = factory.asByteArray(statement);
        }
        long endTime = System.currentTimeMillis();
        long et = endTime - startTime;
        assertThat(et).isPositive();

        startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            statement = (Statement) factory.asObject(serial);
        }
        endTime = System.currentTimeMillis();
        et = endTime - startTime;
        assertThat(statement).isNotNull();
        assertThat(statement.toString()).isEqualTo(target);
    }

    void furySerial() throws JSQLParserException {
        LoggerFactory.disableLogging();
        Statement statement = CCJSqlParserUtil.parse(sql);
        String target = statement.toString();
        FuryFactory factory = FuryFactory.getFuryFactory();
        byte[] serial = null;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            serial = factory.serialize(statement);
        }
        long endTime = System.currentTimeMillis();
        long et = endTime - startTime;
        assertThat(et).isPositive();

        startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            statement = (Statement) factory.deserialize(serial);
        }
        endTime = System.currentTimeMillis();
        et = endTime - startTime;
        assertThat(statement).isNotNull();
        assertThat(statement.toString()).isEqualTo(target);
    }
}
