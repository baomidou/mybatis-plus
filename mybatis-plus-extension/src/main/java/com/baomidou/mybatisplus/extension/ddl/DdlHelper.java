/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.extension.ddl;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.ddl.history.*;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.jdbc.SqlRunner;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * DDL 辅助类
 *
 * @author hubin
 * @since 2021-06-22
 */
public class DdlHelper {

    private static final Log LOG = LogFactory.getLog(DdlHelper.class);

    /**
     * 运行 SQL 脚本文件
     *
     * @param ddlGenerator DDL 生成器
     * @param connection   数据库连接 (自行控制回收)
     * @param sqlFiles     SQL 文件列表
     * @param autoCommit   是否自动提交事务
     * @throws SQLException SQLException
     */
    public static void runScript(IDdlGenerator ddlGenerator, Connection connection, List<String> sqlFiles, boolean autoCommit) throws SQLException {
        runScript(ddlGenerator, connection, sqlFiles, autoCommit, DdlScriptErrorHandler.PrintlnLogErrorHandler.INSTANCE);
    }

    /**
     * 运行 SQL 脚本文件
     *
     * @param ddlGenerator          DDL 生成器
     * @param connection            数据库连接 (自行控制回收)
     * @param sqlFiles              SQL 文件列表
     * @param autoCommit            是否自动提交事务
     * @param ddlScriptErrorHandler 错误处理器
     * @throws SQLException SQLException
     * @since 3.5.11
     */
    public static void runScript(IDdlGenerator ddlGenerator, Connection connection, List<String> sqlFiles,
                                 boolean autoCommit, DdlScriptErrorHandler ddlScriptErrorHandler) throws SQLException {
        runScript(ddlGenerator, connection, sqlFiles, null, autoCommit, ddlScriptErrorHandler);
    }

    /**
     * 运行 SQL 脚本文件
     *
     * @param ddlGenerator          DDL 生成器
     * @param connection            数据库连接 (自行控制回收)
     * @param sqlFiles              SQL 文件列表
     * @param scriptRunnerConsumer  自定义 ScriptRunner 函数
     * @param ddlScriptErrorHandler 错误处理器
     * @throws SQLException SQLException
     * @since 3.5.11
     */
    public static void runScript(IDdlGenerator ddlGenerator, Connection connection, List<String> sqlFiles,
                                 Consumer<ScriptRunner> scriptRunnerConsumer, DdlScriptErrorHandler ddlScriptErrorHandler) throws SQLException {
        runScript(ddlGenerator, connection, sqlFiles, scriptRunnerConsumer, false, ddlScriptErrorHandler);
    }


