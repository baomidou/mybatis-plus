package com.baomidou.mybatisplus.test.extension.parser;

import com.baomidou.mybatisplus.extension.parser.cache.FstFactory;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import org.springframework.util.SerializationUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2023-08-03
 */
class JsqlParserSimpleSerialTest {
    private final static int len = 1000;
    private final static String sql = "SELECT * FROM entity e " +
            "LEFT JOIN entity1 e1 " +
            "LEFT JOIN entity2 e2 ON e2.id = e1.id " +
            "ON e1.id = e.id " +
            "WHERE (e.id = ? OR e.NAME = ?)";

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void test() throws JSQLParserException {
        System.out.println("循环次数: " + len);
        noSerial();
        jdkSerial();
        fstSerial();
    }

    void noSerial() throws JSQLParserException {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            CCJSqlParserUtil.parse(sql);
        }
        long endTime = System.currentTimeMillis();
        long e1 = endTime - startTime;
        System.out.printf("普通解析执行耗时: %s 毫秒, 均耗时: %s%n", e1, (double) e1 / len);
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
        System.out.printf("jdk serialize 执行耗时: %s 毫秒,byte大小: %s, 均耗时: %s%n", et, serial.length, (double) et / len);


        startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            statement = (Statement) SerializationUtils.deserialize(serial);
        }
        endTime = System.currentTimeMillis();
        et = endTime - startTime;
        System.out.printf("jdk deserialize 执行耗时: %s 毫秒, 均耗时: %s%n", et, (double) et / len);
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
        System.out.printf("fst serialize 执行耗时: %s 毫秒,byte大小: %s, 均耗时: %s%n", et, serial.length, (double) et / len);


        startTime = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            statement = (Statement) factory.asObject(serial);
        }
        endTime = System.currentTimeMillis();
        et = endTime - startTime;
        System.out.printf("fst deserialize 执行耗时: %s 毫秒, 均耗时: %s%n", et, (double) et / len);
        assertThat(statement).isNotNull();
        assertThat(statement.toString()).isEqualTo(target);
    }
}
