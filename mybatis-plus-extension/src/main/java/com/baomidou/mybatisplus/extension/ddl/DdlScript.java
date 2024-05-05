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

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.ddl.history.IDdlGenerator;
import org.apache.ibatis.jdbc.ScriptRunner;

import javax.sql.DataSource;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Ddl 脚本执行
 *
 * @author hubin
 * @since 2021-07-23
 */
public class DdlScript {

    private final DataSource dataSource;

    private final IDdlGenerator ddlGenerator;

    private final boolean autoCommit;

    public DdlScript(DataSource dataSource) {
        this(dataSource, null);
    }

    public DdlScript(DataSource dataSource, IDdlGenerator ddlGenerator) {
        this(dataSource, ddlGenerator, false);
    }

    public DdlScript(DataSource dataSource, IDdlGenerator ddlGenerator, boolean autoCommit) {
        this.dataSource = dataSource;
        this.ddlGenerator = ddlGenerator;
        this.autoCommit = autoCommit;
    }

    public void run(List<String> sqlFiles) {
        this.run(sqlFiles, this.autoCommit);
    }

    /**
     * 执行 SQL 脚本文件
     *
     * @param sqlFiles   SQL 脚本文件列表
     * @param autoCommit 自动提交事务
     */
    public void run(List<String> sqlFiles, boolean autoCommit) {
        DdlHelper.runScript(this.ddlGenerator, this.dataSource, sqlFiles, autoCommit);
    }

    public void run(String sqlScript) throws Exception {
        this.run(sqlScript, null);
    }

    /**
     * 执行 SQL 脚本
     *
     * @param sqlScript SQL 脚本内容
     * @param delimiter 执行 SQL 分隔符，默认 ; 符号结束执行
     * @throws Exception
     */
    public void run(String sqlScript, String delimiter) throws Exception {
        this.run(new StringReader(sqlScript), this.autoCommit, delimiter);
    }

    public void run(Reader reader) throws Exception {
        this.run(reader, this.autoCommit, null);
    }

    public void run(Reader reader, boolean autoCommit) throws Exception {
        this.run(reader, autoCommit, null);
    }

    public void run(Reader reader, boolean autoCommit, String delimiter) throws Exception {
        this.run(this.dataSource.getConnection(), reader, autoCommit, delimiter);
    }

    /**
     * 执行 SQL 脚本
     *
     * @param connection {@link Connection}
     * @param reader     SQL 脚本内容
     * @param autoCommit 自动提交事务
     * @param delimiter  执行 SQL 分隔符，默认 ; 符号结束执行
     */
    public void run(Connection connection, Reader reader, boolean autoCommit, String delimiter) {
        ScriptRunner scriptRunner = DdlHelper.getScriptRunner(connection, autoCommit);
        // 设置自定义 SQL 分隔符，默认 ; 符号分割
        if (StringUtils.isNotBlank(delimiter)) {
            scriptRunner.setDelimiter(delimiter);
        }
        // 执行 SQL 脚本
        scriptRunner.runScript(reader);
    }

    public boolean execute(final String driverClassName, final String url, final String user, final String password,
                           final String sql, Consumer<String> exceptionConsumer) {
        return this.execute(driverClassName, url, user, password, sql, null, exceptionConsumer);
    }

    /**
     * jdbc 连接指定 sql 执行
     *
     * @param driverClassName   连接驱动名
     * @param url               连接地址
     * @param user              数据库用户名
     * @param password          数据库密码
     * @param sql               执行 SQL
     * @param delimiter         执行 SQL 分隔符，默认 ; 符号结束执行
     * @param exceptionConsumer 异常处理
     * @return
     */
    public boolean execute(final String driverClassName, final String url, final String user, final String password,
                           final String sql, String delimiter, Consumer<String> exceptionConsumer) {
        Connection connection = null;
        try {
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(url, user, password);
            // 执行 SQL 脚本
            this.run(connection, new StringReader(sql), this.autoCommit, delimiter);
            return true;
        } catch (Exception e) {
            if (null != connection) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            exceptionConsumer.accept(e.getMessage());
        } finally {
            if (null != connection) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }
}
