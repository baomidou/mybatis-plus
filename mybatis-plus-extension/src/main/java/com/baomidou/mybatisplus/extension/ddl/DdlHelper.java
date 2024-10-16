/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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
import com.baomidou.mybatisplus.extension.spi.CompatibleHelper;
import com.baomidou.mybatisplus.extension.ddl.history.*;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.jdbc.SqlRunner;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * DDL 辅助类
 *
 * @author hubin
 * @since 2021-06-22
 */
@Slf4j
public class DdlHelper {

    /**
     * 允许 SQL 脚本文件
     *
     * @param ddlGenerator DDL 生成器
     * @param connection   数据库连接
     * @param sqlFiles     SQL 文件列表
     * @param autoCommit   自动提交事务
     * @throws SQLException SQLException
     */
    public static void runScript(IDdlGenerator ddlGenerator, Connection connection, List<String> sqlFiles, boolean autoCommit) throws SQLException {
        // 执行自定义 DDL 信息
        final String jdbcUrl = connection.getMetaData().getURL();
        final String schema = DdlHelper.getDatabase(jdbcUrl);
        SqlRunner sqlRunner = new SqlRunner(connection);
        ScriptRunner scriptRunner = getScriptRunner(connection, autoCommit);
        if (null == ddlGenerator) {
            ddlGenerator = getDdlGenerator(jdbcUrl);
        }
        if (!ddlGenerator.existTable(schema, sql -> {
            try {
                Map<String, Object> resultMap = sqlRunner.selectOne(sql);
                if (null != resultMap && !StringPool.ZERO.equals(String.valueOf(resultMap.get(StringPool.NUM)))) {
                    return true;
                }
            } catch (SQLException e) {
                log.error("run script sql:{} , error: {}", sql, e.getMessage());
            }
            return false;
        })) {
            scriptRunner.runScript(new StringReader(ddlGenerator.createDdlHistory()));
        }
        // 执行 SQL 脚本
        for (String sqlFile : sqlFiles) {
            try {
                List<Map<String, Object>> objectMap = sqlRunner.selectAll(ddlGenerator.selectDdlHistory(sqlFile, StringPool.SQL));
                if (null == objectMap || objectMap.isEmpty()) {
                    log.debug("run script file: {}", sqlFile);
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
                log.error("run script sql:{} , error: {} , Please check if the table `ddl_history` exists", sqlFile, e.getMessage());
            }
        }
    }

    /**
     * 允许 SQL 脚本文件
     *
     * @param ddlGenerator DDL 生成器
     * @param dataSource   数据源
     * @param sqlFiles     SQL 文件列表
     * @param autoCommit   自动提交事务
     */
    public static void runScript(IDdlGenerator ddlGenerator, DataSource dataSource, List<String> sqlFiles, boolean autoCommit) {
        try (Connection connection = dataSource.getConnection()) {
            runScript(ddlGenerator, connection, sqlFiles, autoCommit);
        } catch (Exception e) {
            log.error("run script error: {}", e.getMessage());
        }
    }

    public static InputStream getInputStream(String path) throws Exception {
        return CompatibleHelper.getCompatibleSet().getInputStream(path);
    }

    protected static String getNowTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
    }

    public static ScriptRunner getScriptRunner(Connection connection, boolean autoCommit) {
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        Resources.setCharset(StandardCharsets.UTF_8);
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
        }
        else if (DbType.SQLITE == dbType){
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
