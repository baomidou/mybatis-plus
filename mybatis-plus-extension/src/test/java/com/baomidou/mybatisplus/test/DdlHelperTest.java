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
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DdlHelper 的脚本执行与 ddl_history 记账。<br>
 * 需要真实数据库的用例统一 @Disabled 并写清依赖；H2 与 SQLite 用例各用自己的库名和临时文件，互不共享状态。<br>
 * 建表语句在不同方言下对标识符大小写的处理不一样，所以表名与记账内容都从结果反查，不假定某一种拼法。
 *
 * @author nieqiurong
 */
public class DdlHelperTest {

    private static final List<String> SCRIPTS = List.of("ddl/test.sql");

    private static final DdlScriptErrorHandler THROWS = DdlScriptErrorHandler.ThrowsErrorHandler.INSTANCE;

    /**
     * 记账行：脚本名取自 sqlFile 去掉 {@code #分隔符} 的部分，类型取自 {@code StringPool.SQL}（值为小写 sql）。
     */
    private static final String RECORDED = "ddl/test.sql|sql";

    @TempDir
    private Path tempDir;

    /**
     * H2 内存库在最后一个连接关闭时就被销毁，而 runScript 用完连接会归还；不加 DB_CLOSE_DELAY=-1 的话， 用例里后续开的验证连接看到的是全新空库。库名每个用例各用一个，避免互相看到对方的
     * ddl_history。
     */
    private static UnpooledDataSource h2DataSource(String dbName, String urlOptions) {
        return new UnpooledDataSource(org.h2.Driver.class.getName(),
                "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1;" + urlOptions, "sa", "");
    }

    private void runAndRecord(IDdlGenerator ddlGenerator, UnpooledDataSource dataSource) throws SQLException {
        DdlHelper.runScript(ddlGenerator, dataSource, SCRIPTS, true, THROWS);
    }

