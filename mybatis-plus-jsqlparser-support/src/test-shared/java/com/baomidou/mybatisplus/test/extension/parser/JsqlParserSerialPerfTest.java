package com.baomidou.mybatisplus.test.extension.parser;

import com.baomidou.mybatisplus.extension.parser.cache.ForyFactory;
import com.baomidou.mybatisplus.extension.parser.cache.FstFactory;
import com.baomidou.mybatisplus.extension.parser.cache.FuryFactory;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JSqlParser AST 序列化耗时冒烟测试：对比 jdk、FST、Fury、Fory 四种序列化的量级耗时，noSerial 段为纯解析基线。 定位是冒烟而非基准：单次运行、JIT 未充分预热，数值仅供横向量级参考。
 * 运行与耗时观测方式（含生成对比表的提示词）见本模块 README「序列化缓存」一节。
 *
 * @author miemie
 * @since 2023-08-03
 */
@Tag("unit")
class JsqlParserSerialPerfTest {

    private static final Logger logger = LoggerFactory.getLogger(JsqlParserSerialPerfTest.class);

    private final static int len = 1000;
    private final static String sql = """
            SELECT * FROM entity e
            LEFT JOIN entity1 e1
            LEFT JOIN entity2 e2 ON e2.id = e1.id
            ON e1.id = e.id
            WHERE (e.id = ? OR e.NAME = ?)""";

    @Test
    void shouldSerializeAndDeserializeSqlStatementWithJdkFstFuryAndFory() throws JSQLParserException {
        noSerial();
        jdkSerial();
        fstSerial();
        furySerial();
        forySerial();
    }

    void noSerial() throws JSQLParserException {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            CCJSqlParserUtil.parse(sql);
        }
        long endTime = System.currentTimeMillis();
        long e1 = endTime - startTime;
        assertThat(e1).isPositive();
        logger.info("noSerial  parse            {} ms", e1);
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
        long serialTime = endTime - startTime;
        assertThat(serialTime).isPositive();

        startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            statement = (Statement) SerializationUtils.deserialize(serial);
        }
        endTime = System.currentTimeMillis();
        long deserialTime = endTime - startTime;
        assertThat(statement).isNotNull();
        assertThat(statement.toString()).isEqualTo(target);
        logger.info("jdk       serialize {} ms / deserialize {} ms", serialTime, deserialTime);
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
        long serialTime = endTime - startTime;
        assertThat(serialTime).isPositive();

        startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            statement = (Statement) factory.asObject(serial);
        }
        endTime = System.currentTimeMillis();
        long deserialTime = endTime - startTime;
        assertThat(statement).isNotNull();
        assertThat(statement.toString()).isEqualTo(target);
        logger.info("fst       serialize {} ms / deserialize {} ms", serialTime, deserialTime);
    }

    @SuppressWarnings("deprecation")
    void furySerial() throws JSQLParserException {
        org.apache.fury.logging.LoggerFactory.disableLogging();
        Statement statement = CCJSqlParserUtil.parse(sql);
        String target = statement.toString();
        FuryFactory factory = FuryFactory.getFuryFactory();
        byte[] serial = null;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            serial = factory.serialize(statement);
        }
        long endTime = System.currentTimeMillis();
        long serialTime = endTime - startTime;
        assertThat(serialTime).isPositive();

        startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            statement = (Statement) factory.deserialize(serial);
        }
        endTime = System.currentTimeMillis();
        long deserialTime = endTime - startTime;
        assertThat(statement).isNotNull();
        assertThat(statement.toString()).isEqualTo(target);
        logger.info("fury      serialize {} ms / deserialize {} ms", serialTime, deserialTime);
    }

    void forySerial() throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        String target = statement.toString();
        ForyFactory factory = ForyFactory.getForyFactory();
        byte[] serial = null;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            serial = factory.serialize(statement);
        }
        long endTime = System.currentTimeMillis();
        long serialTime = endTime - startTime;
        assertThat(serialTime).isPositive();

        startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            statement = (Statement) factory.deserialize(serial);
        }
        endTime = System.currentTimeMillis();
        long deserialTime = endTime - startTime;
        assertThat(statement).isNotNull();
        assertThat(statement.toString()).isEqualTo(target);
        logger.info("fory      serialize {} ms / deserialize {} ms", serialTime, deserialTime);
    }
}
