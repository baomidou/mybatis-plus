package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.ibatis.jdbc.SqlRunner;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author miemie
 * @since 2022-04-11
 */
class IllegalSQLInnerInterceptorTest {

    private final IllegalSQLInnerInterceptor interceptor = new IllegalSQLInnerInterceptor();
//
    // 待研究为啥H2读不到索引信息
//    private static Connection connection;
//
//    @BeforeAll
//    public static void beforeAll() throws SQLException {
//        var jdbcDataSource = new JdbcDataSource();
//        jdbcDataSource.setURL("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
//        connection = jdbcDataSource.getConnection("sa","");
//        var sql = """
//            CREATE TABLE t_demo (
//              `a` int DEFAULT NULL,
//              `b` int DEFAULT NULL,
//              `c` int DEFAULT NULL,
//              KEY `ab_index` (`a`,`b`)
//            );
//            """;
//        SqlRunner sqlRunner = new SqlRunner(connection);
//        sqlRunner.run(sql);
//    }

    @Test
    void test() {
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("SELECT COUNT(*) AS total FROM t_user WHERE (client_id = ?)", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from t_user", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("update t_user set age = 18", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("delete from t_user set age = 18", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from t_user where age != 1", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from t_user where age = 1 or name = 'test'", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from t_user where (age = 1 or name = 'test')", null));
//        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `t_demo` where a = 1 and b = 2", connection));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("update t_user set age = 1 where age = 1 or name = 'test'", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("update t_user set age = 1 where (age = 1 or name = 'test')", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("delete t_user where age = 1 or name = 'test'", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("delete t_user where (age = 1 or name = 'test')", null));
    }

    @Test
    @Disabled
    void testForMysql() {
        /*
         *   CREATE TABLE `t_demo` (
              `a` int DEFAULT NULL,
              `b` int DEFAULT NULL,
              `c` int DEFAULT NULL,
              KEY `ab_index` (`a`,`b`)
            );
            CREATE TABLE `test` (
              `a` int DEFAULT NULL,
              `b` int DEFAULT NULL,
              `c` int DEFAULT NULL,
              KEY `ab_index` (`a`,`b`)
            ) ;
         */
        var dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test?serverTimezone=Asia/Shanghai");
        dataSource.setUser("root");
        dataSource.setPassword("123456");

        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from t_demo where `a` = 1 and `b` = 2", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from t_demo where a = 1 and `b` = 2", dataSource.getConnection()));

        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `t_demo` where `a` = 1 and `b` = 2", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `t_demo` where a = 1 and `b` = 2", dataSource.getConnection()));

        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `t_demo` where c = 3", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from t_demo where c = 3", dataSource.getConnection()));

        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from test.`t_demo` where c = 3", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from test.t_demo where c = 3", dataSource.getConnection()));

        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("SELECT * FROM `t_demo` a INNER JOIN `test` b ON a.a = b.a WHERE a.a = 1", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("SELECT * FROM `t_demo` a INNER JOIN `test` b ON a.a = b.a WHERE a.b = 1", dataSource.getConnection()));

        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("SELECT * FROM test.`t_demo` a INNER JOIN test.`test` b ON a.a = b.a WHERE a.a = 1", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("SELECT * FROM test.`t_demo` a INNER JOIN test.`test` b ON a.a = b.a WHERE a.b = 1", dataSource.getConnection()));

        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("SELECT * FROM t_demo a INNER JOIN `test` b ON a.a = b.a WHERE a.a = 1", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("SELECT * FROM t_demo a INNER JOIN `test` b ON a.a = b.a WHERE a.b = 1", dataSource.getConnection()));

        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("SELECT * FROM `t_demo` a LEFT JOIN `test` b ON a.a = b.a WHERE a.a = 1", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("SELECT * FROM `t_demo` a LEFT JOIN `test` b ON a.a = b.a WHERE a.b = 1", dataSource.getConnection()));

        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `t_demo` where (c = 3 OR b = 2)", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `t_demo` where c = 3 OR b = 2", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `t_demo` where a = 3 AND (c = 3 OR b = 2)", dataSource.getConnection()));

        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `t_demo` where (a = 3 AND c = 3 OR b = 2)", dataSource.getConnection()));

        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `t_demo` where a in (1,3,2)", dataSource.getConnection()));

        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `t_demo` where a in (1,3,2) or b = 2", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `t_demo` where a in (1,3,2) AND b = 2", dataSource.getConnection()));

        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `t_demo` where (a = 3 AND c = 3 AND b = 2)", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `t_demo` a INNER JOIN test b ON a.a = b.a where a.a = 3 AND (b.c = 3 OR b.b = 2)", dataSource.getConnection()));

        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `t_demo` where a != (SELECT b FROM test limit 1) ", dataSource.getConnection()));
        //TODO 低版本这里的抛异常了.看着应该不用抛出
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `t_demo` where a = (SELECT b FROM test limit 1) ", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `t_demo` where a >= (SELECT b FROM test limit 1) ", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `t_demo` where a <= (SELECT b FROM test limit 1) ", dataSource.getConnection()));

        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `t_demo` where b = (SELECT b FROM test limit 1) ", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `t_demo` where b >= (SELECT b FROM test limit 1) ", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `t_demo` where b <= (SELECT b FROM test limit 1) ", dataSource.getConnection()));

    }

}
