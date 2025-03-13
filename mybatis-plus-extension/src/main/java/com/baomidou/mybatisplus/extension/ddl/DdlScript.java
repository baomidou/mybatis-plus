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

import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.ddl.history.IDdlGenerator;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import javax.sql.DataSource;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.function.Consumer;

/**
 * Ddl 脚本执行
 *
 * @author hubin
 * @since 2021-07-23
 */
public class DdlScript {

    private static final Log LOG = LogFactory.getLog(DdlScript.class);

    /**
     * 数据源
     */
    private final DataSource dataSource;


    /**
     * DDL生成器
     *
     * @deprecated 3.5.11 区分职责,如果需要DDL版本控制请直接使用{@link DdlHelper}
     */
    @Deprecated
    private IDdlGenerator ddlGenerator;

    /**
     * 是否自动提交
     */
    private boolean autoCommit;

    /**
     * 自定义脚本运行器
     *
     * @since 3.5.11
     */
    private Consumer<ScriptRunner> scriptRunnerConsumer;

    /**
     * 非池化执行 (非自动提交)
     *
     * @since 3.5.11
     */
    public DdlScript(String driverClassName, String url, String user, String password) {
        this(driverClassName, url, user, password, false);
    }

    /**
     * 非池化执行
     *
     * @since 3.5.11
     */
    public DdlScript(String driverClassName, String url, String user, String password, boolean autoCommit) {
        this.dataSource = new UnpooledDataSource(driverClassName, url, user, password);
        this.autoCommit = autoCommit;
    }

