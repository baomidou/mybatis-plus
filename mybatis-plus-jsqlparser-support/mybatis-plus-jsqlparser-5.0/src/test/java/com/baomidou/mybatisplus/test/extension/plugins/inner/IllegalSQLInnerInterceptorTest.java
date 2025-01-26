package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor;
import org.apache.ibatis.jdbc.SqlRunner;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author miemie
 * @since 2022-04-11
 */
class IllegalSQLInnerInterceptorTest {

    private final IllegalSQLInnerInterceptor interceptor = new IllegalSQLInnerInterceptor();

    private static DataSource dataSource;

    @BeforeAll
    public static void beforeAll() throws SQLException {
        var jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setURL("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        jdbcDataSource.setPassword("");
        jdbcDataSource.setUser("sa");
        dataSource = jdbcDataSource;
        Connection connection = jdbcDataSource.getConnection();
        var sql = """
            CREATE TABLE T_DEMO (
              `a` int DEFAULT NULL,
              `b` int DEFAULT NULL,
              `c` int DEFAULT NULL,
              KEY `ab_index1` (`a`,`b`)
            );
            CREATE TABLE T_TEST (
              `a` int DEFAULT NULL,
              `b` int DEFAULT NULL,
              `c` int DEFAULT NULL,
              KEY `ab_index2` (`a`,`b`)
            );
            """;
        SqlRunner sqlRunner = new SqlRunner(connection);
        sqlRunner.run(sql);
    }

    @Test
    void test() {
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("SELECT COUNT(*) AS total FROM t_user WHERE (client_id = ?)", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from t_user", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("update t_user set age = 18", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("delete from t_user set age = 18", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from t_user where age != 1", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from t_user where age = 1 or name = 'test'", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from t_user where (age = 1 or name = 'test')", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("update t_user set age = 1 where age = 1 or name = 'test'", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("update t_user set age = 1 where (age = 1 or name = 'test')", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("delete t_user where age = 1 or name = 'test'", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("delete t_user where (age = 1 or name = 'test')", null));

        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `T_DEMO` where  b = 2", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from T_DEMO where `a` = 1 and `b` = 2", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from T_DEMO where a = 1 and `b` = 2", dataSource.getConnection()));

        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `T_DEMO` where `a` = 1 and `b` = 2", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `T_DEMO` where a = 1 and `b` = 2", dataSource.getConnection()));

        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `T_DEMO` where c = 3", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from T_DEMO where c = 3", dataSource.getConnection()));

        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from test.`T_DEMO` where c = 3", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from test.T_DEMO where c = 3", dataSource.getConnection()));

        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("SELECT * FROM `T_DEMO` a INNER JOIN `T_TEST` b ON a.a = b.a WHERE a.a = 1", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("SELECT * FROM `T_DEMO` a INNER JOIN `test` b ON a.a = b.a WHERE a.b = 1", dataSource.getConnection()));

        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("SELECT * FROM test.`T_DEMO` a INNER JOIN test.`T_TEST` b ON a.a = b.a WHERE a.a = 1", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("SELECT * FROM test.`T_DEMO` a INNER JOIN test.`T_TEST` b ON a.a = b.a WHERE a.b = 1", dataSource.getConnection()));

        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("SELECT * FROM T_DEMO a INNER JOIN `T_TEST` b ON a.a = b.a WHERE a.a = 1", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("SELECT * FROM T_DEMO a INNER JOIN `T_TEST` b ON a.a = b.a WHERE a.b = 1", dataSource.getConnection()));

        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("SELECT * FROM `T_DEMO` a LEFT JOIN `T_TEST` b ON a.a = b.a WHERE a.a = 1", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("SELECT * FROM `T_DEMO` a LEFT JOIN `T_TEST` b ON a.a = b.a WHERE a.b = 1", dataSource.getConnection()));

        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `T_DEMO` where (c = 3 OR b = 2)", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `T_DEMO` where c = 3 OR b = 2", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `T_DEMO` where a = 3 AND (c = 3 OR b = 2)", dataSource.getConnection()));

        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `T_DEMO` where (a = 3 AND c = 3 OR b = 2)", dataSource.getConnection()));

        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `T_DEMO` where a in (1,3,2)", dataSource.getConnection()));

        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `T_DEMO` where a in (1,3,2) or b = 2", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `T_DEMO` where a in (1,3,2) AND b = 2", dataSource.getConnection()));

        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `T_DEMO` where (a = 3 AND c = 3 AND b = 2)", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `T_DEMO` a INNER JOIN T_TEST b ON a.a = b.a where a.a = 3 AND (b.c = 3 OR b.b = 2)", dataSource.getConnection()));

        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `T_DEMO` where a != (SELECT b FROM T_TEST limit 1) ", dataSource.getConnection()));
        //TODO 低版本这里的抛异常了.看着应该不用抛出
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `T_DEMO` where a = (SELECT b FROM T_TEST limit 1) ", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `T_DEMO` where a >= (SELECT b FROM T_TEST limit 1) ", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select * from `T_DEMO` where a <= (SELECT b FROM T_TEST limit 1) ", dataSource.getConnection()));

        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `T_DEMO` where b = (SELECT b FROM T_TEST limit 1) ", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `T_DEMO` where b >= (SELECT b FROM T_TEST limit 1) ", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from `T_DEMO` where b <= (SELECT b FROM T_TEST limit 1) ", dataSource.getConnection()));
    }
    @Test
    void testCount() {
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select count(*) from T_DEMO where a = 1 and `b` = 2", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select count(*) from (select * from T_DEMO where a = 1 and `b` = 2) a", dataSource.getConnection()));
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("select count(*) from (select count(*) from (select * from T_DEMO where a = 1 and `b` = 2) a) c", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select count(*) from (select * from `T_DEMO`) a ", dataSource.getConnection()));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select count(*) from (select * from `T_DEMO` where b = (SELECT b FROM T_TEST limit 1)) a ", dataSource.getConnection()));
    }

}
