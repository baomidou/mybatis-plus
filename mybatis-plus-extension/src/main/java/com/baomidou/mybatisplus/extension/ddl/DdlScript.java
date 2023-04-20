/*
 * Copyright (c) 2011-2022, baomidou (jobob@qq.com).
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

import com.baomidou.mybatisplus.extension.ddl.history.IDdlGenerator;

import javax.sql.DataSource;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

/**
 * Ddl 脚本执行
 *
 * @author hubin
 * @since 2021-07-23
 */
public class DdlScript {
    private DataSource dataSource;
    private IDdlGenerator ddlGenerator;
    private boolean autoCommit;

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
     * @param sqlFiles SQL 脚本文件列表
     */
    public void run(List<String> sqlFiles, boolean autoCommit) {
        DdlHelper.runScript(this.ddlGenerator, this.dataSource, sqlFiles, autoCommit);
    }

    /**
     * 执行 SQL 脚本
     *
     * @param sqlScript SQL 脚本内容
     * @throws Exception
     */
    public void run(String sqlScript) throws Exception {
        this.run(new StringReader(sqlScript));
    }

    public void run(Reader reader) throws Exception {
        this.run(reader, this.autoCommit);
    }

    /**
     * 执行 SQL 脚本
     *
     * @param reader     SQL 脚本内容
     * @param autoCommit 自动提交事务
     * @throws Exception
     */
    public void run(Reader reader, boolean autoCommit) throws Exception {
        DdlHelper.getScriptRunner(dataSource.getConnection(), autoCommit).runScript(reader);
    }
}