    /**
     * 创建脚本执行器
     *
     * @param dataSource 数据源
     */
    public DdlScript(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 创建脚本执行器
     *
     * @param dataSource   数据源
     * @param ddlGenerator DDL生成器
     * @deprecated 3.5.11
     */
    @Deprecated
    public DdlScript(DataSource dataSource, IDdlGenerator ddlGenerator) {
        this(dataSource, ddlGenerator, false);
    }

    /**
     * 创建脚本执行器
     *
     * @param dataSource   数据源
     * @param ddlGenerator DDL生成器
     * @param autoCommit   是否自动提交
     * @deprecated 3.5.11
     */
    @Deprecated
    public DdlScript(DataSource dataSource, IDdlGenerator ddlGenerator, boolean autoCommit) {
        this.dataSource = dataSource;
        this.ddlGenerator = ddlGenerator;
        this.autoCommit = autoCommit;
    }

    /**
     * 执行 SQL 脚本文件
     *
     * @param sqlFiles SQL 脚本文件列表
     * @see DdlHelper#runScript(IDdlGenerator, DataSource, List, boolean)
     * @deprecated 3.5.11
     */
    @Deprecated
    public void run(List<String> sqlFiles) {
        this.run(sqlFiles, this.autoCommit);
    }

    /**
     * 执行 SQL 脚本文件
     *
     * @param sqlFiles   SQL 脚本文件列表
     * @param autoCommit 自动提交事务
     * @see DdlHelper#runScript(IDdlGenerator, DataSource, List, boolean)
     * @deprecated 3.5.11
     */
    @Deprecated
    public void run(List<String> sqlFiles, boolean autoCommit) {
        try (Connection connection = this.dataSource.getConnection()) {
            DdlHelper.runScript(this.ddlGenerator, connection, sqlFiles, this.scriptRunnerConsumer, autoCommit, DdlScriptErrorHandler.PrintlnLogErrorHandler.INSTANCE);
        } catch (Exception e) {
            // TODO 保持兼容,吞掉所有异常
            LOG.error("Run script error: ", e);
        }
    }

    /**
     * 自定义 ScriptRunner
     *
     * @param scriptRunnerConsumer 处理函数
     * @return this
     * @since 3.5.11
     */
    public DdlScript scriptRunner(Consumer<ScriptRunner> scriptRunnerConsumer) {
        this.scriptRunnerConsumer = scriptRunnerConsumer;
        return this;
    }

    /**
     * 运行 SQL 脚本
     *
     * @param sqlScript 脚本内容
     * @throws Exception {@link org.apache.ibatis.jdbc.RuntimeSqlException}
     */
    public void run(String sqlScript) throws Exception {
        this.run(sqlScript, StringPool.SEMICOLON);
    }

    /**
     * 执行 SQL 脚本
     *
     * @param sqlScript SQL 脚本内容
     * @param delimiter 执行 SQL 分隔符，默认 ; 符号结束执行
     * @throws Exception {@link org.apache.ibatis.jdbc.RuntimeSqlException}
     */
    public void run(String sqlScript, String delimiter) throws Exception {
        this.run(new StringReader(sqlScript), this.autoCommit, delimiter);
    }

    public void run(Reader reader) throws Exception {
        this.run(reader, this.autoCommit, StringPool.SEMICOLON);
    }

    public void run(Reader reader, boolean autoCommit) throws Exception {
        this.run(reader, autoCommit, StringPool.SEMICOLON);
    }

    public void run(Reader reader, boolean autoCommit, String delimiter) throws Exception {
        try (Connection connection = this.dataSource.getConnection()) {
            this.run(connection, reader, autoCommit, delimiter);
        }
    }

    /**
     * 执行 SQL 脚本
     *
     * @param connection {@link Connection} 调用方需要自行控制关闭
     * @param reader     SQL 脚本内容
     * @param autoCommit 自动提交事务
     * @param delimiter  执行 SQL 分隔符，默认 ; 符号结束执行
     */
    public void run(Connection connection, Reader reader, boolean autoCommit, String delimiter) {
        ScriptRunner scriptRunner = DdlHelper.getScriptRunner(connection, autoCommit);
        if (scriptRunnerConsumer != null) {
            scriptRunnerConsumer.accept(scriptRunner);
        }
        if (StringUtils.isNotBlank(delimiter)) {
            scriptRunner.setDelimiter(delimiter);
        }
        scriptRunner.runScript(reader);
    }

    /**
     * 以默认分隔符(;) 执行 SQL 脚本
     *
     * @param driverClassName   连接驱动名
     * @param url               连接地址
     * @param user              数据库用户名
     * @param password          数据库密码
     * @param sqlScript         执行 SQL 脚本
     * @param exceptionConsumer 异常处理
     * @see DdlScript(String, String, String, String)
     * @see #run(String, Consumer)
     * @deprecated 3.5.11
     */
    @Deprecated
    public boolean execute(final String driverClassName, final String url, final String user, final String password,
                           final String sqlScript, Consumer<String> exceptionConsumer) {
        return this.execute(driverClassName, url, user, password, sqlScript, StringPool.SEMICOLON, exceptionConsumer);
    }

    /**
     * 以默认分隔符(;) 执行 SQL 脚本
     *
     * @param sqlScript         执行 SQL脚本
     * @param exceptionConsumer 异常处理
     * @since 3.5.11
     */
    public boolean run(String sqlScript, Consumer<String> exceptionConsumer) {
        return this.run(sqlScript, StringPool.SEMICOLON, exceptionConsumer);
    }

    /**
     * 以指定分隔符 执行 SQL 脚本
     *
     * @param driverClassName   连接驱动名
     * @param url               连接地址
     * @param user              数据库用户名
     * @param password          数据库密码
     * @param sqlScript         执行 SQL 脚本
     * @param delimiter         执行 SQL 分隔符，默认 ; 符号结束执行
     * @param exceptionConsumer 异常处理
     * @return 操作结果
     * @deprecated 3.5.11  {@link #run(String, String, Consumer)}
     */
    @Deprecated
    public boolean execute(final String driverClassName, final String url, final String user, final String password,
                           final String sqlScript, String delimiter, Consumer<String> exceptionConsumer) {
        //一般不需要显示加载,只有很旧很旧的驱动才需要
        if (StringUtils.isNotBlank(driverClassName)) {
            Class<?> driverClass = ClassUtils.toClassConfident(driverClassName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Load driver class: " + driverClass.getName());
            }
        }
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            this.run(connection, new StringReader(sqlScript), this.autoCommit, delimiter);
            return true;
        } catch (Exception e) {
            LOG.error("Execute sqlScript: " + sqlScript + " , fail:", e);
            exceptionConsumer.accept(e.getMessage());
        }
        return false;
    }

    /**
     * 以指定分隔符 执行 SQL 脚本
     *
     * @param sqlScript         执行 SQL 脚本
     * @param exceptionConsumer 异常处理
     * @param delimiter         执行 SQL 分隔符，默认 ; 符号结束执行
     * @return 操作结果
     * @since 3.5.11
     */
    public boolean run(String sqlScript, String delimiter, Consumer<String> exceptionConsumer) {
        try (Connection connection = dataSource.getConnection()) {
            this.run(connection, new StringReader(sqlScript), this.autoCommit, delimiter);
            return true;
        } catch (Exception e) {
            LOG.error("Execute sqlScript: " + sqlScript + " , fail:", e);
            exceptionConsumer.accept(e.getMessage());
        }
        return false;
    }

}
