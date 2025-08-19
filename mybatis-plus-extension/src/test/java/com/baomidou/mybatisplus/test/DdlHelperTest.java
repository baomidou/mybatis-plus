package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.extension.ddl.DdlHelper;
import com.baomidou.mybatisplus.extension.ddl.DdlScriptErrorHandler;
import com.baomidou.mybatisplus.extension.ddl.history.IDdlGenerator;
import com.baomidou.mybatisplus.extension.ddl.history.MysqlDdlGenerator;
import com.baomidou.mybatisplus.extension.ddl.history.OracleDdlGenerator;
import com.baomidou.mybatisplus.extension.ddl.history.PostgreDdlGenerator;
import com.baomidou.mybatisplus.extension.ddl.history.SQLiteDdlGenerator;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author nieqiurong
 */
public class DdlHelperTest {

    @Test
    @Disabled
    void testForMysql() throws SQLException {
        var dataSource = new UnpooledDataSource(com.mysql.cj.jdbc.Driver.class.getName(),
            "jdbc:mysql://127.0.0.1:3306/baomidou?serverTimezone=Asia/Shanghai",
            "root", "123456");
        var ddlGenerator = new MysqlDdlGenerator();
        DdlHelper.runScript(ddlGenerator, dataSource, List.of("ddl/test.sql"),
            true, DdlScriptErrorHandler.ThrowsErrorHandler.INSTANCE);
    }

    @Test
    @Disabled
    void testForPostgresql() throws SQLException {
        var dataSource = new UnpooledDataSource(org.postgresql.Driver.class.getName(),
            "jdbc:postgresql://localhost:5432/postgres",
            "postgres", "123456");
        IDdlGenerator ddlGenerator = PostgreDdlGenerator.newInstance();
        DdlHelper.runScript(ddlGenerator, dataSource, List.of("ddl/test.sql"),
            true, DdlScriptErrorHandler.ThrowsErrorHandler.INSTANCE);
        // 指定scheme运行 旧版本的模式是指定 public,为了兼容当使用默认的示例是无法根据指定的模式走的
        dataSource = new UnpooledDataSource(org.postgresql.Driver.class.getName(),
            "jdbc:postgresql://localhost:5432/postgres?currentSchema=baomidou",
            "postgres", "123456");
        DdlHelper.runScript(ddlGenerator, dataSource, List.of("ddl/test.sql"),
            true, DdlScriptErrorHandler.ThrowsErrorHandler.INSTANCE);
    }

    @Test
    @Disabled
    void testForPostgresqlForAuto() throws SQLException {
        var dataSource = new UnpooledDataSource(org.postgresql.Driver.class.getName(),
            "jdbc:postgresql://localhost:5432/postgres",
            "postgres", "123456");
        IDdlGenerator ddlGenerator = PostgreDdlGenerator.newInstanceWithAutoSchema();
        DdlHelper.runScript(ddlGenerator, dataSource, List.of("ddl/test.sql"),
            true, DdlScriptErrorHandler.ThrowsErrorHandler.INSTANCE);

        // 指定scheme运行
        dataSource = new UnpooledDataSource(org.postgresql.Driver.class.getName(),
            "jdbc:postgresql://localhost:5432/postgres?currentSchema=baomidou",
            "postgres", "123456");
        DdlHelper.runScript(ddlGenerator, dataSource, List.of("ddl/test.sql"),
            true, DdlScriptErrorHandler.ThrowsErrorHandler.INSTANCE);

    }

    @Test
    @Disabled
    void testForOracle() throws SQLException {
        var dataSource = new UnpooledDataSource(oracle.jdbc.driver.OracleDriver.class.getName(),
            "jdbc:oracle:thin:@127.0.0.1:1521:orcl",
            "system", "123456");
        var ddlGenerator = new OracleDdlGenerator();
        DdlHelper.runScript(ddlGenerator, dataSource, List.of("ddl/test.sql"),
            true, DdlScriptErrorHandler.ThrowsErrorHandler.INSTANCE);
    }

    @Test
    void testForSQLite() throws SQLException {
        var dataSource = new UnpooledDataSource(org.sqlite.JDBC.class.getName(),
            "jdbc:sqlite:test.db",
            "", "");
        var ddlGenerator = new SQLiteDdlGenerator();
        DdlHelper.runScript(ddlGenerator, dataSource, List.of("ddl/test.sql"),
            true, DdlScriptErrorHandler.ThrowsErrorHandler.INSTANCE);
    }

    @Test
    void testForH2() throws SQLException {
        var dataSource = new UnpooledDataSource(org.h2.Driver.class.getName(),
            "jdbc:h2:mem:test;DATABASE_TO_LOWER=TRUE",
            "sa", "");
        DdlHelper.runScript(null, dataSource, List.of("ddl/test.sql"),
            true, DdlScriptErrorHandler.ThrowsErrorHandler.INSTANCE);
    }

    @Test
    void testForH2Mysql() throws SQLException {
        var dataSource = new UnpooledDataSource(org.h2.Driver.class.getName(),
            "jdbc:h2:mem:test;MODE=MySQL",
            "sa", "");
        var ddlGenerator = new MysqlDdlGenerator();
        DdlHelper.runScript(ddlGenerator, dataSource, List.of("ddl/test.sql"),
            true, DdlScriptErrorHandler.ThrowsErrorHandler.INSTANCE);
    }

    @Test
    void testForH2Postgresql() throws SQLException {
        var dataSource = new UnpooledDataSource(org.h2.Driver.class.getName(),
            "jdbc:h2:mem:test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
            "sa", "");
        var ddlGenerator = new PostgreDdlGenerator();
        DdlHelper.runScript(ddlGenerator, dataSource, List.of("ddl/test.sql"),
            true, DdlScriptErrorHandler.ThrowsErrorHandler.INSTANCE);
    }

    @Test
    @Disabled
    void testForDm() throws SQLException {
        var dataSource = new UnpooledDataSource(dm.jdbc.driver.DmDriver.class.getName(),
            "jdbc:dm://127.0.0.1:5236/DMSERVER",
            "SYSDBA", "Dm123456");
        var ddlGenerator = new OracleDdlGenerator();
        DdlHelper.runScript(ddlGenerator, dataSource, List.of("ddl/test.sql"),
            true, DdlScriptErrorHandler.ThrowsErrorHandler.INSTANCE);
        // 指定模式运行
        ddlGenerator = new OracleDdlGenerator("TEST1");
        DdlHelper.runScript(ddlGenerator, dataSource, List.of("ddl/test.sql"),
            true, DdlScriptErrorHandler.ThrowsErrorHandler.INSTANCE);
    }


}