    /**
     * 运行 SQL 脚本文件
     *
     * @param ddlGenerator          DDL 生成器
     * @param connection            数据库连接 (自行控制回收)
     * @param sqlFiles              SQL 文件列表
     * @param scriptRunnerConsumer  自定义 ScriptRunner 函数
     * @param autoCommit            是否自动提交事务
     * @param ddlScriptErrorHandler 错误处理器
     * @throws SQLException SQLException
     * @since 3.5.11
     */
    public static void runScript(IDdlGenerator ddlGenerator, Connection connection, List<String> sqlFiles, Consumer<ScriptRunner> scriptRunnerConsumer,
                                 boolean autoCommit, DdlScriptErrorHandler ddlScriptErrorHandler) throws SQLException {
        final String jdbcUrl = connection.getMetaData().getURL();
        SqlRunner sqlRunner = new SqlRunner(connection);
        ScriptRunner scriptRunner = getScriptRunner(connection, autoCommit);
        if (scriptRunnerConsumer != null) {
            scriptRunnerConsumer.accept(scriptRunner);
        }
        if (null == ddlGenerator) {
            ddlGenerator = getDdlGenerator(jdbcUrl);
        }
        if (!ddlGenerator.existTable(connection)) {
            scriptRunner.runScript(new StringReader(ddlGenerator.createDdlHistory()));
        }
        // 执行 SQL 脚本
        for (String sqlFile : sqlFiles) {
            try {
                List<Map<String, Object>> objectMap = sqlRunner.selectAll(ddlGenerator.selectDdlHistory(sqlFile, StringPool.SQL));
                if (null == objectMap || objectMap.isEmpty()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Run script file: " + sqlFile);
                    }
                    String[] sqlFileArr = sqlFile.split(StringPool.HASH);
                    if (Objects.equals(2, sqlFileArr.length)) {
                        // 命令间的分隔符
                        scriptRunner.setDelimiter(sqlFileArr[1]);
                        // 原始文件路径
                        sqlFile = sqlFileArr[0];
                    } else {
                        scriptRunner.setDelimiter(StringPool.SEMICOLON);
                    }
                    File file = new File(sqlFile);
                    if (file.exists()) {
                        scriptRunner.runScript(new FileReader(file));
                    } else {
                        scriptRunner.runScript(new InputStreamReader(getInputStream(sqlFile)));
                    }
                    sqlRunner.insert(ddlGenerator.insertDdlHistory(sqlFile, StringPool.SQL, getNowTime()));
                }
            } catch (Exception e) {
                if (ddlScriptErrorHandler != null) {
                    ddlScriptErrorHandler.handle(sqlFile, e);
                }
            }
        }
    }

    /**
     * 运行 SQL 脚本文件
     *
     * @param ddlGenerator DDL 生成器
     * @param dataSource   数据源
     * @param sqlFiles     SQL 文件列表
     * @param autoCommit   是否自动提交事务
     * @see #runScript(IDdlGenerator, Connection, List, boolean)
     * @see #runScript(IDdlGenerator, DataSource, List, boolean, DdlScriptErrorHandler)
     * @deprecated 3.5.11 方法会吞掉所有异常,建议自行处理.
     */
    @Deprecated
    public static void runScript(IDdlGenerator ddlGenerator, DataSource dataSource, List<String> sqlFiles, boolean autoCommit) {
        try (Connection connection = dataSource.getConnection()) {
            runScript(ddlGenerator, connection, sqlFiles, autoCommit);
        } catch (Exception e) {
            LOG.error("Run script error: ", e);
        }
    }

    /**
     * 运行 SQL 脚本文件
     *
     * @param ddlGenerator          DDL 生成器
     * @param dataSource            数据源
     * @param sqlFiles              SQL 文件列表
     * @param autoCommit            是否自动提交事务
     * @param ddlScriptErrorHandler 错误处理器
     * @since 3.5.11
     */
    public static void runScript(IDdlGenerator ddlGenerator,
                                 DataSource dataSource, List<String> sqlFiles, boolean autoCommit,
                                 DdlScriptErrorHandler ddlScriptErrorHandler) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            runScript(ddlGenerator, connection, sqlFiles, autoCommit, ddlScriptErrorHandler);
        }
    }


    /**
     * 运行 SQL 脚本文件
     *
     * @param ddlGenerator          DDL 生成器
     * @param dataSource            数据源
     * @param sqlFiles              SQL 文件列表
     * @param scriptRunnerConsumer  自定义 ScriptRunner 处理函数
     * @param ddlScriptErrorHandler 错误处理器
     * @since 3.5.11
     */
    public static void runScript(IDdlGenerator ddlGenerator,
                                 DataSource dataSource, List<String> sqlFiles, Consumer<ScriptRunner> scriptRunnerConsumer,
                                 DdlScriptErrorHandler ddlScriptErrorHandler) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            runScript(ddlGenerator, connection, sqlFiles, scriptRunnerConsumer, false, ddlScriptErrorHandler);
        }
    }

    /**
     * 运行 SQL 脚本文件
     *
     * @param ddlGenerator          DDL 生成器
     * @param dataSource            数据源
     * @param sqlFiles              SQL 文件列表
     * @param scriptRunnerConsumer  自定义 ScriptRunner 处理函数
     * @param ddlScriptErrorHandler 错误处理器
     * @since 3.5.11
     */
    public static void runScript(IDdlGenerator ddlGenerator, DataSource dataSource, List<String> sqlFiles,
                                 Consumer<ScriptRunner> scriptRunnerConsumer, boolean autoCommit, DdlScriptErrorHandler ddlScriptErrorHandler) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            runScript(ddlGenerator, connection, sqlFiles, scriptRunnerConsumer, autoCommit, ddlScriptErrorHandler);
        }
    }

    public static InputStream getInputStream(String path) throws Exception {
        return Resources.getResourceAsStream(path);
    }

    protected static String getNowTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
    }

    public static ScriptRunner getScriptRunner(Connection connection, boolean autoCommit) {
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        scriptRunner.setAutoCommit(autoCommit);
        scriptRunner.setEscapeProcessing(false);
        scriptRunner.setRemoveCRs(true);
        scriptRunner.setStopOnError(true);
        scriptRunner.setFullLineDelimiter(false);
        return scriptRunner;
    }

    protected static IDdlGenerator getDdlGenerator(String jdbcUrl) throws RuntimeException {
        DbType dbType = JdbcUtils.getDbType(jdbcUrl);
        // mysql same type
        if (dbType.mysqlSameType()) {
            return MysqlDdlGenerator.newInstance();
        }
        // oracle same type
        else if (dbType.oracleSameType()) {
            return OracleDdlGenerator.newInstance();
        } else if (DbType.SQLITE == dbType) {
            return SQLiteDdlGenerator.newInstance();
        }
        // postgresql same type
        else if (dbType.postgresqlSameType()) {
            return PostgreDdlGenerator.newInstance();
        }
        throw new RuntimeException("Unsupported database type: " + jdbcUrl);
    }

    public static String getDatabase(String jdbcUrl) {
        String[] urlArr = jdbcUrl.split("://");
        if (urlArr.length == 2) {
            String[] dataArr = urlArr[1].split("/");
            if (dataArr.length > 1) {
                return dataArr[1].split("\\?")[0];
            }
        }
        return null;
    }
}