    /**
     * 建表与记账都要看得见：表存在性走元数据，内容直接读 ddl_history。
     */
    private void assertRecorded(UnpooledDataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String table = ddlHistoryTable(connection);
            assertThat(table).as("ddl_history of " + dataSource.getUrl()).isNotNull();
            assertThat(historyRows(connection, table)).containsExactly(RECORDED);
        }
    }

    /**
     * 再跑一次同一个脚本，应当被 ddl_history 里的记录跳过，不产生第二行。
     */
    private void assertSkipAlreadyExecuted(IDdlGenerator ddlGenerator, UnpooledDataSource dataSource)
            throws SQLException {
        runAndRecord(ddlGenerator, dataSource);
        try (Connection connection = dataSource.getConnection()) {
            assertThat(historyRows(connection, ddlHistoryTable(connection))).hasSize(1);
        }
    }

    /**
     * ddl_history 在不同方言下存成小写还是大写并不一致，用元数据拿到真实表名再去查询。
     *
     * @return 表名，不存在时返回 null
     */
    private static String ddlHistoryTable(Connection connection) throws SQLException {
        for (String candidate : new String[]{"ddl_history", "DDL_HISTORY"}) {
            try (ResultSet resultSet = connection.getMetaData()
                    .getTables(connection.getCatalog(), null, candidate, new String[]{"TABLE"})) {
                if (resultSet.next()) {
                    return resultSet.getString("TABLE_NAME");
                }
            }
        }
        return null;
    }

    private static List<String> historyRows(Connection connection, String table) throws SQLException {
        List<String> rows = new ArrayList<>();
        try (var statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM \"" + table + "\"")) {
            while (resultSet.next()) {
                rows.add(value(resultSet, "script") + "|" + value(resultSet, "type"));
            }
        }
        return rows;
    }

    /**
     * 列名的大小写同样跟方言走，按不区分大小写找下标。
     */
    private static String value(ResultSet resultSet, String column) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            if (column.equalsIgnoreCase(metaData.getColumnLabel(i))) {
                return resultSet.getString(i);
            }
        }
        throw new SQLException("ddl_history has no column named " + column);
    }

    @Test
    @Disabled("需要本地 MySQL: jdbc:mysql://127.0.0.1:3306/baomidou，尚未进入 external-db 分层")
    void testForMysql() throws SQLException {
        var dataSource = new UnpooledDataSource(com.mysql.cj.jdbc.Driver.class.getName(),
                "jdbc:mysql://127.0.0.1:3306/baomidou?serverTimezone=Asia/Shanghai", "root", "123456");
        runAndRecord(new MysqlDdlGenerator(), dataSource);
    }

    @Test
    @Disabled("需要本地 PostgreSQL: jdbc:postgresql://localhost:5432/postgres，尚未进入 external-db 分层")
    void testForPostgresql() throws SQLException {
        var dataSource = new UnpooledDataSource(org.postgresql.Driver.class.getName(),
                "jdbc:postgresql://localhost:5432/postgres", "postgres", "123456");
        IDdlGenerator ddlGenerator = PostgreDdlGenerator.newInstance();
        runAndRecord(ddlGenerator, dataSource);
        // 指定scheme运行 旧版本的模式是指定 public,为了兼容当使用默认的示例是无法根据指定的模式走的
        dataSource = new UnpooledDataSource(org.postgresql.Driver.class.getName(),
                "jdbc:postgresql://localhost:5432/postgres?currentSchema=baomidou", "postgres", "123456");
        runAndRecord(ddlGenerator, dataSource);
    }

    @Test
    @Disabled("需要本地 PostgreSQL: jdbc:postgresql://localhost:5432/postgres，尚未进入 external-db 分层")
    void testForPostgresqlForAuto() throws SQLException {
        var dataSource = new UnpooledDataSource(org.postgresql.Driver.class.getName(),
                "jdbc:postgresql://localhost:5432/postgres", "postgres", "123456");
        IDdlGenerator ddlGenerator = PostgreDdlGenerator.newInstanceWithAutoSchema();
        runAndRecord(ddlGenerator, dataSource);

        // 指定scheme运行
        dataSource = new UnpooledDataSource(org.postgresql.Driver.class.getName(),
                "jdbc:postgresql://localhost:5432/postgres?currentSchema=baomidou", "postgres", "123456");
        runAndRecord(ddlGenerator, dataSource);
    }

    @Test
    @Disabled("需要本地 Oracle: jdbc:oracle:thin:@127.0.0.1:1521:orcl，尚未进入 external-db 分层")
    void testForOracle() throws SQLException {
        var dataSource = new UnpooledDataSource(oracle.jdbc.driver.OracleDriver.class.getName(),
                "jdbc:oracle:thin:@127.0.0.1:1521:orcl", "system", "123456");
        runAndRecord(new OracleDdlGenerator(), dataSource);
    }

    @Test
    void testForSQLite() throws SQLException {
        // 落在临时目录，避免在工作区留下 test.db
        var dataSource = new UnpooledDataSource(org.sqlite.JDBC.class.getName(),
                "jdbc:sqlite:" + tempDir.resolve("ddl.db").toFile().getPath(), "", "");
        runAndRecord(new SQLiteDdlGenerator(), dataSource);
        assertRecorded(dataSource);
        assertSkipAlreadyExecuted(new SQLiteDdlGenerator(), dataSource);
    }

    @Test
    void testForH2() throws SQLException {
        var dataSource = h2DataSource("ddl_h2", "DATABASE_TO_LOWER=TRUE");
        // 生成器留空，由 jdbcUrl 推断出 PostgreDdlGenerator
        runAndRecord(null, dataSource);
        assertRecorded(dataSource);
        assertSkipAlreadyExecuted(null, dataSource);
    }

    @Test
    void testForH2Mysql() throws SQLException {
        var dataSource = h2DataSource("ddl_h2_mysql", "MODE=MySQL;DATABASE_TO_LOWER=TRUE");
        runAndRecord(new MysqlDdlGenerator(), dataSource);
        assertRecorded(dataSource);
        assertSkipAlreadyExecuted(new MysqlDdlGenerator(), dataSource);
    }

    @Test
    void testForH2Postgresql() throws SQLException {
        var dataSource = h2DataSource("ddl_h2_postgresql", "MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE");
        runAndRecord(new PostgreDdlGenerator(), dataSource);
        assertRecorded(dataSource);
        assertSkipAlreadyExecuted(new PostgreDdlGenerator(), dataSource);
    }

    /**
     * 分隔符写在脚本名里（{@code ddl/test.sql#;}），执行完仍然只记账一行，且记下的是去掉后缀的文件名。
     */
    @Test
    void testCustomDelimiterScriptName() throws SQLException {
        var dataSource = h2DataSource("ddl_h2_delimiter", "MODE=MySQL;DATABASE_TO_LOWER=TRUE");
        DdlHelper.runScript(new MysqlDdlGenerator(), dataSource, List.of("ddl/test.sql#;"), true, THROWS);
        assertRecorded(dataSource);
    }

    @Test
    @Disabled("需要本地达梦: jdbc:dm://127.0.0.1:5236/DMSERVER，尚未进入 external-db 分层")
    void testForDm() throws SQLException {
        var dataSource = new UnpooledDataSource(dm.jdbc.driver.DmDriver.class.getName(),
                "jdbc:dm://127.0.0.1:5236/DMSERVER", "SYSDBA", "Dm123456");
        runAndRecord(new OracleDdlGenerator(), dataSource);
        // 指定模式运行
        runAndRecord(new OracleDdlGenerator("TEST1"), dataSource);
    }

}
